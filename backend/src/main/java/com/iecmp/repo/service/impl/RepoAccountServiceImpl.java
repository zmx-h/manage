package com.iecmp.repo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.repo.client.RepoApiException;
import com.iecmp.repo.client.RepoPlatformClient;
import com.iecmp.repo.client.RepoPlatformClientRegistry;
import com.iecmp.repo.client.model.RepoUserInfo;
import com.iecmp.repo.dto.RepoAccountAddDTO;
import com.iecmp.repo.dto.RepoAccountUpdateDTO;
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
import com.iecmp.repo.model.RepoIdCount;
import com.iecmp.repo.properties.RepoProperties;
import com.iecmp.repo.query.RepoAccountPageQuery;
import com.iecmp.repo.service.RepoAccountService;
import com.iecmp.repo.service.RepoSyncService;
import com.iecmp.repo.util.RepoDateUtils;
import com.iecmp.repo.util.RepoPageUtils;
import com.iecmp.repo.util.RepoSortUtils;
import com.iecmp.repo.util.RepoTokenCipher;
import com.iecmp.repo.vo.RepoAccountCreateVO;
import com.iecmp.repo.vo.RepoAccountListVO;
import com.iecmp.repo.vo.RepoAccountPageVO;
import com.iecmp.repo.vo.RepoPlatformListVO;
import com.iecmp.repo.vo.RepoSyncStatusVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 托管账户业务实现（{@code repo_account}）。
 *
 * @author IECMP 后端组
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RepoAccountServiceImpl extends ServiceImpl<RepoAccountMapper, RepoAccount>
        implements RepoAccountService {

    /**
     * 接口 2-13 允许排序的字段白名单。
     *
     * <p>带表别名 {@code a.}，因为 SQL 里账户表左连了 {@code sys_user}。
     */
    private static final Map<String, String> ACCOUNT_SORT_FIELDS = Map.of(
            "create_time", "a.create_time",
            "bind_time", "a.bind_time",
            "last_sync_time", "a.last_sync_time",
            "status", "a.status");

    private final RepoProjectMapper repoProjectMapper;
    private final RepoCommitMapper repoCommitMapper;
    private final RepoSyncLogMapper repoSyncLogMapper;
    private final RepoSyncService repoSyncService;
    private final RepoPlatformClientRegistry clientRegistry;
    private final RepoTokenCipher repoTokenCipher;
    private final RepoProperties repoProperties;

    @Override
    public List<RepoPlatformListVO> listPlatforms() {
        List<RepoPlatformListVO> list = new ArrayList<>();
        for (RepoProperties.Platform platform : repoProperties.getPlatform()) {
            // 没有客户端实现的平台不下发，避免前端展示一个注定绑定失败的选项
            if (!clientRegistry.supports(platform.getCode())) {
                continue;
            }
            RepoPlatformListVO vo = new RepoPlatformListVO();
            vo.setPlatform(platform.getCode());
            vo.setPlatformName(platform.getName());
            vo.setTokenUrl(platform.getTokenUrl());
            vo.setTokenTip(platform.getTokenTip());
            list.add(vo);
        }
        return list;
    }

    /**
     * {@inheritDoc}
     *
     * <p><b>刻意不加 {@code @Transactional}</b>，两个原因：
     * <ol>
     *   <li>方法里要同步拉一次仓库列表（第三方 HTTP，可能重试到十几秒），
     *       包进事务会让数据库连接被长时间占住；</li>
     *   <li>后面要异步触发采集，异步线程读账户时事务必须已经提交，
     *       否则 {@code selectById} 查不到刚插入的行，即时采集会静默丢失。</li>
     * </ol>
     * 唯一需要原子性的是「插入账户」这一步，它本身就是单条语句。
     * 而「拉取失败不影响绑定结果」是接口文档 8.2.2 明确要求的行为，正好也不需要回滚。
     */
    @Override
    public RepoAccountCreateVO bind(RepoAccountAddDTO dto) {
        Long userId = LoginUserUtil.getUserId();
        String platform = dto.getPlatform().trim().toLowerCase(Locale.ROOT);
        String plainToken = dto.getAccessToken().trim();

        // 1. 校验令牌：这一步同时把令牌是否有效、平台是否可用问清楚了
        RepoUserInfo userInfo = fetchUserOrThrow(platform, plainToken);

        // 2. 账号名以平台返回的 login 为准
        //    请求里的 accountName 只是前端表单的提示值；唯一键是
        //    (user_id, platform, account_name, deleted)，如果拿用户随手写的字符串当身份，
        //    同一个真实账号换个写法就能重复绑定。平台返回的 login 才是身份。
        String accountName = StringUtils.hasText(userInfo.getLogin())
                ? userInfo.getLogin()
                : dto.getAccountName().trim();

        // 3. 重复绑定：先查一次给出友好错误，真正的兜底是唯一键（见下面的 catch）
        Long exists = baseMapper.selectCount(new LambdaQueryWrapper<RepoAccount>()
                .eq(RepoAccount::getUserId, userId)
                .eq(RepoAccount::getPlatform, platform)
                .eq(RepoAccount::getAccountName, accountName));
        if (exists != null && exists > 0) {
            throw new BusinessException(RepoErrorCode.E20002.getCode(), RepoErrorCode.E20002.getMsg());
        }

        // 4. 加密落库
        RepoAccount account = new RepoAccount();
        account.setUserId(userId);
        account.setPlatform(platform);
        account.setAccountName(accountName);
        account.setAccessToken(repoTokenCipher.encrypt(plainToken));
        account.setAvatarUrl(userInfo.getAvatarUrl());
        account.setStatus(1);
        account.setSyncStatus(SyncStatusEnum.SYNCING.getValue());
        account.setBindTime(LocalDateTime.now());
        try {
            baseMapper.insert(account);
        } catch (DuplicateKeyException e) {
            // 并发下两个请求同时通过第 3 步的检查，唯一键会拦下后一个
            throw new BusinessException(RepoErrorCode.E20002.getCode(), RepoErrorCode.E20002.getMsg());
        }

        // 5. 同步拉一次仓库列表。
        //    接口 2-02 要返回 projectCount「本次拉取到的仓库数量」，异步执行拿不到这个数，
        //    而且只查一次仓库列表耗时可控，所以放在请求线程里做。
        int projectCount = 0;
        try {
            projectCount = repoSyncService.pullProjects(account, plainToken);
        } catch (Exception e) {
            // 接口文档 8.2.2：本次拉取失败不影响绑定结果，失败原因由后续异步采集写进同步日志，
            // 成员可在接口 2-06 看到
            log.warn("账户 {} 绑定后拉取仓库列表失败：{}", account.getId(), e.getMessage());
        }

        // 6. 提交采集放到异步：首次采集要遍历所有仓库的所有分支，耗时远超一次接口调用。
        //    全量拉取——刚绑定的账户没有 last_sync_time，本来就该拉全部历史。
        repoSyncService.triggerAsync(account.getId(), true, SyncTypeEnum.SCHEDULED);

        RepoAccountCreateVO vo = new RepoAccountCreateVO();
        vo.setAccountId(account.getId());
        vo.setProjectCount(projectCount);
        return vo;
    }

    @Override
    public List<RepoAccountListVO> listMyAccounts() {
        List<RepoAccount> accounts = listMyAccountEntities();
        if (accounts.isEmpty()) {
            return List.of();
        }

        Map<Long, Integer> projectCounts = countProjects(accounts);

        List<RepoAccountListVO> list = new ArrayList<>(accounts.size());
        for (RepoAccount account : accounts) {
            RepoAccountListVO vo = new RepoAccountListVO();
            vo.setAccountId(account.getId());
            vo.setPlatform(account.getPlatform());
            vo.setPlatformName(platformName(account.getPlatform()));
            vo.setAccountName(account.getAccountName());
            vo.setAvatarUrl(account.getAvatarUrl());
            vo.setStatus(account.getStatus());
            vo.setProjectCount(projectCounts.getOrDefault(account.getId(), 0));
            vo.setLastSyncTime(RepoDateUtils.format(account.getLastSyncTime()));
            vo.setBindTime(RepoDateUtils.format(account.getBindTime()));
            // accessToken 及其密文都不出现在返回结构里（接口文档 8.2.3 的「令牌不回传」）
            list.add(vo);
        }
        return list;
    }

    @Override
    public void updateToken(RepoAccountUpdateDTO dto) {
        RepoAccount account = requireOwnAccount(dto.getAccountId());
        String plainToken = dto.getAccessToken().trim();

        // 先校验新令牌有效再覆盖：否则写进去一个坏令牌，账户直接变砖，成员只能靠同步失败提示去猜
        fetchUserOrThrow(account.getPlatform(), plainToken);

        RepoAccount update = new RepoAccount();
        update.setId(account.getId());
        update.setAccessToken(repoTokenCipher.encrypt(plainToken));
        // 换了令牌说明账户重新可用，状态一并复位
        update.setStatus(1);
        // 接口文档 8.2.4：置为「1 采集中」，由后台任务立即重采一次
        update.setSyncStatus(SyncStatusEnum.SYNCING.getValue());
        baseMapper.updateById(update);

        // 全量重采：接口文档 8.5.5 指出「成员令牌失效一段时间后重新授权」时，
        // 增量起点（last_sync_time）之后、令牌失效期间的提交会漏采，必须全量补齐
        repoSyncService.triggerAsync(account.getId(), true, SyncTypeEnum.SCHEDULED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbind(Long accountId) {
        RepoAccount account = requireOwnAccount(accountId);

        // 接口文档 8.2.5：解绑会连带逻辑删除该账户下的 repo_project 与 repo_commit。
        // 先取仓库 ID 再删，避免删完仓库就找不到提交记录的范围
        List<RepoProject> projects = repoProjectMapper.selectList(new LambdaQueryWrapper<RepoProject>()
                .select(RepoProject::getId)
                .eq(RepoProject::getAccountId, account.getId()));
        if (!projects.isEmpty()) {
            List<Long> projectIds = projects.stream().map(RepoProject::getId).toList();
            repoCommitMapper.delete(new LambdaQueryWrapper<RepoCommit>().in(RepoCommit::getProjectId, projectIds));
            repoProjectMapper.deleteByIds(projectIds);
        }
        baseMapper.deleteById(account.getId());
    }

    @Override
    public List<RepoSyncStatusVO> listSyncStatus() {
        List<RepoAccount> accounts = listMyAccountEntities();
        if (accounts.isEmpty()) {
            // 接口文档 8.2.6：无绑定账户时返回空数组
            return List.of();
        }

        List<Long> accountIds = accounts.stream().map(RepoAccount::getId).toList();
        Map<Long, RepoSyncLog> latestLogs = new HashMap<>(accountIds.size() * 2);
        for (RepoSyncLog entity : repoSyncLogMapper.selectLatestByAccountIds(accountIds)) {
            latestLogs.put(entity.getAccountId(), entity);
        }

        String nextSyncTime = nextSyncTime();
        List<RepoSyncStatusVO> list = new ArrayList<>(accounts.size());
        for (RepoAccount account : accounts) {
            RepoSyncStatusVO vo = new RepoSyncStatusVO();
            vo.setAccountId(account.getId());
            vo.setPlatform(account.getPlatform());
            vo.setAccountName(account.getAccountName());
            vo.setSyncStatus(account.getSyncStatus());
            vo.setLastSyncTime(RepoDateUtils.format(account.getLastSyncTime()));
            // 接口文档 8.2.6：newCommitCount 为必须字段，默认 0
            vo.setNewCommitCount(0);

            RepoSyncLog latestLog = latestLogs.get(account.getId());
            if (latestLog != null) {
                vo.setLastSyncResult(latestLog.getSyncResult());
                vo.setNewCommitCount(latestLog.getCommitCount() == null ? 0 : latestLog.getCommitCount());
                vo.setErrorMsg(latestLog.getErrorMsg());
            }
            vo.setNextSyncTime(nextSyncTime);
            list.add(vo);
        }
        return list;
    }

    @Override
    public PageResult<RepoAccountPageVO> pageAccounts(RepoAccountPageQuery query) {
        Page<RepoAccountPageVO> page = RepoPageUtils.of(query);
        String orderByClause = RepoSortUtils.resolveClause(query.getOrderBy(), ACCOUNT_SORT_FIELDS);
        IPage<RepoAccountPageVO> result = baseMapper.selectAccountPage(page, query, orderByClause);
        return PageResult.of(result);
    }

    /** 校验令牌并取回平台账号信息，把平台异常翻译成业务错误码。 */
    private RepoUserInfo fetchUserOrThrow(String platform, String plainToken) {
        RepoPlatformClient client = clientRegistry.get(platform);
        try {
            return client.fetchUser(plainToken);
        } catch (RepoApiException e) {
            // 20003 令牌无效 / 20007 平台调用失败，前端据此给不同的引导文案
            throw new BusinessException(RepoPlatformClientRegistry.toErrorCode(e).getCode(), e.getMessage());
        }
    }

    /** 取账户并校验归属，接口 2-04 / 2-05 共用。 */
    private RepoAccount requireOwnAccount(Long accountId) {
        RepoAccount account = baseMapper.selectById(accountId);
        if (account == null) {
            throw new BusinessException(RepoErrorCode.E20001.getCode(), RepoErrorCode.E20001.getMsg());
        }
        if (!LoginUserUtil.getUserId().equals(account.getUserId())) {
            throw new BusinessException(RepoErrorCode.E20005.getCode(), RepoErrorCode.E20005.getMsg());
        }
        return account;
    }

    private List<RepoAccount> listMyAccountEntities() {
        return baseMapper.selectList(new LambdaQueryWrapper<RepoAccount>()
                .eq(RepoAccount::getUserId, LoginUserUtil.getUserId())
                .orderByDesc(RepoAccount::getBindTime)
                .orderByDesc(RepoAccount::getId));
    }

    private Map<Long, Integer> countProjects(List<RepoAccount> accounts) {
        List<Long> accountIds = accounts.stream().map(RepoAccount::getId).toList();
        List<RepoIdCount> counts = repoProjectMapper.selectProjectCountsByAccount(accountIds);
        Map<Long, Integer> map = new HashMap<>(counts.size() * 2);
        for (RepoIdCount item : counts) {
            map.put(item.getId(), item.getCount());
        }
        return map;
    }

    /** 平台展示名，配置里查不到就退回平台标识，保证前端至少有东西可显示。 */
    private String platformName(String platform) {
        RepoProperties.Platform config = repoProperties.findPlatform(platform);
        return config == null || !StringUtils.hasText(config.getName()) ? platform : config.getName();
    }

    /**
     * 下一次自动采集时间（接口 2-06 的 {@code nextSyncTime}）。
     *
     * <p>由 {@code repo.sync.cron} 现算，不落库——定时任务的执行计划是配置，
     * 缓存一份反而会在改配置后不一致。cron 写错时返回 null（该字段非必须），不影响接口可用。
     */
    private String nextSyncTime() {
        String cron = repoProperties.getSync().getCron();
        if (!StringUtils.hasText(cron)) {
            return null;
        }
        try {
            LocalDateTime next = CronExpression.parse(cron).next(LocalDateTime.now());
            return RepoDateUtils.format(next);
        } catch (IllegalArgumentException e) {
            log.warn("repo.sync.cron 配置非法，无法计算下次采集时间：{}", cron);
            return null;
        }
    }
}
