package com.iecmp.repo.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 个人贡献概览 查询条件。
 *
 * <p>对应接口：2-12（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author WanZiyu
 */
@Data
public class RepoStatOverviewQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 统计起始时间 */
    @Schema(description = "统计起始时间")
    private String beginTime;

    /** 统计截止时间 */
    @Schema(description = "统计截止时间")
    private String endTime;
}
