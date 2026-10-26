package com.iecmp.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 支持的平台列表 响应体。
 *
 * <p>对应接口：2-01（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author WanZiyu
 */
@Data
public class RepoPlatformListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 平台标识：gitee / github */
    @Schema(description = "平台标识：gitee / github")
    private String platform;

    /** 平台展示名称 */
    @Schema(description = "平台展示名称")
    private String platformName;

    /** 令牌申请入口地址，前端引导用户前往 */
    @Schema(description = "令牌申请入口地址，前端引导用户前往")
    private String tokenUrl;

    /** 令牌权限要求提示文案 */
    @Schema(description = "令牌权限要求提示文案")
    private String tokenTip;
}
