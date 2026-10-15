package com.iecmp.honor.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.honor.dto.AdminHonorAlumniAddDTO;
import com.iecmp.honor.dto.AdminHonorAlumniUpdateDTO;
import com.iecmp.honor.dto.HonorAlumniAuditDTO;
import com.iecmp.honor.dto.HonorAlumniSortDTO;
import com.iecmp.honor.dto.HonorAlumniStatusDTO;
import com.iecmp.honor.query.AdminHonorAlumniPageQuery;
import com.iecmp.honor.query.HonorAlumniAuditPageQuery;
import com.iecmp.honor.service.HonorAlumniService;
import com.iecmp.honor.service.HonorRecordService;
import com.iecmp.honor.vo.AdminHonorAlumniCreateVO;
import com.iecmp.honor.vo.AdminHonorAlumniPageVO;
import com.iecmp.honor.vo.HonorAlumniAuditPageVO;
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
 * 管理端-学长学姐管理（honor 模块）。
 *
 * <p>覆盖接口：6-05、6-06、6-07、6-08、6-09、6-10、6-17、6-18，共 8 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-学长学姐管理")
@RestController
@RequiredArgsConstructor
public class AdminHonorAlumniController {

    /** HonorAlumni 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final HonorAlumniService honorAlumniService;
    /** HonorRecord 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final HonorRecordService honorRecordService;

    /**
     * 6-05 学长分页（后台）。
     *
     * <p>{@code GET /admin/honor/alumni/page} —— 管理端分页查询全部学长学姐信息（含待审核、已下板），用于管理列表
     *
     * <p>权限：honor:alumni:list；令牌：是；分页返回。
     */
    @Operation(summary = "6-05 学长分页（后台）", description = "管理端分页查询全部学长学姐信息（含待审核、已下板），用于管理列表")
    @RequiresPerm("honor:alumni:list")
    @GetMapping("/admin/honor/alumni/page")
    public Result<PageResult<AdminHonorAlumniPageVO>> getHonorAlumniPage(@ParameterObject AdminHonorAlumniPageQuery query) {
        // TODO 待实现（接口 6-05）：见《IECMP-接口开发文档-v1.6.md》接口 6-05
        return Result.todo("6-05");
    }

    /**
     * 6-06 新增学长。
     *
     * <p>{@code POST /admin/honor/alumni} —— 管理员直接录入学长学姐信息，录入后状态为「已通过」，可立即上板
     *
     * <p>权限：honor:alumni:add；令牌：是。
     */
    @Operation(summary = "6-06 新增学长", description = "管理员直接录入学长学姐信息，录入后状态为「已通过」，可立即上板")
    @RequiresPerm("honor:alumni:add")
    @OperLog(module = "管理端-学长学姐管理", value = "新增学长")
    @PostMapping("/admin/honor/alumni")
    public Result<AdminHonorAlumniCreateVO> postHonorAlumni(@RequestBody @Valid AdminHonorAlumniAddDTO dto) {
        // TODO 待实现（接口 6-06）：见《IECMP-接口开发文档-v1.6.md》接口 6-06
        return Result.todo("6-06");
    }

    /**
     * 6-07 修改学长。
     *
     * <p>{@code PUT /admin/honor/alumni} —— 修改学长学姐信息，支持调整展示排序与上板状态
     *
     * <p>权限：honor:alumni:edit；令牌：是。
     */
    @Operation(summary = "6-07 修改学长", description = "修改学长学姐信息，支持调整展示排序与上板状态")
    @RequiresPerm("honor:alumni:edit")
    @OperLog(module = "管理端-学长学姐管理", value = "修改学长")
    @PutMapping("/admin/honor/alumni")
    public Result<Void> putHonorAlumni(@RequestBody @Valid AdminHonorAlumniUpdateDTO dto) {
        // TODO 待实现（接口 6-07）：见《IECMP-接口开发文档-v1.6.md》接口 6-07
        return Result.todo("6-07");
    }

    /**
     * 6-08 删除学长。
     *
     * <p>{@code DELETE /admin/honor/alumni/{id}} —— 逻辑删除学长学姐信息，同时逻辑删除其下全部荣誉记录
     *
     * <p>权限：honor:alumni:remove；令牌：是。
     */
    @Operation(summary = "6-08 删除学长", description = "逻辑删除学长学姐信息，同时逻辑删除其下全部荣誉记录")
    @RequiresPerm("honor:alumni:remove")
    @OperLog(module = "管理端-学长学姐管理", value = "删除学长")
    @DeleteMapping("/admin/honor/alumni/{id}")
    public Result<Void> deleteHonorAlumni(@PathVariable("id") Long id) {
        // TODO 待实现（接口 6-08）：见《IECMP-接口开发文档-v1.6.md》接口 6-08
        return Result.todo("6-08");
    }

    /**
     * 6-09 上板 / 下板。
     *
     * <p>{@code PUT /admin/honor/alumni/{id}/status} —— 控制某条信息是否在前台展板展示
     *
     * <p>权限：honor:alumni:edit；令牌：是。
     */
    @Operation(summary = "6-09 上板 / 下板", description = "控制某条信息是否在前台展板展示")
    @RequiresPerm("honor:alumni:edit")
    @OperLog(module = "管理端-学长学姐管理", value = "上板 / 下板")
    @PutMapping("/admin/honor/alumni/{id}/status")
    public Result<Void> putHonorAlumniStatus(@PathVariable("id") Long id, @RequestBody @Valid HonorAlumniStatusDTO dto) {
        // TODO 待实现（接口 6-09）：见《IECMP-接口开发文档-v1.6.md》接口 6-09
        return Result.todo("6-09");
    }

    /**
     * 6-10 调整展示排序。
     *
     * <p>{@code PUT /admin/honor/alumni/{id}/sort} —— 调整某条信息的展示排序号，数值越小越靠前
     *
     * <p>权限：honor:alumni:edit；令牌：是。
     */
    @Operation(summary = "6-10 调整展示排序", description = "调整某条信息的展示排序号，数值越小越靠前")
    @RequiresPerm("honor:alumni:edit")
    @OperLog(module = "管理端-学长学姐管理", value = "调整展示排序")
    @PutMapping("/admin/honor/alumni/{id}/sort")
    public Result<Void> putHonorAlumniSort(@PathVariable("id") Long id, @RequestBody @Valid HonorAlumniSortDTO dto) {
        // TODO 待实现（接口 6-10）：见《IECMP-接口开发文档-v1.6.md》接口 6-10
        return Result.todo("6-10");
    }

    /**
     * 6-17 待审核信息分页（后台）。
     *
     * <p>{@code GET /admin/honor/alumni/audit/page} —— 分页查询成员提交的待审核信息，用于审核排队
     *
     * <p>权限：honor:alumni:audit；令牌：是；分页返回。
     */
    @Operation(summary = "6-17 待审核信息分页（后台）", description = "分页查询成员提交的待审核信息，用于审核排队")
    @RequiresPerm("honor:alumni:audit")
    @GetMapping("/admin/honor/alumni/audit/page")
    public Result<PageResult<HonorAlumniAuditPageVO>> getHonorAlumniAuditPage(@ParameterObject HonorAlumniAuditPageQuery query) {
        // TODO 待实现（接口 6-17）：见《IECMP-接口开发文档-v1.6.md》接口 6-17
        return Result.todo("6-17");
    }

    /**
     * 6-18 审核通过 / 驳回。
     *
     * <p>{@code PUT /admin/honor/alumni/{id}/audit} —— 审核成员提交的学长学姐信息；通过后可选择直接上板
     *
     * <p>权限：honor:alumni:audit；令牌：是。
     */
    @Operation(summary = "6-18 审核通过 / 驳回", description = "审核成员提交的学长学姐信息；通过后可选择直接上板")
    @RequiresPerm("honor:alumni:audit")
    @OperLog(module = "管理端-学长学姐管理", value = "审核通过 / 驳回")
    @PutMapping("/admin/honor/alumni/{id}/audit")
    public Result<Void> putHonorAlumniAudit(@PathVariable("id") Long id, @RequestBody @Valid HonorAlumniAuditDTO dto) {
        // TODO 待实现（接口 6-18）：见《IECMP-接口开发文档-v1.6.md》接口 6-18
        return Result.todo("6-18");
    }
}
