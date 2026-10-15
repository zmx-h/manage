package com.iecmp.system.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.system.dto.RoleAddDTO;
import com.iecmp.system.dto.RolePermissionUpdateDTO;
import com.iecmp.system.dto.RoleUpdateDTO;
import com.iecmp.system.query.RolePageQuery;
import com.iecmp.system.service.SysRolePermissionService;
import com.iecmp.system.service.SysRoleService;
import com.iecmp.system.service.SysUserRoleService;
import com.iecmp.system.vo.RoleCreateVO;
import com.iecmp.system.vo.RoleListVO;
import com.iecmp.system.vo.RolePageVO;
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
 * 管理端-角色管理（system 模块）。
 *
 * <p>覆盖接口：1-15、1-16、1-17、1-18、1-19、1-20，共 6 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-角色管理")
@RestController
@RequiredArgsConstructor
public class AdminRoleController {

    /** SysRole 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysRoleService sysRoleService;
    /** SysRolePermission 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysRolePermissionService sysRolePermissionService;
    /** SysUserRole 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysUserRoleService sysUserRoleService;

    /**
     * 1-15 角色下拉列表。
     *
     * <p>{@code GET /admin/role/list} —— 查询全部启用角色，用于用户表单的角色选择；仅返回必要字段
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "1-15 角色下拉列表", description = "查询全部启用角色，用于用户表单的角色选择；仅返回必要字段")
    @GetMapping("/admin/role/list")
    public Result<RoleListVO> getRoleList() {
        // TODO 待实现（接口 1-15）：见《IECMP-接口开发文档-v1.6.md》接口 1-15
        return Result.todo("1-15");
    }

    /**
     * 1-16 角色分页查询。
     *
     * <p>{@code GET /admin/role/page} —— 分页查询角色，含每个角色已分配的权限码数量
     *
     * <p>权限：system:role:list；令牌：是；分页返回。
     */
    @Operation(summary = "1-16 角色分页查询", description = "分页查询角色，含每个角色已分配的权限码数量")
    @RequiresPerm("system:role:list")
    @GetMapping("/admin/role/page")
    public Result<PageResult<RolePageVO>> getRolePage(@ParameterObject RolePageQuery query) {
        // TODO 待实现（接口 1-16）：见《IECMP-接口开发文档-v1.6.md》接口 1-16
        return Result.todo("1-16");
    }

    /**
     * 1-17 新增角色。
     *
     * <p>{@code POST /admin/role} —— 新增角色，可同时指定权限码集合
     *
     * <p>权限：system:role:add；令牌：是。
     */
    @Operation(summary = "1-17 新增角色", description = "新增角色，可同时指定权限码集合")
    @RequiresPerm("system:role:add")
    @OperLog(module = "管理端-角色管理", value = "新增角色")
    @PostMapping("/admin/role")
    public Result<RoleCreateVO> postRole(@RequestBody @Valid RoleAddDTO dto) {
        // TODO 待实现（接口 1-17）：见《IECMP-接口开发文档-v1.6.md》接口 1-17
        return Result.todo("1-17");
    }

    /**
     * 1-18 修改角色。
     *
     * <p>{@code PUT /admin/role} —— 修改角色基础信息；不在此接口调整权限码（见 7.4.6）
     *
     * <p>权限：system:role:edit；令牌：是。
     */
    @Operation(summary = "1-18 修改角色", description = "修改角色基础信息；不在此接口调整权限码（见 7.4.6）")
    @RequiresPerm("system:role:edit")
    @OperLog(module = "管理端-角色管理", value = "修改角色")
    @PutMapping("/admin/role")
    public Result<Void> putRole(@RequestBody @Valid RoleUpdateDTO dto) {
        // TODO 待实现（接口 1-18）：见《IECMP-接口开发文档-v1.6.md》接口 1-18
        return Result.todo("1-18");
    }

    /**
     * 1-19 删除角色。
     *
     * <p>{@code DELETE /admin/role/{id}} —— 逻辑删除角色，已被用户引用时禁止删除
     *
     * <p>权限：system:role:remove；令牌：是。
     */
    @Operation(summary = "1-19 删除角色", description = "逻辑删除角色，已被用户引用时禁止删除")
    @RequiresPerm("system:role:remove")
    @OperLog(module = "管理端-角色管理", value = "删除角色")
    @DeleteMapping("/admin/role/{id}")
    public Result<Void> deleteRole(@PathVariable("id") Long id) {
        // TODO 待实现（接口 1-19）：见《IECMP-接口开发文档-v1.6.md》接口 1-19
        return Result.todo("1-19");
    }

    /**
     * 1-20 角色授权。
     *
     * <p>{@code PUT /admin/role/{id}/permission} —— 全量覆盖式分配角色权限码；权限变更后相关用户需重新登录或前端刷新权限
     *
     * <p>权限：system:role:auth；令牌：是。
     */
    @Operation(summary = "1-20 角色授权", description = "全量覆盖式分配角色权限码；权限变更后相关用户需重新登录或前端刷新权限")
    @RequiresPerm("system:role:auth")
    @OperLog(module = "管理端-角色管理", value = "角色授权")
    @PutMapping("/admin/role/{id}/permission")
    public Result<Void> putRolePermission(@PathVariable("id") Long id, @RequestBody @Valid RolePermissionUpdateDTO dto) {
        // TODO 待实现（接口 1-20）：见《IECMP-接口开发文档-v1.6.md》接口 1-20
        return Result.todo("1-20");
    }
}
