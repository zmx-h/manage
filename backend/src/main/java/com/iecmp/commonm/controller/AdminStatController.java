package com.iecmp.commonm.controller;

import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.Result;
import com.iecmp.commonm.service.SysMessageService;
import com.iecmp.commonm.service.SysNoticeService;
import com.iecmp.commonm.vo.StatDashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-统计看板（commonm 模块）。
 *
 * <p>覆盖接口：8-11，共 1 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-统计看板")
@RestController
@RequiredArgsConstructor
public class AdminStatController {

    /** SysNotice 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysNoticeService sysNoticeService;
    /** SysMessage 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysMessageService sysMessageService;

    /**
     * 8-11 后台统计看板。
     *
     * <p>{@code GET /admin/stat/dashboard} —— 管理端首页看板：核心统计 + 待办聚合，用于管理员快速掌握中心状态
     *
     * <p>权限：common:stat:list；令牌：是。
     */
    @Operation(summary = "8-11 后台统计看板", description = "管理端首页看板：核心统计 + 待办聚合，用于管理员快速掌握中心状态")
    @RequiresPerm("common:stat:list")
    @GetMapping("/admin/stat/dashboard")
    public Result<StatDashboardVO> getStatDashboard() {
        // TODO 待实现（接口 8-11）：见《IECMP-接口开发文档-v1.6.md》接口 8-11
        return Result.todo("8-11");
    }
}
