package com.iecmp.system.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.Result;
import com.iecmp.system.dto.RegisterApplyDTO;
import com.iecmp.system.dto.RegisterDTO;
import com.iecmp.system.service.SysInviteCodeService;
import com.iecmp.system.service.SysRegisterApplyService;
import com.iecmp.system.service.SysUserRoleService;
import com.iecmp.system.service.SysUserService;
import com.iecmp.system.vo.RegisterApplyVO;
import com.iecmp.system.vo.RegisterConfigVO;
import com.iecmp.system.vo.RegisterVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "公共-成员接入")
@RestController
@RequiredArgsConstructor
public class RegisterController {

    /** SysInviteCode 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysInviteCodeService sysInviteCodeService;
    /** SysRegisterApply 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysRegisterApplyService sysRegisterApplyService;
    /** SysUser 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysUserService sysUserService;
    /** SysUserRole 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysUserRoleService sysUserRoleService;

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
        // TODO 待实现（接口 1-22）：见《IECMP-接口开发文档-v1.6.md》接口 1-22
        return Result.todo("1-22");
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
    public Result<RegisterVO> postRegister(@RequestBody @Valid RegisterDTO dto) {
        // TODO 待实现（接口 1-23）：见《IECMP-接口开发文档-v1.6.md》接口 1-23
        return Result.todo("1-23");
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
    public Result<RegisterApplyVO> postRegisterApply(@RequestBody @Valid RegisterApplyDTO dto) {
        // TODO 待实现（接口 1-24）：见《IECMP-接口开发文档-v1.6.md》接口 1-24
        return Result.todo("1-24");
    }
}
