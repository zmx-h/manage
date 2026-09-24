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

/**
 * 管理端-操作日志（commonm 模块）。
 *
 * <p>覆盖接口：8-12，共 1 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-操作日志")
@RestController
@RequiredArgsConstructor
public class AdminOperLogController {

    /** SysOperLog 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysOperLogService sysOperLogService;

    /**
     * 8-12 操作日志分页。
     *
     * <p>{@code GET /admin/log/page} —— 查询后台写操作日志，用于审计与问题回溯
     *
     * <p>权限：system:log:list；令牌：是；分页返回。
     */
    @Operation(summary = "8-12 操作日志分页", description = "查询后台写操作日志，用于审计与问题回溯")
    @RequiresPerm("system:log:list")
    @GetMapping("/admin/log/page")
    public Result<PageResult<LogPageVO>> getLogPage(@ParameterObject LogPageQuery query) {
        // TODO 待实现（接口 8-12）：见《IECMP-接口开发文档-v1.6.md》接口 8-12
        return Result.todo("8-12");
    }
}
