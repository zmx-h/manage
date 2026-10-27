package com.iecmp.budget.controller;

import com.iecmp.budget.query.UserBudgetPeriodPageQuery;
import com.iecmp.budget.service.BudgetItemService;
import com.iecmp.budget.service.BudgetPeriodService;
import com.iecmp.budget.vo.BudgetPeriodDetailVO;
import com.iecmp.budget.vo.BudgetPeriodItemListVO;
import com.iecmp.budget.vo.UserBudgetPeriodPageVO;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端-预算公示（budget 模块）。
 *
 * <p>覆盖接口：3-01、3-02、3-03，共 3 个。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-预算公示")
@RestController
@RequiredArgsConstructor
public class UserBudgetPeriodController {

    private final BudgetPeriodService budgetPeriodService;
    private final BudgetItemService budgetItemService;

    @Operation(summary = "3-01 公示预算周期分页", description = "成员端查询已公示的预算周期及执行进度")
    @GetMapping("/user/budget/period/page")
    public Result<PageResult<UserBudgetPeriodPageVO>> getBudgetPeriodPage(@ParameterObject UserBudgetPeriodPageQuery query) {
        return Result.success(budgetPeriodService.periodPage(
                query.getYear(), query.getStatus(), query.getPageNum(), query.getPageSize()));
    }

    @Operation(summary = "3-02 预算周期详情", description = "查询周期详情与执行概况，用于前台预算详情页头部")
    @GetMapping("/user/budget/period/{id}")
    public Result<BudgetPeriodDetailVO> getBudgetPeriod(@PathVariable("id") Long id) {
        return Result.success(budgetPeriodService.periodDetail(id));
    }

    @Operation(summary = "3-03 预算科目明细", description = "查询指定周期下的全部科目及各自额度使用情况")
    @GetMapping("/user/budget/period/{id}/item/list")
    public Result<List<BudgetPeriodItemListVO>> getBudgetPeriodItemList(@PathVariable("id") Long id) {
        return Result.success(budgetItemService.itemList(id));
    }
}
