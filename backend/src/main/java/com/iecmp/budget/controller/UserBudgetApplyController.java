package com.iecmp.budget.controller;

import com.iecmp.budget.dto.BudgetApplyDTO;
import com.iecmp.budget.query.BudgetApplyMyPageQuery;
import com.iecmp.budget.query.BudgetStatMyQuery;
import com.iecmp.budget.service.BudgetApplyService;
import com.iecmp.budget.service.BudgetExpenseService;
import com.iecmp.budget.service.BudgetItemService;
import com.iecmp.budget.service.BudgetPeriodService;
import com.iecmp.budget.vo.BudgetApplyDetailVO;
import com.iecmp.budget.vo.BudgetApplyMyPageVO;
import com.iecmp.budget.vo.BudgetApplyVO;
import com.iecmp.budget.vo.BudgetStatMyVO;
import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端-预算申请（budget 模块）。
 *
 * <p>覆盖接口：3-05、3-06、3-07、3-08、3-09，共 5 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-预算申请")
@RestController
@RequiredArgsConstructor
public class UserBudgetApplyController {

    /** BudgetApply 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetApplyService budgetApplyService;
    /** BudgetItem 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetItemService budgetItemService;
    /** BudgetPeriod 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetPeriodService budgetPeriodService;
    /** BudgetExpense 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final BudgetExpenseService budgetExpenseService;

    /**
     * 3-05 提交预算申请。
     *
     * <p>{@code POST /user/budget/apply} —— 成员提交经费申请；预算周期**执行期间可随时提交**，无需等待集中征集
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "3-05 提交预算申请", description = "成员提交经费申请；预算周期**执行期间可随时提交**，无需等待集中征集")
    @OperLog(module = "用户端-预算申请", value = "提交预算申请")
    @PostMapping("/user/budget/apply")
    public Result<BudgetApplyVO> postBudgetApply(@RequestBody @Valid BudgetApplyDTO dto) {
        // TODO 待实现（接口 3-05）：见《IECMP-接口开发文档-v1.6.md》接口 3-05
        return Result.todo("3-05");
    }

    /**
     * 3-06 我的预算申请分页。
     *
     * <p>{@code GET /user/budget/apply/my/page} —— 分页查询当前登录用户提交的经费申请
     *
     * <p>权限：登录即可；令牌：是；分页返回。
     */
    @Operation(summary = "3-06 我的预算申请分页", description = "分页查询当前登录用户提交的经费申请")
    @GetMapping("/user/budget/apply/my/page")
    public Result<PageResult<BudgetApplyMyPageVO>> getBudgetApplyMyPage(@ParameterObject BudgetApplyMyPageQuery query) {
        // TODO 待实现（接口 3-06）：见《IECMP-接口开发文档-v1.6.md》接口 3-06
        return Result.todo("3-06");
    }

    /**
     * 3-07 预算申请详情。
     *
     * <p>{@code GET /user/budget/apply/{id}} —— 查询指定申请的完整信息，含申请事由与关联的支出记录
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "3-07 预算申请详情", description = "查询指定申请的完整信息，含申请事由与关联的支出记录")
    @GetMapping("/user/budget/apply/{id}")
    public Result<BudgetApplyDetailVO> getBudgetApply(@PathVariable("id") Long id) {
        // TODO 待实现（接口 3-07）：见《IECMP-接口开发文档-v1.6.md》接口 3-07
        return Result.todo("3-07");
    }

    /**
     * 3-08 撤销预算申请。
     *
     * <p>{@code PUT /user/budget/apply/{id}/cancel} —— 成员撤销尚未审核的申请
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "3-08 撤销预算申请", description = "成员撤销尚未审核的申请")
    @OperLog(module = "用户端-预算申请", value = "撤销预算申请")
    @PutMapping("/user/budget/apply/{id}/cancel")
    public Result<Void> putBudgetApplyCancel(@PathVariable("id") Long id) {
        // TODO 待实现（接口 3-08）：见《IECMP-接口开发文档-v1.6.md》接口 3-08
        return Result.todo("3-08");
    }

    /**
     * 3-09 我的经费汇总。
     *
     * <p>{@code GET /user/budget/stat/my} —— 统计当前登录用户在某年度的申请与获批情况
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "3-09 我的经费汇总", description = "统计当前登录用户在某年度的申请与获批情况")
    @GetMapping("/user/budget/stat/my")
    public Result<BudgetStatMyVO> getBudgetStatMy(@ParameterObject BudgetStatMyQuery query) {
        // TODO 待实现（接口 3-09）：见《IECMP-接口开发文档-v1.6.md》接口 3-09
        return Result.todo("3-09");
    }
}
