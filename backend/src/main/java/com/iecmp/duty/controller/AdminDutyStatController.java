package com.iecmp.duty.controller;

import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.Result;
import com.iecmp.duty.query.DutyStatOverviewQuery;
import com.iecmp.duty.service.DutyStatService;
import com.iecmp.duty.vo.DutyStatOverviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-值日统计。
 *
 * <p>覆盖接口：7-19。
 */
@Tag(name = "管理端-值日统计")
@RestController
@RequiredArgsConstructor
public class AdminDutyStatController {

    private final DutyStatService dutyStatService;

    @Operation(summary = "7-19 值日统计看板", description = "返回值日执行情况统计，用于管理端查看谁在认真值日")
    @RequiresPerm("duty:stat:list")
    @GetMapping("/admin/duty/stat/overview")
    public Result<DutyStatOverviewVO> getDutyStatOverview(
            @ParameterObject DutyStatOverviewQuery query) {
        return Result.success(dutyStatService.overview(query));
    }
}