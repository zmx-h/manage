package com.iecmp.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 成员贡献排行（后台） 响应体。
 *
 * <p>对应接口：2-14（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author WanZiyu
 */
@Data
public class RepoStatRankVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 名次，从 1 开始 */
    @Schema(description = "名次，从 1 开始")
    private Integer rank;

    /** 成员用户 ID */
    @Schema(description = "成员用户 ID")
    private Long userId;

    /** 成员姓名 */
    @Schema(description = "成员姓名")
    private String realName;

    /** 年级 */
    @Schema(description = "年级")
    private String grade;

    /** 头像地址 */
    @Schema(description = "头像地址")
    private String avatarUrl;

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

    /** 贡献度得分 */
    @Schema(description = "贡献度得分")
    private BigDecimal contributionScore;
}
