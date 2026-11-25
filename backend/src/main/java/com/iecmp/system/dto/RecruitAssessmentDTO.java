package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 招新考核成绩录入 请求体（接口 1-34）。
 */
@Data
public class RecruitAssessmentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 考核成绩，0~100 */
    @Schema(description = "考核成绩，0~100")
    @NotNull(message = "考核成绩不能为空")
    @DecimalMin(value = "0", message = "考核成绩不能小于 0")
    @DecimalMax(value = "100", message = "考核成绩不能大于 100")
    private BigDecimal score;

    /** 通过分，不传时使用系统默认值 */
    @Schema(description = "通过分，不传时使用系统默认值")
    @Min(value = 0, message = "通过分不能小于 0")
    @Max(value = 100, message = "通过分不能大于 100")
    private Integer passScore;

    /** 成绩备注 */
    @Schema(description = "成绩备注")
    @Size(max = 255, message = "成绩备注最长 255 字")
    private String assessmentRemark;
}
