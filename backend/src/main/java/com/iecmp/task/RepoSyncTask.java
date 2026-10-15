package com.iecmp.task;

import com.iecmp.repo.service.RepoSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务：代码仓库提交采集（架构文档 4.6 节）。
 *
 * <p>频率：每日凌晨 1 次，cron 由 {@code repo.sync.cron} 配置（接口 2-06 的
 * {@code nextSyncTime} 也用同一份配置现算，改这里就同时改了两处）。
 *
 * <p>职责：遍历启用状态的 {@code repo_account}，拉取仓库<b>所有分支</b>的提交写入
 * {@code repo_commit}，以 {@code commit_sha} 去重，并写入 {@code repo_sync_log}
 * 记录耗时与结果（{@code sync_type = 1} 定时自动）。
 *
 * <p><b>本类只做一件事：把调度交给 {@link RepoSyncService}</b>，采集逻辑一行都不在这里。
 * 因为同时有三条链路要采数据（定时、管理员补采 2-17、绑定/换令牌后的即时采集），
 * 逻辑写两份必然漂移；三条链路共用同一把 Redis 锁，也要求入口收敛到同一个方法。
 *
 * <p>多实例部署下 {@code syncAllScheduled} 抢不到锁会静默跳过——那不是异常，
 * 是「另一个实例正在采」，跳过即可，明天还会再来。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RepoSyncTask {

    private final RepoSyncService repoSyncService;

    /** 默认每日 01:00 执行，可由 {@code repo.sync.cron} 覆盖。 */
    @Scheduled(cron = "${repo.sync.cron:0 0 1 * * ?}")
    public void syncAll() {
        log.info("定时采集任务开始");
        try {
            repoSyncService.syncAllScheduled();
        } catch (Exception e) {
            // 调度线程上抛异常会打断后续调度，这里兜住；具体失败已由 RepoSyncService 写进 repo_sync_log
            log.error("定时采集任务异常", e);
        }
    }
}
