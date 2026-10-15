package com.iecmp.commonm.controller;

import com.iecmp.common.result.Result;
import com.iecmp.commonm.query.VideoListQuery;
import com.iecmp.commonm.service.SysVideoService;
import com.iecmp.commonm.vo.VideoListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端-展示视频（commonm 模块）。
 *
 * <p>覆盖接口：8-15，共 1 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-展示视频")
@RestController
@RequiredArgsConstructor
public class UserVideoController {

    /** SysVideo 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysVideoService sysVideoService;

    /**
     * 8-15 前台展示视频列表。
     *
     * <p>{@code GET /user/video/list} —— 按业务归属拉取上架视频，用于首页与学长学姐展板的视频展示区
     *
     * <p>权限：登录即可；令牌：可选。
     */
    @Operation(summary = "8-15 前台展示视频列表", description = "按业务归属拉取上架视频，用于首页与学长学姐展板的视频展示区")
    @GetMapping("/user/video/list")
    public Result<VideoListVO> getVideoList(@ParameterObject VideoListQuery query) {
        // TODO 待实现（接口 8-15）：见《IECMP-接口开发文档-v1.6.md》接口 8-15
        return Result.todo("8-15");
    }
}
