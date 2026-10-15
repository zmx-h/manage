package com.iecmp.repo.client;

import lombok.Getter;

/**
 * 第三方托管平台调用异常。
 *
 * <p>之所以要区分「令牌无效」与「调用失败」：接口文档 8.2.2 / 8.2.4 把两者定义为不同错误码——
 * 前者是 {@code 20003 访问令牌无效或已过期}（要让成员换令牌），后者是
 * {@code 20007 第三方平台接口调用失败}（等下次自动采集即可）。若混成一个异常，
 * 前端就无法给出正确的引导文案。
 */
@Getter
public class RepoApiException extends RuntimeException {

    /** 令牌无效或权限不足（第三方返回 401 / 403）。 */
    public static final String INVALID_TOKEN = "INVALID_TOKEN";

    /** 其它调用失败：网络超时、限流、第三方 5xx 等。 */
    public static final String API_FAILED = "API_FAILED";

    /** 失败类型，取值见本类常量。 */
    private final String kind;

    /** 是否值得重试：限流、第三方 5xx、网络抖动为 true；令牌无效与 4xx 为 false。 */
    private final boolean retryable;

    public RepoApiException(String kind, String message) {
        this(kind, message, false, null);
    }

    public RepoApiException(String kind, String message, Throwable cause) {
        this(kind, message, false, cause);
    }

    public RepoApiException(String kind, String message, boolean retryable) {
        this(kind, message, retryable, null);
    }

    public RepoApiException(String kind, String message, boolean retryable, Throwable cause) {
        super(message, cause);
        this.kind = kind;
        this.retryable = retryable;
    }

    /** 是否属于「令牌无效」，供业务层映射到错误码 20003。 */
    public boolean isInvalidToken() {
        return INVALID_TOKEN.equals(kind);
    }
}
