package com.iecmp.budget.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 公示预算周期分页 查询条件。
 *
 * <p>对应接口：3-01（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserBudgetPeriodPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 年度筛选，格式 yyyy */
    @Schema(description = "年度筛选，格式 yyyy")
    private String year;

    /** 状态筛选：1 执行中 2 已结束 */
    @Schema(description = "状态筛选：1 执行中 2 已结束")
    private Integer status;
}
