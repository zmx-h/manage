package com.iecmp.budget.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 预算科目明细 响应体。
 *
 * <p>对应接口：3-03（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetPeriodItemListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 科目 ID */
    @Schema(description = "科目 ID")
    private Long itemId;

    /** 科目名称 */
    @Schema(description = "科目名称")
    private String name;

    /** 1 设备采购，2 耗材，3 活动，4 差旅，5 其他 */
    @Schema(description = "1 设备采购，2 耗材，3 活动，4 差旅，5 其他")
    private Integer category;

    /** 科目预算额度 */
    @Schema(description = "科目预算额度")
    private BigDecimal budgetAmount;

    /** 已使用额度 */
    @Schema(description = "已使用额度")
    private BigDecimal usedAmount;

    /** 执行率（百分比数值） */
    @Schema(description = "执行率（百分比数值）")
    private BigDecimal executionRate;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
