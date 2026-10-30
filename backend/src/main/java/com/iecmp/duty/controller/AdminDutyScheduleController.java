package com.iecmp.duty.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.duty.dto.DutyScheduleAddDTO;
import com.iecmp.duty.dto.DutyScheduleStatusDTO;
import com.iecmp.duty.dto.DutyScheduleUpdateDTO;
import com.iecmp.duty.query.AdminDutySchedulePageQuery;
import com.iecmp.duty.service.DutyScheduleService;
import com.iecmp.duty.vo.AdminDutySchedulePageVO;
import com.iecmp.duty.vo.DutyScheduleCreateVO;
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
 * 管理端-值日条目。
 *
 * <p>覆盖接口：7-14、7-15、7-16、7-17、7-18，共 5 个。
 */
@Tag(name = "管理端-值日条目")
@RestController
@RequiredArgsConstructor
public class AdminDutyScheduleController {

    private final DutyScheduleService dutyScheduleService;

    @Operation(summary = "7-14 新增值日条目", description = "手工登记一条值日安排，用于补排、临时调整或手工指定场景")
    @RequiresPerm("duty:schedule:edit")
    @OperLog(module = "管理端-值日条目", value = "新增值日条目")
    @PostMapping("/admin/duty/schedule")
    public Result<DutyScheduleCreateVO> postDutySchedule(
            @RequestBody @Valid DutyScheduleAddDTO dto) {
        return Result.success(dutyScheduleService.add(dto));
    }

    @Operation(summary = "7-15 修改值日条目", description = "修改值日人、日期、内容或备注")
    @RequiresPerm("duty:schedule:edit")
    @OperLog(module = "管理端-值日条目", value = "修改值日条目")
    @PutMapping("/admin/duty/schedule")
    public Result<Void> putDutySchedule(@RequestBody @Valid DutyScheduleUpdateDTO dto) {
        dutyScheduleService.update(dto);
        return Result.success();
    }

    @Operation(summary = "7-16 删除值日条目", description = "逻辑删除一条值日安排")
    @RequiresPerm("duty:schedule:edit")
    @OperLog(module = "管理端-值日条目", value = "删除值日条目")
    @DeleteMapping("/admin/duty/schedule/{id}")
    public Result<Void> deleteDutySchedule(@PathVariable("id") Long id) {
        dutyScheduleService.delete(id);
        return Result.success();
    }

    @Operation(summary = "7-17 值日安排分页（后台）", description = "管理端分页查询值日安排，支持更多筛选维度与全局视图")
    @RequiresPerm("duty:schedule:list")
    @GetMapping("/admin/duty/schedule/page")
    public Result<PageResult<AdminDutySchedulePageVO>> getDutySchedulePage(
            @ParameterObject AdminDutySchedulePageQuery query) {
        return Result.success(dutyScheduleService.page(query));
    }

    @Operation(summary = "7-18 值日状态变更", description = "管理员变更值日完成状态，用于代确认、登记请假、纠正误操作")
    @RequiresPerm("duty:schedule:edit")
    @OperLog(module = "管理端-值日条目", value = "值日状态变更")
    @PutMapping("/admin/duty/schedule/{id}/status")
    public Result<Void> putDutyScheduleStatus(@PathVariable("id") Long id,
                                              @RequestBody @Valid DutyScheduleStatusDTO dto) {
        dutyScheduleService.updateStatus(id, dto);
        return Result.success();
    }
}