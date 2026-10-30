package com.iecmp.seat.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.seat.dto.SeatAddDTO;
import com.iecmp.seat.dto.SeatApplyAuditDTO;
import com.iecmp.seat.dto.SeatAreaAddDTO;
import com.iecmp.seat.dto.SeatAreaUpdateDTO;
import com.iecmp.seat.dto.SeatAssignDTO;
import com.iecmp.seat.dto.SeatBatchDTO;
import com.iecmp.seat.dto.SeatPlanDragDTO;
import com.iecmp.seat.dto.SeatStatusDTO;
import com.iecmp.seat.dto.SeatSwapAuditDTO;
import com.iecmp.seat.dto.SeatUpdateDTO;
import com.iecmp.seat.entity.SeatApply;
import com.iecmp.seat.entity.SeatArea;
import com.iecmp.seat.entity.SeatInfo;
import com.iecmp.seat.entity.SeatSwap;
import com.iecmp.seat.enums.SeatErrorCode;
import com.iecmp.seat.query.AdminSeatPlanQuery;
import com.iecmp.seat.query.SeatApplyPageQuery;
import com.iecmp.seat.query.SeatSwapPageQuery;
import com.iecmp.seat.service.SeatAdminService;
import com.iecmp.seat.service.SeatApplyService;
import com.iecmp.seat.service.SeatAreaService;
import com.iecmp.seat.service.SeatInfoService;
import com.iecmp.seat.service.SeatSwapService;
import com.iecmp.seat.vo.AdminSeatPlanVO;
import com.iecmp.seat.vo.SeatApplyPageVO;
import com.iecmp.seat.vo.SeatAreaCreateVO;
import com.iecmp.seat.vo.SeatAreaListVO;
import com.iecmp.seat.vo.SeatAssignVO;
import com.iecmp.seat.vo.SeatBatchVO;
import com.iecmp.seat.vo.SeatCreateVO;
import com.iecmp.seat.vo.SeatPlanDragVO;
import com.iecmp.seat.vo.SeatSwapPageVO;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/** 管理端工位业务实现。 */
@Service
@RequiredArgsConstructor
public class SeatAdminServiceImpl implements SeatAdminService {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SeatAreaService seatAreaService;
    private final SeatInfoService seatInfoService;
    private final SeatApplyService seatApplyService;
    private final SeatSwapService seatSwapService;
    private final SysUserService sysUserService;

    @Override
    public List<SeatAreaListVO> areaList() {
        List<SeatArea> areas = seatAreaService.list(Wrappers.<SeatArea>lambdaQuery()
                .orderByAsc(SeatArea::getSortNo).orderByAsc(SeatArea::getId));
        if (areas.isEmpty()) {
            return List.of();
        }
        List<Long> areaIds = areas.stream().map(SeatArea::getId).toList();
        Map<Long, Long> counts = seatInfoService.list(Wrappers.<SeatInfo>lambdaQuery()
                        .in(SeatInfo::getAreaId, areaIds))
                .stream().collect(Collectors.groupingBy(SeatInfo::getAreaId, Collectors.counting()));
        return areas.stream().map(area -> {
            SeatAreaListVO vo = new SeatAreaListVO();
            vo.setAreaId(area.getId());
            vo.setName(area.getName());
            vo.setPlanImageUrl(area.getPlanImageUrl());
            vo.setWidth(area.getWidth());
            vo.setHeight(area.getHeight());
            vo.setDescription(area.getDescription());
            vo.setSeatCount(counts.getOrDefault(area.getId(), 0L).intValue());
            vo.setSortNo(area.getSortNo());
            return vo;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeatAreaCreateVO createArea(SeatAreaAddDTO dto) {
        SeatArea area = new SeatArea();
        area.setName(requireText(dto.getName(), 64, "区域名称"));
        area.setPlanImageUrl(validateUrl(dto.getPlanImageUrl()));
        area.setWidth(validateDimension(dto.getWidth()));
        area.setHeight(validateDimension(dto.getHeight()));
        area.setDescription(validateText(dto.getDescription(), 500, "区域说明"));
        area.setSortNo(validateSortNo(dto.getSortNo()));
        seatAreaService.save(area);
        SeatAreaCreateVO vo = new SeatAreaCreateVO();
        vo.setAreaId(area.getId());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArea(SeatAreaUpdateDTO dto) {
        SeatArea area = requireArea(dto.getAreaId());
        area.setName(requireText(dto.getName(), 64, "区域名称"));
        if (dto.getPlanImageUrl() != null) {
            area.setPlanImageUrl(validateUrl(dto.getPlanImageUrl()));
        }
        if (dto.getWidth() != null) {
            area.setWidth(validateDimension(dto.getWidth()));
        }
        if (dto.getHeight() != null) {
            area.setHeight(validateDimension(dto.getHeight()));
        }
        if (dto.getDescription() != null) {
            area.setDescription(validateText(dto.getDescription(), 500, "区域说明"));
        }
        if (dto.getSortNo() != null) {
            area.setSortNo(validateSortNo(dto.getSortNo()));
        }
        if (area.getWidth() == null || area.getWidth() <= 0
                || area.getHeight() == null || area.getHeight() <= 0) {
            throw error(SeatErrorCode.E50014);
        }
        seatAreaService.updateById(area);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArea(Long id) {
        requireArea(id);
        if (seatInfoService.count(Wrappers.<SeatInfo>lambdaQuery()
                .eq(SeatInfo::getAreaId, id)) > 0) {
            throw error(SeatErrorCode.E50002);
        }
        seatAreaService.removeById(id);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeatCreateVO createSeat(SeatAddDTO dto) {
        requireArea(dto.getAreaId());
        String seatNo = validateSeatNo(dto.getSeatNo());
        if (seatInfoService.count(Wrappers.<SeatInfo>lambdaQuery()
                .eq(SeatInfo::getAreaId, dto.getAreaId())
                .eq(SeatInfo::getSeatNo, seatNo)) > 0) {
            throw error(SeatErrorCode.E50004);
        }
        SeatInfo seat = new SeatInfo();
        seat.setAreaId(dto.getAreaId());
        seat.setSeatNo(seatNo);
        seat.setSeatType(validateSeatType(dto.getSeatType()));
        seat.setXRatio(ratio(dto.getXRatio(), "X 坐标"));
        seat.setYRatio(ratio(dto.getYRatio(), "Y 坐标"));
        seat.setWidthRatio(ratio(dto.getWidthRatio(), "宽度"));
        seat.setHeightRatio(ratio(dto.getHeightRatio(), "高度"));
        seat.setRotation(rotation(dto.getRotation()));
        seat.setStatus(1);
        seat.setRemark(validateText(dto.getRemark(), 255, "备注"));
        seatInfoService.save(seat);
        SeatCreateVO vo = new SeatCreateVO();
        vo.setSeatId(seat.getId());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSeat(SeatUpdateDTO dto) {
        SeatInfo seat = requireSeat(dto.getSeatId());
        requireArea(dto.getAreaId());
        String seatNo = validateSeatNo(dto.getSeatNo());
        if (seatInfoService.count(Wrappers.<SeatInfo>lambdaQuery()
                .eq(SeatInfo::getAreaId, dto.getAreaId())
                .eq(SeatInfo::getSeatNo, seatNo)
                .ne(SeatInfo::getId, seat.getId())) > 0) {
            throw error(SeatErrorCode.E50004);
        }
        seat.setAreaId(dto.getAreaId());
        seat.setSeatNo(seatNo);
        if (dto.getSeatType() != null) {
            seat.setSeatType(validateSeatType(dto.getSeatType()));
        }
        if (dto.getXRatio() != null) {
            seat.setXRatio(ratio(dto.getXRatio(), "X 坐标"));
        }
        if (dto.getYRatio() != null) {
            seat.setYRatio(ratio(dto.getYRatio(), "Y 坐标"));
        }
        if (dto.getWidthRatio() != null) {
            seat.setWidthRatio(ratio(dto.getWidthRatio(), "宽度"));
        }
        if (dto.getHeightRatio() != null) {
            seat.setHeightRatio(ratio(dto.getHeightRatio(), "高度"));
        }
        if (dto.getRotation() != null) {
            seat.setRotation(rotation(dto.getRotation()));
        }
        if (dto.getRemark() != null) {
            seat.setRemark(validateText(dto.getRemark(), 255, "备注"));
        }
        if (!seatInfoService.updateById(seat)) {
            throw error(SeatErrorCode.E50003);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeatBatchVO saveLayout(SeatBatchDTO dto) {
        requireArea(dto.getAreaId());
        List<SeatBatchDTO.SeatDTO> seats = dto.getSeats();
        if (seats == null || seats.isEmpty() || seats.size() > 200) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "工位坐标数组需为 1~200 项");
        }
        Set<Long> seen = new HashSet<>();
        int updated = 0;
        for (SeatBatchDTO.SeatDTO item : seats) {
            if (item.getSeatId() == null || !seen.add(item.getSeatId())) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "seatId 不能为空且不能重复");
            }
            SeatInfo seat = requireSeat(item.getSeatId());
            if (!Objects.equals(seat.getAreaId(), dto.getAreaId())) {
                throw error(SeatErrorCode.E50003);
            }
            if (item.getXRatio() != null) {
                seat.setXRatio(ratio(item.getXRatio(), "X 坐标"));
            }
            if (item.getYRatio() != null) {
                seat.setYRatio(ratio(item.getYRatio(), "Y 坐标"));
            }
            if (item.getWidthRatio() != null) {
                seat.setWidthRatio(ratio(item.getWidthRatio(), "宽度"));
            }
            if (item.getHeightRatio() != null) {
                seat.setHeightRatio(ratio(item.getHeightRatio(), "高度"));
            }
            if (item.getRotation() != null) {
                seat.setRotation(rotation(item.getRotation()));
            }
            if (!seatInfoService.updateById(seat)) {
                throw error(SeatErrorCode.E50003);
            }
            updated++;
        }
        SeatBatchVO vo = new SeatBatchVO();
        vo.setUpdatedCount(updated);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSeat(Long id) {
        SeatInfo seat = requireSeat(id);
        if (seat.getUserId() != null || (seat.getStatus() != null && seat.getStatus() == 2)) {
            throw error(SeatErrorCode.E50005);
        }
        long pendingApply = seatApplyService.count(Wrappers.<SeatApply>lambdaQuery()
                .eq(SeatApply::getStatus, 0)
                .and(w -> w.eq(SeatApply::getSeatId, id).or().eq(SeatApply::getAssignSeatId, id)));
        long pendingSwap = seatSwapService.count(Wrappers.<SeatSwap>lambdaQuery()
                .in(SeatSwap::getStatus, 0, 1)
                .and(w -> w.eq(SeatSwap::getApplySeatId, id).or().eq(SeatSwap::getTargetSeatId, id)));
        if (pendingApply > 0 || pendingSwap > 0) {
            throw error(SeatErrorCode.E50008);
        }
        seatInfoService.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeatAssignVO assignSeat(Long id, SeatAssignDTO dto) {
        SeatInfo target = requireSeat(id);
        Long released = assignInternal(target, dto.getUserId());
        SeatAssignVO vo = new SeatAssignVO();
        vo.setSeatId(id);
        vo.setReleasedSeatId(released);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseSeat(Long id) {
        SeatInfo seat = requireSeat(id);
        if (seat.getStatus() == null || seat.getStatus() != 2 || seat.getUserId() == null) {
            throw error(SeatErrorCode.E50008);
        }
        seat.setUserId(null);
        seat.setAssignTime(null);
        seat.setStatus(1);
        if (!seatInfoService.updateById(seat)) {
            throw error(SeatErrorCode.E50005);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeSeatStatus(Long id, SeatStatusDTO dto) {
        SeatInfo seat = requireSeat(id);
        Integer status = dto.getStatus();
        if (status == null || (status != 1 && status != 3 && status != 4)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "目标状态仅支持 1 空闲、3 预留、4 维修中");
        }
        if (seat.getStatus() != null && seat.getStatus() == 2) {
            throw error(SeatErrorCode.E50008);
        }
        if (seat.getStatus() != null && seat.getStatus().equals(status)
                && Objects.equals(dto.getRemark(), seat.getRemark())) {
            return;
        }
        seat.setStatus(status);
        seat.setRemark(validateText(dto.getRemark(), 255, "变更原因"));
        if (!seatInfoService.updateById(seat)) {
            throw error(SeatErrorCode.E50005);
        }
    }
    @Override
    public List<AdminSeatPlanVO> plan(AdminSeatPlanQuery query) {
        var areaWrapper = Wrappers.<SeatArea>lambdaQuery()
                .eq(query != null && query.getAreaId() != null, SeatArea::getId,
                        query == null ? null : query.getAreaId())
                .orderByAsc(SeatArea::getSortNo).orderByAsc(SeatArea::getId);
        List<SeatArea> areas = seatAreaService.list(areaWrapper);
        if (areas.isEmpty()) {
            return List.of();
        }
        List<Long> areaIds = areas.stream().map(SeatArea::getId).toList();
        List<SeatInfo> seats = seatInfoService.list(Wrappers.<SeatInfo>lambdaQuery()
                .in(SeatInfo::getAreaId, areaIds)
                .orderByAsc(SeatInfo::getSeatNo));
        Set<Long> userIds = seats.stream().map(SeatInfo::getUserId).filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<Long, SysUser> users = loadUsers(userIds);
        Map<Long, List<SeatInfo>> seatMap = seats.stream()
                .collect(Collectors.groupingBy(SeatInfo::getAreaId));
        List<AdminSeatPlanVO> result = new ArrayList<>(areas.size());
        for (SeatArea area : areas) {
            List<SeatInfo> areaSeats = seatMap.getOrDefault(area.getId(), List.of());
            AdminSeatPlanVO vo = new AdminSeatPlanVO();
            vo.setAreaId(area.getId());
            vo.setName(area.getName());
            vo.setPlanImageUrl(area.getPlanImageUrl());
            vo.setWidth(area.getWidth());
            vo.setHeight(area.getHeight());
            vo.setSortNo(area.getSortNo());
            vo.setSeatStat(stat(areaSeats));
            vo.setSeats(areaSeats.stream().map(seat -> toPlanSeat(seat, users)).toList());
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeatPlanDragVO drag(SeatPlanDragDTO dto) {
        if (Objects.equals(dto.getFromSeatId(), dto.getToSeatId())) {
            throw error(SeatErrorCode.E50016);
        }
        SeatInfo from = requireSeat(dto.getFromSeatId());
        SeatInfo to = requireSeat(dto.getToSeatId());
        if (from.getUserId() == null || from.getStatus() == null || from.getStatus() != 2) {
            throw error(SeatErrorCode.E50015);
        }
        if (to.getStatus() != null && (to.getStatus() == 3 || to.getStatus() == 4)) {
            throw error(SeatErrorCode.E50017);
        }
        if (Objects.equals(from.getUserId(), to.getUserId())) {
            throw error(SeatErrorCode.E50008);
        }

        String action;
        if (to.getStatus() != null && to.getStatus() == 2) {
            if (to.getUserId() == null) {
                throw error(SeatErrorCode.E50008);
            }
            action = "swap";
            Long fromUser = from.getUserId();
            LocalDateTime fromTime = from.getAssignTime();
            from.setUserId(to.getUserId());
            from.setAssignTime(to.getAssignTime());
            to.setUserId(fromUser);
            to.setAssignTime(fromTime);
            if (!seatInfoService.updateById(from) || !seatInfoService.updateById(to)) {
                throw error(SeatErrorCode.E50005);
            }
        } else {
            action = "move";
            Long movedUser = from.getUserId();
            LocalDateTime movedTime = from.getAssignTime();
            from.setUserId(null);
            from.setAssignTime(null);
            from.setStatus(1);
            to.setUserId(movedUser);
            to.setAssignTime(movedTime == null ? LocalDateTime.now() : movedTime);
            to.setStatus(2);
            if (!seatInfoService.updateById(from) || !seatInfoService.updateById(to)) {
                throw error(SeatErrorCode.E50005);
            }
        }

        SeatInfo savedFrom = seatInfoService.getById(from.getId());
        SeatInfo savedTo = seatInfoService.getById(to.getId());
        Set<Long> userIds = new LinkedHashSet<>();
        if (savedFrom.getUserId() != null) {
            userIds.add(savedFrom.getUserId());
        }
        if (savedTo.getUserId() != null) {
            userIds.add(savedTo.getUserId());
        }
        Map<Long, SysUser> users = loadUsers(userIds);
        SeatPlanDragVO vo = new SeatPlanDragVO();
        vo.setAction(action);
        vo.setFromSeat(toDragSeat(savedFrom, users));
        vo.setToSeat(toDragSeat(savedTo, users));
        return vo;
    }
    @Override
    public PageResult<SeatApplyPageVO> applyPage(SeatApplyPageQuery query) {
        var wrapper = Wrappers.<SeatApply>lambdaQuery()
                .eq(query.getStatus() != null, SeatApply::getStatus, query.getStatus())
                .eq(query.getAreaId() != null, SeatApply::getAreaId, query.getAreaId())
                .eq(query.getUserId() != null, SeatApply::getUserId, query.getUserId())
                .ge(StringUtils.hasText(query.getBeginTime()), SeatApply::getCreateTime,
                        parseDateTime(query.getBeginTime(), false))
                .le(StringUtils.hasText(query.getEndTime()), SeatApply::getCreateTime,
                        parseDateTime(query.getEndTime(), true));
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
                boolean hasCondition = false;
                if (!userIds.isEmpty()) {
                    w.in(SeatApply::getUserId, userIds);
                    hasCondition = true;
                }
                if (!seatIds.isEmpty()) {
                    if (hasCondition) {
                        w.or();
                    }
                    w.in(SeatApply::getSeatId, seatIds);
                }
            });
        }
        wrapper.orderByDesc(SeatApply::getCreateTime).orderByDesc(SeatApply::getId);
        IPage<SeatApply> page = seatApplyService.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        ApplyLookups lookups = loadApplyLookups(page.getRecords());
        return PageResult.of(page, apply -> toApplyVO(apply, lookups));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditApply(Long id, SeatApplyAuditDTO dto, Long operatorUserId) {
        SeatApply apply = id == null ? null : seatApplyService.getById(id);
        if (apply == null) {
            throw error(SeatErrorCode.E50006);
        }
        if (apply.getStatus() == null || apply.getStatus() != 0) {
            throw error(SeatErrorCode.E50008);
        }
        if (dto.getAuditStatus() == null || (dto.getAuditStatus() != 1 && dto.getAuditStatus() != 2)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "auditStatus 仅支持 1 通过或 2 驳回");
        }
        if (dto.getAuditStatus() == 1) {
            Long assignSeatId = dto.getAssignSeatId() != null ? dto.getAssignSeatId() : apply.getSeatId();
            if (assignSeatId == null) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "通过审核时必须指定 assignSeatId");
            }
            assignInternal(requireSeat(assignSeatId), apply.getUserId());
            apply.setAssignSeatId(assignSeatId);
            apply.setStatus(1);
        } else {
            apply.setStatus(2);
        }
        apply.setAuditUserId(operatorUserId);
        apply.setAuditTime(LocalDateTime.now());
        apply.setAuditRemark(validateText(dto.getAuditRemark(), 255, "审核意见"));
        seatApplyService.updateById(apply);
    }

    @Override
    public PageResult<SeatSwapPageVO> swapPage(SeatSwapPageQuery query) {
        return seatSwapService.adminPage(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditSwap(Long id, SeatSwapAuditDTO dto, Long operatorUserId) {
        seatSwapService.audit(id, dto, operatorUserId);
    }

    private Long assignInternal(SeatInfo target, Long userId) {
        SysUser user = userId == null ? null : sysUserService.getById(userId);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "被分配成员不存在或已被禁用");
        }
        if (target.getStatus() != null && (target.getStatus() == 3 || target.getStatus() == 4)) {
            throw error(SeatErrorCode.E50017);
        }
        if (target.getUserId() != null && !target.getUserId().equals(userId)) {
            throw error(SeatErrorCode.E50005);
        }
        if (target.getStatus() != null && target.getStatus() == 2 && target.getUserId() == null) {
            throw error(SeatErrorCode.E50005);
        }
        if (userId.equals(target.getUserId())) {
            return null;
        }
        List<SeatInfo> currentSeats = seatInfoService.list(Wrappers.<SeatInfo>lambdaQuery()
                .eq(SeatInfo::getUserId, userId)
                .ne(SeatInfo::getId, target.getId()));
        Long releasedSeatId = null;
        for (SeatInfo current : currentSeats) {
            current.setUserId(null);
            current.setAssignTime(null);
            current.setStatus(1);
            if (!seatInfoService.updateById(current)) {
                throw error(SeatErrorCode.E50005);
            }
            if (releasedSeatId == null) {
                releasedSeatId = current.getId();
            }
        }
        target.setUserId(userId);
        target.setAssignTime(LocalDateTime.now());
        target.setStatus(2);
        if (!seatInfoService.updateById(target)) {
            throw error(SeatErrorCode.E50005);
        }
        return releasedSeatId;
    }
    private ApplyLookups loadApplyLookups(List<SeatApply> applies) {
        Set<Long> userIds = new LinkedHashSet<>();
        Set<Long> areaIds = new LinkedHashSet<>();
        Set<Long> seatIds = new LinkedHashSet<>();
        for (SeatApply apply : applies) {
            userIds.add(apply.getUserId());
            userIds.add(apply.getAuditUserId());
            areaIds.add(apply.getAreaId());
            seatIds.add(apply.getSeatId());
            seatIds.add(apply.getAssignSeatId());
        }
        userIds.remove(null);
        areaIds.remove(null);
        seatIds.remove(null);
        Map<Long, SysUser> users = loadUsers(userIds);
        Map<Long, SeatArea> areas = areaIds.isEmpty() ? Map.of()
                : seatAreaService.listByIds(areaIds).stream()
                .collect(Collectors.toMap(SeatArea::getId, Function.identity(), (a, b) -> a));
        Map<Long, SeatInfo> seats = seatIds.isEmpty() ? Map.of()
                : seatInfoService.listByIds(seatIds).stream()
                .collect(Collectors.toMap(SeatInfo::getId, Function.identity(), (a, b) -> a));
        Map<Long, SeatInfo> currentSeats = userIds.isEmpty() ? Map.of()
                : seatInfoService.list(Wrappers.<SeatInfo>lambdaQuery()
                        .in(SeatInfo::getUserId, userIds)
                        .eq(SeatInfo::getStatus, 2))
                .stream().collect(Collectors.toMap(SeatInfo::getUserId, Function.identity(), (a, b) -> a));
        return new ApplyLookups(users, areas, seats, currentSeats);
    }

    private SeatApplyPageVO toApplyVO(SeatApply apply, ApplyLookups lookups) {
        SeatApplyPageVO vo = new SeatApplyPageVO();
        vo.setApplyId(apply.getId());
        vo.setUserId(apply.getUserId());
        SysUser user = lookups.users().get(apply.getUserId());
        vo.setUserName(user == null ? null : user.getRealName());
        vo.setGrade(user == null ? null : user.getGrade());
        SeatInfo currentSeat = lookups.currentSeats().get(apply.getUserId());
        vo.setCurrentSeatId(currentSeat == null ? null : currentSeat.getId());
        vo.setCurrentSeatNo(currentSeat == null ? null : currentSeat.getSeatNo());
        vo.setAreaId(apply.getAreaId());
        SeatArea area = lookups.areas().get(apply.getAreaId());
        vo.setAreaName(area == null ? null : area.getName());
        vo.setSeatId(apply.getSeatId());
        SeatInfo seat = lookups.seats().get(apply.getSeatId());
        vo.setSeatNo(seat == null ? null : seat.getSeatNo());
        vo.setSeatStatus(seat == null ? null : seat.getStatus());
        vo.setReason(apply.getReason());
        vo.setStatus(apply.getStatus());
        SysUser auditor = lookups.users().get(apply.getAuditUserId());
        vo.setAuditUserName(auditor == null ? null : auditor.getRealName());
        vo.setAuditTime(format(apply.getAuditTime()));
        vo.setAuditRemark(apply.getAuditRemark());
        vo.setCreateTime(format(apply.getCreateTime()));
        return vo;
    }

    private AdminSeatPlanVO.SeatVO toPlanSeat(SeatInfo seat, Map<Long, SysUser> users) {
        AdminSeatPlanVO.SeatVO vo = new AdminSeatPlanVO.SeatVO();
        vo.setSeatId(seat.getId());
        vo.setSeatNo(seat.getSeatNo());
        vo.setSeatType(seat.getSeatType());
        vo.setXRatio(seat.getXRatio());
        vo.setYRatio(seat.getYRatio());
        vo.setWidthRatio(seat.getWidthRatio());
        vo.setHeightRatio(seat.getHeightRatio());
        vo.setRotation(seat.getRotation());
        vo.setStatus(seat.getStatus());
        vo.setOccupantUserId(seat.getUserId());
        SysUser user = seat.getUserId() == null ? null : users.get(seat.getUserId());
        vo.setOccupantName(user == null ? null : user.getRealName());
        vo.setOccupantGrade(user == null ? null : user.getGrade());
        vo.setOccupantMajor(user == null ? null : user.getMajor());
        vo.setAssignTime(format(seat.getAssignTime()));
        vo.setRemark(seat.getRemark());
        return vo;
    }

    private AdminSeatPlanVO.SeatStatVO stat(List<SeatInfo> seats) {
        AdminSeatPlanVO.SeatStatVO stat = new AdminSeatPlanVO.SeatStatVO();
        stat.setTotal(seats.size());
        stat.setFree((int) seats.stream().filter(seat -> seat.getStatus() != null && seat.getStatus() == 1).count());
        stat.setUsed((int) seats.stream().filter(seat -> seat.getStatus() != null && seat.getStatus() == 2).count());
        stat.setReserved((int) seats.stream().filter(seat -> seat.getStatus() != null && seat.getStatus() == 3).count());
        stat.setRepairing((int) seats.stream().filter(seat -> seat.getStatus() != null && seat.getStatus() == 4).count());
        return stat;
    }

    private SeatPlanDragVO.FromSeatVO toDragSeat(SeatInfo seat, Map<Long, SysUser> users) {
        SeatPlanDragVO.FromSeatVO vo = new SeatPlanDragVO.FromSeatVO();
        vo.setSeatId(seat.getId());
        vo.setSeatNo(seat.getSeatNo());
        vo.setStatus(seat.getStatus());
        vo.setOccupantUserId(seat.getUserId());
        SysUser user = seat.getUserId() == null ? null : users.get(seat.getUserId());
        vo.setOccupantName(user == null ? null : user.getRealName());
        return vo;
    }

    private Map<Long, SysUser> loadUsers(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        return sysUserService.listByIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, Function.identity(), (a, b) -> a));
    }

    private SeatArea requireArea(Long id) {
        SeatArea area = id == null ? null : seatAreaService.getById(id);
        if (area == null) {
            throw error(SeatErrorCode.E50001);
        }
        return area;
    }

    private SeatInfo requireSeat(Long id) {
        SeatInfo seat = id == null ? null : seatInfoService.getById(id);
        if (seat == null) {
            throw error(SeatErrorCode.E50003);
        }
        return seat;
    }

    private String requireText(String value, int maxLength, String label) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, label + "不能为空");
        }
        String text = value.trim();
        if (text.length() > maxLength) {
            throw new BusinessException(ResultCode.PARAM_ERROR, label + "最长 " + maxLength + " 字");
        }
        return text;
    }

    private String validateSeatNo(String value) {
        return requireText(value, 32, "工位编号");
    }

    private String validateText(String value, int maxLength, String label) {
        if (value == null) {
            return null;
        }
        String text = value.trim();
        if (text.length() > maxLength) {
            throw new BusinessException(ResultCode.PARAM_ERROR, label + "最长 " + maxLength + " 字");
        }
        return text.isEmpty() ? null : text;
    }

    private String validateUrl(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String url = value.trim();
        if (url.length() > 512) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "平面图地址最长 512 字");
        }
        return url;
    }

    private Integer validateDimension(Integer value) {
        if (value == null || value <= 0) {
            throw error(SeatErrorCode.E50014);
        }
        return value;
    }

    private Integer validateSortNo(Integer value) {
        int sortNo = value == null ? 0 : value;
        if (sortNo < 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "排序号不能为负数");
        }
        return sortNo;
    }

    private Integer validateSeatType(Integer value) {
        int type = value == null ? 1 : value;
        if (type < 1 || type > 4) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "seatType 仅支持 1~4");
        }
        return type;
    }

    private BigDecimal ratio(BigDecimal value, String label) {
        BigDecimal result = value == null ? BigDecimal.ZERO : value;
        if (result.compareTo(BigDecimal.ZERO) < 0 || result.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, label + "百分比需在 0~100 之间");
        }
        return result.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal rotation(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
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
            return LocalDateTime.parse(text, text.contains("T")
                    ? DateTimeFormatter.ISO_LOCAL_DATE_TIME : DATE_TIME_FMT);
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

    private record ApplyLookups(Map<Long, SysUser> users, Map<Long, SeatArea> areas,
                                Map<Long, SeatInfo> seats, Map<Long, SeatInfo> currentSeats) {
    }
}