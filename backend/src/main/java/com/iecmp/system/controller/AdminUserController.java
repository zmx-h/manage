package com.iecmp.system.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.system.dto.UserAddDTO;
import com.iecmp.system.dto.UserPasswordResetDTO;
import com.iecmp.system.dto.UserStatusDTO;
import com.iecmp.system.dto.UserUpdateDTO;
import com.iecmp.system.query.UserPageQuery;
import com.iecmp.system.service.UserAdminService;
import com.iecmp.system.vo.UserCreateVO;
import com.iecmp.system.vo.UserDetailVO;
import com.iecmp.system.vo.UserPageVO;
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

/** 管理端-用户管理（1-08 ~ 1-14）。 */
@Tag(name = "管理端-用户管理")
@RestController
@RequiredArgsConstructor
public class AdminUserController {

    private final UserAdminService userAdminService;

    @Operation(summary = "1-08 用户分页查询", description = "分页查询用户列表，支持按姓名 / 账号 / 学号模糊搜索与状态、角色筛选")
    @RequiresPerm("system:user:list")
    @GetMapping("/admin/user/page")
    public Result<PageResult<UserPageVO>> getUserPage(@ParameterObject UserPageQuery query) {
        return Result.success(userAdminService.page(query));
    }

    @Operation(summary = "1-09 用户详情", description = "查询指定用户详情，含角色 ID 列表，用于编辑回显")
    @RequiresPerm("system:user:query")
    @GetMapping("/admin/user/{id}")
    public Result<UserDetailVO> getUser(@PathVariable("id") Long id) {
        return Result.success(userAdminService.detail(id));
    }

    @Operation(summary = "1-10 新增用户", description = "由负责人创建成员账号，初始密码由后台配置（默认 123456），首次登录建议修改")
    @RequiresPerm("system:user:add")
    @OperLog(module = "管理端-用户管理", value = "新增用户")
    @PostMapping("/admin/user")
    public Result<UserCreateVO> postUser(@RequestBody @Valid UserAddDTO dto) {
        return Result.success(userAdminService.create(dto, LoginUserUtil.isSuperAdmin()));
    }

    @Operation(summary = "1-11 修改用户", description = "修改用户基础信息与角色，不允许修改登录账号")
    @RequiresPerm("system:user:edit")
    @OperLog(module = "管理端-用户管理", value = "修改用户")
    @PutMapping("/admin/user")
    public Result<Void> putUser(@RequestBody @Valid UserUpdateDTO dto) {
        userAdminService.update(dto, LoginUserUtil.isSuperAdmin());
        return Result.success();
    }

    @Operation(summary = "1-12 启用 / 禁用用户", description = "变更用户启用状态，禁用后该用户令牌立即失效且无法登录")
    @RequiresPerm("system:user:edit")
    @OperLog(module = "管理端-用户管理", value = "启用 / 禁用用户")
    @PutMapping("/admin/user/{id}/status")
    public Result<Void> putUserStatus(@PathVariable("id") Long id, @RequestBody @Valid UserStatusDTO dto) {
        userAdminService.changeStatus(id, dto.getStatus(), LoginUserUtil.isSuperAdmin());
        return Result.success();
    }

    @Operation(summary = "1-13 重置密码", description = "管理员重置指定用户密码，重置后用户需重新登录")
    @RequiresPerm("system:user:reset")
    @OperLog(module = "管理端-用户管理", value = "重置密码")
    @PutMapping("/admin/user/{id}/password/reset")
    public Result<Void> putUserPasswordReset(@PathVariable("id") Long id, @RequestBody @Valid UserPasswordResetDTO dto) {
        userAdminService.resetPassword(id, dto.getNewPassword());
        return Result.success();
    }

    @Operation(summary = "1-14 删除用户", description = "逻辑删除用户；存在未归还资产或待审核申请时禁止删除")
    @RequiresPerm("system:user:remove")
    @OperLog(module = "管理端-用户管理", value = "删除用户")
    @DeleteMapping("/admin/user/{id}")
    public Result<Void> deleteUser(@PathVariable("id") Long id) {
        userAdminService.delete(id, LoginUserUtil.isSuperAdmin());
        return Result.success();
    }
}