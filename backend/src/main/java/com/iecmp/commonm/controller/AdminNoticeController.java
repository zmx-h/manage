package com.iecmp.commonm.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.commonm.dto.NoticeAddDTO;
import com.iecmp.commonm.dto.NoticeUpdateDTO;
import com.iecmp.commonm.query.AdminNoticePageQuery;
import com.iecmp.commonm.service.SysNoticeService;
import com.iecmp.commonm.vo.AdminNoticePageVO;
import com.iecmp.commonm.vo.NoticeCreateVO;
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
 * 管理端-公告管理（commonm 模块）。
 *
 * <p>覆盖接口：8-06、8-07、8-08、8-09，共 4 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-公告管理")
@RestController
@RequiredArgsConstructor
public class AdminNoticeController {

    /** SysNotice 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysNoticeService sysNoticeService;

    /**
     * 8-06 公告分页（后台）。
     *
     * <p>{@code GET /admin/notice/page} —— 后台查询全部公告（含草稿、已下架），用于管理列表
     *
     * <p>权限：common:notice:list；令牌：是；分页返回。
     */
    @Operation(summary = "8-06 公告分页（后台）", description = "后台查询全部公告（含草稿、已下架），用于管理列表")
    @RequiresPerm("common:notice:list")
    @GetMapping("/admin/notice/page")
    public Result<PageResult<AdminNoticePageVO>> getNoticePage(@ParameterObject AdminNoticePageQuery query) {
        // TODO 待实现（接口 8-06）：见《IECMP-接口开发文档-v1.6.md》接口 8-06
        return Result.todo("8-06");
    }

    /**
     * 8-07 公告新增。
     *
     * <p>{@code POST /admin/notice} —— 新增公告，可立即发布或定时发布
     *
     * <p>权限：common:notice:edit；令牌：是。
     */
    @Operation(summary = "8-07 公告新增", description = "新增公告，可立即发布或定时发布")
    @RequiresPerm("common:notice:edit")
    @OperLog(module = "管理端-公告管理", value = "公告新增")
    @PostMapping("/admin/notice")
    public Result<NoticeCreateVO> postNotice(@RequestBody @Valid NoticeAddDTO dto) {
        // TODO 待实现（接口 8-07）：见《IECMP-接口开发文档-v1.6.md》接口 8-07
        return Result.todo("8-07");
    }

    /**
     * 8-08 公告修改。
     *
     * <p>{@code PUT /admin/notice} —— 修改公告信息，字段同新增，另可下架（`status = 2`）
     *
     * <p>权限：common:notice:edit；令牌：是。
     */
    @Operation(summary = "8-08 公告修改", description = "修改公告信息，字段同新增，另可下架（`status = 2`）")
    @RequiresPerm("common:notice:edit")
    @OperLog(module = "管理端-公告管理", value = "公告修改")
    @PutMapping("/admin/notice")
    public Result<Void> putNotice(@RequestBody @Valid NoticeUpdateDTO dto) {
        // TODO 待实现（接口 8-08）：见《IECMP-接口开发文档-v1.6.md》接口 8-08
        return Result.todo("8-08");
    }

    /**
     * 8-09 公告删除。
     *
     * <p>{@code DELETE /admin/notice/{id}} —— 逻辑删除公告
     *
     * <p>权限：common:notice:edit；令牌：是。
     */
    @Operation(summary = "8-09 公告删除", description = "逻辑删除公告")
    @RequiresPerm("common:notice:edit")
    @OperLog(module = "管理端-公告管理", value = "公告删除")
    @DeleteMapping("/admin/notice/{id}")
    public Result<Void> deleteNotice(@PathVariable("id") Long id) {
        // TODO 待实现（接口 8-09）：见《IECMP-接口开发文档-v1.6.md》接口 8-09
        return Result.todo("8-09");
    }
}
