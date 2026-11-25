package com.iecmp.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 申请审核（通过 / 驳回） 请求体。
 *
 * <p>对应接口：3-21（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetApplyAuditDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 1 通过，2 驳回 */
    @Schema(description = "1 通过，2 驳回")
    @NotNull(message = "1 通过，2 驳回不能为空")
    private Integer auditStatus;

    /** 批准金额，通过时可少于申请金额（部分批准） */
    @Schema(description = "批准金额，通过时可少于申请金额（部分批准）")
    @DecimalMin(value = "0.01", message = "批准金额必须大于 0")
    @Digits(integer = 10, fraction = 2, message = "批准金额最多 10 位整数、2 位小数")
    private BigDecimal approvedAmount;

    /** 审核意见，驳回时建议填写 */
    @Schema(description = "审核意见，驳回时建议填写")
    @Size(max = 255, message = "审核意见最长 255 字")
    private String auditRemark;
}
