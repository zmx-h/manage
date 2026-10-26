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
import com.iecmp.seat.service.SeatAreaService;
import com.iecmp.seat.service.SeatInfoService;
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

/**
 * 管理端-工位管理（seat 模块）。
 *
 * <p>覆盖接口：5-11、5-12、5-13、5-14、5-15、5-16、5-17、5-25、5-26，共 9 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-工位管理")
@RestController
@RequiredArgsConstructor
public class AdminSeatController {

    /** SeatInfo 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SeatInfoService seatInfoService;
    /** SeatArea 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SeatAreaService seatAreaService;

    /**
     * 5-11 新增工位。
     *
     * <p>{@code POST /admin/seat} —— 在指定区域内新增工位并标注坐标
     *
     * <p>权限：seat:seat:add；令牌：是。
     */
    @Operation(summary = "5-11 新增工位", description = "在指定区域内新增工位并标注坐标")
    @RequiresPerm("seat:seat:add")
    @OperLog(module = "管理端-工位管理", value = "新增工位")
    @PostMapping("/admin/seat")
    public Result<SeatCreateVO> postSeat(@RequestBody @Valid SeatAddDTO dto) {
        return Result.success(seatInfoService.createSeat(dto));
    }

    /**
     * 5-12 修改工位。
     *
     * <p>{@code PUT /admin/seat} —— 修改工位编号、类型、坐标与备注；不改变使用人
     *
     * <p>权限：seat:seat:edit；令牌：是。
     */
    @Operation(summary = "5-12 修改工位", description = "修改工位编号、类型、坐标与备注；不改变使用人")
    @RequiresPerm("seat:seat:edit")
    @OperLog(module = "管理端-工位管理", value = "修改工位")
    @PutMapping("/admin/seat")
    public Result<Void> putSeat(@RequestBody @Valid SeatUpdateDTO dto) {
        seatInfoService.updateSeat(dto);
        return Result.success();
    }

    /**
     * 5-13 批量保存工位布局坐标。
     *
     * <p>{@code PUT /admin/seat/batch} —— 一次性保存同一区域内多个工位的坐标，用于平面图布局编辑器的「保存」动作；只改坐标，不涉及使用人
     *
     * <p>权限：seat:seat:edit；令牌：是。
     */
    @Operation(summary = "5-13 批量保存工位布局坐标", description = "一次性保存同一区域内多个工位的坐标，用于平面图布局编辑器的「保存」动作；只改坐标，不涉及使用人")
    @RequiresPerm("seat:seat:edit")
    @OperLog(module = "管理端-工位管理", value = "批量保存工位布局坐标")
    @PutMapping("/admin/seat/batch")
    public Result<SeatBatchVO> putSeatBatch(@RequestBody @Valid SeatBatchDTO dto) {
        return Result.success(seatInfoService.batchUpdateSeats(dto));
    }

    /**
     * 5-14 删除工位。
     *
     * <p>{@code DELETE /admin/seat/{id}} —— 逻辑删除工位；工位仍被占用时禁止删除
     *
     * <p>权限：seat:seat:remove；令牌：是。
     */
    @Operation(summary = "5-14 删除工位", description = "逻辑删除工位；工位仍被占用时禁止删除")
    @RequiresPerm("seat:seat:remove")
    @OperLog(module = "管理端-工位管理", value = "删除工位")
    @DeleteMapping("/admin/seat/{id}")
    public Result<Void> deleteSeat(@PathVariable("id") Long id) {
        seatInfoService.deleteSeat(id);
        return Result.success();
    }

    /**
     * 5-15 分配工位。
     *
     * <p>{@code PUT /admin/seat/{id}/assign} —— 负责人直接把工位分配给指定成员；若该成员已有其他工位则自动释放原工位
     *
     * <p>权限：seat:seat:assign；令牌：是。
     */
    @Operation(summary = "5-15 分配工位", description = "负责人直接把工位分配给指定成员；若该成员已有其他工位则自动释放原工位")
    @RequiresPerm("seat:seat:assign")
    @OperLog(module = "管理端-工位管理", value = "分配工位")
    @PutMapping("/admin/seat/{id}/assign")
    public Result<SeatAssignVO> putSeatAssign(@PathVariable("id") Long id, @RequestBody @Valid SeatAssignDTO dto) {
        return Result.success(seatInfoService.assignSeat(id, dto.getUserId()));
    }

    /**
     * 5-16 释放工位。
     *
     * <p>{@code PUT /admin/seat/{id}/release} —— 解除工位与使用人的绑定，状态置为「空闲」
     *
     * <p>权限：seat:seat:assign；令牌：是。
     */
    @Operation(summary = "5-16 释放工位", description = "解除工位与使用人的绑定，状态置为「空闲」")
    @RequiresPerm("seat:seat:assign")
    @OperLog(module = "管理端-工位管理", value = "释放工位")
    @PutMapping("/admin/seat/{id}/release")
    public Result<Void> putSeatRelease(@PathVariable("id") Long id) {
        seatInfoService.releaseSeat(id);
        return Result.success();
    }

    /**
     * 5-17 工位状态变更。
     *
     * <p>{@code PUT /admin/seat/{id}/status} —— 变更工位状态，用于预留、维修等场景
     *
     * <p>权限：seat:seat:edit；令牌：是。
     */
    @Operation(summary = "5-17 工位状态变更", description = "变更工位状态，用于预留、维修等场景")
    @RequiresPerm("seat:seat:edit")
    @OperLog(module = "管理端-工位管理", value = "工位状态变更")
    @PutMapping("/admin/seat/{id}/status")
    public Result<Void> putSeatStatus(@PathVariable("id") Long id, @RequestBody @Valid SeatStatusDTO dto) {
        seatInfoService.updateSeatStatus(id, dto);
        return Result.success();
    }

    /**
     * 5-25 平面图数据（后台）。
     *
     * <p>{@code GET /admin/seat/plan} —— 一次返回区域、底图尺寸与全部工位坐标及使用人信息，供管理端渲染可拖拽平面图
     *
     * <p>权限：seat:seat:list；令牌：是。
     */
    @Operation(summary = "5-25 平面图数据（后台）", description = "一次返回区域、底图尺寸与全部工位坐标及使用人信息，供管理端渲染可拖拽平面图")
    @RequiresPerm("seat:seat:list")
    @GetMapping("/admin/seat/plan")
    public Result<AdminSeatPlanVO> getSeatPlan(@ParameterObject AdminSeatPlanQuery query) {
        // TODO 待实现（接口 5-25）：见《IECMP-接口开发文档-v1.6.md》接口 5-25
        return Result.todo("5-25");
    }

    /**
     * 5-26 平面图拖拽调整（移动 / 互换）。
     *
     * <p>{@code POST /admin/seat/plan/drag} —— 管理员在平面图上拖动使用人：拖到空工位即移动，拖到已占用工位即互换；即时生效
     *
     * <p>权限：seat:seat:assign；令牌：是。
     */
    @Operation(summary = "5-26 平面图拖拽调整（移动 / 互换）", description = "管理员在平面图上拖动使用人：拖到空工位即移动，拖到已占用工位即互换；即时生效")
    @RequiresPerm("seat:seat:assign")
    @OperLog(module = "管理端-工位管理", value = "平面图拖拽调整（移动 / 互换）")
    @PostMapping("/admin/seat/plan/drag")
    public Result<SeatPlanDragVO> postSeatPlanDrag(@RequestBody @Valid SeatPlanDragDTO dto) {
        // TODO 待实现（接口 5-26）：见《IECMP-接口开发文档-v1.6.md》接口 5-26
        return Result.todo("5-26");
    }
}
