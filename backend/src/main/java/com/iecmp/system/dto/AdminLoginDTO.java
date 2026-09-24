package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 后台登录 请求体。
 *
 * <p>对应接口：1-01（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminLoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 登录账号，4~32 位 */
    @Schema(description = "登录账号，4~32 位")
    @NotBlank(message = "登录账号，4~32 位不能为空")
    @Size(min = 4, max = 32, message = "登录账号，4~32 位长度需在 4~32 之间")
    private String username;

    /** 登录密码，6~32 位 */
    @Schema(description = "登录密码，6~32 位")
    @NotBlank(message = "登录密码，6~32 位不能为空")
    @Size(min = 6, max = 32, message = "登录密码，6~32 位长度需在 6~32 之间")
    private String password;

    /** 验证码标识，开启验证码时必填 */
    @Schema(description = "验证码标识，开启验证码时必填")
    private String captchaKey;

    /** 验证码内容，开启验证码时必填 */
    @Schema(description = "验证码内容，开启验证码时必填")
    private String captchaCode;

    /**
     * UserInfoDTO：后台登录 请求体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class UserInfoDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** userId（字段含义见接口文档） */
        @Schema(description = "userId（字段含义见接口文档）")
        private Long userId;

        /** username（字段含义见接口文档） */
        @Schema(description = "username（字段含义见接口文档）")
        private String username;

        /** realName（字段含义见接口文档） */
        @Schema(description = "realName（字段含义见接口文档）")
        private String realName;

        /** avatarUrl（字段含义见接口文档） */
        @Schema(description = "avatarUrl（字段含义见接口文档）")
        private String avatarUrl;

        /** roles（字段含义见接口文档） */
        @Schema(description = "roles（字段含义见接口文档）")
        private List<String> roles;

        /** permissions（字段含义见接口文档） */
        @Schema(description = "permissions（字段含义见接口文档）")
        private List<String> permissions;
    }
}
