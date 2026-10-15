package com.iecmp.duty.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 提醒配置查询 响应体。
 *
 * <p>对应接口：7-20（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyReminderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 配置 ID；尚未创建时为 null */
    @Schema(description = "配置 ID；尚未创建时为 null")
    private Long configId;

    /** 所属排班计划 ID；null 表示全局配置 */
    @Schema(description = "所属排班计划 ID；null 表示全局配置")
    private Long planId;

    /** 提前提醒分钟数 */
    @Schema(description = "提前提醒分钟数")
    private Integer aheadMinutes;

    /** 提醒渠道：1 站内消息 */
    @Schema(description = "提醒渠道：1 站内消息")
    private Integer channel;

    /** 是否启用：0 停用 1 启用 */
    @Schema(description = "是否启用：0 停用 1 启用")
    private Integer enabled;

    /** 是否来自全局配置：0 否 1 是 */
    @Schema(description = "是否来自全局配置：0 否 1 是")
    private Integer isGlobal;
}
