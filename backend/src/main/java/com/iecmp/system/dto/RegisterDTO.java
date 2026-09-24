package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

/**
 * 邀请码注册 请求体。
 *
 * <p>对应接口：1-23（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 管理员下发的邀请码 */
    @Schema(description = "管理员下发的邀请码")
    @NotBlank(message = "管理员下发的邀请码不能为空")
    private String inviteCode;

    /** 登录账号，4~32 位，全局唯一 */
    @Schema(description = "登录账号，4~32 位，全局唯一")
    @NotBlank(message = "登录账号，4~32 位，全局唯一不能为空")
    @Size(min = 4, max = 32, message = "登录账号，4~32 位，全局唯一长度需在 4~32 之间")
    private String username;

    /** 登录密码，6~32 位 */
    @Schema(description = "登录密码，6~32 位")
    @NotBlank(message = "登录密码，6~32 位不能为空")
    @Size(min = 6, max = 32, message = "登录密码，6~32 位长度需在 6~32 之间")
    private String password;

    /** 真实姓名 */
    @Schema(description = "真实姓名")
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    /** 学号，全局唯一 */
    @Schema(description = "学号，全局唯一")
    @NotBlank(message = "学号，全局唯一不能为空")
    private String studentNo;

    /** 专业 */
    @Schema(description = "专业")
    private String major;

    /** 手机号 */
    @Schema(description = "手机号")
    private String phone;

    /** 验证码标识，开启验证码时必填 */
    @Schema(description = "验证码标识，开启验证码时必填")
    private String captchaKey;

    /** 验证码内容 */
    @Schema(description = "验证码内容")
    private String captchaCode;
}
