package com.iecmp.asset.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.asset.dto.AssetInventoryAddDTO;
import com.iecmp.asset.dto.AssetInventoryFinishDTO;
import com.iecmp.asset.entity.AssetCategory;
import com.iecmp.asset.entity.AssetInfo;
import com.iecmp.asset.entity.AssetInventory;
import com.iecmp.asset.entity.AssetInventoryItem;
import com.iecmp.asset.entity.AssetRecord;
import com.iecmp.asset.enums.AssetErrorCode;
import com.iecmp.asset.enums.AssetRecordTypeEnum;
import com.iecmp.asset.enums.AssetStatusEnum;
import com.iecmp.asset.enums.InventoryCheckStatusEnum;
import com.iecmp.asset.enums.InventoryStatusEnum;
import com.iecmp.asset.mapper.AssetInfoMapper;
import com.iecmp.asset.mapper.AssetInventoryItemMapper;
import com.iecmp.asset.mapper.AssetInventoryMapper;
import com.iecmp.asset.query.AssetInventoryPageQuery;
import com.iecmp.asset.service.AssetCategoryService;
import com.iecmp.asset.service.AssetInventoryService;
import com.iecmp.asset.service.AssetRecordService;
import com.iecmp.asset.vo.AssetInventoryCreateVO;
import com.iecmp.asset.vo.AssetInventoryFinishVO;
import com.iecmp.asset.vo.AssetInventoryPageVO;
import com.iecmp.common.context.UserContext;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 盘点任务业务实现（{@code asset_inventory}）。
 *
 * @author IECMP 后端组
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetInventoryServiceImpl extends ServiceImpl<AssetInventoryMapper, AssetInventory>
        implements AssetInventoryService {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final AssetInfoMapper assetInfoMapper;
    private final AssetInventoryItemMapper assetInventoryItemMapper;
    private final AssetCategoryService assetCategoryService;
    private final AssetRecordService assetRecordService;

    // ============================== 4-23 发起盘点 ==============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssetInventoryCreateVO add(AssetInventoryAddDTO dto) {
        // 1. 不允许并存多个进行中的盘点任务
        Long ongoing = this.count(Wrappers.lambdaQuery(AssetInventory.class)
                .eq(AssetInventory::getStatus, InventoryStatusEnum.IN_PROGRESS.getValue()));
        if (ongoing > 0) {
            throw new BusinessException(AssetErrorCode.E40013.getCode(), AssetErrorCode.E40013.getMsg());
        }

        // 2. 分类存在性校验（若指定）
        Set<Long> categoryIds = null;
        if (dto.getCategoryId() != null) {
            AssetCategory category = assetCategoryService.getById(dto.getCategoryId());
            if (category == null) {
                throw new BusinessException(AssetErrorCode.E40003.getCode(), AssetErrorCode.E40003.getMsg());
            }
            categoryIds = collectCategoryIds(dto.getCategoryId());
        }

        // 3. 查询待盘点资产
        var assetWrapper = Wrappers.lambdaQuery(AssetInfo.class);
        if (categoryIds != null && !categoryIds.isEmpty()) {
            assetWrapper.in(AssetInfo::getCategoryId, categoryIds);
        }
        int excludeScrapped = dto.getExcludeScrapped() == null ? 1 : dto.getExcludeScrapped();
        if (excludeScrapped == 1) {
            assetWrapper.ne(AssetInfo::getStatus, AssetStatusEnum.SCRAPPED.getValue());
        }
        List<AssetInfo> assets = assetInfoMapper.selectList(assetWrapper);

        // 4. 创建盘点任务
        AssetInventory inventory = new AssetInventory();
        inventory.setTitle(dto.getTitle());
        inventory.setBeginTime(LocalDateTime.now());
        inventory.setStatus(InventoryStatusEnum.IN_PROGRESS.getValue());
        inventory.setTotalCount(assets.size());
        inventory.setNormalCount(0);
        inventory.setAbnormalCount(0);
        this.save(inventory);

        // 5. 批量生成明细行
        if (!assets.isEmpty()) {
            List<AssetInventoryItem> items = new ArrayList<>(assets.size());
            for (AssetInfo a : assets) {
                AssetInventoryItem item = new AssetInventoryItem();
                item.setInventoryId(inventory.getId());
                item.setAssetId(a.getId());
                item.setCheckStatus(InventoryCheckStatusEnum.UNCHECKED.getValue());
                items.add(item);
            }
            // 批量生成明细行（逐条插入；大批量可后续优化为 saveBatch）
            for (AssetInventoryItem item : items) {
                assetInventoryItemMapper.insert(item);
            }
        }

        AssetInventoryCreateVO vo = new AssetInventoryCreateVO();
        vo.setInventoryId(inventory.getId());
        vo.setTotalCount(assets.size());
        return vo;
    }

    // ============================== 4-24 盘点任务分页 ==============================

    @Override
    public PageResult<AssetInventoryPageVO> page(AssetInventoryPageQuery query) {
        var wrapper = Wrappers.lambdaQuery(AssetInventory.class);
        if (query.getStatus() != null) wrapper.eq(AssetInventory::getStatus, query.getStatus());
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(AssetInventory::getTitle, query.getKeyword().trim());
        }
        if (query.getBeginDate() != null && !query.getBeginDate().isBlank()) {
            wrapper.ge(AssetInventory::getBeginTime, parseDateStart(query.getBeginDate()));
        }
        if (query.getEndDate() != null && !query.getEndDate().isBlank()) {
            wrapper.le(AssetInventory::getBeginTime, parseDateEnd(query.getEndDate()));
        }
        wrapper.orderByDesc(AssetInventory::getBeginTime);

        Page<AssetInventory> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<AssetInventory> result = this.page(page, wrapper);

        // 批量统计各任务的明细
        Set<Long> inventoryIds = result.getRecords().stream()
                .map(AssetInventory::getId).collect(Collectors.toSet());
        Map<Long, List<AssetInventoryItem>> itemMap;
        if (!inventoryIds.isEmpty()) {
            List<AssetInventoryItem> items = assetInventoryItemMapper.selectList(
                    Wrappers.lambdaQuery(AssetInventoryItem.class)
                            .in(AssetInventoryItem::getInventoryId, inventoryIds));
            itemMap = items.stream().collect(Collectors.groupingBy(AssetInventoryItem::getInventoryId));
        } else {
            itemMap = Collections.emptyMap();
        }

        return PageResult.of(result, inv -> {
            AssetInventoryPageVO vo = new AssetInventoryPageVO();
            vo.setInventoryId(inv.getId());
            vo.setTitle(inv.getTitle());
            vo.setBeginTime(fmt(inv.getBeginTime()));
            vo.setEndTime(fmt(inv.getEndTime()));
            vo.setStatus(inv.getStatus());

            List<AssetInventoryItem> items = itemMap.getOrDefault(inv.getId(), Collections.emptyList());
            int total = items.size();
            int normal = 0, abnormal = 0, checked = 0;
            for (AssetInventoryItem it : items) {
                Integer cs = it.getCheckStatus();
                if (cs == null || cs == 0) continue;
                checked++;
                if (cs == 1) normal++;
                else if (cs == 2 || cs == 3) abnormal++;
            }
            vo.setTotalCount(total);
            vo.setCheckedCount(checked);
            vo.setNormalCount(normal);
            vo.setAbnormalCount(abnormal);
            vo.setProgressRate(total == 0 ? BigDecimal.ZERO
                    : BigDecimal.valueOf(checked).multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP));
            return vo;
        });
    }

    // ============================== 4-26 结束盘点 ==============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssetInventoryFinishVO finish(Long id, AssetInventoryFinishDTO dto) {
        AssetInventory inventory = this.getById(id);
        if (inventory == null) {
            throw new BusinessException(AssetErrorCode.E40012.getCode(), AssetErrorCode.E40012.getMsg());
        }
        if (InventoryStatusEnum.COMPLETED.getValue().equals(inventory.getStatus())) {
            throw new BusinessException(AssetErrorCode.E40013.getCode(), AssetErrorCode.E40013.getMsg());
        }

        // 1. 统计明细
        List<AssetInventoryItem> items = assetInventoryItemMapper.selectList(
                Wrappers.lambdaQuery(AssetInventoryItem.class)
                        .eq(AssetInventoryItem::getInventoryId, id));
        int total = items.size();
        int normal = 0, loss = 0, mismatch = 0, unchecked = 0;
        List<AssetInventoryItem> abnormalItems = new ArrayList<>();
        for (AssetInventoryItem it : items) {
            Integer cs = it.getCheckStatus();
            if (cs == null || cs == 0) { unchecked++; continue; }
            if (cs == 1) normal++;
            else if (cs == 2) { loss++; abnormalItems.add(it); }
            else if (cs == 3) { mismatch++; abnormalItems.add(it); }
        }

        // 2. 回填盘点任务
        inventory.setTotalCount(total);
        inventory.setNormalCount(normal);
        inventory.setAbnormalCount(loss + mismatch);
        inventory.setStatus(InventoryStatusEnum.COMPLETED.getValue());
        inventory.setEndTime(LocalDateTime.now());
        this.updateById(inventory);

        // 3. 盘亏资产同步报废（可选）
        boolean updateStatus = dto.getUpdateAssetStatus() != null && dto.getUpdateAssetStatus() == 1;
        if (updateStatus && loss > 0) {
            List<Long> lossAssetIds = items.stream()
                    .filter(it -> InventoryCheckStatusEnum.LOSS.getValue().equals(it.getCheckStatus()))
                    .map(AssetInventoryItem::getAssetId)
                    .toList();
            for (Long assetId : lossAssetIds) {
                AssetInfo update = new AssetInfo();
                update.setId(assetId);
                update.setStatus(AssetStatusEnum.SCRAPPED.getValue());
                assetInfoMapper.updateById(update);
            }
        }

        // 4. 为每条异常明细写入盘点流水
        LocalDateTime now = LocalDateTime.now();
        Long operatorId = UserContext.getUserId();
        Map<Long, AssetInfo> assetMap = getAssetMap(
                abnormalItems.stream().map(AssetInventoryItem::getAssetId).collect(Collectors.toSet()));
        for (AssetInventoryItem it : abnormalItems) {
            AssetInfo asset = assetMap.get(it.getAssetId());
            AssetRecord record = new AssetRecord();
            record.setAssetId(it.getAssetId());
            record.setRecordType(AssetRecordTypeEnum.INVENTORY.getValue());
            record.setFromUserId(asset == null ? null : asset.getKeeperId());
            record.setToUserId(asset == null ? null : asset.getKeeperId());
            record.setFromLocation(asset == null ? null : asset.getLocation());
            record.setToLocation(asset == null ? null : asset.getLocation());
            record.setRecordTime(now);
            record.setOperatorId(operatorId);
            record.setRemark("盘点异常：" + InventoryCheckStatusEnum.labelOf(it.getCheckStatus())
                    + (it.getRemark() == null ? "" : "，" + it.getRemark()));
            assetRecordService.save(record);
        }

        // 5. 组装返回
        AssetInventoryFinishVO vo = new AssetInventoryFinishVO();
        vo.setInventoryId(id);
        vo.setTotalCount(total);
        vo.setNormalCount(normal);
        vo.setLossCount(loss);
        vo.setMismatchCount(mismatch);
        vo.setUncheckedCount(unchecked);
        List<AssetInventoryFinishVO.AbnormalListVO> abnormalList = abnormalItems.stream().map(it -> {
            AssetInventoryFinishVO.AbnormalListVO a = new AssetInventoryFinishVO.AbnormalListVO();
            a.setAssetId(it.getAssetId());
            AssetInfo asset = assetMap.get(it.getAssetId());
            a.setAssetNo(asset == null ? null : asset.getAssetNo());
            a.setAssetName(asset == null ? null : asset.getName());
            a.setCheckStatus(it.getCheckStatus());
            a.setRemark(it.getRemark());
            return a;
        }).toList();
        vo.setAbnormalList(abnormalList);
        return vo;
    }

    // ============================== 内部方法 ==============================

    private Set<Long> collectCategoryIds(Long categoryId) {
        List<AssetCategory> all = assetCategoryService.list();
        Map<Long, List<AssetCategory>> parentMap = all.stream()
                .collect(Collectors.groupingBy(c -> c.getParentId() == null ? 0L : c.getParentId()));
        Set<Long> result = new HashSet<>();
        java.util.Deque<Long> stack = new java.util.ArrayDeque<>();
        stack.push(categoryId);
        while (!stack.isEmpty()) {
            Long cur = stack.pop();
            if (!result.add(cur)) continue;
            List<AssetCategory> children = parentMap.get(cur);
            if (children != null) {
                for (AssetCategory c : children) stack.push(c.getId());
            }
        }
        return result;
    }

    private Map<Long, AssetInfo> getAssetMap(Collection<Long> assetIds) {
        if (assetIds == null || assetIds.isEmpty()) return Collections.emptyMap();
        List<AssetInfo> list = assetInfoMapper.selectBatchIds(assetIds);
        Map<Long, AssetInfo> map = new HashMap<>();
        for (AssetInfo a : list) map.put(a.getId(), a);
        return map;
    }

    private String fmt(LocalDateTime dt) {
        return dt == null ? null : dt.format(DATE_TIME_FMT);
    }

    private LocalDateTime parseDateStart(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return LocalDate.parse(text.trim(), DATE_FMT).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "日期格式错误，应为 yyyy-MM-dd");
        }
    }

    private LocalDateTime parseDateEnd(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return LocalDate.parse(text.trim(), DATE_FMT).atTime(LocalTime.of(23, 59, 59));
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "日期格式错误，应为 yyyy-MM-dd");
        }
    }
}
