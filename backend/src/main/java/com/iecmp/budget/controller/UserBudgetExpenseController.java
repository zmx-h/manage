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
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-支出公示")
@RestController
@RequiredArgsConstructor
public class UserBudgetExpenseController {

    /** BudgetExpense 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetExpenseService budgetExpenseService;

    /**
     * 3-04 支出明细公示。
     *
     * <p>{@code GET /user/budget/expense/page} —— 分页查询周期的支出明细，供成员监督资金去向
     *
     * <p>权限：登录即可；令牌：是；分页返回。
     */
    @Operation(summary = "3-04 支出明细公示", description = "分页查询周期的支出明细，供成员监督资金去向")
    @GetMapping("/user/budget/expense/page")
    public Result<PageResult<UserBudgetExpensePageVO>> getBudgetExpensePage(@ParameterObject UserBudgetExpensePageQuery query) {
        // TODO 待实现（接口 3-04）：见《IECMP-接口开发文档-v1.6.md》接口 3-04
        return Result.todo("3-04");
    }
}
