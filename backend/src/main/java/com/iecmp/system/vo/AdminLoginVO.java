package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 后台登录 响应体。
 *
 * <p>对应接口：1-01（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminLoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 访问令牌，后续放入 Authorization 头 */
    @Schema(description = "访问令牌，后续放入 Authorization 头")
    private String accessToken;

    /** 刷新令牌，仅用于换取新 accessToken */
    @Schema(description = "刷新令牌，仅用于换取新 accessToken")
    private String refreshToken;

    /** 令牌类型 */
    @Schema(description = "令牌类型")
    private String tokenType;

    /** accessToken 有效期（秒），默认 7200 */
    @Schema(description = "accessToken 有效期（秒），默认 7200")
    private Integer expiresIn;

    /** 用户信息，结构见下表 */
    @Schema(description = "用户信息，结构见下表")
    private UserInfoVO userInfo;

    /**
     * UserInfoVO：后台登录 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class UserInfoVO implements Serializable {

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
