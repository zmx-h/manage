package com.iecmp.common.util;

import com.iecmp.common.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 工具：双令牌的签发与解析（架构文档 4.1 节）。
 *
 * <p>令牌载荷约定：{@code sub} = 用户 ID，{@code username} 为登录账号，
 * {@code type} 区分 {@code access} / {@code refresh}，{@code jti} 为令牌唯一标识。
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    /** 令牌类型：访问令牌。 */
    public static final String TYPE_ACCESS = "access";

    /** 令牌类型：刷新令牌。 */
    public static final String TYPE_REFRESH = "refresh";

    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_TYPE = "type";

    private final JwtProperties jwtProperties;

    /** 签发 accessToken。 */
    public String createAccessToken(Long userId, String username) {
        return createToken(userId, username, TYPE_ACCESS, jwtProperties.getAccessTokenExpire());
    }

    /** 签发 refreshToken。 */
    public String createRefreshToken(Long userId, String username) {
        return createToken(userId, username, TYPE_REFRESH, jwtProperties.getRefreshTokenExpire());
    }

    private String createToken(Long userId, String username, String type, Long expireSeconds) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(userId))
                .claim(CLAIM_USERNAME, username)
                .claim(CLAIM_TYPE, type)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expireSeconds * 1000L))
                .signWith(secretKey())
                .compact();
    }

    /**
     * 解析令牌，签名错误或已过期都会抛出 {@link JwtException}。
     */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 宽容解析：令牌非法时返回 null，用于「令牌可选」的公开接口。
     */
    public Claims parseQuietly(String token) {
        try {
            return parse(token);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    /** 取用户 ID。 */
    public Long getUserId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    /** 取登录账号。 */
    public String getUsername(Claims claims) {
        return claims.get(CLAIM_USERNAME, String.class);
    }

    /** 取令牌类型。 */
    public String getType(Claims claims) {
        return claims.get(CLAIM_TYPE, String.class);
    }

    /** 令牌剩余有效秒数，已过期返回 0。 */
    public long getRemainSeconds(Claims claims) {
        Date expiration = claims.getExpiration();
        if (expiration == null) {
            return 0L;
        }
        long remain = (expiration.getTime() - System.currentTimeMillis()) / 1000L;
        return Math.max(remain, 0L);
    }

    private SecretKey secretKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
