package com.iecmp.duty.controller;

import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.Result;
import com.iecmp.duty.query.DutyStatOverviewQuery;
import com.iecmp.duty.service.DutyPlanService;
import com.iecmp.duty.service.DutyScheduleService;
import com.iecmp.duty.vo.DutyStatOverviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-值日统计（duty 模块）。
 *
 * <p>覆盖接口：7-19，共 1 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-值日统计")
@RestController
@RequiredArgsConstructor
public class AdminDutyStatController {

    /** DutySchedule 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final DutyScheduleService dutyScheduleService;
    /** DutyPlan 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final DutyPlanService dutyPlanService;

    /**
     * 7-19 值日统计看板。
     *
     * <p>{@code GET /admin/duty/stat/overview} —— 返回值日执行情况统计，用于管理端查看谁在认真值日
     *
     * <p>权限：duty:stat:list；令牌：是。
     */
    @Operation(summary = "7-19 值日统计看板", description = "返回值日执行情况统计，用于管理端查看谁在认真值日")
    @RequiresPerm("duty:stat:list")
    @GetMapping("/admin/duty/stat/overview")
    public Result<DutyStatOverviewVO> getDutyStatOverview(@ParameterObject DutyStatOverviewQuery query) {
        // TODO 待实现（接口 7-19）：见《IECMP-接口开发文档-v1.6.md》接口 7-19
        return Result.todo("7-19");
    }
}
