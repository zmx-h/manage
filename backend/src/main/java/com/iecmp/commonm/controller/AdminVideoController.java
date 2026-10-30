package com.iecmp.commonm.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.commonm.dto.VideoAddDTO;
import com.iecmp.commonm.dto.VideoUpdateDTO;
import com.iecmp.commonm.query.VideoPageQuery;
import com.iecmp.commonm.service.SysVideoService;
import com.iecmp.commonm.vo.VideoCreateVO;
import com.iecmp.commonm.vo.VideoPageVO;
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

/** 管理端-视频资源：8-16 ~ 8-19。 */
@Tag(name = "管理端-视频资源")
@RestController
@RequiredArgsConstructor
public class AdminVideoController {

    private final SysVideoService sysVideoService;

    @Operation(summary = "8-16 视频分页（后台）", description = "后台分页查询视频记录，含下架数据")
    @RequiresPerm("common:video:list")
    @GetMapping("/admin/video/page")
    public Result<PageResult<VideoPageVO>> getVideoPage(@ParameterObject VideoPageQuery query) {
        return Result.success(sysVideoService.pageAdmin(query));
    }

    @Operation(summary = "8-17 新增视频", description = "登记一条 Jellyfin 播放链接")
    @RequiresPerm("common:video:edit")
    @OperLog(module = "管理端-视频资源", value = "新增视频")
    @PostMapping("/admin/video")
    public Result<VideoCreateVO> postVideo(@RequestBody @Valid VideoAddDTO dto) {
        return Result.success(sysVideoService.create(dto));
    }

    @Operation(summary = "8-18 视频修改", description = "修改视频信息、播放链接与上下架状态")
    @RequiresPerm("common:video:edit")
    @OperLog(module = "管理端-视频资源", value = "视频修改")
    @PutMapping("/admin/video")
    public Result<Void> putVideo(@RequestBody @Valid VideoUpdateDTO dto) {
        sysVideoService.update(dto);
        return Result.success();
    }

    @Operation(summary = "8-19 视频删除", description = "仅删除平台登记信息")
    @RequiresPerm("common:video:edit")
    @OperLog(module = "管理端-视频资源", value = "视频删除")
    @DeleteMapping("/admin/video/{id}")
    public Result<Void> deleteVideo(@PathVariable("id") Long id) {
        sysVideoService.delete(id);
        return Result.success();
    }
}