package com.iecmp.repo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 模块二（代码仓库关联）自定义配置（架构文档 7.3 节配置项清单）。
 *
 * <p>覆盖三块内容：
 * <ol>
 *   <li>{@code repo.crypto}：{@code repo_account.access_token} 的 AES 密钥（接口文档 8.2.2）；</li>
 *   <li>{@code repo.platform}：接口 2-01 下发的平台清单与令牌申请入口，同时决定要接入哪些第三方平台客户端；</li>
 *   <li>{@code repo.contribution} / {@code repo.sync}：贡献度权重（接口文档 8.4.4）与采集节流参数（架构文档 4.6 节）。</li>
 * </ol>
 */
@Data
@Component
@ConfigurationProperties(prefix = "repo")
public class RepoProperties {

    /** 令牌加解密配置。 */
    private Crypto crypto = new Crypto();

    /** 贡献度算法权重。 */
    private Contribution contribution = new Contribution();

    /** 采集节流与超时配置。 */
    private Sync sync = new Sync();

    /** 支持的托管平台清单，顺序即接口 2-01 的返回顺序。 */
    private List<Platform> platform = new ArrayList<>();

    /** 令牌加解密配置。 */
    @Data
    public static class Crypto {

        /**
         * AES 密钥。
         *
         * <p>长度须为 16 / 24 / 32 个字符（对应 AES-128 / 192 / 256）；其它长度会先做 SHA-256
         * 再取 32 字节，保证一定能构造出密钥而不至于启动失败。<b>上线前必须通过环境变量
         * {@code IECMP_REPO_AES_KEY} 注入随机密钥</b>，更换密钥会导致存量令牌无法解密。
         */
        private String aesKey;
    }

    /** 贡献度算法权重，对应接口文档 8.4.4 的 {@code repo.contribution.weight.*}。 */
    @Data
    public static class Contribution {

        /** 权重配置。 */
        private Weight weight = new Weight();
    }

    /** 贡献度公式各项权重。 */
    @Data
    public static class Weight {

        /** 提交数权重，默认 1。 */
        private BigDecimal commit = BigDecimal.ONE;

        /** 活跃天数权重，默认 2。 */
        private BigDecimal activeDay = new BigDecimal("2");

        /**
         * 代码行数权重，默认 0.01。
         *
         * <p>「代码行数」口径取 {@code add_lines + del_lines}（总变更行数），
         * 因为接口文档 8.4.4 只写了「代码行数 × 0.01」而未区分新增/删除，
         * 而 {@code addLines}、{@code delLines} 都是对外返回的指标。若团队会议确定只算新增行，
         * 把 {@code del_lines} 从公式中剔除即可——本项权重与字段结构都不变。
         */
        private BigDecimal codeLine = new BigDecimal("0.01");
    }

    /** 采集节流与超时配置（架构文档 4.6 节实现要点 2、3）。 */
    @Data
    public static class Sync {

        /** 定时采集 cron，默认每日 01:00；同时用于计算接口 2-06 的 {@code nextSyncTime}。 */
        private String cron = "0 0 1 * * ?";

        /** 单次采集的分布式锁 TTL（秒），超时自动释放，避免进程崩溃后永久占锁。 */
        private long lockTtlSeconds = 1800L;

        /** 相邻两次第三方请求之间的间隔（毫秒），按仓库维度串行限流。 */
        private long requestIntervalMs = 200L;

        /** 采集提交明细时是否再查一次提交详情以补齐增删行数；关闭后 {@code add_lines}/{@code del_lines} 恒为 0。 */
        private boolean commitDetail = true;

        /** 单个列表接口最多翻页数，防止异常分页导致无限循环。 */
        private int maxPages = 20;

        /** 连接超时（毫秒）。 */
        private int connectTimeoutMs = 5000;

        /** 读取超时（毫秒）。 */
        private int readTimeoutMs = 15000;

        /** 第三方调用失败重试次数（不含首次）。 */
        private int maxRetries = 2;

        /** 增量采集时向前回溯的分钟数，覆盖第三方提交时间与服务端时钟的微小偏差，重复部分由 (project_id, commit_sha) 去重。 */
        private long incrementalOverlapMinutes = 5L;
    }

    /** 单个托管平台。 */
    @Data
    public static class Platform {

        /** 平台标识：gitee / github，与 {@code repo_account.platform} 取值一致。 */
        private String code;

        /** 平台展示名称。 */
        private String name;

        /** 令牌申请入口地址。 */
        private String tokenUrl;

        /** 令牌权限要求提示文案。 */
        private String tokenTip;

        /** 平台 REST 接口根地址。 */
        private String apiBase;

        /** 列表类接口的每页条数。 */
        private int pageSize = 100;
    }

    /** 按平台标识取平台配置，查不到返回 null。 */
    public Platform findPlatform(String code) {
        if (code == null) {
            return null;
        }
        for (Platform item : platform) {
            if (code.equalsIgnoreCase(item.getCode())) {
                return item;
            }
        }
        return null;
    }
}
