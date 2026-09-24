package com.iecmp.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 个人贡献概览 响应体。
 *
 * <p>对应接口：2-12（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoStatOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 提交总数 */
    @Schema(description = "提交总数")
    private Integer commitCount;

    /** 活跃天数 */
    @Schema(description = "活跃天数")
    private Integer activeDays;

    /** 新增代码行数 */
    @Schema(description = "新增代码行数")
    private Integer addLines;

    /** 删除代码行数 */
    @Schema(description = "删除代码行数")
    private Integer delLines;

    /** 有提交的仓库数 */
    @Schema(description = "有提交的仓库数")
    private Integer projectCount;

    /** 有提交的分支数 */
    @Schema(description = "有提交的分支数")
    private Integer branchCount;

    /** 在中心内的提交数排名 */
    @Schema(description = "在中心内的提交数排名")
    private Integer rank;

    /** 贡献度得分 */
    @Schema(description = "贡献度得分")
    private BigDecimal contributionScore;

    /** 最近一次自动采集完成时间，用于展示「数据更新于…」 */
    @Schema(description = "最近一次自动采集完成时间，用于展示「数据更新于…」")
    private String lastSyncTime;
}
