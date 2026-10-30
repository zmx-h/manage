package com.iecmp.budget.controller;

import com.iecmp.budget.dto.BudgetPeriodAddDTO;
import com.iecmp.budget.dto.BudgetPeriodPublicDTO;
import com.iecmp.budget.dto.BudgetPeriodStatusDTO;
import com.iecmp.budget.dto.BudgetPeriodUpdateDTO;
import com.iecmp.budget.query.AdminBudgetPeriodPageQuery;
import com.iecmp.budget.service.BudgetPeriodService;
import com.iecmp.budget.vo.AdminBudgetPeriodPageVO;
import com.iecmp.budget.vo.BudgetPeriodCreateVO;
import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** 管理端-预算周期（3-10 ~ 3-16）。 */
@Tag(name = "管理端-预算周期")
@RestController
@RequiredArgsConstructor
public class AdminBudgetPeriodController {

    private final BudgetPeriodService budgetPeriodService;

    @Operation(summary = "3-10 预算周期分页（后台）", description = "管理端分页查询全部预算周期（含草稿）")
    @RequiresPerm("budget:period:list")
    @GetMapping("/admin/budget/period/page")
    public Result<PageResult<AdminBudgetPeriodPageVO>> getBudgetPeriodPage(
            @ParameterObject AdminBudgetPeriodPageQuery query) {
        return Result.success(budgetPeriodService.adminPeriodPage(query));
    }

    @Operation(summary = "3-11 新增预算周期（起草）", description = "创建预算周期，初始状态为草稿")
    @RequiresPerm("budget:period:add")
    @OperLog(module = "管理端-预算周期", value = "新增预算周期（起草）")
    @PostMapping("/admin/budget/period")
    public Result<BudgetPeriodCreateVO> postBudgetPeriod(@RequestBody @Valid BudgetPeriodAddDTO dto) {
        return Result.success(budgetPeriodService.createPeriod(dto));
    }

    @Operation(summary = "3-12 修改预算周期", description = "修改周期信息与总额度；总额度不得低于已使用额度")
    @RequiresPerm("budget:period:edit")
    @OperLog(module = "管理端-预算周期", value = "修改预算周期")
    @PutMapping("/admin/budget/period")
    public Result<Void> putBudgetPeriod(@RequestBody @Valid BudgetPeriodUpdateDTO dto) {
        budgetPeriodService.updatePeriod(dto);
        return Result.success();
    }

    @Operation(summary = "3-13 删除预算周期", description = "逻辑删除预算周期；存在科目或申请时禁止删除")
    @RequiresPerm("budget:period:remove")
    @OperLog(module = "管理端-预算周期", value = "删除预算周期")
    @DeleteMapping("/admin/budget/period/{id}")
    public Result<Void> deleteBudgetPeriod(@PathVariable("id") Long id) {
        budgetPeriodService.deletePeriod(id);
        return Result.success();
    }

    @Operation(summary = "3-14 周期状态流转", description = "变更周期状态：草稿 -> 执行中 -> 已结束")
    @RequiresPerm("budget:period:audit")
    @OperLog(module = "管理端-预算周期", value = "周期状态流转")
    @PutMapping("/admin/budget/period/{id}/status")
    public Result<Void> putBudgetPeriodStatus(@PathVariable("id") Long id,
                                              @RequestBody @Valid BudgetPeriodStatusDTO dto) {
        budgetPeriodService.changeStatus(id, dto.getStatus());
        return Result.success();
    }

    @Operation(summary = "3-15 公示 / 取消公示", description = "控制周期是否对成员端公示")
    @RequiresPerm("budget:period:public")
    @OperLog(module = "管理端-预算周期", value = "公示 / 取消公示")
    @PutMapping("/admin/budget/period/{id}/public")
    public Result<Void> putBudgetPeriodPublic(@PathVariable("id") Long id,
                                              @RequestBody @Valid BudgetPeriodPublicDTO dto) {
        budgetPeriodService.changePublic(id, dto.getIsPublic());
        return Result.success();
    }

    @Operation(summary = "3-16 发起预算征集（v1.1 预留）", description = "v1.0 不开放，调用返回业务错误 30020")
    @RequiresPerm("budget:collect:exec")
    @OperLog(module = "管理端-预算周期", value = "发起预算征集（v1.1 预留）")
    @PostMapping("/admin/budget/period/{id}/collect")
    public Result<Void> postBudgetPeriodCollect(@PathVariable("id") Long id) {
        budgetPeriodService.collect(id);
        return Result.success();
    }
}