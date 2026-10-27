package com.iecmp.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 提交预算申请 请求体。
 *
 * <p>对应接口：3-05（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetApplyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 预算周期 ID */
    @Schema(description = "预算周期 ID")
    @NotNull(message = "预算周期 ID不能为空")
    private Long periodId;

    /** 预算科目 ID */
    @Schema(description = "预算科目 ID")
    @NotNull(message = "预算科目 ID不能为空")
    private Long itemId;

    /** 申请标题，最长 100 字 */
    @Schema(description = "申请标题，最长 100 字")
    @NotBlank(message = "申请标题不能为空")
    @Size(max = 100, message = "申请标题长度不能超过 100 个字符")
    private String title;

    /** 申请金额，最多两位小数 */
    @Schema(description = "申请金额，最多两位小数")
    @NotNull(message = "申请金额不能为空")
    @DecimalMin(value = "0.01", message = "申请金额必须大于 0")
    @Digits(integer = 10, fraction = 2, message = "申请金额最多 10 位整数、2 位小数")
    private BigDecimal amount;

    /** 申请事由，最长 500 字 */
    @Schema(description = "申请事由，最长 500 字")
    @Size(max = 500, message = "申请事由长度不能超过 500 个字符")
    private String reason;

    /** 期望使用日期，格式 yyyy-MM-dd */
    @Schema(description = "期望使用日期，格式 yyyy-MM-dd")
    @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}$", message = "期望使用日期格式应为 yyyy-MM-dd")
    private String expectDate;

    /** 附件文件 ID，可先调用接口 8-01 上传后填入 */
    @Schema(description = "附件文件 ID，可先调用接口 8-01 上传后填入")
    private Long attachFileId;
}
