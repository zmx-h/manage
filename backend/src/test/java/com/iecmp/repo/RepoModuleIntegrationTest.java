package com.iecmp.repo;

import com.iecmp.common.context.LoginUser;
import com.iecmp.common.context.UserContext;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.repo.query.RepoAccountPageQuery;
import com.iecmp.repo.query.RepoCommitChartQuery;
import com.iecmp.repo.query.RepoCommitHeatmapQuery;
import com.iecmp.repo.query.RepoCommitPageQuery;
import com.iecmp.repo.query.RepoProjectPageQuery;
import com.iecmp.repo.query.RepoStatOverviewQuery;
import com.iecmp.repo.query.RepoStatRankQuery;
import com.iecmp.repo.query.RepoStatTrendQuery;
import com.iecmp.repo.query.RepoSyncLogPageQuery;
import com.iecmp.repo.service.RepoAccountService;
import com.iecmp.repo.service.RepoCommitService;
import com.iecmp.repo.service.RepoProjectService;
import com.iecmp.repo.service.RepoSyncLogService;
import com.iecmp.repo.vo.RepoAccountListVO;
import com.iecmp.repo.vo.RepoAccountPageVO;
import com.iecmp.repo.vo.RepoCommitChartVO;
import com.iecmp.repo.vo.RepoCommitHeatmapVO;
import com.iecmp.repo.vo.RepoCommitPageVO;
import com.iecmp.repo.vo.RepoPlatformListVO;
import com.iecmp.repo.vo.RepoProjectPageVO;
import com.iecmp.repo.vo.RepoStatOverviewVO;
import com.iecmp.repo.vo.RepoStatRankVO;
import com.iecmp.repo.vo.RepoStatTrendVO;
import com.iecmp.repo.vo.RepoSyncLogPageVO;
import com.iecmp.repo.vo.RepoSyncStatusVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 模块二集成测试：真实 MySQL + 真实 mapper XML + 真实 MyBatis-Plus。
 *
 * <p>只绕过鉴权（模块一的登录接口尚未实现），业务链路一行不 mock。
 * 固定数据集让每个统计值都可以手算核对，断言写的是<b>手算结果</b>而不是「跑出来是什么就是什么」。
 *
 * <pre>
 * 用户 101 张三：账户 201（有效）→ 仓库 301（纳入统计）、302（未纳入统计）
 *   301: 2026-01-10 两次提交（+10/-5, +20/0）、01-11 一次（+5/-5）、01-12 一次合并提交（+100/-100）
 *   302: 2026-01-10 一次（+999/-999）
 * 用户 102 李四：账户 202（有效）→ 仓库 303
 *   303: 2026-02-03 两次提交（+1/-1, +1/-1）
 * 用户 103 王五：账户 203（令牌失效）→ 仓库 304
 *   304: 2026-02-05 一次（+50/-50）
 * </pre>
 *
 * <p><b>需要显式开启</b>：本类依赖一台加载了 {@code docs/sql} 建表与种子数据的 MySQL，
 * 直接跑 {@code mvn test} 会跳过，避免没有测试库的环境一片红。启用方式：
 * <pre>
 *   mvn test -Diecmp.it.enabled=true
 * </pre>
 * 连接参数可用 {@code -Diecmp.it.db.port / db.name / db.username / db.password} 覆盖，
 * 默认 {@code 127.0.0.1:3307/iecmp}、{@code root}、空密码。
 * 测试会清空并重建 {@code repo_*} 表的数据，<b>只能指向测试库，不要指向开发库</b>。
 */
@EnabledIfSystemProperty(named = "iecmp.it.enabled", matches = "true")
@SpringBootTest(properties = {
        "mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.nologging.NoLoggingImpl"
})
class RepoModuleIntegrationTest {

    private static final long USER_A = 101L;
    private static final long USER_B = 102L;
    private static final long USER_C = 103L;

    @Autowired
    private RepoAccountService repoAccountService;
    @Autowired
    private RepoProjectService repoProjectService;
    @Autowired
    private RepoCommitService repoCommitService;
    @Autowired
    private RepoSyncLogService repoSyncLogService;
    @Autowired
    private JdbcTemplate jdbc;

    /** 测试库连接参数，允许通过系统属性覆盖，默认指向本机 3307 上的独立测试实例。 */
    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        String url = "jdbc:mysql://127.0.0.1:" + System.getProperty("iecmp.it.db.port", "3307")
                + "/" + System.getProperty("iecmp.it.db.name", "iecmp")
                + "?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai"
                + "&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true";
        registry.add("spring.datasource.url", () -> url);
        registry.add("spring.datasource.username",
                () -> System.getProperty("iecmp.it.db.username", "root"));
        registry.add("spring.datasource.password",
                () -> System.getProperty("iecmp.it.db.password", ""));
    }

    @BeforeEach
    void setUp() {
        clean();
        seed();
        loginAs(USER_A);
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    // ------------------------------------------------------------------ 2-01

    @Test
    @DisplayName("2-01 平台列表：按配置顺序下发，且只下发已实现客户端的平台")
    void platformList() {
        List<RepoPlatformListVO> list = repoAccountService.listPlatforms();

        assertThat(list).extracting(RepoPlatformListVO::getPlatform)
                .containsExactly("github", "gitee");
        assertThat(list).extracting(RepoPlatformListVO::getPlatformName)
                .containsExactly("GitHub", "Gitee");
        assertThat(list.get(0).getTokenUrl()).isEqualTo("https://github.com/settings/tokens");
    }

    // ------------------------------------------------------------------ 2-03

    @Test
    @DisplayName("2-03 我的账户：只返回自己的账户，回填仓库数")
    void myAccounts() {
        List<RepoAccountListVO> list = repoAccountService.listMyAccounts();

        assertThat(list).hasSize(1);
        RepoAccountListVO vo = list.get(0);
        assertThat(vo.getAccountId()).isEqualTo(201L);
        assertThat(vo.getAccountName()).isEqualTo("acc_a");
        assertThat(vo.getStatus()).isEqualTo(1);
        // 账户 201 下有 301、302 两个仓库
        assertThat(vo.getProjectCount()).isEqualTo(2);
        assertThat(vo.getLastSyncTime()).isEqualTo("2026-03-01 02:00:00");
        assertThat(vo.getBindTime()).isEqualTo("2026-01-01 00:00:00");

        loginAs(USER_B);
        assertThat(repoAccountService.listMyAccounts()).hasSize(1)
                .extracting(RepoAccountListVO::getAccountId).containsExactly(202L);
    }

    // ------------------------------------------------------------------ 2-06

    @Test
    @DisplayName("2-06 同步状态：取最近一条日志的提交数与失败原因，并给出下次采集时间")
    void syncStatus() {
        List<RepoSyncStatusVO> list = repoAccountService.listSyncStatus();

        assertThat(list).hasSize(1);
        RepoSyncStatusVO vo = list.get(0);
        assertThat(vo.getAccountId()).isEqualTo(201L);
        assertThat(vo.getSyncStatus()).isEqualTo(2);
        assertThat(vo.getLastSyncTime()).isEqualTo("2026-03-01 02:00:00");
        // 最近一条日志是 502（2026-03-02，失败，commitCount = 0）
        assertThat(vo.getLastSyncResult()).isZero();
        assertThat(vo.getNewCommitCount()).isZero();
        assertThat(vo.getErrorMsg()).isEqualTo("令牌无效");
        assertThat(vo.getNextSyncTime()).isNotBlank();
    }

    // ------------------------------------------------------------------ 2-07

    @Test
    @DisplayName("2-07 我的仓库：默认 create_time 倒序，commitCount 排除合并提交")
    void projectPage() {
        PageResult<RepoProjectPageVO> page = repoProjectService.pageMyProjects(new RepoProjectPageQuery());

        assertThat(page.getTotal()).isEqualTo(2);
        // 302 的 create_time 更晚，默认倒序排第一
        assertThat(page.getList()).extracting(RepoProjectPageVO::getProjectId)
                .containsExactly(302L, 301L);
        assertThat(page.getList()).extracting(RepoProjectPageVO::getCommitCount)
                .containsExactly(1, 3);
        assertThat(page.getList().get(0).getRepoName()).isEqualTo("repo-untracked");
    }

    @Test
    @DisplayName("2-07 我的仓库：tracked / accountId / keyword 过滤，且 orderBy 注入被白名单挡住")
    void projectPageFilters() {
        RepoProjectPageQuery trackedQuery = new RepoProjectPageQuery();
        trackedQuery.setTracked(1);
        assertThat(repoProjectService.pageMyProjects(trackedQuery).getList())
                .extracting(RepoProjectPageVO::getProjectId).containsExactly(301L);

        RepoProjectPageQuery accountQuery = new RepoProjectPageQuery();
        accountQuery.setAccountId(201L);
        assertThat(repoProjectService.pageMyProjects(accountQuery).getTotal()).isEqualTo(2);

        RepoProjectPageQuery keywordQuery = new RepoProjectPageQuery();
        keywordQuery.setKeyword("repo-a");
        assertThat(repoProjectService.pageMyProjects(keywordQuery).getList())
                .extracting(RepoProjectPageVO::getRepoName).containsExactly("repo-a");

        // 非法排序列不应拼进 SQL，回落到默认排序且不抛异常
        RepoProjectPageQuery injection = new RepoProjectPageQuery();
        injection.setOrderBy("create_time desc; drop table repo_project");
        assertThat(repoProjectService.pageMyProjects(injection).getTotal()).isEqualTo(2);
    }

    // ------------------------------------------------------------------ 2-08

    @Test
    @DisplayName("2-08 开关统计：改 tracked 不动已采集的提交")
    void updateTracked() {
        repoProjectService.updateTracked(301L, 0);

        Integer tracked = jdbc.queryForObject(
                "SELECT tracked FROM repo_project WHERE id = 301", Integer.class);
        assertThat(tracked).isZero();
        // 关闭统计只影响聚合，提交记录仍在
        Integer commits = jdbc.queryForObject(
                "SELECT COUNT(*) FROM repo_commit WHERE project_id = 301 AND deleted = 0", Integer.class);
        assertThat(commits).isEqualTo(4);
    }

    @Test
    @DisplayName("2-08 错误分支：非法取值 400、越权 20005、不存在 20004")
    void updateTrackedErrors() {
        assertThatThrownBy(() -> repoProjectService.updateTracked(301L, 2))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getCode()).isEqualTo(400));

        assertThatThrownBy(() -> repoProjectService.updateTracked(303L, 0))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getCode()).isEqualTo(20005));

        assertThatThrownBy(() -> repoProjectService.updateTracked(99999L, 0))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getCode()).isEqualTo(20004));
    }

    // ------------------------------------------------------------------ 2-09

    @Test
    @DisplayName("2-09 提交明细：默认排除合并提交，includeMerge=1 可看到")
    void commitPage() {
        PageResult<RepoCommitPageVO> page = repoCommitService.pageMyCommits(new RepoCommitPageQuery());
        // 401、402、403、405（404 是合并提交，被排除）
        assertThat(page.getTotal()).isEqualTo(4);
        assertThat(page.getList()).extracting(RepoCommitPageVO::getCommitSha)
                .doesNotContain("aaaa0004");

        RepoCommitPageQuery withMerge = new RepoCommitPageQuery();
        withMerge.setIncludeMerge(1);
        assertThat(repoCommitService.pageMyCommits(withMerge).getTotal()).isEqualTo(5);

        // shortSha 取前 8 位，projectName 由批量查询回填
        RepoCommitPageVO first = repoCommitService.pageMyCommits(new RepoCommitPageQuery()).getList().get(0);
        assertThat(first.getShortSha()).hasSize(8);
        assertThat(first.getProjectName()).isNotBlank();
    }

    @Test
    @DisplayName("2-09 提交明细：仓库 / 分支 / 关键字 / 时间过滤")
    void commitPageFilters() {
        RepoCommitPageQuery branch = new RepoCommitPageQuery();
        branch.setBranch("dev");
        assertThat(repoCommitService.pageMyCommits(branch).getList())
                .extracting(RepoCommitPageVO::getCommitSha).containsExactly("aaaa0003");

        RepoCommitPageQuery keyword = new RepoCommitPageQuery();
        keyword.setKeyword("fix");
        assertThat(repoCommitService.pageMyCommits(keyword).getTotal()).isEqualTo(2);

        RepoCommitPageQuery range = new RepoCommitPageQuery();
        range.setBeginTime("2026-01-10");
        range.setEndTime("2026-01-10");
        // 401、402 在 301，405 在未纳入统计的 302；明细接口不受 tracked 影响，三次都算
        assertThat(repoCommitService.pageMyCommits(range).getTotal()).isEqualTo(3);

        RepoCommitPageQuery project = new RepoCommitPageQuery();
        project.setProjectId(302L);
        assertThat(repoCommitService.pageMyCommits(project).getList())
                .extracting(RepoCommitPageVO::getCommitSha).containsExactly("aaaa0005");
    }

    // ------------------------------------------------------------------ 2-10

    @Test
    @DisplayName("2-10 提交趋势：按日分桶 + 缺失日期补 0 + 排除合并提交")
    void chartByDay() {
        RepoCommitChartQuery query = chartQuery("2026-01-09", "2026-01-12", 1);
        List<RepoCommitChartVO> list = repoCommitService.chart(query);

        assertThat(list).extracting(RepoCommitChartVO::getDate)
                .containsExactly("2026-01-09", "2026-01-10", "2026-01-11", "2026-01-12");
        assertThat(list).extracting(RepoCommitChartVO::getCommitCount)
                .containsExactly(0, 2, 1, 0);
        // 01-10：+10/-5 与 +20/0
        assertThat(list.get(1).getAddLines()).isEqualTo(30);
        assertThat(list.get(1).getDelLines()).isEqualTo(5);
        // 01-12 只有合并提交，按 0 计
        assertThat(list.get(3).getAddLines()).isZero();
    }

    @Test
    @DisplayName("2-10 提交趋势：按月分桶")
    void chartByMonth() {
        List<RepoCommitChartVO> list = repoCommitService.chart(chartQuery("2026-01-01", "2026-01-31", 3));

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDate()).isEqualTo("2026-01");
        assertThat(list.get(0).getCommitCount()).isEqualTo(3);
        assertThat(list.get(0).getAddLines()).isEqualTo(35);
        assertThat(list.get(0).getDelLines()).isEqualTo(10);
    }

    @Test
    @DisplayName("2-10 提交趋势：按周分桶，标签为 ISO 周")
    void chartByWeek() {
        List<RepoCommitChartVO> list = repoCommitService.chart(chartQuery("2026-01-09", "2026-01-12", 2));

        // 2026-01-05 ~ 01-11 是第 2 周，01-12 属于第 3 周；第 3 周没有提交，补 0 而不是省略
        assertThat(list).extracting(RepoCommitChartVO::getDate)
                .containsExactly("2026-第2周", "2026-第3周");
        assertThat(list.get(0).getCommitCount()).isEqualTo(3);
        assertThat(list.get(1).getCommitCount()).isZero();
    }

    // ------------------------------------------------------------------ 2-11

    @Test
    @DisplayName("2-11 热力图：整年逐日下发，色阶由后端按 25/50/75% 阈值算好")
    void heatmap() {
        RepoCommitHeatmapQuery query = new RepoCommitHeatmapQuery();
        query.setYear("2026");
        RepoCommitHeatmapVO vo = repoCommitService.heatmap(query);

        assertThat(vo.getYear()).isEqualTo("2026");
        assertThat(vo.getTotalCommits()).isEqualTo(3);
        assertThat(vo.getActiveDays()).isEqualTo(2);
        assertThat(vo.getMaxDaily()).isEqualTo(2);
        // 2026 不是闰年
        assertThat(vo.getCells()).hasSize(365);

        RepoCommitHeatmapVO.CellVO jan10 = cellOf(vo, "2026-01-10");
        RepoCommitHeatmapVO.CellVO jan11 = cellOf(vo, "2026-01-11");
        RepoCommitHeatmapVO.CellVO jan12 = cellOf(vo, "2026-01-12");

        assertThat(jan10.getCount()).isEqualTo(2);
        // 2 > max(2) × 75% = 1.5 → 4 级
        assertThat(jan10.getLevel()).isEqualTo(4);
        // 1 ≤ max × 50% = 1 → 2 级
        assertThat(jan11.getLevel()).isEqualTo(2);
        // 当天只有合并提交，按 0 计
        assertThat(jan12.getCount()).isZero();
        assertThat(jan12.getLevel()).isZero();
    }

    // ------------------------------------------------------------------ 2-12

    @Test
    @DisplayName("2-12 个人概览：各项指标与贡献度得分与手算一致")
    void overview() {
        RepoStatOverviewQuery query = new RepoStatOverviewQuery();
        query.setBeginTime("2026-01-01 00:00:00");
        query.setEndTime("2026-12-31 23:59:59");
        RepoStatOverviewVO vo = repoCommitService.overview(query);

        assertThat(vo.getCommitCount()).isEqualTo(3);
        assertThat(vo.getActiveDays()).isEqualTo(2);
        assertThat(vo.getAddLines()).isEqualTo(35);
        assertThat(vo.getDelLines()).isEqualTo(10);
        // 合并提交在 301，302 未纳入统计
        assertThat(vo.getProjectCount()).isEqualTo(1);
        assertThat(vo.getBranchCount()).isEqualTo(2);
        // 3×1 + 2×2 + (35+10)×0.01 = 7.45
        assertThat(vo.getContributionScore()).isEqualByComparingTo(new BigDecimal("7.45"));
        assertThat(vo.getRank()).isEqualTo(1);
        assertThat(vo.getLastSyncTime()).isEqualTo("2026-03-01 02:00:00");
    }

    @Test
    @DisplayName("2-12 个人概览：没有提交的成员不给名次，指标全 0")
    void overviewEmpty() {
        loginAs(USER_C);
        RepoStatOverviewQuery query = new RepoStatOverviewQuery();
        query.setBeginTime("2026-01-01 00:00:00");
        query.setEndTime("2026-12-31 23:59:59");
        RepoStatOverviewVO vo = repoCommitService.overview(query);

        // 用户 C 的账户令牌失效，成员端口径仍统计他的历史提交
        assertThat(vo.getCommitCount()).isEqualTo(1);
        assertThat(vo.getRank()).isNull();
    }

    // ------------------------------------------------------------------ 2-14

    @Test
    @DisplayName("2-14 贡献排行：排除令牌失效账户，名次从 1 开始")
    void rank() {
        List<RepoStatRankVO> list = repoCommitService.rank(new RepoStatRankQuery());

        // C 的账户 status = 0，不进后台看板
        assertThat(list).hasSize(2);
        assertThat(list).extracting(RepoStatRankVO::getRank).containsExactly(1, 2);
        assertThat(list).extracting(RepoStatRankVO::getUserId).containsExactly(USER_A, USER_B);
        assertThat(list).extracting(RepoStatRankVO::getRealName).containsExactly("张三", "李四");

        RepoStatRankVO first = list.get(0);
        assertThat(first.getCommitCount()).isEqualTo(3);
        assertThat(first.getActiveDays()).isEqualTo(2);
        assertThat(first.getContributionScore()).isEqualByComparingTo(new BigDecimal("7.45"));
        // 2×1 + 1×2 + (2+2)×0.01 = 4.04
        assertThat(list.get(1).getContributionScore()).isEqualByComparingTo(new BigDecimal("4.04"));
    }

    @Test
    @DisplayName("2-14 贡献排行：sortField 切换排序依据，limit 收敛到 1~100")
    void rankSortAndLimit() {
        RepoStatRankQuery byActiveDays = new RepoStatRankQuery();
        byActiveDays.setSortField(2);
        assertThat(repoCommitService.rank(byActiveDays))
                .extracting(RepoStatRankVO::getUserId).containsExactly(USER_A, USER_B);

        RepoStatRankQuery byLines = new RepoStatRankQuery();
        byLines.setSortField(3);
        assertThat(repoCommitService.rank(byLines))
                .extracting(RepoStatRankVO::getUserId).containsExactly(USER_A, USER_B);

        RepoStatRankQuery byScore = new RepoStatRankQuery();
        byScore.setSortField(4);
        assertThat(repoCommitService.rank(byScore))
                .extracting(RepoStatRankVO::getUserId).containsExactly(USER_A, USER_B);

        RepoStatRankQuery limited = new RepoStatRankQuery();
        limited.setLimit(1);
        assertThat(repoCommitService.rank(limited)).hasSize(1);

        // 非法 sortField 回落为「按提交数」
        RepoStatRankQuery invalid = new RepoStatRankQuery();
        invalid.setSortField(99);
        assertThat(repoCommitService.rank(invalid).get(0).getUserId()).isEqualTo(USER_A);
    }

    // ------------------------------------------------------------------ 2-15

    @Test
    @DisplayName("2-15 中心趋势：按月分桶，参与人数按周期去重，排除令牌失效账户")
    void centerTrend() {
        RepoStatTrendQuery query = new RepoStatTrendQuery();
        query.setBeginDate("2026-01-01");
        query.setEndDate("2026-02-28");
        query.setGranularity(3);
        List<RepoStatTrendVO> list = repoCommitService.centerTrend(query);

        assertThat(list).extracting(RepoStatTrendVO::getDate).containsExactly("2026-01", "2026-02");
        assertThat(list).extracting(RepoStatTrendVO::getCommitCount).containsExactly(3, 2);
        assertThat(list).extracting(RepoStatTrendVO::getMemberCount).containsExactly(1, 1);
        assertThat(list.get(0).getAddLines()).isEqualTo(35);
        assertThat(list.get(0).getDelLines()).isEqualTo(10);
        assertThat(list.get(1).getAddLines()).isEqualTo(2);
    }

    @Test
    @DisplayName("2-15 中心趋势：按周分桶时无提交的周期补 0")
    void centerTrendZeroFill() {
        RepoStatTrendQuery query = new RepoStatTrendQuery();
        query.setBeginDate("2026-01-01");
        query.setEndDate("2026-02-28");
        query.setGranularity(2);
        List<RepoStatTrendVO> list = repoCommitService.centerTrend(query);

        assertThat(list.size()).isGreaterThan(2);
        // 第 1 周没有提交，补 0 而不是断点
        assertThat(list.get(0).getCommitCount()).isZero();
        assertThat(list.get(0).getMemberCount()).isZero();
    }

    // ------------------------------------------------------------------ 2-13

    @Test
    @DisplayName("2-13 账户分页：projectCount / commitCount 聚合，且排除合并提交")
    void accountPage() {
        PageResult<RepoAccountPageVO> page = repoAccountService.pageAccounts(new RepoAccountPageQuery());

        assertThat(page.getTotal()).isEqualTo(3);
        RepoAccountPageVO acc201 = page.getList().stream()
                .filter(v -> v.getAccountId().equals(201L)).findFirst().orElseThrow();
        assertThat(acc201.getRealName()).isEqualTo("张三");
        assertThat(acc201.getGrade()).isEqualTo("2024");
        assertThat(acc201.getProjectCount()).isEqualTo(2);
        // 301 的三条非合并提交 + 302 的一条（302 只是不纳入统计，提交仍然采集到了）
        assertThat(acc201.getCommitCount()).isEqualTo(4);
        assertThat(acc201.getLastSyncTime()).isEqualTo("2026-03-01 02:00:00");

        RepoAccountPageVO acc203 = page.getList().stream()
                .filter(v -> v.getAccountId().equals(203L)).findFirst().orElseThrow();
        assertThat(acc203.getStatus()).isZero();
        assertThat(acc203.getCommitCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("2-13 账户分页：keyword / platform / status / grade 过滤")
    void accountPageFilters() {
        RepoAccountPageQuery keyword = new RepoAccountPageQuery();
        keyword.setKeyword("张三");
        assertThat(repoAccountService.pageAccounts(keyword).getTotal()).isEqualTo(1);

        RepoAccountPageQuery platform = new RepoAccountPageQuery();
        platform.setPlatform("github");
        assertThat(repoAccountService.pageAccounts(platform).getTotal()).isEqualTo(2);

        RepoAccountPageQuery status = new RepoAccountPageQuery();
        status.setStatus(0);
        assertThat(repoAccountService.pageAccounts(status).getTotal()).isEqualTo(1);

        RepoAccountPageQuery grade = new RepoAccountPageQuery();
        grade.setGrade("2024");
        assertThat(repoAccountService.pageAccounts(grade).getTotal()).isEqualTo(2);
    }

    // ------------------------------------------------------------------ 2-16

    @Test
    @DisplayName("2-16 同步日志：过滤条件与分页")
    void syncLogPage() {
        assertThat(repoSyncLogService.pageLogs(new RepoSyncLogPageQuery()).getTotal()).isEqualTo(3);

        RepoSyncLogPageQuery failed = new RepoSyncLogPageQuery();
        failed.setSyncResult(0);
        assertThat(repoSyncLogService.pageLogs(failed).getList())
                .extracting(RepoSyncLogPageVO::getLogId).containsExactly(502L);

        RepoSyncLogPageQuery manual = new RepoSyncLogPageQuery();
        manual.setSyncType(2);
        assertThat(repoSyncLogService.pageLogs(manual).getTotal()).isEqualTo(1);

        RepoSyncLogPageQuery byUser = new RepoSyncLogPageQuery();
        byUser.setUserId(USER_B);
        PageResult<RepoSyncLogPageVO> page = repoSyncLogService.pageLogs(byUser);
        assertThat(page.getTotal()).isEqualTo(1);
        assertThat(page.getList().get(0).getRealName()).isEqualTo("李四");
    }

    @Test
    @DisplayName("2-16 同步日志：只给日期时截止时间补齐到 23:59:59，当天日志不会被漏掉")
    void syncLogDateBoundary() {
        RepoSyncLogPageQuery query = new RepoSyncLogPageQuery();
        query.setBeginTime("2026-03-02");
        query.setEndTime("2026-03-02");

        // 502（02:00）与 503（03:00）都在 2026-03-02 当天
        assertThat(repoSyncLogService.pageLogs(query).getTotal()).isEqualTo(2);
    }

    // ------------------------------------------------------------------ 2-05

    @Test
    @DisplayName("2-05 解绑：连带逻辑删除该账户下的仓库与提交")
    void unbindCascades() {
        repoAccountService.unbind(201L);

        assertThat(count("repo_account", 201L)).isZero();
        assertThat(count("repo_project", 301L)).isZero();
        assertThat(count("repo_project", 302L)).isZero();
        assertThat(count("repo_commit", 401L)).isZero();
        assertThat(count("repo_commit", 405L)).isZero();
        // 别人的数据不受影响
        assertThat(count("repo_project", 303L)).isEqualTo(1);
        assertThat(count("repo_commit", 406L)).isEqualTo(1);
    }

    @Test
    @DisplayName("2-05 解绑：账户不存在 20001，解绑他人账户 20005")
    void unbindErrors() {
        assertThatThrownBy(() -> repoAccountService.unbind(99999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getCode()).isEqualTo(20001));

        assertThatThrownBy(() -> repoAccountService.unbind(202L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getCode()).isEqualTo(20005));
    }

    // ------------------------------------------------------------------ helpers

    private RepoCommitChartQuery chartQuery(String begin, String end, int granularity) {
        RepoCommitChartQuery query = new RepoCommitChartQuery();
        query.setBeginDate(begin);
        query.setEndDate(end);
        query.setGranularity(granularity);
        return query;
    }

    private RepoCommitHeatmapVO.CellVO cellOf(RepoCommitHeatmapVO vo, String date) {
        return vo.getCells().stream().filter(c -> date.equals(c.getDate())).findFirst().orElseThrow();
    }

    private int count(String table, long id) {
        Integer value = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + table + " WHERE id = ? AND deleted = 0", Integer.class, id);
        return value == null ? 0 : value;
    }

    private void loginAs(long userId) {
        LoginUser user = new LoginUser();
        user.setUserId(userId);
        user.setUsername("u" + userId);
        user.setSuperAdmin(true);
        UserContext.set(user);
    }

    /** 用原始 SQL 清库，确保每一轮都是干净且完全相同的初始状态。 */
    private void clean() {
        jdbc.update("DELETE FROM repo_sync_log");
        jdbc.update("DELETE FROM repo_commit");
        jdbc.update("DELETE FROM repo_project");
        jdbc.update("DELETE FROM repo_account");
        jdbc.update("DELETE FROM sys_user WHERE id IN (101, 102, 103)");
    }

    private void seed() {
        jdbc.update("INSERT INTO sys_user (id, username, password, real_name, grade, status, deleted) "
                + "VALUES (101, 'user_a', 'x', '张三', '2024', 1, 0), "
                + "(102, 'user_b', 'x', '李四', '2025', 1, 0), "
                + "(103, 'user_c', 'x', '王五', '2024', 1, 0)");

        jdbc.update("INSERT INTO repo_account "
                + "(id, user_id, platform, account_name, access_token, status, sync_status, last_sync_time, bind_time, deleted) "
                + "VALUES (201, 101, 'github', 'acc_a', 'tok', 1, 2, '2026-03-01 02:00:00', '2026-01-01 00:00:00', 0), "
                + "(202, 102, 'gitee', 'acc_b', 'tok', 1, 2, '2026-03-02 02:00:00', '2026-01-02 00:00:00', 0), "
                + "(203, 103, 'github', 'acc_c', 'tok', 0, 3, NULL, '2026-01-03 00:00:00', 0)");

        jdbc.update("INSERT INTO repo_project "
                + "(id, account_id, user_id, platform, repo_name, repo_url, owner_name, star_count, fork_count, "
                + "tracked, sync_status, create_time, deleted) "
                + "VALUES (301, 201, 101, 'github', 'repo-a', 'https://github.com/a/repo-a', 'a', 1, 0, 1, 2, '2026-01-01 10:00:00', 0), "
                + "(302, 201, 101, 'github', 'repo-untracked', 'https://github.com/a/repo-u', 'a', 0, 0, 0, 2, '2026-01-02 10:00:00', 0), "
                + "(303, 202, 102, 'gitee', 'repo-b', 'https://gitee.com/b/repo-b', 'b', 0, 0, 1, 2, '2026-01-03 10:00:00', 0), "
                + "(304, 203, 103, 'github', 'repo-c', 'https://github.com/c/repo-c', 'c', 0, 0, 1, 3, '2026-01-04 10:00:00', 0)");

        jdbc.update("INSERT INTO repo_commit "
                + "(id, project_id, user_id, commit_sha, branch, author_name, author_email, message, "
                + "commit_time, add_lines, del_lines, is_merge, deleted) VALUES "
                + "(401, 301, 101, 'aaaa0001', 'main', '张三', 'a@x', 'feat: init',  '2026-01-10 10:00:00', 10, 5, 0, 0), "
                + "(402, 301, 101, 'aaaa0002', 'main', '张三', 'a@x', 'fix: bug',     '2026-01-10 11:00:00', 20, 0, 0, 0), "
                + "(403, 301, 101, 'aaaa0003', 'dev',  '张三', 'a@x', 'fix: another', '2026-01-11 09:00:00', 5, 5, 0, 0), "
                + "(404, 301, 101, 'aaaa0004', 'main', '张三', 'a@x', 'Merge branch', '2026-01-12 09:00:00', 100, 100, 1, 0), "
                + "(405, 302, 101, 'aaaa0005', 'main', '张三', 'a@x', 'feat: untracked', '2026-01-10 12:00:00', 999, 999, 0, 0), "
                + "(406, 303, 102, 'bbbb0001', 'main', '李四', 'b@x', 'feat: b1', '2026-02-03 10:00:00', 1, 1, 0, 0), "
                + "(407, 303, 102, 'bbbb0002', 'main', '李四', 'b@x', 'feat: b2', '2026-02-03 11:00:00', 1, 1, 0, 0), "
                + "(408, 304, 103, 'cccc0001', 'main', '王五', 'c@x', 'feat: c1', '2026-02-05 10:00:00', 50, 50, 0, 0)");

        jdbc.update("INSERT INTO repo_sync_log "
                + "(id, account_id, user_id, sync_type, sync_result, commit_count, cost_ms, error_msg, create_time, deleted) "
                + "VALUES (501, 201, 101, 1, 1, 5, 1000, NULL, '2026-03-01 02:00:00', 0), "
                + "(502, 201, 101, 2, 0, 0, 500, '令牌无效', '2026-03-02 02:00:00', 0), "
                + "(503, 202, 102, 1, 1, 2, 800, NULL, '2026-03-02 03:00:00', 0)");
    }
}
