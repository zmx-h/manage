package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.context.UserContext; // 解决获取真实用户ID
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.duty.dto.*;
import com.iecmp.duty.entity.DutyPlan;
import com.iecmp.duty.entity.DutySchedule;
import com.iecmp.duty.mapper.DutyScheduleMapper;
import com.iecmp.duty.query.AdminDutySchedulePageQuery;
import com.iecmp.duty.query.DutyScheduleCalendarQuery;
import com.iecmp.duty.query.DutyStatOverviewQuery;
import com.iecmp.duty.query.UserDutySchedulePageQuery;
import com.iecmp.duty.service.DutyPlanService;
import com.iecmp.duty.service.DutyScheduleService;
import com.iecmp.duty.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 值日安排业务实现（{@code duty_schedule}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link DutyScheduleService} 中声明后在此实现。
 *
 * @author liuyujie
 * @date 2026-10-27
 */
@Service
@RequiredArgsConstructor
public class DutyScheduleServiceImpl extends ServiceImpl<DutyScheduleMapper, DutySchedule>
        implements DutyScheduleService {

    private final DutyPlanService dutyPlanService;

    @Override
    public DutyScheduleCreateVO add(DutyScheduleAddDTO dto) {
        // 1. 防重复校验
        Long count = this.count(new LambdaQueryWrapper<DutySchedule>()
                .eq(DutySchedule::getUserId, dto.getUserId())
                .eq(DutySchedule::getDutyDate, dto.getDutyDate())
        );
        if (count > 0) {
            throw new BusinessException(70014, "该成员当日已有值日安排");
        }

        // 2. 补全任务内容
        String finalTaskContent = dto.getTaskContent();
        if (finalTaskContent == null || finalTaskContent.isEmpty()) {
            DutyPlan plan = dutyPlanService.getById(dto.getPlanId());
            if (plan != null) {
                finalTaskContent = plan.getTaskContent();
            }
        }


        // 3. 组装数据并保存到数据库
        DutySchedule schedule = new DutySchedule();
        schedule.setPlanId(dto.getPlanId());
        schedule.setUserId(dto.getUserId());

        // 把前端的 String 日期，转换成 LocalDate
        LocalDate realDate = LocalDate.parse(dto.getDutyDate());

        // 把转换好的 realDate 塞进去
        schedule.setDutyDate(realDate);
        schedule.setTaskContent(finalTaskContent);
        schedule.setStatus(0);

        // 千万别忘了这一句！把数据存进数据库
        this.save(schedule);

        // 4. 组装返回结果
        DutyScheduleCreateVO vo = new DutyScheduleCreateVO();
        vo.setScheduleId(schedule.getId());
        return vo;
    }

    @Override
    public void update(DutyScheduleUpdateDTO dto) {
        // 1. 查旧数据（必须有这一步，才能判断存不存在）
        DutySchedule schedule = this.getById(dto.getScheduleId());
        if (schedule == null) {
            throw new BusinessException(70006, "值日安排不存在");
        }

        // 2. 把前端传的新数据塞进去
        schedule.setUserId(dto.getUserId());
        schedule.setTaskContent(dto.getTaskContent());
        // 日期是 String，记得转换
        if (dto.getDutyDate() != null) {
            schedule.setDutyDate(LocalDate.parse(dto.getDutyDate()));
        }
        // 如果 DTO 里还有 status，也塞进去（没有就不管）

        // 3. 核心步骤！把修改后的数据存进数据库！
        this.updateById(schedule);
    }

    @Override
    public void delete(Long id) {
        // 1. 查旧数据（必须有这一步，才能判断存不存在）
        DutySchedule schedule = this.getById(id);
        if (schedule == null) {
            throw new BusinessException(70006, "值日安排不存在");
        }
        this.removeById(id);

    }

    @Override
    public PageResult<AdminDutySchedulePageVO> page(AdminDutySchedulePageQuery query) {
        // 1. 创建分页对象（提示：new Page<>(query.getPageNum(), query.getPageSize())）
        Page<DutySchedule> page = new Page<>(query.getPageNum(), query.getPageSize());

        // 2. 构造查询条件（LambdaQueryWrapper）
        LambdaQueryWrapper<DutySchedule> wrapper = new LambdaQueryWrapper<>();

        // 3. 拼装条件（用 if 判空，如果前端传了，才去查）
        // 比如：按状态查 wrapper.eq(query.getStatus() != null, DutySchedule::getStatus, query.getStatus());
        // 比如：按日期范围查 wrapper.ge(...) 和 wrapper.le(...)
        // 比如：按人名查（人名在前端怎么传的？如果是 userId，就直接 eq；如果是 keyword 模糊查人名，这里有点复杂，需要先查 sys_user 表拿 ID）

        // 4. 执行分页查询
        Page<DutySchedule> result = this.page(page, wrapper);

        // 5. 把 Entity 转成 VO 并返回
        return PageResult.of(result, entity -> {
            AdminDutySchedulePageVO vo = new AdminDutySchedulePageVO();
            vo.setScheduleId(entity.getId());
            vo.setPlanId(entity.getPlanId());
            vo.setDutyDate(entity.getDutyDate() != null ? entity.getDutyDate().toString() : null); // 日期转字符串
            vo.setTaskContent(entity.getTaskContent());
            vo.setStatus(entity.getStatus());
            // ... 其他的如果你看着不报错也可以 set
            return vo;
        });
    }

    @Override
    public void changeStatus(Long id, DutyScheduleStatusDTO dto) {
        DutySchedule schedule = this.getById(id);
        if (schedule == null) {
            throw new BusinessException(70006, "值日安排不存在");
        }
        schedule.setStatus(dto.getStatus());
        // 如果是改为已完成(1)，记录确认时间
        if (dto.getStatus() != null && dto.getStatus() == 1) {
            schedule.setConfirmTime(java.time.LocalDateTime.now());
        }
        this.updateById(schedule);
    }

    // ================= 7-01 已发布计划下的值日安排分页 =================
    @Override
    public PageResult<UserDutySchedulePageVO> pagePublic(UserDutySchedulePageQuery query) {
        Long planId = query.getPlanId();
        String planTitle = null;

        // 1. 查计划
        if (planId == null) {
            // 假设状态字段叫 status，1是已发布，按最新ID倒序
            DutyPlan latest = dutyPlanService.getOne(new LambdaQueryWrapper<DutyPlan>()
                    .eq(DutyPlan::getStatus, 1)
                    .orderByDesc(DutyPlan::getId)
                    .last("LIMIT 1"));
            if (latest == null) {
                return PageResult.empty(query.getPageNum(), query.getPageSize());
            }
            planId = latest.getId();
            planTitle = latest.getTitle();
        } else {
            DutyPlan plan = dutyPlanService.getById(planId);
            if (plan == null || !Integer.valueOf(1).equals(plan.getStatus())) {
                return PageResult.empty(query.getPageNum(), query.getPageSize());
            }
            planTitle = plan.getTitle();
        }

        // 2. 分页条件
        Page<DutySchedule> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<DutySchedule> wrapper = new LambdaQueryWrapper<DutySchedule>()
                .eq(DutySchedule::getPlanId, planId)
                .eq(query.getUserId() != null, DutySchedule::getUserId, query.getUserId())
                .eq(query.getStatus() != null, DutySchedule::getStatus, query.getStatus())
                .orderByAsc(DutySchedule::getDutyDate);

        if (StringUtils.hasText(query.getBeginDate())) {
            wrapper.ge(DutySchedule::getDutyDate, LocalDate.parse(query.getBeginDate()));
        }
        if (StringUtils.hasText(query.getEndDate())) {
            wrapper.le(DutySchedule::getDutyDate, LocalDate.parse(query.getEndDate()));
        }

        Page<DutySchedule> result = this.page(page, wrapper);

        // 3. 转 VO
        final String finalPlanTitle = planTitle;
        // 替换为真实获取当前用户ID
        Long currentUserId = UserContext.getUserIdOrDefault();

        return PageResult.of(result, entity -> {
            UserDutySchedulePageVO vo = new UserDutySchedulePageVO();
            vo.setScheduleId(entity.getId());
            vo.setPlanId(entity.getPlanId());
            vo.setPlanTitle(finalPlanTitle);
            vo.setDutyDate(entity.getDutyDate() != null ? entity.getDutyDate().toString() : null);
            vo.setUserId(entity.getUserId());
            vo.setTaskContent(entity.getTaskContent());
            vo.setStatus(entity.getStatus());
            vo.setConfirmTime(entity.getConfirmTime() != null ? entity.getConfirmTime().toString() : null);
            vo.setIsMine(currentUserId.equals(entity.getUserId()) ? 1 : 0);

            // 用户姓名暂时塞个空字符串（如果 VO 里是 String）
            // vo.setUserName("未知");
            return vo;
        });
    }

    // ================= 7-02 我的值日（含待办） =================
    @Override
    public DutyScheduleMyVO getMyDuty() {
        // 替换为真实获取当前用户ID
        Long userId = UserContext.getUserIdOrDefault();

        // 待办：当前用户负责、状态=待完成、值日日期 >= 今天
        Long todoCount = this.count(new LambdaQueryWrapper<DutySchedule>()
                .eq(DutySchedule::getUserId, userId)
                .eq(DutySchedule::getStatus, 0) // 0=待完成，按实际枚举调整
                .ge(DutySchedule::getDutyDate, LocalDate.now()));

        // 下次值日：同上条件，取最近一条
        DutySchedule next = this.getOne(new LambdaQueryWrapper<DutySchedule>()
                .eq(DutySchedule::getUserId, userId)
                .eq(DutySchedule::getStatus, 0)
                .ge(DutySchedule::getDutyDate, LocalDate.now())
                .orderByAsc(DutySchedule::getDutyDate)
                .last("LIMIT 1"));

        DutyScheduleMyVO vo = new DutyScheduleMyVO();
        // 注意：如果报错，请改成 todoCount.intValue()
        vo.setPendingCount(todoCount.intValue());

        // vo.setNextDuty(...); 按 VO 字段填充，可能还要带分组名、项目名
        // 如果你的 VO 里 nextDuty 报红，说明它不是 DutySchedule 类型，请按 Alt+Enter 查看
        // vo.setNextDuty(next);

        return vo;
    }

    // ================= 7-03 值日日历（月视图） =================
    @Override
    public DutyScheduleCalendarVO getCalendar(DutyScheduleCalendarQuery query) {
        // 计算月份起止：query 里通常是 year + month
        String monthStr = query.getMonth();
        if (!StringUtils.hasText(monthStr)) {
            monthStr = YearMonth.now().toString(); // 默认当前月
        }
        YearMonth ym = YearMonth.parse(monthStr);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<DutySchedule> list = this.list(new LambdaQueryWrapper<DutySchedule>()
                .between(DutySchedule::getDutyDate, start, end)
                .orderByAsc(DutySchedule::getDutyDate));

        // 按日期分组，Map<LocalDate, List<...>>
        Map<LocalDate, List<DutySchedule>> grouped = list.stream()
                .collect(Collectors.groupingBy(DutySchedule::getDutyDate));

        DutyScheduleCalendarVO vo = new DutyScheduleCalendarVO();
        vo.setMonth(monthStr);
        // vo.setDays(...); 按 VO 结构填充，key 建议用 "yyyy-MM-dd"
        // 注意：如果 VO 里 days 类型不是 Map，请注释掉下面这行
        // vo.setDays(grouped);

        return vo;
    }

    // ================= 7-04 值日完成确认 =================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(Long id, DutyScheduleConfirmDTO dto) {
        // 替换为真实获取当前用户ID
        Long userId = UserContext.getUserIdOrDefault();

        DutySchedule schedule = this.getById(id);
        if (schedule == null) {
            throw new BusinessException(70006, "值日安排不存在");
        }
        // 1. 只能确认自己的值日
        if (!userId.equals(schedule.getUserId())) {
            throw new BusinessException(70007, "无权确认他人的值日");
        }
        // 2. 状态必须允许确认（0=待完成）
        if (!Integer.valueOf(0).equals(schedule.getStatus())) {
            throw new BusinessException(70008, "当前状态不可确认");
        }
        // 3. 可选：只允许当天或已到期确认
        // if (schedule.getDutyDate().isAfter(LocalDate.now())) {
        //     throw new BusinessException("未到值日日期");
        // }

        schedule.setStatus(1); // 1=已完成，按实际枚举调整
        schedule.setConfirmTime(LocalDateTime.now());
        // schedule.setConfirmRemark(dto.getRemark()); // 按 DTO 字段
        this.updateById(schedule);

        // 4. 如项目要求写变动流水，在这里同步写入（同一事务）
    }
    // ================= 7-19 管理端值日统计看板 =================
    @Override
    public DutyStatOverviewVO getStatOverview(DutyStatOverviewQuery query) {
        DutyStatOverviewVO vo = new DutyStatOverviewVO();

        // 1. 确定要查询的排班计划
        Long planId = query.getPlanId();
        DutyPlan plan = null;
        if (planId == null) {
            plan = dutyPlanService.getOne(new LambdaQueryWrapper<DutyPlan>()
                    .eq(DutyPlan::getStatus, 1) // 1=已发布
                    .orderByDesc(DutyPlan::getId)
                    .last("LIMIT 1"));
        } else {
            plan = dutyPlanService.getById(planId);
        }

        // 如果没有计划，直接返回空数据，避免报错
        if (plan == null) {
            vo.setTotalCount(0);
            vo.setCompletedCount(0);
            vo.setUnfinishedCount(0);
            vo.setLeaveCount(0);
            vo.setSwappedCount(0);
            vo.setOverdueCount(0);
            vo.setCompletionRate(BigDecimal.ZERO);
            vo.setMemberStats(new ArrayList<>());
            vo.setGroupStats(new ArrayList<>());
            return vo;
        }

        vo.setPlanId(plan.getId());
        vo.setPlanTitle(plan.getTitle());

        // 2. 一次性查出该计划下的所有值日数据
        List<DutySchedule> schedules = this.list(new LambdaQueryWrapper<DutySchedule>()
                .eq(DutySchedule::getPlanId, plan.getId()));

        if (schedules.isEmpty()) {
            vo.setTotalCount(0);
            vo.setCompletedCount(0);
            vo.setUnfinishedCount(0);
            vo.setLeaveCount(0);
            vo.setSwappedCount(0);
            vo.setOverdueCount(0);
            vo.setCompletionRate(BigDecimal.ZERO);
            vo.setMemberStats(new ArrayList<>());
            vo.setGroupStats(new ArrayList<>());
            return vo;
        }

        // 3. 总体数据统计
        int total = schedules.size();
        int completed = (int) schedules.stream().filter(s -> Integer.valueOf(1).equals(s.getStatus())).count();
        int unfinished = (int) schedules.stream().filter(s -> Integer.valueOf(0).equals(s.getStatus())).count();
        int leave = (int) schedules.stream().filter(s -> Integer.valueOf(2).equals(s.getStatus())).count();
        int swapped = (int) schedules.stream().filter(s -> Integer.valueOf(3).equals(s.getStatus())).count();

        LocalDate today = LocalDate.now();
        int overdue = (int) schedules.stream()
                .filter(s -> s.getDutyDate() != null && s.getDutyDate().isBefore(today) && Integer.valueOf(0).equals(s.getStatus()))
                .count();

        vo.setTotalCount(total);
        vo.setCompletedCount(completed);
        vo.setUnfinishedCount(unfinished);
        vo.setLeaveCount(leave);
        vo.setSwappedCount(swapped);
        vo.setOverdueCount(overdue);

        // 计算总完成率
        if (total > 0) {
            BigDecimal rate = BigDecimal.valueOf(completed)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
            vo.setCompletionRate(rate);
        } else {
            vo.setCompletionRate(BigDecimal.ZERO);
        }

        // 4. 成员维度统计
        Map<Long, List<DutySchedule>> userGrouped = schedules.stream()
                .filter(s -> s.getUserId() != null)
                .collect(Collectors.groupingBy(DutySchedule::getUserId));

        List<DutyStatOverviewVO.MemberStatVO> memberStats = new ArrayList<>();
        for (Map.Entry<Long, List<DutySchedule>> entry : userGrouped.entrySet()) {
            Long userId = entry.getKey();
            List<DutySchedule> userSchedules = entry.getValue();

            DutyStatOverviewVO.MemberStatVO memberStat = new DutyStatOverviewVO.MemberStatVO();
            memberStat.setUserId(userId);
            memberStat.setUserName("用户" + userId); // 暂时占位，后续可换成查用户表拿真实姓名
            memberStat.setGroupName(""); // 暂时留空

            int uTotal = userSchedules.size();
            int uCompleted = (int) userSchedules.stream().filter(s -> Integer.valueOf(1).equals(s.getStatus())).count();
            int uOverdue = (int) userSchedules.stream()
                    .filter(s -> s.getDutyDate() != null && s.getDutyDate().isBefore(today) && Integer.valueOf(0).equals(s.getStatus()))
                    .count();

            memberStat.setTotalCount(uTotal);
            memberStat.setCompletedCount(uCompleted);
            memberStat.setOverdueCount(uOverdue);

            if (uTotal > 0) {
                BigDecimal uRate = BigDecimal.valueOf(uCompleted)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(uTotal), 2, RoundingMode.HALF_UP);
                memberStat.setCompletionRate(uRate);
            } else {
                memberStat.setCompletionRate(BigDecimal.ZERO);
            }

            memberStats.add(memberStat);
        }
        vo.setMemberStats(memberStats);

        // 5. 分组维度统计（由于 DutySchedule 没有 groupName 字段，这里直接返回空列表防报错）
        vo.setGroupStats(new ArrayList<>());

        return vo;
    }

}