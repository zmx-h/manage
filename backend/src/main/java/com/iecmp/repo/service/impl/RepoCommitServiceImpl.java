package com.iecmp.repo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.repo.entity.RepoAccount;
import com.iecmp.repo.entity.RepoCommit;
import com.iecmp.repo.entity.RepoProject;
import com.iecmp.repo.mapper.RepoAccountMapper;
import com.iecmp.repo.mapper.RepoCommitMapper;
import com.iecmp.repo.mapper.RepoProjectMapper;
import com.iecmp.repo.model.RepoDailyUserStat;
import com.iecmp.repo.model.RepoUserSummary;
import com.iecmp.repo.properties.RepoProperties;
import com.iecmp.repo.query.RepoCommitChartQuery;
import com.iecmp.repo.query.RepoCommitHeatmapQuery;
import com.iecmp.repo.query.RepoCommitPageQuery;
import com.iecmp.repo.query.RepoStatOverviewQuery;
import com.iecmp.repo.query.RepoStatRankQuery;
import com.iecmp.repo.query.RepoStatTrendQuery;
import com.iecmp.repo.service.RepoCommitService;
import com.iecmp.repo.util.RepoDateUtils;
import com.iecmp.repo.util.RepoPageUtils;
import com.iecmp.repo.util.RepoSortUtils;
import com.iecmp.repo.vo.RepoCommitChartVO;
import com.iecmp.repo.vo.RepoCommitHeatmapVO;
import com.iecmp.repo.vo.RepoCommitPageVO;
import com.iecmp.repo.vo.RepoStatOverviewVO;
import com.iecmp.repo.vo.RepoStatRankVO;
import com.iecmp.repo.vo.RepoStatTrendVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 提交记录业务实现（{@code repo_commit}）。
 *
 * <p>本类是模块二所有统计口径的唯一落点，六条约定在下面各方法里逐一体现，改口径只需改这里。
 *
 * @author WanZiyu
 */
@Service
@RequiredArgsConstructor
public class RepoCommitServiceImpl extends ServiceImpl<RepoCommitMapper, RepoCommit>
        implements RepoCommitService {

    /**
     * 成员端统计口径。
     *
     * <p>服务 {@code tracked}，但<b>不</b>跟账户 {@code status}：成员令牌过期后，
     * 接口 2-06 会提示他去 2-04 换令牌，但已经采到的历史提交不该从他的图表里消失——
     * 统计的是「他做过什么」，不是「他的令牌现在还有效吗」。
     */
    private static final boolean MEMBER_TRACKED_ONLY = true;

    private static final boolean MEMBER_ACTIVE_ACCOUNT_ONLY = false;

    /**
     * 后台看板统计口径。
     *
     * <p>接口文档 8.5.2 明确要求：{@code repo_account.status = 0}（令牌失效）与
     * {@code tracked = 0} 的仓库都不计入排行，接口 2-15 与之一致。
     */
    private static final boolean ADMIN_TRACKED_ONLY = true;

    private static final boolean ADMIN_ACTIVE_ACCOUNT_ONLY = true;

    /** 接口 2-10 的默认时间粒度：按日。 */
    private static final int CHART_DEFAULT_GRANULARITY = RepoDateUtils.GRANULARITY_DAY;

    /** 接口 2-15 的默认时间粒度：按周。 */
    private static final int TREND_DEFAULT_GRANULARITY = RepoDateUtils.GRANULARITY_WEEK;

    /** 接口 2-10 默认统计近 30 天。 */
    private static final int CHART_DEFAULT_DAYS = 30;

    /** 接口 2-15 默认统计近 90 天。 */
    private static final int TREND_DEFAULT_DAYS = 90;

    /** 接口 2-09 允许排序的字段白名单。 */
    private static final Map<String, String> COMMIT_SORT_FIELDS = Map.of(
            "commit_time", "commit_time",
            "create_time", "create_time",
            "add_lines", "add_lines",
            "del_lines", "del_lines");

    /** 提交 SHA 的短格式长度（接口文档 8.4.1 的 {@code shortSha}）。 */
    private static final int SHORT_SHA_LENGTH = 8;

    /** 贡献度得分保留位数。 */
    private static final int SCORE_SCALE = 2;

    private final RepoProjectMapper repoProjectMapper;
    private final RepoAccountMapper repoAccountMapper;
    private final RepoProperties repoProperties;

    @Override
    public PageResult<RepoCommitPageVO> pageMyCommits(RepoCommitPageQuery query) {
        Long userId = LoginUserUtil.getUserId();

        QueryWrapper<RepoCommit> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (query.getProjectId() != null) {
            wrapper.eq("project_id", query.getProjectId());
        }
        if (StringUtils.hasText(query.getBranch())) {
            wrapper.eq("branch", query.getBranch().trim());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like("message", query.getKeyword().trim());
        }
        // includeMerge 默认 0（接口文档 8.4.1）：明细默认看不到合并提交，
        // 这正是「一次合并刷出一堆提交」不会污染明细的原因
        if (!Integer.valueOf(1).equals(query.getIncludeMerge())) {
            wrapper.eq("is_merge", 0);
        }
        LocalDateTime beginTime = RepoDateUtils.parseDateTime(query.getBeginTime(), false);
        if (beginTime != null) {
            wrapper.ge("commit_time", beginTime);
        }
        LocalDateTime endTime = RepoDateUtils.parseDateTime(query.getEndTime(), true);
        if (endTime != null) {
            wrapper.le("commit_time", endTime);
        }

        RepoSortUtils.Sort sort = RepoSortUtils.resolve(query.getOrderBy(), COMMIT_SORT_FIELDS);
        if (sort == null) {
            // 接口文档 2.4 节：orderBy 默认 create_time desc
            wrapper.orderByDesc("create_time").orderByDesc("id");
        } else if (sort.asc()) {
            wrapper.orderByAsc(sort.column());
        } else {
            wrapper.orderByDesc(sort.column());
        }

        Page<RepoCommit> page = RepoPageUtils.of(query);
        IPage<RepoCommit> result = baseMapper.selectPage(page, wrapper);

        // 明细接口不受 tracked 影响：接口 2-08 说的是「不计入统计」，
        // 而不是「明细查不到」，关掉统计的仓库依然要能看到提交记录
        Map<Long, String> projectNames = loadProjectNames(result.getRecords());
        List<RepoCommitPageVO> list = result.getRecords().stream()
                .map(commit -> toCommitPageVo(commit, projectNames))
                .toList();

        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getPages(), list);
    }

    @Override
    public List<RepoCommitChartVO> chart(RepoCommitChartQuery query) {
        Long userId = LoginUserUtil.getUserId();

        LocalDate end = RepoDateUtils.parseDate(query.getEndDate());
        if (end == null) {
            end = LocalDate.now();
        }
        LocalDate begin = RepoDateUtils.parseDate(query.getBeginDate());
        if (begin == null) {
            // 接口文档 8.4.2：beginDate 默认「近 30 天」
            begin = end.minusDays(CHART_DEFAULT_DAYS - 1L);
        }
        if (begin.isAfter(end)) {
            // 区间反了就退化成单点，不报错——日期写反不该让图表接口 400
            begin = end;
        }
        int granularity = RepoDateUtils.normalizeGranularity(
                query.getGranularity() == null ? CHART_DEFAULT_GRANULARITY : query.getGranularity());

        List<RepoDailyUserStat> dailyStats = baseMapper.selectDailyUserStats(userId, query.getProjectId(),
                begin.atStartOfDay(), end.atTime(23, 59, 59), MEMBER_TRACKED_ONLY, MEMBER_ACTIVE_ACCOUNT_ONLY);

        // 按周期分桶累加
        Map<String, long[]> buckets = new LinkedHashMap<>();
        for (RepoDailyUserStat stat : dailyStats) {
            long[] bucket = buckets.computeIfAbsent(
                    RepoDateUtils.periodLabel(stat.getStatDate(), granularity), key -> new long[3]);
            bucket[0] += nullToZero(stat.getCommitCount());
            bucket[1] += nullToZero(stat.getAddLines());
            bucket[2] += nullToZero(stat.getDelLines());
        }

        // 接口文档 8.4.2：区间内没有提交的时间点同样返回，commitCount = 0，保证折线不断点
        List<RepoCommitChartVO> list = new ArrayList<>();
        for (String label : RepoDateUtils.periodLabels(begin, end, granularity)) {
            long[] bucket = buckets.getOrDefault(label, ZERO_BUCKET);
            RepoCommitChartVO vo = new RepoCommitChartVO();
            vo.setDate(label);
            vo.setCommitCount((int) bucket[0]);
            vo.setAddLines((int) bucket[1]);
            vo.setDelLines((int) bucket[2]);
            list.add(vo);
        }
        return list;
    }

    @Override
    public RepoCommitHeatmapVO heatmap(RepoCommitHeatmapQuery query) {
        // 不传 userId 看自己的；传了就看指定成员（接口文档 8.4.3）
        Long userId = query.getUserId() == null ? LoginUserUtil.getUserId() : query.getUserId();
        Integer year = RepoDateUtils.parseYear(query.getYear());
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        LocalDate begin = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        List<RepoDailyUserStat> dailyStats = baseMapper.selectDailyUserStats(userId, null,
                begin.atStartOfDay(), end.atTime(23, 59, 59), MEMBER_TRACKED_ONLY, MEMBER_ACTIVE_ACCOUNT_ONLY);

        Map<LocalDate, Integer> countByDate = new HashMap<>();
        int totalCommits = 0;
        int maxDaily = 0;
        for (RepoDailyUserStat stat : dailyStats) {
            int count = nullToZero(stat.getCommitCount());
            countByDate.merge(stat.getStatDate(), count, Integer::sum);
            totalCommits += count;
            maxDaily = Math.max(maxDaily, count);
        }

        // 整年逐日下发（含无提交的日子，level = 0）：色阶阈值由后端算，
        // 前端不需要自己铺格子也不需要自己判阈值，PC 与移动端着色才能一致
        List<RepoCommitHeatmapVO.CellVO> cells = new ArrayList<>();
        int activeDays = 0;
        for (LocalDate cursor = begin; !cursor.isAfter(end); cursor = cursor.plusDays(1)) {
            int count = countByDate.getOrDefault(cursor, 0);
            if (count > 0) {
                activeDays++;
            }
            RepoCommitHeatmapVO.CellVO cell = new RepoCommitHeatmapVO.CellVO();
            cell.setDate(RepoDateUtils.formatDate(cursor));
            cell.setCount(count);
            cell.setLevel(resolveLevel(count, maxDaily));
            cells.add(cell);
        }

        RepoCommitHeatmapVO vo = new RepoCommitHeatmapVO();
        vo.setYear(String.valueOf(year));
        vo.setTotalCommits(totalCommits);
        vo.setActiveDays(activeDays);
        vo.setMaxDaily(maxDaily);
        vo.setCells(cells);
        return vo;
    }

    @Override
    public RepoStatOverviewVO overview(RepoStatOverviewQuery query) {
        Long userId = LoginUserUtil.getUserId();
        LocalDateTime begin = defaultBeginTime(RepoDateUtils.parseDateTime(query.getBeginTime(), false));
        LocalDateTime end = defaultEndTime(RepoDateUtils.parseDateTime(query.getEndTime(), true));

        RepoUserSummary summary = baseMapper.selectUserSummary(userId, begin, end,
                MEMBER_TRACKED_ONLY, MEMBER_ACTIVE_ACCOUNT_ONLY);

        RepoStatOverviewVO vo = new RepoStatOverviewVO();
        vo.setCommitCount(summary == null ? 0 : nullToZero(summary.getCommitCount()));
        vo.setActiveDays(summary == null ? 0 : nullToZero(summary.getActiveDays()));
        vo.setAddLines(summary == null ? 0 : nullToZero(summary.getAddLines()));
        vo.setDelLines(summary == null ? 0 : nullToZero(summary.getDelLines()));
        vo.setProjectCount(summary == null ? 0 : nullToZero(summary.getProjectCount()));
        vo.setBranchCount(summary == null ? 0 : nullToZero(summary.getBranchCount()));
        vo.setContributionScore(summary == null ? BigDecimal.ZERO.setScale(SCORE_SCALE) : contributionScore(summary));
        vo.setLastSyncTime(RepoDateUtils.format(lastSyncTimeOf(userId)));

        // rank 是「在中心内的提交数排名」，也就是接口 2-14 排行榜上自己的位置。
        // 因此门槛值必须用后台口径单独算一遍自己：成员口径会把令牌已失效账户的提交也算进来，
        // 拿它去和后台口径统计出来的其他成员比，等于用两把尺子量身高，名次会偏好。
        // 代价是多一次走 idx_user_commit_time 的聚合查询。
        // commitCount 为 0 时不给名次（该字段非必须），也顺带免掉这次查询。
        if (vo.getCommitCount() > 0) {
            RepoUserSummary rankScope = baseMapper.selectUserSummary(userId, begin, end,
                    ADMIN_TRACKED_ONLY, ADMIN_ACTIVE_ACCOUNT_ONLY);
            int ownCommits = rankScope == null ? 0 : nullToZero(rankScope.getCommitCount());
            if (ownCommits > 0) {
                int more = baseMapper.countUsersWithMoreCommits(ownCommits, begin, end,
                        ADMIN_TRACKED_ONLY, ADMIN_ACTIVE_ACCOUNT_ONLY);
                vo.setRank(more + 1);
            }
        }
        return vo;
    }

    @Override
    public List<RepoStatRankVO> rank(RepoStatRankQuery query) {
        LocalDateTime begin = defaultBeginTime(RepoDateUtils.parseDateTime(query.getBeginTime(), false));
        LocalDateTime end = defaultEndTime(RepoDateUtils.parseDateTime(query.getEndTime(), true));
        Integer sortField = normalizeSortField(query.getSortField());
        Integer limit = normalizeLimit(query.getLimit());

        RepoProperties.Weight weight = repoProperties.getContribution().getWeight();
        List<RepoUserSummary> rows = baseMapper.selectUserSummaries(begin, end, sortField,
                weight.getCommit(), weight.getActiveDay(), weight.getCodeLine(), limit,
                ADMIN_TRACKED_ONLY, ADMIN_ACTIVE_ACCOUNT_ONLY);

        List<RepoStatRankVO> list = new ArrayList<>(rows.size());
        int rank = 1;
        for (RepoUserSummary row : rows) {
            RepoStatRankVO vo = new RepoStatRankVO();
            // 名次按排序后的顺序原地编号，从 1 开始（接口文档 8.5.2）
            vo.setRank(rank++);
            vo.setUserId(row.getUserId());
            vo.setRealName(row.getRealName());
            vo.setGrade(row.getGrade());
            vo.setAvatarUrl(row.getAvatarUrl());
            vo.setCommitCount(nullToZero(row.getCommitCount()));
            vo.setActiveDays(nullToZero(row.getActiveDays()));
            vo.setAddLines(nullToZero(row.getAddLines()));
            vo.setDelLines(nullToZero(row.getDelLines()));
            vo.setContributionScore(contributionScore(row));
            list.add(vo);
        }
        return list;
    }

    @Override
    public List<RepoStatTrendVO> centerTrend(RepoStatTrendQuery query) {
        LocalDate end = RepoDateUtils.parseDate(query.getEndDate());
        if (end == null) {
            end = LocalDate.now();
        }
        LocalDate begin = RepoDateUtils.parseDate(query.getBeginDate());
        if (begin == null) {
            // 接口文档 8.5.3：beginDate 默认「近 90 天」
            begin = end.minusDays(TREND_DEFAULT_DAYS - 1L);
        }
        if (begin.isAfter(end)) {
            begin = end;
        }
        int granularity = RepoDateUtils.normalizeGranularity(
                query.getGranularity() == null ? TREND_DEFAULT_GRANULARITY : query.getGranularity());

        // userId 传 null：查全中心
        List<RepoDailyUserStat> dailyStats = baseMapper.selectDailyUserStats(null, null,
                begin.atStartOfDay(), end.atTime(23, 59, 59), ADMIN_TRACKED_ONLY, ADMIN_ACTIVE_ACCOUNT_ONLY);

        Map<String, long[]> buckets = new LinkedHashMap<>();
        Map<String, Set<Long>> members = new LinkedHashMap<>();
        for (RepoDailyUserStat stat : dailyStats) {
            String label = RepoDateUtils.periodLabel(stat.getStatDate(), granularity);
            long[] bucket = buckets.computeIfAbsent(label, key -> new long[3]);
            bucket[0] += nullToZero(stat.getCommitCount());
            bucket[1] += nullToZero(stat.getAddLines());
            bucket[2] += nullToZero(stat.getDelLines());
            // 成员数在<b>周期内</b>去重：把每日活跃人数直接相加会把同一个人重复计数
            members.computeIfAbsent(label, key -> new HashSet<>()).add(stat.getUserId());
        }

        List<RepoStatTrendVO> list = new ArrayList<>();
        for (String label : RepoDateUtils.periodLabels(begin, end, granularity)) {
            long[] bucket = buckets.getOrDefault(label, ZERO_BUCKET);
            RepoStatTrendVO vo = new RepoStatTrendVO();
            vo.setDate(label);
            vo.setCommitCount((int) bucket[0]);
            vo.setMemberCount(members.getOrDefault(label, Set.of()).size());
            vo.setAddLines((int) bucket[1]);
            vo.setDelLines((int) bucket[2]);
            list.add(vo);
        }
        return list;
    }

    /**
     * 贡献度得分：{@code 提交数 × w1 + 活跃天数 × w2 + 代码行数 × w3}（接口文档 8.4.4）。
     *
     * <p>权重取自 {@code repo.contribution.weight.*}，公式是 v1 占位实现，团队会议定下来后
     * 只改配置即可，{@code contributionScore} 字段结构不变。
     *
     * <p>「代码行数」取 {@code add_lines + del_lines}（总变更行数）——接口文档只写了
     * 「代码行数 × 0.01」没区分增删，而两个字段都是对外返回的指标。
     */
    private BigDecimal contributionScore(RepoUserSummary row) {
        RepoProperties.Weight weight = repoProperties.getContribution().getWeight();
        BigDecimal commits = BigDecimal.valueOf(nullToZero(row.getCommitCount()));
        BigDecimal activeDays = BigDecimal.valueOf(nullToZero(row.getActiveDays()));
        BigDecimal codeLines = BigDecimal.valueOf(
                (long) nullToZero(row.getAddLines()) + nullToZero(row.getDelLines()));

        return commits.multiply(weight.getCommit())
                .add(activeDays.multiply(weight.getActiveDay()))
                .add(codeLines.multiply(weight.getCodeLine()))
                .setScale(SCORE_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 热力图的色阶等级（接口文档 8.4.3）。
     *
     * <p>阈值基于全年单日最大值，边界为「小于等于」：
     * 1 级 {@code count ≤ max × 25%}，2 级 {@code ≤ 50%}，3 级 {@code ≤ 75%}，超过为 4 级。
     */
    private int resolveLevel(int count, int maxDaily) {
        if (count <= 0 || maxDaily <= 0) {
            return 0;
        }
        if (count <= maxDaily * 0.25) {
            return 1;
        }
        if (count <= maxDaily * 0.5) {
            return 2;
        }
        if (count <= maxDaily * 0.75) {
            return 3;
        }
        return 4;
    }

    /** 接口 2-12 / 2-14 的默认起始时间：本年度 1 月 1 日 00:00:00。 */
    private LocalDateTime defaultBeginTime(LocalDateTime parsed) {
        return parsed == null ? LocalDate.now().withDayOfYear(1).atStartOfDay() : parsed;
    }

    /** 接口 2-12 / 2-14 的默认截止时间：当前时间。 */
    private LocalDateTime defaultEndTime(LocalDateTime parsed) {
        return parsed == null ? LocalDateTime.now() : parsed;
    }

    /** 接口 2-14 的 sortField 收敛到 1~4，非法值按「提交数」处理。 */
    private Integer normalizeSortField(Integer sortField) {
        if (sortField == null || sortField < 1 || sortField > 4) {
            return 1;
        }
        return sortField;
    }

    /** 接口 2-14 的 limit 收敛到 1~100，默认 20。 */
    private Integer normalizeLimit(Integer limit) {
        if (limit == null) {
            return 20;
        }
        return Math.min(Math.max(limit, 1), RepoPageUtils.MAX_PAGE_SIZE);
    }

    /**
     * 接口 2-12 的 lastSyncTime：当前用户各账户最近一次采集完成时间里的最大值。
     *
     * <p>某个账户还没完成过首次采集时 {@code last_sync_time} 为 NULL，此时 MyBatis 会把这一行
     * 映射成 {@code null} 元素放进 List（只 select 一列且该列为 NULL 时的既有行为），
     * 所以过滤空元素必须放在取 getter 之前，否则这里会对 null 调 getLastSyncTime 抛 NPE。
     */
    private LocalDateTime lastSyncTimeOf(Long userId) {
        List<RepoAccount> accounts = repoAccountMapper.selectList(new LambdaQueryWrapper<RepoAccount>()
                .select(RepoAccount::getLastSyncTime)
                .eq(RepoAccount::getUserId, userId));
        return accounts.stream()
                .filter(Objects::nonNull)
                .map(RepoAccount::getLastSyncTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    /** 批量取仓库名，供接口 2-09 的 {@code projectName} 回填，避免逐行查询。 */
    private Map<Long, String> loadProjectNames(List<RepoCommit> commits) {
        if (commits.isEmpty()) {
            return Map.of();
        }
        List<Long> projectIds = commits.stream().map(RepoCommit::getProjectId).distinct().toList();
        List<RepoProject> projects = repoProjectMapper.selectList(new LambdaQueryWrapper<RepoProject>()
                .select(RepoProject::getId, RepoProject::getRepoName)
                .in(RepoProject::getId, projectIds));
        Map<Long, String> names = new HashMap<>(projects.size() * 2);
        for (RepoProject project : projects) {
            names.put(project.getId(), project.getRepoName());
        }
        return names;
    }

    private RepoCommitPageVO toCommitPageVo(RepoCommit commit, Map<Long, String> projectNames) {
        RepoCommitPageVO vo = new RepoCommitPageVO();
        vo.setCommitId(commit.getId());
        vo.setCommitSha(commit.getCommitSha());
        vo.setShortSha(shortSha(commit.getCommitSha()));
        vo.setProjectId(commit.getProjectId());
        vo.setProjectName(projectNames.get(commit.getProjectId()));
        vo.setBranch(commit.getBranch());
        vo.setAuthorName(commit.getAuthorName());
        vo.setMessage(commit.getMessage());
        vo.setCommitTime(RepoDateUtils.format(commit.getCommitTime()));
        vo.setAddLines(commit.getAddLines());
        vo.setDelLines(commit.getDelLines());
        vo.setIsMerge(commit.getIsMerge());
        return vo;
    }

    /** 提交 SHA 前 8 位；异常数据（过短）原样返回，不做补位。 */
    private String shortSha(String sha) {
        if (sha == null) {
            return null;
        }
        return sha.length() <= SHORT_SHA_LENGTH ? sha : sha.substring(0, SHORT_SHA_LENGTH);
    }

    private static int nullToZero(Integer value) {
        return value == null ? 0 : value;
    }

    /** 补零用的空桶，复用同一个实例避免每个周期都 new 一次。 */
    private static final long[] ZERO_BUCKET = new long[3];
}
