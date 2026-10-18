package com.iecmp.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.asset.dto.AdminAssetBorrowReturnUpdateDTO;
import com.iecmp.asset.dto.AssetBorrowAuditDTO;
import com.iecmp.asset.dto.AssetBorrowDTO;
import com.iecmp.asset.dto.UserAssetBorrowReturnUpdateDTO;
import com.iecmp.asset.entity.AssetBorrow;
import com.iecmp.asset.entity.AssetInfo;
import com.iecmp.asset.entity.AssetRecord;
import com.iecmp.asset.enums.AssetErrorCode;
import com.iecmp.asset.enums.AssetRecordTypeEnum;
import com.iecmp.asset.enums.AssetStatusEnum;
import com.iecmp.asset.enums.BorrowStatusEnum;
import com.iecmp.asset.enums.BorrowTypeEnum;
import com.iecmp.asset.mapper.AssetBorrowMapper;
import com.iecmp.asset.mapper.AssetInfoMapper;
import com.iecmp.asset.query.AssetBorrowMyPageQuery;
import com.iecmp.asset.query.AssetBorrowPageQuery;
import com.iecmp.asset.service.AssetBorrowService;
import com.iecmp.asset.service.AssetRecordService;
import com.iecmp.asset.vo.AssetBorrowMyPageVO;
import com.iecmp.asset.vo.AssetBorrowPageVO;
import com.iecmp.asset.vo.AssetBorrowVO;
import com.iecmp.common.context.UserContext;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 借用/领用业务实现（{@code asset_borrow}）。
 *
 * @author IECMP 后端组
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetBorrowServiceImpl extends ServiceImpl<AssetBorrowMapper, AssetBorrow>
        implements AssetBorrowService {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final AssetInfoMapper assetInfoMapper;
    private final SysUserService sysUserService;
    private final AssetRecordService assetRecordService;

    // ============================== 4-20 借用审核分页 ==============================

    @Override
    public PageResult<AssetBorrowPageVO> page(AssetBorrowPageQuery query) {
        LambdaQueryWrapper<AssetBorrow> wrapper = buildPageWrapper(query);
        Page<AssetBorrow> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<AssetBorrow> result = this.page(page, wrapper);

        // 批量回填资产与用户信息
        Set<Long> assetIds = new HashSet<>();
        Set<Long> userIds = new HashSet<>();
        for (AssetBorrow b : result.getRecords()) {
            assetIds.add(b.getAssetId());
            if (b.getUserId() != null) userIds.add(b.getUserId());
            if (b.getAuditUserId() != null) userIds.add(b.getAuditUserId());
        }
        Map<Long, AssetInfo> assetMap = getAssetMap(assetIds);
        Map<Long, SysUser> userMap = getUserMap(userIds);

        return PageResult.of(result, b -> {
            AssetBorrowPageVO vo = new AssetBorrowPageVO();
            vo.setBorrowId(b.getId());
            vo.setAssetId(b.getAssetId());
            AssetInfo asset = assetMap.get(b.getAssetId());
            vo.setAssetNo(asset == null ? null : asset.getAssetNo());
            vo.setAssetName(asset == null ? null : asset.getName());
            vo.setBorrowType(b.getBorrowType());
            vo.setUserId(b.getUserId());
            SysUser borrower = userMap.get(b.getUserId());
            vo.setUserName(borrower == null ? null : borrower.getRealName());
            vo.setGrade(borrower == null ? null : borrower.getGrade());
            vo.setPurpose(b.getPurpose());
            vo.setBorrowTime(fmt(b.getBorrowTime()));
            vo.setExpectReturnTime(fmt(b.getExpectReturnTime()));
            vo.setActualReturnTime(fmt(b.getActualReturnTime()));
            vo.setStatus(b.getStatus());
            vo.setAuditUserId(b.getAuditUserId());
            SysUser auditor = userMap.get(b.getAuditUserId());
            vo.setAuditUserName(auditor == null ? null : auditor.getRealName());
            vo.setAuditTime(fmt(b.getAuditTime()));
            vo.setAuditRemark(b.getAuditRemark());
            return vo;
        });
    }

    // ============================== 4-21 借用审核 ==============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, AssetBorrowAuditDTO dto) {
        Integer auditStatus = dto.getAuditStatus();
        if (auditStatus == null || (auditStatus != 1 && auditStatus != 2)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "审核状态只能为 1 通过或 2 驳回");
        }

        AssetBorrow borrow = this.getById(id);
        if (borrow == null) {
            throw new BusinessException(AssetErrorCode.E40008.getCode(), AssetErrorCode.E40008.getMsg());
        }
        if (!BorrowStatusEnum.PENDING.getValue().equals(borrow.getStatus())) {
            throw new BusinessException(AssetErrorCode.E40017.getCode(), AssetErrorCode.E40017.getMsg());
        }
        if (BorrowTypeEnum.of(borrow.getBorrowType()) == null) {
            throw new BusinessException(AssetErrorCode.E40017.getCode(), AssetErrorCode.E40017.getMsg());
        }

        LocalDateTime now = LocalDateTime.now();
        Long currentUserId = UserContext.getUserIdOrDefault();

        if (auditStatus == 2) {
            boolean updated = this.update(Wrappers.<AssetBorrow>lambdaUpdate()
                    .eq(AssetBorrow::getId, id)
                    .eq(AssetBorrow::getStatus, BorrowStatusEnum.PENDING.getValue())
                    .set(AssetBorrow::getStatus, BorrowStatusEnum.REJECTED.getValue())
                    .set(AssetBorrow::getAuditUserId, currentUserId)
                    .set(AssetBorrow::getAuditTime, now)
                    .set(AssetBorrow::getAuditRemark, dto.getAuditRemark()));
            if (!updated) {
                throw new BusinessException(AssetErrorCode.E40017.getCode(), AssetErrorCode.E40017.getMsg());
            }
            return;
        }

        AssetInfo asset = assetInfoMapper.selectById(borrow.getAssetId());
        if (asset == null) {
            throw new BusinessException(AssetErrorCode.E40001.getCode(), AssetErrorCode.E40001.getMsg());
        }

        boolean updated = this.update(Wrappers.<AssetBorrow>lambdaUpdate()
                .eq(AssetBorrow::getId, id)
                .eq(AssetBorrow::getStatus, BorrowStatusEnum.PENDING.getValue())
                .set(AssetBorrow::getStatus, BorrowStatusEnum.APPROVED.getValue())
                .set(AssetBorrow::getAuditUserId, currentUserId)
                .set(AssetBorrow::getAuditTime, now)
                .set(AssetBorrow::getAuditRemark, dto.getAuditRemark()));
        if (!updated) {
            throw new BusinessException(AssetErrorCode.E40017.getCode(), AssetErrorCode.E40017.getMsg());
        }

        int assetRows = assetInfoMapper.update(null, Wrappers.<AssetInfo>lambdaUpdate()
                .eq(AssetInfo::getId, asset.getId())
                .in(AssetInfo::getStatus, AssetStatusEnum.IN_USE.getValue(), AssetStatusEnum.IDLE.getValue())
                .set(AssetInfo::getStatus, AssetStatusEnum.BORROWED.getValue()));
        if (assetRows == 0) {
            throw new BusinessException(AssetErrorCode.E40006.getCode(), AssetErrorCode.E40006.getMsg());
        }

        boolean isReceive = BorrowTypeEnum.RECEIVE.getValue().equals(borrow.getBorrowType());
        AssetRecord record = new AssetRecord();
        record.setAssetId(asset.getId());
        record.setRecordType(isReceive ? AssetRecordTypeEnum.RECEIVE.getValue() : AssetRecordTypeEnum.BORROW.getValue());
        record.setFromUserId(asset.getKeeperId());
        record.setToUserId(borrow.getUserId());
        record.setFromLocation(asset.getLocation());
        record.setToLocation(null);
        record.setRecordTime(now);
        record.setOperatorId(currentUserId);
        record.setRemark(isReceive ? "领用审核通过" : "借用审核通过");
        assetRecordService.save(record);
    }
    // ============================== 4-22 管理员代归还 ==============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminReturn(Long id, AdminAssetBorrowReturnUpdateDTO dto) {
        doReturn(id, dto.getRemark(), null);
    }

    // ============================== 4-05 借用/领用申请 ==============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssetBorrowVO apply(AssetBorrowDTO dto) {
        BorrowTypeEnum borrowType = BorrowTypeEnum.of(dto.getBorrowType());
        if (borrowType == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "借用类型只能为 1 借用或 2 领用");
        }

        // 锁定资产行，串行化同一资产的申请，避免并发产生多条待审核记录
        AssetInfo asset = assetInfoMapper.selectOne(Wrappers.<AssetInfo>lambdaQuery()
                .eq(AssetInfo::getId, dto.getAssetId())
                .last("FOR UPDATE"));
        if (asset == null) {
            throw new BusinessException(AssetErrorCode.E40001.getCode(), AssetErrorCode.E40001.getMsg());
        }
        Long pending = this.count(Wrappers.lambdaQuery(AssetBorrow.class)
                .eq(AssetBorrow::getAssetId, dto.getAssetId())
                .eq(AssetBorrow::getStatus, BorrowStatusEnum.PENDING.getValue()));
        if (pending != null && pending > 0) {
            throw new BusinessException(AssetErrorCode.E40019.getCode(), AssetErrorCode.E40019.getMsg());
        }
        AssetStatusEnum assetStatus = AssetStatusEnum.of(asset.getStatus());
        if (AssetStatusEnum.BORROWED.equals(assetStatus)) {
            throw new BusinessException(AssetErrorCode.E40007.getCode(), AssetErrorCode.E40007.getMsg());
        }
        if (AssetStatusEnum.SCRAPPED.equals(assetStatus)) {
            throw new BusinessException(AssetErrorCode.E40015.getCode(), AssetErrorCode.E40015.getMsg());
        }
        if (!AssetStatusEnum.IN_USE.equals(assetStatus) && !AssetStatusEnum.IDLE.equals(assetStatus)) {
            throw new BusinessException(AssetErrorCode.E40006.getCode(), AssetErrorCode.E40006.getMsg());
        }

        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        AssetBorrow borrow = new AssetBorrow();
        borrow.setAssetId(dto.getAssetId());
        borrow.setUserId(currentUserId);
        borrow.setBorrowType(borrowType.getValue());
        borrow.setPurpose(dto.getPurpose());
        borrow.setBorrowTime(LocalDateTime.now());
        borrow.setExpectReturnTime(parseDateTime(dto.getExpectReturnTime()));
        borrow.setStatus(BorrowStatusEnum.PENDING.getValue());
        this.save(borrow);

        AssetBorrowVO vo = new AssetBorrowVO();
        vo.setBorrowId(borrow.getId());
        return vo;
    }
    // ============================== 4-06 我的借用记录 ==============================

    @Override
    public PageResult<AssetBorrowMyPageVO> myPage(AssetBorrowMyPageQuery query) {
        LambdaQueryWrapper<AssetBorrow> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AssetBorrow::getUserId, UserContext.getUserId());
        if (query.getStatus() != null) wrapper.eq(AssetBorrow::getStatus, query.getStatus());
        if (query.getBorrowType() != null) wrapper.eq(AssetBorrow::getBorrowType, query.getBorrowType());
        if (query.getBeginTime() != null && !query.getBeginTime().isBlank()) {
            wrapper.ge(AssetBorrow::getBorrowTime, parseDateTime(query.getBeginTime()));
        }
        if (query.getEndTime() != null && !query.getEndTime().isBlank()) {
            wrapper.le(AssetBorrow::getBorrowTime, parseDateTimeEnd(query.getEndTime()));
        }
        wrapper.orderByDesc(AssetBorrow::getBorrowTime);

        Page<AssetBorrow> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<AssetBorrow> result = this.page(page, wrapper);

        Set<Long> assetIds = result.getRecords().stream()
                .map(AssetBorrow::getAssetId).collect(Collectors.toSet());
        Map<Long, AssetInfo> assetMap = getAssetMap(assetIds);

        return PageResult.of(result, b -> {
            AssetBorrowMyPageVO vo = new AssetBorrowMyPageVO();
            vo.setBorrowId(b.getId());
            vo.setAssetId(b.getAssetId());
            AssetInfo asset = assetMap.get(b.getAssetId());
            vo.setAssetNo(asset == null ? null : asset.getAssetNo());
            vo.setAssetName(asset == null ? null : asset.getName());
            vo.setBorrowType(b.getBorrowType());
            vo.setPurpose(b.getPurpose());
            vo.setBorrowTime(fmt(b.getBorrowTime()));
            vo.setExpectReturnTime(fmt(b.getExpectReturnTime()));
            vo.setActualReturnTime(fmt(b.getActualReturnTime()));
            vo.setStatus(b.getStatus());
            vo.setAuditRemark(b.getAuditRemark());
            return vo;
        });
    }

    // ============================== 4-07 本人归还登记 ==============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userReturn(Long id, UserAssetBorrowReturnUpdateDTO dto) {
        doReturn(id, dto.getRemark(), UserContext.getUserId());
    }

    // ============================== 内部方法 ==============================

    /** 通用归还逻辑。ownerUserId 非空时校验只能归还本人记录（用户端）。 */
    private void doReturn(Long id, String remark, Long ownerUserId) {
        AssetBorrow borrow = this.getById(id);
        if (borrow == null) {
            throw new BusinessException(AssetErrorCode.E40008.getCode(), AssetErrorCode.E40008.getMsg());
        }

        if (BorrowTypeEnum.RECEIVE.getValue().equals(borrow.getBorrowType())) {
            throw new BusinessException(AssetErrorCode.E40011.getCode(), AssetErrorCode.E40011.getMsg());
        }
        if (!BorrowStatusEnum.APPROVED.getValue().equals(borrow.getStatus())
                && !BorrowStatusEnum.OVERDUE.getValue().equals(borrow.getStatus())) {
            throw new BusinessException(AssetErrorCode.E40010.getCode(), AssetErrorCode.E40010.getMsg());
        }
        if (ownerUserId != null && !ownerUserId.equals(borrow.getUserId())) {
            throw new BusinessException(AssetErrorCode.E40009.getCode(), AssetErrorCode.E40009.getMsg());
        }

        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        LocalDateTime now = LocalDateTime.now();

        boolean updated = this.update(Wrappers.<AssetBorrow>lambdaUpdate()
                .eq(AssetBorrow::getId, id)
                .in(AssetBorrow::getStatus,
                        BorrowStatusEnum.APPROVED.getValue(), BorrowStatusEnum.OVERDUE.getValue())
                .set(AssetBorrow::getStatus, BorrowStatusEnum.RETURNED.getValue())
                .set(AssetBorrow::getActualReturnTime, now));
        if (!updated) {
            throw new BusinessException(AssetErrorCode.E40010.getCode(), AssetErrorCode.E40010.getMsg());
        }

        AssetInfo asset = assetInfoMapper.selectById(borrow.getAssetId());
        if (asset == null) {
            throw new BusinessException(AssetErrorCode.E40001.getCode(), AssetErrorCode.E40001.getMsg());
        }
        int assetRows = assetInfoMapper.update(null, Wrappers.<AssetInfo>lambdaUpdate()
                .eq(AssetInfo::getId, asset.getId())
                .eq(AssetInfo::getStatus, AssetStatusEnum.BORROWED.getValue())
                .set(AssetInfo::getStatus, AssetStatusEnum.IN_USE.getValue()));
        if (assetRows == 0) {
            throw new BusinessException(AssetErrorCode.E40006.getCode(), AssetErrorCode.E40006.getMsg());
        }

        AssetRecord record = new AssetRecord();
        record.setAssetId(borrow.getAssetId());
        record.setRecordType(AssetRecordTypeEnum.RETURN.getValue());
        record.setFromUserId(borrow.getUserId());
        record.setToUserId(asset.getKeeperId());
        record.setFromLocation(null);
        record.setToLocation(asset.getLocation());
        record.setRecordTime(now);
        record.setOperatorId(currentUserId);
        record.setRemark(remark);
        assetRecordService.save(record);
    }
    /** 构建管理端分页查询条件。 */
    private LambdaQueryWrapper<AssetBorrow> buildPageWrapper(AssetBorrowPageQuery query) {
        LambdaQueryWrapper<AssetBorrow> wrapper = Wrappers.lambdaQuery();
        if (query.getStatus() != null) wrapper.eq(AssetBorrow::getStatus, query.getStatus());
        if (query.getBorrowType() != null) wrapper.eq(AssetBorrow::getBorrowType, query.getBorrowType());
        if (query.getUserId() != null) wrapper.eq(AssetBorrow::getUserId, query.getUserId());
        if (query.getAssetId() != null) wrapper.eq(AssetBorrow::getAssetId, query.getAssetId());

        if (query.getBeginTime() != null && !query.getBeginTime().isBlank()) {
            wrapper.ge(AssetBorrow::getBorrowTime, parseDateTime(query.getBeginTime()));
        }
        if (query.getEndTime() != null && !query.getEndTime().isBlank()) {
            wrapper.le(AssetBorrow::getBorrowTime, parseDateTimeEnd(query.getEndTime()));
        }

        // keyword：资产编号 / 资产名称 / 借用人姓名
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            String kw = query.getKeyword().trim();
            if (kw.length() > 100) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "关键字长度不能超过 100 个字符");
            }
            String likeValue = "%" + kw + "%";
            wrapper.and(w -> w
                    .apply("EXISTS (SELECT 1 FROM asset_info ai "
                            + "WHERE ai.id = asset_borrow.asset_id AND ai.deleted = 0 "
                            + "AND (ai.asset_no LIKE {0} OR ai.name LIKE {0}))", likeValue)
                    .or()
                    .apply("EXISTS (SELECT 1 FROM sys_user u "
                            + "WHERE u.id = asset_borrow.user_id AND u.deleted = 0 "
                            + "AND u.real_name LIKE {0})", likeValue));
        }

        wrapper.orderByDesc(AssetBorrow::getBorrowTime);
        return wrapper;
    }

    private Map<Long, AssetInfo> getAssetMap(Collection<Long> assetIds) {
        if (assetIds == null || assetIds.isEmpty()) return Collections.emptyMap();
        List<AssetInfo> list = assetInfoMapper.selectBatchIds(assetIds);
        Map<Long, AssetInfo> map = new HashMap<>();
        for (AssetInfo a : list) map.put(a.getId(), a);
        return map;
    }

    private Map<Long, SysUser> getUserMap(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return Collections.emptyMap();
        List<SysUser> list = sysUserService.listByIds(userIds);
        Map<Long, SysUser> map = new HashMap<>();
        for (SysUser u : list) map.put(u.getId(), u);
        return map;
    }

    private String fmt(LocalDateTime dt) {
        return dt == null ? null : dt.format(DATE_TIME_FMT);
    }

    /** 解析日期时间，支持 yyyy-MM-dd HH:mm:ss 与 yyyy-MM-dd（取 00:00:00）。 */
    private LocalDateTime parseDateTime(String text) {
        if (text == null || text.isBlank()) return null;
        String t = text.trim();
        try {
            return LocalDateTime.parse(t, DATE_TIME_FMT);
        } catch (DateTimeParseException ignore) {
            try {
                return LocalDate.parse(t, DATE_FMT).atStartOfDay();
            } catch (DateTimeParseException e) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "时间格式错误");
            }
        }
    }

    /** 解析结束时间，yyyy-MM-dd 取当天 23:59:59。 */
    private LocalDateTime parseDateTimeEnd(String text) {
        if (text == null || text.isBlank()) return null;
        String t = text.trim();
        try {
            return LocalDateTime.parse(t, DATE_TIME_FMT);
        } catch (DateTimeParseException ignore) {
            try {
                return LocalDate.parse(t, DATE_FMT).atTime(LocalTime.of(23, 59, 59));
            } catch (DateTimeParseException e) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "时间格式错误");
            }
        }
    }
}
