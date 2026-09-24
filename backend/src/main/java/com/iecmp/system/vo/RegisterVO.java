package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 邀请码注册 响应体。
 *
 * <p>对应接口：1-23（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RegisterVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新账号 ID */
    @Schema(description = "新账号 ID")
    private Long userId;

    /** 登录账号 */
    @Schema(description = "登录账号")
    private String username;
}
