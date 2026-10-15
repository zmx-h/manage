package com.iecmp.commonm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 未读消息数 响应体。
 *
 * <p>对应接口：8-21（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class MessageUnreadCountVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 未读消息总数 */
    @Schema(description = "未读消息总数")
    private Integer total;

    /** 未读「值日提醒」数量 */
    @Schema(description = "未读「值日提醒」数量")
    private Integer dutyCount;

    /** 未读「审核结果」数量 */
    @Schema(description = "未读「审核结果」数量")
    private Integer auditCount;

    /** 未读「系统通知」数量 */
    @Schema(description = "未读「系统通知」数量")
    private Integer systemCount;
}
