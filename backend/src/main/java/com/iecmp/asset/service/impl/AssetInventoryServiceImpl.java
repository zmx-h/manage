package com.iecmp.asset.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.asset.dto.AssetInventoryAddDTO;
import com.iecmp.asset.dto.AssetInventoryFinishDTO;
import com.iecmp.asset.dto.AssetInventoryStatDTO;
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
        // 1. 不允许并存多个进行中的盘点任务；锁住 status=0 的索引区间，避免并发同时创建
        List<AssetInventory> ongoing = baseMapper.selectList(Wrappers.<AssetInventory>lambdaQuery()
                .select(AssetInventory::getId)
                .eq(AssetInventory::getStatus, InventoryStatusEnum.IN_PROGRESS.getValue())
                .last("FOR UPDATE"));
        if (!ongoing.isEmpty()) {
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
        List<Object> assetIdRows = assetInfoMapper.selectObjs(assetWrapper.select(AssetInfo::getId));

        // 4. 创建盘点任务
        AssetInventory inventory = new AssetInventory();
        inventory.setTitle(dto.getTitle());
        inventory.setBeginTime(LocalDateTime.now());
        inventory.setStatus(InventoryStatusEnum.IN_PROGRESS.getValue());
        inventory.setTotalCount(assetIdRows.size());
        inventory.setNormalCount(0);
        inventory.setAbnormalCount(0);
        this.save(inventory);

        // 5. 分批生成明细行，避免全量对象加载与逐条 INSERT
        if (!assetIdRows.isEmpty()) {
            List<AssetInventoryItem> items = new ArrayList<>(assetIdRows.size());
            for (Object rawId : assetIdRows) {
                if (!(rawId instanceof Number number)) {
                    continue;
                }
                AssetInventoryItem item = new AssetInventoryItem();
                item.setInventoryId(inventory.getId());
                item.setAssetId(number.longValue());
                item.setCheckStatus(InventoryCheckStatusEnum.UNCHECKED.getValue());
                items.add(item);
            }
            for (int i = 0; i < items.size(); i += 500) {
                assetInventoryItemMapper.insertBatch(items.subList(i, Math.min(i + 500, items.size())));
            }
        }

        AssetInventoryCreateVO vo = new AssetInventoryCreateVO();
        vo.setInventoryId(inventory.getId());
        vo.setTotalCount(assetIdRows.size());
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

        // 按库存任务在数据库中聚合，避免把每页所有明细加载到 JVM
        Set<Long> inventoryIds = result.getRecords().stream()
                .map(AssetInventory::getId).collect(Collectors.toSet());
        Map<Long, int[]> statMap = new HashMap<>();
        if (!inventoryIds.isEmpty()) {
            for (AssetInventoryStatDTO stat : assetInventoryItemMapper.selectStatByInventoryIds(inventoryIds)) {
                if (stat.getInventoryId() != null) {
                    statMap.put(stat.getInventoryId(), new int[]{
                            valueOrZero(stat.getTotalCount()),
                            valueOrZero(stat.getCheckedCount()),
                            valueOrZero(stat.getNormalCount()),
                            valueOrZero(stat.getAbnormalCount())
                    });
                }
            }
        }

        return PageResult.of(result, inv -> {
            AssetInventoryPageVO vo = new AssetInventoryPageVO();
            vo.setInventoryId(inv.getId());
            vo.setTitle(inv.getTitle());
            vo.setBeginTime(fmt(inv.getBeginTime()));
            vo.setEndTime(fmt(inv.getEndTime()));
            vo.setStatus(inv.getStatus());

            int[] stat = statMap.getOrDefault(inv.getId(), new int[4]);
            int total = stat[0];
            int checked = stat[1];
            vo.setTotalCount(total);
            vo.setCheckedCount(checked);
            vo.setNormalCount(stat[2]);
            vo.setAbnormalCount(stat[3]);
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

        // 先抢占「进行中 -> 已完成」，并发调用时只有一个请求能继续执行
        LocalDateTime finishTime = LocalDateTime.now();
        boolean claimed = this.update(Wrappers.<AssetInventory>lambdaUpdate()
                .eq(AssetInventory::getId, id)
                .eq(AssetInventory::getStatus, InventoryStatusEnum.IN_PROGRESS.getValue())
                .set(AssetInventory::getStatus, InventoryStatusEnum.COMPLETED.getValue())
                .set(AssetInventory::getEndTime, finishTime));
        if (!claimed) {
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

        // 2. 回填盘点统计结果（状态与结束时间已在上面的条件更新中写入）
        this.update(Wrappers.<AssetInventory>lambdaUpdate()
                .eq(AssetInventory::getId, id)
                .set(AssetInventory::getTotalCount, total)
                .set(AssetInventory::getNormalCount, normal)
                .set(AssetInventory::getAbnormalCount, loss + mismatch));

        // 3. 盘亏资产同步报废（可选）
        boolean updateStatus = dto.getUpdateAssetStatus() != null && dto.getUpdateAssetStatus() == 1;
        if (updateStatus && loss > 0) {
            List<Long> lossAssetIds = items.stream()
                    .filter(it -> InventoryCheckStatusEnum.LOSS.getValue().equals(it.getCheckStatus()))
                    .map(AssetInventoryItem::getAssetId)
                    .toList();
            if (!lossAssetIds.isEmpty()) {
                assetInfoMapper.update(null, Wrappers.<AssetInfo>lambdaUpdate()
                        .in(AssetInfo::getId, lossAssetIds)
                        .ne(AssetInfo::getStatus, AssetStatusEnum.SCRAPPED.getValue())
                        .set(AssetInfo::getStatus, AssetStatusEnum.SCRAPPED.getValue()));
            }
        }

        // 4. 为每条异常明细写入盘点流水
        LocalDateTime now = LocalDateTime.now();
        Long operatorId = UserContext.getUserIdOrDefault();
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

    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }
}
