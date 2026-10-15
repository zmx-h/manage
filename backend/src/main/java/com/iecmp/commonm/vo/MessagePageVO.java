package com.iecmp.commonm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 我的消息分页 响应体。
 *
 * <p>对应接口：8-20（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class MessagePageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 消息 ID */
    @Schema(description = "消息 ID")
    private Long messageId;

    /** 消息标题 */
    @Schema(description = "消息标题")
    private String title;

    /** 消息内容 */
    @Schema(description = "消息内容")
    private String content;

    /** 消息类型：1 值日提醒 2 审核结果 3 系统通知 */
    @Schema(description = "消息类型：1 值日提醒 2 审核结果 3 系统通知")
    private Integer msgType;

    /** 关联业务类型，如 `duty`、`budget`、`asset`、`honor` */
    @Schema(description = "关联业务类型，如 `duty`、`budget`、`asset`、`honor`")
    private String bizType;

    /** 关联业务主键，供前端跳转详情 */
    @Schema(description = "关联业务主键，供前端跳转详情")
    private Long bizId;

    /** 阅读状态：0 未读 1 已读 */
    @Schema(description = "阅读状态：0 未读 1 已读")
    private Integer isRead;

    /** 消息时间 */
    @Schema(description = "消息时间")
    private String createTime;
}
