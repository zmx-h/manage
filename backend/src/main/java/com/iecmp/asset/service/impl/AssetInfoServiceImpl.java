package com.iecmp.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.asset.dto.AssetAddDTO;
import com.iecmp.asset.dto.AssetAssignDTO;
import com.iecmp.asset.dto.AssetStatusDTO;
import com.iecmp.asset.dto.AssetUpdateDTO;
import com.iecmp.asset.entity.AssetBorrow;
import com.iecmp.asset.entity.AssetCategory;
import com.iecmp.asset.entity.AssetInfo;
import com.iecmp.asset.entity.AssetRecord;
import com.iecmp.asset.enums.AssetErrorCode;
import com.iecmp.asset.enums.AssetRecordTypeEnum;
import com.iecmp.asset.enums.AssetStatusEnum;
import com.iecmp.asset.mapper.AssetInfoMapper;
import com.iecmp.asset.query.AdminAssetPageQuery;
import com.iecmp.asset.query.AssetExportQuery;
import com.iecmp.asset.query.UserAssetPageQuery;
import com.iecmp.asset.service.AssetBorrowService;
import com.iecmp.asset.service.AssetCategoryService;
import com.iecmp.asset.service.AssetInfoService;
import com.iecmp.asset.service.AssetRecordService;
import com.iecmp.asset.vo.AdminAssetDetailVO;
import com.iecmp.asset.vo.AdminAssetPageVO;
import com.iecmp.asset.vo.AssetCreateVO;
import com.iecmp.asset.vo.AssetStatOverviewVO;
import com.iecmp.asset.vo.UserAssetDetailVO;
import com.iecmp.asset.vo.UserAssetPageVO;
import com.iecmp.common.context.UserContext;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.service.SysUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 资产台账业务实现（{@code asset_info}）。
 *
 * @author IECMP 后端组
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetInfoServiceImpl extends ServiceImpl<AssetInfoMapper, AssetInfo>
        implements AssetInfoService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AssetCategoryService assetCategoryService;
    private final SysUserService sysUserService;
    private final AssetRecordService assetRecordService;
    private final AssetBorrowService assetBorrowService;

    @Value("${asset.export.max-rows:10000}")
    private int exportMaxRows;

    // ============================== 4-08 分页 ==============================

    @Override
    public PageResult<AdminAssetPageVO> page(AdminAssetPageQuery query) {
        // 1. 构造查询条件
        LambdaQueryWrapper<AssetInfo> wrapper = buildAdminQueryWrapper(query);

        // 2. 执行分页查询
        Page<AssetInfo> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<AssetInfo> result = this.page(page, wrapper);

        // 3. 收集分类 ID 与保管人 ID，批量回填名称（参考 userPage 写法）
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> keeperIds = new HashSet<>();
        for (AssetInfo a : result.getRecords()) {
            if (a.getCategoryId() != null) categoryIds.add(a.getCategoryId());
            if (a.getKeeperId() != null) keeperIds.add(a.getKeeperId());
        }
        Map<Long, String> categoryNameMap = getCategoryNameMap(categoryIds);
        Map<Long, String> keeperNameMap = getUserNameMap(keeperIds);

        // 4. 转换成 PageResult 返回
        return PageResult.of(result, entity -> {
            AdminAssetPageVO vo = new AdminAssetPageVO();
            vo.setAssetId(entity.getId());
            vo.setAssetNo(entity.getAssetNo());
            vo.setName(entity.getName());
            vo.setCategoryId(entity.getCategoryId());
            vo.setCategoryName(categoryNameMap.get(entity.getCategoryId()));
            vo.setBrand(entity.getBrand());
            vo.setModel(entity.getModel());
            vo.setPrice(entity.getPrice());
            if (entity.getPurchaseDate() != null) {
                vo.setPurchaseDate(entity.getPurchaseDate().toString());
            }
            vo.setSourceType(entity.getSourceType());
            vo.setStatus(entity.getStatus());
            vo.setKeeperId(entity.getKeeperId());
            vo.setKeeperName(keeperNameMap.get(entity.getKeeperId()));
            vo.setLocation(entity.getLocation());
            vo.setBudgetExpenseId(entity.getBudgetExpenseId());
            if (entity.getCreateTime() != null) {
                vo.setCreateTime(entity.getCreateTime().format(DATE_TIME_FMT));
            }
            return vo;
        });
    }

    // ============================== 4-09 详情（补全 records 与 borrowHistory） ==============================

    @Override
    public AdminAssetDetailVO detail(Long id) {
        // 1. 查询资产
        AssetInfo assetInfo = this.getById(id);

        // 2. 不存在就报错
        if (assetInfo == null) {
            throw new BusinessException(
                    AssetErrorCode.E40001.getCode(),
                    AssetErrorCode.E40001.getMsg()
            );
        }

        // 3. 转换成 VO
        AdminAssetDetailVO vo = new AdminAssetDetailVO();
        vo.setAssetId(assetInfo.getId());
        vo.setAssetNo(assetInfo.getAssetNo());
        vo.setName(assetInfo.getName());
        vo.setCategoryId(assetInfo.getCategoryId());
        vo.setBrand(assetInfo.getBrand());
        vo.setModel(assetInfo.getModel());
        vo.setPrice(assetInfo.getPrice());
        vo.setSupplier(assetInfo.getSupplier());
        vo.setSourceType(assetInfo.getSourceType());
        vo.setBudgetExpenseId(assetInfo.getBudgetExpenseId());
        vo.setStatus(assetInfo.getStatus());
        vo.setKeeperId(assetInfo.getKeeperId());
        vo.setLocation(assetInfo.getLocation());
        vo.setImageUrls(assetInfo.getImageUrls());
        vo.setRemark(assetInfo.getRemark());

        // 日期类型转换（LocalDate → String）
        if (assetInfo.getPurchaseDate() != null) {
            vo.setPurchaseDate(assetInfo.getPurchaseDate().toString());
        }
        if (assetInfo.getWarrantyEndDate() != null) {
            vo.setWarrantyEndDate(assetInfo.getWarrantyEndDate().toString());
        }

        // 创建时间转换（LocalDateTime → String）
        if (assetInfo.getCreateTime() != null) {
            vo.setCreateTime(assetInfo.getCreateTime().format(DATE_TIME_FMT));
        }

        // 4. 补充分类名称
        if (assetInfo.getCategoryId() != null) {
            AssetCategory category = assetCategoryService.getById(assetInfo.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }

        // 5. 补充保管人姓名
        if (assetInfo.getKeeperId() != null) {
            SysUser user = sysUserService.getById(assetInfo.getKeeperId());
            if (user != null) {
                vo.setKeeperName(user.getRealName());
            }
        }

        // 6. 变动流水
        List<AssetRecord> records = assetRecordService.list(
                Wrappers.lambdaQuery(AssetRecord.class)
                        .eq(AssetRecord::getAssetId, id)
                        .orderByDesc(AssetRecord::getRecordTime)
        );
        // 收集涉及的用户 ID 并批量查询姓名
        Set<Long> userIds = new HashSet<>();
        for (AssetRecord r : records) {
            if (r.getFromUserId() != null) userIds.add(r.getFromUserId());
            if (r.getToUserId() != null) userIds.add(r.getToUserId());
            if (r.getOperatorId() != null) userIds.add(r.getOperatorId());
        }
        Map<Long, String> userNameMap = getUserNameMap(userIds);
        List<AdminAssetDetailVO.RecordVO> recordVOList = records.stream().map(r -> {
            AdminAssetDetailVO.RecordVO rvo = new AdminAssetDetailVO.RecordVO();
            rvo.setRecordId(r.getId());
            rvo.setRecordType(r.getRecordType());
            rvo.setFromUserName(userNameMap.get(r.getFromUserId()));
            rvo.setToUserName(userNameMap.get(r.getToUserId()));
            rvo.setFromLocation(r.getFromLocation());
            rvo.setToLocation(r.getToLocation());
            rvo.setRecordTime(r.getRecordTime() == null ? null : r.getRecordTime().format(DATE_TIME_FMT));
            rvo.setOperatorName(userNameMap.get(r.getOperatorId()));
            rvo.setRemark(r.getRemark());
            return rvo;
        }).toList();
        vo.setRecords(recordVOList);

        // 7. 历史借用记录
        List<AssetBorrow> borrows = assetBorrowService.list(
                Wrappers.lambdaQuery(AssetBorrow.class)
                        .eq(AssetBorrow::getAssetId, id)
                        .orderByDesc(AssetBorrow::getBorrowTime)
        );
        Set<Long> borrowUserIds = borrows.stream()
                .map(AssetBorrow::getUserId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> borrowUserNameMap = getUserNameMap(borrowUserIds);
        List<AdminAssetDetailVO.BorrowHistoryVO> historyVOList = borrows.stream().map(b -> {
            AdminAssetDetailVO.BorrowHistoryVO hvo = new AdminAssetDetailVO.BorrowHistoryVO();
            hvo.setBorrowId(b.getId());
            hvo.setUserName(borrowUserNameMap.get(b.getUserId()));
            hvo.setBorrowTime(b.getBorrowTime() == null ? null : b.getBorrowTime().format(DATE_TIME_FMT));
            hvo.setActualReturnTime(b.getActualReturnTime() == null ? null : b.getActualReturnTime().format(DATE_TIME_FMT));
            hvo.setStatus(b.getStatus());
            return hvo;
        }).toList();
        vo.setBorrowHistory(historyVOList);

        // 8. 返回
        return vo;
    }

    // ============================== 4-10 新增资产 ==============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssetCreateVO add(AssetAddDTO dto) {
        // 1. 资产编号唯一性校验
        Long count = this.count(Wrappers.lambdaQuery(AssetInfo.class)
                .eq(AssetInfo::getAssetNo, dto.getAssetNo()));
        if (count > 0) {
            throw new BusinessException(AssetErrorCode.E40002.getCode(), AssetErrorCode.E40002.getMsg());
        }

        // 2. 分类存在性校验
        if (dto.getCategoryId() != null) {
            AssetCategory category = assetCategoryService.getById(dto.getCategoryId());
            if (category == null) {
                throw new BusinessException(AssetErrorCode.E40003.getCode(), AssetErrorCode.E40003.getMsg());
            }
        }

        // 3. 组装实体
        AssetInfo entity = new AssetInfo();
        entity.setAssetNo(dto.getAssetNo());
        entity.setName(dto.getName());
        entity.setCategoryId(dto.getCategoryId());
        entity.setBrand(dto.getBrand());
        entity.setModel(dto.getModel());
        entity.setPrice(dto.getPrice() == null ? BigDecimal.ZERO : dto.getPrice());
        entity.setPurchaseDate(parseDate(dto.getPurchaseDate()));
        entity.setSupplier(dto.getSupplier());
        entity.setSourceType(dto.getSourceType() == null ? 1 : dto.getSourceType());
        entity.setBudgetExpenseId(dto.getBudgetExpenseId());
        entity.setStatus(AssetStatusEnum.IN_USE.getValue());
        entity.setKeeperId(dto.getKeeperId());
        entity.setLocation(dto.getLocation());
        entity.setImageUrls(dto.getImageUrls());
        entity.setWarrantyEndDate(parseDate(dto.getWarrantyEndDate()));
        entity.setRemark(dto.getRemark());

        this.save(entity);

        AssetCreateVO vo = new AssetCreateVO();
        vo.setAssetId(entity.getId());
        return vo;
    }

    // ============================== 4-11 修改资产 ==============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AssetUpdateDTO dto) {
        // 1. 资产存在性校验
        AssetInfo entity = this.getById(dto.getAssetId());
        if (entity == null) {
            throw new BusinessException(AssetErrorCode.E40001.getCode(), AssetErrorCode.E40001.getMsg());
        }

        // 2. 资产编号唯一性校验（排除自身）
        Long count = this.count(Wrappers.lambdaQuery(AssetInfo.class)
                .eq(AssetInfo::getAssetNo, dto.getAssetNo())
                .ne(AssetInfo::getId, dto.getAssetId()));
        if (count > 0) {
            throw new BusinessException(AssetErrorCode.E40002.getCode(), AssetErrorCode.E40002.getMsg());
        }

        // 3. 分类存在性校验
        if (dto.getCategoryId() != null) {
            AssetCategory category = assetCategoryService.getById(dto.getCategoryId());
            if (category == null) {
                throw new BusinessException(AssetErrorCode.E40003.getCode(), AssetErrorCode.E40003.getMsg());
            }
        }

        // 4. 更新基础字段（不修改 status / keeperId / location）
        entity.setAssetNo(dto.getAssetNo());
        entity.setName(dto.getName());
        entity.setCategoryId(dto.getCategoryId());
        entity.setBrand(dto.getBrand());
        entity.setModel(dto.getModel());
        entity.setPrice(dto.getPrice() == null ? BigDecimal.ZERO : dto.getPrice());
        entity.setPurchaseDate(parseDate(dto.getPurchaseDate()));
        entity.setSupplier(dto.getSupplier());
        entity.setSourceType(dto.getSourceType() == null ? 1 : dto.getSourceType());
        entity.setBudgetExpenseId(dto.getBudgetExpenseId());
        entity.setWarrantyEndDate(parseDate(dto.getWarrantyEndDate()));
        entity.setRemark(dto.getRemark());

        this.updateById(entity);
    }

    // ============================== 4-12 删除资产 ==============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) {
        // 1. 资产存在性校验
        AssetInfo entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(AssetErrorCode.E40001.getCode(), AssetErrorCode.E40001.getMsg());
        }

        // 2. 存在未归还借用记录（待审核 0 / 已通过 1 / 已逾期 4）则禁止删除
        Long unreturned = assetBorrowService.count(Wrappers.lambdaQuery(AssetBorrow.class)
                .eq(AssetBorrow::getAssetId, id)
                .in(AssetBorrow::getStatus, 0, 1, 4));
        if (unreturned > 0) {
            throw new BusinessException(AssetErrorCode.E40007.getCode(), AssetErrorCode.E40007.getMsg());
        }

        // 3. 逻辑删除（@TableLogic 自动维护 deleted）
        this.removeById(id);
    }

    // ============================== 4-13 资产状态变更 ==============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, AssetStatusDTO dto) {
        // 1. 资产存在性校验
        AssetInfo entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(AssetErrorCode.E40001.getCode(), AssetErrorCode.E40001.getMsg());
        }

        Integer target = dto.getStatus();
        Integer current = entity.getStatus();

        // 2. 「已借出」不允许手工设置
        if (AssetStatusEnum.BORROWED.getValue().equals(target)) {
            throw new BusinessException(AssetErrorCode.E40006.getCode(), AssetErrorCode.E40006.getMsg());
        }

        // 3. 校验合法流转并确定流水类型
        Integer recordType = resolveStatusRecordType(current, target);
        if (recordType == null) {
            throw new BusinessException(AssetErrorCode.E40006.getCode(), AssetErrorCode.E40006.getMsg());
        }

        // 4. 更新状态
        entity.setStatus(target);
        this.updateById(entity);

        // 5. 写入变动流水
        AssetRecord record = new AssetRecord();
        record.setAssetId(id);
        record.setRecordType(recordType);
        record.setFromUserId(entity.getKeeperId());
        record.setToUserId(entity.getKeeperId());
        record.setFromLocation(entity.getLocation());
        record.setToLocation(entity.getLocation());
        record.setRecordTime(LocalDateTime.now());
        record.setOperatorId(UserContext.getUserIdOrDefault());
        record.setRemark(dto.getRemark());
        assetRecordService.save(record);
    }

    // ============================== 4-14 分配保管人与存放位置 ==============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assign(Long id, AssetAssignDTO dto) {
        // 1. 资产存在性校验
        AssetInfo entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(AssetErrorCode.E40001.getCode(), AssetErrorCode.E40001.getMsg());
        }

        Long oldKeeperId = entity.getKeeperId();
        String oldLocation = entity.getLocation();

        // 2. 计算新值：keeperId == null 不变；location == null 不变，空串清空
        Long newKeeperId = dto.getKeeperId() == null ? oldKeeperId : dto.getKeeperId();
        String newLocation = dto.getLocation() == null ? oldLocation : dto.getLocation();

        boolean keeperChanged = !java.util.Objects.equals(oldKeeperId, newKeeperId);
        boolean locationChanged = !java.util.Objects.equals(oldLocation, newLocation);

        // 3. 任一发生变化则写入调拨流水
        if (keeperChanged || locationChanged) {
            AssetRecord record = new AssetRecord();
            record.setAssetId(id);
            record.setRecordType(AssetRecordTypeEnum.ALLOCATION.getValue());
            record.setFromUserId(oldKeeperId);
            record.setToUserId(newKeeperId);
            record.setFromLocation(oldLocation);
            record.setToLocation(newLocation);
            record.setRecordTime(LocalDateTime.now());
            record.setOperatorId(UserContext.getUserIdOrDefault());
            record.setRemark(dto.getRemark());
            assetRecordService.save(record);
        }

        // 4. 更新
        entity.setKeeperId(newKeeperId);
        entity.setLocation(newLocation);
        this.updateById(entity);
    }

    // ============================== 4-01 资产分页（前台） ==============================

    @Override
    public PageResult<UserAssetPageVO> userPage(UserAssetPageQuery query) {
        LambdaQueryWrapper<AssetInfo> wrapper = buildUserQueryWrapper(query);
        Page<AssetInfo> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<AssetInfo> result = this.page(page, wrapper);

        // 收集分类 ID 与保管人 ID，批量回填名称
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> keeperIds = new HashSet<>();
        for (AssetInfo a : result.getRecords()) {
            if (a.getCategoryId() != null) categoryIds.add(a.getCategoryId());
            if (a.getKeeperId() != null) keeperIds.add(a.getKeeperId());
        }
        Map<Long, String> categoryNameMap = getCategoryNameMap(categoryIds);
        Map<Long, String> keeperNameMap = getUserNameMap(keeperIds);

        return PageResult.of(result, entity -> {
            UserAssetPageVO vo = new UserAssetPageVO();
            vo.setAssetId(entity.getId());
            vo.setAssetNo(entity.getAssetNo());
            vo.setName(entity.getName());
            vo.setCategoryId(entity.getCategoryId());
            vo.setCategoryName(categoryNameMap.get(entity.getCategoryId()));
            vo.setBrand(entity.getBrand());
            vo.setModel(entity.getModel());
            vo.setStatus(entity.getStatus());
            vo.setKeeperName(keeperNameMap.get(entity.getKeeperId()));
            vo.setLocation(entity.getLocation());
            vo.setImageUrls(entity.getImageUrls());
            return vo;
        });
    }

    // ============================== 4-02 资产详情（前台） ==============================

    @Override
    public UserAssetDetailVO userDetail(Long id) {
        AssetInfo assetInfo = this.getById(id);
        if (assetInfo == null) {
            throw new BusinessException(AssetErrorCode.E40001.getCode(), AssetErrorCode.E40001.getMsg());
        }

        UserAssetDetailVO vo = new UserAssetDetailVO();
        vo.setAssetId(assetInfo.getId());
        vo.setAssetNo(assetInfo.getAssetNo());
        vo.setName(assetInfo.getName());
        vo.setCategoryId(assetInfo.getCategoryId());
        vo.setBrand(assetInfo.getBrand());
        vo.setModel(assetInfo.getModel());
        vo.setPrice(assetInfo.getPrice());
        vo.setSupplier(assetInfo.getSupplier());
        vo.setSourceType(assetInfo.getSourceType());
        vo.setStatus(assetInfo.getStatus());
        vo.setKeeperId(assetInfo.getKeeperId());
        vo.setLocation(assetInfo.getLocation());
        vo.setImageUrls(assetInfo.getImageUrls());
        vo.setRemark(assetInfo.getRemark());
        if (assetInfo.getPurchaseDate() != null) {
            vo.setPurchaseDate(assetInfo.getPurchaseDate().toString());
        }
        if (assetInfo.getWarrantyEndDate() != null) {
            vo.setWarrantyEndDate(assetInfo.getWarrantyEndDate().toString());
        }

        if (assetInfo.getCategoryId() != null) {
            AssetCategory category = assetCategoryService.getById(assetInfo.getCategoryId());
            if (category != null) vo.setCategoryName(category.getName());
        }
        if (assetInfo.getKeeperId() != null) {
            SysUser user = sysUserService.getById(assetInfo.getKeeperId());
            if (user != null) vo.setKeeperName(user.getRealName());
        }

        // 最近 10 条变动流水
        List<AssetRecord> records = assetRecordService.list(
                Wrappers.lambdaQuery(AssetRecord.class)
                        .eq(AssetRecord::getAssetId, id)
                        .orderByDesc(AssetRecord::getRecordTime)
                        .last("LIMIT 10")
        );
        Set<Long> userIds = new HashSet<>();
        for (AssetRecord r : records) {
            if (r.getFromUserId() != null) userIds.add(r.getFromUserId());
            if (r.getToUserId() != null) userIds.add(r.getToUserId());
        }
        Map<Long, String> userNameMap = getUserNameMap(userIds);
        List<UserAssetDetailVO.RecentRecordVO> recent = records.stream().map(r -> {
            UserAssetDetailVO.RecentRecordVO rvo = new UserAssetDetailVO.RecentRecordVO();
            rvo.setRecordType(r.getRecordType());
            rvo.setFromUserName(userNameMap.get(r.getFromUserId()));
            rvo.setToUserName(userNameMap.get(r.getToUserId()));
            rvo.setRecordTime(r.getRecordTime() == null ? null : r.getRecordTime().format(DATE_TIME_FMT));
            rvo.setRemark(r.getRemark());
            return rvo;
        }).toList();
        vo.setRecentRecords(recent);

        return vo;
    }

    // ============================== 4-04 资产总览统计 ==============================

    @Override
    public AssetStatOverviewVO statOverview() {
        List<AssetInfo> all = this.list();

        AssetStatOverviewVO vo = new AssetStatOverviewVO();
        vo.setTotal(all.size());

        BigDecimal totalValue = BigDecimal.ZERO;
        int inUse = 0, idle = 0, repair = 0, borrowed = 0, scrapped = 0;
        Map<Long, Integer> categoryCountMap = new HashMap<>();

        for (AssetInfo a : all) {
            if (a.getPrice() != null) totalValue = totalValue.add(a.getPrice());
            if (a.getStatus() == null) continue;
            switch (a.getStatus()) {
                case 1 -> inUse++;
                case 2 -> idle++;
                case 3 -> repair++;
                case 4 -> borrowed++;
                case 5 -> scrapped++;
                default -> { }
            }
            if (a.getCategoryId() != null) {
                categoryCountMap.merge(a.getCategoryId(), 1, Integer::sum);
            }
        }
        vo.setTotalValue(totalValue);
        vo.setInUse(inUse);
        vo.setIdle(idle);
        vo.setRepair(repair);
        vo.setBorrowed(borrowed);
        vo.setScrapped(scrapped);

        // 分类维度统计
        if (!categoryCountMap.isEmpty()) {
            Map<Long, String> categoryNameMap = getCategoryNameMap(categoryCountMap.keySet());
            List<AssetStatOverviewVO.CategoryStatVO> categoryStats = categoryCountMap.entrySet().stream()
                    .map(e -> {
                        AssetStatOverviewVO.CategoryStatVO c = new AssetStatOverviewVO.CategoryStatVO();
                        c.setCategoryName(categoryNameMap.get(e.getKey()));
                        c.setCount(e.getValue());
                        return c;
                    }).toList();
            vo.setCategoryStats(categoryStats);
        } else {
            vo.setCategoryStats(new ArrayList<>());
        }

        return vo;
    }

    // ============================== 4-27 资产台账导出（CSV） ==============================

    @Override
    public void export(AssetExportQuery query, HttpServletResponse response) {
        // 1. 按筛选条件查询
        LambdaQueryWrapper<AssetInfo> wrapper = buildExportQueryWrapper(query);
        Long total = this.count(wrapper);
        if (total > exportMaxRows) {
            throw new BusinessException(AssetErrorCode.E40016.getCode(), AssetErrorCode.E40016.getMsg());
        }

        List<AssetInfo> list = this.list(wrapper);

        // 收集分类 ID 与保管人 ID，批量回填名称
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> keeperIds = new HashSet<>();
        for (AssetInfo a : list) {
            if (a.getCategoryId() != null) categoryIds.add(a.getCategoryId());
            if (a.getKeeperId() != null) keeperIds.add(a.getKeeperId());
        }
        Map<Long, String> categoryNameMap = getCategoryNameMap(categoryIds);
        Map<Long, String> keeperNameMap = getUserNameMap(keeperIds);

        // 2. 输出 CSV
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String filename = "资产台账_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
            // BOM，避免 Excel 打开中文乱码
            pw.write('\uFEFF');
            pw.println("资产编号,资产名称,分类,品牌,型号,购置价格,购置日期,供应商,来源,状态,保管人,存放位置,关联预算支出,备注");

            for (AssetInfo a : list) {
                pw.println(String.join(",",
                        csvCell(a.getAssetNo()),
                        csvCell(a.getName()),
                        csvCell(categoryNameMap.get(a.getCategoryId())),
                        csvCell(a.getBrand()),
                        csvCell(a.getModel()),
                        a.getPrice() == null ? "" : a.getPrice().toPlainString(),
                        a.getPurchaseDate() == null ? "" : a.getPurchaseDate().toString(),
                        csvCell(a.getSupplier()),
                        csvCell(sourceLabel(a.getSourceType())),
                        csvCell(AssetStatusEnum.labelOf(a.getStatus())),
                        csvCell(keeperNameMap.get(a.getKeeperId())),
                        csvCell(a.getLocation()),
                        a.getBudgetExpenseId() == null ? "" : a.getBudgetExpenseId().toString(),
                        csvCell(a.getRemark())
                ));
            }
            pw.flush();
        } catch (IOException e) {
            log.error("资产台账导出失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR.getCode(), "导出失败");
        }
    }

    // ============================== 内部工具方法 ==============================

    /** 解析 yyyy-MM-dd 日期，空串返回 null，格式错误抛 400。 */
    private LocalDate parseDate(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return LocalDate.parse(text.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "日期格式错误，应为 yyyy-MM-dd");
        }
    }

    /** 根据目标状态确定流水类型，非法流转返回 null。 */
    private Integer resolveStatusRecordType(Integer current, Integer target) {
        if (current == null || target == null) return null;
        return switch (target) {
            case 2 -> (current == 1 || current == 3) ? AssetRecordTypeEnum.ALLOCATION.getValue() : null;
            case 3 -> (current == 1 || current == 2) ? AssetRecordTypeEnum.REPAIR.getValue() : null;
            case 5 -> (current == 1 || current == 2 || current == 3) ? AssetRecordTypeEnum.SCRAP.getValue() : null;
            case 1 -> (current == 2 || current == 3) ? AssetRecordTypeEnum.REPAIR.getValue() : null;
            default -> null;
        };
    }

    /** 批量查询用户姓名。 */
    private Map<Long, String> getUserNameMap(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return Collections.emptyMap();
        List<SysUser> users = sysUserService.listByIds(userIds);
        Map<Long, String> map = new HashMap<>();
        for (SysUser u : users) {
            map.put(u.getId(), u.getRealName());
        }
        return map;
    }

    /** 批量查询分类名称。 */
    private Map<Long, String> getCategoryNameMap(Collection<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) return Collections.emptyMap();
        List<AssetCategory> categories = assetCategoryService.listByIds(categoryIds);
        Map<Long, String> map = new HashMap<>();
        for (AssetCategory c : categories) {
            map.put(c.getId(), c.getName());
        }
        return map;
    }

    /** 递归收集某分类及其所有子孙分类 ID。 */
    private Set<Long> collectCategoryIds(Long categoryId) {
        if (categoryId == null) return null;
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
                for (AssetCategory child : children) stack.push(child.getId());
            }
        }
        return result;
    }

    /** 后台分页查询条件。 */
    private LambdaQueryWrapper<AssetInfo> buildAdminQueryWrapper(AdminAssetPageQuery query) {
        LambdaQueryWrapper<AssetInfo> wrapper = Wrappers.lambdaQuery();

        // keyword：模糊匹配 assetNo / name / brand / model
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            String kw = query.getKeyword().trim();
            wrapper.and(w -> w.like(AssetInfo::getAssetNo, kw)
                    .or().like(AssetInfo::getName, kw)
                    .or().like(AssetInfo::getBrand, kw)
                    .or().like(AssetInfo::getModel, kw));
        }

        // status：精确匹配
        if (query.getStatus() != null) {
            wrapper.eq(AssetInfo::getStatus, query.getStatus());
        }

        // categoryId：含子孙分类，用 IN
        if (query.getCategoryId() != null) {
            Set<Long> ids = collectCategoryIds(query.getCategoryId());
            if (ids != null && !ids.isEmpty()) {
                wrapper.in(AssetInfo::getCategoryId, ids);
            }
        }

        // sourceType：精确匹配
        if (query.getSourceType() != null) {
            wrapper.eq(AssetInfo::getSourceType, query.getSourceType());
        }

        // keeperId：精确匹配
        if (query.getKeeperId() != null) {
            wrapper.eq(AssetInfo::getKeeperId, query.getKeeperId());
        }

        // beginDate / endDate：按 purchaseDate 范围
        if (query.getBeginDate() != null && !query.getBeginDate().isBlank()) {
            wrapper.ge(AssetInfo::getPurchaseDate, parseDate(query.getBeginDate()));
        }
        if (query.getEndDate() != null && !query.getEndDate().isBlank()) {
            wrapper.le(AssetInfo::getPurchaseDate, parseDate(query.getEndDate()));
        }

        wrapper.orderByDesc(AssetInfo::getCreateTime);
        return wrapper;
    }

    /** 前台分页查询条件。 */
    private LambdaQueryWrapper<AssetInfo> buildUserQueryWrapper(UserAssetPageQuery query) {
        LambdaQueryWrapper<AssetInfo> wrapper = Wrappers.lambdaQuery();
        if (query.getCategoryId() != null) {
            Set<Long> ids = collectCategoryIds(query.getCategoryId());
            if (ids != null && !ids.isEmpty()) wrapper.in(AssetInfo::getCategoryId, ids);
        }
        if (query.getStatus() != null) wrapper.eq(AssetInfo::getStatus, query.getStatus());
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            String kw = query.getKeyword().trim();
            wrapper.and(w -> w.like(AssetInfo::getAssetNo, kw)
                    .or().like(AssetInfo::getName, kw)
                    .or().like(AssetInfo::getBrand, kw)
                    .or().like(AssetInfo::getModel, kw));
        }
        wrapper.orderByDesc(AssetInfo::getCreateTime);
        return wrapper;
    }

    /** 导出查询条件。 */
    private LambdaQueryWrapper<AssetInfo> buildExportQueryWrapper(AssetExportQuery query) {
        LambdaQueryWrapper<AssetInfo> wrapper = Wrappers.lambdaQuery();
        if (query.getCategoryId() != null) {
            Set<Long> ids = collectCategoryIds(query.getCategoryId());
            if (ids != null && !ids.isEmpty()) wrapper.in(AssetInfo::getCategoryId, ids);
        }
        if (query.getStatus() != null) wrapper.eq(AssetInfo::getStatus, query.getStatus());
        if (query.getSourceType() != null) wrapper.eq(AssetInfo::getSourceType, query.getSourceType());
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            String kw = query.getKeyword().trim();
            wrapper.and(w -> w.like(AssetInfo::getAssetNo, kw)
                    .or().like(AssetInfo::getName, kw)
                    .or().like(AssetInfo::getBrand, kw)
                    .or().like(AssetInfo::getModel, kw));
        }
        if (query.getBeginDate() != null && !query.getBeginDate().isBlank()) {
            wrapper.ge(AssetInfo::getPurchaseDate, parseDate(query.getBeginDate()));
        }
        if (query.getEndDate() != null && !query.getEndDate().isBlank()) {
            wrapper.le(AssetInfo::getPurchaseDate, parseDate(query.getEndDate()));
        }
        wrapper.orderByDesc(AssetInfo::getCreateTime);
        return wrapper;
    }

    /** CSV 单元格转义：含逗号/引号/换行时用双引号包裹，内部引号转义。 */
    private String csvCell(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /** 来源类型文案。 */
    private String sourceLabel(Integer sourceType) {
        if (sourceType == null) return "";
        return switch (sourceType) {
            case 1 -> "采购";
            case 2 -> "捐赠";
            case 3 -> "调拨";
            default -> "";
        };
    }
}
