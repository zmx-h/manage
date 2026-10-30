package com.iecmp.system.controller;

import com.iecmp.common.result.Result;
import com.iecmp.system.service.RoleAdminService;
import com.iecmp.system.vo.PermissionTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 管理端-权限管理（1-21）。 */
@Tag(name = "管理端-权限管理")
@RestController
@RequiredArgsConstructor
public class AdminPermissionController {

    private final RoleAdminService roleAdminService;

    @Operation(summary = "1-21 权限码树", description = "返回后端登记的权限码树（按模块分组），供授权界面渲染勾选框")
    @GetMapping("/admin/permission/tree")
    public Result<List<PermissionTreeVO>> getPermissionTree() {
        return Result.success(roleAdminService.permissionTree());
    }
}