package com.iecmp.repo.client;

import com.iecmp.repo.client.model.RepoCommitInfo;
import com.iecmp.repo.client.model.RepoInfo;
import com.iecmp.repo.client.model.RepoUserInfo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 代码托管平台客户端：把 Gitee / GitHub 的 REST 差异收敛到这一层。
 *
 * <p>上层（{@code RepoSyncServiceImpl}、{@code RepoAccountServiceImpl}）只面向本接口编程，
 * 新增第三方平台时实现本接口并注册为 Bean 即可，业务代码不用改。
 *
 * <p><b>失败语义</b>：所有方法统一抛 {@link RepoApiException}，
 * 调用方据此区分「令牌失效」（错误码 20003）与「调用失败」（错误码 20007）。
 */
public interface RepoPlatformClient {

    /** 平台标识：gitee / github，与 {@code repo_account.platform} 取值一致。 */
    String platform();

    /**
     * 校验令牌并取回账号信息（Gitee / GitHub 的 {@code GET /user}）。
     *
     * @throws RepoApiException 令牌无效时 {@code kind = INVALID_TOKEN}
     */
    RepoUserInfo fetchUser(String accessToken);

    /**
     * 拉取该账号<b>名下</b>的全部仓库（不含仅作为成员参与的仓库）。
     *
     * <p>按 {@code maxPages} 分页拉全，避免成员仓库较多时只拿到第一页。
     */
    List<RepoInfo> fetchRepos(String accessToken);

    /**
     * 拉取仓库的全部分支名。
     *
     * <p>接口文档 8.1 已确定口径 2：提交统计范围包含所有分支，不做分支过滤。
     */
    List<String> fetchBranches(String accessToken, String owner, String repo);

    /**
     * 按分支拉取提交列表。
     *
     * @param since 增量起点，为 null 表示拉全量历史
     */
    List<RepoCommitInfo> fetchCommits(String accessToken, String owner, String repo, String branch, LocalDateTime since);

    /**
     * 查询单条提交详情，用于补齐列表接口没给的增删行数。
     *
     * @return 取不到行数时返回 null，调用方按 0 处理
     */
    RepoCommitInfo fetchCommitDetail(String accessToken, String owner, String repo, String sha);
}
