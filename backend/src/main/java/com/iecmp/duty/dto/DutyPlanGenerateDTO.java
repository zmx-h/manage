package com.iecmp.duty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 自动生成排班 请求体。
 *
 * <p>对应接口：7-12（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyPlanGenerateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否覆盖已存在的条目：0 不覆盖（仅补空缺） 1 覆盖重建 */
    @Schema(description = "是否覆盖已存在的条目：0 不覆盖（仅补空缺） 1 覆盖重建")
    private Integer overwrite;

    /** 是否跳过周末：0 不跳过 1 跳过周六周日 */
    @Schema(description = "是否跳过周末：0 不跳过 1 跳过周六周日")
    private Integer skipWeekend;

    /** 每天值日人数 */
    @Schema(description = "每天值日人数")
    private Integer perDayCount;
}
