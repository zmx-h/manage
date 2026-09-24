package com.iecmp.duty.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 值日统计看板 查询条件。
 *
 * <p>对应接口：7-19（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyStatOverviewQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 排班计划 ID */
    @Schema(description = "排班计划 ID")
    private Long planId;
}
