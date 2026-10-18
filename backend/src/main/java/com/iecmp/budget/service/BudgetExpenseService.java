package com.iecmp.budget.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.budget.entity.BudgetExpense;
import com.iecmp.budget.vo.UserBudgetExpensePageVO;
import com.iecmp.common.result.PageResult;

/**
 * 支出记录业务接口（{@code budget_expense}）。
 *
 * @author IECMP 后端组
 */
public interface BudgetExpenseService extends IService<BudgetExpense> {

    /** 3-04 支出明细公示 */
    PageResult<UserBudgetExpensePageVO> expensePage(Long periodId, Long itemId, String keyword,
                                                     String beginDate, String endDate,
                                                     Integer pageNum, Integer pageSize);
}
