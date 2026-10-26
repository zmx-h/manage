package com.iecmp.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecmp.repo.client.GitHubPlatformClient;
import com.iecmp.repo.client.GiteePlatformClient;
import com.iecmp.repo.client.RepoApiException;
import com.iecmp.repo.client.model.RepoCommitInfo;
import com.iecmp.repo.client.model.RepoInfo;
import com.iecmp.repo.client.model.RepoUserInfo;
import com.iecmp.repo.properties.RepoProperties;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * GitHub / Gitee 客户端测试：用 JDK 内置 HttpServer 起一个本地假平台，走真实 HTTP 链路。
 *
 * <p>覆盖路径解析、分页、两家平台的鉴权与时间格式差异，以及 401 / 404 / 429 / 5xx
 * 的错误归类与重试。全程不访问外网。
 */
class RepoPlatformClientTest {

    private static TimeZone originalTimeZone;

    private HttpServer server;
    private volatile int port;
    private volatile Function<Request, Response> handler;
    private final List<Request> requests = new CopyOnWriteArrayList<>();

    @BeforeAll
    static void fixTimeZone() {
        // 统计口径依赖 GMT+8 的墙上时间，固定时区让断言确定
        originalTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    @AfterAll
    static void restoreTimeZone() {
        TimeZone.setDefault(originalTimeZone);
    }

    @BeforeEach
    void setUp() throws IOException {
        requests.clear();
        handler = request -> new Response(200, "[]");

        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", this::handle);
        server.start();
        port = server.getAddress().getPort();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    // ------------------------------------------------------------------ 构造

    private RepoProperties properties(String platform, int pageSize, int maxRetries) {
        RepoProperties properties = new RepoProperties();
        properties.getSync().setMaxRetries(maxRetries);
        RepoProperties.Platform config = new RepoProperties.Platform();
        config.setCode(platform);
        config.setName(platform);
        config.setApiBase("http://127.0.0.1:" + port);
        config.setPageSize(pageSize);
        properties.setPlatform(List.of(config));
        return properties;
    }

    private GitHubPlatformClient github(int pageSize) {
        return new GitHubPlatformClient(properties("github", pageSize, 0), new ObjectMapper());
    }

    private GiteePlatformClient gitee(int pageSize) {
        return new GiteePlatformClient(properties("gitee", pageSize, 0), new ObjectMapper());
    }

    // ------------------------------------------------------------------ 账号

    @Test
    @DisplayName("GitHub：/user 解析账号信息，令牌走 Bearer 头")
    void githubFetchUser() {
        handler = request -> new Response(200,
                "{\"login\":\"octocat\",\"name\":\"The Octocat\",\"avatar_url\":\"https://avatar\"}");

        RepoUserInfo user = github(100).fetchUser("tok");

        assertThat(user.getLogin()).isEqualTo("octocat");
        assertThat(user.getName()).isEqualTo("The Octocat");
        assertThat(user.getAvatarUrl()).isEqualTo("https://avatar");
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).path()).isEqualTo("/user");
        assertThat(requests.get(0).headers().get("authorization")).isEqualTo("Bearer tok");
        assertThat(requests.get(0).headers().get("x-github-api-version")).isEqualTo("2022-11-28");
    }

    @Test
    @DisplayName("Gitee：令牌走 access_token 查询参数，不带 Authorization 头")
    void giteeFetchUserUsesQueryToken() {
        handler = request -> new Response(200, "{\"login\":\"gitee-user\"}");

        RepoUserInfo user = gitee(100).fetchUser("tok");

        assertThat(user.getLogin()).isEqualTo("gitee-user");
        assertThat(requests.get(0).query().get("access_token")).isEqualTo("tok");
        assertThat(requests.get(0).headers().get("authorization")).isNull();
    }

    // ------------------------------------------------------------------ 仓库

    @Test
    @DisplayName("GitHub：仓库分页拉全，解析 star / fork / 推送时间")
    void githubFetchReposPagination() {
        handler = request -> {
            if ("1".equals(request.query().get("page"))) {
                return new Response(200, """
                        [{"name":"repo-a","html_url":"https://github.com/o/repo-a",
                          "owner":{"login":"o"},"language":"Java","default_branch":"main",
                          "stargazers_count":3,"forks_count":1,"pushed_at":"2026-01-10T02:00:00Z"},
                         {"name":"repo-b","html_url":"https://github.com/o/repo-b","owner":{"login":"o"}}]
                        """);
            }
            return new Response(200, """
                    [{"name":"repo-c","html_url":"https://github.com/o/repo-c","owner":{"login":"o"}}]
                    """);
        };

        List<RepoInfo> repos = github(2).fetchRepos("tok");

        assertThat(repos).hasSize(3);
        assertThat(repos).extracting(RepoInfo::getRepoName)
                .containsExactly("repo-a", "repo-b", "repo-c");
        RepoInfo first = repos.get(0);
        assertThat(first.getOwnerName()).isEqualTo("o");
        assertThat(first.getLanguage()).isEqualTo("Java");
        assertThat(first.getDefaultBranch()).isEqualTo("main");
        assertThat(first.getStarCount()).isEqualTo(3);
        assertThat(first.getForkCount()).isEqualTo(1);
        // 02:00Z 在 GMT+8 下是当天 10:00
        assertThat(first.getLastPushTime()).isEqualTo(LocalDateTime.of(2026, 1, 10, 10, 0));

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).query().get("affiliation")).isEqualTo("owner");
        assertThat(requests.get(0).query().get("per_page")).isEqualTo("2");
    }

    @Test
    @DisplayName("分页：单页数量不足 pageSize 时不再请求下一页")
    void stopsWhenPageNotFull() {
        handler = request -> new Response(200,
                "[{\"name\":\"only\",\"html_url\":\"https://x/only\",\"owner\":{\"login\":\"o\"}}]");

        assertThat(github(100).fetchRepos("tok")).hasSize(1);
        assertThat(requests).hasSize(1);
    }

    @Test
    @DisplayName("分页：达到 max-pages 上限时停止，避免异常分页拉爆")
    void stopsAtMaxPages() {
        RepoProperties properties = properties("github", 1, 0);
        properties.getSync().setMaxPages(3);
        handler = request -> new Response(200,
                "[{\"name\":\"r\",\"html_url\":\"https://x/r\",\"owner\":{\"login\":\"o\"}}]");

        List<RepoInfo> repos = new GitHubPlatformClient(properties, new ObjectMapper()).fetchRepos("tok");

        assertThat(repos).hasSize(3);
        assertThat(requests).hasSize(3);
    }

    // ------------------------------------------------------------------ 分支

    @Test
    @DisplayName("分支：解析 name，跳过空名")
    void fetchBranches() {
        handler = request -> new Response(200,
                "[{\"name\":\"main\"},{\"name\":\"\"},{\"name\":\"dev\"},{\"name\":null}]");

        assertThat(github(100).fetchBranches("tok", "o", "r"))
                .containsExactly("main", "dev");
        assertThat(requests.get(0).path()).isEqualTo("/repos/o/r/branches");
    }

    // ------------------------------------------------------------------ 提交

    @Test
    @DisplayName("GitHub 提交：解析 sha / 作者 / 时间，父提交数 > 1 判为合并提交")
    void githubFetchCommits() {
        handler = request -> new Response(200, """
                [{"sha":"aaaa0001",
                  "commit":{"message":"feat: init\\n\\nbody","author":
                    {"name":"张三","email":"a@x","date":"2026-01-10T02:00:00Z"}},
                  "parents":[{"sha":"p1"},{"sha":"p2"}],
                  "stats":{"additions":12,"deletions":3}},
                 {"sha":"bbbb0002",
                  "commit":{"message":"feat: b","author":{"name":"李四","date":"2026-01-11T00:00:00Z"}},
                  "parents":[{"sha":"p1"}]}]
                """);

        List<RepoCommitInfo> commits = github(100).fetchCommits("tok", "o", "r", "main",
                LocalDateTime.of(2026, 3, 1, 1, 55));

        assertThat(commits).hasSize(2);
        RepoCommitInfo first = commits.get(0);
        assertThat(first.getCommitSha()).isEqualTo("aaaa0001");
        assertThat(first.getMessage()).isEqualTo("feat: init body");
        assertThat(first.getAuthorName()).isEqualTo("张三");
        assertThat(first.getAuthorEmail()).isEqualTo("a@x");
        assertThat(first.getCommitTime()).isEqualTo(LocalDateTime.of(2026, 1, 10, 10, 0));
        assertThat(first.getIsMerge()).isEqualTo(1);
        assertThat(first.getAddLines()).isEqualTo(12);
        assertThat(first.getDelLines()).isEqualTo(3);

        // 列表没给 stats 时保持 null，由采集层再查详情
        assertThat(commits.get(1).getAddLines()).isNull();
        assertThat(commits.get(1).getIsMerge()).isZero();

        // since 必须是 UTC 的 Z 格式：本地 01:55 → UTC 前一天 17:55
        assertThat(requests.get(0).query().get("since")).isEqualTo("2026-02-28T17:55:00Z");
        assertThat(requests.get(0).query().get("sha")).isEqualTo("main");
        assertThat(requests.get(0).path()).isEqualTo("/repos/o/r/commits");
    }

    @Test
    @DisplayName("Gitee 提交：since 带 +08:00 偏移，列表无 stats 时为 null")
    void giteeFetchCommits() {
        handler = request -> new Response(200, """
                [{"sha":"cccc0001",
                  "commit":{"message":"fix","author":{"name":"王五","date":"2026-02-03T10:00:00+08:00"}},
                  "parents":[{"sha":"p1"}]}]
                """);

        List<RepoCommitInfo> commits = gitee(100).fetchCommits("tok", "o", "r", "main",
                LocalDateTime.of(2026, 3, 1, 1, 55));

        assertThat(commits).hasSize(1);
        assertThat(commits.get(0).getCommitTime()).isEqualTo(LocalDateTime.of(2026, 2, 3, 10, 0));
        assertThat(commits.get(0).getAddLines()).isNull();
        assertThat(requests.get(0).query().get("since")).isEqualTo("2026-03-01T01:55:00+08:00");
        assertThat(requests.get(0).query().get("access_token")).isEqualTo("tok");
    }

    @Test
    @DisplayName("提交详情：取 stats 的增删行数，缺 stats 返回 null")
    void fetchCommitDetail() {
        handler = request -> request.path().endsWith("/aaaa0001")
                ? new Response(200, "{\"sha\":\"aaaa0001\",\"stats\":{\"additions\":20,\"deletions\":7}}")
                : new Response(200, "{\"sha\":\"bbbb0002\"}");

        GitHubPlatformClient client = github(100);
        RepoCommitInfo detail = client.fetchCommitDetail("tok", "o", "r", "aaaa0001");
        assertThat(detail.getAddLines()).isEqualTo(20);
        assertThat(detail.getDelLines()).isEqualTo(7);
        assertThat(detail.getCommitSha()).isEqualTo("aaaa0001");

        assertThat(client.fetchCommitDetail("tok", "o", "r", "bbbb0002")).isNull();
    }

    // ------------------------------------------------------------------ 错误

    @Test
    @DisplayName("401 / 403 归为令牌无效，不可重试")
    void unauthorizedIsInvalidToken() {
        handler = request -> new Response(401, "{\"message\":\"Bad credentials\"}");

        assertThatThrownBy(() -> github(100).fetchUser("bad"))
                .isInstanceOf(RepoApiException.class)
                .satisfies(e -> {
                    RepoApiException api = (RepoApiException) e;
                    assertThat(api.isInvalidToken()).isTrue();
                    assertThat(api.isRetryable()).isFalse();
                });

        // 令牌无效不重试，只打一次
        assertThat(requests).hasSize(1);
    }

    @Test
    @DisplayName("404 归为调用失败，不可重试，错误信息带响应体")
    void notFoundIsNotRetried() {
        handler = request -> new Response(404, "{\"message\":\"Not Found\"}");

        assertThatThrownBy(() -> github(100).fetchUser("tok"))
                .isInstanceOf(RepoApiException.class)
                .satisfies(e -> {
                    RepoApiException api = (RepoApiException) e;
                    assertThat(api.isInvalidToken()).isFalse();
                    assertThat(api.isRetryable()).isFalse();
                    assertThat(api.getMessage()).contains("404").contains("Not Found");
                });

        assertThat(requests).hasSize(1);
    }

    @Test
    @DisplayName("5xx 可重试：按 max-retries 重试后仍失败才抛出")
    void serverErrorRetried() {
        AtomicInteger calls = new AtomicInteger();
        handler = request -> {
            calls.incrementAndGet();
            return new Response(503, "unavailable");
        };

        GitHubPlatformClient client =
                new GitHubPlatformClient(properties("github", 100, 1), new ObjectMapper());

        assertThatThrownBy(() -> client.fetchUser("tok"))
                .isInstanceOf(RepoApiException.class)
                .satisfies(e -> {
                    RepoApiException api = (RepoApiException) e;
                    assertThat(api.isRetryable()).isTrue();
                    assertThat(api.getMessage()).contains("503");
                });

        // 首次 + 1 次重试
        assertThat(calls.get()).isEqualTo(2);
    }

    @Test
    @DisplayName("429 限流归为可重试的调用失败")
    void rateLimitedIsRetryable() {
        handler = request -> new Response(429, "rate limited");

        assertThatThrownBy(() -> github(100).fetchUser("tok"))
                .isInstanceOf(RepoApiException.class)
                .satisfies(e -> {
                    RepoApiException api = (RepoApiException) e;
                    assertThat(api.isRetryable()).isTrue();
                    assertThat(api.isInvalidToken()).isFalse();
                });
    }

    // ------------------------------------------------------------------ 假平台

    private void handle(HttpExchange exchange) throws IOException {
        MultiValueMap<String, String> params =
                UriComponentsBuilder.fromUri(exchange.getRequestURI()).build().getQueryParams();
        Map<String, String> query = new HashMap<>();
        params.forEach((key, values) -> {
            if (!values.isEmpty()) {
                query.put(key, values.get(0));
            }
        });
        Map<String, String> headers = new HashMap<>();
        exchange.getRequestHeaders().forEach((key, values) -> {
            if (!values.isEmpty()) {
                headers.put(key.toLowerCase(Locale.ROOT), values.get(0));
            }
        });

        Request request = new Request(exchange.getRequestURI().getPath(), query, headers);
        requests.add(request);

        Response response = handler.apply(request);
        byte[] body = response.body() == null
                ? new byte[0]
                : response.body().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
        exchange.sendResponseHeaders(response.status(), body.length == 0 ? -1 : body.length);
        if (body.length > 0) {
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        }
        exchange.close();
    }

    /** 一次收到的请求。 */
    private record Request(String path, Map<String, String> query, Map<String, String> headers) {
    }

    /** 假平台的响应。 */
    private record Response(int status, String body) {
    }
}
