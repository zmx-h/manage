package com.iecmp.seat.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.seat.dto.SeatSwapAuditDTO;
import com.iecmp.seat.dto.SeatSwapDTO;
import com.iecmp.seat.dto.SeatSwapHandleDTO;
import com.iecmp.seat.entity.SeatInfo;
import com.iecmp.seat.entity.SeatSwap;
import com.iecmp.seat.enums.SeatErrorCode;
import com.iecmp.seat.mapper.SeatSwapMapper;
import com.iecmp.seat.query.SeatSwapMyPageQuery;
import com.iecmp.seat.query.SeatSwapPageQuery;
import com.iecmp.seat.service.SeatInfoService;
import com.iecmp.seat.service.SeatSwapService;
import com.iecmp.seat.vo.SeatSwapMyPageVO;
import com.iecmp.seat.vo.SeatSwapPageVO;
import com.iecmp.seat.vo.SeatSwapVO;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/** 座位调换申请业务实现。 */
@Service
@RequiredArgsConstructor
public class SeatSwapServiceImpl extends ServiceImpl<SeatSwapMapper, SeatSwap>
        implements SeatSwapService {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SeatInfoService seatInfoService;
    private final SysUserService sysUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeatSwapVO createSwap(SeatSwapDTO dto, Long currentUserId) {
        if (dto.getTargetUserId() == null || dto.getTargetUserId().equals(currentUserId)) {
            throw error(SeatErrorCode.E50010);
        }
        SysUser target = sysUserService.getById(dto.getTargetUserId());
        if (target == null || target.getStatus() == null || target.getStatus() != 1) {
            throw error(SeatErrorCode.E50011);
        }
        SeatInfo applySeat = seatInfoService.getOne(Wrappers.<SeatInfo>lambdaQuery()
                .eq(SeatInfo::getUserId, currentUserId)
                .eq(SeatInfo::getStatus, 2)
                .last("LIMIT 1"), false);
        SeatInfo targetSeat = seatInfoService.getOne(Wrappers.<SeatInfo>lambdaQuery()
                .eq(SeatInfo::getUserId, dto.getTargetUserId())
                .eq(SeatInfo::getStatus, 2)
                .last("LIMIT 1"), false);
        if (applySeat == null || targetSeat == null || Objects.equals(applySeat.getId(), targetSeat.getId())) {
            throw error(SeatErrorCode.E50011);
        }
        long active = count(Wrappers.<SeatSwap>lambdaQuery()
                .in(SeatSwap::getStatus, 0, 1)
                .and(w -> w.and(a -> a.eq(SeatSwap::getApplyUserId, currentUserId)
                                .eq(SeatSwap::getTargetUserId, dto.getTargetUserId()))
                        .or(b -> b.eq(SeatSwap::getApplyUserId, dto.getTargetUserId())
                                .eq(SeatSwap::getTargetUserId, currentUserId))));
        if (active > 0) {
            throw error(SeatErrorCode.E50008);
        }

        SeatSwap swap = new SeatSwap();
        swap.setApplyUserId(currentUserId);
        swap.setTargetUserId(dto.getTargetUserId());
        swap.setApplySeatId(applySeat.getId());
        swap.setTargetSeatId(targetSeat.getId());
        swap.setReason(validateText(dto.getReason(), 500, "调换理由"));
        swap.setStatus(0);
        save(swap);

        SeatSwapVO vo = new SeatSwapVO();
        vo.setSwapId(swap.getId());
        vo.setApplySeatId(applySeat.getId());
        vo.setTargetSeatId(targetSeat.getId());
        return vo;
    }

    @Override
    public PageResult<SeatSwapMyPageVO> myPage(SeatSwapMyPageQuery query, Long currentUserId) {
        var wrapper = Wrappers.<SeatSwap>lambdaQuery()
                .eq(query.getStatus() != null, SeatSwap::getStatus, query.getStatus());
        int role = query.getRole() == null ? 0 : query.getRole();
        if (role == 1) {
            wrapper.eq(SeatSwap::getApplyUserId, currentUserId);
        } else if (role == 2) {
            wrapper.eq(SeatSwap::getTargetUserId, currentUserId);
        } else {
            wrapper.and(w -> w.eq(SeatSwap::getApplyUserId, currentUserId)
                    .or().eq(SeatSwap::getTargetUserId, currentUserId));
        }
        wrapper.orderByDesc(SeatSwap::getCreateTime).orderByDesc(SeatSwap::getId);
        IPage<SeatSwap> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        SwapLookups lookups = loadLookups(page.getRecords());
        return PageResult.of(page, swap -> toMyVO(swap, lookups, currentUserId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(Long id, SeatSwapHandleDTO dto, Long currentUserId) {
        SeatSwap swap = requireSwap(id);
        if (!swap.getTargetUserId().equals(currentUserId)) {
            throw error(SeatErrorCode.E50012);
        }
        if (swap.getStatus() != 0) {
            throw error(SeatErrorCode.E50013);
        }
        if (dto.getHandleStatus() == null || (dto.getHandleStatus() != 1 && dto.getHandleStatus() != 2)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "handleStatus 仅支持 1 同意或 2 拒绝");
        }
        swap.setStatus(dto.getHandleStatus() == 1 ? 1 : 3);
        swap.setHandleTime(LocalDateTime.now());
        swap.setHandleRemark(validateText(dto.getHandleRemark(), 255, "处理意见"));
        updateById(swap);
    }

    @Override
    public PageResult<SeatSwapPageVO> adminPage(SeatSwapPageQuery query) {
        var wrapper = Wrappers.<SeatSwap>lambdaQuery()
                .eq(query.getStatus() != null, SeatSwap::getStatus, query.getStatus())
                .ge(StringUtils.hasText(query.getBeginTime()), SeatSwap::getCreateTime,
                        parseDateTime(query.getBeginTime(), false))
                .le(StringUtils.hasText(query.getEndTime()), SeatSwap::getCreateTime,
                        parseDateTime(query.getEndTime(), true));
        if (query.getUserId() != null) {
            wrapper.and(w -> w.eq(SeatSwap::getApplyUserId, query.getUserId())
                    .or().eq(SeatSwap::getTargetUserId, query.getUserId()));
        }
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            List<Long> userIds = sysUserService.list(Wrappers.<SysUser>lambdaQuery()
                            .like(SysUser::getRealName, keyword)
                            .or().like(SysUser::getUsername, keyword))
                    .stream().map(SysUser::getId).toList();
            List<Long> seatIds = seatInfoService.list(Wrappers.<SeatInfo>lambdaQuery()
                            .like(SeatInfo::getSeatNo, keyword))
                    .stream().map(SeatInfo::getId).toList();
            if (userIds.isEmpty() && seatIds.isEmpty()) {
                return PageResult.empty(query.getPageNum(), query.getPageSize());
            }
            wrapper.and(w -> {
                if (!userIds.isEmpty()) {
                    w.in(SeatSwap::getApplyUserId, userIds)
                            .or().in(SeatSwap::getTargetUserId, userIds)
                            .or();
                }
                if (!seatIds.isEmpty()) {
                    w.in(SeatSwap::getApplySeatId, seatIds)
                            .or().in(SeatSwap::getTargetSeatId, seatIds);
                } else {
                    w.eq(SeatSwap::getId, -1L);
                }
            });
        }
        wrapper.orderByDesc(SeatSwap::getCreateTime).orderByDesc(SeatSwap::getId);
        IPage<SeatSwap> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        SwapLookups lookups = loadLookups(page.getRecords());
        return PageResult.of(page, swap -> toAdminVO(swap, lookups));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, SeatSwapAuditDTO dto, Long currentUserId) {
        SeatSwap swap = requireSwap(id);
        if (swap.getStatus() != 1) {
            throw error(SeatErrorCode.E50013);
        }
        if (dto.getAuditStatus() == null || (dto.getAuditStatus() != 1 && dto.getAuditStatus() != 2)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "auditStatus 仅支持 1 通过或 2 驳回");
        }
        if (dto.getAuditStatus() == 1) {
            SeatInfo applySeat = seatInfoService.getById(swap.getApplySeatId());
            SeatInfo targetSeat = seatInfoService.getById(swap.getTargetSeatId());
            if (applySeat == null || targetSeat == null
                    || !Objects.equals(applySeat.getUserId(), swap.getApplyUserId())
                    || !Objects.equals(targetSeat.getUserId(), swap.getTargetUserId())
                    || applySeat.getStatus() == null || applySeat.getStatus() != 2
                    || targetSeat.getStatus() == null || targetSeat.getStatus() != 2) {
                throw error(SeatErrorCode.E50011);
            }
            Long applyUser = applySeat.getUserId();
            LocalDateTime applyTime = applySeat.getAssignTime();
            applySeat.setUserId(targetSeat.getUserId());
            applySeat.setAssignTime(targetSeat.getAssignTime());
            targetSeat.setUserId(applyUser);
            targetSeat.setAssignTime(applyTime);
            if (!seatInfoService.updateById(applySeat) || !seatInfoService.updateById(targetSeat)) {
                throw error(SeatErrorCode.E50005);
            }
            swap.setStatus(2);
        } else {
            swap.setStatus(3);
        }
        swap.setAuditUserId(currentUserId);
        swap.setAuditTime(LocalDateTime.now());
        swap.setAuditRemark(validateText(dto.getAuditRemark(), 255, "审核意见"));
        updateById(swap);
    }

    private SeatSwap requireSwap(Long id) {
        SeatSwap swap = id == null ? null : getById(id);
        if (swap == null) {
            throw error(SeatErrorCode.E50009);
        }
        return swap;
    }

    private SwapLookups loadLookups(List<SeatSwap> swaps) {
        Set<Long> userIds = new java.util.LinkedHashSet<>();
        Set<Long> seatIds = new java.util.LinkedHashSet<>();
        for (SeatSwap swap : swaps) {
            userIds.add(swap.getApplyUserId());
            userIds.add(swap.getTargetUserId());
            userIds.add(swap.getAuditUserId());
            seatIds.add(swap.getApplySeatId());
            seatIds.add(swap.getTargetSeatId());
        }
        userIds.remove(null);
        seatIds.remove(null);
        Map<Long, SysUser> users = userIds.isEmpty() ? Map.of()
                : sysUserService.listByIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, Function.identity(), (a, b) -> a));
        Map<Long, SeatInfo> seats = seatIds.isEmpty() ? Map.of()
                : seatInfoService.listByIds(seatIds).stream()
                .collect(Collectors.toMap(SeatInfo::getId, Function.identity(), (a, b) -> a));
        return new SwapLookups(users, seats);
    }

    private SeatSwapMyPageVO toMyVO(SeatSwap swap, SwapLookups lookups, Long currentUserId) {
        SeatSwapMyPageVO vo = new SeatSwapMyPageVO();
        fillBase(vo, swap, lookups);
        vo.setIsTarget(Objects.equals(swap.getTargetUserId(), currentUserId) ? 1 : 0);
        return vo;
    }

    private SeatSwapPageVO toAdminVO(SeatSwap swap, SwapLookups lookups) {
        SeatSwapPageVO vo = new SeatSwapPageVO();
        vo.setSwapId(swap.getId());
        vo.setApplyUserId(swap.getApplyUserId());
        SysUser applyUser = lookups.users().get(swap.getApplyUserId());
        vo.setApplyUserName(applyUser == null ? null : applyUser.getRealName());
        vo.setApplyUserGrade(applyUser == null ? null : applyUser.getGrade());
        vo.setApplySeatId(swap.getApplySeatId());
        vo.setApplySeatNo(seatNo(lookups, swap.getApplySeatId()));
        vo.setTargetUserId(swap.getTargetUserId());
        SysUser targetUser = lookups.users().get(swap.getTargetUserId());
        vo.setTargetUserName(targetUser == null ? null : targetUser.getRealName());
        vo.setTargetUserGrade(targetUser == null ? null : targetUser.getGrade());
        vo.setTargetSeatId(swap.getTargetSeatId());
        vo.setTargetSeatNo(seatNo(lookups, swap.getTargetSeatId()));
        vo.setReason(swap.getReason());
        vo.setStatus(swap.getStatus());
        vo.setHandleTime(format(swap.getHandleTime()));
        vo.setHandleRemark(swap.getHandleRemark());
        vo.setAuditUserId(swap.getAuditUserId());
        SysUser auditor = lookups.users().get(swap.getAuditUserId());
        vo.setAuditUserName(auditor == null ? null : auditor.getRealName());
        vo.setAuditTime(format(swap.getAuditTime()));
        vo.setAuditRemark(swap.getAuditRemark());
        vo.setCreateTime(format(swap.getCreateTime()));
        return vo;
    }

    private void fillBase(SeatSwapMyPageVO vo, SeatSwap swap, SwapLookups lookups) {
        vo.setSwapId(swap.getId());
        vo.setApplyUserId(swap.getApplyUserId());
        SysUser applyUser = lookups.users().get(swap.getApplyUserId());
        vo.setApplyUserName(applyUser == null ? null : applyUser.getRealName());
        vo.setApplySeatId(swap.getApplySeatId());
        vo.setApplySeatNo(seatNo(lookups, swap.getApplySeatId()));
        vo.setTargetUserId(swap.getTargetUserId());
        SysUser targetUser = lookups.users().get(swap.getTargetUserId());
        vo.setTargetUserName(targetUser == null ? null : targetUser.getRealName());
        vo.setTargetSeatId(swap.getTargetSeatId());
        vo.setTargetSeatNo(seatNo(lookups, swap.getTargetSeatId()));
        vo.setReason(swap.getReason());
        vo.setStatus(swap.getStatus());
        vo.setHandleTime(format(swap.getHandleTime()));
        vo.setHandleRemark(swap.getHandleRemark());
        SysUser auditor = lookups.users().get(swap.getAuditUserId());
        vo.setAuditUserName(auditor == null ? null : auditor.getRealName());
        vo.setAuditTime(format(swap.getAuditTime()));
        vo.setAuditRemark(swap.getAuditRemark());
        vo.setCreateTime(format(swap.getCreateTime()));
    }

    private String seatNo(SwapLookups lookups, Long seatId) {
        SeatInfo seat = lookups.seats().get(seatId);
        return seat == null ? null : seat.getSeatNo();
    }

    private String validateText(String value, int maxLength, String label) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String text = value.trim();
        if (text.length() > maxLength) {
            throw new BusinessException(ResultCode.PARAM_ERROR, label + "最长 " + maxLength + " 字");
        }
        return text;
    }

    private LocalDateTime parseDateTime(String value, boolean endOfDay) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String text = value.trim();
        try {
            if (text.length() == 10) {
                LocalDate date = LocalDate.parse(text);
                return endOfDay ? date.atTime(23, 59, 59) : date.atStartOfDay();
            }
            return LocalDateTime.parse(text.contains("T") ? text : text,
                    text.contains("T") ? DateTimeFormatter.ISO_LOCAL_DATE_TIME : DATE_TIME_FMT);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "时间格式应为 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss");
        }
    }

    private String format(LocalDateTime time) {
        return time == null ? null : time.format(DATE_TIME_FMT);
    }

    private BusinessException error(SeatErrorCode code) {
        return new BusinessException(code.getCode(), code.getMsg());
    }

    private record SwapLookups(Map<Long, SysUser> users, Map<Long, SeatInfo> seats) {
    }
}