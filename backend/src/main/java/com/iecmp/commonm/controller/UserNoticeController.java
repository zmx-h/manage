package com.iecmp.commonm.controller;

import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.commonm.query.UserNoticePageQuery;
import com.iecmp.commonm.service.SysNoticeService;
import com.iecmp.commonm.vo.UserNoticePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端-公告（commonm 模块）。
 *
 * <p>覆盖接口：8-05，共 1 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-公告")
@RestController
@RequiredArgsConstructor
public class UserNoticeController {

    /** SysNotice 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysNoticeService sysNoticeService;

    /**
     * 8-05 公告分页（前台）。
     *
     * <p>{@code GET /user/notice/page} —— 成员端查询已发布公告，置顶优先，用于首页与公告列表页
     *
     * <p>权限：登录即可；令牌：是；分页返回。
     */
    @Operation(summary = "8-05 公告分页（前台）", description = "成员端查询已发布公告，置顶优先，用于首页与公告列表页")
    @GetMapping("/user/notice/page")
    public Result<PageResult<UserNoticePageVO>> getNoticePage(@ParameterObject UserNoticePageQuery query) {
        // TODO 待实现（接口 8-05）：见《IECMP-接口开发文档-v1.6.md》接口 8-05
        return Result.todo("8-05");
    }
}
