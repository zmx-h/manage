package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.duty.dto.DutyPlanAddDTO;
import com.iecmp.duty.dto.DutyPlanGenerateDTO;
import com.iecmp.duty.dto.DutyPlanPublishDTO;
import com.iecmp.duty.dto.DutyPlanUpdateDTO;
import com.iecmp.duty.entity.DutyPlan;
import com.iecmp.duty.entity.DutySchedule;
import com.iecmp.duty.entity.DutySwap;
import com.iecmp.duty.enums.DutyErrorCode;
import com.iecmp.duty.mapper.DutyPlanMapper;
import com.iecmp.duty.mapper.DutyScheduleMapper;
import com.iecmp.duty.mapper.DutySwapMapper;
import com.iecmp.duty.query.DutyPlanPageQuery;
import com.iecmp.duty.service.DutyPlanService;
import com.iecmp.duty.vo.DutyPlanCreateVO;
import com.iecmp.duty.vo.DutyPlanGenerateVO;
import com.iecmp.duty.vo.DutyPlanPageVO;
import com.iecmp.system.service.SysUserService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 排班计划业务实现。
 */
@Service
@RequiredArgsConstructor
public class DutyPlanServiceImpl extends ServiceImpl<DutyPlanMapper, DutyPlan>
        implements DutyPlanService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DutyScheduleMapper dutyScheduleMapper;
    private final DutySwapMapper dutySwapMapper;
    private final SysUserService sysUserService;

    @Override
    public PageResult<DutyPlanPageVO> page(DutyPlanPageQuery query) {
        DutyPlanPageQuery q = query == null ? new DutyPlanPageQuery() : query;
        Page<DutyPlan> page = page(new Page<>(q.getPageNum(), q.getPageSize()),
                Wrappers.<DutyPlan>lambdaQuery()
                        .like(hasText(q.getKeyword()), DutyPlan::getTitle, trim(q.getKeyword()))
                        .eq(q.getPlanType() != null, DutyPlan::getPlanType, q.getPlanType())
                        .eq(q.getStatus() != null, DutyPlan::getStatus, q.getStatus())
                        .orderByDesc(DutyPlan::getCreateTime)
                        .orderByDesc(DutyPlan::getId));

        List<Long> planIds = page.getRecords().stream().map(DutyPlan::getId).toList();
        Map<Long, List<DutySchedule>> schedulesByPlan = planIds.isEmpty() ? Map.of()
                : dutyScheduleMapper.selectList(Wrappers.<DutySchedule>lambdaQuery()
                                .in(DutySchedule::getPlanId, planIds))
                        .stream().collect(Collectors.groupingBy(DutySchedule::getPlanId));
        return PageResult.of(page, plan -> toPageVO(plan, schedulesByPlan.getOrDefault(plan.getId(), List.of())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DutyPlanCreateVO add(DutyPlanAddDTO dto) {
        PlanData data = validate(dto.getTitle(), dto.getPlanType(), dto.getStartDate(), dto.getEndDate(),
                dto.getMemberIds(), groupInputs(dto.getGroupConfig()), dto.getTaskContent(), dto.getRemark());
        DutyPlan plan = new DutyPlan();
        apply(plan, data);
        plan.setStatus(0);
        if (!save(plan)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "新增排班计划失败");
        }
        DutyPlanCreateVO vo = new DutyPlanCreateVO();
        vo.setPlanId(plan.getId());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DutyPlanUpdateDTO dto) {
        DutyPlan plan = dto.getPlanId() == null ? null : getById(dto.getPlanId());
        if (plan == null) {
            throw new BusinessException(DutyErrorCode.E70001.getCode(), DutyErrorCode.E70001.getMsg());
        }
        PlanData data = validate(dto.getTitle(), dto.getPlanType(), dto.getStartDate(), dto.getEndDate(),
                dto.getMemberIds(), groupInputsUpdate(dto.getGroupConfig()), dto.getTaskContent(), dto.getRemark());
        apply(plan, data);
        if (!updateById(plan)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "修改排班计划失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        DutyPlan plan = id == null ? null : getById(id);
        if (plan == null) {
            throw new BusinessException(DutyErrorCode.E70001.getCode(), DutyErrorCode.E70001.getMsg());
        }
        List<DutySchedule> schedules = dutyScheduleMapper.selectList(Wrappers.<DutySchedule>lambdaQuery()
                .eq(DutySchedule::getPlanId, id));
        List<Long> scheduleIds = schedules.stream().map(DutySchedule::getId).toList();
        if (!scheduleIds.isEmpty()) {
            dutySwapMapper.delete(Wrappers.<DutySwap>lambdaQuery()
                    .and(w -> w.in(DutySwap::getScheduleId, scheduleIds)
                            .or().in(DutySwap::getTargetScheduleId, scheduleIds)));
            dutyScheduleMapper.delete(Wrappers.<DutySchedule>lambdaQuery()
                    .eq(DutySchedule::getPlanId, id));
        }
        if (!removeById(id)) {
            throw new BusinessException(DutyErrorCode.E70001.getCode(), DutyErrorCode.E70001.getMsg());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DutyPlanGenerateVO generate(Long id, DutyPlanGenerateDTO dto) {
        DutyPlan plan = id == null ? null : getById(id);
        if (plan == null) {
            throw new BusinessException(DutyErrorCode.E70001.getCode(), DutyErrorCode.E70001.getMsg());
        }
        if (plan.getMemberIds() == null || plan.getMemberIds().isEmpty()) {
            throw new BusinessException(DutyErrorCode.E70004.getCode(), DutyErrorCode.E70004.getMsg());
        }
        int overwrite = dto == null || dto.getOverwrite() == null ? 0 : dto.getOverwrite();
        int skipWeekend = dto == null || dto.getSkipWeekend() == null ? 1 : dto.getSkipWeekend();
        int perDayCount = dto == null || dto.getPerDayCount() == null ? 1 : dto.getPerDayCount();
        if ((overwrite != 0 && overwrite != 1) || (skipWeekend != 0 && skipWeekend != 1)
                || perDayCount < 1 || perDayCount > 20) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "排班生成参数不合法");
        }

        List<DutySchedule> existing = dutyScheduleMapper.selectList(Wrappers.<DutySchedule>lambdaQuery()
                .eq(DutySchedule::getPlanId, id));
        if (overwrite == 1) {
            if (existing.stream().anyMatch(item -> Integer.valueOf(1).equals(item.getStatus()))) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "计划已有已完成值日，不能覆盖重建");
            }
            List<Long> ids = existing.stream().map(DutySchedule::getId).toList();
            if (!ids.isEmpty()) {
                dutySwapMapper.delete(Wrappers.<DutySwap>lambdaQuery()
                        .and(w -> w.in(DutySwap::getScheduleId, ids)
                                .or().in(DutySwap::getTargetScheduleId, ids)));
                dutyScheduleMapper.delete(Wrappers.<DutySchedule>lambdaQuery()
                        .eq(DutySchedule::getPlanId, id));
            }
            existing = List.of();
        }

        Map<LocalDate, Set<Long>> existingByDate = existing.stream()
                .collect(Collectors.groupingBy(DutySchedule::getDutyDate,
                        Collectors.mapping(DutySchedule::getUserId, Collectors.toSet())));
        Map<LocalDate, List<Long>> candidates = buildCandidates(plan, skipWeekend, perDayCount);
        int generated = 0;
        int skipped = 0;
        for (Map.Entry<LocalDate, List<Long>> entry : candidates.entrySet()) {
            Set<Long> existUsers = existingByDate.computeIfAbsent(entry.getKey(), key -> new HashSet<>());
            for (Long userId : entry.getValue()) {
                if (!existUsers.add(userId)) {
                    skipped++;
                    continue;
                }
                DutySchedule schedule = new DutySchedule();
                schedule.setPlanId(plan.getId());
                schedule.setDutyDate(entry.getKey());
                schedule.setUserId(userId);
                schedule.setGroupName(resolveGroupName(plan, userId));
                schedule.setTaskContent(plan.getTaskContent());
                schedule.setStatus(0);
                dutyScheduleMapper.insert(schedule);
                generated++;
            }
        }

        DutyPlanGenerateVO vo = new DutyPlanGenerateVO();
        vo.setPlanId(plan.getId());
        vo.setGeneratedCount(generated);
        vo.setSkippedCount(skipped);
        vo.setDateRange(plan.getStartDate().format(DATE_FMT) + " ~ " + plan.getEndDate().format(DATE_FMT));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id, DutyPlanPublishDTO dto) {
        DutyPlan plan = id == null ? null : getById(id);
        if (plan == null) {
            throw new BusinessException(DutyErrorCode.E70001.getCode(), DutyErrorCode.E70001.getMsg());
        }
        if (dto.getPublish() == null || (dto.getPublish() != 0 && dto.getPublish() != 1)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "发布状态必须为 0 或 1");
        }
        if (dto.getPublish() == 1) {
            if (Integer.valueOf(1).equals(plan.getStatus())) {
                throw new BusinessException(DutyErrorCode.E70002.getCode(), DutyErrorCode.E70002.getMsg());
            }
            long count = dutyScheduleMapper.selectCount(Wrappers.<DutySchedule>lambdaQuery()
                    .eq(DutySchedule::getPlanId, id));
            if (count == 0) {
                throw new BusinessException(DutyErrorCode.E70005.getCode(), DutyErrorCode.E70005.getMsg());
            }
            plan.setStatus(1);
            plan.setPublishTime(LocalDateTime.now());
        } else {
            plan.setStatus(0);
        }
        if (!updateById(plan)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "发布状态修改失败");
        }
    }

    private Map<LocalDate, List<Long>> buildCandidates(DutyPlan plan, int skipWeekend, int perDayCount) {
        Map<LocalDate, List<Long>> result = new LinkedHashMap<>();
        List<Long> members = sanitizeMemberIds(plan.getMemberIds());
        if (members.isEmpty()) {
            throw new BusinessException(DutyErrorCode.E70004.getCode(), DutyErrorCode.E70004.getMsg());
        }
        int eligibleDayIndex = 0;
        for (LocalDate date = plan.getStartDate(); !date.isAfter(plan.getEndDate()); date = date.plusDays(1)) {
            if (skipWeekend == 1 && (date.getDayOfWeek() == DayOfWeek.SATURDAY
                    || date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                continue;
            }
            List<Long> dayMembers = switch (plan.getPlanType()) {
                case 1 -> List.of(members.get((int) (weekIndex(plan.getStartDate(), date) % members.size())));
                case 2 -> groupMembers(plan, eligibleDayIndex, perDayCount);
                case 3 -> List.of();
                default -> throw new BusinessException(ResultCode.PARAM_ERROR, "排班计划轮转方式不合法");
            };
            if (!dayMembers.isEmpty()) {
                result.put(date, dayMembers);
            }
            eligibleDayIndex++;
        }
        return result;
    }

    private List<Long> groupMembers(DutyPlan plan, int dayIndex, int perDayCount) {
        List<DutyPlan.GroupConfig> groups = plan.getGroupConfig();
        if (groups == null || groups.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "分组轮转计划缺少分组配置");
        }
        DutyPlan.GroupConfig group = groups.get(dayIndex % groups.size());
        List<Long> groupMembers = sanitizeMemberIds(group.getMemberIds());
        return groupMembers.stream().limit(perDayCount).toList();
    }

    private String resolveGroupName(DutyPlan plan, Long userId) {
        if (!Integer.valueOf(2).equals(plan.getPlanType()) || plan.getGroupConfig() == null) {
            return null;
        }
        return plan.getGroupConfig().stream()
                .filter(group -> group.getMemberIds() != null && group.getMemberIds().contains(userId))
                .map(DutyPlan.GroupConfig::getName)
                .findFirst().orElse(null);
    }

    private static long weekIndex(LocalDate start, LocalDate date) {
        LocalDate firstMonday = start.with(DayOfWeek.MONDAY);
        LocalDate currentMonday = date.with(DayOfWeek.MONDAY);
        return ChronoUnit.WEEKS.between(firstMonday, currentMonday);
    }

    private PlanData validate(String title, Integer planTypeValue, String startText, String endText,
                              List<Long> memberIds, List<GroupInput> rawGroups,
                              String taskContent, String remark) {
        if (!hasText(title)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "计划标题不能为空");
        }
        if (title.trim().length() > 100) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "计划标题长度不能超过 100 个字符");
        }
        int planType = planTypeValue == null ? 2 : planTypeValue;
        if (planType < 1 || planType > 3) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "轮转方式必须为 1、2 或 3");
        }
        LocalDate start = parseDate(startText, "计划开始日期");
        LocalDate end = parseDate(endText, "计划结束日期");
        if (start == null || end == null || !end.isAfter(start)) {
            throw new BusinessException(DutyErrorCode.E70003.getCode(), DutyErrorCode.E70003.getMsg());
        }
        List<Long> cleanMembers = sanitizeMemberIds(memberIds);
        if (cleanMembers.isEmpty()) {
            throw new BusinessException(DutyErrorCode.E70004.getCode(), DutyErrorCode.E70004.getMsg());
        }
        if (sysUserService.listByIds(cleanMembers).size() != cleanMembers.size()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "参与轮转的成员中存在无效用户");
        }
        if (taskContent != null && taskContent.trim().length() > 500) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "值日内容长度不能超过 500 个字符");
        }
        if (remark != null && remark.trim().length() > 255) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "备注长度不能超过 255 个字符");
        }

        List<DutyPlan.GroupConfig> groups = new ArrayList<>();
        if (planType == 2) {
            if (rawGroups == null || rawGroups.isEmpty()) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "分组轮转计划必须配置分组");
            }
            Set<String> names = new LinkedHashSet<>();
            for (GroupInput input : rawGroups) {
                String groupName = trimToNull(input.name());
                if (groupName == null || groupName.length() > 32) {
                    throw new BusinessException(ResultCode.PARAM_ERROR, "分组名称不能为空且不能超过 32 个字符");
                }
                if (!names.add(groupName)) {
                    throw new BusinessException(ResultCode.PARAM_ERROR, "分组名称不能重复");
                }
                List<Long> groupMembers = sanitizeMemberIds(input.memberIds());
                if (groupMembers.isEmpty()) {
                    throw new BusinessException(ResultCode.PARAM_ERROR, "每个分组至少需要一名成员");
                }
                if (!cleanMembers.containsAll(groupMembers)) {
                    throw new BusinessException(ResultCode.PARAM_ERROR, "分组内成员必须包含在参与轮转成员中");
                }
                DutyPlan.GroupConfig group = new DutyPlan.GroupConfig();
                group.setName(groupName);
                group.setMemberIds(groupMembers);
                groups.add(group);
            }
        }
        return new PlanData(title.trim(), planType, start, end, cleanMembers, groups,
                trimToNull(taskContent), trimToNull(remark));
    }

    private void apply(DutyPlan plan, PlanData data) {
        plan.setTitle(data.title());
        plan.setPlanType(data.planType());
        plan.setStartDate(data.startDate());
        plan.setEndDate(data.endDate());
        plan.setMemberIds(data.memberIds());
        plan.setGroupConfig(data.groupConfig());
        plan.setTaskContent(data.taskContent());
        plan.setRemark(data.remark());
    }

    private DutyPlanPageVO toPageVO(DutyPlan plan, List<DutySchedule> schedules) {
        DutyPlanPageVO vo = new DutyPlanPageVO();
        vo.setPlanId(plan.getId());
        vo.setTitle(plan.getTitle());
        vo.setPlanType(plan.getPlanType());
        vo.setStartDate(plan.getStartDate() == null ? null : plan.getStartDate().format(DATE_FMT));
        vo.setEndDate(plan.getEndDate() == null ? null : plan.getEndDate().format(DATE_FMT));
        vo.setMemberCount(plan.getMemberIds() == null ? 0 : new HashSet<>(plan.getMemberIds()).size());
        vo.setGroupCount(plan.getGroupConfig() == null ? 0 : plan.getGroupConfig().size());
        vo.setScheduleCount(schedules.size());
        vo.setCompletedCount((int) schedules.stream()
                .filter(item -> Integer.valueOf(1).equals(item.getStatus())).count());
        vo.setStatus(plan.getStatus());
        vo.setPublishTime(plan.getPublishTime() == null ? null : plan.getPublishTime().format(DATE_TIME_FMT));
        vo.setRemark(plan.getRemark());
        vo.setCreateTime(plan.getCreateTime() == null ? null : plan.getCreateTime().format(DATE_TIME_FMT));
        return vo;
    }

    private static List<Long> sanitizeMemberIds(List<Long> ids) {
        if (ids == null) {
            return List.of();
        }
        return ids.stream().filter(java.util.Objects::nonNull)
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(LinkedHashSet::new), ArrayList::new));
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

    private static List<GroupInput> groupInputs(List<DutyPlanAddDTO.GroupConfigDTO> groups) {
        if (groups == null) {
            return List.of();
        }
        return groups.stream()
                .map(group -> new GroupInput(group.getName(), group.getMemberIds()))
                .toList();
    }

    private static List<GroupInput> groupInputsUpdate(List<DutyPlanUpdateDTO.GroupConfigDTO> groups) {
        if (groups == null) {
            return List.of();
        }
        return groups.stream()
                .map(group -> new GroupInput(group.getName(), group.getMemberIds()))
                .toList();
    }

    private record GroupInput(String name, List<Long> memberIds) {
    }

    private record PlanData(String title, int planType, LocalDate startDate, LocalDate endDate,
                            List<Long> memberIds, List<DutyPlan.GroupConfig> groupConfig,
                            String taskContent, String remark) {
    }
}
