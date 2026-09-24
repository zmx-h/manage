package com.iecmp.budget.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 预算周期分页（后台） 查询条件。
 *
 * <p>对应接口：3-10（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminBudgetPeriodPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 模糊匹配周期标题 */
    @Schema(description = "模糊匹配周期标题")
    private String keyword;

    /** 年度筛选，格式 yyyy */
    @Schema(description = "年度筛选，格式 yyyy")
    private String year;

    /** 0 草稿，1 执行中，2 已结束 */
    @Schema(description = "0 草稿，1 执行中，2 已结束")
    private Integer status;

    /** 0 不公示，1 公示中 */
    @Schema(description = "0 不公示，1 公示中")
    private Integer isPublic;
}
