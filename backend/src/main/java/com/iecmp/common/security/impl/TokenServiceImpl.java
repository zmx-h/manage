package com.iecmp.common.security.impl;

import com.iecmp.common.constant.CacheKeyConstants;
import com.iecmp.common.context.LoginUser;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.properties.JwtProperties;
import com.iecmp.common.result.ResultCode;
import com.iecmp.common.security.TokenService;
import com.iecmp.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 令牌服务实现（骨架）。
 *
 * <p><b>待实现</b>：{@link #loadLoginUser(Long)} 需要查询 {@code sys_user}、
 * {@code sys_role}、{@code sys_role_permission} 三张表装配角色与权限码集合。
 * 为避免 {@code common} 依赖 {@code system} 模块的 Mapper，此处通过
 * {@code LoginUserLoader} 扩展点解耦（见类中的 TODO），实现时由 system 模块提供实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public TokenPair issue(Long userId, String username, String realName, LoginUser loginUser) {
        String accessToken = jwtUtil.createAccessToken(userId, username);
        String refreshToken = jwtUtil.createRefreshToken(userId, username);
        return new TokenPair(accessToken, refreshToken, "Bearer", jwtProperties.getAccessTokenExpire());
    }

    @Override
    public LoginUser resolveAccessToken(String accessToken) {
        Claims claims;
        try {
            claims = jwtUtil.parse(accessToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        if (!JwtUtil.TYPE_ACCESS.equals(jwtUtil.getType(claims))) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        if (isBlacklisted(accessToken)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        LoginUser loginUser = loadLoginUser(jwtUtil.getUserId(claims));
        if (loginUser == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return loginUser;
    }

    @Override
    public Claims verifyRefreshToken(String refreshToken) {
        Claims claims;
        try {
            claims = jwtUtil.parse(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        if (!JwtUtil.TYPE_REFRESH.equals(jwtUtil.getType(claims)) || isBlacklisted(refreshToken)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return claims;
    }

    @Override
    public void blacklist(String token) {
        try {
            Claims claims = jwtUtil.parse(token);
            long remain = jwtUtil.getRemainSeconds(claims);
            if (remain > 0) {
                stringRedisTemplate.opsForValue().set(
                        CacheKeyConstants.TOKEN_BLACKLIST_PREFIX + token, "1", remain, TimeUnit.SECONDS);
            }
        } catch (JwtException | IllegalArgumentException e) {
            // 令牌本身已失效，无需加入黑名单
            log.debug("令牌已失效，跳过黑名单写入");
        }
    }

    @Override
    public LoginUser loadLoginUser(Long userId) {

        throw new BusinessException(ResultCode.SYSTEM_ERROR);
    }

    private boolean isBlacklisted(String token) {
        Boolean exists = stringRedisTemplate.hasKey(CacheKeyConstants.TOKEN_BLACKLIST_PREFIX + token);
        // Redis 故障时宁可拒绝服务也不放行未校验的令牌（架构文档 4.5 节设计原则 4）
        return Boolean.TRUE.equals(exists);
    }
}
