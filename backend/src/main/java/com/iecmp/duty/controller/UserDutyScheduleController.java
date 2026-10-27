package com.iecmp.duty.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.duty.dto.DutyScheduleConfirmDTO;
import com.iecmp.duty.query.DutyScheduleCalendarQuery;
import com.iecmp.duty.query.UserDutySchedulePageQuery;
import com.iecmp.duty.service.DutyPlanService;
import com.iecmp.duty.service.DutyScheduleService;
import com.iecmp.duty.vo.DutyScheduleCalendarVO;
import com.iecmp.duty.vo.DutyScheduleMyVO;
import com.iecmp.duty.vo.UserDutySchedulePageVO;
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
 * 用户端-我的值日（duty 模块）。
 *
 * <p>覆盖接口：7-01、7-02、7-03、7-04，共 4 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author liuyujie
 * @date 2026.10.27
 */
@Tag(name = "用户端-我的值日")
@RestController
@RequiredArgsConstructor
public class UserDutyScheduleController {

    /** DutySchedule 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final DutyScheduleService dutyScheduleService;
    /** DutyPlan 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final DutyPlanService dutyPlanService;

    /**
     * 7-01 值日安排分页（公示）。
     *
     * <p>{@code GET /user/duty/schedule/page} —— 成员端查询已发布计划下的值日安排
     *
     * <p>权限：登录即可；令牌：是；分页返回。
     */
    @Operation(summary = "7-01 值日安排分页（公示）", description = "成员端查询已发布计划下的值日安排")
    @GetMapping("/user/duty/schedule/page")
    public Result<PageResult<UserDutySchedulePageVO>> getDutySchedulePage(@ParameterObject UserDutySchedulePageQuery query) {
        // TODO 待实现（接口 7-01）：见《IECMP-接口开发文档-v1.6.md》接口 7-01
        return Result.success(dutyScheduleService.pagePublic(query));
    }

    /**
     * 7-02 我的值日（含待办）。
     *
     * <p>{@code GET /user/duty/schedule/my} —— 返回当前用户的下次值日与待办统计，用于首页待办卡片
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "7-02 我的值日（含待办）", description = "返回当前用户的下次值日与待办统计，用于首页待办卡片")
    @GetMapping("/user/duty/schedule/my")
    public Result<DutyScheduleMyVO> getDutyScheduleMy() {
        // TODO 待实现（接口 7-02）：见《IECMP-接口开发文档-v1.6.md》接口 7-02
        return Result.success(dutyScheduleService.getMyDuty());
    }

    /**
     * 7-03 值日日历（月视图）。
     *
     * <p>{@code GET /user/duty/schedule/calendar} —— 按月返回值日排布，供前端渲染日历视图
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "7-03 值日日历（月视图）", description = "按月返回值日排布，供前端渲染日历视图")
    @GetMapping("/user/duty/schedule/calendar")
    public Result<DutyScheduleCalendarVO> getDutyScheduleCalendar(@ParameterObject DutyScheduleCalendarQuery query) {
        // TODO 待实现（接口 7-03）：见《IECMP-接口开发文档-v1.6.md》接口 7-03
        return Result.success(dutyScheduleService.getCalendar(query));
    }

    /**
     * 7-04 值日完成确认。
     *
     * <p>{@code PUT /user/duty/schedule/{id}/confirm} —— 值日人确认自己已完成当日值日
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "7-04 值日完成确认", description = "值日人确认自己已完成当日值日")
    @OperLog(module = "用户端-我的值日", value = "值日完成确认")
    @PutMapping("/user/duty/schedule/{id}/confirm")
    public Result<Void> putDutyScheduleConfirm(@PathVariable("id") Long id, @RequestBody @Valid DutyScheduleConfirmDTO dto) {
        // TODO 待实现（接口 7-04）：见《IECMP-接口开发文档-v1.6.md》接口 7-04
        dutyScheduleService.confirm(id, dto);
        return Result.success();
    }
}
