package com.iecmp.budget.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 新增预算周期（起草） 响应体。
 *
 * <p>对应接口：3-11（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetPeriodCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新增周期 ID */
    @Schema(description = "新增周期 ID")
    private Long periodId;
}
