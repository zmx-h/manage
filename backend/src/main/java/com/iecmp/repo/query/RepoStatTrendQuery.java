package com.iecmp.repo.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 中心提交趋势（后台） 查询条件。
 *
 * <p>对应接口：2-15（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoStatTrendQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 开始日期，格式 yyyy-MM-dd */
    @Schema(description = "开始日期，格式 yyyy-MM-dd")
    private String beginDate;

    /** 结束日期 */
    @Schema(description = "结束日期")
    private String endDate;

    /** 时间粒度：1 按日 2 按周 3 按月 */
    @Schema(description = "时间粒度：1 按日 2 按周 3 按月")
    private Integer granularity;
}
