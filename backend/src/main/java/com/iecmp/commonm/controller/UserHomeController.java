package com.iecmp.commonm.controller;

import com.iecmp.common.result.Result;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.commonm.service.CommonDashboardService;
import com.iecmp.commonm.vo.HomeOverviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 用户端-首页看板：8-10。 */
@Tag(name = "用户端-首页看板")
@RestController
@RequiredArgsConstructor
public class UserHomeController {

    private final CommonDashboardService commonDashboardService;

    @Operation(summary = "8-10 前台首页看板", description = "一次请求返回首页各模块摘要")
    @GetMapping("/user/home/overview")
    public Result<HomeOverviewVO> getHomeOverview() {
        return Result.success(commonDashboardService.homeOverview(LoginUserUtil.getUserId()));
    }
}