package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 重置密码 请求体。
 *
 * <p>对应接口：1-13（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserPasswordResetDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 不传则重置为系统默认密码（123456） */
    @Schema(description = "不传则重置为系统默认密码（123456）")
    private String newPassword;
}
