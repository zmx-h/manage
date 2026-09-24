package com.iecmp.duty.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 自动生成排班 响应体。
 *
 * <p>对应接口：7-12（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyPlanGenerateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 排班计划 ID */
    @Schema(description = "排班计划 ID")
    private Long planId;

    /** 本次生成的值日条目数 */
    @Schema(description = "本次生成的值日条目数")
    private Integer generatedCount;

    /** 因已存在而跳过的条目数（`overwrite = 0` 时） */
    @Schema(description = "因已存在而跳过的条目数（`overwrite = 0` 时）")
    private Integer skippedCount;

    /** 生成的日期范围，如 `2026-09-23 ~ 2026-12-31` */
    @Schema(description = "生成的日期范围，如 `2026-09-23 ~ 2026-12-31`")
    private String dateRange;
}
