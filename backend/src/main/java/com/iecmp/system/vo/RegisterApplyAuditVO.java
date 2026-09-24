package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 注册申请审核 响应体。
 *
 * <p>对应接口：1-26（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RegisterApplyAuditVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 通过时生成的用户 ID */
    @Schema(description = "通过时生成的用户 ID")
    private Long userId;

    /** 通过时生成的登录账号 */
    @Schema(description = "通过时生成的登录账号")
    private String username;

    /** 初始密码，仅审核通过时返回一次 */
    @Schema(description = "初始密码，仅审核通过时返回一次")
    private String initialPassword;
}
