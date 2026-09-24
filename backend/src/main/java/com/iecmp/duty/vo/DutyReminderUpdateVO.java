package com.iecmp.duty.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 提醒配置保存 响应体。
 *
 * <p>对应接口：7-21（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyReminderUpdateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 配置 ID */
    @Schema(description = "配置 ID")
    private Long configId;
}
