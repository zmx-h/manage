package com.iecmp.commonm.controller;

import com.iecmp.common.result.Result;
import com.iecmp.common.util.IpUtil;
import com.iecmp.commonm.service.CommonSystemService;
import com.iecmp.commonm.vo.CaptchaVO;
import com.iecmp.commonm.vo.HealthVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公共-验证码与健康检查（commonm 模块）。
 *
 * <p>覆盖接口：8-13、8-14，共 2 个。
 *
 * @author IECMP 后端组
 */
@Tag(name = "公共-验证码与健康检查")
@RestController
@RequiredArgsConstructor
public class CommonCaptchaController {

    private final CommonSystemService commonSystemService;

    /**
     * 8-13 图形验证码。
     *
     * <p>{@code GET /common/captcha} —— 获取图形验证码，用于登录与注册申请，防机器提交
     *
     * <p>权限：无（公开接口）；令牌：否。
     */
    @Operation(summary = "8-13 图形验证码", description = "获取图形验证码，用于登录与注册申请，防机器提交")
    @GetMapping("/common/captcha")
    public Result<CaptchaVO> getCaptcha(HttpServletRequest request) {
        return Result.success(commonSystemService.generateCaptcha(IpUtil.getIp(request)));
    }

    /**
     * 8-14 健康检查。
     *
     * <p>{@code GET /common/health} —— 供 FRP、运维脚本与前端探活使用，返回服务与依赖状态
     *
     * <p>权限：无（公开接口）；令牌：否。
     */
    @Operation(summary = "8-14 健康检查", description = "供 FRP、运维脚本与前端探活使用，返回服务与依赖状态")
    @GetMapping("/common/health")
    public Result<HealthVO> getHealth() {
        return Result.success(commonSystemService.health());
    }
}