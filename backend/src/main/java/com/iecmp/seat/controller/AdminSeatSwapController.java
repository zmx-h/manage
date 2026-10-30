package com.iecmp.seat.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.seat.dto.SeatSwapAuditDTO;
import com.iecmp.seat.query.SeatSwapPageQuery;
import com.iecmp.seat.service.SeatAdminService;
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

/** 管理端-调换申请审核（5-23 ~ 5-24）。 */
@Tag(name = "管理端-调换申请审核")
@RestController
@RequiredArgsConstructor
public class AdminSeatSwapController {

    private final SeatAdminService seatAdminService;

    @Operation(summary = "5-23 调换申请分页（后台）", description = "管理端分页查询座位调换申请")
    @RequiresPerm("seat:swap:list")
    @GetMapping("/admin/seat/swap/page")
    public Result<PageResult<SeatSwapPageVO>> getSeatSwapPage(
            @ParameterObject SeatSwapPageQuery query) {
        return Result.success(seatAdminService.swapPage(query));
    }

    @Operation(summary = "5-24 调换申请审核（负责人）", description = "审核已获对方同意的调换申请；通过时交换双方工位")
    @RequiresPerm("seat:swap:audit")
    @OperLog(module = "管理端-调换申请审核", value = "调换申请审核（负责人）")
    @PutMapping("/admin/seat/swap/{id}/audit")
    public Result<Void> putSeatSwapAudit(@PathVariable("id") Long id,
                                         @RequestBody @Valid SeatSwapAuditDTO dto) {
        seatAdminService.auditSwap(id, dto, LoginUserUtil.getUserId());
        return Result.success();
    }
}