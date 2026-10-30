package com.iecmp.duty.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.duty.dto.DutyScheduleConfirmDTO;
import com.iecmp.duty.query.DutyScheduleCalendarQuery;
import com.iecmp.duty.query.UserDutySchedulePageQuery;
import com.iecmp.duty.service.UserDutyScheduleService;
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
 * 用户端-我的值日。
 *
 * <p>覆盖接口：7-01、7-02、7-03、7-04，共 4 个。
 */
@Tag(name = "用户端-我的值日")
@RestController
@RequiredArgsConstructor
public class UserDutyScheduleController {

    private final UserDutyScheduleService userDutyScheduleService;

    @Operation(summary = "7-01 值日安排分页（公示）", description = "成员端查询已发布计划下的值日安排")
    @GetMapping("/user/duty/schedule/page")
    public Result<PageResult<UserDutySchedulePageVO>> getDutySchedulePage(
            @ParameterObject UserDutySchedulePageQuery query) {
        return Result.success(userDutyScheduleService.page(query));
    }

    @Operation(summary = "7-02 我的值日（含待办）", description = "返回当前用户的下次值日与待办统计，用于首页待办卡片")
    @GetMapping("/user/duty/schedule/my")
    public Result<DutyScheduleMyVO> getDutyScheduleMy() {
        return Result.success(userDutyScheduleService.my());
    }

    @Operation(summary = "7-03 值日日历（月视图）", description = "按月返回值日排布，供前端渲染日历视图")
    @GetMapping("/user/duty/schedule/calendar")
    public Result<DutyScheduleCalendarVO> getDutyScheduleCalendar(
            @ParameterObject DutyScheduleCalendarQuery query) {
        return Result.success(userDutyScheduleService.calendar(query));
    }

    @Operation(summary = "7-04 值日完成确认", description = "值日人确认自己已完成当日值日")
    @OperLog(module = "用户端-我的值日", value = "值日完成确认")
    @PutMapping("/user/duty/schedule/{id}/confirm")
    public Result<Void> putDutyScheduleConfirm(@PathVariable("id") Long id,
                                               @RequestBody @Valid DutyScheduleConfirmDTO dto) {
        userDutyScheduleService.confirm(id, dto);
        return Result.success();
    }
}