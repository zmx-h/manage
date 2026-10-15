package com.iecmp.budget.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 支出分页（后台） 查询条件。
 *
 * <p>对应接口：3-23（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminBudgetExpensePageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 限定预算周期 */
    @Schema(description = "限定预算周期")
    private Long periodId;

    /** 限定科目 */
    @Schema(description = "限定科目")
    private Long itemId;

    /** 限定经办人 */
    @Schema(description = "限定经办人")
    private Long handlerId;

    /** 模糊匹配支出标题 */
    @Schema(description = "模糊匹配支出标题")
    private String keyword;

    /** 支出日期起 */
    @Schema(description = "支出日期起")
    private String beginDate;

    /** 支出日期止 */
    @Schema(description = "支出日期止")
    private String endDate;
}
