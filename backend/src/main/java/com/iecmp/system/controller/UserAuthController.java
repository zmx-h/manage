package com.iecmp.system.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.Result;
import com.iecmp.common.util.JwtUtil;
import com.iecmp.system.dto.TokenRefreshDTO;
import com.iecmp.system.dto.UserLoginDTO;
import com.iecmp.system.entity.SysUser;
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
 */
@Tag(name = "用户端-登录与令牌")
@RestController
@RequiredArgsConstructor
public class UserAuthController {

    private final SysUserService sysUserService;

    // 注入我们刚才写的 JwtUtil 工具
    private final JwtUtil jwtUtil;

    /**
     * 1-02 前台登录。
     */
    @Operation(summary = "1-02 前台登录", description = "成员端登录，仅允许成员角色登录；校验通过后返回双令牌与用户信息")
    @OperLog(module = "用户端-登录与令牌", value = "前台登录")
    @PostMapping("/user/login")
    public Result<UserLoginVO> postLogin(@RequestBody @Valid UserLoginDTO dto) {
        // 1. 查数据库
        SysUser user = sysUserService.getByUsername(dto.getUsername());
        if (user == null) {
            return Result.fail(10001, "账号不存在");
        }

        // 2. 校验密码（这里假设数据库里存的是明文密码）
        if (!user.getPassword().equals(dto.getPassword())) {
            return Result.fail(10003, "用户名或密码错误");
        }

        // 3. 生成 Token
        String accesssToken = jwtUtil.createToken(user.getId(), user.getUsername());

        String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getUsername());
        // 4. 组装返回给前端的 VO（如果你项目里的 UserLoginVO 字段名不一样，请根据实际字段修改 setter）
        UserLoginVO vo = new UserLoginVO();
        vo.setAccessToken(accesssToken);
        vo.setRefreshToken(refreshToken);
        vo.setTokenType("Bearer");
        vo.setExpiresIn(7200);

        // 如果 VO 里包含用户信息对象（比如 userInfo），你需要在这里把 user 的字段 set 进去。
        // 这里假设你的 UserLoginVO 包含了直接返回的字段（或者不需要给 userInfo，前端只要能拿到 token 就行）

        return Result.success(vo);
    }

    /**
     * 1-03 退出登录。
     */
    @Operation(summary = "1-03 退出登录", description = "注销当前令牌，令牌写入 Redis 黑名单直至自然过期")
    @OperLog(module = "用户端-登录与令牌", value = "退出登录")
    @PostMapping("/user/logout")
    public Result<Void> postLogout() {
        return Result.todo("1-03");
    }

    /**
     * 1-04 刷新令牌。
     */
    @Operation(summary = "1-04 刷新令牌", description = "使用 refreshToken 换取新的 accessToken；refreshToken 剩余有效期不足 1 天时一并下发新 refreshToken")
    @OperLog(module = "用户端-登录与令牌", value = "刷新令牌")
    @PostMapping("/user/token/refresh")
    public Result<TokenRefreshVO> postTokenRefresh(@RequestBody @Valid TokenRefreshDTO dto) {
        return Result.todo("1-04");
    }
}