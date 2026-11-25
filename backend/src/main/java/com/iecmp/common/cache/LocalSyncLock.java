package com.iecmp.common.cache;

import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

/**
 * 单机进程内互斥锁。
 *
 * <p>代码仓库采集由定时任务与接口 2-17 手动补采共用同一把锁，单实例部署下
 * 用进程内信号量即可保证同一时刻只有一条采集链路在跑。进程崩溃时锁随进程释放，
 * 不会出现 Redis 锁需要 TTL 兜底的问题。
 *
 * <p><b>部署约束</b>：该锁只在当前 JVM 内生效。若将来改为多实例部署，
 * 必须替换为 Redis {@code SETNX} 或调度中心等外部互斥方案。
 */
@Component
public class LocalSyncLock {

    private final Semaphore semaphore = new Semaphore(1);

    /** 尝试获取锁，非阻塞。 */
    public boolean tryAcquire() {
        return semaphore.tryAcquire();
    }

    /** 释放锁，未持有时调用不会改变信号量。 */
    public void release() {
        if (semaphore.availablePermits() == 0) {
            semaphore.release();
        }
    }
}
