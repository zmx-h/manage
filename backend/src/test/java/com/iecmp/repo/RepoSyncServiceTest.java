package com.iecmp.repo;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.iecmp.common.constant.CacheKeyConstants;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.repo.client.RepoApiException;
import com.iecmp.repo.client.RepoPlatformClient;
import com.iecmp.repo.client.RepoPlatformClientRegistry;
import com.iecmp.repo.client.model.RepoCommitInfo;
import com.iecmp.repo.entity.RepoAccount;
import com.iecmp.repo.entity.RepoCommit;
import com.iecmp.repo.entity.RepoProject;
import com.iecmp.repo.entity.RepoSyncLog;
import com.iecmp.repo.enums.RepoErrorCode;
import com.iecmp.repo.enums.SyncStatusEnum;
import com.iecmp.repo.enums.SyncTypeEnum;
import com.iecmp.repo.mapper.RepoAccountMapper;
import com.iecmp.repo.mapper.RepoCommitMapper;
import com.iecmp.repo.mapper.RepoProjectMapper;
import com.iecmp.repo.mapper.RepoSyncLogMapper;
import com.iecmp.repo.properties.RepoProperties;
import com.iecmp.repo.service.impl.RepoSyncServiceImpl;
import com.iecmp.repo.util.RepoTokenCipher;
import com.iecmp.repo.vo.RepoSyncVO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 接口 2-17 采集调度与 2-02 异步触发的单元测试。
 *
 * <p>采集链路依赖 Redis 分布式锁与第三方平台，集成测试覆盖不到，这里用 Mockito 断言：
 * 抢不到锁返回 20006、锁一定会被释放、单个账户失败不影响整体计数、
 * 失败原因会成对写进同步日志。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RepoSyncServiceTest {

    private static final String LOCK_KEY = CacheKeyConstants.REPO_SYNC_LOCK;

    @Mock
    private RepoAccountMapper accountMapper;
    @Mock
    private RepoProjectMapper projectMapper;
    @Mock
    private RepoCommitMapper commitMapper;
    @Mock
    private RepoSyncLogMapper syncLogMapper;
    @Mock
    private RepoPlatformClientRegistry clientRegistry;
    @Mock
    private RepoTokenCipher tokenCipher;
    @Mock
    private StringRedisTemplate redis;
    @Mock
    private ValueOperations<String, String> valueOps;
    @Mock
    private RepoPlatformClient client;

    private RepoProperties properties;
    private RepoSyncServiceImpl service;

    /**
     * 纯 Mockito 测试没有经过 Spring 的 mapper 扫描，MyBatis-Plus 的 lambda 列名缓存是空的，
     * 构造 {@code LambdaQueryWrapper<RepoCommit>} 时会报 "can not find lambda cache"。
     * 这里手工初始化实体元数据，等效于容器启动时做的那一步。
     */
    @BeforeAll
    static void initMybatisMetadata() {
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "");
        TableInfoHelper.initTableInfo(assistant, RepoAccount.class);
        TableInfoHelper.initTableInfo(assistant, RepoProject.class);
        TableInfoHelper.initTableInfo(assistant, RepoCommit.class);
        TableInfoHelper.initTableInfo(assistant, RepoSyncLog.class);
    }

    @BeforeEach
    void setUp() {
        properties = new RepoProperties();
        // 测试里不要真的 sleep
        properties.getSync().setRequestIntervalMs(0);
        when(redis.opsForValue()).thenReturn(valueOps);

        service = new RepoSyncServiceImpl(accountMapper, projectMapper, commitMapper, syncLogMapper,
                clientRegistry, tokenCipher, properties, redis);
    }

    // ------------------------------------------------------------------ 锁

    @Test
    @DisplayName("2-17 手动补采：抢不到锁返回 20006，且不访问数据库")
    void manualSyncLocked() {
        when(valueOps.setIfAbsent(eq(LOCK_KEY), anyString(), any())).thenReturn(false);

        assertThatThrownBy(() -> service.syncAccounts(List.of(201L), false, SyncTypeEnum.MANUAL))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getCode())
                        .isEqualTo(RepoErrorCode.E20006.getCode()));

        verify(accountMapper, never()).selectList(any());
    }

    @Test
    @DisplayName("2-17 手动补采：执行完一定释放锁（含抛异常的情况）")
    void lockReleasedEvenOnFailure() {
        when(valueOps.setIfAbsent(eq(LOCK_KEY), anyString(), any())).thenReturn(true);
        when(accountMapper.selectList(any())).thenReturn(List.of());

        assertThatThrownBy(() -> service.syncAccounts(List.of(201L), false, SyncTypeEnum.MANUAL))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getCode())
                        .isEqualTo(RepoErrorCode.E20008.getCode()));

        verify(redis).execute(any(RedisScript.class), eq(List.of(LOCK_KEY)), anyString());
    }

    @Test
    @DisplayName("定时采集：锁被占用时静默跳过，不打断调度线程")
    void scheduledSkipsWhenLocked() {
        when(valueOps.setIfAbsent(eq(LOCK_KEY), anyString(), any())).thenReturn(false);

        assertThatCode(() -> service.syncAllScheduled()).doesNotThrowAnyException();

        verify(accountMapper, never()).selectList(any());
        verify(redis, never()).execute(any(RedisScript.class), anyList(), anyString());
    }

    @Test
    @DisplayName("绑定后的异步采集：锁被占用时直接跳过，不抛异常")
    void triggerAsyncSkipsWhenLocked() {
        when(accountMapper.selectById(201L)).thenReturn(account(201L));
        when(valueOps.setIfAbsent(eq(LOCK_KEY), anyString(), any())).thenReturn(false);

        assertThatCode(() -> service.triggerAsync(201L, true, SyncTypeEnum.SCHEDULED))
                .doesNotThrowAnyException();

        verify(projectMapper, never()).selectList(any());
    }

    @Test
    @DisplayName("绑定后的异步采集：账户已被解绑时跳过，不抛异常")
    void triggerAsyncSkipsMissingAccount() {
        when(accountMapper.selectById(999L)).thenReturn(null);

        assertThatCode(() -> service.triggerAsync(999L, true, SyncTypeEnum.SCHEDULED))
                .doesNotThrowAnyException();

        // 账户不存在时连锁都不用抢
        verify(valueOps, never()).setIfAbsent(anyString(), anyString(), any());
    }

    // ------------------------------------------------------------------ 采集

    @Test
    @DisplayName("2-17 采集成功：提交入库并写成功日志，返回新增提交数")
    void syncSuccess() {
        RepoAccount account = account(201L);
        RepoProject project = project(301L, 201L, "repo-a", "owner");
        when(valueOps.setIfAbsent(eq(LOCK_KEY), anyString(), any())).thenReturn(true);
        when(accountMapper.selectList(any())).thenReturn(List.of(account));
        when(tokenCipher.decrypt("ENC")).thenReturn("tok");
        when(clientRegistry.get("github")).thenReturn(client);
        when(client.fetchRepos("tok")).thenReturn(List.of());
        when(projectMapper.selectList(any())).thenReturn(List.of(project));
        when(client.fetchBranches("tok", "owner", "repo-a")).thenReturn(List.of("main"));
        when(commitMapper.selectList(any())).thenReturn(List.of());
        when(client.fetchCommits("tok", "owner", "repo-a", "main", null))
                .thenReturn(List.of(commitInfo("aaaa0001")));
        when(commitMapper.insertBatch(any())).thenReturn(1);

        RepoSyncVO vo = service.syncAccounts(List.of(201L), true, SyncTypeEnum.MANUAL);

        assertThat(vo.getTotalAccounts()).isEqualTo(1);
        assertThat(vo.getSuccessCount()).isEqualTo(1);
        assertThat(vo.getFailCount()).isZero();
        assertThat(vo.getCommitCount()).isEqualTo(1);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RepoCommit>> commits = ArgumentCaptor.forClass(List.class);
        verify(commitMapper).insertBatch(commits.capture());
        RepoCommit saved = commits.getValue().get(0);
        assertThat(saved.getProjectId()).isEqualTo(301L);
        assertThat(saved.getUserId()).isEqualTo(101L);
        assertThat(saved.getCommitSha()).isEqualTo("aaaa0001");
        assertThat(saved.getBranch()).isEqualTo("main");
        assertThat(saved.getAddLines()).isEqualTo(10);
        assertThat(saved.getDelLines()).isEqualTo(5);
        assertThat(saved.getIsMerge()).isZero();

        ArgumentCaptor<RepoSyncLog> log = ArgumentCaptor.forClass(RepoSyncLog.class);
        verify(syncLogMapper).insert(log.capture());
        assertThat(log.getValue().getSyncResult()).isEqualTo(1);
        assertThat(log.getValue().getSyncType()).isEqualTo(SyncTypeEnum.MANUAL.getValue());
        assertThat(log.getValue().getCommitCount()).isEqualTo(1);
        assertThat(log.getValue().getErrorMsg()).isNull();
    }

    @Test
    @DisplayName("2-17 采集失败：账户置采集失败、写失败日志，整体结果计入 failCount")
    void syncFailureWritesLog() {
        RepoAccount account = account(201L);
        when(valueOps.setIfAbsent(eq(LOCK_KEY), anyString(), any())).thenReturn(true);
        when(accountMapper.selectList(any())).thenReturn(List.of(account));
        when(tokenCipher.decrypt("ENC")).thenReturn("tok");
        when(clientRegistry.get("github")).thenReturn(client);
        when(client.fetchRepos("tok"))
                .thenThrow(new RepoApiException(RepoApiException.INVALID_TOKEN, "401 Unauthorized"));

        RepoSyncVO vo = service.syncAccounts(List.of(201L), true, SyncTypeEnum.MANUAL);

        assertThat(vo.getSuccessCount()).isZero();
        assertThat(vo.getFailCount()).isEqualTo(1);
        assertThat(vo.getCommitCount()).isZero();

        ArgumentCaptor<RepoAccount> updates = ArgumentCaptor.forClass(RepoAccount.class);
        verify(accountMapper, times(2)).updateById(updates.capture());
        // 第一次置采集中，第二次置采集失败
        assertThat(updates.getAllValues().get(0).getSyncStatus())
                .isEqualTo(SyncStatusEnum.SYNCING.getValue());
        assertThat(updates.getAllValues().get(1).getSyncStatus())
                .isEqualTo(SyncStatusEnum.SYNC_FAILED.getValue());
        // 失败时 last_sync_time 必须保持为 null，下次才能从同一增量起点重试
        assertThat(updates.getAllValues().get(1).getLastSyncTime()).isNull();

        ArgumentCaptor<RepoSyncLog> log = ArgumentCaptor.forClass(RepoSyncLog.class);
        verify(syncLogMapper).insert(log.capture());
        assertThat(log.getValue().getSyncResult()).isZero();
        assertThat(log.getValue().getErrorMsg()).contains("令牌无效").contains("401");

        verify(redis).execute(any(RedisScript.class), eq(List.of(LOCK_KEY)), anyString());
    }

    @Test
    @DisplayName("2-17 采集：已入库的提交不会重复入库")
    void syncSkipsKnownCommits() {
        RepoAccount account = account(201L);
        RepoProject project = project(301L, 201L, "repo-a", "owner");
        RepoCommit known = new RepoCommit();
        known.setCommitSha("aaaa0001");

        when(valueOps.setIfAbsent(eq(LOCK_KEY), anyString(), any())).thenReturn(true);
        when(accountMapper.selectList(any())).thenReturn(List.of(account));
        when(tokenCipher.decrypt("ENC")).thenReturn("tok");
        when(clientRegistry.get("github")).thenReturn(client);
        when(client.fetchRepos("tok")).thenReturn(List.of());
        when(projectMapper.selectList(any())).thenReturn(List.of(project));
        when(client.fetchBranches("tok", "owner", "repo-a")).thenReturn(List.of("main"));
        when(commitMapper.selectList(any())).thenReturn(List.of(known));
        when(client.fetchCommits("tok", "owner", "repo-a", "main", null))
                .thenReturn(List.of(commitInfo("aaaa0001")));

        RepoSyncVO vo = service.syncAccounts(List.of(201L), true, SyncTypeEnum.MANUAL);

        assertThat(vo.getCommitCount()).isZero();
        verify(commitMapper, never()).insertBatch(any());
    }

    @Test
    @DisplayName("2-17 采集：令牌解不开时按令牌失效处理，不发起任何第三方调用")
    void syncRejectsUndecryptableToken() {
        RepoAccount account = account(201L);
        when(valueOps.setIfAbsent(eq(LOCK_KEY), anyString(), any())).thenReturn(true);
        when(accountMapper.selectList(any())).thenReturn(List.of(account));
        when(tokenCipher.decrypt("ENC")).thenReturn(null);

        RepoSyncVO vo = service.syncAccounts(List.of(201L), true, SyncTypeEnum.MANUAL);

        assertThat(vo.getFailCount()).isEqualTo(1);
        verify(clientRegistry, never()).get(anyString());
        verify(syncLogMapper).insert(any(RepoSyncLog.class));
    }

    @Test
    @DisplayName("2-17 增量采集：非全量时从 last_sync_time 往前回溯 overlap 分钟")
    void incrementalSinceUsesOverlap() {
        RepoAccount account = account(201L);
        account.setLastSyncTime(LocalDateTime.of(2026, 3, 1, 2, 0, 0));
        RepoProject project = project(301L, 201L, "repo-a", "owner");

        when(valueOps.setIfAbsent(eq(LOCK_KEY), anyString(), any())).thenReturn(true);
        when(accountMapper.selectList(any())).thenReturn(List.of(account));
        when(tokenCipher.decrypt("ENC")).thenReturn("tok");
        when(clientRegistry.get("github")).thenReturn(client);
        when(client.fetchRepos("tok")).thenReturn(List.of());
        when(projectMapper.selectList(any())).thenReturn(List.of(project));
        when(client.fetchBranches("tok", "owner", "repo-a")).thenReturn(List.of("main"));
        when(commitMapper.selectList(any())).thenReturn(List.of());
        when(client.fetchCommits(anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn(List.of());

        service.syncAccounts(List.of(201L), false, SyncTypeEnum.SCHEDULED);

        // 默认 overlap = 5 分钟
        verify(client).fetchCommits("tok", "owner", "repo-a", "main",
                LocalDateTime.of(2026, 3, 1, 1, 55, 0));
    }

    @Test
    @DisplayName("2-17 账号范围：没有有效账户时定时采集自行吞掉异常并释放锁")
    void scheduledOnlyResolvesActiveAccounts() {
        when(valueOps.setIfAbsent(eq(LOCK_KEY), anyString(), any())).thenReturn(true);
        when(accountMapper.selectList(any())).thenReturn(List.of());

        // 定时任务不能把异常抛给调度线程
        assertThatCode(() -> service.syncAllScheduled()).doesNotThrowAnyException();

        // 无 ID 时按「全部有效账户」查询，空结果对应的 20008 由调度入口自己消化
        verify(accountMapper).selectList(any());
        verify(redis).execute(any(RedisScript.class), eq(List.of(LOCK_KEY)), anyString());
    }

    // ------------------------------------------------------------------ helpers

    private RepoAccount account(Long id) {
        RepoAccount account = new RepoAccount();
        account.setId(id);
        account.setUserId(101L);
        account.setPlatform("github");
        account.setAccountName("octocat");
        account.setAccessToken("ENC");
        return account;
    }

    private RepoProject project(Long id, Long accountId, String repoName, String owner) {
        RepoProject project = new RepoProject();
        project.setId(id);
        project.setAccountId(accountId);
        project.setUserId(101L);
        project.setPlatform("github");
        project.setRepoName(repoName);
        project.setRepoUrl("https://github.com/" + owner + "/" + repoName);
        project.setOwnerName(owner);
        project.setDefaultBranch("main");
        return project;
    }

    private RepoCommitInfo commitInfo(String sha) {
        RepoCommitInfo info = new RepoCommitInfo();
        info.setCommitSha(sha);
        info.setCommitTime(LocalDateTime.of(2026, 1, 10, 10, 0, 0));
        info.setAuthorName("张三");
        info.setAuthorEmail("a@x");
        info.setMessage("feat: init");
        info.setAddLines(10);
        info.setDelLines(5);
        info.setParentCount(1);
        return info;
    }
}
