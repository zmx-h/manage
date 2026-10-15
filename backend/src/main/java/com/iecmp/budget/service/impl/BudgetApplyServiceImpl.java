package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.entity.BudgetApply;
import com.iecmp.budget.mapper.BudgetApplyMapper;
import com.iecmp.budget.service.BudgetApplyService;
import org.springframework.stereotype.Service;

/**
 * 预算申请业务实现（{@code budget_apply}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link BudgetApplyService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class BudgetApplyServiceImpl extends ServiceImpl<BudgetApplyMapper, BudgetApply>
        implements BudgetApplyService {
}
