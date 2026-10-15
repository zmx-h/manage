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
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * GitHub 客户端（REST API v3）。
 *
 * <p>与 Gitee 的两处差异在这里被吃掉：
 * <ol>
 *   <li>令牌走 {@code Authorization: Bearer} 请求头，且需要 {@code Accept} 与
 *       {@code X-GitHub-Api-Version} 头才会拿到稳定版本的响应；</li>
 *   <li>{@code since} 必须是 UTC 的 ISO 8601（{@code ...Z}），不能带本地时区偏移。</li>
 * </ol>
 */
@Component
public class GitHubPlatformClient extends AbstractRepoPlatformClient {

    /** 平台标识。 */
    public static final String PLATFORM = "github";

    /** GitHub 的时间入参格式：UTC，秒级，以 Z 结尾。 */
    private static final DateTimeFormatter SINCE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /** GitHub 仓库归属范围：只要该账号名下的仓库，不含仅作为成员参与的仓库。 */
    private static final String REPO_AFFILIATION_OWNER = "owner";

    public GitHubPlatformClient(RepoProperties repoProperties, ObjectMapper objectMapper) {
        super(repoProperties, objectMapper);
    }

    @Override
    public String platform() {
        return PLATFORM;
    }

    @Override
    public RepoUserInfo fetchUser(String accessToken) {
        JsonNode node = getJson("/user", null, authHeaders(accessToken));
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
            query.put("affiliation", REPO_AFFILIATION_OWNER);
            query.put("sort", "pushed");
            query.put("direction", "desc");
            query.put("per_page", pageSize);
            query.put("page", page);

            JsonNode array = getJson("/user/repos", query, authHeaders(accessToken));
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

            JsonNode array = getJson(repoPath(owner, repo) + "/branches", query, authHeaders(accessToken));
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
                // GitHub 只认 UTC 的 ISO 8601，本地时间必须先转成 UTC 再按 'Z' 字面量输出
                query.put("since", SINCE_FORMAT.format(
                        since.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC)));
            }

            JsonNode array = getJson(repoPath(owner, repo) + "/commits", query, authHeaders(accessToken));
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
        JsonNode node = getJson(repoPath(owner, repo) + "/commits/" + sha, null, authHeaders(accessToken));
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

    private Map<String, String> authHeaders(String accessToken) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);
        headers.put("Accept", "application/vnd.github+json");
        headers.put("X-GitHub-Api-Version", "2022-11-28");
        return headers;
    }
}
