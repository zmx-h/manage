package com.iecmp.seat.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.seat.dto.SeatSwapAuditDTO;
import com.iecmp.seat.query.SeatSwapPageQuery;
import com.iecmp.seat.service.SeatInfoService;
import com.iecmp.seat.service.SeatSwapService;
import com.iecmp.seat.vo.SeatSwapPageVO;
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
 * 管理端-调换申请审核（seat 模块）。
 *
 * <p>覆盖接口：5-23、5-24，共 2 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-调换申请审核")
@RestController
@RequiredArgsConstructor
public class AdminSeatSwapController {

    /** SeatSwap 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SeatSwapService seatSwapService;
    /** SeatInfo 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SeatInfoService seatInfoService;

    /**
     * 5-23 调换申请分页（后台）。
     *
     * <p>{@code GET /admin/seat/swap/page} —— 管理端分页查询座位调换申请，用于审核与历史追溯
     *
     * <p>权限：seat:swap:list；令牌：是；分页返回。
     */
    @Operation(summary = "5-23 调换申请分页（后台）", description = "管理端分页查询座位调换申请，用于审核与历史追溯")
    @RequiresPerm("seat:swap:list")
    @GetMapping("/admin/seat/swap/page")
    public Result<PageResult<SeatSwapPageVO>> getSeatSwapPage(@ParameterObject SeatSwapPageQuery query) {
        return Result.success(seatSwapService.pageSwap(query));
    }

    /**
     * 5-24 调换申请审核（负责人）。
     *
     * <p>{@code PUT /admin/seat/swap/{id}/audit} —— 负责人审核已获对方同意的调换申请；通过时在同一事务内交换双方工位
     *
     * <p>权限：seat:swap:audit；令牌：是。
     */
    @Operation(summary = "5-24 调换申请审核（负责人）", description = "负责人审核已获对方同意的调换申请；通过时在同一事务内交换双方工位")
    @RequiresPerm("seat:swap:audit")
    @OperLog(module = "管理端-调换申请审核", value = "调换申请审核（负责人）")
    @PutMapping("/admin/seat/swap/{id}/audit")
    public Result<Void> putSeatSwapAudit(@PathVariable("id") Long id, @RequestBody @Valid SeatSwapAuditDTO dto) {
        Long auditUserId = LoginUserUtil.getUserId();
        seatSwapService.auditSwap(id, dto, auditUserId);
        return Result.success(null);
    }
}
