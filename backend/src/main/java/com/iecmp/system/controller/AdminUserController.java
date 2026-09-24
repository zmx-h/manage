package com.iecmp.system.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.system.dto.UserAddDTO;
import com.iecmp.system.dto.UserPasswordResetDTO;
import com.iecmp.system.dto.UserStatusDTO;
import com.iecmp.system.dto.UserUpdateDTO;
import com.iecmp.system.query.UserPageQuery;
import com.iecmp.system.service.SysRoleService;
import com.iecmp.system.service.SysUserRoleService;
import com.iecmp.system.service.SysUserService;
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

/**
 * 管理端-用户管理（system 模块）。
 *
 * <p>覆盖接口：1-08、1-09、1-10、1-11、1-12、1-13、1-14，共 7 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-用户管理")
@RestController
@RequiredArgsConstructor
public class AdminUserController {

    /** SysUser 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysUserService sysUserService;
    /** SysRole 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysRoleService sysRoleService;
    /** SysUserRole 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysUserRoleService sysUserRoleService;

    /**
     * 1-08 用户分页查询。
     *
     * <p>{@code GET /admin/user/page} —— 分页查询用户列表，支持按姓名 / 账号 / 学号模糊搜索与状态、角色筛选
     *
     * <p>权限：system:user:list；令牌：是；分页返回。
     */
    @Operation(summary = "1-08 用户分页查询", description = "分页查询用户列表，支持按姓名 / 账号 / 学号模糊搜索与状态、角色筛选")
    @RequiresPerm("system:user:list")
    @GetMapping("/admin/user/page")
    public Result<PageResult<UserPageVO>> getUserPage(@ParameterObject UserPageQuery query) {
        // TODO 待实现（接口 1-08）：见《IECMP-接口开发文档-v1.6.md》接口 1-08
        return Result.todo("1-08");
    }

    /**
     * 1-09 用户详情。
     *
     * <p>{@code GET /admin/user/{id}} —— 查询指定用户详情，含角色 ID 列表，用于编辑回显
     *
     * <p>权限：system:user:query；令牌：是。
     */
    @Operation(summary = "1-09 用户详情", description = "查询指定用户详情，含角色 ID 列表，用于编辑回显")
    @RequiresPerm("system:user:query")
    @GetMapping("/admin/user/{id}")
    public Result<UserDetailVO> getUser(@PathVariable("id") Long id) {
        // TODO 待实现（接口 1-09）：见《IECMP-接口开发文档-v1.6.md》接口 1-09
        return Result.todo("1-09");
    }

    /**
     * 1-10 新增用户。
     *
     * <p>{@code POST /admin/user} —— 由负责人创建成员账号，初始密码由后台配置（默认 123456），首次登录建议修改
     *
     * <p>权限：system:user:add；令牌：是。
     */
    @Operation(summary = "1-10 新增用户", description = "由负责人创建成员账号，初始密码由后台配置（默认 123456），首次登录建议修改")
    @RequiresPerm("system:user:add")
    @OperLog(module = "管理端-用户管理", value = "新增用户")
    @PostMapping("/admin/user")
    public Result<UserCreateVO> postUser(@RequestBody @Valid UserAddDTO dto) {
        // TODO 待实现（接口 1-10）：见《IECMP-接口开发文档-v1.6.md》接口 1-10
        return Result.todo("1-10");
    }

    /**
     * 1-11 修改用户。
     *
     * <p>{@code PUT /admin/user} —— 修改用户基础信息与角色，不允许修改登录账号
     *
     * <p>权限：system:user:edit；令牌：是。
     */
    @Operation(summary = "1-11 修改用户", description = "修改用户基础信息与角色，不允许修改登录账号")
    @RequiresPerm("system:user:edit")
    @OperLog(module = "管理端-用户管理", value = "修改用户")
    @PutMapping("/admin/user")
    public Result<Void> putUser(@RequestBody @Valid UserUpdateDTO dto) {
        // TODO 待实现（接口 1-11）：见《IECMP-接口开发文档-v1.6.md》接口 1-11
        return Result.todo("1-11");
    }

    /**
     * 1-12 启用 / 禁用用户。
     *
     * <p>{@code PUT /admin/user/{id}/status} —— 变更用户启用状态，禁用后该用户令牌立即失效且无法登录
     *
     * <p>权限：system:user:edit；令牌：是。
     */
    @Operation(summary = "1-12 启用 / 禁用用户", description = "变更用户启用状态，禁用后该用户令牌立即失效且无法登录")
    @RequiresPerm("system:user:edit")
    @OperLog(module = "管理端-用户管理", value = "启用 / 禁用用户")
    @PutMapping("/admin/user/{id}/status")
    public Result<Void> putUserStatus(@PathVariable("id") Long id, @RequestBody @Valid UserStatusDTO dto) {
        // TODO 待实现（接口 1-12）：见《IECMP-接口开发文档-v1.6.md》接口 1-12
        return Result.todo("1-12");
    }

    /**
     * 1-13 重置密码。
     *
     * <p>{@code PUT /admin/user/{id}/password/reset} —— 管理员重置指定用户密码，重置后用户需重新登录
     *
     * <p>权限：system:user:reset；令牌：是。
     */
    @Operation(summary = "1-13 重置密码", description = "管理员重置指定用户密码，重置后用户需重新登录")
    @RequiresPerm("system:user:reset")
    @OperLog(module = "管理端-用户管理", value = "重置密码")
    @PutMapping("/admin/user/{id}/password/reset")
    public Result<Void> putUserPasswordReset(@PathVariable("id") Long id, @RequestBody @Valid UserPasswordResetDTO dto) {
        // TODO 待实现（接口 1-13）：见《IECMP-接口开发文档-v1.6.md》接口 1-13
        return Result.todo("1-13");
    }

    /**
     * 1-14 删除用户。
     *
     * <p>{@code DELETE /admin/user/{id}} —— 逻辑删除用户；存在未归还资产或待审核申请时禁止删除
     *
     * <p>权限：system:user:remove；令牌：是。
     */
    @Operation(summary = "1-14 删除用户", description = "逻辑删除用户；存在未归还资产或待审核申请时禁止删除")
    @RequiresPerm("system:user:remove")
    @OperLog(module = "管理端-用户管理", value = "删除用户")
    @DeleteMapping("/admin/user/{id}")
    public Result<Void> deleteUser(@PathVariable("id") Long id) {
        // TODO 待实现（接口 1-14）：见《IECMP-接口开发文档-v1.6.md》接口 1-14
        return Result.todo("1-14");
    }
}
