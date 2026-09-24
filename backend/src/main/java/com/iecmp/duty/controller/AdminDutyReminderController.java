package com.iecmp.duty.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.Result;
import com.iecmp.duty.dto.DutyReminderUpdateDTO;
import com.iecmp.duty.query.DutyReminderQuery;
import com.iecmp.duty.service.DutyReminderConfigService;
import com.iecmp.duty.vo.DutyReminderUpdateVO;
import com.iecmp.duty.vo.DutyReminderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-值日提醒（duty 模块）。
 *
 * <p>覆盖接口：7-20、7-21，共 2 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-值日提醒")
@RestController
@RequiredArgsConstructor
public class AdminDutyReminderController {

    /** DutyReminderConfig 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final DutyReminderConfigService dutyReminderConfigService;

    /**
     * 7-20 提醒配置查询。
     *
     * <p>{@code GET /admin/duty/reminder} —— 查询值日提醒配置；支持全局配置与按计划配置
     *
     * <p>权限：duty:reminder:config；令牌：是。
     */
    @Operation(summary = "7-20 提醒配置查询", description = "查询值日提醒配置；支持全局配置与按计划配置")
    @RequiresPerm("duty:reminder:config")
    @GetMapping("/admin/duty/reminder")
    public Result<DutyReminderVO> getDutyReminder(@ParameterObject DutyReminderQuery query) {
        // TODO 待实现（接口 7-20）：见《IECMP-接口开发文档-v1.6.md》接口 7-20
        return Result.todo("7-20");
    }

    /**
     * 7-21 提醒配置保存。
     *
     * <p>{@code PUT /admin/duty/reminder} —— 保存值日提醒配置；存在则更新，不存在则创建
     *
     * <p>权限：duty:reminder:config；令牌：是。
     */
    @Operation(summary = "7-21 提醒配置保存", description = "保存值日提醒配置；存在则更新，不存在则创建")
    @RequiresPerm("duty:reminder:config")
    @OperLog(module = "管理端-值日提醒", value = "提醒配置保存")
    @PutMapping("/admin/duty/reminder")
    public Result<DutyReminderUpdateVO> putDutyReminder(@RequestBody @Valid DutyReminderUpdateDTO dto) {
        // TODO 待实现（接口 7-21）：见《IECMP-接口开发文档-v1.6.md》接口 7-21
        return Result.todo("7-21");
    }
}
