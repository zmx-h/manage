package com.iecmp.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 我的仓库分页 响应体。
 *
 * <p>对应接口：2-07（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoProjectPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 仓库 ID */
    @Schema(description = "仓库 ID")
    private Long projectId;

    /** 仓库名称 */
    @Schema(description = "仓库名称")
    private String repoName;

    /** 仓库地址 */
    @Schema(description = "仓库地址")
    private String repoUrl;

    /** 仓库所有者 */
    @Schema(description = "仓库所有者")
    private String ownerName;

    /** 主要语言 */
    @Schema(description = "主要语言")
    private String language;

    /** 默认分支 */
    @Schema(description = "默认分支")
    private String defaultBranch;

    /** Star 数 */
    @Schema(description = "Star 数")
    private Integer starCount;

    /** Fork 数 */
    @Schema(description = "Fork 数")
    private Integer forkCount;

    /** 最近推送时间 */
    @Schema(description = "最近推送时间")
    private String lastPushTime;

    /** 是否纳入统计：0 不统计 1 统计 */
    @Schema(description = "是否纳入统计：0 不统计 1 统计")
    private Integer tracked;

    /** 采集状态：0 未采集 1 采集中 2 采集正常 3 采集失败 */
    @Schema(description = "采集状态：0 未采集 1 采集中 2 采集正常 3 采集失败")
    private Integer syncStatus;

    /** 该仓库已采集的提交总数，用于列表展示 */
    @Schema(description = "该仓库已采集的提交总数，用于列表展示")
    private Integer commitCount;
}
