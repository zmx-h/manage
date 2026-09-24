package com.iecmp.commonm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 图形验证码 响应体。
 *
 * <p>对应接口：8-13（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class CaptchaVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 验证码标识，提交表单时回传 */
    @Schema(description = "验证码标识，提交表单时回传")
    private String captchaKey;

    /** 验证码图片，Base64 字符串（含 `data:image/png;base64` 前缀） */
    @Schema(description = "验证码图片，Base64 字符串（含 `data:image/png;base64` 前缀）")
    private String captchaImage;
}
