package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.entity.BudgetExpense;
import com.iecmp.budget.mapper.BudgetExpenseMapper;
import com.iecmp.budget.service.BudgetExpenseService;
import org.springframework.stereotype.Service;

/**
 * 支出记录业务实现（{@code budget_expense}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link BudgetExpenseService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class BudgetExpenseServiceImpl extends ServiceImpl<BudgetExpenseMapper, BudgetExpense>
        implements BudgetExpenseService {
}
