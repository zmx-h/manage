package com.iecmp.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 周期状态流转 请求体。
 *
 * <p>对应接口：3-14（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetPeriodStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 目标状态：1 执行中，2 已结束 */
    @Schema(description = "目标状态：1 执行中，2 已结束")
    @NotNull(message = "目标状态：1 执行中，2 已结束不能为空")
    private Integer status;
}
