package com.iecmp.commonm.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 我的消息分页 查询条件。
 *
 * <p>对应接口：8-20（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class MessagePageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 阅读状态筛选：0 未读 1 已读 */
    @Schema(description = "阅读状态筛选：0 未读 1 已读")
    private Integer isRead;

    /** 消息类型：1 值日提醒 2 审核结果 3 系统通知 */
    @Schema(description = "消息类型：1 值日提醒 2 审核结果 3 系统通知")
    private Integer msgType;

    /** 消息时间起 */
    @Schema(description = "消息时间起")
    private String beginTime;

    /** 消息时间止 */
    @Schema(description = "消息时间止")
    private String endTime;
}
