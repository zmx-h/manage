package com.iecmp.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 登记支出 请求体。
 *
 * <p>对应接口：3-22（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetExpenseAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 所属预算周期 ID */
    @Schema(description = "所属预算周期 ID")
    @NotNull(message = "所属预算周期 ID不能为空")
    private Long periodId;

    /** 所属预算科目 ID */
    @Schema(description = "所属预算科目 ID")
    @NotNull(message = "所属预算科目 ID不能为空")
    private Long itemId;

    /** 关联的预算申请 ID；不传表示无申请的直接支出 */
    @Schema(description = "关联的预算申请 ID；不传表示无申请的直接支出")
    private Long applyId;

    /** 支出标题，最长 100 字 */
    @Schema(description = "支出标题，最长 100 字")
    @NotBlank(message = "支出标题，最长 100 字不能为空")
    private String title;

    /** 支出金额，必须大于 0 */
    @Schema(description = "支出金额，必须大于 0")
    @NotNull(message = "支出金额，必须大于 0不能为空")
    private BigDecimal amount;

    /** 支出日期，格式 yyyy-MM-dd */
    @Schema(description = "支出日期，格式 yyyy-MM-dd")
    @NotBlank(message = "支出日期，格式 yyyy-MM-dd不能为空")
    private String expenseDate;

    /** 凭证文件 ID（发票 / 收据），先调接口 8-01 上传 */
    @Schema(description = "凭证文件 ID（发票 / 收据），先调接口 8-01 上传")
    private Long voucherFileId;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
