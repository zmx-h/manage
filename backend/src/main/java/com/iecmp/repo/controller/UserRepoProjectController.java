package com.iecmp.repo.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.repo.dto.RepoProjectTrackDTO;
import com.iecmp.repo.query.RepoProjectPageQuery;
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
 * @author WanZiyu
 */
@Tag(name = "用户端-仓库列表")
@RestController
@RequiredArgsConstructor
public class UserRepoProjectController {

    private final RepoProjectService repoProjectService;

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
        return Result.success(repoProjectService.pageMyProjects(query));
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
        repoProjectService.updateTracked(id, dto.getTracked());
        return Result.success();
    }
}
