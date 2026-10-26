package com.iecmp.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 中心提交趋势（后台） 响应体。
 *
 * <p>对应接口：2-15（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author WanZiyu
 */
@Data
public class RepoStatTrendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 时间点标签 */
    @Schema(description = "时间点标签")
    private String date;

    /** 该时间点的提交总数 */
    @Schema(description = "该时间点的提交总数")
    private Integer commitCount;

    /** 该时间点有提交的成员数 */
    @Schema(description = "该时间点有提交的成员数")
    private Integer memberCount;

    /** 新增行数 */
    @Schema(description = "新增行数")
    private Integer addLines;

    /** 删除行数 */
    @Schema(description = "删除行数")
    private Integer delLines;
}
