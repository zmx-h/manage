package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.entity.BudgetPeriod;
import com.iecmp.budget.mapper.BudgetPeriodMapper;
import com.iecmp.budget.service.BudgetPeriodService;
import org.springframework.stereotype.Service;

/**
 * 预算周期业务实现（{@code budget_period}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link BudgetPeriodService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class BudgetPeriodServiceImpl extends ServiceImpl<BudgetPeriodMapper, BudgetPeriod>
        implements BudgetPeriodService {
}
