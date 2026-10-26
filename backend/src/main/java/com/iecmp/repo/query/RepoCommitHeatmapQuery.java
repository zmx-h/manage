package com.iecmp.repo.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 贡献热力图 查询条件。
 *
 * <p>对应接口：2-11（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author WanZiyu
 */
@Data
public class RepoCommitHeatmapQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 统计年份，格式 yyyy */
    @Schema(description = "统计年份，格式 yyyy")
    private String year;

    /** 查看指定成员的热力图（仅统计口径相同的前台场景使用） */
    @Schema(description = "查看指定成员的热力图（仅统计口径相同的前台场景使用）")
    private Long userId;
}
