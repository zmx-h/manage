package com.iecmp.system.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.system.dto.RecruitAssessmentDTO;
import com.iecmp.system.query.RecruitApplyPageQuery;
import com.iecmp.system.service.RecruitService;
import com.iecmp.system.vo.RecruitApplyPageVO;
import com.iecmp.system.vo.RecruitApplyVO;
import com.iecmp.system.vo.RecruitAssessmentVO;
import com.iecmp.system.vo.RecruitCodeVO;
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
 * 管理端-招新报名与考核（system 模块）。
 *
 * <p>覆盖接口：1-32 ~ 1-35，共 4 个。
 */
@Tag(name = "管理端-招新报名与考核")
@RestController
@RequiredArgsConstructor
public class AdminRecruitController {

    private final RecruitService recruitService;

    /**
     * 1-32 招新报名分页。
     *
     * <p>{@code GET /admin/recruit/apply/page} —— 分页查询报名记录，支持状态、关键字与报名时间筛选
     *
     * <p>权限：system:recruit:list；令牌：是；分页返回。
     */
    @Operation(summary = "1-32 招新报名分页", description = "分页查询报名记录，支持状态、关键字与报名时间筛选")
    @RequiresPerm("system:recruit:list")
    @GetMapping("/admin/recruit/apply/page")
    public Result<PageResult<RecruitApplyPageVO>> getRecruitApplyPage(
            @ParameterObject RecruitApplyPageQuery query) {
        return Result.success(recruitService.page(query));
    }

    /**
     * 1-33 招新报名详情。
     *
     * <p>{@code GET /admin/recruit/apply/{id}} —— 查看报名信息、成绩与注册码状态（不返回注册码明文）
     *
     * <p>权限：system:recruit:list；令牌：是。
     */
    @Operation(summary = "1-33 招新报名详情", description = "查看报名信息、成绩与注册码状态（不返回注册码明文）")
    @RequiresPerm("system:recruit:list")
    @GetMapping("/admin/recruit/apply/{id}")
    public Result<RecruitApplyVO> getRecruitApply(@PathVariable("id") Long id) {
        return Result.success(recruitService.detail(id));
    }

    /**
     * 1-34 招新考核成绩录入。
     *
     * <p>{@code PUT /admin/recruit/apply/{id}/assessment} —— 录入一轮考核成绩；达到通过分时自动生成并发送注册码
     *
     * <p>权限：system:recruit:assess；令牌：是。
     */
    @Operation(summary = "1-34 招新考核成绩录入", description = "录入一轮考核成绩；达到通过分时自动生成并发送注册码")
    @RequiresPerm("system:recruit:assess")
    @OperLog(module = "管理端-招新报名与考核", value = "招新考核成绩录入")
    @PutMapping("/admin/recruit/apply/{id}/assessment")
    public Result<RecruitAssessmentVO> putRecruitAssessment(@PathVariable("id") Long id,
            @RequestBody @Valid RecruitAssessmentDTO dto) {
        return Result.success(recruitService.assess(id, dto));
    }

    /**
     * 1-35 招新注册码补发。
     *
     * <p>{@code POST /admin/recruit/apply/{id}/send-code} —— 重新生成并发送注册码；邮件发送失败时返回明文注册码供线下告知
     *
     * <p>权限：system:recruit:send；令牌：是。
     */
    @Operation(summary = "1-35 招新注册码补发", description = "重新生成并发送注册码；邮件发送失败时返回明文注册码供线下告知")
    @RequiresPerm("system:recruit:send")
    @OperLog(module = "管理端-招新报名与考核", value = "招新注册码补发")
    @PostMapping("/admin/recruit/apply/{id}/send-code")
    public Result<RecruitCodeVO> postSendCode(@PathVariable("id") Long id) {
        return Result.success(recruitService.resendCode(id));
    }
}
