package com.iecmp.system.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.system.dto.RoleAddDTO;
import com.iecmp.system.dto.RolePermissionUpdateDTO;
import com.iecmp.system.dto.RoleUpdateDTO;
import com.iecmp.system.query.RolePageQuery;
import com.iecmp.system.service.RoleAdminService;
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

import java.util.List;

/** 管理端-角色管理（1-15 ~ 1-20）。 */
@Tag(name = "管理端-角色管理")
@RestController
@RequiredArgsConstructor
public class AdminRoleController {

    private final RoleAdminService roleAdminService;

    @Operation(summary = "1-15 角色下拉列表", description = "查询全部启用角色，用于用户表单的角色选择；仅返回必要字段")
    @GetMapping("/admin/role/list")
    public Result<List<RoleListVO>> getRoleList() {
        return Result.success(roleAdminService.listEnabled());
    }

    @Operation(summary = "1-16 角色分页查询", description = "分页查询角色，含每个角色已分配的权限码数量")
    @RequiresPerm("system:role:list")
    @GetMapping("/admin/role/page")
    public Result<PageResult<RolePageVO>> getRolePage(@ParameterObject RolePageQuery query) {
        return Result.success(roleAdminService.page(query));
    }

    @Operation(summary = "1-17 新增角色", description = "新增角色，可同时指定权限码集合")
    @RequiresPerm("system:role:add")
    @OperLog(module = "管理端-角色管理", value = "新增角色")
    @PostMapping("/admin/role")
    public Result<RoleCreateVO> postRole(@RequestBody @Valid RoleAddDTO dto) {
        return Result.success(roleAdminService.create(dto));
    }

    @Operation(summary = "1-18 修改角色", description = "修改角色基础信息；不在此接口调整权限码")
    @RequiresPerm("system:role:edit")
    @OperLog(module = "管理端-角色管理", value = "修改角色")
    @PutMapping("/admin/role")
    public Result<Void> putRole(@RequestBody @Valid RoleUpdateDTO dto) {
        roleAdminService.update(dto);
        return Result.success();
    }

    @Operation(summary = "1-19 删除角色", description = "逻辑删除角色，已被用户引用时禁止删除")
    @RequiresPerm("system:role:remove")
    @OperLog(module = "管理端-角色管理", value = "删除角色")
    @DeleteMapping("/admin/role/{id}")
    public Result<Void> deleteRole(@PathVariable("id") Long id) {
        roleAdminService.delete(id);
        return Result.success();
    }

    @Operation(summary = "1-20 角色授权", description = "全量覆盖式分配角色权限码；传空数组表示清空权限")
    @RequiresPerm("system:role:auth")
    @OperLog(module = "管理端-角色管理", value = "角色授权")
    @PutMapping("/admin/role/{id}/permission")
    public Result<Void> putRolePermission(@PathVariable("id") Long id,
                                          @RequestBody @Valid RolePermissionUpdateDTO dto) {
        roleAdminService.assignPermissions(id, dto.getPermCodes());
        return Result.success();
    }
}