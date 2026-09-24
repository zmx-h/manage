package com.iecmp.repo.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.repo.dto.RepoSyncDTO;
import com.iecmp.repo.query.RepoAccountPageQuery;
import com.iecmp.repo.query.RepoStatRankQuery;
import com.iecmp.repo.query.RepoStatTrendQuery;
import com.iecmp.repo.query.RepoSyncLogPageQuery;
import com.iecmp.repo.service.RepoAccountService;
import com.iecmp.repo.service.RepoCommitService;
import com.iecmp.repo.service.RepoProjectService;
import com.iecmp.repo.service.RepoSyncLogService;
import com.iecmp.repo.vo.RepoAccountPageVO;
import com.iecmp.repo.vo.RepoStatRankVO;
import com.iecmp.repo.vo.RepoStatTrendVO;
import com.iecmp.repo.vo.RepoSyncLogPageVO;
import com.iecmp.repo.vo.RepoSyncVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-仓库与采集（repo 模块）。
 *
 * <p>覆盖接口：2-13、2-14、2-15、2-16、2-17，共 5 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-仓库与采集")
@RestController
@RequiredArgsConstructor
public class AdminRepoController {

    /** RepoAccount 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final RepoAccountService repoAccountService;
    /** RepoProject 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final RepoProjectService repoProjectService;
    /** RepoCommit 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final RepoCommitService repoCommitService;
    /** RepoSyncLog 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final RepoSyncLogService repoSyncLogService;

    /**
     * 2-13 账户与仓库分页（后台）。
     *
     * <p>{@code GET /admin/repo/account/page} —— 管理端查看全中心成员的账户绑定与提交采集情况
     *
     * <p>权限：repo:account:list；令牌：是；分页返回。
     */
    @Operation(summary = "2-13 账户与仓库分页（后台）", description = "管理端查看全中心成员的账户绑定与提交采集情况")
    @RequiresPerm("repo:account:list")
    @GetMapping("/admin/repo/account/page")
    public Result<PageResult<RepoAccountPageVO>> getRepoAccountPage(@ParameterObject RepoAccountPageQuery query) {
        // TODO 待实现（接口 2-13）：见《IECMP-接口开发文档-v1.6.md》接口 2-13
        return Result.todo("2-13");
    }

    /**
     * 2-14 成员贡献排行（后台）。
     *
     * <p>{@code GET /admin/repo/stat/rank} —— 按指定指标返回全中心成员的贡献排行，用于贡献度看板
     *
     * <p>权限：repo:stat:list；令牌：是。
     */
    @Operation(summary = "2-14 成员贡献排行（后台）", description = "按指定指标返回全中心成员的贡献排行，用于贡献度看板")
    @RequiresPerm("repo:stat:list")
    @GetMapping("/admin/repo/stat/rank")
    public Result<RepoStatRankVO> getRepoStatRank(@ParameterObject RepoStatRankQuery query) {
        // TODO 待实现（接口 2-14）：见《IECMP-接口开发文档-v1.6.md》接口 2-14
        return Result.todo("2-14");
    }

    /**
     * 2-15 中心提交趋势（后台）。
     *
     * <p>{@code GET /admin/repo/stat/trend} —— 按时间粒度返回全中心的提交趋势与参与人数
     *
     * <p>权限：repo:stat:list；令牌：是。
     */
    @Operation(summary = "2-15 中心提交趋势（后台）", description = "按时间粒度返回全中心的提交趋势与参与人数")
    @RequiresPerm("repo:stat:list")
    @GetMapping("/admin/repo/stat/trend")
    public Result<RepoStatTrendVO> getRepoStatTrend(@ParameterObject RepoStatTrendQuery query) {
        // TODO 待实现（接口 2-15）：见《IECMP-接口开发文档-v1.6.md》接口 2-15
        return Result.todo("2-15");
    }

    /**
     * 2-16 同步日志分页（后台）。
     *
     * <p>{@code GET /admin/repo/sync/log/page} —— 查询自动采集与管理员手动补采的执行记录，用于排查采集失败原因
     *
     * <p>权限：repo:sync:list；令牌：是；分页返回。
     */
    @Operation(summary = "2-16 同步日志分页（后台）", description = "查询自动采集与管理员手动补采的执行记录，用于排查采集失败原因")
    @RequiresPerm("repo:sync:list")
    @GetMapping("/admin/repo/sync/log/page")
    public Result<PageResult<RepoSyncLogPageVO>> getRepoSyncLogPage(@ParameterObject RepoSyncLogPageQuery query) {
        // TODO 待实现（接口 2-16）：见《IECMP-接口开发文档-v1.6.md》接口 2-16
        return Result.todo("2-16");
    }

    /**
     * 2-17 手动补采（后台）。
     *
     * <p>{@code POST /admin/repo/sync} —— 管理员手工触发一次补采（一个、多个或全部账户）；日常采集由定时任务自动完成
     *
     * <p>权限：repo:sync:exec；令牌：是。
     */
    @Operation(summary = "2-17 手动补采（后台）", description = "管理员手工触发一次补采（一个、多个或全部账户）；日常采集由定时任务自动完成")
    @RequiresPerm("repo:sync:exec")
    @OperLog(module = "管理端-仓库与采集", value = "手动补采（后台）")
    @PostMapping("/admin/repo/sync")
    public Result<RepoSyncVO> postRepoSync(@RequestBody @Valid RepoSyncDTO dto) {
        // TODO 待实现（接口 2-17）：见《IECMP-接口开发文档-v1.6.md》接口 2-17
        return Result.todo("2-17");
    }
}
