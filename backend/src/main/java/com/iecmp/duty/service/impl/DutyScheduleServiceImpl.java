package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.duty.dto.DutyScheduleAddDTO;
import com.iecmp.duty.entity.DutyPlan;
import com.iecmp.duty.entity.DutySchedule;
import com.iecmp.duty.mapper.DutyScheduleMapper;
import com.iecmp.duty.service.DutyPlanService;
import com.iecmp.duty.service.DutyScheduleService;
import com.iecmp.duty.vo.DutyScheduleCreateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 值日安排业务实现（{@code duty_schedule}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link DutyScheduleService} 中声明后在此实现。
 *
 * @author liuyujie
 * @date 2026-10-21
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
}
