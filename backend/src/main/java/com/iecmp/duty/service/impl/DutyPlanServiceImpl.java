package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.result.PageResult;
import com.iecmp.duty.dto.DutyPlanAddDTO;
import com.iecmp.duty.entity.DutyPlan;
import com.iecmp.duty.mapper.DutyPlanMapper;
import com.iecmp.duty.query.DutyPlanPageQuery;
import com.iecmp.duty.service.DutyPlanService;
import com.iecmp.duty.vo.DutyPlanCreateVO;
import com.iecmp.duty.vo.DutyPlanPageVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 排班计划业务实现（{@code duty_plan}）。
 *
 * @author liuyujie
 * @date 2026-10-22
 */
@Service
public class DutyPlanServiceImpl extends ServiceImpl<DutyPlanMapper, DutyPlan> implements DutyPlanService {

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
}
