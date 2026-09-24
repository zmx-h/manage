package com.iecmp.commonm.controller;

import com.iecmp.common.result.Result;
import com.iecmp.commonm.vo.CaptchaVO;
import com.iecmp.commonm.vo.HealthVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公共-验证码与健康检查（commonm 模块）。
 *
 * <p>覆盖接口：8-13、8-14，共 2 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "公共-验证码与健康检查")
@RestController
public class CommonCaptchaController {

    /**
     * 8-13 图形验证码。
     *
     * <p>{@code GET /common/captcha} —— 获取图形验证码，用于登录与注册申请，防机器提交
     *
     * <p>权限：无（公开接口）；令牌：否。
     */
    @Operation(summary = "8-13 图形验证码", description = "获取图形验证码，用于登录与注册申请，防机器提交")
    @GetMapping("/common/captcha")
    public Result<CaptchaVO> getCaptcha() {
        // TODO 待实现（接口 8-13）：见《IECMP-接口开发文档-v1.6.md》接口 8-13
        return Result.todo("8-13");
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
        // TODO 待实现（接口 8-14）：见《IECMP-接口开发文档-v1.6.md》接口 8-14
        return Result.todo("8-14");
    }
}
