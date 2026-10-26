package com.iecmp.seat.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.Result;
import com.iecmp.seat.dto.SeatAreaAddDTO;
import com.iecmp.seat.dto.SeatAreaUpdateDTO;
import com.iecmp.seat.service.SeatAreaService;
import com.iecmp.seat.service.SeatInfoService;
import com.iecmp.seat.vo.SeatAreaCreateVO;
import com.iecmp.seat.vo.SeatAreaListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-区域与平面图（seat 模块）。
 *
 * <p>覆盖接口：5-07、5-08、5-09、5-10，共 4 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-区域与平面图")
@RestController
@RequiredArgsConstructor
public class AdminSeatAreaController {

    /** SeatArea 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SeatAreaService seatAreaService;
    /** SeatInfo 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SeatInfoService seatInfoService;

    /**
     * 5-07 区域列表（后台）。
     *
     * <p>{@code GET /admin/seat/area/list} —— 查询全部区域（含未启用），用于工位表单的区域选择与布局编辑器区域切换
     *
     * <p>权限：seat:area:list；令牌：是。
     */
    @Operation(summary = "5-07 区域列表（后台）", description = "查询全部区域（含未启用），用于工位表单的区域选择与布局编辑器区域切换")
    @RequiresPerm("seat:area:list")
    @GetMapping("/admin/seat/area/list")
    public Result<List<SeatAreaListVO>> getSeatAreaList() {
        return Result.success(seatAreaService.getAreaList());
    }

    /**
     * 5-08 新增区域（上传平面图）。
     *
     * <p>{@code POST /admin/seat/area} —— 新增区域并关联平面图底图；`width` / `height` 是工位坐标换算的基准，必须填写
     *
     * <p>权限：seat:area:add；令牌：是。
     */
    @Operation(summary = "5-08 新增区域（上传平面图）", description = "新增区域并关联平面图底图；`width` / `height` 是工位坐标换算的基准，必须填写")
    @RequiresPerm("seat:area:add")
    @OperLog(module = "管理端-区域与平面图", value = "新增区域（上传平面图）")
    @PostMapping("/admin/seat/area")
    public Result<SeatAreaCreateVO> postSeatArea(@RequestBody @Valid SeatAreaAddDTO dto) {
        return Result.success(seatAreaService.createArea(dto));
    }

    /**
     * 5-09 修改区域。
     *
     * <p>{@code PUT /admin/seat/area} —— 修改区域信息或更换底图；更换底图且尺寸变化时需同步复核工位坐标
     *
     * <p>权限：seat:area:edit；令牌：是。
     */
    @Operation(summary = "5-09 修改区域", description = "修改区域信息或更换底图；更换底图且尺寸变化时需同步复核工位坐标")
    @RequiresPerm("seat:area:edit")
    @OperLog(module = "管理端-区域与平面图", value = "修改区域")
    @PutMapping("/admin/seat/area")
    public Result<Void> putSeatArea(@RequestBody @Valid SeatAreaUpdateDTO dto) {
        seatAreaService.updateArea(dto);
        return Result.success();
    }

    /**
     * 5-10 删除区域。
     *
     * <p>{@code DELETE /admin/seat/area/{id}} —— 逻辑删除区域；区域下存在工位时禁止删除
     *
     * <p>权限：seat:area:remove；令牌：是。
     */
    @Operation(summary = "5-10 删除区域", description = "逻辑删除区域；区域下存在工位时禁止删除")
    @RequiresPerm("seat:area:remove")
    @OperLog(module = "管理端-区域与平面图", value = "删除区域")
    @DeleteMapping("/admin/seat/area/{id}")
    public Result<Void> deleteSeatArea(@PathVariable("id") Long id) {
        seatAreaService.deleteArea(id);
        return Result.success();
    }
}
