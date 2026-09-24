package com.iecmp.budget.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 支出明细公示 响应体。
 *
 * <p>对应接口：3-04（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserBudgetExpensePageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 支出记录 ID */
    @Schema(description = "支出记录 ID")
    private Long expenseId;

    /** 所属周期 ID */
    @Schema(description = "所属周期 ID")
    private Long periodId;

    /** 周期标题 */
    @Schema(description = "周期标题")
    private String periodTitle;

    /** 所属科目 ID */
    @Schema(description = "所属科目 ID")
    private Long itemId;

    /** 科目名称 */
    @Schema(description = "科目名称")
    private String itemName;

    /** 支出标题 */
    @Schema(description = "支出标题")
    private String title;

    /** 支出金额 */
    @Schema(description = "支出金额")
    private BigDecimal amount;

    /** 支出日期 */
    @Schema(description = "支出日期")
    private String expenseDate;

    /** 经办人姓名 */
    @Schema(description = "经办人姓名")
    private String handlerName;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
