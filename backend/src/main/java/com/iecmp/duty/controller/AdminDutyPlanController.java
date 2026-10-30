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
 * 管理端-排班计划。
 *
 * <p>覆盖接口：7-08、7-09、7-10、7-11、7-12、7-13，共 6 个。
 */
@Tag(name = "管理端-排班计划")
@RestController
@RequiredArgsConstructor
public class AdminDutyPlanController {

    private final DutyPlanService dutyPlanService;

    @Operation(summary = "7-08 排班计划分页（后台）", description = "管理端分页查询排班计划（含草稿与已结束）")
    @RequiresPerm("duty:plan:list")
    @GetMapping("/admin/duty/plan/page")
    public Result<PageResult<DutyPlanPageVO>> getDutyPlanPage(
            @ParameterObject DutyPlanPageQuery query) {
        return Result.success(dutyPlanService.page(query));
    }

    @Operation(summary = "7-09 新增排班计划", description = "创建排班计划，指定轮转方式、周期与参与成员")
    @RequiresPerm("duty:plan:add")
    @OperLog(module = "管理端-排班计划", value = "新增排班计划")
    @PostMapping("/admin/duty/plan")
    public Result<DutyPlanCreateVO> postDutyPlan(@RequestBody @Valid DutyPlanAddDTO dto) {
        return Result.success(dutyPlanService.add(dto));
    }

    @Operation(summary = "7-10 修改排班计划", description = "修改排班计划的轮转方式、周期、参与成员与值日内容")
    @RequiresPerm("duty:plan:edit")
    @OperLog(module = "管理端-排班计划", value = "修改排班计划")
    @PutMapping("/admin/duty/plan")
    public Result<Void> putDutyPlan(@RequestBody @Valid DutyPlanUpdateDTO dto) {
        dutyPlanService.update(dto);
        return Result.success();
    }

    @Operation(summary = "7-11 删除排班计划", description = "逻辑删除计划，并连带逻辑删除其下的值日条目与换班申请")
    @RequiresPerm("duty:plan:remove")
    @OperLog(module = "管理端-排班计划", value = "删除排班计划")
    @DeleteMapping("/admin/duty/plan/{id}")
    public Result<Void> deleteDutyPlan(@PathVariable("id") Long id) {
        dutyPlanService.delete(id);
        return Result.success();
    }

    @Operation(summary = "7-12 自动生成排班", description = "按计划配置自动生成周期内每日的值日安排")
    @RequiresPerm("duty:plan:generate")
    @OperLog(module = "管理端-排班计划", value = "自动生成排班")
    @PostMapping("/admin/duty/plan/{id}/generate")
    public Result<DutyPlanGenerateVO> postDutyPlanGenerate(
            @PathVariable("id") Long id,
            @RequestBody @Valid DutyPlanGenerateDTO dto) {
        return Result.success(dutyPlanService.generate(id, dto));
    }

    @Operation(summary = "7-13 发布 / 取消发布", description = "控制计划是否对成员端公示；发布后成员才能看到排班")
    @RequiresPerm("duty:plan:publish")
    @OperLog(module = "管理端-排班计划", value = "发布/取消发布")
    @PutMapping("/admin/duty/plan/{id}/publish")
    public Result<Void> putDutyPlanPublish(@PathVariable("id") Long id,
                                            @RequestBody @Valid DutyPlanPublishDTO dto) {
        dutyPlanService.publish(id, dto);
        return Result.success();
    }
}