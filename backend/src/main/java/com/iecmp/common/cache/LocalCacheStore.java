package com.iecmp.common.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单机进程内缓存。
 *
 * <p>平台按单实例部署（最多二三十人），令牌黑名单、验证码、字典缓存与限流计数
 * 都只在本进程内使用，无需引入 Redis。所有条目都带过期时间，读取时惰性清理，
 * 另有定时任务兜底清理长期不再访问的过期 Key。
 *
 * <p><b>部署约束</b>：进程重启后缓存全部丢失（令牌黑名单失效、验证码需重新获取），
 * 若将来改为多实例部署，必须替换为 Redis 等外部共享存储。
 */
@Slf4j
@Component
public class LocalCacheStore {

    private static final long NEVER_EXPIRE = Long.MAX_VALUE;

    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    /**
     * 写入缓存。
     *
     * @param ttlSeconds 过期时间（秒），小于等于 0 表示不过期
     */
    public void set(String key, String value, long ttlSeconds) {
        if (key == null || value == null) {
            return;
        }
        store.put(key, new Entry(value, expireAt(ttlSeconds)));
    }

    /** 读取缓存，已过期返回 {@code null}。 */
    public String get(String key) {
        if (key == null) {
            return null;
        }
        Entry entry = store.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.expired()) {
            store.remove(key, entry);
            return null;
        }
        return entry.value();
    }

    /** 判断 Key 是否存在且未过期。 */
    public boolean hasKey(String key) {
        return get(key) != null;
    }

    /** 删除 Key。 */
    public void delete(String key) {
        if (key != null) {
            store.remove(key);
        }
    }

    /**
     * 原子自增，语义与 Redis {@code INCR} 一致：
     * Key 不存在时从 1 开始并设置 TTL，已存在时只加一，不重置剩余有效期。
     */
    public long increment(String key, long ttlSeconds) {
        Entry result = store.compute(key, (k, old) -> {
            long now = System.currentTimeMillis();
            if (old == null || old.expired()) {
                return new Entry("1", expireAt(ttlSeconds));
            }
            long next;
            try {
                next = Long.parseLong(old.value()) + 1;
            } catch (NumberFormatException e) {
                next = 1;
            }
            return new Entry(String.valueOf(next), old.expireAt());
        });
        return Long.parseLong(result.value());
    }

    /** 定时清理过期条目，避免只写不读的 Key（如令牌黑名单）长期占用内存。 */
    @Scheduled(fixedDelay = 60_000L)
    public void purgeExpired() {
        int before = store.size();
        store.entrySet().removeIf(entry -> entry.getValue().expired());
        int removed = before - store.size();
        if (removed > 0) {
            log.debug("本地缓存清理过期条目 {} 个，剩余 {} 个", removed, store.size());
        }
    }

    private static long expireAt(long ttlSeconds) {
        if (ttlSeconds <= 0) {
            return NEVER_EXPIRE;
        }
        long millis = System.currentTimeMillis() + ttlSeconds * 1000L;
        return millis < 0 ? NEVER_EXPIRE : millis;
    }

    private record Entry(String value, long expireAt) {

        boolean expired() {
            return expireAt != NEVER_EXPIRE && expireAt <= System.currentTimeMillis();
        }
    }
}
