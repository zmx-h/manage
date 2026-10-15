package com.iecmp.commonm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 操作日志分页 响应体。
 *
 * <p>对应接口：8-12（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class LogPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 日志 ID */
    @Schema(description = "日志 ID")
    private Long logId;

    /** 所属模块 */
    @Schema(description = "所属模块")
    private String module;

    /** 操作类型 */
    @Schema(description = "操作类型")
    private String operation;

    /** 请求地址 */
    @Schema(description = "请求地址")
    private String requestUri;

    /** 请求参数摘要，超长截断（≤ 2000 字符），敏感字段脱敏 */
    @Schema(description = "请求参数摘要，超长截断（≤ 2000 字符），敏感字段脱敏")
    private String requestParam;

    /** 返回信息摘要 */
    @Schema(description = "返回信息摘要")
    private String responseMsg;

    /** 操作人 ID */
    @Schema(description = "操作人 ID")
    private Long operatorId;

    /** 操作人姓名 */
    @Schema(description = "操作人姓名")
    private String operatorName;

    /** 来源 IP */
    @Schema(description = "来源 IP")
    private String ip;

    /** 耗时（毫秒） */
    @Schema(description = "耗时（毫秒）")
    private Long costMs;

    /** 0 失败，1 成功 */
    @Schema(description = "0 失败，1 成功")
    private Integer status;

    /** 操作时间 */
    @Schema(description = "操作时间")
    private String createTime;
}
