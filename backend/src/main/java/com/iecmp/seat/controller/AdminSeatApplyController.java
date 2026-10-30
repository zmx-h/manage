package com.iecmp.seat.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.seat.dto.SeatApplyAuditDTO;
import com.iecmp.seat.query.SeatApplyPageQuery;
import com.iecmp.seat.service.SeatAdminService;
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

/** 管理端-工位申请审核（5-18 ~ 5-19）。 */
@Tag(name = "管理端-工位申请审核")
@RestController
@RequiredArgsConstructor
public class AdminSeatApplyController {

    private final SeatAdminService seatAdminService;

    @Operation(summary = "5-18 工位申请分页（后台）", description = "管理端分页查询工位申请，用于审核排队")
    @RequiresPerm("seat:apply:list")
    @GetMapping("/admin/seat/apply/page")
    public Result<PageResult<SeatApplyPageVO>> getSeatApplyPage(
            @ParameterObject SeatApplyPageQuery query) {
        return Result.success(seatAdminService.applyPage(query));
    }

    @Operation(summary = "5-19 工位申请审核", description = "审核工位申请；通过时可直接指定实际分配的工位")
    @RequiresPerm("seat:apply:audit")
    @OperLog(module = "管理端-工位申请审核", value = "工位申请审核")
    @PutMapping("/admin/seat/apply/{id}/audit")
    public Result<Void> putSeatApplyAudit(@PathVariable("id") Long id,
                                          @RequestBody @Valid SeatApplyAuditDTO dto) {
        seatAdminService.auditApply(id, dto, LoginUserUtil.getUserId());
        return Result.success();
    }
}