package com.iecmp.duty.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.duty.dto.DutyPlanAddDTO;
import com.iecmp.duty.dto.DutyPlanGenerateDTO;
import com.iecmp.duty.dto.DutyPlanPublishDTO;
import com.iecmp.duty.dto.DutyPlanUpdateDTO;
import com.iecmp.duty.query.DutyPlanPageQuery;
import com.iecmp.duty.service.DutyPlanService;
import com.iecmp.duty.service.DutyScheduleService;
import com.iecmp.duty.vo.DutyPlanCreateVO;
import com.iecmp.duty.vo.DutyPlanGenerateVO;
import com.iecmp.duty.vo.DutyPlanPageVO;
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
 * 管理端-排班计划（duty 模块）。
 *
 * <p>覆盖接口：7-08、7-09、7-10、7-11、7-12、7-13，共 6 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author liuyujie
 * @date 2026-10-26
 */
@Tag(name = "管理端-排班计划")
@RestController
@RequiredArgsConstructor
public class AdminDutyPlanController {

    /** DutyPlan 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final DutyPlanService dutyPlanService;
    /** DutySchedule 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final DutyScheduleService dutyScheduleService;

    /**
     * 7-08 排班计划分页（后台）。
     *
     * <p>{@code GET /admin/duty/plan/page} —— 管理端分页查询排班计划（含草稿与已结束）
     *
     * <p>权限：duty:plan:list；令牌：是；分页返回。
     */
    @Operation(summary = "7-08 排班计划分页（后台）", description = "管理端分页查询排班计划（含草稿与已结束）")
    @RequiresPerm("duty:plan:list")
    @GetMapping("/admin/duty/plan/page")
    public Result<PageResult<DutyPlanPageVO>> getDutyPlanPage(@ParameterObject DutyPlanPageQuery query) {
        // TODO 待实现（接口 7-08）：见《IECMP-接口开发文档-v1.6.md》接口 7-08
        return Result.success(dutyPlanService.page(query));
    }

    /**
     * 7-09 新增排班计划。
     *
     * <p>{@code POST /admin/duty/plan} —— 创建排班计划，指定轮转方式、周期与参与成员
     *
     * <p>权限：duty:plan:add；令牌：是。
     */
    @Operation(summary = "7-09 新增排班计划", description = "创建排班计划，指定轮转方式、周期与参与成员")
    @RequiresPerm("duty:plan:add")
    @OperLog(module = "管理端-排班计划", value = "新增排班计划")
    @PostMapping("/admin/duty/plan")
    public Result<DutyPlanCreateVO> postDutyPlan(@RequestBody @Valid DutyPlanAddDTO dto) {
        // TODO 待实现（接口 7-09）：见《IECMP-接口开发文档-v1.6.md》接口 7-09
        return Result.success(dutyPlanService.add(dto));
    }

    /**
     * 7-10 修改排班计划。
     *
     * <p>{@code PUT /admin/duty/plan} —— 修改排班计划的轮转方式、周期、参与成员与值日内容
     *
     * <p>权限：duty:plan:edit；令牌：是。
     */
    @Operation(summary = "7-10 修改排班计划", description = "修改排班计划的轮转方式、周期、参与成员与值日内容")
    @RequiresPerm("duty:plan:edit")
    @OperLog(module = "管理端-排班计划", value = "修改排班计划")
    @PutMapping("/admin/duty/plan")
    public Result<Void> putDutyPlan(@RequestBody @Valid DutyPlanUpdateDTO dto) {
        // TODO 待实现（接口 7-10）：见《IECMP-接口开发文档-v1.6.md》接口 7-10
        dutyPlanService.putDutyPlan(dto);
        return Result.success();
    }

    /**
     * 7-11 删除排班计划。
     *
     * <p>{@code DELETE /admin/duty/plan/{id}} —— 逻辑删除计划，并连带逻辑删除其下的值日条目与换班申请
     *
     * <p>权限：duty:plan:remove；令牌：是。
     */
    @Operation(summary = "7-11 删除排班计划", description = "逻辑删除计划，并连带逻辑删除其下的值日条目与换班申请")
    @RequiresPerm("duty:plan:remove")
    @OperLog(module = "管理端-排班计划", value = "删除排班计划")
    @DeleteMapping("/admin/duty/plan/{id}")
    public Result<Void> deleteDutyPlan(@PathVariable("id") Long id) {
        // TODO 待实现（接口 7-11）：见《IECMP-接口开发文档-v1.6.md》接口 7-11
        dutyPlanService.deleteDutyPlan(id);
        return Result.success();
    }

    /**
     * 7-12 自动生成排班。
     *
     * <p>{@code POST /admin/duty/plan/{id}/generate} —— 按计划配置自动生成周期内每日的值日安排
     *
     * <p>权限：duty:plan:generate；令牌：是。
     */
    @Operation(summary = "7-12 自动生成排班", description = "按计划配置自动生成周期内每日的值日安排")
    @RequiresPerm("duty:plan:generate")
    @OperLog(module = "管理端-排班计划", value = "自动生成排班")
    @PostMapping("/admin/duty/plan/{id}/generate")
    public Result<DutyPlanGenerateVO> postDutyPlanGenerate(@PathVariable("id") Long id, @RequestBody @Valid DutyPlanGenerateDTO dto) {
        // TODO 待实现（接口 7-12）：见《IECMP-接口开发文档-v1.6.md》接口 7-12
        return Result.todo("7-12");
    }

    /**
     * 7-13 发布 / 取消发布。
     *
     * <p>{@code PUT /admin/duty/plan/{id}/publish} —— 控制计划是否对成员端公示；发布后成员才能看到排班
     *
     * <p>权限：duty:plan:publish；令牌：是。
     */
    @Operation(summary = "7-13 发布 / 取消发布", description = "控制计划是否对成员端公示；发布后成员才能看到排班")
    @RequiresPerm("duty:plan:publish")
    @OperLog(module = "管理端-排班计划", value = "发布 / 取消发布")
    @PutMapping("/admin/duty/plan/{id}/publish")
    public Result<Void> putDutyPlanPublish(@PathVariable("id") Long id, @RequestBody @Valid DutyPlanPublishDTO dto) {
        // TODO 待实现（接口 7-13）：见《IECMP-接口开发文档-v1.6.md》接口 7-13
        return Result.todo("7-13");
    }
}
