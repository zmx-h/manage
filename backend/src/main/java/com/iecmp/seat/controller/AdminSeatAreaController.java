package com.iecmp.seat.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.Result;
import com.iecmp.seat.dto.SeatAreaAddDTO;
import com.iecmp.seat.dto.SeatAreaUpdateDTO;
import com.iecmp.seat.service.SeatAdminService;
import com.iecmp.seat.vo.SeatAreaCreateVO;
import com.iecmp.seat.vo.SeatAreaListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 管理端-区域与平面图（5-07 ~ 5-10）。 */
@Tag(name = "管理端-区域与平面图")
@RestController
@RequiredArgsConstructor
public class AdminSeatAreaController {

    private final SeatAdminService seatAdminService;

    @Operation(summary = "5-07 区域列表（后台）", description = "查询全部区域，用于工位表单与布局编辑器")
    @RequiresPerm("seat:area:list")
    @GetMapping("/admin/seat/area/list")
    public Result<List<SeatAreaListVO>> getSeatAreaList() {
        return Result.success(seatAdminService.areaList());
    }

    @Operation(summary = "5-08 新增区域（上传平面图）", description = "新增区域并关联平面图底图")
    @RequiresPerm("seat:area:add")
    @OperLog(module = "管理端-区域与平面图", value = "新增区域（上传平面图）")
    @PostMapping("/admin/seat/area")
    public Result<SeatAreaCreateVO> postSeatArea(@RequestBody @Valid SeatAreaAddDTO dto) {
        return Result.success(seatAdminService.createArea(dto));
    }

    @Operation(summary = "5-09 修改区域", description = "修改区域信息或更换底图")
    @RequiresPerm("seat:area:edit")
    @OperLog(module = "管理端-区域与平面图", value = "修改区域")
    @PutMapping("/admin/seat/area")
    public Result<Void> putSeatArea(@RequestBody @Valid SeatAreaUpdateDTO dto) {
        seatAdminService.updateArea(dto);
        return Result.success();
    }

    @Operation(summary = "5-10 删除区域", description = "逻辑删除区域；区域下存在工位时禁止删除")
    @RequiresPerm("seat:area:remove")
    @OperLog(module = "管理端-区域与平面图", value = "删除区域")
    @DeleteMapping("/admin/seat/area/{id}")
    public Result<Void> deleteSeatArea(@PathVariable("id") Long id) {
        seatAdminService.deleteArea(id);
        return Result.success();
    }
}