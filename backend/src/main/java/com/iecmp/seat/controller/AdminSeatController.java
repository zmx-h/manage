package com.iecmp.seat.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.Result;
import com.iecmp.seat.dto.SeatAddDTO;
import com.iecmp.seat.dto.SeatAssignDTO;
import com.iecmp.seat.dto.SeatBatchDTO;
import com.iecmp.seat.dto.SeatPlanDragDTO;
import com.iecmp.seat.dto.SeatStatusDTO;
import com.iecmp.seat.dto.SeatUpdateDTO;
import com.iecmp.seat.query.AdminSeatPlanQuery;
import com.iecmp.seat.service.SeatAdminService;
import com.iecmp.seat.vo.AdminSeatPlanVO;
import com.iecmp.seat.vo.SeatAssignVO;
import com.iecmp.seat.vo.SeatBatchVO;
import com.iecmp.seat.vo.SeatCreateVO;
import com.iecmp.seat.vo.SeatPlanDragVO;
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

import java.util.List;

/** 管理端-工位管理（5-11 ~ 5-17、5-25 ~ 5-26）。 */
@Tag(name = "管理端-工位管理")
@RestController
@RequiredArgsConstructor
public class AdminSeatController {

    private final SeatAdminService seatAdminService;

    @Operation(summary = "5-11 新增工位", description = "在指定区域内新增工位并标注坐标")
    @RequiresPerm("seat:seat:add")
    @OperLog(module = "管理端-工位管理", value = "新增工位")
    @PostMapping("/admin/seat")
    public Result<SeatCreateVO> postSeat(@RequestBody @Valid SeatAddDTO dto) {
        return Result.success(seatAdminService.createSeat(dto));
    }

    @Operation(summary = "5-12 修改工位", description = "修改工位编号、类型、坐标与备注；不改变使用人")
    @RequiresPerm("seat:seat:edit")
    @OperLog(module = "管理端-工位管理", value = "修改工位")
    @PutMapping("/admin/seat")
    public Result<Void> putSeat(@RequestBody @Valid SeatUpdateDTO dto) {
        seatAdminService.updateSeat(dto);
        return Result.success();
    }

    @Operation(summary = "5-13 批量保存工位布局坐标", description = "一次性保存同一区域内多个工位的坐标")
    @RequiresPerm("seat:seat:edit")
    @OperLog(module = "管理端-工位管理", value = "批量保存工位布局坐标")
    @PutMapping("/admin/seat/batch")
    public Result<SeatBatchVO> putSeatBatch(@RequestBody @Valid SeatBatchDTO dto) {
        return Result.success(seatAdminService.saveLayout(dto));
    }

    @Operation(summary = "5-14 删除工位", description = "逻辑删除工位；工位仍被占用时禁止删除")
    @RequiresPerm("seat:seat:remove")
    @OperLog(module = "管理端-工位管理", value = "删除工位")
    @DeleteMapping("/admin/seat/{id}")
    public Result<Void> deleteSeat(@PathVariable("id") Long id) {
        seatAdminService.deleteSeat(id);
        return Result.success();
    }

    @Operation(summary = "5-15 分配工位", description = "负责人直接分配工位；成员已有工位时自动释放")
    @RequiresPerm("seat:seat:assign")
    @OperLog(module = "管理端-工位管理", value = "分配工位")
    @PutMapping("/admin/seat/{id}/assign")
    public Result<SeatAssignVO> putSeatAssign(@PathVariable("id") Long id,
                                              @RequestBody @Valid SeatAssignDTO dto) {
        return Result.success(seatAdminService.assignSeat(id, dto));
    }

    @Operation(summary = "5-16 释放工位", description = "解除工位与使用人的绑定，状态置为空闲")
    @RequiresPerm("seat:seat:assign")
    @OperLog(module = "管理端-工位管理", value = "释放工位")
    @PutMapping("/admin/seat/{id}/release")
    public Result<Void> putSeatRelease(@PathVariable("id") Long id) {
        seatAdminService.releaseSeat(id);
        return Result.success();
    }

    @Operation(summary = "5-17 工位状态变更", description = "变更工位状态，用于预留、维修等场景")
    @RequiresPerm("seat:seat:edit")
    @OperLog(module = "管理端-工位管理", value = "工位状态变更")
    @PutMapping("/admin/seat/{id}/status")
    public Result<Void> putSeatStatus(@PathVariable("id") Long id,
                                      @RequestBody @Valid SeatStatusDTO dto) {
        seatAdminService.changeSeatStatus(id, dto);
        return Result.success();
    }

    @Operation(summary = "5-25 平面图数据（后台）", description = "返回区域、底图尺寸、工位坐标及使用人信息")
    @RequiresPerm("seat:seat:list")
    @GetMapping("/admin/seat/plan")
    public Result<List<AdminSeatPlanVO>> getSeatPlan(@ParameterObject AdminSeatPlanQuery query) {
        return Result.success(seatAdminService.plan(query));
    }

    @Operation(summary = "5-26 平面图拖拽调整（移动 / 互换）", description = "拖到空工位即移动，拖到已占用工位即互换；即时生效")
    @RequiresPerm("seat:seat:assign")
    @OperLog(module = "管理端-工位管理", value = "平面图拖拽调整（移动 / 互换）")
    @PostMapping("/admin/seat/plan/drag")
    public Result<SeatPlanDragVO> postSeatPlanDrag(@RequestBody @Valid SeatPlanDragDTO dto) {
        return Result.success(seatAdminService.drag(dto));
    }
}