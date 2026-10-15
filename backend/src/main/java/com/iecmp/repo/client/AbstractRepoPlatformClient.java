package com.iecmp.repo.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecmp.repo.properties.RepoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 第三方平台客户端公共实现：HTTP 调用、超时、重试与 JSON 解析。
 *
 * <p>三个约束直接来自架构文档 4.6 节实现要点 3「外部接口调用必须设超时与重试上限，
 * 避免 Gitee / GitHub 不可用时任务线程被长时间挂起」：
 * <ol>
 *   <li>连接 / 读取超时取自 {@code repo.sync.*-timeout-ms}；</li>
 *   <li>只对「限流、第三方 5xx、网络异常」重试，令牌无效与 4xx 立即失败，避免拿坏令牌反复打；</li>
 *   <li>响应统一按 {@link JsonNode} 解析——两家平台的字段有无、类型都不同，
 *       绑成强类型对象会因缺字段直接抛异常。</li>
 * </ol>
 */
@Slf4j
public abstract class AbstractRepoPlatformClient implements RepoPlatformClient {

    /** 分页页码从 1 开始。 */
    protected static final int FIRST_PAGE = 1;

    protected final RepoProperties repoProperties;

    protected final ObjectMapper objectMapper;

    private final RestClient restClient;

    protected AbstractRepoPlatformClient(RepoProperties repoProperties, ObjectMapper objectMapper) {
        this.repoProperties = repoProperties;
        this.objectMapper = objectMapper;

        RepoProperties.Sync sync = repoProperties.getSync();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(sync.getConnectTimeoutMs());
        factory.setReadTimeout(sync.getReadTimeoutMs());
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    /** 平台配置（apiBase、pageSize），由子类在构造后使用。 */
    protected RepoProperties.Platform platformConfig() {
        RepoProperties.Platform config = repoProperties.findPlatform(platform());
        if (config == null) {
            throw new RepoApiException(RepoApiException.API_FAILED,
                    "未配置平台 " + platform() + " 的 repo.platform 参数");
        }
        return config;
    }

    /**
     * 发起一次 GET 并解析为 JSON。
     *
     * @param path   相对 {@code apiBase} 的路径，如 {@code /user/repos}
     * @param query  查询参数，值为 null 的键会被跳过
     * @param headers 额外请求头（GitHub 用 Authorization，Gitee 用查询参数传令牌）
     */
    protected JsonNode getJson(String path, Map<String, Object> query, Map<String, String> headers) {
        String url = platformConfig().getApiBase() + path;
        int maxRetries = Math.max(0, repoProperties.getSync().getMaxRetries());
        RepoApiException last = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                return objectMapper.readTree(execute(url, query, headers));
            } catch (RepoApiException e) {
                if (!e.isRetryable() || attempt == maxRetries) {
                    throw e;
                }
                last = e;
            } catch (Exception e) {
                if (attempt == maxRetries) {
                    throw new RepoApiException(RepoApiException.API_FAILED, "第三方响应解析失败：" + e.getMessage(), e);
                }
                last = new RepoApiException(RepoApiException.API_FAILED, "第三方响应解析失败：" + e.getMessage(), true, e);
            }
            log.warn("[{}] 第 {} 次调用失败，准备重试：{}", platform(), attempt + 1, last.getMessage());
            sleep(200L * (attempt + 1));
        }
        throw last == null ? new RepoApiException(RepoApiException.API_FAILED, "第三方平台接口调用失败") : last;
    }

    /** 单次 HTTP 调用：按状态码把响应归类成「令牌无效」或「调用失败」。 */
    private String execute(String url, Map<String, Object> query, Map<String, String> headers) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> {
                        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
                        if (query != null) {
                            query.forEach((key, value) -> {
                                if (value != null) {
                                    builder.queryParam(key, value);
                                }
                            });
                        }
                        return builder.build().encode().toUri();
                    })
                    .headers(httpHeaders -> {
                        if (headers != null) {
                            headers.forEach(httpHeaders::add);
                        }
                    })
                    .exchange((HttpRequest request, RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse response) -> {
                        int status = response.getStatusCode().value();
                        String body = readBody(response);
                        if (status == 401 || status == 403) {
                            throw new RepoApiException(RepoApiException.INVALID_TOKEN,
                                    "第三方平台返回 " + status + "，令牌无效或权限不足");
                        }
                        if (status == 429) {
                            throw new RepoApiException(RepoApiException.API_FAILED,
                                    "第三方平台限流（429），稍后重试", true);
                        }
                        if (status >= 500) {
                            throw new RepoApiException(RepoApiException.API_FAILED,
                                    "第三方平台返回 " + status, true);
                        }
                        if (status >= 400) {
                            throw new RepoApiException(RepoApiException.API_FAILED,
                                    "第三方平台返回 " + status + "：" + abbreviate(body));
                        }
                        return body;
                    });
        } catch (ResourceAccessException e) {
            // 连接超时 / 读取超时 / DNS 失败
            throw new RepoApiException(RepoApiException.API_FAILED, "访问第三方平台超时或网络不可达：" + e.getMessage(), true, e);
        } catch (RestClientException e) {
            throw new RepoApiException(RepoApiException.API_FAILED, "调用第三方平台失败：" + e.getMessage(), true, e);
        }
    }

    private String readBody(RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse response) throws IOException {
        return StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
    }

    /** 取文本字段，缺失或为 null 时返回 null。 */
    protected static String text(JsonNode node, String field) {
        if (node == null) {
            return null;
        }
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    /** 取整数字段，缺失或为 null 时返回 fallback。 */
    protected static Integer integer(JsonNode node, String field, Integer fallback) {
        if (node == null) {
            return fallback;
        }
        JsonNode value = node.get(field);
        return value == null || !value.isNumber() ? fallback : value.asInt();
    }

    /** 按第三方返回的时间文本解析，格式不识别时返回 null 而不是抛异常。 */
    protected static java.time.LocalDateTime parseTime(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String trimmed = text.trim();
        try {
            return java.time.OffsetDateTime.parse(trimmed).toLocalDateTime();
        } catch (Exception ignored) {
            // 继续尝试其它格式
        }
        try {
            return java.time.LocalDateTime.parse(trimmed.replace(' ', 'T'));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 按 DB 字段长度截断，避免一个超长仓库名把整批插入打回。
     *
     * <p>{@code utf8mb4} 下 MySQL 的 {@code VARCHAR(n)} 按字符计数，与 Java 的 {@code length()} 一致。
     */
    protected static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    /** 提交信息里的换行与多余空白压成单行，避免列表展示错位。 */
    protected static String singleLine(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return truncate(value.replaceAll("\\s+", " ").trim(), maxLength);
    }

    private static String abbreviate(String body) {
        if (body == null) {
            return "";
        }
        String flattened = body.replaceAll("\\s+", " ").trim();
        return flattened.length() > 200 ? flattened.substring(0, 200) : flattened;
    }

    /** 采集过程中的主动限流间隔，避免触发第三方风控。 */
    protected static void sleep(long millis) {
        if (millis <= 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RepoApiException(RepoApiException.API_FAILED, "采集任务被中断");
        }
    }
}
