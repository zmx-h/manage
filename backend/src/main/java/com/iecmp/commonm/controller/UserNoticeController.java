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

/** 用户端-公告：8-05。 */
@Tag(name = "用户端-公告")
@RestController
@RequiredArgsConstructor
public class UserNoticeController {

    private final SysNoticeService sysNoticeService;

    @Operation(summary = "8-05 公告分页（前台）", description = "仅返回已发布且已到发布时间的公告")
    @GetMapping("/user/notice/page")
    public Result<PageResult<UserNoticePageVO>> getNoticePage(@ParameterObject UserNoticePageQuery query) {
        return Result.success(sysNoticeService.userPage(query));
    }
}