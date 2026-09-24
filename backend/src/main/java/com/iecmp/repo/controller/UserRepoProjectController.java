package com.iecmp.repo.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.repo.dto.RepoProjectTrackDTO;
import com.iecmp.repo.query.RepoProjectPageQuery;
import com.iecmp.repo.service.RepoAccountService;
import com.iecmp.repo.service.RepoProjectService;
import com.iecmp.repo.vo.RepoProjectPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端-仓库列表（repo 模块）。
 *
 * <p>覆盖接口：2-07、2-08，共 2 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-仓库列表")
@RestController
@RequiredArgsConstructor
public class UserRepoProjectController {

    /** RepoProject 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final RepoProjectService repoProjectService;
    /** RepoAccount 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final RepoAccountService repoAccountService;

    /**
     * 2-07 我的仓库分页。
     *
     * <p>{@code GET /user/repo/project/page} —— 分页查询当前用户已采集的仓库，支持按平台、是否纳入统计筛选
     *
     * <p>权限：登录即可；令牌：是；分页返回。
     */
    @Operation(summary = "2-07 我的仓库分页", description = "分页查询当前用户已采集的仓库，支持按平台、是否纳入统计筛选")
    @GetMapping("/user/repo/project/page")
    public Result<PageResult<RepoProjectPageVO>> getRepoProjectPage(@ParameterObject RepoProjectPageQuery query) {
        // TODO 待实现（接口 2-07）：见《IECMP-接口开发文档-v1.6.md》接口 2-07
        return Result.todo("2-07");
    }

    /**
     * 2-08 开启 / 关闭仓库统计。
     *
     * <p>{@code PUT /user/repo/project/{id}/track} —— 切换某个仓库是否纳入提交统计；关闭后该仓库的提交不再计入热力图与排行榜
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "2-08 开启 / 关闭仓库统计", description = "切换某个仓库是否纳入提交统计；关闭后该仓库的提交不再计入热力图与排行榜")
    @OperLog(module = "用户端-仓库列表", value = "开启 / 关闭仓库统计")
    @PutMapping("/user/repo/project/{id}/track")
    public Result<Void> putRepoProjectTrack(@PathVariable("id") Long id, @RequestBody @Valid RepoProjectTrackDTO dto) {
        // TODO 待实现（接口 2-08）：见《IECMP-接口开发文档-v1.6.md》接口 2-08
        return Result.todo("2-08");
    }
}
