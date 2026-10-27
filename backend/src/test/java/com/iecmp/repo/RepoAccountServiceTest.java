package com.iecmp.repo;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.iecmp.common.context.LoginUser;
import com.iecmp.common.context.UserContext;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.repo.client.RepoApiException;
import com.iecmp.repo.client.RepoPlatformClient;
import com.iecmp.repo.client.RepoPlatformClientRegistry;
import com.iecmp.repo.client.model.RepoUserInfo;
import com.iecmp.repo.dto.RepoAccountAddDTO;
import com.iecmp.repo.dto.RepoAccountUpdateDTO;
import com.iecmp.repo.entity.RepoAccount;
import com.iecmp.repo.entity.RepoCommit;
import com.iecmp.repo.entity.RepoProject;
import com.iecmp.repo.enums.RepoErrorCode;
import com.iecmp.repo.enums.SyncStatusEnum;
import com.iecmp.repo.enums.SyncTypeEnum;
import com.iecmp.repo.mapper.RepoAccountMapper;
import com.iecmp.repo.mapper.RepoCommitMapper;
import com.iecmp.repo.mapper.RepoProjectMapper;
import com.iecmp.repo.mapper.RepoSyncLogMapper;
import com.iecmp.repo.properties.RepoProperties;
import com.iecmp.repo.service.RepoSyncService;
import com.iecmp.repo.service.impl.RepoAccountServiceImpl;
import com.iecmp.repo.util.RepoTokenCipher;
import com.iecmp.repo.vo.RepoAccountCreateVO;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 接口 2-02 / 2-04 的单元测试。
 *
 * <p>这两条链路要访问 Redis 与第三方平台，集成测试无法覆盖，因此用 Mockito 隔离外部依赖，
 * 断言的重点是：令牌必须先校验再落库、落库的是密文、失败错误码正确、换令牌后触发全量重采。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RepoAccountServiceTest {

    private static final long USER_ID = 101L;

    @Mock
    private RepoProjectMapper repoProjectMapper;
    @Mock
    private RepoCommitMapper repoCommitMapper;
    @Mock
    private RepoSyncLogMapper repoSyncLogMapper;
    @Mock
    private RepoSyncService repoSyncService;
    @Mock
    private RepoPlatformClientRegistry clientRegistry;
    @Mock
    private RepoTokenCipher repoTokenCipher;
    @Mock
    private RepoAccountMapper accountMapper;
    @Mock
    private RepoPlatformClient client;

    private RepoAccountServiceImpl service;

    /** 纯 Mockito 测试不经过 mapper 扫描，手工初始化 MyBatis-Plus 的 lambda 列名缓存。 */
    @BeforeAll
    static void initMybatisMetadata() {
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "");
        TableInfoHelper.initTableInfo(assistant, RepoAccount.class);
        TableInfoHelper.initTableInfo(assistant, RepoCommit.class);
        TableInfoHelper.initTableInfo(assistant, RepoProject.class);
    }

    @BeforeEach
    void setUp() {
        service = new RepoAccountServiceImpl(repoProjectMapper, repoCommitMapper, repoSyncLogMapper,
                repoSyncService, clientRegistry, repoTokenCipher, mock(RepoProperties.class));
        ReflectionTestUtils.setField(service, "baseMapper", accountMapper);

        LoginUser user = new LoginUser();
        user.setUserId(USER_ID);
        UserContext.set(user);
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    // ------------------------------------------------------------------ 2-02

    @Test
    @DisplayName("2-02 绑定：账号名取平台 login，令牌加密落库，返回本次拉到的仓库数")
    void bindSuccess() {
        when(clientRegistry.get("github")).thenReturn(client);
        when(client.fetchUser("plain-token")).thenReturn(userInfo("octocat", "https://avatar/1"));
        when(accountMapper.selectCount(any())).thenReturn(0L);
        when(repoTokenCipher.encrypt("plain-token")).thenReturn("ENC");
        when(accountMapper.insert(any(RepoAccount.class))).thenAnswer(invocation -> {
            invocation.<RepoAccount>getArgument(0).setId(500L);
            return 1;
        });
        when(repoSyncService.pullProjects(any(), eq("plain-token"))).thenReturn(3);

        RepoAccountCreateVO vo = service.bind(addDto(" GitHub ", "form-name", "  plain-token  "));

        assertThat(vo.getAccountId()).isEqualTo(500L);
        assertThat(vo.getProjectCount()).isEqualTo(3);

        ArgumentCaptor<RepoAccount> captor = ArgumentCaptor.forClass(RepoAccount.class);
        verify(accountMapper).insert(captor.capture());
        RepoAccount saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getPlatform()).isEqualTo("github");
        // 表单里写的 accountName 不可信，以平台返回的 login 为准
        assertThat(saved.getAccountName()).isEqualTo("octocat");
        assertThat(saved.getAccessToken()).isEqualTo("ENC");
        assertThat(saved.getStatus()).isEqualTo(1);
        assertThat(saved.getSyncStatus()).isEqualTo(SyncStatusEnum.SYNCING.getValue());
        assertThat(saved.getBindTime()).isNotNull();

        // 绑定后立即触发一次全量异步采集
        verify(repoSyncService).triggerAsync(500L, true, SyncTypeEnum.SCHEDULED);
    }

    @Test
    @DisplayName("2-02 绑定：平台没返回 login 时退回表单填写的账号名")
    void bindFallsBackToFormAccountName() {
        when(clientRegistry.get("gitee")).thenReturn(client);
        when(client.fetchUser("t")).thenReturn(userInfo(null, null));
        when(accountMapper.selectCount(any())).thenReturn(0L);
        when(repoTokenCipher.encrypt("t")).thenReturn("ENC");
        when(accountMapper.insert(any(RepoAccount.class))).thenAnswer(invocation -> {
            invocation.<RepoAccount>getArgument(0).setId(501L);
            return 1;
        });

        service.bind(addDto("gitee", "form-name", "t"));

        ArgumentCaptor<RepoAccount> captor = ArgumentCaptor.forClass(RepoAccount.class);
        verify(accountMapper).insert(captor.capture());
        assertThat(captor.getValue().getAccountName()).isEqualTo("form-name");
    }

    @Test
    @DisplayName("2-02 绑定：拉取仓库失败不影响绑定结果，仓库数按 0 返回")
    void bindSucceedsWhenPullProjectsFails() {
        when(clientRegistry.get("github")).thenReturn(client);
        when(client.fetchUser("t")).thenReturn(userInfo("octocat", null));
        when(accountMapper.selectCount(any())).thenReturn(0L);
        when(repoTokenCipher.encrypt("t")).thenReturn("ENC");
        when(accountMapper.insert(any(RepoAccount.class))).thenAnswer(invocation -> {
            invocation.<RepoAccount>getArgument(0).setId(502L);
            return 1;
        });
        // 接口文档 8.2.2：本次拉取失败不影响绑定结果
        when(repoSyncService.pullProjects(any(), anyString()))
                .thenThrow(new RepoApiException(RepoApiException.API_FAILED, "502 Bad Gateway"));

        RepoAccountCreateVO vo = service.bind(addDto("github", "form-name", "t"));

        assertThat(vo.getAccountId()).isEqualTo(502L);
        assertThat(vo.getProjectCount()).isZero();
        verify(accountMapper).insert(any(RepoAccount.class));
        verify(repoSyncService).triggerAsync(502L, true, SyncTypeEnum.SCHEDULED);
    }

    @Test
    @DisplayName("2-02 绑定：重复绑定返回 20002，且不落库")
    void bindDuplicate() {
        when(clientRegistry.get("github")).thenReturn(client);
        when(client.fetchUser("t")).thenReturn(userInfo("octocat", null));
        when(accountMapper.selectCount(any())).thenReturn(1L);

        assertThatThrownBy(() -> service.bind(addDto("github", "form-name", "t")))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getCode())
                        .isEqualTo(RepoErrorCode.E20002.getCode()));

        verify(accountMapper, never()).insert(any(RepoAccount.class));
        verify(repoSyncService, never()).triggerAsync(any(), any(Boolean.class), any());
    }

    @Test
    @DisplayName("2-02 绑定：令牌无效映射 20003，平台调用失败映射 20007")
    void bindErrorCodes() {
        when(clientRegistry.get("github")).thenReturn(client);

        when(client.fetchUser("bad")).thenThrow(
                new RepoApiException(RepoApiException.INVALID_TOKEN, "401 Unauthorized"));
        assertThatThrownBy(() -> service.bind(addDto("github", "x", "bad")))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getCode())
                        .isEqualTo(RepoErrorCode.E20003.getCode()));

        when(client.fetchUser("boom")).thenThrow(
                new RepoApiException(RepoApiException.API_FAILED, "503"));
        assertThatThrownBy(() -> service.bind(addDto("github", "x", "boom")))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getCode())
                        .isEqualTo(RepoErrorCode.E20007.getCode()));

        verify(accountMapper, never()).insert(any(RepoAccount.class));
    }

    // ------------------------------------------------------------------ 2-04

    @Test
    @DisplayName("2-04 换令牌：先校验新令牌，再写密文、复位状态并触发全量重采")
    void updateTokenSuccess() {
        RepoAccount existing = existingAccount(201L, USER_ID, "github");
        when(accountMapper.selectById(201L)).thenReturn(existing);
        when(clientRegistry.get("github")).thenReturn(client);
        when(client.fetchUser("new-token")).thenReturn(userInfo("octocat", null));
        when(repoTokenCipher.encrypt("new-token")).thenReturn("ENC2");

        service.updateToken(updateDto(201L, "  new-token  "));

        ArgumentCaptor<RepoAccount> captor = ArgumentCaptor.forClass(RepoAccount.class);
        verify(accountMapper).updateById(captor.capture());
        RepoAccount update = captor.getValue();
        assertThat(update.getId()).isEqualTo(201L);
        assertThat(update.getAccessToken()).isEqualTo("ENC2");
        // 换令牌意味着账户恢复可用
        assertThat(update.getStatus()).isEqualTo(1);
        assertThat(update.getSyncStatus()).isEqualTo(SyncStatusEnum.SYNCING.getValue());
        // 令牌失效期间的提交会漏采，必须全量补齐
        verify(repoSyncService).triggerAsync(201L, true, SyncTypeEnum.SCHEDULED);
    }

    @Test
    @DisplayName("2-04 换令牌：新令牌无效时返回 20003，且不覆盖旧令牌")
    void updateTokenRejectsInvalidToken() {
        when(accountMapper.selectById(201L)).thenReturn(existingAccount(201L, USER_ID, "github"));
        when(clientRegistry.get("github")).thenReturn(client);
        when(client.fetchUser("bad")).thenThrow(
                new RepoApiException(RepoApiException.INVALID_TOKEN, "401"));

        assertThatThrownBy(() -> service.updateToken(updateDto(201L, "bad")))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getCode())
                        .isEqualTo(RepoErrorCode.E20003.getCode()));

        verify(accountMapper, never()).updateById(any(RepoAccount.class));
        verify(repoSyncService, never()).triggerAsync(any(), any(Boolean.class), any());
    }

    @Test
    @DisplayName("2-04 换令牌：账户不存在 20001，操作他人账户 20005")
    void updateTokenOwnership() {
        when(accountMapper.selectById(99999L)).thenReturn(null);
        assertThatThrownBy(() -> service.updateToken(updateDto(99999L, "t")))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getCode())
                        .isEqualTo(RepoErrorCode.E20001.getCode()));

        when(accountMapper.selectById(202L)).thenReturn(existingAccount(202L, 102L, "gitee"));
        assertThatThrownBy(() -> service.updateToken(updateDto(202L, "t")))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getCode())
                        .isEqualTo(RepoErrorCode.E20005.getCode()));

        verify(accountMapper, never()).updateById(any(RepoAccount.class));
    }

    // ------------------------------------------------------------------ helpers

    private RepoAccountAddDTO addDto(String platform, String accountName, String token) {
        RepoAccountAddDTO dto = new RepoAccountAddDTO();
        dto.setPlatform(platform);
        dto.setAccountName(accountName);
        dto.setAccessToken(token);
        return dto;
    }

    private RepoAccountUpdateDTO updateDto(Long accountId, String token) {
        RepoAccountUpdateDTO dto = new RepoAccountUpdateDTO();
        dto.setAccountId(accountId);
        dto.setAccessToken(token);
        return dto;
    }

    private RepoUserInfo userInfo(String login, String avatarUrl) {
        RepoUserInfo info = new RepoUserInfo();
        info.setLogin(login);
        info.setAvatarUrl(avatarUrl);
        return info;
    }

    private RepoAccount existingAccount(Long id, Long userId, String platform) {
        RepoAccount account = new RepoAccount();
        account.setId(id);
        account.setUserId(userId);
        account.setPlatform(platform);
        account.setAccountName("acc");
        account.setAccessToken("OLD_ENC");
        return account;
    }
}
