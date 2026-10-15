package com.iecmp.budget.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 支出明细公示 查询条件。
 *
 * <p>对应接口：3-04（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserBudgetExpensePageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 预算周期 ID */
    @Schema(description = "预算周期 ID")
    private Long periodId;

    /** 科目筛选 */
    @Schema(description = "科目筛选")
    private Long itemId;

    /** 模糊匹配支出标题 */
    @Schema(description = "模糊匹配支出标题")
    private String keyword;

    /** 支出日期起，格式 yyyy-MM-dd */
    @Schema(description = "支出日期起，格式 yyyy-MM-dd")
    private String beginDate;

    /** 支出日期止 */
    @Schema(description = "支出日期止")
    private String endDate;
}
