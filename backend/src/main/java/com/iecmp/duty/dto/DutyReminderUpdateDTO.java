package com.iecmp.duty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 提醒配置保存 请求体。
 *
 * <p>对应接口：7-21（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyReminderUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 所属排班计划 ID；不传或传 null 表示保存全局配置 */
    @Schema(description = "所属排班计划 ID；不传或传 null 表示保存全局配置")
    private Long planId;

    /** 提前提醒分钟数，范围 5~1440 */
    @Schema(description = "提前提醒分钟数，范围 5~1440")
    @NotNull(message = "提前提醒分钟数，范围 5~1440不能为空")
    private Integer aheadMinutes;

    /** 提醒渠道：1 站内消息 */
    @Schema(description = "提醒渠道：1 站内消息")
    private Integer channel;

    /** 是否启用：0 停用 1 启用 */
    @Schema(description = "是否启用：0 停用 1 启用")
    private Integer enabled;
}
