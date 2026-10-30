package com.iecmp.commonm.controller;

import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.commonm.query.LogPageQuery;
import com.iecmp.commonm.service.SysOperLogService;
import com.iecmp.commonm.vo.LogPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 管理端-操作日志：8-12。 */
@Tag(name = "管理端-操作日志")
@RestController
@RequiredArgsConstructor
public class AdminOperLogController {

    private final SysOperLogService sysOperLogService;

    @Operation(summary = "8-12 操作日志分页", description = "查询后台写操作日志")
    @RequiresPerm("system:log:list")
    @GetMapping("/admin/log/page")
    public Result<PageResult<LogPageVO>> getLogPage(@ParameterObject LogPageQuery query) {
        return Result.success(sysOperLogService.pageQuery(query));
    }
}