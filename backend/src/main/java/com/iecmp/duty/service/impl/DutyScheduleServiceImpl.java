package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.duty.dto.DutyScheduleAddDTO;
import com.iecmp.duty.dto.DutyScheduleStatusDTO;
import com.iecmp.duty.dto.DutyScheduleUpdateDTO;
import com.iecmp.duty.entity.DutyPlan;
import com.iecmp.duty.entity.DutySchedule;
import com.iecmp.duty.entity.DutySwap;
import com.iecmp.duty.enums.DutyErrorCode;
import com.iecmp.duty.mapper.DutyScheduleMapper;
import com.iecmp.duty.mapper.DutySwapMapper;
import com.iecmp.duty.query.AdminDutySchedulePageQuery;
import com.iecmp.duty.service.DutyPlanService;
import com.iecmp.duty.service.DutyScheduleService;
import com.iecmp.duty.vo.AdminDutySchedulePageVO;
import com.iecmp.duty.vo.DutyScheduleCreateVO;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.service.SysUserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 值日安排业务实现。
 */
@Service
@RequiredArgsConstructor
public class DutyScheduleServiceImpl extends ServiceImpl<DutyScheduleMapper, DutySchedule>
        implements DutyScheduleService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DutyPlanService dutyPlanService;
    private final DutySwapMapper dutySwapMapper;
    private final SysUserService sysUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DutyScheduleCreateVO add(DutyScheduleAddDTO dto) {
        DutyPlan plan = getPlan(dto.getPlanId());
        LocalDate date = parseDate(dto.getDutyDate(), "值日日期");
        validateDateInPlan(plan, date);
        SysUser user = validateUser(dto.getUserId());
        validateRemark(dto.getRemark());
        if (dto.getTaskContent() != null && dto.getTaskContent().trim().length() > 500) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "值日内容长度不能超过 500 个字符");
        }
        boolean duplicate = count(Wrappers.<DutySchedule>lambdaQuery()
                .eq(DutySchedule::getPlanId, plan.getId())
                .eq(DutySchedule::getDutyDate, date)
                .eq(DutySchedule::getUserId, dto.getUserId())) > 0;
        if (duplicate) {
            throw new BusinessException(DutyErrorCode.E70014.getCode(), DutyErrorCode.E70014.getMsg());
        }

        DutySchedule schedule = new DutySchedule();
        schedule.setPlanId(plan.getId());
        schedule.setDutyDate(date);
        schedule.setUserId(user.getId());
        schedule.setGroupName(hasText(dto.getGroupName())
                ? dto.getGroupName().trim() : resolveGroupName(plan, user.getId()));
        schedule.setTaskContent(hasText(dto.getTaskContent())
                ? dto.getTaskContent().trim() : plan.getTaskContent());
        schedule.setRemark(trimToNull(dto.getRemark()));
        schedule.setStatus(0);
        if (!save(schedule)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "新增值日安排失败");
        }

        DutyScheduleCreateVO vo = new DutyScheduleCreateVO();
        vo.setScheduleId(schedule.getId());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DutyScheduleUpdateDTO dto) {
        DutySchedule schedule = dto.getScheduleId() == null ? null : getById(dto.getScheduleId());
        if (schedule == null) {
            throw new BusinessException(DutyErrorCode.E70006.getCode(), DutyErrorCode.E70006.getMsg());
        }
        DutyPlan plan = getPlan(schedule.getPlanId());
        LocalDate date = parseDate(dto.getDutyDate(), "值日日期");
        validateDateInPlan(plan, date);
        SysUser user = validateUser(dto.getUserId());
        validateRemark(dto.getRemark());
        if (dto.getTaskContent() != null && dto.getTaskContent().trim().length() > 500) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "值日内容长度不能超过 500 个字符");
        }
        boolean duplicate = count(Wrappers.<DutySchedule>lambdaQuery()
                .eq(DutySchedule::getPlanId, plan.getId())
                .eq(DutySchedule::getDutyDate, date)
                .eq(DutySchedule::getUserId, user.getId())
                .ne(DutySchedule::getId, schedule.getId())) > 0;
        if (duplicate) {
            throw new BusinessException(DutyErrorCode.E70014.getCode(), DutyErrorCode.E70014.getMsg());
        }

        schedule.setDutyDate(date);
        schedule.setUserId(user.getId());
        schedule.setGroupName(hasText(dto.getGroupName())
                ? dto.getGroupName().trim() : resolveGroupName(plan, user.getId()));
        if (dto.getTaskContent() != null) {
            schedule.setTaskContent(trimToNull(dto.getTaskContent()));
        }
        if (dto.getRemark() != null) {
            schedule.setRemark(trimToNull(dto.getRemark()));
        }
        if (!updateById(schedule)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "修改值日安排失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        DutySchedule schedule = id == null ? null : getById(id);
        if (schedule == null) {
            throw new BusinessException(DutyErrorCode.E70006.getCode(), DutyErrorCode.E70006.getMsg());
        }
        if (!removeById(id)) {
            throw new BusinessException(DutyErrorCode.E70006.getCode(), DutyErrorCode.E70006.getMsg());
        }
    }

    @Override
    public PageResult<AdminDutySchedulePageVO> page(AdminDutySchedulePageQuery query) {
        AdminDutySchedulePageQuery q = query == null ? new AdminDutySchedulePageQuery() : query;
        LocalDate begin = parseDate(q.getBeginDate(), "开始日期");
        LocalDate end = parseDate(q.getEndDate(), "结束日期");
        if (begin != null && end != null && begin.isAfter(end)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "开始日期不能晚于结束日期");
        }
        List<Long> keywordUserIds = null;
        if (hasText(q.getKeyword())) {
            String keyword = q.getKeyword().trim();
            keywordUserIds = sysUserService.list(Wrappers.<SysUser>lambdaQuery()
                            .select(SysUser::getId)
                            .and(w -> w.like(SysUser::getRealName, keyword)
                                    .or().like(SysUser::getUsername, keyword)))
                    .stream().map(SysUser::getId).toList();
            if (keywordUserIds.isEmpty()) {
                return PageResult.empty(q.getPageNum(), q.getPageSize());
            }
        }

        Page<DutySchedule> page = page(new Page<>(q.getPageNum(), q.getPageSize()),
                Wrappers.<DutySchedule>lambdaQuery()
                        .eq(q.getPlanId() != null, DutySchedule::getPlanId, q.getPlanId())
                        .eq(q.getUserId() != null, DutySchedule::getUserId, q.getUserId())
                        .in(keywordUserIds != null, DutySchedule::getUserId, keywordUserIds)
                        .eq(hasText(q.getGroupName()), DutySchedule::getGroupName, trim(q.getGroupName()))
                        .eq(q.getStatus() != null, DutySchedule::getStatus, q.getStatus())
                        .ge(begin != null, DutySchedule::getDutyDate, begin)
                        .le(end != null, DutySchedule::getDutyDate, end)
                        .orderByDesc(DutySchedule::getDutyDate)
                        .orderByDesc(DutySchedule::getId));

        Set<Long> planIds = page.getRecords().stream().map(DutySchedule::getPlanId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> planTitles = planIds.isEmpty() ? Map.of()
                : dutyPlanService.listByIds(planIds).stream()
                        .collect(Collectors.toMap(DutyPlan::getId, DutyPlan::getTitle, (a, b) -> a));
        Set<Long> userIds = page.getRecords().stream().map(DutySchedule::getUserId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, SysUser> users = userIds.isEmpty() ? Map.of()
                : sysUserService.listByIds(userIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, user -> user, (a, b) -> a));
        List<Long> scheduleIds = page.getRecords().stream().map(DutySchedule::getId).toList();
        Map<Long, Long> swapCounts = scheduleIds.isEmpty() ? Map.of()
                : dutySwapMapper.selectList(Wrappers.<DutySwap>lambdaQuery()
                                .in(DutySwap::getScheduleId, scheduleIds))
                        .stream().collect(Collectors.groupingBy(DutySwap::getScheduleId, Collectors.counting()));
        return PageResult.of(page, schedule -> toAdminPageVO(schedule, planTitles, users, swapCounts));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, DutyScheduleStatusDTO dto) {
        DutySchedule schedule = id == null ? null : getById(id);
        if (schedule == null) {
            throw new BusinessException(DutyErrorCode.E70006.getCode(), DutyErrorCode.E70006.getMsg());
        }
        Integer target = dto.getStatus();
        if (target == null || target < 0 || target > 3) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "目标状态必须为 0、1、2 或 3");
        }
        if (target == 3) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "已换班状态只能由换班流程自动写入");
        }
        validateRemark(dto.getRemark());
        int current = schedule.getStatus() == null ? 0 : schedule.getStatus();
        if (target == 1) {
            if (current == 1) {
                throw new BusinessException(DutyErrorCode.E70008.getCode(), DutyErrorCode.E70008.getMsg());
            }
            if (current != 0 && current != 2) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "当前状态不允许变更为已完成");
            }
            schedule.setStatus(1);
            schedule.setConfirmTime(LocalDateTime.now());
        } else if (target == 2) {
            if (current != 0) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "仅未完成的值日可以登记请假");
            }
            schedule.setStatus(2);
        } else {
            if (current == 3) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "已换班的值日不可手工恢复");
            }
            schedule.setStatus(0);
            schedule.setConfirmTime(null);
        }
        if (dto.getRemark() != null) {
            schedule.setRemark(trimToNull(dto.getRemark()));
        }
        if (!updateById(schedule)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "状态修改失败");
        }
    }

    private DutyPlan getPlan(Long planId) {
        DutyPlan plan = planId == null ? null : dutyPlanService.getById(planId);
        if (plan == null) {
            throw new BusinessException(DutyErrorCode.E70001.getCode(), DutyErrorCode.E70001.getMsg());
        }
        return plan;
    }

    private SysUser validateUser(Long userId) {
        SysUser user = userId == null ? null : sysUserService.getById(userId);
        if (user == null || Integer.valueOf(0).equals(user.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "值日人不存在或已被禁用");
        }
        return user;
    }

    private static void validateDateInPlan(DutyPlan plan, LocalDate date) {
        if (date == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "值日日期不能为空");
        }
        if (plan.getStartDate() != null && date.isBefore(plan.getStartDate())
                || plan.getEndDate() != null && date.isAfter(plan.getEndDate())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "值日日期必须在计划起止日期范围内");
        }
    }

    private String resolveGroupName(DutyPlan plan, Long userId) {
        if (plan.getGroupConfig() == null) {
            return null;
        }
        return plan.getGroupConfig().stream()
                .filter(group -> group.getMemberIds() != null && group.getMemberIds().contains(userId))
                .map(DutyPlan.GroupConfig::getName)
                .findFirst().orElse(null);
    }

    private AdminDutySchedulePageVO toAdminPageVO(DutySchedule schedule, Map<Long, String> planTitles,
                                                   Map<Long, SysUser> users,
                                                   Map<Long, Long> swapCounts) {
        AdminDutySchedulePageVO vo = new AdminDutySchedulePageVO();
        vo.setScheduleId(schedule.getId());
        vo.setPlanId(schedule.getPlanId());
        vo.setPlanTitle(planTitles.get(schedule.getPlanId()));
        vo.setDutyDate(schedule.getDutyDate() == null ? null : schedule.getDutyDate().format(DATE_FMT));
        vo.setUserId(schedule.getUserId());
        SysUser user = users.get(schedule.getUserId());
        vo.setUserName(user == null ? null : user.getRealName());
        vo.setGrade(user == null ? null : user.getGrade());
        vo.setGroupName(schedule.getGroupName());
        vo.setTaskContent(schedule.getTaskContent());
        vo.setStatus(schedule.getStatus());
        vo.setConfirmTime(schedule.getConfirmTime() == null ? null : schedule.getConfirmTime().format(DATE_TIME_FMT));
        vo.setRemark(schedule.getRemark());
        vo.setSwapCount(swapCounts.getOrDefault(schedule.getId(), 0L).intValue());
        return vo;
    }

    private static void validateRemark(String remark) {
        if (remark != null && remark.trim().length() > 255) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "备注长度不能超过 255 个字符");
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

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    private static String trimToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }
}