package com.iecmp.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 提交-日期趋势图 响应体。
 *
 * <p>对应接口：2-10（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoCommitChartVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 时间点标签；按日 yyyy-MM-dd，按周 yyyy-第w周，按月 yyyy-MM */
    @Schema(description = "时间点标签；按日 yyyy-MM-dd，按周 yyyy-第w周，按月 yyyy-MM")
    private String date;

    /** 该时间点的提交数 */
    @Schema(description = "该时间点的提交数")
    private Integer commitCount;

    /** 新增行数 */
    @Schema(description = "新增行数")
    private Integer addLines;

    /** 删除行数 */
    @Schema(description = "删除行数")
    private Integer delLines;
}
