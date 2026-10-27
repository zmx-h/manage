package com.iecmp.repo.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.Result;
import com.iecmp.repo.dto.RepoAccountAddDTO;
import com.iecmp.repo.dto.RepoAccountUpdateDTO;
import com.iecmp.repo.service.RepoAccountService;
import com.iecmp.repo.vo.RepoAccountCreateVO;
import com.iecmp.repo.vo.RepoAccountListVO;
import com.iecmp.repo.vo.RepoPlatformListVO;
import com.iecmp.repo.vo.RepoSyncStatusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端-托管账户（repo 模块）。
 *
 * <p>覆盖接口：2-01、2-02、2-03、2-04、2-05、2-06，共 6 个。
 *
 * <p><b>返回值形状</b>：2-01 / 2-03 / 2-06 的 {@code data} 在接口文档里是数组，
 * 所以返回 {@code Result<List<...>>}——对应的 VO 类是数组元素类型，不是列表包装类。
 *
 * @author WanZiyu
 */
@Tag(name = "用户端-托管账户")
@RestController
@RequiredArgsConstructor
public class UserRepoAccountController {

    private final RepoAccountService repoAccountService;

    /**
     * 2-01 支持的平台列表。
     *
     * <p>{@code GET /user/repo/platform/list} —— 返回平台支持的代码托管平台与令牌申请入口，用于绑定表单的平台下拉与帮助提示
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "2-01 支持的平台列表", description = "返回平台支持的代码托管平台与令牌申请入口，用于绑定表单的平台下拉与帮助提示")
    @GetMapping("/user/repo/platform/list")
    public Result<List<RepoPlatformListVO>> getRepoPlatformList() {
        return Result.success(repoAccountService.listPlatforms());
    }

    /**
     * 2-02 绑定代码托管账户。
     *
     * <p>{@code POST /user/repo/account} —— 成员提交个人访问令牌绑定托管账户；后端校验令牌有效性后加密落库，并拉取一次仓库列表
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "2-02 绑定代码托管账户", description = "成员提交个人访问令牌绑定托管账户；后端校验令牌有效性后加密落库，并拉取一次仓库列表")
    @OperLog(module = "用户端-托管账户", value = "绑定代码托管账户")
    @PostMapping("/user/repo/account")
    public Result<RepoAccountCreateVO> postRepoAccount(@RequestBody @Valid RepoAccountAddDTO dto) {
        return Result.success(repoAccountService.bind(dto));
    }

    /**
     * 2-03 我的绑定账户列表。
     *
     * <p>{@code GET /user/repo/account/list} —— 查询当前登录用户绑定的全部托管账户，用于绑定管理页展示
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "2-03 我的绑定账户列表", description = "查询当前登录用户绑定的全部托管账户，用于绑定管理页展示")
    @GetMapping("/user/repo/account/list")
    public Result<List<RepoAccountListVO>> getRepoAccountList() {
        return Result.success(repoAccountService.listMyAccounts());
    }

    /**
     * 2-04 更新绑定账户令牌。
     *
     * <p>{@code PUT /user/repo/account} —— 托管平台令牌过期或权限变更时，用新令牌覆盖旧令牌
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "2-04 更新绑定账户令牌", description = "托管平台令牌过期或权限变更时，用新令牌覆盖旧令牌")
    @OperLog(module = "用户端-托管账户", value = "更新绑定账户令牌")
    @PutMapping("/user/repo/account")
    public Result<Void> putRepoAccount(@RequestBody @Valid RepoAccountUpdateDTO dto) {
        repoAccountService.updateToken(dto);
        return Result.success();
    }

    /**
     * 2-05 解绑账户。
     *
     * <p>{@code DELETE /user/repo/account/{id}} —— 解绑托管账户；解绑后不再采集该账户下的仓库与提交
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "2-05 解绑账户", description = "解绑托管账户；解绑后不再采集该账户下的仓库与提交")
    @OperLog(module = "用户端-托管账户", value = "解绑账户")
    @DeleteMapping("/user/repo/account/{id}")
    public Result<Void> deleteRepoAccount(@PathVariable("id") Long id) {
        repoAccountService.unbind(id);
        return Result.success();
    }

    /**
     * 2-06 我的同步状态。
     *
     * <p>{@code GET /user/repo/sync/status} —— 查询当前用户各绑定账户的自动采集状态、最近一次采集结果与下次自动采集时间
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "2-06 我的同步状态", description = "查询当前用户各绑定账户的自动采集状态、最近一次采集结果与下次自动采集时间")
    @GetMapping("/user/repo/sync/status")
    public Result<List<RepoSyncStatusVO>> getRepoSyncStatus() {
        return Result.success(repoAccountService.listSyncStatus());
    }
}
