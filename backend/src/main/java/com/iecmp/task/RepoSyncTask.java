package com.iecmp.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务：代码仓库提交采集（架构文档 4.6 节）。
 *
 * <p>频率：每日凌晨 1 次。职责：遍历启用状态的 {@code repo_account}，
 * 拉取仓库**所有分支**的提交写入 {@code repo_commit}，以 {@code commit_sha} 去重，
 * 并写入 {@code repo_sync_log} 记录耗时与结果（{@code sync_type = 1} 定时自动）。
 *
 * <p><b>实现要点</b>：
 * <ol>
 *   <li>按仓库维度串行 + 间隔，避免触发第三方平台限流；</li>
 *   <li>采集前把 {@code repo_account.sync_status} / {@code repo_project.sync_status} 置 1（采集中）；</li>
 *   <li>成员端不提供手动同步入口，管理员补采走接口 2-17（{@code sync_type = 2}）；</li>
 *   <li>多实例部署时必须加分布式锁（Redis SETNX），否则会重复采集。</li>
 * </ol>
 */
@Slf4j
@Component
public class RepoSyncTask {

    /** 每日 01:00 执行。 */
    @Scheduled(cron = "0 0 1 * * ?")
    public void syncAll() {
        // TODO 模块二：注入 RepoAccountService / RepoProjectService / RepoCommitService / RepoSyncLogService 后实现
        log.info("[骨架] RepoSyncTask.syncAll 尚未实现");
    }
}
