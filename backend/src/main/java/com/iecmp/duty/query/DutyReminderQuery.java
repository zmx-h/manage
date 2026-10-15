package com.iecmp.duty.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 提醒配置查询 查询条件。
 *
 * <p>对应接口：7-20（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyReminderQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 查询指定计划的配置；不传则查询全局配置 */
    @Schema(description = "查询指定计划的配置；不传则查询全局配置")
    private Long planId;
}
