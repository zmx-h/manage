package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 刷新令牌 响应体。
 *
 * <p>对应接口：1-04（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class TokenRefreshVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新的访问令牌 */
    @Schema(description = "新的访问令牌")
    private String accessToken;

    /** 新的刷新令牌，仅在原令牌临近过期时返回 */
    @Schema(description = "新的刷新令牌，仅在原令牌临近过期时返回")
    private String refreshToken;

    /** 新的有效期（秒） */
    @Schema(description = "新的有效期（秒）")
    private Integer expiresIn;
}
