package com.iecmp.common.security;

import com.iecmp.common.context.LoginUser;
import io.jsonwebtoken.Claims;

/**
 * 令牌服务：签发、解析、黑名单与用户状态装配（架构文档 4.1 节）。
 *
 * <p>放在 {@code common/security} 下，作为鉴权基础设施被 {@code AuthInterceptor}
 * 与各模块的登录接口共用。
 */
public interface TokenService {

    /** 组装令牌响应所需的两枚令牌。 */
    TokenPair issue(Long userId, String username, String realName, LoginUser loginUser);

    /** 解析 accessToken，非法或已过期抛 401；命中黑名单同样抛 401。 */
    LoginUser resolveAccessToken(String accessToken);

    /** 校验 refreshToken 并返回其载荷；非法、过期、类型不符或命中黑名单都会抛错。 */
    Claims verifyRefreshToken(String refreshToken);

    /** 把令牌加入黑名单，TTL 取令牌剩余有效期（退出登录用）。 */
    void blacklist(String token);

    /** 按用户 ID 重新装配登录用户（角色、权限、禁用状态），供令牌刷新与请求鉴权使用。 */
    LoginUser loadLoginUser(Long userId);

    /** 双令牌。 */
    record TokenPair(String accessToken, String refreshToken, String tokenType, Long expiresIn) {
    }
}
