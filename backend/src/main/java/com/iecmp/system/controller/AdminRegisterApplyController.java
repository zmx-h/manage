package com.iecmp.system.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.system.dto.RegisterApplyAuditDTO;
import com.iecmp.system.query.RegisterApplyPageQuery;
import com.iecmp.system.service.RegisterService;
import com.iecmp.system.vo.RegisterApplyAuditVO;
import com.iecmp.system.vo.RegisterApplyPageVO;
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
 * 管理端-注册申请审核（system 模块）。
 *
 * <p>覆盖接口：1-25、1-26，共 2 个。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-注册申请审核")
@RestController
@RequiredArgsConstructor
public class AdminRegisterApplyController {

    private final RegisterService registerService;

    /**
     * 1-25 注册申请分页。
     *
     * <p>{@code GET /admin/register/apply/page} —— 分页查询注册申请，支持按状态与时间筛选
     *
     * <p>权限：system:register:list；令牌：是；分页返回。
     */
    @Operation(summary = "1-25 注册申请分页", description = "分页查询注册申请，支持按状态与时间筛选")
    @RequiresPerm("system:register:list")
    @GetMapping("/admin/register/apply/page")
    public Result<PageResult<RegisterApplyPageVO>> getRegisterApplyPage(
            @ParameterObject RegisterApplyPageQuery query) {
        return Result.success(registerService.pageApply(query));
    }

    /**
     * 1-26 注册申请审核。
     *
     * <p>{@code PUT /admin/register/apply/{id}/audit} —— 审核注册申请；通过时自动创建账号、分配角色并返回初始密码
     *
     * <p>权限：system:register:audit；令牌：是。
     */
    @Operation(summary = "1-26 注册申请审核", description = "审核注册申请；通过时自动创建账号、分配角色并返回初始密码")
    @RequiresPerm("system:register:audit")
    @OperLog(module = "管理端-注册申请审核", value = "注册申请审核")
    @PutMapping("/admin/register/apply/{id}/audit")
    public Result<RegisterApplyAuditVO> putRegisterApplyAudit(@PathVariable("id") Long id,
            @RequestBody @Valid RegisterApplyAuditDTO dto) {
        return Result.success(registerService.auditApply(id, dto));
    }
}