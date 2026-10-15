package com.iecmp.budget.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 我的经费汇总 查询条件。
 *
 * <p>对应接口：3-09（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetStatMyQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 统计年度，格式 yyyy */
    @Schema(description = "统计年度，格式 yyyy")
    private String year;
}
