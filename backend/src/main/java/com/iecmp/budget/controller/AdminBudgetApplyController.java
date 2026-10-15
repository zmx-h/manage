package com.iecmp.budget.controller;

import com.iecmp.budget.dto.BudgetApplyAuditDTO;
import com.iecmp.budget.query.BudgetApplyPageQuery;
import com.iecmp.budget.service.BudgetApplyService;
import com.iecmp.budget.service.BudgetItemService;
import com.iecmp.budget.vo.BudgetApplyPageVO;
import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-预算审核（budget 模块）。
 *
 * <p>覆盖接口：3-20、3-21，共 2 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-预算审核")
@RestController
@RequiredArgsConstructor
public class AdminBudgetApplyController {

    /** BudgetApply 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetApplyService budgetApplyService;
    /** BudgetItem 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetItemService budgetItemService;

    /**
     * 3-20 申请审核分页（后台）。
     *
     * <p>{@code GET /admin/budget/apply/page} —— 管理端分页查询全部经费申请，用于审核排队与进度跟踪
     *
     * <p>权限：budget:apply:list；令牌：是；分页返回。
     */
    @Operation(summary = "3-20 申请审核分页（后台）", description = "管理端分页查询全部经费申请，用于审核排队与进度跟踪")
    @RequiresPerm("budget:apply:list")
    @GetMapping("/admin/budget/apply/page")
    public Result<PageResult<BudgetApplyPageVO>> getBudgetApplyPage(@ParameterObject BudgetApplyPageQuery query) {
        // TODO 待实现（接口 3-20）：见《IECMP-接口开发文档-v1.6.md》接口 3-20
        return Result.todo("3-20");
    }

    /**
     * 3-21 申请审核（通过 / 驳回）。
     *
     * <p>{@code PUT /admin/budget/apply/{id}/audit} —— 管理员审核经费申请；通过时可调整批准金额
     *
     * <p>权限：budget:apply:audit；令牌：是。
     */
    @Operation(summary = "3-21 申请审核（通过 / 驳回）", description = "管理员审核经费申请；通过时可调整批准金额")
    @RequiresPerm("budget:apply:audit")
    @OperLog(module = "管理端-预算审核", value = "申请审核（通过 / 驳回）")
    @PutMapping("/admin/budget/apply/{id}/audit")
    public Result<Void> putBudgetApplyAudit(@PathVariable("id") Long id, @RequestBody @Valid BudgetApplyAuditDTO dto) {
        // TODO 待实现（接口 3-21）：见《IECMP-接口开发文档-v1.6.md》接口 3-21
        return Result.todo("3-21");
    }
}
