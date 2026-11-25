package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

/**
 * 招新注册码注册 请求体（接口 1-31）。
 *
 * <p>姓名、年级、班级、学号等信息从报名记录带出，前端只需提交邮箱、注册码与账号密码。
 */
@Data
public class RecruitRegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 报名邮箱 */
    @Schema(description = "报名邮箱")
    @NotBlank(message = "报名邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 64, message = "邮箱最长 64 位")
    private String email;

    /** 邮件中的 8 位注册码 */
    @Schema(description = "邮件中的 8 位注册码")
    @NotBlank(message = "注册码不能为空")
    @Size(min = 8, max = 8, message = "注册码为 8 位")
    private String code;

    /** 登录账号，4~32 位 */
    @Schema(description = "登录账号，4~32 位")
    @NotBlank(message = "登录账号不能为空")
    @Size(min = 4, max = 32, message = "登录账号长度需为 4~32 位")
    private String username;

    /** 登录密码，6~32 位 */
    @Schema(description = "登录密码，6~32 位")
    @NotBlank(message = "登录密码不能为空")
    @Size(min = 6, max = 32, message = "登录密码长度需为 6~32 位")
    private String password;
}
