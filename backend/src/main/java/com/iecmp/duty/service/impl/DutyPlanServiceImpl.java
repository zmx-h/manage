package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.duty.dto.DutyPlanAddDTO;
import com.iecmp.duty.dto.DutyPlanGenerateDTO;
import com.iecmp.duty.dto.DutyPlanPublishDTO;
import com.iecmp.duty.dto.DutyPlanUpdateDTO;
import com.iecmp.duty.entity.DutyPlan;
import com.iecmp.duty.entity.DutySchedule;
import com.iecmp.duty.mapper.DutyPlanMapper;
import com.iecmp.duty.query.DutyPlanPageQuery;
import com.iecmp.duty.service.DutyPlanService;
import com.iecmp.duty.vo.DutyPlanCreateVO;
import com.iecmp.duty.vo.DutyPlanGenerateVO;
import com.iecmp.duty.vo.DutyPlanPageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.iecmp.duty.mapper.DutyScheduleMapper;
import org.springframework.transaction.annotation.Transactional;


/**
 * 排班计划业务实现（{@code duty_plan}）。
 *
 * @author liuyujie
 * @date 2026-10-26
 */
@Service
@RequiredArgsConstructor
public class DutyPlanServiceImpl extends ServiceImpl<DutyPlanMapper, DutyPlan> implements DutyPlanService {
    private final DutyScheduleMapper dutyScheduleMapper;

    @Override
    public PageResult<DutyPlanPageVO> page(DutyPlanPageQuery query) {
        // 1. 创建分页对象 (第几页，每页几条)
        Page<DutyPlan> page = new Page<>(query.getPageNum(), query.getPageSize());

        // 2. 构造查询条件（现在咱们先不加任何筛选条件，查全部）
        LambdaQueryWrapper<DutyPlan> wrapper = new LambdaQueryWrapper<>();

        // 3. 执行查询，把数据库里的数据查出来
        Page<DutyPlan> result = this.page(page, wrapper);

        // 4. 转换结果：把 Entity 转换成前端要的 VO
        return PageResult.of(result, entity -> {
            DutyPlanPageVO vo = new DutyPlanPageVO();

            // 把实体类里的值，塞给 VO
            vo.setPlanId(entity.getId());       // 实体里的 id 对应 VO 里的 planId
            vo.setTitle(entity.getTitle());     // 标题
            vo.setPlanType(entity.getPlanType()); // 轮转方式
            vo.setStatus(entity.getStatus());   // 状态

            // 处理日期：数据库里是 LocalDate 对象，前端需要 String
            if (entity.getStartDate() != null) {
                vo.setStartDate(entity.getStartDate().toString());
            }
            if (entity.getEndDate() != null) {
                vo.setEndDate(entity.getEndDate().toString());
            }

            // 注意：VO里可能还有 memberCount(成员数)、scheduleCount(排班数) 这种统计字段
            // 因为还没做复杂的联表统计，为了防止前端报错，先填个默认值 0
            vo.setMemberCount(0);
            vo.setScheduleCount(0);
            vo.setGroupCount(0);
            vo.setCompletedCount(0);

            return vo;
        });
    }

    @Override
    public DutyPlanCreateVO add(DutyPlanAddDTO dto) {
        DutyPlan plan = new DutyPlan();
        plan.setTitle(dto.getTitle());
        plan.setPlanType(dto.getPlanType());
        plan.setTaskContent(dto.getTaskContent());
        if (dto.getStartDate() != null) {
            plan.setStartDate(LocalDate.parse(dto.getStartDate()));
        }
        if (dto.getEndDate() != null) {
            plan.setEndDate(LocalDate.parse(dto.getEndDate()));
        }
        plan.setMemberIds(dto.getMemberIds());

        plan.setStatus(0);
        this.save(plan);
        DutyPlanCreateVO vo = new DutyPlanCreateVO();
        vo.setPlanId(plan.getId());
        return vo;
    }

    @Override
    public void putDutyPlan(DutyPlanUpdateDTO dto) {
        DutyPlan plan = this.getById(dto.getPlanId());
        if(plan==null){
            throw new BusinessException(70001,"排班计划不存在");
        }
        plan.setTitle(dto.getTitle());
        plan.setPlanType(dto.getPlanType());
        plan.setTaskContent(dto.getTaskContent());
        plan.setMemberIds(dto.getMemberIds());
        if(dto.getStartDate()!=null){
            plan.setStartDate(LocalDate.parse(dto.getStartDate()));
        }
        if(dto.getEndDate()!=null){
            plan.setEndDate(LocalDate.parse(dto.getEndDate()));
        }
        this.updateById(plan);

    }

    @Override
    public void deleteDutyPlan(Long id) {
        DutyPlan plan = this.getById(id);
        if(plan==null){
            throw new BusinessException(70001,"排班计划不存在");
        }
        this.removeById(id);

    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 批量操作必须有事务
    public DutyPlanGenerateVO postDutyPlanGenerate(Long id, DutyPlanGenerateDTO dto) {
        // 1. 查出计划
        DutyPlan plan = this.getById(id);
        if (plan == null) {
            throw new BusinessException(70001, "排班计划不存在");
        }
        List<Long> members = plan.getMemberIds();
        if (members == null || members.isEmpty()) {
            throw new BusinessException(70004, "参与轮转的成员为空，无法生成排班");
        }

        // 2. 如果前端选了“覆盖重建(overwrite=1)”，先把这个计划原有的值日安排全删了
        if (dto.getOverwrite() != null && dto.getOverwrite() == 1) {
            dutyScheduleMapper.delete(new LambdaQueryWrapper<DutySchedule>()
                    .eq(DutySchedule::getPlanId, id));
        }

        // 3. 准备循环参数
        LocalDate currentDate = plan.getStartDate();
        LocalDate endDate = plan.getEndDate();
        List<DutySchedule> scheduleList = new ArrayList<>(); // 装批量数据
        int memberIndex = 0; // 轮转指针
        int skippedCount = 0; // 跳过的天数

        // 4. 开始按天循环
        while (!currentDate.isAfter(endDate)) {
            // 4.1 判断是否跳过周末
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            if (dto.getSkipWeekend() != null && dto.getSkipWeekend() == 1
                    && (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)) {
                skippedCount++;
                currentDate = currentDate.plusDays(1);
                continue; // 跳过今天
            }

            // 4.2 算出今天轮到谁 (取余数保证一直在这个数组里循环)
            Long userId = members.get(memberIndex % members.size());

            // 4.3 组装实体
            DutySchedule schedule = new DutySchedule();
            schedule.setPlanId(id);
            schedule.setUserId(userId);
            schedule.setDutyDate(currentDate);
            schedule.setTaskContent(plan.getTaskContent());
            schedule.setStatus(0); // 未完成
            scheduleList.add(schedule);

            // 4.4 轮到下一个人，日期往后推一天
            memberIndex++;
            currentDate = currentDate.plusDays(1);
        }

        // 5. 批量保存到数据库
        if (!scheduleList.isEmpty()) {
            // 注意：这里用 mapper 里的 insert 逐条插入，或者如果你有 mybatis-plus 的批量方法也可以
            // 最简单稳妥的办法：循环插入，因为在事务里，速度不会太慢
            for (DutySchedule schedule : scheduleList) {
                dutyScheduleMapper.insert(schedule);
            }
        }

        // 6. 组装返回结果
        DutyPlanGenerateVO vo = new DutyPlanGenerateVO();
        vo.setPlanId(id);
        vo.setGeneratedCount(scheduleList.size());
        vo.setSkippedCount(skippedCount);
        vo.setDateRange(plan.getStartDate() + " ~ " + plan.getEndDate());

        return vo;
    }

    @Override
    public void putDutyPlanPublish(Long id, DutyPlanPublishDTO dto) {
        DutyPlan plan=this.getById(id);
        if(plan==null){
            throw new BusinessException(70001,"排班计划不存在");
        }
        if(dto.getPublish()!=null&&dto.getPublish()==1){
    plan.setStatus(1);
    plan.setPublishTime(LocalDateTime.now());
        }else{
    plan.setStatus(0);
        }
        this.updateById(plan);

    }
}
