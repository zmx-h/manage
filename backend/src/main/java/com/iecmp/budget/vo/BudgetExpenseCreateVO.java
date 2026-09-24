package com.iecmp.budget.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 登记支出 响应体。
 *
 * <p>对应接口：3-22（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetExpenseCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新增支出记录 ID */
    @Schema(description = "新增支出记录 ID")
    private Long expenseId;

    /** 扣减后周期已用额度 */
    @Schema(description = "扣减后周期已用额度")
    private BigDecimal periodUsedAmount;

    /** 扣减后科目已用额度 */
    @Schema(description = "扣减后科目已用额度")
    private BigDecimal itemUsedAmount;
}
