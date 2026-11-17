package com.iecmp.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 公示 / 取消公示 请求体。
 *
 * <p>对应接口：3-15（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetPeriodPublicDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 取消公示，1 公示 */
    @Schema(description = "0 取消公示，1 公示")
    @NotNull(message = "是否公示不能为空")
    @Min(value = 0, message = "是否公示取值只能为 0 或 1")
    @Max(value = 1, message = "是否公示取值只能为 0 或 1")
    private Integer isPublic;
}
