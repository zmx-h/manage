package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.duty.entity.DutyPlan;
import com.iecmp.duty.mapper.DutyPlanMapper;
import com.iecmp.duty.service.DutyPlanService;
import org.springframework.stereotype.Service;

/**
 * 排班计划业务实现（{@code duty_plan}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link DutyPlanService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class DutyPlanServiceImpl extends ServiceImpl<DutyPlanMapper, DutyPlan>
        implements DutyPlanService {
}
