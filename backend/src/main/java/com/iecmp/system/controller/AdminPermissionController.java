package com.iecmp.system.controller;

import com.iecmp.common.result.Result;
import com.iecmp.system.service.SysRolePermissionService;
import com.iecmp.system.vo.PermissionTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-权限管理（system 模块）。
 *
 * <p>覆盖接口：1-21，共 1 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-权限管理")
@RestController
@RequiredArgsConstructor
public class AdminPermissionController {

    /** SysRolePermission 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysRolePermissionService sysRolePermissionService;

    /**
     * 1-21 权限码树。
     *
     * <p>{@code GET /admin/permission/tree} —— 返回后端登记的权限码树（按模块分组），供授权界面渲染勾选框；v1 为只读数据
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "1-21 权限码树", description = "返回后端登记的权限码树（按模块分组），供授权界面渲染勾选框；v1 为只读数据")
    @GetMapping("/admin/permission/tree")
    public Result<PermissionTreeVO> getPermissionTree() {
        // TODO 待实现（接口 1-21）：见《IECMP-接口开发文档-v1.6.md》接口 1-21
        return Result.todo("1-21");
    }
}
