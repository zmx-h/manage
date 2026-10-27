package com.iecmp.budget.controller;

import com.iecmp.budget.query.UserBudgetExpensePageQuery;
import com.iecmp.budget.service.BudgetExpenseService;
import com.iecmp.budget.vo.UserBudgetExpensePageVO;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端-支出公示（budget 模块）。
 *
 * <p>覆盖接口：3-04，共 1 个。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-支出公示")
@RestController
@RequiredArgsConstructor
public class UserBudgetExpenseController {

    private final BudgetExpenseService budgetExpenseService;

    @Operation(summary = "3-04 支出明细公示", description = "分页查询周期的支出明细，供成员监督资金去向")
    @GetMapping("/user/budget/expense/page")
    public Result<PageResult<UserBudgetExpensePageVO>> getBudgetExpensePage(@ParameterObject UserBudgetExpensePageQuery query) {
        return Result.success(budgetExpenseService.expensePage(
                query.getPeriodId(), query.getItemId(), query.getKeyword(),
                query.getBeginDate(), query.getEndDate(),
                query.getPageNum(), query.getPageSize()));
    }
}
