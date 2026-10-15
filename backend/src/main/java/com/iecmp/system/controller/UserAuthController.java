package com.iecmp.system.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.Result;
import com.iecmp.system.dto.TokenRefreshDTO;
import com.iecmp.system.dto.UserLoginDTO;
import com.iecmp.system.service.SysUserService;
import com.iecmp.system.vo.TokenRefreshVO;
import com.iecmp.system.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端-登录与令牌（system 模块）。
 *
 * <p>覆盖接口：1-02、1-03、1-04，共 3 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-登录与令牌")
@RestController
@RequiredArgsConstructor
public class UserAuthController {

    /** SysUser 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysUserService sysUserService;

    /**
     * 1-02 前台登录。
     *
     * <p>{@code POST /user/login} —— 成员端登录，仅允许成员角色登录；校验通过后返回双令牌与用户信息
     *
     * <p>权限：无（公开接口）；令牌：否。
     */
    @Operation(summary = "1-02 前台登录", description = "成员端登录，仅允许成员角色登录；校验通过后返回双令牌与用户信息")
    @OperLog(module = "用户端-登录与令牌", value = "前台登录")
    @PostMapping("/user/login")
    public Result<UserLoginVO> postLogin(@RequestBody @Valid UserLoginDTO dto) {
        // TODO 待实现（接口 1-02）：见《IECMP-接口开发文档-v1.6.md》接口 1-02
        return Result.todo("1-02");
    }

    /**
     * 1-03 退出登录。
     *
     * <p>{@code POST /user/logout} —— 注销当前令牌，令牌写入 Redis 黑名单直至自然过期
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "1-03 退出登录", description = "注销当前令牌，令牌写入 Redis 黑名单直至自然过期")
    @OperLog(module = "用户端-登录与令牌", value = "退出登录")
    @PostMapping("/user/logout")
    public Result<Void> postLogout() {
        // TODO 待实现（接口 1-03）：见《IECMP-接口开发文档-v1.6.md》接口 1-03
        return Result.todo("1-03");
    }

    /**
     * 1-04 刷新令牌。
     *
     * <p>{@code POST /user/token/refresh} —— 使用 refreshToken 换取新的 accessToken；refreshToken 剩余有效期不足 1 天时一并下发新 refreshToken
     *
     * <p>权限：无（凭 refreshToken）；令牌：否。
     */
    @Operation(summary = "1-04 刷新令牌", description = "使用 refreshToken 换取新的 accessToken；refreshToken 剩余有效期不足 1 天时一并下发新 refreshToken")
    @OperLog(module = "用户端-登录与令牌", value = "刷新令牌")
    @PostMapping("/user/token/refresh")
    public Result<TokenRefreshVO> postTokenRefresh(@RequestBody @Valid TokenRefreshDTO dto) {
        // TODO 待实现（接口 1-04）：见《IECMP-接口开发文档-v1.6.md》接口 1-04
        return Result.todo("1-04");
    }
}
