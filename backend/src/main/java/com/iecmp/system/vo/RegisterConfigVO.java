package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 注册方式配置查询 响应体。
 *
 * <p>对应接口：1-22（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RegisterConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 1 开启邀请码注册，0 关闭 */
    @Schema(description = "1 开启邀请码注册，0 关闭")
    private Integer inviteEnabled;

    /** 1 开启注册申请，0 关闭 */
    @Schema(description = "1 开启注册申请，0 关闭")
    private Integer applyEnabled;

    /** 1 需要图形验证码 */
    @Schema(description = "1 需要图形验证码")
    private Integer captchaEnabled;

    /** 注册页提示文案，由后台配置 */
    @Schema(description = "注册页提示文案，由后台配置")
    private String tips;

    /** 1 开放招新注册码注册，0 关闭 */
    @Schema(description = "1 开放招新注册码注册，0 关闭")
    private Integer recruitEnabled;

    /** 招新注册提示文案 */
    @Schema(description = "招新注册提示文案")
    private String recruitTips;

    /** 招新注册码有效期（小时） */
    @Schema(description = "招新注册码有效期（小时）")
    private Integer recruitCodeExpireHours;
}
