package com.iecmp.honor.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.honor.dto.AdminHonorAlumniAddDTO;
import com.iecmp.honor.dto.AdminHonorAlumniUpdateDTO;
import com.iecmp.honor.dto.HonorAlumniAuditDTO;
import com.iecmp.honor.dto.HonorAlumniSortDTO;
import com.iecmp.honor.dto.HonorAlumniStatusDTO;
import com.iecmp.honor.query.AdminHonorAlumniPageQuery;
import com.iecmp.honor.query.HonorAlumniAuditPageQuery;
import com.iecmp.honor.service.HonorAlumniAdminService;
import com.iecmp.honor.vo.AdminHonorAlumniCreateVO;
import com.iecmp.honor.vo.AdminHonorAlumniPageVO;
import com.iecmp.honor.vo.HonorAlumniAuditPageVO;
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
 * 管理端-学长学姐管理。
 *
 * <p>覆盖接口：6-05、6-06、6-07、6-08、6-09、6-10、6-17、6-18，共 8 个。
 */
@Tag(name = "管理端-学长学姐管理")
@RestController
@RequiredArgsConstructor
public class AdminHonorAlumniController {

    private final HonorAlumniAdminService honorAlumniAdminService;

    @Operation(summary = "6-05 学长分页（后台）", description = "管理端分页查询全部学长学姐信息（含待审核、已下板），用于管理列表")
    @RequiresPerm("honor:alumni:list")
    @GetMapping("/admin/honor/alumni/page")
    public Result<PageResult<AdminHonorAlumniPageVO>> getHonorAlumniPage(
            @ParameterObject AdminHonorAlumniPageQuery query) {
        return Result.success(honorAlumniAdminService.page(query));
    }

    @Operation(summary = "6-06 新增学长", description = "管理员直接录入学长学姐信息，录入后状态为「已通过」，可立即上板")
    @RequiresPerm("honor:alumni:add")
    @OperLog(module = "管理端-学长学姐管理", value = "新增学长")
    @PostMapping("/admin/honor/alumni")
    public Result<AdminHonorAlumniCreateVO> postHonorAlumni(
            @RequestBody @Valid AdminHonorAlumniAddDTO dto) {
        return Result.success(honorAlumniAdminService.add(dto));
    }

    @Operation(summary = "6-07 修改学长", description = "修改学长学姐信息，支持调整展示排序")
    @RequiresPerm("honor:alumni:edit")
    @OperLog(module = "管理端-学长学姐管理", value = "修改学长")
    @PutMapping("/admin/honor/alumni")
    public Result<Void> putHonorAlumni(@RequestBody @Valid AdminHonorAlumniUpdateDTO dto) {
        honorAlumniAdminService.update(dto);
        return Result.success();
    }

    @Operation(summary = "6-08 删除学长", description = "逻辑删除学长学姐信息，同时逻辑删除其下全部荣誉记录")
    @RequiresPerm("honor:alumni:remove")
    @OperLog(module = "管理端-学长学姐管理", value = "删除学长")
    @DeleteMapping("/admin/honor/alumni/{id}")
    public Result<Void> deleteHonorAlumni(@PathVariable("id") Long id) {
        honorAlumniAdminService.delete(id);
        return Result.success();
    }

    @Operation(summary = "6-09 上板 / 下板", description = "控制某条信息是否在前台展板展示")
    @RequiresPerm("honor:alumni:edit")
    @OperLog(module = "管理端-学长学姐管理", value = "上板/下板")
    @PutMapping("/admin/honor/alumni/{id}/status")
    public Result<Void> putHonorAlumniStatus(@PathVariable("id") Long id,
                                             @RequestBody @Valid HonorAlumniStatusDTO dto) {
        honorAlumniAdminService.updateShowStatus(id, dto);
        return Result.success();
    }

    @Operation(summary = "6-10 调整展示排序", description = "调整某条信息的展示排序号，数值越小越靠前")
    @RequiresPerm("honor:alumni:edit")
    @OperLog(module = "管理端-学长学姐管理", value = "调整展示排序")
    @PutMapping("/admin/honor/alumni/{id}/sort")
    public Result<Void> putHonorAlumniSort(@PathVariable("id") Long id,
                                           @RequestBody @Valid HonorAlumniSortDTO dto) {
        honorAlumniAdminService.updateSort(id, dto);
        return Result.success();
    }

    @Operation(summary = "6-17 待审核信息分页（后台）", description = "分页查询成员提交的待审核信息，用于审核排队")
    @RequiresPerm("honor:alumni:audit")
    @GetMapping("/admin/honor/alumni/audit/page")
    public Result<PageResult<HonorAlumniAuditPageVO>> getHonorAlumniAuditPage(
            @ParameterObject HonorAlumniAuditPageQuery query) {
        return Result.success(honorAlumniAdminService.auditPage(query));
    }

    @Operation(summary = "6-18 审核通过 / 驳回", description = "审核成员提交的学长学姐信息；通过后可选择直接上板")
    @RequiresPerm("honor:alumni:audit")
    @OperLog(module = "管理端-学长学姐管理", value = "审核学长信息")
    @PutMapping("/admin/honor/alumni/{id}/audit")
    public Result<Void> putHonorAlumniAudit(@PathVariable("id") Long id,
                                            @RequestBody @Valid HonorAlumniAuditDTO dto) {
        honorAlumniAdminService.audit(id, dto);
        return Result.success();
    }
}