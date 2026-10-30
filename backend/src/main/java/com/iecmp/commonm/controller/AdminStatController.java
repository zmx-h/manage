package com.iecmp.commonm.controller;

import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.Result;
import com.iecmp.commonm.service.CommonDashboardService;
import com.iecmp.commonm.vo.StatDashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 管理端-统计看板：8-11。 */
@Tag(name = "管理端-统计看板")
@RestController
@RequiredArgsConstructor
public class AdminStatController {

    private final CommonDashboardService commonDashboardService;

    @Operation(summary = "8-11 后台统计看板", description = "管理端首页核心统计与待办聚合")
    @RequiresPerm("common:stat:list")
    @GetMapping("/admin/stat/dashboard")
    public Result<StatDashboardVO> getStatDashboard() {
        return Result.success(commonDashboardService.adminDashboard());
    }
}