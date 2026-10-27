package com.iecmp.repo.client.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 第三方平台的仓库信息，写入 {@code repo_project}。
 */
@Data
public class RepoInfo {

    /** 仓库名称，对应 {@code repo_project.repo_name}。 */
    private String repoName;

    /** 仓库所有者（login），对应 {@code repo_project.owner_name}。 */
    private String ownerName;

    /** 仓库主页地址，对应 {@code repo_project.repo_url}，同时是去重键的一半。 */
    private String repoUrl;

    /** 主要语言。 */
    private String language;

    /** 默认分支。 */
    private String defaultBranch;

    /** Star 数。 */
    private Integer starCount;

    /** Fork 数。 */
    private Integer forkCount;

    /** 最近推送时间。 */
    private LocalDateTime lastPushTime;
}
