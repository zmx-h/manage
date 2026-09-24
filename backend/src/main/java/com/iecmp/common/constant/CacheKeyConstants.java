package com.iecmp.common.constant;

/**
 * Redis Key 常量（架构文档 4.5 节）。
 *
 * <p>所有 Key 统一以 {@code iecmp:} 开头；计数类 Key 一律带 TTL，避免无限增长。
 */
public final class CacheKeyConstants {

    private CacheKeyConstants() {
    }

    /** 令牌黑名单：{@code iecmp:token:blacklist:{token}}，TTL 为令牌剩余有效期。 */
    public static final String TOKEN_BLACKLIST_PREFIX = "iecmp:token:blacklist:";

    /** 图形验证码：{@code iecmp:captcha:{captchaKey}}，TTL 300 秒，一次性使用后立即删除。 */
    public static final String CAPTCHA_PREFIX = "iecmp:captcha:";

    /** 数据字典缓存：{@code iecmp:dict:{dictType}}，TTL 1 小时。 */
    public static final String DICT_PREFIX = "iecmp:dict:";

    /** 登录失败计数：{@code iecmp:login:fail:{username}}，TTL 600 秒。 */
    public static final String LOGIN_FAIL_PREFIX = "iecmp:login:fail:";

    /** 注册申请同 IP 限流：{@code iecmp:limit:register:{ip}}，3 次/小时。 */
    public static final String LIMIT_REGISTER_PREFIX = "iecmp:limit:register:";

    /** 验证码同 IP 限流：{@code iecmp:limit:captcha:{ip}}，10 次/分钟。 */
    public static final String LIMIT_CAPTCHA_PREFIX = "iecmp:limit:captcha:";

    /** 各类 TTL（秒）。 */
    public static final long CAPTCHA_TTL_SECONDS = 300L;

    public static final long DICT_TTL_SECONDS = 3600L;

    public static final long LOGIN_FAIL_TTL_SECONDS = 600L;

    public static final long LIMIT_REGISTER_TTL_SECONDS = 3600L;

    public static final long LIMIT_CAPTCHA_TTL_SECONDS = 60L;

    /** 登录失败锁定阈值。 */
    public static final int LOGIN_FAIL_MAX_TIMES = 5;

    /** 同一 IP 每小时最多提交的注册申请次数。 */
    public static final int REGISTER_LIMIT_PER_HOUR = 3;

    /** 同一 IP 每分钟最多获取的验证码次数。 */
    public static final int CAPTCHA_LIMIT_PER_MINUTE = 10;
}
