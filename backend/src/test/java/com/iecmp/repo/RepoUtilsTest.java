package com.iecmp.repo;

import com.iecmp.common.base.PageQuery;
import com.iecmp.repo.client.RepoApiException;
import com.iecmp.repo.client.RepoPlatformClient;
import com.iecmp.repo.client.RepoPlatformClientRegistry;
import com.iecmp.repo.enums.RepoErrorCode;
import com.iecmp.repo.properties.RepoProperties;
import com.iecmp.repo.util.RepoDateUtils;
import com.iecmp.repo.util.RepoPageUtils;
import com.iecmp.repo.util.RepoSortUtils;
import com.iecmp.repo.util.RepoTokenCipher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 模块二工具类纯逻辑测试：日期分桶、排序白名单、分页兜底、令牌加解密、平台注册表。
 *
 * <p>这些类不依赖 Spring 容器，全部直接构造，测试结果与数据库、网络无关。
 */
class RepoUtilsTest {

    // ---------------------------------------------------------------- 日期

    @Test
    @DisplayName("日期解析：只给日期时，起始取 00:00:00、截止取 23:59:59")
    void parseDateTimeDateOnly() {
        assertThat(RepoDateUtils.parseDateTime("2026-03-02", false))
                .isEqualTo(LocalDateTime.of(2026, 3, 2, 0, 0, 0));
        assertThat(RepoDateUtils.parseDateTime("2026-03-02", true))
                .isEqualTo(LocalDateTime.of(2026, 3, 2, 23, 59, 59));
    }

    @Test
    @DisplayName("日期解析：带时分秒时原样解析，非法输入返回 null")
    void parseDateTimeInvalid() {
        assertThat(RepoDateUtils.parseDateTime("2026-03-02 08:30:15", true))
                .isEqualTo(LocalDateTime.of(2026, 3, 2, 8, 30, 15));
        assertThat(RepoDateUtils.parseDateTime("2026-13-40", false)).isNull();
        assertThat(RepoDateUtils.parseDateTime("not-a-date", false)).isNull();
        assertThat(RepoDateUtils.parseDateTime("  ", false)).isNull();
        assertThat(RepoDateUtils.parseDate("2026-03-02")).isEqualTo(LocalDate.of(2026, 3, 2));
    }

    @Test
    @DisplayName("年份解析：只认 4 位数字")
    void parseYear() {
        assertThat(RepoDateUtils.parseYear("2026")).isEqualTo(2026);
        assertThat(RepoDateUtils.parseYear("26")).isNull();
        assertThat(RepoDateUtils.parseYear("20a6")).isNull();
        assertThat(RepoDateUtils.parseYear(null)).isNull();
    }

    @Test
    @DisplayName("周期标签：按周用 ISO 周，跨年周归入周所属年份")
    void periodLabelWeekAcrossYear() {
        // 2025-12-29 是周一，属于 ISO 2026 年第 1 周
        assertThat(RepoDateUtils.periodLabel(LocalDate.of(2025, 12, 29), RepoDateUtils.GRANULARITY_WEEK))
                .isEqualTo("2026-第1周");
        assertThat(RepoDateUtils.periodLabel(LocalDate.of(2026, 1, 4), RepoDateUtils.GRANULARITY_WEEK))
                .isEqualTo("2026-第1周");
        assertThat(RepoDateUtils.periodLabel(LocalDate.of(2026, 1, 5), RepoDateUtils.GRANULARITY_WEEK))
                .isEqualTo("2026-第2周");
    }

    @Test
    @DisplayName("周期枚举：区间内每个周期只出现一次，无提交的周期也会补出来")
    void periodLabels() {
        assertThat(RepoDateUtils.periodLabels(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 3),
                RepoDateUtils.GRANULARITY_DAY))
                .containsExactly("2026-01-01", "2026-01-02", "2026-01-03");

        assertThat(RepoDateUtils.periodLabels(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31),
                RepoDateUtils.GRANULARITY_MONTH))
                .containsExactly("2026-01", "2026-02", "2026-03");

        // 起止倒置返回空列表，不抛异常
        assertThat(RepoDateUtils.periodLabels(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1),
                RepoDateUtils.GRANULARITY_DAY)).isEmpty();
    }

    @Test
    @DisplayName("粒度收敛：非法值一律按日处理")
    void normalizeGranularity() {
        assertThat(RepoDateUtils.normalizeGranularity(null)).isEqualTo(RepoDateUtils.GRANULARITY_DAY);
        assertThat(RepoDateUtils.normalizeGranularity(9)).isEqualTo(RepoDateUtils.GRANULARITY_DAY);
        assertThat(RepoDateUtils.normalizeGranularity(2)).isEqualTo(RepoDateUtils.GRANULARITY_WEEK);
        assertThat(RepoDateUtils.normalizeGranularity(3)).isEqualTo(RepoDateUtils.GRANULARITY_MONTH);
    }

    // ---------------------------------------------------------------- 排序

    private static final Map<String, String> SORT_FIELDS = Map.of(
            "create_time", "a.create_time",
            "last_sync_time", "a.last_sync_time");

    @Test
    @DisplayName("排序白名单：合法字段映射为写死的列名，方向大小写不敏感")
    void sortClause() {
        assertThat(RepoSortUtils.resolveClause("last_sync_time desc", SORT_FIELDS))
                .isEqualTo("a.last_sync_time DESC");
        assertThat(RepoSortUtils.resolveClause("CREATE_TIME ASC", SORT_FIELDS))
                .isEqualTo("a.create_time ASC");
        // 只给字段名时默认升序
        assertThat(RepoSortUtils.resolveClause("create_time", SORT_FIELDS))
                .isEqualTo("a.create_time ASC");
    }

    @Test
    @DisplayName("排序白名单：未知字段、非法方向、注入串一律回落为 null")
    void sortClauseRejectsInjection() {
        assertThat(RepoSortUtils.resolveClause("password desc", SORT_FIELDS)).isNull();
        assertThat(RepoSortUtils.resolveClause("create_time sideways", SORT_FIELDS)).isNull();
        assertThat(RepoSortUtils.resolveClause("create_time desc, (select 1)", SORT_FIELDS)).isNull();
        assertThat(RepoSortUtils.resolveClause("create_time desc; drop table t", SORT_FIELDS)).isNull();
        assertThat(RepoSortUtils.resolveClause(null, SORT_FIELDS)).isNull();
        assertThat(RepoSortUtils.resolveClause("  ", SORT_FIELDS)).isNull();
    }

    @Test
    @DisplayName("排序结果：列名与方向分离，供 QueryWrapper 使用")
    void sortRecord() {
        RepoSortUtils.Sort sort = RepoSortUtils.resolve("last_sync_time desc", SORT_FIELDS);
        assertThat(sort).isNotNull();
        assertThat(sort.column()).isEqualTo("a.last_sync_time");
        assertThat(sort.asc()).isFalse();

        assertThat(RepoSortUtils.resolve("nope", SORT_FIELDS)).isNull();
    }

    // ---------------------------------------------------------------- 分页

    @Test
    @DisplayName("分页兜底：页码至少 1，每页 1~100，默认 10")
    void pageClamp() {
        PageQuery query = new PageQuery();

        assertThat(RepoPageUtils.of(query).getSize()).isEqualTo(RepoPageUtils.DEFAULT_PAGE_SIZE);

        query.setPageNum(0);
        query.setPageSize(0);
        assertThat(RepoPageUtils.of(query).getCurrent()).isEqualTo(1);
        assertThat(RepoPageUtils.of(query).getSize()).isEqualTo(1);

        query.setPageNum(3);
        query.setPageSize(9999);
        assertThat(RepoPageUtils.of(query).getCurrent()).isEqualTo(3);
        assertThat(RepoPageUtils.of(query).getSize()).isEqualTo(RepoPageUtils.MAX_PAGE_SIZE);

        query.setPageNum(-5);
        assertThat(RepoPageUtils.of(query).getCurrent()).isEqualTo(1);
    }

    // ---------------------------------------------------------------- 令牌

    private static RepoProperties propsWithKey(String key) {
        RepoProperties properties = new RepoProperties();
        properties.getCrypto().setAesKey(key);
        return properties;
    }

    @Test
    @DisplayName("令牌加解密：可逆，且同一明文两次加密的密文不同")
    void tokenRoundTrip() {
        RepoTokenCipher cipher = new RepoTokenCipher(propsWithKey("0123456789abcdef"));

        String first = cipher.encrypt("ghp_secret_token");
        String second = cipher.encrypt("ghp_secret_token");

        assertThat(cipher.decrypt(first)).isEqualTo("ghp_secret_token");
        assertThat(cipher.decrypt(second)).isEqualTo("ghp_secret_token");
        // 随机 IV：密文不能相等，否则等于泄露「两个账户用了同一个令牌」
        assertThat(first).isNotEqualTo(second);
    }

    @Test
    @DisplayName("令牌加解密：空值原样返回，解密空值返回 null")
    void tokenEmpty() {
        RepoTokenCipher cipher = new RepoTokenCipher(propsWithKey("0123456789abcdef"));

        assertThat(cipher.encrypt(null)).isNull();
        assertThat(cipher.encrypt("")).isEmpty();
        assertThat(cipher.decrypt(null)).isNull();
        assertThat(cipher.decrypt("")).isNull();
    }

    @Test
    @DisplayName("令牌加解密：换密钥或密文被篡改时解密返回 null，不抛 500")
    void tokenTamperAndWrongKey() {
        RepoTokenCipher cipher = new RepoTokenCipher(propsWithKey("0123456789abcdef"));
        String encrypted = cipher.encrypt("ghp_secret_token");

        RepoTokenCipher wrongKey = new RepoTokenCipher(propsWithKey("fedcba9876543210"));
        assertThat(wrongKey.decrypt(encrypted)).isNull();

        byte[] payload = Base64.getDecoder().decode(encrypted);
        payload[payload.length - 1] ^= 0x01;
        assertThat(cipher.decrypt(Base64.getEncoder().encodeToString(payload))).isNull();

        assertThat(cipher.decrypt("not-base64!!!")).isNull();
        assertThat(cipher.decrypt(Base64.getEncoder().encodeToString(new byte[4]))).isNull();
    }

    @Test
    @DisplayName("令牌加解密：密钥长度不是 16/24/32 时先做 SHA-256，不因配置写错启动失败")
    void tokenKeyLengthNormalized() {
        RepoTokenCipher cipher = new RepoTokenCipher(propsWithKey("short-key"));
        assertThat(cipher.decrypt(cipher.encrypt("token"))).isEqualTo("token");
    }

    @Test
    @DisplayName("令牌加解密：未配置密钥时加密直接失败，不降级存明文")
    void tokenMissingKey() {
        RepoTokenCipher cipher = new RepoTokenCipher(propsWithKey(null));
        assertThatThrownBy(() -> cipher.encrypt("token"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("repo.crypto.aes-key");
    }

    // ---------------------------------------------------------------- 注册表

    @Test
    @DisplayName("平台注册表：按平台标识取客户端，大小写不敏感")
    void registryLookup() {
        RepoPlatformClient github = mock(RepoPlatformClient.class);
        when(github.platform()).thenReturn("github");

        RepoPlatformClientRegistry registry =
                new RepoPlatformClientRegistry(List.of(github), new RepoProperties());

        assertThat(registry.supports("github")).isTrue();
        assertThat(registry.supports("GITHUB")).isTrue();
        assertThat(registry.supports("gitee")).isFalse();
        assertThat(registry.supports(null)).isFalse();
        assertThat(registry.get("GitHub")).isSameAs(github);
        assertThatThrownBy(() -> registry.get("gitee"))
                .isInstanceOf(RepoApiException.class)
                .hasMessageContaining("不支持的代码托管平台");
    }

    @Test
    @DisplayName("错误码映射：令牌无效 20003，其它调用失败 20007")
    void errorCodeMapping() {
        assertThat(RepoPlatformClientRegistry.toErrorCode(
                new RepoApiException(RepoApiException.INVALID_TOKEN, "401")))
                .isEqualTo(RepoErrorCode.E20003);
        assertThat(RepoPlatformClientRegistry.toErrorCode(
                new RepoApiException(RepoApiException.API_FAILED, "502")))
                .isEqualTo(RepoErrorCode.E20007);
        // 可重试标记不影响错误码
        assertThat(RepoPlatformClientRegistry.toErrorCode(
                new RepoApiException(RepoApiException.API_FAILED, "429", true)))
                .isEqualTo(RepoErrorCode.E20007);
    }
}
