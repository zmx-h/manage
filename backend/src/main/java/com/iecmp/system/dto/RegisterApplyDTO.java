package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Data;

/**
 * 提交注册申请 请求体。
 *
 * <p>对应接口：1-24（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RegisterApplyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 真实姓名 */
    @Schema(description = "真实姓名")
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    /** 学号 */
    @Schema(description = "学号")
    @NotBlank(message = "学号不能为空")
    private String studentNo;

    /** 专业 */
    @Schema(description = "专业")
    private String major;

    /** 手机号 */
    @Schema(description = "手机号")
    private String phone;

    /** 申请理由，最长 200 字 */
    @Schema(description = "申请理由，最长 200 字")
    private String reason;

    /** 验证码标识 */
    @Schema(description = "验证码标识")
    @NotBlank(message = "验证码标识不能为空")
    private String captchaKey;

    /** 验证码内容 */
    @Schema(description = "验证码内容")
    @NotBlank(message = "验证码内容不能为空")
    private String captchaCode;
}
