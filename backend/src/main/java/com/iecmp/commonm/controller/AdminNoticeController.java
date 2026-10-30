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

/** 管理端-公告管理：8-06 ~ 8-09。 */
@Tag(name = "管理端-公告管理")
@RestController
@RequiredArgsConstructor
public class AdminNoticeController {

    private final SysNoticeService sysNoticeService;

    @Operation(summary = "8-06 公告分页（后台）", description = "查询全部公告，含草稿与已下架")
    @RequiresPerm("common:notice:list")
    @GetMapping("/admin/notice/page")
    public Result<PageResult<AdminNoticePageVO>> getNoticePage(@ParameterObject AdminNoticePageQuery query) {
        return Result.success(sysNoticeService.adminPage(query));
    }

    @Operation(summary = "8-07 公告新增", description = "新增公告，可立即发布或定时发布")
    @RequiresPerm("common:notice:edit")
    @OperLog(module = "管理端-公告管理", value = "公告新增")
    @PostMapping("/admin/notice")
    public Result<NoticeCreateVO> postNotice(@RequestBody @Valid NoticeAddDTO dto) {
        return Result.success(sysNoticeService.create(dto));
    }

    @Operation(summary = "8-08 公告修改", description = "修改公告信息，status=2 表示下架")
    @RequiresPerm("common:notice:edit")
    @OperLog(module = "管理端-公告管理", value = "公告修改")
    @PutMapping("/admin/notice")
    public Result<Void> putNotice(@RequestBody @Valid NoticeUpdateDTO dto) {
        sysNoticeService.update(dto);
        return Result.success();
    }

    @Operation(summary = "8-09 公告删除", description = "逻辑删除公告")
    @RequiresPerm("common:notice:edit")
    @OperLog(module = "管理端-公告管理", value = "公告删除")
    @DeleteMapping("/admin/notice/{id}")
    public Result<Void> deleteNotice(@PathVariable("id") Long id) {
        sysNoticeService.delete(id);
        return Result.success();
    }
}