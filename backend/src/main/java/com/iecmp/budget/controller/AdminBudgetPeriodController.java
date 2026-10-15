package com.iecmp.budget.controller;

import com.iecmp.budget.dto.BudgetPeriodAddDTO;
import com.iecmp.budget.dto.BudgetPeriodPublicDTO;
import com.iecmp.budget.dto.BudgetPeriodStatusDTO;
import com.iecmp.budget.dto.BudgetPeriodUpdateDTO;
import com.iecmp.budget.query.AdminBudgetPeriodPageQuery;
import com.iecmp.budget.service.BudgetItemService;
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

/**
 * 管理端-预算周期（budget 模块）。
 *
 * <p>覆盖接口：3-10、3-11、3-12、3-13、3-14、3-15、3-16，共 7 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-预算周期")
@RestController
@RequiredArgsConstructor
public class AdminBudgetPeriodController {

    /** BudgetPeriod 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetPeriodService budgetPeriodService;
    /** BudgetItem 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetItemService budgetItemService;

    /**
     * 3-10 预算周期分页（后台）。
     *
     * <p>{@code GET /admin/budget/period/page} —— 管理端分页查询全部预算周期（含草稿）
     *
     * <p>权限：budget:period:list；令牌：是；分页返回。
     */
    @Operation(summary = "3-10 预算周期分页（后台）", description = "管理端分页查询全部预算周期（含草稿）")
    @RequiresPerm("budget:period:list")
    @GetMapping("/admin/budget/period/page")
    public Result<PageResult<AdminBudgetPeriodPageVO>> getBudgetPeriodPage(@ParameterObject AdminBudgetPeriodPageQuery query) {
        // TODO 待实现（接口 3-10）：见《IECMP-接口开发文档-v1.6.md》接口 3-10
        return Result.todo("3-10");
    }

    /**
     * 3-11 新增预算周期（起草）。
     *
     * <p>{@code POST /admin/budget/period} —— 管理员创建预算周期，初始状态为草稿；确认无误后由接口 `3-14` 流转为执行中
     *
     * <p>权限：budget:period:add；令牌：是。
     */
    @Operation(summary = "3-11 新增预算周期（起草）", description = "管理员创建预算周期，初始状态为草稿；确认无误后由接口 `3-14` 流转为执行中")
    @RequiresPerm("budget:period:add")
    @OperLog(module = "管理端-预算周期", value = "新增预算周期（起草）")
    @PostMapping("/admin/budget/period")
    public Result<BudgetPeriodCreateVO> postBudgetPeriod(@RequestBody @Valid BudgetPeriodAddDTO dto) {
        // TODO 待实现（接口 3-11）：见《IECMP-接口开发文档-v1.6.md》接口 3-11
        return Result.todo("3-11");
    }

    /**
     * 3-12 修改预算周期。
     *
     * <p>{@code PUT /admin/budget/period} —— 修改周期信息与总额度；总额度不得低于已使用额度
     *
     * <p>权限：budget:period:edit；令牌：是。
     */
    @Operation(summary = "3-12 修改预算周期", description = "修改周期信息与总额度；总额度不得低于已使用额度")
    @RequiresPerm("budget:period:edit")
    @OperLog(module = "管理端-预算周期", value = "修改预算周期")
    @PutMapping("/admin/budget/period")
    public Result<Void> putBudgetPeriod(@RequestBody @Valid BudgetPeriodUpdateDTO dto) {
        // TODO 待实现（接口 3-12）：见《IECMP-接口开发文档-v1.6.md》接口 3-12
        return Result.todo("3-12");
    }

    /**
     * 3-13 删除预算周期。
     *
     * <p>{@code DELETE /admin/budget/period/{id}} —— 逻辑删除预算周期；存在科目或申请时禁止删除
     *
     * <p>权限：budget:period:remove；令牌：是。
     */
    @Operation(summary = "3-13 删除预算周期", description = "逻辑删除预算周期；存在科目或申请时禁止删除")
    @RequiresPerm("budget:period:remove")
    @OperLog(module = "管理端-预算周期", value = "删除预算周期")
    @DeleteMapping("/admin/budget/period/{id}")
    public Result<Void> deleteBudgetPeriod(@PathVariable("id") Long id) {
        // TODO 待实现（接口 3-13）：见《IECMP-接口开发文档-v1.6.md》接口 3-13
        return Result.todo("3-13");
    }

    /**
     * 3-14 周期状态流转。
     *
     * <p>{@code PUT /admin/budget/period/{id}/status} —— 变更周期状态：草稿 → 执行中 → 已结束
     *
     * <p>权限：budget:period:audit；令牌：是。
     */
    @Operation(summary = "3-14 周期状态流转", description = "变更周期状态：草稿 → 执行中 → 已结束")
    @RequiresPerm("budget:period:audit")
    @OperLog(module = "管理端-预算周期", value = "周期状态流转")
    @PutMapping("/admin/budget/period/{id}/status")
    public Result<Void> putBudgetPeriodStatus(@PathVariable("id") Long id, @RequestBody @Valid BudgetPeriodStatusDTO dto) {
        // TODO 待实现（接口 3-14）：见《IECMP-接口开发文档-v1.6.md》接口 3-14
        return Result.todo("3-14");
    }

    /**
     * 3-15 公示 / 取消公示。
     *
     * <p>{@code PUT /admin/budget/period/{id}/public} —— 控制周期是否对成员端公示；公示后成员可查看周期、科目与支出明细
     *
     * <p>权限：budget:period:public；令牌：是。
     */
    @Operation(summary = "3-15 公示 / 取消公示", description = "控制周期是否对成员端公示；公示后成员可查看周期、科目与支出明细")
    @RequiresPerm("budget:period:public")
    @OperLog(module = "管理端-预算周期", value = "公示 / 取消公示")
    @PutMapping("/admin/budget/period/{id}/public")
    public Result<Void> putBudgetPeriodPublic(@PathVariable("id") Long id, @RequestBody @Valid BudgetPeriodPublicDTO dto) {
        // TODO 待实现（接口 3-15）：见《IECMP-接口开发文档-v1.6.md》接口 3-15
        return Result.todo("3-15");
    }

    /**
     * 3-16 发起预算征集（v1.1 预留）。
     *
     * <p>{@code POST /admin/budget/period/{id}/collect} —— **v1.0 不实现，接口位预留**。原设计为集中征集成员需求，现已改为「成员在执行期间随时提交申请」，此环节取消
     *
     * <p>权限：budget:collect:exec；令牌：是。
     */
    @Operation(summary = "3-16 发起预算征集（v1.1 预留）", description = "**v1.0 不实现，接口位预留**。原设计为集中征集成员需求，现已改为「成员在执行期间随时提交申请」，此环节取消")
    @RequiresPerm("budget:collect:exec")
    @OperLog(module = "管理端-预算周期", value = "发起预算征集（v1.1 预留）")
    @PostMapping("/admin/budget/period/{id}/collect")
    public Result<Void> postBudgetPeriodCollect(@PathVariable("id") Long id) {
        // TODO 待实现（接口 3-16）：见《IECMP-接口开发文档-v1.6.md》接口 3-16
        return Result.todo("3-16");
    }
}
