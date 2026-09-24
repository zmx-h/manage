package com.iecmp.seat.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.seat.dto.SeatApplyAuditDTO;
import com.iecmp.seat.query.SeatApplyPageQuery;
import com.iecmp.seat.service.SeatApplyService;
import com.iecmp.seat.service.SeatInfoService;
import com.iecmp.seat.vo.SeatApplyPageVO;
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
 * 管理端-工位申请审核（seat 模块）。
 *
 * <p>覆盖接口：5-18、5-19，共 2 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-工位申请审核")
@RestController
@RequiredArgsConstructor
public class AdminSeatApplyController {

    /** SeatApply 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SeatApplyService seatApplyService;
    /** SeatInfo 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SeatInfoService seatInfoService;

    /**
     * 5-18 工位申请分页（后台）。
     *
     * <p>{@code GET /admin/seat/apply/page} —— 管理端分页查询工位申请，用于审核排队
     *
     * <p>权限：seat:apply:list；令牌：是；分页返回。
     */
    @Operation(summary = "5-18 工位申请分页（后台）", description = "管理端分页查询工位申请，用于审核排队")
    @RequiresPerm("seat:apply:list")
    @GetMapping("/admin/seat/apply/page")
    public Result<PageResult<SeatApplyPageVO>> getSeatApplyPage(@ParameterObject SeatApplyPageQuery query) {
        // TODO 待实现（接口 5-18）：见《IECMP-接口开发文档-v1.6.md》接口 5-18
        return Result.todo("5-18");
    }

    /**
     * 5-19 工位申请审核。
     *
     * <p>{@code PUT /admin/seat/apply/{id}/audit} —— 审核工位申请；通过时可直接指定实际分配的工位
     *
     * <p>权限：seat:apply:audit；令牌：是。
     */
    @Operation(summary = "5-19 工位申请审核", description = "审核工位申请；通过时可直接指定实际分配的工位")
    @RequiresPerm("seat:apply:audit")
    @OperLog(module = "管理端-工位申请审核", value = "工位申请审核")
    @PutMapping("/admin/seat/apply/{id}/audit")
    public Result<Void> putSeatApplyAudit(@PathVariable("id") Long id, @RequestBody @Valid SeatApplyAuditDTO dto) {
        // TODO 待实现（接口 5-19）：见《IECMP-接口开发文档-v1.6.md》接口 5-19
        return Result.todo("5-19");
    }
}
