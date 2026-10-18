package com.iecmp.repo.client.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 第三方平台的提交信息，写入 {@code repo_commit}。
 *
 * <p>{@code addLines} / {@code delLines} 允许为 null：Gitee 与 GitHub 的提交<b>列表</b>接口
 * 都不保证返回行数统计，取不到时由采集逻辑再调一次提交详情补齐（见
 * {@code RepoProperties.Sync#commitDetail}）。
 */
@Data
public class RepoCommitInfo {

    /** 提交 SHA（完整 40 位）。 */
    private String commitSha;

    /** 提交作者名（git author，不是平台账号）。 */
    private String authorName;

    /** 提交作者邮箱。 */
    private String authorEmail;

    /** 提交信息。 */
    private String message;

    /** 提交时间。 */
    private LocalDateTime commitTime;

    /** 新增行数，取不到为 null。 */
    private Integer addLines;

    /** 删除行数，取不到为 null。 */
    private Integer delLines;

    /** 父提交数量大于 1 即为合并提交，对应 {@code repo_commit.is_merge}。 */
    private Integer parentCount;

    /** 是否合并提交：0 否 1 是。 */
    public Integer getIsMerge() {
        return parentCount != null && parentCount > 1 ? 1 : 0;
    }
}
