package com.iecmp.honor.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.honor.dto.UserHonorAlumniAddDTO;
import com.iecmp.honor.dto.UserHonorAlumniUpdateDTO;
import com.iecmp.honor.query.HonorAlumniMyPageQuery;
import com.iecmp.honor.service.HonorSubmitService;
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
 * 用户端-展板信息提交。
 *
 * <p>覆盖接口：6-14、6-15、6-16、6-19，共 4 个。
 */
@Tag(name = "用户端-展板信息提交")
@RestController
@RequiredArgsConstructor
public class UserHonorSubmitController {

    private final HonorSubmitService honorSubmitService;

    @Operation(summary = "6-14 成员提交学长信息", description = "成员自助提交学长学姐信息，提交后进入待审核状态")
    @OperLog(module = "用户端-展板信息提交", value = "成员提交学长信息")
    @PostMapping("/user/honor/alumni")
    public Result<UserHonorAlumniCreateVO> postHonorAlumni(
            @RequestBody @Valid UserHonorAlumniAddDTO dto) {
        return Result.success(honorSubmitService.submit(dto));
    }

    @Operation(summary = "6-15 我的提交记录分页", description = "分页查询当前登录用户提交的学长学姐信息及其审核状态")
    @GetMapping("/user/honor/alumni/my/page")
    public Result<PageResult<HonorAlumniMyPageVO>> getHonorAlumniMyPage(
            @ParameterObject HonorAlumniMyPageQuery query) {
        return Result.success(honorSubmitService.myPage(query));
    }

    @Operation(summary = "6-16 撤回提交", description = "成员撤回尚未审核的提交，撤回后记录失效")
    @OperLog(module = "用户端-展板信息提交", value = "撤回提交")
    @PutMapping("/user/honor/alumni/{id}/withdraw")
    public Result<Void> putHonorAlumniWithdraw(@PathVariable("id") Long id) {
        honorSubmitService.withdraw(id);
        return Result.success();
    }

    @Operation(summary = "6-19 重新提交我的信息", description = "提交人修改自己提交的学长学姐信息并重新送审")
    @OperLog(module = "用户端-展板信息提交", value = "重新提交我的信息")
    @PutMapping("/user/honor/alumni/{id}")
    public Result<HonorAlumniUpdateVO> putHonorAlumni(
            @PathVariable("id") Long id,
            @RequestBody @Valid UserHonorAlumniUpdateDTO dto) {
        return Result.success(honorSubmitService.update(id, dto));
    }
}