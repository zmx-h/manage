package com.iecmp.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expire}")
    private Long accessTokenExpire;

    @Value("${jwt.refresh-token-expire}")
    private Long refreshTokenExpire;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ================= 基础方法（你刚学的） =================

    public String createToken(Long userId, String username) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .claim("type", TYPE_ACCESS)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpire * 1000))
                .signWith(getSecretKey())
                .compact();
    }



    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ================= 兼容项目原有代码的方法 =================

    public String createAccessToken(Long userId, String username) {
        return createToken(userId, username);
    }

    public String createRefreshToken(Long userId, String username) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .claim("type", TYPE_REFRESH)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpire * 1000))
                .signWith(getSecretKey())
                .compact();
    }

    public Claims parse(String token) {
        return parseToken(token);
    }

    public String getType(Claims claims) {
        return claims.get("type", String.class);
    }

    public Long getUserId(Claims claims) {
        return claims.get("userId", Long.class);
    }

    public long getRemainSeconds(Claims claims) {
        Date expirationDate = claims.getExpiration();
        if (expirationDate == null) {
            return 0;
        }
        long remain = expirationDate.getTime() - System.currentTimeMillis();
        return Math.max(remain / 1000, 0);
    }
}