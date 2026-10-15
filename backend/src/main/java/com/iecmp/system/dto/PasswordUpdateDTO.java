package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

/**
 * 修改密码 请求体。
 *
 * <p>对应接口：1-07（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class PasswordUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 原密码 */
    @Schema(description = "原密码")
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    /** 新密码，6~32 位，需包含字母与数字 */
    @Schema(description = "新密码，6~32 位，需包含字母与数字")
    @NotBlank(message = "新密码，6~32 位，需包含字母与数字不能为空")
    @Size(min = 6, max = 32, message = "新密码，6~32 位，需包含字母与数字长度需在 6~32 之间")
    private String newPassword;

    /** 确认新密码，须与 newPassword 一致 */
    @Schema(description = "确认新密码，须与 newPassword 一致")
    @NotBlank(message = "确认新密码，须与 newPassword 一致不能为空")
    private String confirmPassword;
}
