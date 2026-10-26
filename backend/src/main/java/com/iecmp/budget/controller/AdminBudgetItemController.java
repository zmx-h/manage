package com.iecmp.budget.controller;

import com.iecmp.budget.dto.BudgetItemAddDTO;
import com.iecmp.budget.dto.BudgetItemUpdateDTO;
import com.iecmp.budget.service.BudgetItemService;
import com.iecmp.budget.vo.BudgetItemCreateVO;
import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-预算科目（budget 模块）。
 *
 * <p>覆盖接口：3-17、3-18、3-19，共 3 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-预算科目")
@RestController
@RequiredArgsConstructor
public class AdminBudgetItemController {

    /** BudgetItem 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetItemService budgetItemService;

    /**
     * 3-17 新增预算科目。
     *
     * <p>{@code POST /admin/budget/item} —— 在指定周期下新增预算科目与额度
     *
     * <p>权限：budget:item:edit；令牌：是。
     */
    @Operation(summary = "3-17 新增预算科目", description = "在指定周期下新增预算科目与额度")
    @RequiresPerm("budget:item:edit")
    @OperLog(module = "管理端-预算科目", value = "新增预算科目")
    @PostMapping("/admin/budget/item")
    public Result<BudgetItemCreateVO> postBudgetItem(@RequestBody @Valid BudgetItemAddDTO dto) {
        return Result.success(budgetItemService.createItem(dto));
    }

    /**
     * 3-18 修改预算科目。
     *
     * <p>{@code PUT /admin/budget/item} —— 修改科目信息与额度；额度不得低于已使用额度
     *
     * <p>权限：budget:item:edit；令牌：是。
     */
    @Operation(summary = "3-18 修改预算科目", description = "修改科目信息与额度；额度不得低于已使用额度")
    @RequiresPerm("budget:item:edit")
    @OperLog(module = "管理端-预算科目", value = "修改预算科目")
    @PutMapping("/admin/budget/item")
    public Result<Void> putBudgetItem(@RequestBody @Valid BudgetItemUpdateDTO dto) {
        budgetItemService.updateItem(dto);
        return Result.success();
    }

    /**
     * 3-19 删除预算科目。
     *
     * <p>{@code DELETE /admin/budget/item/{id}} —— 逻辑删除预算科目；已被申请或支出引用时禁止删除
     *
     * <p>权限：budget:item:edit；令牌：是。
     */
    @Operation(summary = "3-19 删除预算科目", description = "逻辑删除预算科目；已被申请或支出引用时禁止删除")
    @RequiresPerm("budget:item:edit")
    @OperLog(module = "管理端-预算科目", value = "删除预算科目")
    @DeleteMapping("/admin/budget/item/{id}")
    public Result<Void> deleteBudgetItem(@PathVariable("id") Long id) {
        budgetItemService.deleteItem(id);
        return Result.success();
    }
}
