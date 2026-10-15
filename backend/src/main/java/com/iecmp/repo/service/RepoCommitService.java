package com.iecmp.repo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.common.result.PageResult;
import com.iecmp.repo.entity.RepoCommit;
import com.iecmp.repo.query.RepoCommitChartQuery;
import com.iecmp.repo.query.RepoCommitHeatmapQuery;
import com.iecmp.repo.query.RepoCommitPageQuery;
import com.iecmp.repo.query.RepoStatOverviewQuery;
import com.iecmp.repo.query.RepoStatRankQuery;
import com.iecmp.repo.query.RepoStatTrendQuery;
import com.iecmp.repo.vo.RepoCommitChartVO;
import com.iecmp.repo.vo.RepoCommitHeatmapVO;
import com.iecmp.repo.vo.RepoCommitPageVO;
import com.iecmp.repo.vo.RepoStatOverviewVO;
import com.iecmp.repo.vo.RepoStatRankVO;
import com.iecmp.repo.vo.RepoStatTrendVO;

import java.util.List;

/**
 * 提交记录业务接口（{@code repo_commit}），覆盖接口 2-09 ~ 2-12 与后台的 2-14、2-15。
 *
 * <p><b>模块二的统计口径全部在这里收敛</b>（各方法实现里的注释会逐条说明）：
 * <ul>
 *   <li>合并提交（{@code is_merge = 1}）默认不计入任何统计，只在接口 2-09 传
 *       {@code includeMerge = 1} 时出现在明细里；</li>
 *   <li>{@code tracked = 0} 的仓库不计入统计（接口文档 8.3.2），但提交明细仍可查；</li>
 *   <li>后台看板额外要求账户 {@code status = 1}（接口文档 8.5.2），成员端不要求——
 *       否则成员令牌一过期，自己的历史提交就从图表里消失了；</li>
 *   <li>接口 2-10、2-15 的折线图必须给没有提交的时间点补 0，否则前端断点。</li>
 * </ul>
 */
public interface RepoCommitService extends IService<RepoCommit> {

    /** 2-09 提交记录分页。 */
    PageResult<RepoCommitPageVO> pageMyCommits(RepoCommitPageQuery query);

    /** 2-10 提交-日期趋势图（当前用户）。 */
    List<RepoCommitChartVO> chart(RepoCommitChartQuery query);

    /** 2-11 贡献热力图（当前用户，或指定的其他成员）。 */
    RepoCommitHeatmapVO heatmap(RepoCommitHeatmapQuery query);

    /** 2-12 个人贡献概览。 */
    RepoStatOverviewVO overview(RepoStatOverviewQuery query);

    /** 2-14 成员贡献排行（后台，全中心）。 */
    List<RepoStatRankVO> rank(RepoStatRankQuery query);

    /** 2-15 中心提交趋势（后台，全中心）。 */
    List<RepoStatTrendVO> centerTrend(RepoStatTrendQuery query);
}
