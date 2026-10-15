package com.iecmp.budget.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 我的经费汇总 响应体。
 *
 * <p>对应接口：3-09（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetStatMyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 申请总数 */
    @Schema(description = "申请总数")
    private Integer applyTotal;

    /** 申请总金额 */
    @Schema(description = "申请总金额")
    private BigDecimal applyAmount;

    /** 已通过数量 */
    @Schema(description = "已通过数量")
    private Integer approvedCount;

    /** 已通过总金额 */
    @Schema(description = "已通过总金额")
    private BigDecimal approvedAmount;

    /** 已驳回数量 */
    @Schema(description = "已驳回数量")
    private Integer rejectedCount;

    /** 待审核数量 */
    @Schema(description = "待审核数量")
    private Integer pendingCount;

    /** 已撤销数量 */
    @Schema(description = "已撤销数量")
    private Integer cancelledCount;

    /** 已产生的实际支出总额 */
    @Schema(description = "已产生的实际支出总额")
    private BigDecimal expenseAmount;
}
