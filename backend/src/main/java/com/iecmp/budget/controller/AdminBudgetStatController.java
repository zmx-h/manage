package com.iecmp.budget.controller;

import com.iecmp.budget.query.BudgetStatOverviewQuery;
import com.iecmp.budget.service.BudgetApplyService;
import com.iecmp.budget.service.BudgetExpenseService;
import com.iecmp.budget.service.BudgetItemService;
import com.iecmp.budget.service.BudgetPeriodService;
import com.iecmp.budget.vo.BudgetStatOverviewVO;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-预算看板（budget 模块）。
 *
 * <p>覆盖接口：3-25，共 1 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-预算看板")
@RestController
@RequiredArgsConstructor
public class AdminBudgetStatController {

    /** BudgetPeriod 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetPeriodService budgetPeriodService;
    /** BudgetItem 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetItemService budgetItemService;
    /** BudgetApply 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetApplyService budgetApplyService;
    /** BudgetExpense 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetExpenseService budgetExpenseService;

    /**
     * 3-25 预算执行看板。
     *
     * <p>{@code GET /admin/budget/stat/overview} —— 一次性返回预算执行的整体指标、科目维度明细与月度趋势，供管理端看板渲染
     *
     * <p>权限：budget:stat:list；令牌：是。
     */
    @Operation(summary = "3-25 预算执行看板", description = "一次性返回预算执行的整体指标、科目维度明细与月度趋势，供管理端看板渲染")
    @RequiresPerm("budget:stat:list")
    @GetMapping("/admin/budget/stat/overview")
    public Result<BudgetStatOverviewVO> getBudgetStatOverview(@ParameterObject BudgetStatOverviewQuery query) {
        return Result.success(budgetPeriodService.statOverview(query));
    }
}
