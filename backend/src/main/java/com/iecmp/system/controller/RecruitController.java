package com.iecmp.system.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.Result;
import com.iecmp.common.util.IpUtil;
import com.iecmp.system.dto.RecruitApplyDTO;
import com.iecmp.system.dto.RecruitRegisterDTO;
import com.iecmp.system.service.RecruitService;
import com.iecmp.system.vo.RecruitApplySubmitVO;
import com.iecmp.system.vo.RegisterVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公共-招新接入（system 模块）。
 *
 * <p>覆盖接口：1-30、1-31，共 2 个。
 */
@Tag(name = "公共-招新接入")
@RestController
@RequiredArgsConstructor
public class RecruitController {

    private final RecruitService recruitService;

    /**
     * 1-30 招新报名提交。
     *
     * <p>{@code POST /common/recruit/apply} —— 外部招新网页提交报名信息，邮箱用于后续接收注册码
     *
     * <p>权限：无（公开接口）；令牌：否。
     */
    @Operation(summary = "1-30 招新报名提交", description = "外部招新网页提交报名信息，邮箱用于后续接收注册码")
    @OperLog(module = "公共-招新接入", value = "招新报名提交")
    @PostMapping("/common/recruit/apply")
    public Result<RecruitApplySubmitVO> postRecruitApply(@RequestBody @Valid RecruitApplyDTO dto,
            HttpServletRequest request) {
        return Result.success(recruitService.apply(dto, IpUtil.getIp(request)));
    }

    /**
     * 1-31 招新注册码注册。
     *
     * <p>{@code POST /common/register/recruit} —— 凭报名邮箱与邮件注册码完成注册，报名信息自动带入账号
     *
     * <p>权限：无（公开接口）；令牌：否。
     */
    @Operation(summary = "1-31 招新注册码注册", description = "凭报名邮箱与邮件注册码完成注册，报名信息自动带入账号")
    @OperLog(module = "公共-招新接入", value = "招新注册码注册")
    @PostMapping("/common/register/recruit")
    public Result<RegisterVO> postRecruitRegister(@RequestBody @Valid RecruitRegisterDTO dto) {
        return Result.success(recruitService.register(dto));
    }
}
