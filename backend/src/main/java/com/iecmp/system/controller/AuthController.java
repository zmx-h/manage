package com.iecmp.system.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.Result;
import com.iecmp.system.dto.AdminLoginDTO;
import com.iecmp.system.service.SysRoleService;
import com.iecmp.system.service.SysUserService;
import com.iecmp.system.vo.AdminLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-登录认证（system 模块）。
 *
 * <p>覆盖接口：1-01，共 1 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-登录认证")
@RestController
@RequiredArgsConstructor
public class AuthController {

    /** SysUser 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysUserService sysUserService;
    /** SysRole 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysRoleService sysRoleService;

    /**
     * 1-01 后台登录。
     *
     * <p>{@code POST /admin/login} —— 管理端账号登录，校验通过后返回双令牌与用户信息
     *
     * <p>权限：无（公开接口）；令牌：否。
     */
    @Operation(summary = "1-01 后台登录", description = "管理端账号登录，校验通过后返回双令牌与用户信息")
    @OperLog(module = "管理端-登录认证", value = "后台登录")
    @PostMapping("/admin/login")
    public Result<AdminLoginVO> postLogin(@RequestBody @Valid AdminLoginDTO dto) {
        // TODO 待实现（接口 1-01）：见《IECMP-接口开发文档-v1.6.md》接口 1-01
        return Result.todo("1-01");
    }
}
