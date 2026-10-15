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

/**
 * 用户端-预算公示（budget 模块）。
 *
 * <p>覆盖接口：3-01、3-02、3-03，共 3 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-预算公示")
@RestController
@RequiredArgsConstructor
public class UserBudgetPeriodController {

    /** BudgetPeriod 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetPeriodService budgetPeriodService;
    /** BudgetItem 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetItemService budgetItemService;

    /**
     * 3-01 公示预算周期分页。
     *
     * <p>{@code GET /user/budget/period/page} —— 成员端查询已公示的预算周期及执行进度
     *
     * <p>权限：登录即可；令牌：是；分页返回。
     */
    @Operation(summary = "3-01 公示预算周期分页", description = "成员端查询已公示的预算周期及执行进度")
    @GetMapping("/user/budget/period/page")
    public Result<PageResult<UserBudgetPeriodPageVO>> getBudgetPeriodPage(@ParameterObject UserBudgetPeriodPageQuery query) {
        // TODO 待实现（接口 3-01）：见《IECMP-接口开发文档-v1.6.md》接口 3-01
        return Result.todo("3-01");
    }

    /**
     * 3-02 预算周期详情。
     *
     * <p>{@code GET /user/budget/period/{id}} —— 查询周期详情与执行概况，用于前台预算详情页头部
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "3-02 预算周期详情", description = "查询周期详情与执行概况，用于前台预算详情页头部")
    @GetMapping("/user/budget/period/{id}")
    public Result<BudgetPeriodDetailVO> getBudgetPeriod(@PathVariable("id") Long id) {
        // TODO 待实现（接口 3-02）：见《IECMP-接口开发文档-v1.6.md》接口 3-02
        return Result.todo("3-02");
    }

    /**
     * 3-03 预算科目明细。
     *
     * <p>{@code GET /user/budget/period/{id}/item/list} —— 查询指定周期下的全部科目及各自额度使用情况
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "3-03 预算科目明细", description = "查询指定周期下的全部科目及各自额度使用情况")
    @GetMapping("/user/budget/period/{id}/item/list")
    public Result<BudgetPeriodItemListVO> getBudgetPeriodItemList(@PathVariable("id") Long id) {
        // TODO 待实现（接口 3-03）：见《IECMP-接口开发文档-v1.6.md》接口 3-03
        return Result.todo("3-03");
    }
}
