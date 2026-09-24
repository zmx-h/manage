package com.iecmp.system.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.Result;
import com.iecmp.system.dto.InfoUpdateDTO;
import com.iecmp.system.dto.PasswordUpdateDTO;
import com.iecmp.system.service.SysUserService;
import com.iecmp.system.vo.InfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端-个人信息（system 模块）。
 *
 * <p>覆盖接口：1-05、1-06、1-07，共 3 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-个人信息")
@RestController
@RequiredArgsConstructor
public class UserInfoController {

    /** SysUser 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysUserService sysUserService;

    /**
     * 1-05 当前用户信息。
     *
     * <p>{@code GET /user/info} —— 获取当前登录用户的基本信息、角色与权限码，用于前端渲染菜单与按钮
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "1-05 当前用户信息", description = "获取当前登录用户的基本信息、角色与权限码，用于前端渲染菜单与按钮")
    @GetMapping("/user/info")
    public Result<InfoVO> getInfo() {
        // TODO 待实现（接口 1-05）：见《IECMP-接口开发文档-v1.6.md》接口 1-05
        return Result.todo("1-05");
    }

    /**
     * 1-06 修改个人资料。
     *
     * <p>{@code PUT /user/info} —— 修改当前登录用户的个人资料，账号与角色不可自行修改
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "1-06 修改个人资料", description = "修改当前登录用户的个人资料，账号与角色不可自行修改")
    @OperLog(module = "用户端-个人信息", value = "修改个人资料")
    @PutMapping("/user/info")
    public Result<Void> putInfo(@RequestBody @Valid InfoUpdateDTO dto) {
        // TODO 待实现（接口 1-06）：见《IECMP-接口开发文档-v1.6.md》接口 1-06
        return Result.todo("1-06");
    }

    /**
     * 1-07 修改密码。
     *
     * <p>{@code PUT /user/password} —— 修改当前登录用户密码，校验原密码；修改后当前令牌失效，需重新登录
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "1-07 修改密码", description = "修改当前登录用户密码，校验原密码；修改后当前令牌失效，需重新登录")
    @OperLog(module = "用户端-个人信息", value = "修改密码")
    @PutMapping("/user/password")
    public Result<Void> putPassword(@RequestBody @Valid PasswordUpdateDTO dto) {
        // TODO 待实现（接口 1-07）：见《IECMP-接口开发文档-v1.6.md》接口 1-07
        return Result.todo("1-07");
    }
}
