package com.iecmp.duty.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 值日日历（月视图） 查询条件。
 *
 * <p>对应接口：7-03（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyScheduleCalendarQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 查询月份，格式 yyyy-MM */
    @Schema(description = "查询月份，格式 yyyy-MM")
    private String month;

    /** 排班计划 ID */
    @Schema(description = "排班计划 ID")
    private Long planId;
}
