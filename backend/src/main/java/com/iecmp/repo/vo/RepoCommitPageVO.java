package com.iecmp.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 提交记录分页 响应体。
 *
 * <p>对应接口：2-09（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoCommitPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 提交记录 ID */
    @Schema(description = "提交记录 ID")
    private Long commitId;

    /** 提交 SHA（完整 40 位） */
    @Schema(description = "提交 SHA（完整 40 位）")
    private String commitSha;

    /** 提交 SHA 前 8 位，便于列表展示 */
    @Schema(description = "提交 SHA 前 8 位，便于列表展示")
    private String shortSha;

    /** 所属仓库 ID */
    @Schema(description = "所属仓库 ID")
    private Long projectId;

    /** 仓库名称 */
    @Schema(description = "仓库名称")
    private String projectName;

    /** 所属分支 */
    @Schema(description = "所属分支")
    private String branch;

    /** 提交作者名 */
    @Schema(description = "提交作者名")
    private String authorName;

    /** 提交信息 */
    @Schema(description = "提交信息")
    private String message;

    /** 提交时间 */
    @Schema(description = "提交时间")
    private String commitTime;

    /** 新增行数 */
    @Schema(description = "新增行数")
    private Integer addLines;

    /** 删除行数 */
    @Schema(description = "删除行数")
    private Integer delLines;

    /** 是否合并提交：0 否 1 是 */
    @Schema(description = "是否合并提交：0 否 1 是")
    private Integer isMerge;
}
