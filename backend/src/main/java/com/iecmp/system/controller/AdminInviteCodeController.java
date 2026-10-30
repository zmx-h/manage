package com.iecmp.system.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.system.dto.InviteCodeAddDTO;
import com.iecmp.system.query.InviteCodePageQuery;
import com.iecmp.system.service.RegisterService;
import com.iecmp.system.vo.InviteCodeCreateVO;
import com.iecmp.system.vo.InviteCodePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-邀请码管理（system 模块）。
 *
 * <p>覆盖接口：1-27、1-28、1-29，共 3 个。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-邀请码管理")
@RestController
@RequiredArgsConstructor
public class AdminInviteCodeController {

    private final RegisterService registerService;

    /**
     * 1-27 邀请码分页。
     *
     * <p>{@code GET /admin/invite-code/page} —— 分页查询邀请码及其使用情况
     *
     * <p>权限：system:invite:list；令牌：是；分页返回。
     */
    @Operation(summary = "1-27 邀请码分页", description = "分页查询邀请码及其使用情况")
    @RequiresPerm("system:invite:list")
    @GetMapping("/admin/invite-code/page")
    public Result<PageResult<InviteCodePageVO>> getInviteCodePage(
            @ParameterObject InviteCodePageQuery query) {
        return Result.success(registerService.pageInvite(query));
    }

    /**
     * 1-28 生成邀请码。
     *
     * <p>{@code POST /admin/invite-code} —— 批量生成邀请码，生成后由负责人在群内分发
     *
     * <p>权限：system:invite:add；令牌：是。
     */
    @Operation(summary = "1-28 生成邀请码", description = "批量生成邀请码，生成后由负责人在群内分发")
    @RequiresPerm("system:invite:add")
    @OperLog(module = "管理端-邀请码管理", value = "生成邀请码")
    @PostMapping("/admin/invite-code")
    public Result<InviteCodeCreateVO> postInviteCode(@RequestBody @Valid InviteCodeAddDTO dto) {
        return Result.success(registerService.createInvite(dto));
    }

    /**
     * 1-29 作废邀请码。
     *
     * <p>{@code DELETE /admin/invite-code/{id}} —— 作废邀请码，作废后不可再用于注册
     *
     * <p>权限：system:invite:remove；令牌：是。
     */
    @Operation(summary = "1-29 作废邀请码", description = "作废邀请码，作废后不可再用于注册")
    @RequiresPerm("system:invite:remove")
    @OperLog(module = "管理端-邀请码管理", value = "作废邀请码")
    @DeleteMapping("/admin/invite-code/{id}")
    public Result<Void> deleteInviteCode(@PathVariable("id") Long id) {
        registerService.disableInvite(id);
        return Result.success();
    }
}