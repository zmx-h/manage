package com.iecmp.repo.service;

import com.iecmp.repo.entity.RepoAccount;
import com.iecmp.repo.enums.SyncTypeEnum;
import com.iecmp.repo.vo.RepoSyncVO;

import java.util.List;

/**
 * 采集调度服务：模块二「数据从哪来」的唯一入口。
 *
 * <p>接口文档 8.1 已确定口径 5：采集全部由后台自动完成，成员端不提供手动同步。
 * 因此本服务被三条链路调用，行为完全一致，只是 {@code syncType} 不同：
 * <ol>
 *   <li>每日定时任务（{@code RepoSyncTask}）→ {@link SyncTypeEnum#SCHEDULED}；</li>
 *   <li>接口 2-17 管理员补采 → {@link SyncTypeEnum#MANUAL}；</li>
 *   <li>接口 2-02 绑定 / 2-04 换令牌后的即时采集 → 异步执行，不阻塞接口返回。</li>
 * </ol>
 *
 * <p><b>并发控制</b>：三条链路共用 Redis 上的同一把锁（{@code iecmp:repo:sync:lock}）。
 * 同一时刻只允许一次采集在跑，接口 2-17 抢不到锁时按错误码 {@code 20006} 返回，
 * 定时任务与异步触发抢不到锁则直接跳过（下一次还会来）。
 *
 * <p><b>不使用实体 Service</b>：本服务直接依赖 Mapper 而不是 {@code RepoAccountService} 等，
 * 因为 {@code RepoAccountServiceImpl} 需要反过来调用本服务触发即时采集。
 * 走 Mapper 可以避免两个 Service 互相注入形成的循环依赖，也让「采集」这件事的边界更清楚。
 */
public interface RepoSyncService {

    /**
     * 按账户串行采集（接口 2-17 的主逻辑）。
     *
     * @param accountIds 待补采的账户；为 null 或空表示全部<b>有效</b>账户
     * @param fullSync   true 忽略 {@code last_sync_time} 重拉全部历史
     * @param syncType   写入 {@code repo_sync_log.sync_type}
     * @throws com.iecmp.common.exception.BusinessException 20006 抢不到锁；20008 没有可采账户
     */
    RepoSyncVO syncAccounts(List<Long> accountIds, boolean fullSync, SyncTypeEnum syncType);

    /**
     * 拉取某个账户名下的仓库列表并写入 {@code repo_project}（已存在的按仓库地址更新）。
     *
     * <p>单独暴露是因为接口 2-02 需要<b>同步</b>拿到仓库数量填 {@code projectCount}——
     * 只拉一次仓库列表，耗时可控；真正耗时的提交采集才放到异步链路。
     *
     * @param plainToken 已解密的明文令牌
     * @return 本次拉取到的仓库数量
     */
    int pullProjects(RepoAccount account, String plainToken);

    /**
     * 异步触发某个账户的采集，用于接口 2-02 / 2-04 之后。
     *
     * <p>调用方无需等待，异常与锁冲突都在方法内部消化——采集失败要写进
     * {@code repo_sync_log} 让成员在接口 2-06 看到，不能影响绑定 / 换令牌本身的结果。
     */
    void triggerAsync(Long accountId, boolean fullSync, SyncTypeEnum syncType);

    /**
     * 定时任务入口：采集全部有效账户。
     *
     * <p>与 {@link #syncAccounts} 的区别是抢不到锁时静默跳过（多实例部署下属于正常情况），
     * 不抛异常打断调度线程。
     */
    void syncAllScheduled();
}
