package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Data;

/**
 * 刷新令牌 请求体。
 *
 * <p>对应接口：1-04（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class TokenRefreshDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 登录时下发的刷新令牌 */
    @Schema(description = "登录时下发的刷新令牌")
    @NotBlank(message = "登录时下发的刷新令牌不能为空")
    private String refreshToken;
}
