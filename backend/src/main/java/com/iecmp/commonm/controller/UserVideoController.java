package com.iecmp.commonm.controller;

import com.iecmp.common.result.Result;
import com.iecmp.commonm.query.VideoListQuery;
import com.iecmp.commonm.service.SysVideoService;
import com.iecmp.commonm.vo.VideoListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 用户端-展示视频：8-15。 */
@Tag(name = "用户端-展示视频")
@RestController
@RequiredArgsConstructor
public class UserVideoController {

    private final SysVideoService sysVideoService;

    @Operation(summary = "8-15 前台展示视频列表", description = "按业务归属拉取上架视频")
    @GetMapping("/user/video/list")
    public Result<List<VideoListVO>> getVideoList(@ParameterObject VideoListQuery query) {
        return Result.success(sysVideoService.listPublished(query));
    }
}