package com.iecmp.system.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.Result;
import com.iecmp.common.util.IpUtil;
import com.iecmp.system.dto.RegisterApplyDTO;
import com.iecmp.system.dto.RegisterDTO;
import com.iecmp.system.service.RegisterService;
import com.iecmp.system.vo.RegisterApplyVO;
import com.iecmp.system.vo.RegisterConfigVO;
import com.iecmp.system.vo.RegisterVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公共-成员接入（system 模块）。
 *
 * <p>覆盖接口：1-22、1-23、1-24，共 3 个。
 *
 * @author IECMP 后端组
 */
@Tag(name = "公共-成员接入")
@RestController
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    /**
     * 1-22 注册方式配置查询。
     *
     * <p>{@code GET /common/register/config} —— 返回当前开放的注册通道与提示文案，供注册页决定展示「邀请码注册」还是「申请入驻」表单
     *
     * <p>权限：无（公开接口）；令牌：否。
     */
    @Operation(summary = "1-22 注册方式配置查询", description = "返回当前开放的注册通道与提示文案，供注册页决定展示「邀请码注册」还是「申请入驻」表单")
    @GetMapping("/common/register/config")
    public Result<RegisterConfigVO> getRegisterConfig() {
        return Result.success(registerService.getConfig());
    }

    /**
     * 1-23 邀请码注册。
     *
     * <p>{@code POST /user/register} —— 成员凭邀请码自助注册，注册成功即启用并返回登录账号
     *
     * <p>权限：无（公开接口）；令牌：否。
     */
    @Operation(summary = "1-23 邀请码注册", description = "成员凭邀请码自助注册，注册成功即启用并返回登录账号")
    @OperLog(module = "公共-成员接入", value = "邀请码注册")
    @PostMapping("/user/register")
    public Result<RegisterVO> postRegister(@RequestBody @Valid RegisterDTO dto, HttpServletRequest request) {
        return Result.success(registerService.register(dto, IpUtil.getIp(request)));
    }

    /**
     * 1-24 提交注册申请。
     *
     * <p>{@code POST /common/register/apply} —— 无邀请码时提交入驻申请，由管理员审核；同一学号重复提交将覆盖旧申请
     *
     * <p>权限：无（公开接口）；令牌：否。
     */
    @Operation(summary = "1-24 提交注册申请", description = "无邀请码时提交入驻申请，由管理员审核；同一学号重复提交将覆盖旧申请")
    @OperLog(module = "公共-成员接入", value = "提交注册申请")
    @PostMapping("/common/register/apply")
    public Result<RegisterApplyVO> postRegisterApply(@RequestBody @Valid RegisterApplyDTO dto,
            HttpServletRequest request) {
        return Result.success(registerService.apply(dto, IpUtil.getIp(request)));
    }
}