package com.iecmp.repo.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 成员贡献排行（后台） 查询条件。
 *
 * <p>对应接口：2-14（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author WanZiyu
 */
@Data
public class RepoStatRankQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 统计起始时间 */
    @Schema(description = "统计起始时间")
    private String beginTime;

    /** 统计截止时间 */
    @Schema(description = "统计截止时间")
    private String endTime;

    /** 排序依据：1 提交数 2 活跃天数 3 代码行数 4 贡献度得分 */
    @Schema(description = "排序依据：1 提交数 2 活跃天数 3 代码行数 4 贡献度得分")
    private Integer sortField;

    /** 返回条数上限，范围 1~100 */
    @Schema(description = "返回条数上限，范围 1~100")
    private Integer limit;
}
