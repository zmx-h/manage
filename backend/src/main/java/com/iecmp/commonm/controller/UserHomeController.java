package com.iecmp.commonm.controller;

import com.iecmp.common.result.Result;
import com.iecmp.commonm.service.SysMessageService;
import com.iecmp.commonm.service.SysNoticeService;
import com.iecmp.commonm.vo.HomeOverviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端-首页看板（commonm 模块）。
 *
 * <p>覆盖接口：8-10，共 1 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-首页看板")
@RestController
@RequiredArgsConstructor
public class UserHomeController {

    /** SysNotice 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysNoticeService sysNoticeService;
    /** SysMessage 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysMessageService sysMessageService;

    /**
     * 8-10 前台首页看板。
     *
     * <p>{@code GET /user/home/overview} —— 成员端首页聚合数据，一次请求返回首页需要的各模块摘要，避免前端多次调用
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "8-10 前台首页看板", description = "成员端首页聚合数据，一次请求返回首页需要的各模块摘要，避免前端多次调用")
    @GetMapping("/user/home/overview")
    public Result<HomeOverviewVO> getHomeOverview() {
        // TODO 待实现（接口 8-10）：见《IECMP-接口开发文档-v1.6.md》接口 8-10
        return Result.todo("8-10");
    }
}
