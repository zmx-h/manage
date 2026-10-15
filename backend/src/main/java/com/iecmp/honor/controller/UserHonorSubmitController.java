package com.iecmp.honor.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.honor.dto.UserHonorAlumniAddDTO;
import com.iecmp.honor.dto.UserHonorAlumniUpdateDTO;
import com.iecmp.honor.query.HonorAlumniMyPageQuery;
import com.iecmp.honor.service.HonorAlumniService;
import com.iecmp.honor.service.HonorRecordService;
import com.iecmp.honor.vo.HonorAlumniMyPageVO;
import com.iecmp.honor.vo.HonorAlumniUpdateVO;
import com.iecmp.honor.vo.UserHonorAlumniCreateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端-展板信息提交（honor 模块）。
 *
 * <p>覆盖接口：6-14、6-15、6-16、6-19，共 4 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-展板信息提交")
@RestController
@RequiredArgsConstructor
public class UserHonorSubmitController {

    /** HonorAlumni 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final HonorAlumniService honorAlumniService;
    /** HonorRecord 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final HonorRecordService honorRecordService;

    /**
     * 6-14 成员提交学长信息。
     *
     * <p>{@code POST /user/honor/alumni} —— 成员自助提交学长学姐信息，提交后进入待审核状态
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "6-14 成员提交学长信息", description = "成员自助提交学长学姐信息，提交后进入待审核状态")
    @OperLog(module = "用户端-展板信息提交", value = "成员提交学长信息")
    @PostMapping("/user/honor/alumni")
    public Result<UserHonorAlumniCreateVO> postHonorAlumni(@RequestBody @Valid UserHonorAlumniAddDTO dto) {
        // TODO 待实现（接口 6-14）：见《IECMP-接口开发文档-v1.6.md》接口 6-14
        return Result.todo("6-14");
    }

    /**
     * 6-15 我的提交记录分页。
     *
     * <p>{@code GET /user/honor/alumni/my/page} —— 分页查询当前登录用户提交的学长学姐信息及其审核状态
     *
     * <p>权限：登录即可；令牌：是；分页返回。
     */
    @Operation(summary = "6-15 我的提交记录分页", description = "分页查询当前登录用户提交的学长学姐信息及其审核状态")
    @GetMapping("/user/honor/alumni/my/page")
    public Result<PageResult<HonorAlumniMyPageVO>> getHonorAlumniMyPage(@ParameterObject HonorAlumniMyPageQuery query) {
        // TODO 待实现（接口 6-15）：见《IECMP-接口开发文档-v1.6.md》接口 6-15
        return Result.todo("6-15");
    }

    /**
     * 6-16 撤回提交。
     *
     * <p>{@code PUT /user/honor/alumni/{id}/withdraw} —— 成员撤回尚未审核的提交，撤回后记录失效
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "6-16 撤回提交", description = "成员撤回尚未审核的提交，撤回后记录失效")
    @OperLog(module = "用户端-展板信息提交", value = "撤回提交")
    @PutMapping("/user/honor/alumni/{id}/withdraw")
    public Result<Void> putHonorAlumniWithdraw(@PathVariable("id") Long id) {
        // TODO 待实现（接口 6-16）：见《IECMP-接口开发文档-v1.6.md》接口 6-16
        return Result.todo("6-16");
    }

    /**
     * 6-19 重新提交我的信息。
     *
     * <p>{@code PUT /user/honor/alumni/{id}} —— 提交人修改自己提交的学长学姐信息（被驳回后修正、或已通过后补充资料）并重新送审
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "6-19 重新提交我的信息", description = "提交人修改自己提交的学长学姐信息（被驳回后修正、或已通过后补充资料）并重新送审")
    @OperLog(module = "用户端-展板信息提交", value = "重新提交我的信息")
    @PutMapping("/user/honor/alumni/{id}")
    public Result<HonorAlumniUpdateVO> putHonorAlumni(@PathVariable("id") Long id, @RequestBody @Valid UserHonorAlumniUpdateDTO dto) {
        // TODO 待实现（接口 6-19）：见《IECMP-接口开发文档-v1.6.md》接口 6-19
        return Result.todo("6-19");
    }
}
