package com.iecmp.commonm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 健康检查 响应体。
 *
 * <p>对应接口：8-14（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class HealthVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** UP / DOWN */
    @Schema(description = "UP / DOWN")
    private String status;

    /** 服务版本号 */
    @Schema(description = "服务版本号")
    private String version;

    /** 服务器当前时间 */
    @Schema(description = "服务器当前时间")
    private String time;

    /** 数据库连通状态 */
    @Schema(description = "数据库连通状态")
    private String mysql;

    /** 缓存连通状态 */
    @Schema(description = "缓存连通状态")
    private String redis;
}
