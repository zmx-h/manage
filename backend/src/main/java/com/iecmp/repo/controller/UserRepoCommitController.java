package com.iecmp.repo.controller;

import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.repo.query.RepoCommitChartQuery;
import com.iecmp.repo.query.RepoCommitHeatmapQuery;
import com.iecmp.repo.query.RepoCommitPageQuery;
import com.iecmp.repo.query.RepoStatOverviewQuery;
import com.iecmp.repo.service.RepoCommitService;
import com.iecmp.repo.vo.RepoCommitChartVO;
import com.iecmp.repo.vo.RepoCommitHeatmapVO;
import com.iecmp.repo.vo.RepoCommitPageVO;
import com.iecmp.repo.vo.RepoStatOverviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端-提交与贡献（repo 模块）。
 *
 * <p>覆盖接口：2-09、2-10、2-11、2-12，共 4 个。
 *
 * <p><b>返回值形状</b>：2-10 的 {@code data} 在接口文档里是数组（折线图的点集），
 * 2-11 与 2-12 是单个对象，因此前者返回 {@code Result<List<...>>}。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-提交与贡献")
@RestController
@RequiredArgsConstructor
public class UserRepoCommitController {

    private final RepoCommitService repoCommitService;

    /**
     * 2-09 提交记录分页。
     *
     * <p>{@code GET /user/repo/commit/page} —— 分页查询当前用户已采集的提交明细，支持按仓库、分支、时间与提交信息筛选
     *
     * <p>权限：登录即可；令牌：是；分页返回。
     */
    @Operation(summary = "2-09 提交记录分页", description = "分页查询当前用户已采集的提交明细，支持按仓库、分支、时间与提交信息筛选")
    @GetMapping("/user/repo/commit/page")
    public Result<PageResult<RepoCommitPageVO>> getRepoCommitPage(@ParameterObject RepoCommitPageQuery query) {
        return Result.success(repoCommitService.pageMyCommits(query));
    }

    /**
     * 2-10 提交-日期趋势图。
     *
     * <p>{@code GET /user/repo/commit/chart} —— 按时间粒度返回当前用户的提交趋势，供前端绘制折线图
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "2-10 提交-日期趋势图", description = "按时间粒度返回当前用户的提交趋势，供前端绘制折线图")
    @GetMapping("/user/repo/commit/chart")
    public Result<List<RepoCommitChartVO>> getRepoCommitChart(@ParameterObject RepoCommitChartQuery query) {
        return Result.success(repoCommitService.chart(query));
    }

    /**
     * 2-11 贡献热力图。
     *
     * <p>{@code GET /user/repo/commit/heatmap} —— 按「日期 → 当日提交数量」聚合，返回 GitHub 风格热力图数据
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "2-11 贡献热力图", description = "按「日期 → 当日提交数量」聚合，返回 GitHub 风格热力图数据")
    @GetMapping("/user/repo/commit/heatmap")
    public Result<RepoCommitHeatmapVO> getRepoCommitHeatmap(@ParameterObject RepoCommitHeatmapQuery query) {
        return Result.success(repoCommitService.heatmap(query));
    }

    /**
     * 2-12 个人贡献概览。
     *
     * <p>{@code GET /user/repo/stat/overview} —— 返回当前用户在指定时间范围内的贡献指标汇总与排名
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "2-12 个人贡献概览", description = "返回当前用户在指定时间范围内的贡献指标汇总与排名")
    @GetMapping("/user/repo/stat/overview")
    public Result<RepoStatOverviewVO> getRepoStatOverview(@ParameterObject RepoStatOverviewQuery query) {
        return Result.success(repoCommitService.overview(query));
    }
}
