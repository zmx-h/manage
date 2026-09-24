package com.iecmp.duty.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.duty.dto.DutyScheduleAddDTO;
import com.iecmp.duty.dto.DutyScheduleStatusDTO;
import com.iecmp.duty.dto.DutyScheduleUpdateDTO;
import com.iecmp.duty.query.AdminDutySchedulePageQuery;
import com.iecmp.duty.service.DutyPlanService;
import com.iecmp.duty.service.DutyScheduleService;
import com.iecmp.duty.vo.AdminDutySchedulePageVO;
import com.iecmp.duty.vo.DutyScheduleCreateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-值日安排（duty 模块）。
 *
 * <p>覆盖接口：7-14、7-15、7-16、7-17、7-18，共 5 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-值日安排")
@RestController
@RequiredArgsConstructor
public class AdminDutyScheduleController {

    /** DutySchedule 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final DutyScheduleService dutyScheduleService;
    /** DutyPlan 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final DutyPlanService dutyPlanService;

    /**
     * 7-14 新增值日条目。
     *
     * <p>{@code POST /admin/duty/schedule} —— 手工登记一条值日安排，用于补排、临时调整或 `planType = 3 手工指定` 的场景
     *
     * <p>权限：duty:schedule:edit；令牌：是。
     */
    @Operation(summary = "7-14 新增值日条目", description = "手工登记一条值日安排，用于补排、临时调整或 `planType = 3 手工指定` 的场景")
    @RequiresPerm("duty:schedule:edit")
    @OperLog(module = "管理端-值日安排", value = "新增值日条目")
    @PostMapping("/admin/duty/schedule")
    public Result<DutyScheduleCreateVO> postDutySchedule(@RequestBody @Valid DutyScheduleAddDTO dto) {
        // TODO 待实现（接口 7-14）：见《IECMP-接口开发文档-v1.6.md》接口 7-14
        return Result.todo("7-14");
    }

    /**
     * 7-15 修改值日条目。
     *
     * <p>{@code PUT /admin/duty/schedule} —— 修改值日人、日期、内容或备注
     *
     * <p>权限：duty:schedule:edit；令牌：是。
     */
    @Operation(summary = "7-15 修改值日条目", description = "修改值日人、日期、内容或备注")
    @RequiresPerm("duty:schedule:edit")
    @OperLog(module = "管理端-值日安排", value = "修改值日条目")
    @PutMapping("/admin/duty/schedule")
    public Result<Void> putDutySchedule(@RequestBody @Valid DutyScheduleUpdateDTO dto) {
        // TODO 待实现（接口 7-15）：见《IECMP-接口开发文档-v1.6.md》接口 7-15
        return Result.todo("7-15");
    }

    /**
     * 7-16 删除值日条目。
     *
     * <p>{@code DELETE /admin/duty/schedule/{id}} —— 逻辑删除一条值日安排
     *
     * <p>权限：duty:schedule:edit；令牌：是。
     */
    @Operation(summary = "7-16 删除值日条目", description = "逻辑删除一条值日安排")
    @RequiresPerm("duty:schedule:edit")
    @OperLog(module = "管理端-值日安排", value = "删除值日条目")
    @DeleteMapping("/admin/duty/schedule/{id}")
    public Result<Void> deleteDutySchedule(@PathVariable("id") Long id) {
        // TODO 待实现（接口 7-16）：见《IECMP-接口开发文档-v1.6.md》接口 7-16
        return Result.todo("7-16");
    }

    /**
     * 7-17 值日安排分页（后台）。
     *
     * <p>{@code GET /admin/duty/schedule/page} —— 管理端分页查询值日安排，支持更多筛选维度与全局视图
     *
     * <p>权限：duty:schedule:list；令牌：是；分页返回。
     */
    @Operation(summary = "7-17 值日安排分页（后台）", description = "管理端分页查询值日安排，支持更多筛选维度与全局视图")
    @RequiresPerm("duty:schedule:list")
    @GetMapping("/admin/duty/schedule/page")
    public Result<PageResult<AdminDutySchedulePageVO>> getDutySchedulePage(@ParameterObject AdminDutySchedulePageQuery query) {
        // TODO 待实现（接口 7-17）：见《IECMP-接口开发文档-v1.6.md》接口 7-17
        return Result.todo("7-17");
    }

    /**
     * 7-18 值日状态变更。
     *
     * <p>{@code PUT /admin/duty/schedule/{id}/status} —— 管理员变更值日完成状态，用于代确认、登记请假、纠正误操作
     *
     * <p>权限：duty:schedule:edit；令牌：是。
     */
    @Operation(summary = "7-18 值日状态变更", description = "管理员变更值日完成状态，用于代确认、登记请假、纠正误操作")
    @RequiresPerm("duty:schedule:edit")
    @OperLog(module = "管理端-值日安排", value = "值日状态变更")
    @PutMapping("/admin/duty/schedule/{id}/status")
    public Result<Void> putDutyScheduleStatus(@PathVariable("id") Long id, @RequestBody @Valid DutyScheduleStatusDTO dto) {
        // TODO 待实现（接口 7-18）：见《IECMP-接口开发文档-v1.6.md》接口 7-18
        return Result.todo("7-18");
    }
}
