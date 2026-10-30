package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.duty.dto.DutySwapDTO;
import com.iecmp.duty.dto.DutySwapHandleDTO;
import com.iecmp.duty.entity.DutySchedule;
import com.iecmp.duty.entity.DutySwap;
import com.iecmp.duty.enums.DutyErrorCode;
import com.iecmp.duty.mapper.DutySwapMapper;
import com.iecmp.duty.query.DutySwapMyPageQuery;
import com.iecmp.duty.service.DutyScheduleService;
import com.iecmp.duty.service.DutySwapService;
import com.iecmp.duty.vo.DutySwapMyPageVO;
import com.iecmp.duty.vo.DutySwapVO;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.service.SysUserService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 换班申请业务实现。
 */
@Service
@RequiredArgsConstructor
public class DutySwapServiceImpl extends ServiceImpl<DutySwapMapper, DutySwap>
        implements DutySwapService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DutyScheduleService dutyScheduleService;
    private final SysUserService sysUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DutySwapVO create(DutySwapDTO dto) {
        Long currentUserId = LoginUserUtil.getUserId();
        DutySchedule source = dto.getScheduleId() == null ? null
                : dutyScheduleService.getById(dto.getScheduleId());
        if (source == null) {
            throw new BusinessException(DutyErrorCode.E70006.getCode(), DutyErrorCode.E70006.getMsg());
        }
        if (!Objects.equals(source.getUserId(), currentUserId)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "只能针对自己的值日发起换班");
        }
        if (!Integer.valueOf(0).equals(source.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "仅未完成的值日可以发起换班");
        }
        if (dto.getTargetUserId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "换班对象不能为空");
        }
        if (Objects.equals(dto.getTargetUserId(), currentUserId)) {
            throw new BusinessException(DutyErrorCode.E70010.getCode(), DutyErrorCode.E70010.getMsg());
        }
        SysUser targetUser = sysUserService.getById(dto.getTargetUserId());
        if (targetUser == null || Integer.valueOf(0).equals(targetUser.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "换班对象不存在或已被禁用");
        }
        if (dto.getReason() != null && dto.getReason().trim().length() > 500) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "换班理由长度不能超过 500 个字符");
        }

        DutySchedule target = null;
        if (dto.getTargetScheduleId() != null) {
            target = dutyScheduleService.getById(dto.getTargetScheduleId());
            if (target == null
                    || !Objects.equals(target.getUserId(), dto.getTargetUserId())
                    || !Integer.valueOf(0).equals(target.getStatus())) {
                throw new BusinessException(DutyErrorCode.E70011.getCode(), DutyErrorCode.E70011.getMsg());
            }
            if (Objects.equals(target.getId(), source.getId())) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "不能与自己当前值日对调");
            }
            if (Objects.equals(target.getDutyDate(), source.getDutyDate())) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "对调的两条值日不能是同一天");
            }
        }

        DutySwap swap = new DutySwap();
        swap.setScheduleId(source.getId());
        swap.setApplyUserId(currentUserId);
        swap.setTargetUserId(dto.getTargetUserId());
        swap.setTargetScheduleId(target == null ? null : target.getId());
        swap.setReason(dto.getReason() == null ? null : dto.getReason().trim());
        swap.setStatus(0);
        if (!save(swap)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "发起换班失败，请稍后重试");
        }

        DutySwapVO vo = new DutySwapVO();
        vo.setSwapId(swap.getId());
        return vo;
    }

    @Override
    public PageResult<DutySwapMyPageVO> myPage(DutySwapMyPageQuery query) {
        DutySwapMyPageQuery q = query == null ? new DutySwapMyPageQuery() : query;
        int role = q.getRole() == null ? 0 : q.getRole();
        if (role < 0 || role > 2) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "查询视角必须为 0、1 或 2");
        }
        Long currentUserId = LoginUserUtil.getUserId();
        var wrapper = Wrappers.<DutySwap>lambdaQuery()
                .eq(q.getStatus() != null, DutySwap::getStatus, q.getStatus());
        if (role == 1) {
            wrapper.eq(DutySwap::getApplyUserId, currentUserId);
        } else if (role == 2) {
            wrapper.eq(DutySwap::getTargetUserId, currentUserId);
        } else {
            wrapper.and(w -> w.eq(DutySwap::getApplyUserId, currentUserId)
                    .or().eq(DutySwap::getTargetUserId, currentUserId));
        }
        wrapper.orderByDesc(DutySwap::getCreateTime).orderByDesc(DutySwap::getId);

        Page<DutySwap> page = page(new Page<>(q.getPageNum(), q.getPageSize()), wrapper);
        Set<Long> scheduleIds = page.getRecords().stream()
                .flatMap(item -> Stream.of(item.getScheduleId(), item.getTargetScheduleId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, DutySchedule> schedules = scheduleIds.isEmpty() ? Map.of()
                : dutyScheduleService.listByIds(scheduleIds).stream()
                        .collect(Collectors.toMap(DutySchedule::getId, item -> item, (a, b) -> a));
        Set<Long> userIds = page.getRecords().stream()
                .flatMap(item -> Stream.of(item.getApplyUserId(), item.getTargetUserId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> userNames = userNames(userIds.stream().toList());
        return PageResult.of(page, swap -> toMyPageVO(swap, schedules, userNames, currentUserId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(Long id, DutySwapHandleDTO dto) {
        DutySwap swap = id == null ? null : getById(id);
        if (swap == null) {
            throw new BusinessException(DutyErrorCode.E70009.getCode(), DutyErrorCode.E70009.getMsg());
        }
        if (!Objects.equals(swap.getTargetUserId(), LoginUserUtil.getUserId())) {
            throw new BusinessException(DutyErrorCode.E70012.getCode(), DutyErrorCode.E70012.getMsg());
        }
        if (!Integer.valueOf(0).equals(swap.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该换班申请已处理");
        }
        if (dto.getHandleStatus() == null || (dto.getHandleStatus() != 1 && dto.getHandleStatus() != 2)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "处理结果必须为 1 同意或 2 拒绝");
        }
        if (dto.getHandleRemark() != null && dto.getHandleRemark().trim().length() > 255) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "处理意见长度不能超过 255 个字符");
        }

        if (dto.getHandleStatus() == 2) {
            swap.setStatus(2);
            swap.setHandleTime(LocalDateTime.now());
            swap.setHandleRemark(trimToNull(dto.getHandleRemark()));
            updateById(swap);
            return;
        }

        DutySchedule source = dutyScheduleService.getById(swap.getScheduleId());
        if (source == null || !Integer.valueOf(0).equals(source.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "原值日已变更，无法换班");
        }
        if (swap.getTargetScheduleId() == null) {
            acceptOneWay(swap, source);
        } else {
            acceptTwoWay(swap, source);
        }
        swap.setStatus(1);
        swap.setHandleTime(LocalDateTime.now());
        swap.setHandleRemark(trimToNull(dto.getHandleRemark()));
        if (!updateById(swap)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "换班处理失败，请稍后重试");
        }
    }

    private void acceptOneWay(DutySwap swap, DutySchedule source) {
        boolean exists = dutyScheduleService.count(Wrappers.<DutySchedule>lambdaQuery()
                .eq(DutySchedule::getPlanId, source.getPlanId())
                .eq(DutySchedule::getDutyDate, source.getDutyDate())
                .eq(DutySchedule::getUserId, swap.getTargetUserId())) > 0;
        if (exists) {
            throw new BusinessException(DutyErrorCode.E70014.getCode(), DutyErrorCode.E70014.getMsg());
        }
        source.setStatus(3);
        if (!dutyScheduleService.updateById(source)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "换班失败，请稍后重试");
        }
        DutySchedule substitute = new DutySchedule();
        substitute.setPlanId(source.getPlanId());
        substitute.setDutyDate(source.getDutyDate());
        substitute.setUserId(swap.getTargetUserId());
        substitute.setGroupName(source.getGroupName());
        substitute.setTaskContent(source.getTaskContent());
        substitute.setStatus(0);
        substitute.setRemark("换班代值，来源值日 ID：" + source.getId());
        if (!dutyScheduleService.save(substitute)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "生成代值安排失败，请稍后重试");
        }
    }

    private void acceptTwoWay(DutySwap swap, DutySchedule source) {
        DutySchedule target = dutyScheduleService.getById(swap.getTargetScheduleId());
        if (target == null
                || !Objects.equals(target.getUserId(), swap.getTargetUserId())
                || !Integer.valueOf(0).equals(target.getStatus())) {
            throw new BusinessException(DutyErrorCode.E70011.getCode(), DutyErrorCode.E70011.getMsg());
        }
        if (Objects.equals(target.getDutyDate(), source.getDutyDate())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "对调的两条值日不能是同一天");
        }

        Long sourceUserId = source.getUserId();
        Long targetUserId = target.getUserId();
        target.setUserId(sourceUserId);
        target.setStatus(0);
        source.setUserId(targetUserId);
        source.setStatus(3);
        if (!dutyScheduleService.updateById(target)
                || !dutyScheduleService.updateById(source)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "换班失败，请稍后重试");
        }
    }

    private DutySwapMyPageVO toMyPageVO(DutySwap swap, Map<Long, DutySchedule> schedules,
                                        Map<Long, String> userNames, Long currentUserId) {
        DutySwapMyPageVO vo = new DutySwapMyPageVO();
        vo.setSwapId(swap.getId());
        vo.setScheduleId(swap.getScheduleId());
        vo.setApplyUserId(swap.getApplyUserId());
        vo.setApplyUserName(userNames.get(swap.getApplyUserId()));
        DutySchedule source = schedules.get(swap.getScheduleId());
        vo.setApplyDutyDate(source == null || source.getDutyDate() == null
                ? null : source.getDutyDate().format(DATE_FMT));
        vo.setTargetUserId(swap.getTargetUserId());
        vo.setTargetUserName(userNames.get(swap.getTargetUserId()));
        vo.setTargetScheduleId(swap.getTargetScheduleId());
        DutySchedule target = swap.getTargetScheduleId() == null ? null : schedules.get(swap.getTargetScheduleId());
        vo.setTargetDutyDate(target == null || target.getDutyDate() == null
                ? null : target.getDutyDate().format(DATE_FMT));
        vo.setReason(swap.getReason());
        vo.setStatus(swap.getStatus());
        vo.setIsTarget(Objects.equals(swap.getTargetUserId(), currentUserId) ? 1 : 0);
        vo.setHandleTime(swap.getHandleTime() == null ? null : swap.getHandleTime().format(DATE_TIME_FMT));
        vo.setHandleRemark(swap.getHandleRemark());
        vo.setCreateTime(swap.getCreateTime() == null ? null : swap.getCreateTime().format(DATE_TIME_FMT));
        return vo;
    }

    private Map<Long, String> userNames(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return sysUserService.listByIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName, (a, b) -> a));
    }

    private static String trimToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}