package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.duty.entity.DutyPlan;
import com.iecmp.duty.entity.DutySchedule;
import com.iecmp.duty.enums.DutyErrorCode;
import com.iecmp.duty.query.DutyStatOverviewQuery;
import com.iecmp.duty.service.DutyPlanService;
import com.iecmp.duty.service.DutyScheduleService;
import com.iecmp.duty.service.DutyStatService;
import com.iecmp.duty.vo.DutyStatOverviewVO;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.service.SysUserService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 值日统计看板实现。
 */
@Service
@RequiredArgsConstructor
public class DutyStatServiceImpl implements DutyStatService {

    private final DutyPlanService dutyPlanService;
    private final DutyScheduleService dutyScheduleService;
    private final SysUserService sysUserService;

    @Override
    public DutyStatOverviewVO overview(DutyStatOverviewQuery query) {
        Long planId = query == null ? null : query.getPlanId();
        DutyPlan plan = resolvePlan(planId);
        DutyStatOverviewVO vo = new DutyStatOverviewVO();
        vo.setMemberStats(List.of());
        vo.setGroupStats(List.of());
        vo.setTotalCount(0);
        vo.setCompletedCount(0);
        vo.setUnfinishedCount(0);
        vo.setLeaveCount(0);
        vo.setSwappedCount(0);
        vo.setOverdueCount(0);
        vo.setCompletionRate(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        if (plan == null) {
            return vo;
        }

        List<DutySchedule> schedules = dutyScheduleService.list(Wrappers.<DutySchedule>lambdaQuery()
                .eq(DutySchedule::getPlanId, plan.getId())
                .orderByAsc(DutySchedule::getDutyDate)
                .orderByAsc(DutySchedule::getId));
        LocalDate today = LocalDate.now();
        int completed = (int) schedules.stream()
                .filter(item -> Integer.valueOf(1).equals(item.getStatus())).count();
        int unfinished = (int) schedules.stream()
                .filter(item -> Integer.valueOf(0).equals(item.getStatus())).count();
        int leave = (int) schedules.stream()
                .filter(item -> Integer.valueOf(2).equals(item.getStatus())).count();
        int swapped = (int) schedules.stream()
                .filter(item -> Integer.valueOf(3).equals(item.getStatus())).count();
        int overdue = (int) schedules.stream()
                .filter(item -> Integer.valueOf(0).equals(item.getStatus()))
                .filter(item -> item.getDutyDate() != null && item.getDutyDate().isBefore(today))
                .count();

        vo.setPlanId(plan.getId());
        vo.setPlanTitle(plan.getTitle());
        vo.setTotalCount(schedules.size());
        vo.setCompletedCount(completed);
        vo.setUnfinishedCount(unfinished);
        vo.setLeaveCount(leave);
        vo.setSwappedCount(swapped);
        vo.setOverdueCount(overdue);
        vo.setCompletionRate(rate(completed, schedules.size()));

        Map<Long, MemberCounter> memberCounters = new LinkedHashMap<>();
        Map<String, GroupCounter> groupCounters = new LinkedHashMap<>();
        for (DutySchedule schedule : schedules) {
            MemberCounter member = memberCounters.computeIfAbsent(schedule.getUserId(),
                    key -> new MemberCounter());
            member.groupName = hasText(schedule.getGroupName()) ? schedule.getGroupName() : member.groupName;
            member.total++;
            if (Integer.valueOf(1).equals(schedule.getStatus())) {
                member.completed++;
            }
            if (Integer.valueOf(0).equals(schedule.getStatus())
                    && schedule.getDutyDate() != null && schedule.getDutyDate().isBefore(today)) {
                member.overdue++;
            }

            String groupName = hasText(schedule.getGroupName()) ? schedule.getGroupName() : "未分组";
            GroupCounter group = groupCounters.computeIfAbsent(groupName, key -> new GroupCounter());
            group.total++;
            if (Integer.valueOf(1).equals(schedule.getStatus())) {
                group.completed++;
            }
        }

        Map<Long, String> userNames = userNames(memberCounters.keySet().stream().toList());
        List<DutyStatOverviewVO.MemberStatVO> memberStats = new ArrayList<>();
        memberCounters.forEach((userId, counter) -> {
            DutyStatOverviewVO.MemberStatVO item = new DutyStatOverviewVO.MemberStatVO();
            item.setUserId(userId);
            item.setUserName(userNames.get(userId));
            item.setGroupName(counter.groupName);
            item.setTotalCount(counter.total);
            item.setCompletedCount(counter.completed);
            item.setOverdueCount(counter.overdue);
            item.setCompletionRate(rate(counter.completed, counter.total));
            memberStats.add(item);
        });
        memberStats.sort(Comparator.comparing(DutyStatOverviewVO.MemberStatVO::getTotalCount).reversed()
                .thenComparing(DutyStatOverviewVO.MemberStatVO::getUserId));
        vo.setMemberStats(memberStats);

        List<DutyStatOverviewVO.GroupStatVO> groupStats = new ArrayList<>();
        groupCounters.forEach((groupName, counter) -> {
            DutyStatOverviewVO.GroupStatVO item = new DutyStatOverviewVO.GroupStatVO();
            item.setGroupName(groupName);
            item.setTotalCount(counter.total);
            item.setCompletedCount(counter.completed);
            item.setCompletionRate(rate(counter.completed, counter.total));
            groupStats.add(item);
        });
        groupStats.sort(Comparator.comparing(DutyStatOverviewVO.GroupStatVO::getGroupName));
        vo.setGroupStats(groupStats);
        return vo;
    }

    private DutyPlan resolvePlan(Long planId) {
        if (planId != null) {
            DutyPlan plan = dutyPlanService.getById(planId);
            if (plan == null) {
                throw new BusinessException(DutyErrorCode.E70001.getCode(), DutyErrorCode.E70001.getMsg());
            }
            return plan;
        }
        return dutyPlanService.getOne(Wrappers.<DutyPlan>lambdaQuery()
                .eq(DutyPlan::getStatus, 1)
                .orderByDesc(DutyPlan::getPublishTime)
                .orderByDesc(DutyPlan::getId)
                .last("LIMIT 1"));
    }

    private Map<Long, String> userNames(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return sysUserService.listByIds(userIds).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName, (a, b) -> a));
    }

    private static BigDecimal rate(int completed, int total) {
        if (total == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(completed * 100.0 / total)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static final class MemberCounter {
        private String groupName;
        private int total;
        private int completed;
        private int overdue;
    }

    private static final class GroupCounter {
        private int total;
        private int completed;
    }
}