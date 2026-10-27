package com.iecmp.repo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iecmp.common.constant.CacheKeyConstants;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.repo.client.RepoApiException;
import com.iecmp.repo.client.RepoPlatformClient;
import com.iecmp.repo.client.RepoPlatformClientRegistry;
import com.iecmp.repo.client.model.RepoCommitInfo;
import com.iecmp.repo.client.model.RepoInfo;
import com.iecmp.repo.entity.RepoAccount;
import com.iecmp.repo.entity.RepoCommit;
import com.iecmp.repo.entity.RepoProject;
import com.iecmp.repo.entity.RepoSyncLog;
import com.iecmp.repo.enums.ProjectTrackedEnum;
import com.iecmp.repo.enums.RepoErrorCode;
import com.iecmp.repo.enums.SyncResultEnum;
import com.iecmp.repo.enums.SyncStatusEnum;
import com.iecmp.repo.enums.SyncTypeEnum;
import com.iecmp.repo.mapper.RepoAccountMapper;
import com.iecmp.repo.mapper.RepoCommitMapper;
import com.iecmp.repo.mapper.RepoProjectMapper;
import com.iecmp.repo.mapper.RepoSyncLogMapper;
import com.iecmp.repo.properties.RepoProperties;
import com.iecmp.repo.service.RepoSyncService;
import com.iecmp.repo.util.RepoTokenCipher;
import com.iecmp.repo.vo.RepoSyncVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 采集调度实现。
 *
 * <p>整体流程（每个账户一次）：
 * <pre>
 *   账户置「1 采集中」
 *     → 拉取仓库列表并 upsert 到 repo_project
 *     → 逐仓库串行：拉分支 → 逐分支拉提交 → 过滤已入库 SHA → 补齐增删行数 → 批量入库
 *     → 账户置「2 采集正常」+ last_sync_time
 *     → 写 repo_sync_log（成功或失败都要写）
 * </pre>
 *
 * <p><b>刻意不做事务</b>：整段流程包含大量第三方 HTTP 调用，可能持续几分钟。
 * 放进一个事务会让数据库连接被长时间占用，还会把「外部调用的部分成功」卷进回滚。
 * 采集本身是幂等的——提交按 {@code (project_id, commit_sha)} 去重，重跑只会补齐，不会重复。
 *
 * <p><b>限流</b>：仓库之间、分支之间、补齐详情之间都插入 {@code repo.sync.request-interval-ms}
 * 的间隔，对应架构文档 4.6 节实现要点 2「按仓库维度串行 + 间隔」。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RepoSyncServiceImpl implements RepoSyncService {

    /** 比较并删除锁的 Lua 脚本：只删自己加的锁，避免误删其它实例的。 */
    private static final RedisScript<Long> RENEW_LOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then "
                    + "return redis.call('expire', KEYS[1], ARGV[2]) else return 0 end",
            Long.class);

    private static final RedisScript<Long> RELEASE_LOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
            Long.class);

    /** 错误信息落库前的截断长度，与 {@code repo_sync_log.error_msg} 一致。 */
    private static final int ERROR_MSG_MAX_LENGTH = 1024;
    private static final long LOCK_RENEW_INTERVAL_MS = 60_000L;

    /** 当前线程持有的采集锁；sleep() 时按分钟续期，避免长任务执行中锁过期。 */
    private static final ThreadLocal<String> LOCK_TOKEN = new ThreadLocal<>();
    private static final ThreadLocal<Long> LOCK_LAST_RENEW = new ThreadLocal<>();

    private final RepoAccountMapper repoAccountMapper;
    private final RepoProjectMapper repoProjectMapper;
    private final RepoCommitMapper repoCommitMapper;
    private final RepoSyncLogMapper repoSyncLogMapper;
    private final RepoPlatformClientRegistry clientRegistry;
    private final RepoTokenCipher repoTokenCipher;
    private final RepoProperties repoProperties;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public RepoSyncVO syncAccounts(List<Long> accountIds, boolean fullSync, SyncTypeEnum syncType) {
        String lockToken = acquireLock();
        if (lockToken == null) {
            // 接口文档 8.5.5 错误码 20006
            throw new BusinessException(RepoErrorCode.E20006.getCode(), RepoErrorCode.E20006.getMsg());
        }
        try {
            return doSync(accountIds, fullSync, syncType);
        } finally {
            releaseLock(lockToken);
        }
    }

    @Override
    public void syncAllScheduled() {
        String lockToken = acquireLock();
        if (lockToken == null) {
            // 多实例部署下另一台正在采集，属正常情况，不打断调度线程
            log.info("代码仓库采集锁被占用，本次定时采集跳过");
            return;
        }
        try {
            doSync(null, false, SyncTypeEnum.SCHEDULED);
        } catch (Exception e) {
            log.warn("定时采集执行失败：{}", e.getMessage());
        } finally {
            releaseLock(lockToken);
        }
    }

    @Async("iecmpAsyncExecutor")
    @Override
    public void triggerAsync(Long accountId, boolean fullSync, SyncTypeEnum syncType) {
        try {
            RepoAccount account = repoAccountMapper.selectById(accountId);
            if (account == null) {
                log.warn("异步采集跳过：托管账户 {} 不存在", accountId);
                return;
            }
            String lockToken = acquireLock();
            if (lockToken == null) {
                log.info("代码仓库采集锁被占用，账户 {} 的即时采集跳过，等待下次定时采集", accountId);
                // 绑定/换令牌时已把 sync_status 置为「采集中」，这里必须回退，避免前端一直显示采集中
                markAccount(accountId, SyncStatusEnum.NOT_SYNCED, null);
                return;
            }
            try {
                syncOneAccount(account, fullSync, syncType);
            } finally {
                releaseLock(lockToken);
            }
        } catch (Exception e) {
            // 采集失败已经在 syncOneAccount 里写进 repo_sync_log，成员可在接口 2-06 看到，
            // 这里只记日志，绝不让异常冒到异步线程池
            log.warn("账户 {} 的即时采集失败：{}", accountId, e.getMessage());
        }
    }

    @Override
    public int pullProjects(RepoAccount account, String plainToken) {
        RepoPlatformClient client = clientRegistry.get(account.getPlatform());
        List<RepoInfo> repos = client.fetchRepos(plainToken);
        // 拉取仓库列表只发一次第三方请求，处理列表期间不会长时间占用连接；
        // 这里顺手续一次锁，避免仓库很多时后续逐仓库采集尚未开始锁已到期。
        renewLockIfNeeded();

        // 与 uk_account_repo (account_id, repo_url, deleted) 对齐，按仓库地址判断是否已入库
        Map<String, RepoProject> existing = new LinkedHashMap<>();
        for (RepoProject project : listProjects(account.getId())) {
            existing.putIfAbsent(project.getRepoUrl(), project);
        }

        int saved = 0;
        for (RepoInfo info : repos) {
            if (info.getRepoUrl() == null || info.getRepoName() == null) {
                continue;
            }
            RepoProject project = existing.get(info.getRepoUrl());
            boolean isNew = project == null;
            if (isNew) {
                project = new RepoProject();
                project.setAccountId(account.getId());
                project.setUserId(account.getUserId());
                project.setPlatform(account.getPlatform());
                project.setRepoUrl(info.getRepoUrl());
                // 新仓库默认纳入统计（repo_project.tracked 默认 1），成员可在接口 2-08 关掉
                project.setTracked(ProjectTrackedEnum.TRACKED.getValue());
                project.setSyncStatus(SyncStatusEnum.NOT_SYNCED.getValue());
            }
            project.setRepoName(info.getRepoName());
            project.setOwnerName(info.getOwnerName());
            project.setLanguage(info.getLanguage());
            project.setDefaultBranch(info.getDefaultBranch());
            project.setStarCount(info.getStarCount() == null ? 0 : info.getStarCount());
            project.setForkCount(info.getForkCount() == null ? 0 : info.getForkCount());
            project.setLastPushTime(info.getLastPushTime());

            if (isNew) {
                repoProjectMapper.insert(project);
            } else {
                repoProjectMapper.updateById(project);
            }
            saved++;
            // 纯数据库操作，不 sleep；只在需要时续锁，避免仓库很多时锁提前过期
            renewLockIfNeeded();
        }
        return saved;
    }

    /** 采集主循环：解析账户 → 逐个串行采集 → 汇总结果。 */
    private RepoSyncVO doSync(List<Long> accountIds, boolean fullSync, SyncTypeEnum syncType) {
        List<RepoAccount> accounts = resolveAccounts(accountIds);

        long start = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        int commitCount = 0;

        for (RepoAccount account : accounts) {
            try {
                commitCount += syncOneAccount(account, fullSync, syncType);
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.warn("账户 {}（{}）采集失败：{}", account.getId(), account.getAccountName(), e.getMessage());
            }
            // 账户之间也留间隔，避免连续打第三方平台
            sleep();
        }

        RepoSyncVO vo = new RepoSyncVO();
        vo.setTotalAccounts(accounts.size());
        vo.setSuccessCount(successCount);
        vo.setFailCount(failCount);
        vo.setCommitCount(commitCount);
        vo.setCostMs(System.currentTimeMillis() - start);
        return vo;
    }

    /**
     * 采集单个账户。
     *
     * <p>失败时先写 {@code repo_sync_log} 记下原因，再把异常抛给调用方计数——
     * 这样「接口返回里 failCount 加一」与「日志表里有一条失败记录」始终成对出现。
     *
     * @return 本次新增的提交数
     */
    private int syncOneAccount(RepoAccount account, boolean fullSync, SyncTypeEnum syncType) {
        long start = System.currentTimeMillis();
        LocalDateTime syncStartedAt = LocalDateTime.now();
        int newCommits = 0;
        markAccount(account.getId(), SyncStatusEnum.SYNCING, null);
        try {
            String plainToken = repoTokenCipher.decrypt(account.getAccessToken());
            if (plainToken == null) {
                throw new BusinessException(RepoErrorCode.E20003.getCode(), RepoErrorCode.E20003.getMsg());
            }

            // 1. 刷新仓库列表：新仓库、改名、star 数变化都在这一步同步过来
            pullProjects(account, plainToken);

            // 2. 逐仓库采集提交。关闭统计（tracked = 0）的仓库照样采集，
            //    只是统计时过滤——接口文档 8.3.2：重新开启后历史提交立即恢复计入，无需重新同步
            LocalDateTime since = resolveSince(account, fullSync);
            int projectCount = 0;
            for (RepoProject project : listProjects(account.getId())) {
                newCommits += collectProject(account, project, plainToken, since);
                projectCount++;
                sleep();
            }

            // 水位用「本次采集开始时间」，不能用完成时间：
            // 若 01:00 开始、01:10 完成，01:05 产生但 01:04 已查询过的提交会被完成时间永久跳过。
            markAccount(account.getId(), SyncStatusEnum.SYNC_OK, syncStartedAt);
            writeLog(account, syncType, SyncResultEnum.SUCCESS, newCommits, start, null);
            log.info("账户 {}（{}）采集完成：{} 个仓库，新增 {} 条提交",
                    account.getId(), account.getAccountName(), projectCount, newCommits);
            return newCommits;
        } catch (Exception e) {
            // 只有明确的令牌失效才停用账户；限流/网络/5xx 不改账户状态，避免误伤
            if (e instanceof RepoApiException apiException && apiException.isInvalidToken()) {
                markAccountStatus(account.getId(), 0, SyncStatusEnum.SYNC_FAILED);
            } else {
                markAccount(account.getId(), SyncStatusEnum.SYNC_FAILED, null);
            }
            writeLog(account, syncType, SyncResultEnum.FAILED, newCommits, start, describe(e));
            throw e instanceof RuntimeException runtime ? runtime : new IllegalStateException(e);
        }
    }
    /** 采集单个仓库：拉分支 → 逐分支拉提交 → 去重 → 补齐行数 → 批量入库。 */
    private int collectProject(RepoAccount account, RepoProject project, String plainToken, LocalDateTime since) {
        RepoPlatformClient client = clientRegistry.get(account.getPlatform());
        String owner = project.getOwnerName() == null ? account.getAccountName() : project.getOwnerName();
        String repo = project.getRepoName();

        markProject(project.getId(), SyncStatusEnum.SYNCING);
        try {
            List<String> branches = client.fetchBranches(plainToken, owner, repo);
            if (branches.isEmpty() && project.getDefaultBranch() != null) {
                // 分支接口偶发返回空（空仓库或权限不足），退回默认分支，至少把主干采到
                branches = List.of(project.getDefaultBranch());
            }

            Set<String> known = loadKnownShas(project.getId(), since);
            Map<String, RepoCommit> fresh = new LinkedHashMap<>();
            for (String branch : branches) {
                List<RepoCommitInfo> commits = client.fetchCommits(plainToken, owner, repo, branch, since);
                for (RepoCommitInfo info : commits) {
                    String sha = info.getCommitSha();
                    // commit_time 是 NOT NULL，第三方没给时间的记录直接丢弃
                    if (sha == null || info.getCommitTime() == null
                            || known.contains(sha) || fresh.containsKey(sha)) {
                        continue;
                    }
                    fresh.put(sha, toEntity(account, project, info, branch));
                }
                sleep();
            }

            fillCommitDetail(client, plainToken, owner, repo, fresh);
            if (!fresh.isEmpty()) {
                repoCommitMapper.insertBatch(new ArrayList<>(fresh.values()));
            }
            markProject(project.getId(), SyncStatusEnum.SYNC_OK);
            return fresh.size();
        } catch (Exception e) {
            markProject(project.getId(), SyncStatusEnum.SYNC_FAILED);
            throw e instanceof RuntimeException runtime ? runtime : new IllegalStateException(e);
        }
    }

    /**
     * 补齐提交的增删行数。
     *
     * <p>列表接口不保证返回行数统计，因此需要按 SHA 再查一次详情。任一详情失败时本次仓库采集整体失败，
     * 不推进账户的采集水位，下一轮会重新拉取并补齐；否则以 0 入库后会被 loadKnownShas 当成已完成，
     * 永远不再补。关闭 {@code repo.sync.commit-detail} 时按 0 入库。
     */
    private void fillCommitDetail(RepoPlatformClient client, String plainToken, String owner, String repo,
                                  Map<String, RepoCommit> fresh) {
        if (fresh.isEmpty()) {
            return;
        }
        if (!repoProperties.getSync().isCommitDetail()) {
            normalizeLineCounts(fresh);
            return;
        }

        List<String> detailFailed = new ArrayList<>();
        for (RepoCommit entity : fresh.values()) {
            if (entity.getAddLines() != null && entity.getDelLines() != null) {
                continue;
            }
            try {
                RepoCommitInfo detail = client.fetchCommitDetail(plainToken, owner, repo, entity.getCommitSha());
                if (detail == null) {
                    detailFailed.add(entity.getCommitSha());
                } else {
                    entity.setAddLines(detail.getAddLines());
                    entity.setDelLines(detail.getDelLines());
                }
            } catch (RepoApiException e) {
                if (e.isInvalidToken()) {
                    throw e;
                }
                // 限流/网络/单仓库权限问题：本次先不入库，下次增量采集再补
                log.debug("提交 {} 详情获取失败，本次跳过：{}", entity.getCommitSha(), e.getMessage());
                detailFailed.add(entity.getCommitSha());
            }
            sleep();
        }

        if (!detailFailed.isEmpty()) {
            throw new RepoApiException(RepoApiException.API_FAILED,
                    "有 " + detailFailed.size() + " 条提交详情获取失败，本次不推进采集水位，下一轮重试", true);
        }
        normalizeLineCounts(fresh);
    }

    /** add_lines / del_lines 是 NOT NULL，取不到详情时落 0。 */
    private void normalizeLineCounts(Map<String, RepoCommit> commits) {
        for (RepoCommit entity : commits.values()) {
            if (entity.getAddLines() == null) {
                entity.setAddLines(0);
            }
            if (entity.getDelLines() == null) {
                entity.setDelLines(0);
            }
        }
    }
    private RepoCommit toEntity(RepoAccount account, RepoProject project, RepoCommitInfo info, String branch) {
        RepoCommit entity = new RepoCommit();
        entity.setProjectId(project.getId());
        entity.setUserId(account.getUserId());
        entity.setCommitSha(truncate(info.getCommitSha(), 64));
        entity.setBranch(truncate(branch, 128));
        // 平台返回的作者名/邮箱/提交信息长度不可控，按数据库列宽截断，避免严格模式下整批插入失败
        entity.setAuthorName(truncate(info.getAuthorName(), 64));
        entity.setAuthorEmail(truncate(info.getAuthorEmail(), 128));
        entity.setMessage(truncate(info.getMessage(), 1024));
        entity.setCommitTime(info.getCommitTime());
        entity.setAddLines(info.getAddLines());
        entity.setDelLines(info.getDelLines());
        entity.setIsMerge(info.getIsMerge());
        return entity;
    }

    /**
     * 已入库的提交 SHA，用于跳过重复提交，避免为老提交反复查详情。
     *
     * <p>增量采集只加载本次时间窗口内的 SHA；更早的重复提交由唯一键兜底，不会重复入库。
     */
    private Set<String> loadKnownShas(Long projectId, LocalDateTime since) {
        LambdaQueryWrapper<RepoCommit> wrapper = new LambdaQueryWrapper<RepoCommit>()
                .select(RepoCommit::getCommitSha)
                .eq(RepoCommit::getProjectId, projectId);
        if (since != null) {
            wrapper.ge(RepoCommit::getCommitTime, since);
        }
        List<RepoCommit> existing = repoCommitMapper.selectList(wrapper);
        Set<String> shas = new HashSet<>(Math.max(16, existing.size() * 2));
        for (RepoCommit commit : existing) {
            shas.add(commit.getCommitSha());
        }
        return shas;
    }

    /**
     * 增量采集起点。
     *
     * <p>从 {@code last_sync_time} 往前多回溯 {@code incremental-overlap-minutes} 分钟，
     * 抵消第三方提交时间与服务端时钟的偏差；重复部分由 {@code (project_id, commit_sha)} 唯一键去重，
     * 代价只是多查几条。
     */
    private LocalDateTime resolveSince(RepoAccount account, boolean fullSync) {
        if (fullSync || account.getLastSyncTime() == null) {
            return null;
        }
        return account.getLastSyncTime().minusMinutes(repoProperties.getSync().getIncrementalOverlapMinutes());
    }

    /**
     * 解析待采集账户。
     *
     * <p>不传 {@code accountIds} 时取全部<b>有效</b>账户（接口文档 8.5.5）；
     * 显式传了 ID 就按 ID 取，不再过滤状态——令牌失效的账户也允许管理员补采一次，
     * 失败原因会写进同步日志，正是排查所需要的。
     */
    private List<RepoAccount> resolveAccounts(List<Long> accountIds) {
        boolean all = accountIds == null || accountIds.isEmpty();
        LambdaQueryWrapper<RepoAccount> wrapper = new LambdaQueryWrapper<RepoAccount>()
                .orderByAsc(RepoAccount::getId);
        if (all) {
            wrapper.eq(RepoAccount::getStatus, 1);
        } else {
            wrapper.in(RepoAccount::getId, accountIds);
        }
        List<RepoAccount> accounts = repoAccountMapper.selectList(wrapper);
        if (accounts.isEmpty()) {
            // 接口文档 8.5.5 错误码 20008
            throw new BusinessException(RepoErrorCode.E20008.getCode(), RepoErrorCode.E20008.getMsg());
        }
        return accounts;
    }

    private List<RepoProject> listProjects(Long accountId) {
        return repoProjectMapper.selectList(new LambdaQueryWrapper<RepoProject>()
                .eq(RepoProject::getAccountId, accountId)
                .orderByAsc(RepoProject::getId));
    }

    private void markAccount(Long accountId, SyncStatusEnum status, LocalDateTime lastSyncTime) {
        RepoAccount update = new RepoAccount();
        update.setId(accountId);
        update.setSyncStatus(status.getValue());
        // update-strategy = not_null：传 null 时该列不参与 UPDATE，失败场景因此能保住原来的 last_sync_time
        update.setLastSyncTime(lastSyncTime);
        repoAccountMapper.updateById(update);
    }

    /** 同时更新账户启用状态与采集状态（令牌明确失效时使用）。 */
    private void markAccountStatus(Long accountId, Integer status, SyncStatusEnum syncStatus) {
        RepoAccount update = new RepoAccount();
        update.setId(accountId);
        update.setStatus(status);
        update.setSyncStatus(syncStatus.getValue());
        repoAccountMapper.updateById(update);
    }
    private void markProject(Long projectId, SyncStatusEnum status) {
        RepoProject update = new RepoProject();
        update.setId(projectId);
        update.setSyncStatus(status.getValue());
        repoProjectMapper.updateById(update);
    }

    /** 写同步日志：成功、失败都要写，接口 2-16 的排查依据全靠它。 */
    private void writeLog(RepoAccount account, SyncTypeEnum syncType, SyncResultEnum result,
                          int commitCount, long startMillis, String errorMsg) {
        try {
            RepoSyncLog entity = new RepoSyncLog();
            entity.setAccountId(account.getId());
            entity.setUserId(account.getUserId());
            entity.setSyncType(syncType.getValue());
            entity.setSyncResult(result.getValue());
            entity.setCommitCount(commitCount);
            entity.setCostMs(System.currentTimeMillis() - startMillis);
            entity.setErrorMsg(truncate(errorMsg, ERROR_MSG_MAX_LENGTH));
            repoSyncLogMapper.insert(entity);
        } catch (Exception e) {
            // 日志写失败不能反过来影响采集结果
            log.warn("同步日志落库失败：{}", e.getMessage());
        }
    }

    /** 把异常转成一句可读的失败原因，写进 {@code repo_sync_log.error_msg}。 */
    private String describe(Exception e) {
        if (e instanceof RepoApiException apiException) {
            return (apiException.isInvalidToken() ? "令牌无效：" : "平台调用失败：") + apiException.getMessage();
        }
        String message = e.getMessage();
        return message == null || message.isBlank() ? e.getClass().getSimpleName() : message;
    }

    /** 抢采集锁：SETNX + TTL，抢不到返回 null。 */
    private String acquireLock() {
        String token = UUID.randomUUID().toString();
        Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(
                CacheKeyConstants.REPO_SYNC_LOCK, token,
                Duration.ofSeconds(repoProperties.getSync().getLockTtlSeconds()));
        if (Boolean.TRUE.equals(acquired)) {
            LOCK_TOKEN.set(token);
            LOCK_LAST_RENEW.set(System.currentTimeMillis());
            return token;
        }
        return null;
    }

    /** 释放采集锁：比对 value 后再删，避免锁过期后误删别的实例刚拿到的锁。 */
    private void releaseLock(String token) {
        try {
            stringRedisTemplate.execute(RELEASE_LOCK_SCRIPT, List.of(CacheKeyConstants.REPO_SYNC_LOCK), token);
        } catch (Exception e) {
            // 锁有 TTL 兜底，释放失败不影响正确性
            log.warn("释放采集锁失败：{}", e.getMessage());
        } finally {
            LOCK_TOKEN.remove();
            LOCK_LAST_RENEW.remove();
        }
    }

    /** 长任务心跳：每 60 秒把当前线程持有的锁续到完整 TTL。 */
    private void renewLockIfNeeded() {
        String token = LOCK_TOKEN.get();
        if (token == null) {
            return;
        }
        long now = System.currentTimeMillis();
        Long last = LOCK_LAST_RENEW.get();
        if (last != null && now - last < LOCK_RENEW_INTERVAL_MS) {
            return;
        }
        try {
            stringRedisTemplate.execute(RENEW_LOCK_SCRIPT,
                    List.of(CacheKeyConstants.REPO_SYNC_LOCK),
                    token, String.valueOf(repoProperties.getSync().getLockTtlSeconds()));
            LOCK_LAST_RENEW.set(now);
        } catch (Exception e) {
            // 续期失败不改变业务结果，锁本身仍有 TTL 兜底
            log.warn("采集锁续期失败：{}", e.getMessage());
        }
    }

    private void sleep() {
        // 即使未配置请求间隔，也要检查是否需要续锁，否则长任务会因锁过期被其他实例抢占。
        renewLockIfNeeded();
        long interval = repoProperties.getSync().getRequestIntervalMs();
        if (interval <= 0) {
            return;
        }
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("采集任务被中断", e);
        }
    }

    private static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
