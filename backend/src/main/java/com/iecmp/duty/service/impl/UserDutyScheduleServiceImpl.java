package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.duty.dto.DutyScheduleConfirmDTO;
import com.iecmp.duty.entity.DutyPlan;
import com.iecmp.duty.entity.DutySchedule;
import com.iecmp.duty.entity.DutySwap;
import com.iecmp.duty.enums.DutyErrorCode;
import com.iecmp.duty.query.DutyScheduleCalendarQuery;
import com.iecmp.duty.query.UserDutySchedulePageQuery;
import com.iecmp.duty.service.DutyPlanService;
import com.iecmp.duty.service.DutyScheduleService;
import com.iecmp.duty.service.DutySwapService;
import com.iecmp.duty.service.UserDutyScheduleService;
import com.iecmp.duty.vo.DutyScheduleCalendarVO;
import com.iecmp.duty.vo.DutyScheduleMyVO;
import com.iecmp.duty.vo.UserDutySchedulePageVO;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.service.SysUserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 成员端值日查看与确认实现。
 */
@Service
@RequiredArgsConstructor
public class UserDutyScheduleServiceImpl implements UserDutyScheduleService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final DutyPlanService dutyPlanService;
    private final DutyScheduleService dutyScheduleService;
    private final DutySwapService dutySwapService;
    private final SysUserService sysUserService;

    @Override
    public PageResult<UserDutySchedulePageVO> page(UserDutySchedulePageQuery query) {
        UserDutySchedulePageQuery q = query == null ? new UserDutySchedulePageQuery() : query;
        DutyPlan plan = resolvePublishedPlan(q.getPlanId());
        if (plan == null) {
            return PageResult.empty(q.getPageNum(), q.getPageSize());
        }
        LocalDate begin = parseDate(q.getBeginDate(), "开始日期");
        LocalDate end = parseDate(q.getEndDate(), "结束日期");
        if (begin != null && end != null && begin.isAfter(end)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "开始日期不能晚于结束日期");
        }

        Page<DutySchedule> page = dutyScheduleService.page(new Page<>(q.getPageNum(), q.getPageSize()),
                Wrappers.<DutySchedule>lambdaQuery()
                        .eq(DutySchedule::getPlanId, plan.getId())
                        .eq(q.getUserId() != null, DutySchedule::getUserId, q.getUserId())
                        .eq(hasText(q.getGroupName()), DutySchedule::getGroupName, trim(q.getGroupName()))
                        .eq(q.getStatus() != null, DutySchedule::getStatus, q.getStatus())
                        .ge(begin != null, DutySchedule::getDutyDate, begin)
                        .le(end != null, DutySchedule::getDutyDate, end)
                        .orderByAsc(DutySchedule::getDutyDate)
                        .orderByAsc(DutySchedule::getId));

        Map<Long, String> userNames = userNames(page.getRecords().stream()
                .map(DutySchedule::getUserId).filter(Objects::nonNull).distinct().toList());
        Long currentUserId = LoginUserUtil.getUserId();
        return PageResult.of(page, schedule -> toPageVO(schedule, plan, userNames, currentUserId));
    }

    @Override
    public DutyScheduleMyVO my() {
        Long currentUserId = LoginUserUtil.getUserId();
        DutyPlan plan = resolvePublishedPlan(null);
        DutyScheduleMyVO vo = new DutyScheduleMyVO();
        vo.setPendingCount(0);
        vo.setCompletedCount(0);
        vo.setSwappedCount(0);
        vo.setPendingSwapCount(Math.toIntExact(dutySwapService.count(
                Wrappers.<DutySwap>lambdaQuery()
                        .eq(DutySwap::getTargetUserId, currentUserId)
                        .eq(DutySwap::getStatus, 0))));
        if (plan == null) {
            return vo;
        }

        List<DutySchedule> schedules = dutyScheduleService.list(Wrappers.<DutySchedule>lambdaQuery()
                .eq(DutySchedule::getPlanId, plan.getId())
                .eq(DutySchedule::getUserId, currentUserId)
                .orderByAsc(DutySchedule::getDutyDate)
                .orderByAsc(DutySchedule::getId));
        LocalDate today = LocalDate.now();
        vo.setPendingCount((int) schedules.stream().filter(item -> Integer.valueOf(0).equals(item.getStatus())).count());
        vo.setCompletedCount((int) schedules.stream().filter(item -> Integer.valueOf(1).equals(item.getStatus())).count());
        vo.setSwappedCount((int) schedules.stream().filter(item -> Integer.valueOf(3).equals(item.getStatus())).count());
        vo.setTodayDuty(schedules.stream()
                .filter(item -> today.equals(item.getDutyDate()))
                .findFirst().map(this::toNextDutyVO).orElse(null));
        vo.setNextDuty(schedules.stream()
                .filter(item -> Integer.valueOf(0).equals(item.getStatus()))
                .filter(item -> item.getDutyDate() != null && item.getDutyDate().isAfter(today))
                .findFirst().map(this::toNextDutyVO).orElse(null));
        return vo;
    }

    @Override
    public DutyScheduleCalendarVO calendar(DutyScheduleCalendarQuery query) {
        DutyScheduleCalendarQuery q = query == null ? new DutyScheduleCalendarQuery() : query;
        YearMonth month = parseMonth(q.getMonth());
        DutyPlan plan = resolvePublishedPlan(q.getPlanId());
        Long currentUserId = LoginUserUtil.getUserId();

        LocalDate begin = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        List<DutySchedule> schedules = plan == null ? List.of()
                : dutyScheduleService.list(Wrappers.<DutySchedule>lambdaQuery()
                        .eq(DutySchedule::getPlanId, plan.getId())
                        .ge(DutySchedule::getDutyDate, begin)
                        .le(DutySchedule::getDutyDate, end)
                        .orderByAsc(DutySchedule::getDutyDate)
                        .orderByAsc(DutySchedule::getId));

        Map<Long, String> userNames = userNames(schedules.stream()
                .map(DutySchedule::getUserId).filter(Objects::nonNull).distinct().toList());
        Map<LocalDate, List<DutyScheduleCalendarVO.DutyVO>> byDate = new LinkedHashMap<>();
        for (DutySchedule schedule : schedules) {
            byDate.computeIfAbsent(schedule.getDutyDate(), key -> new ArrayList<>())
                    .add(toCalendarDutyVO(schedule, userNames));
        }

        List<DutyScheduleCalendarVO.DayVO> days = new ArrayList<>();
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            List<DutyScheduleCalendarVO.DutyVO> duties = byDate.getOrDefault(date, List.of());
            DutyScheduleCalendarVO.DayVO day = new DutyScheduleCalendarVO.DayVO();
            day.setDate(date.format(DATE_FMT));
            day.setWeekday(date.getDayOfWeek().getValue());
            day.setIsMine(duties.stream().anyMatch(item -> Objects.equals(item.getUserId(), currentUserId)) ? 1 : 0);
            day.setDuties(duties);
            days.add(day);
        }

        DutyScheduleCalendarVO vo = new DutyScheduleCalendarVO();
        vo.setMonth(month.format(MONTH_FMT));
        vo.setPlanId(plan == null ? null : plan.getId());
        vo.setPlanTitle(plan == null ? null : plan.getTitle());
        vo.setDays(days);
        return vo;
    }

    @Override
    public void confirm(Long id, DutyScheduleConfirmDTO dto) {
        DutySchedule schedule = id == null ? null : dutyScheduleService.getById(id);
        if (schedule == null) {
            throw new BusinessException(DutyErrorCode.E70006.getCode(), DutyErrorCode.E70006.getMsg());
        }
        if (!Objects.equals(schedule.getUserId(), LoginUserUtil.getUserId())) {
            throw new BusinessException(DutyErrorCode.E70007.getCode(), DutyErrorCode.E70007.getMsg());
        }
        if (Integer.valueOf(1).equals(schedule.getStatus())) {
            throw new BusinessException(DutyErrorCode.E70008.getCode(), DutyErrorCode.E70008.getMsg());
        }
        if (!Integer.valueOf(0).equals(schedule.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "已换班或请假的值日不可确认");
        }
        if (dto != null && dto.getRemark() != null && dto.getRemark().trim().length() > 255) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "备注长度不能超过 255 个字符");
        }
        schedule.setStatus(1);
        schedule.setConfirmTime(LocalDateTime.now());
        if (dto != null && hasText(dto.getRemark())) {
            schedule.setRemark(dto.getRemark().trim());
        }
        if (!dutyScheduleService.updateById(schedule)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "确认失败，请稍后重试");
        }
    }

    private DutyPlan resolvePublishedPlan(Long planId) {
        if (planId != null) {
            DutyPlan plan = dutyPlanService.getById(planId);
            return plan != null && Integer.valueOf(1).equals(plan.getStatus()) ? plan : null;
        }
        return dutyPlanService.getOne(Wrappers.<DutyPlan>lambdaQuery()
                .eq(DutyPlan::getStatus, 1)
                .orderByDesc(DutyPlan::getPublishTime)
                .orderByDesc(DutyPlan::getId)
                .last("LIMIT 1"));
    }

    private UserDutySchedulePageVO toPageVO(DutySchedule schedule, DutyPlan plan,
                                              Map<Long, String> userNames, Long currentUserId) {
        UserDutySchedulePageVO vo = new UserDutySchedulePageVO();
        vo.setScheduleId(schedule.getId());
        vo.setPlanId(schedule.getPlanId());
        vo.setPlanTitle(plan.getTitle());
        vo.setDutyDate(schedule.getDutyDate() == null ? null : schedule.getDutyDate().format(DATE_FMT));
        vo.setUserId(schedule.getUserId());
        vo.setUserName(userNames.get(schedule.getUserId()));
        vo.setGroupName(schedule.getGroupName());
        vo.setTaskContent(schedule.getTaskContent());
        vo.setStatus(schedule.getStatus());
        vo.setConfirmTime(formatDateTime(schedule.getConfirmTime()));
        vo.setIsMine(Objects.equals(schedule.getUserId(), currentUserId) ? 1 : 0);
        return vo;
    }

    private DutyScheduleMyVO.NextDutyVO toNextDutyVO(DutySchedule schedule) {
        DutyScheduleMyVO.NextDutyVO vo = new DutyScheduleMyVO.NextDutyVO();
        vo.setScheduleId(schedule.getId());
        vo.setDutyDate(schedule.getDutyDate() == null ? null : schedule.getDutyDate().format(DATE_FMT));
        vo.setGroupName(schedule.getGroupName());
        vo.setTaskContent(schedule.getTaskContent());
        vo.setStatus(schedule.getStatus());
        return vo;
    }

    private DutyScheduleCalendarVO.DutyVO toCalendarDutyVO(DutySchedule schedule,
                                                            Map<Long, String> userNames) {
        DutyScheduleCalendarVO.DutyVO vo = new DutyScheduleCalendarVO.DutyVO();
        vo.setScheduleId(schedule.getId());
        vo.setUserId(schedule.getUserId());
        vo.setUserName(userNames.get(schedule.getUserId()));
        vo.setGroupName(schedule.getGroupName());
        vo.setTaskContent(schedule.getTaskContent());
        vo.setStatus(schedule.getStatus());
        return vo;
    }

    private Map<Long, String> userNames(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return sysUserService.listByIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName, (a, b) -> a));
    }

    private static YearMonth parseMonth(String value) {
        if (!hasText(value)) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(value.trim(), MONTH_FMT);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "月份格式不正确，应为 yyyy-MM");
        }
    }

    private static LocalDate parseDate(String value, String label) {
        if (!hasText(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR, label + "格式不正确，应为 yyyy-MM-dd");
        }
    }

    private static String formatDateTime(LocalDateTime time) {
        return time == null ? null : time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}