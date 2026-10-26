package com.iecmp.budget.controller;

import com.iecmp.budget.dto.BudgetExpenseAddDTO;
import com.iecmp.budget.query.AdminBudgetExpensePageQuery;
import com.iecmp.budget.service.BudgetExpenseService;
import com.iecmp.budget.service.BudgetItemService;
import com.iecmp.budget.service.BudgetPeriodService;
import com.iecmp.budget.vo.AdminBudgetExpensePageVO;
import com.iecmp.budget.vo.BudgetExpenseCreateVO;
import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.common.util.LoginUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-支出登记（budget 模块）。
 *
 * <p>覆盖接口：3-22、3-23、3-24，共 3 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-支出登记")
@RestController
@RequiredArgsConstructor
public class AdminBudgetExpenseController {

    /** BudgetExpense 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetExpenseService budgetExpenseService;
    /** BudgetPeriod 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetPeriodService budgetPeriodService;
    /** BudgetItem 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetItemService budgetItemService;

    /**
     * 3-22 登记支出。
     *
     * <p>{@code POST /admin/budget/expense} —— 管理员登记实际支出并扣减周期与科目额度，形成「申请 → 审核 → 拨款 → 支出」链路的最后一环
     *
     * <p>权限：budget:expense:add；令牌：是。
     */
    @Operation(summary = "3-22 登记支出", description = "管理员登记实际支出并扣减周期与科目额度，形成「申请 → 审核 → 拨款 → 支出」链路的最后一环")
    @RequiresPerm("budget:expense:add")
    @OperLog(module = "管理端-支出登记", value = "登记支出")
    @PostMapping("/admin/budget/expense")
    public Result<BudgetExpenseCreateVO> postBudgetExpense(@RequestBody @Valid BudgetExpenseAddDTO dto) {
        return Result.success(budgetExpenseService.createExpense(dto, LoginUserUtil.getUserId()));
    }

    /**
     * 3-23 支出分页（后台）。
     *
     * <p>{@code GET /admin/budget/expense/page} —— 管理端分页查询支出记录，含凭证文件 ID
     *
     * <p>权限：budget:expense:list；令牌：是；分页返回。
     */
    @Operation(summary = "3-23 支出分页（后台）", description = "管理端分页查询支出记录，含凭证文件 ID")
    @RequiresPerm("budget:expense:list")
    @GetMapping("/admin/budget/expense/page")
    public Result<PageResult<AdminBudgetExpensePageVO>> getBudgetExpensePage(@ParameterObject AdminBudgetExpensePageQuery query) {
        return Result.success(budgetExpenseService.adminExpensePage(query));
    }

    /**
     * 3-24 删除支出记录。
     *
     * <p>{@code DELETE /admin/budget/expense/{id}} —— 逻辑删除支出记录，并回退已占用的周期与科目额度
     *
     * <p>权限：budget:expense:remove；令牌：是。
     */
    @Operation(summary = "3-24 删除支出记录", description = "逻辑删除支出记录，并回退已占用的周期与科目额度")
    @RequiresPerm("budget:expense:remove")
    @OperLog(module = "管理端-支出登记", value = "删除支出记录")
    @DeleteMapping("/admin/budget/expense/{id}")
    public Result<Void> deleteBudgetExpense(@PathVariable("id") Long id) {
        budgetExpenseService.deleteExpense(id);
        return Result.success();
    }
}
