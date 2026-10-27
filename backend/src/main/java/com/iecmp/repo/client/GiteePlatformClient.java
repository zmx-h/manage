package com.iecmp.repo.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecmp.repo.client.model.RepoCommitInfo;
import com.iecmp.repo.client.model.RepoInfo;
import com.iecmp.repo.client.model.RepoUserInfo;
import com.iecmp.repo.properties.RepoProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gitee 客户端（OpenAPI v5）。
 *
 * <p>与 GitHub 的两处差异在这里被吃掉：
 * <ol>
 *   <li>令牌走 <b>查询参数</b> {@code access_token}，不是 {@code Authorization} 头；</li>
 *   <li>提交<b>列表</b>接口不返回 {@code stats}，增删行数必须再查一次提交详情。</li>
 * </ol>
 */
@Component
public class GiteePlatformClient extends AbstractRepoPlatformClient {

    /** 平台标识。 */
    public static final String PLATFORM = "gitee";

    /** Gitee 的时间入参格式，与它文档中的 {@code 2016-01-01T00:00:00+08:00} 一致。 */
    private static final DateTimeFormatter SINCE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    /** Gitee 仓库归属范围：只要该账号名下的仓库，不含仅作为成员参与的仓库。 */
    private static final String REPO_TYPE_OWNER = "owner";

    public GiteePlatformClient(RepoProperties repoProperties, ObjectMapper objectMapper) {
        super(repoProperties, objectMapper);
    }

    @Override
    public String platform() {
        return PLATFORM;
    }

    @Override
    public RepoUserInfo fetchUser(String accessToken) {
        JsonNode node = getJson("/user", tokenQuery(accessToken, null), null);
        RepoUserInfo user = new RepoUserInfo();
        user.setLogin(text(node, "login"));
        user.setName(text(node, "name"));
        user.setAvatarUrl(text(node, "avatar_url"));
        return user;
    }

    @Override
    public List<RepoInfo> fetchRepos(String accessToken) {
        int pageSize = platformConfig().getPageSize();
        List<RepoInfo> repos = new ArrayList<>();
        for (int page = FIRST_PAGE; page < FIRST_PAGE + repoProperties.getSync().getMaxPages(); page++) {
            Map<String, Object> query = new LinkedHashMap<>();
            query.put("type", REPO_TYPE_OWNER);
            query.put("sort", "pushed");
            query.put("direction", "desc");
            query.put("per_page", pageSize);
            query.put("page", page);

            JsonNode array = getJson("/user/repos", tokenQuery(accessToken, query), null);
            if (array == null || !array.isArray() || array.isEmpty()) {
                break;
            }
            for (JsonNode item : array) {
                repos.add(toRepoInfo(item));
            }
            if (array.size() < pageSize) {
                break;
            }
        }
        return repos;
    }

    @Override
    public List<String> fetchBranches(String accessToken, String owner, String repo) {
        int pageSize = platformConfig().getPageSize();
        List<String> branches = new ArrayList<>();
        for (int page = FIRST_PAGE; page < FIRST_PAGE + repoProperties.getSync().getMaxPages(); page++) {
            Map<String, Object> query = new LinkedHashMap<>();
            query.put("per_page", pageSize);
            query.put("page", page);

            JsonNode array = getJson(repoPath(owner, repo) + "/branches", tokenQuery(accessToken, query), null);
            if (array == null || !array.isArray() || array.isEmpty()) {
                break;
            }
            for (JsonNode item : array) {
                String name = text(item, "name");
                if (name != null && !name.isBlank()) {
                    branches.add(name);
                }
            }
            if (array.size() < pageSize) {
                break;
            }
        }
        return branches;
    }

    @Override
    public List<RepoCommitInfo> fetchCommits(String accessToken, String owner, String repo,
                                             String branch, LocalDateTime since) {
        int pageSize = platformConfig().getPageSize();
        List<RepoCommitInfo> commits = new ArrayList<>();
        for (int page = FIRST_PAGE; page < FIRST_PAGE + repoProperties.getSync().getMaxPages(); page++) {
            Map<String, Object> query = new LinkedHashMap<>();
            query.put("sha", branch);
            query.put("per_page", pageSize);
            query.put("page", page);
            if (since != null) {
                // 边界回溯的缓冲量由 RepoSyncService 统一控制，这里只负责按 Gitee 的格式序列化
                query.put("since", SINCE_FORMAT.format(since.atZone(ZoneId.systemDefault())));
            }

            JsonNode array = getJson(repoPath(owner, repo) + "/commits", tokenQuery(accessToken, query), null);
            if (array == null || !array.isArray() || array.isEmpty()) {
                break;
            }
            for (JsonNode item : array) {
                commits.add(toCommitInfo(item));
            }
            if (array.size() < pageSize) {
                break;
            }
        }
        return commits;
    }

    @Override
    public RepoCommitInfo fetchCommitDetail(String accessToken, String owner, String repo, String sha) {
        JsonNode node = getJson(repoPath(owner, repo) + "/commits/" + sha, tokenQuery(accessToken, null), null);
        JsonNode stats = node == null ? null : node.get("stats");
        if (stats == null || stats.isNull()) {
            return null;
        }
        RepoCommitInfo detail = new RepoCommitInfo();
        detail.setCommitSha(sha);
        detail.setAddLines(integer(stats, "additions", 0));
        detail.setDelLines(integer(stats, "deletions", 0));
        return detail;
    }

    private RepoInfo toRepoInfo(JsonNode item) {
        RepoInfo info = new RepoInfo();
        info.setRepoName(truncate(text(item, "name"), 128));
        info.setRepoUrl(truncate(text(item, "html_url"), 512));

        JsonNode owner = item.get("owner");
        info.setOwnerName(truncate(text(owner, "login"), 64));
        info.setLanguage(truncate(text(item, "language"), 32));
        info.setDefaultBranch(truncate(text(item, "default_branch"), 64));
        info.setStarCount(integer(item, "stargazers_count", 0));
        info.setForkCount(integer(item, "forks_count", 0));
        info.setLastPushTime(parseTime(text(item, "pushed_at")));
        return info;
    }

    private RepoCommitInfo toCommitInfo(JsonNode item) {
        JsonNode commit = item.get("commit");

        RepoCommitInfo info = new RepoCommitInfo();
        info.setCommitSha(truncate(text(item, "sha"), 64));
        info.setMessage(singleLine(text(commit, "message"), 1024));

        JsonNode author = commit == null ? null : commit.get("author");
        info.setAuthorName(truncate(text(author, "name"), 64));
        info.setAuthorEmail(truncate(text(author, "email"), 128));
        info.setCommitTime(parseTime(text(author, "date")));

        JsonNode parents = item.get("parents");
        info.setParentCount(parents != null && parents.isArray() ? parents.size() : 0);

        // 列表接口通常不带 stats，带了就顺手用上，省掉一次详情调用
        JsonNode stats = item.get("stats");
        if (stats != null && !stats.isNull()) {
            info.setAddLines(integer(stats, "additions", 0));
            info.setDelLines(integer(stats, "deletions", 0));
        }
        return info;
    }

    private String repoPath(String owner, String repo) {
        return "/repos/" + owner + "/" + repo;
    }

    private Map<String, Object> tokenQuery(String accessToken, Map<String, Object> query) {
        Map<String, Object> merged = query == null ? new LinkedHashMap<>() : new LinkedHashMap<>(query);
        merged.put("access_token", accessToken);
        return Collections.unmodifiableMap(merged);
    }
}
