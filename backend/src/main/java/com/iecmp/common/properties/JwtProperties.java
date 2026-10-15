package com.iecmp.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 双令牌配置（架构文档 4.1 节）。
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** 签名密钥，HS256 要求至少 32 字节；生产环境必须通过环境变量注入。 */
    private String secret;

    /** accessToken 有效期（秒），默认 2 小时。 */
    private Long accessTokenExpire = 7200L;

    /** refreshToken 有效期（秒），默认 7 天。 */
    private Long refreshTokenExpire = 604800L;

    /** refreshToken 剩余不足该值（秒）时滑动续期，默认 1 天。 */
    private Long refreshRenewThreshold = 86400L;

    /** 令牌所在请求头名称。 */
    private String header = "Authorization";

    /** 令牌前缀。 */
    private String prefix = "Bearer ";
}
