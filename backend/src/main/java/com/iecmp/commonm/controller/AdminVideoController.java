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

/**
 * 管理端-视频资源（commonm 模块）。
 *
 * <p>覆盖接口：8-16、8-17、8-18、8-19，共 4 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-视频资源")
@RestController
@RequiredArgsConstructor
public class AdminVideoController {

    /** SysVideo 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysVideoService sysVideoService;

    /**
     * 8-16 视频分页（后台）。
     *
     * <p>{@code GET /admin/video/page} —— 后台分页查询视频记录，含下架数据，便于维护播放链接
     *
     * <p>权限：common:video:list；令牌：是；分页返回。
     */
    @Operation(summary = "8-16 视频分页（后台）", description = "后台分页查询视频记录，含下架数据，便于维护播放链接")
    @RequiresPerm("common:video:list")
    @GetMapping("/admin/video/page")
    public Result<PageResult<VideoPageVO>> getVideoPage(@ParameterObject VideoPageQuery query) {
        // TODO 待实现（接口 8-16）：见《IECMP-接口开发文档-v1.6.md》接口 8-16
        return Result.todo("8-16");
    }

    /**
     * 8-17 新增视频。
     *
     * <p>{@code POST /admin/video} —— 登记一条视频：管理员在 Jellyfin 取得播放链接后填入平台
     *
     * <p>权限：common:video:edit；令牌：是。
     */
    @Operation(summary = "8-17 新增视频", description = "登记一条视频：管理员在 Jellyfin 取得播放链接后填入平台")
    @RequiresPerm("common:video:edit")
    @OperLog(module = "管理端-视频资源", value = "新增视频")
    @PostMapping("/admin/video")
    public Result<VideoCreateVO> postVideo(@RequestBody @Valid VideoAddDTO dto) {
        // TODO 待实现（接口 8-17）：见《IECMP-接口开发文档-v1.6.md》接口 8-17
        return Result.todo("8-17");
    }

    /**
     * 8-18 视频修改。
     *
     * <p>{@code PUT /admin/video} —— 修改视频信息，常用于更换失效的播放链接、上下架与调整排序
     *
     * <p>权限：common:video:edit；令牌：是。
     */
    @Operation(summary = "8-18 视频修改", description = "修改视频信息，常用于更换失效的播放链接、上下架与调整排序")
    @RequiresPerm("common:video:edit")
    @OperLog(module = "管理端-视频资源", value = "视频修改")
    @PutMapping("/admin/video")
    public Result<Void> putVideo(@RequestBody @Valid VideoUpdateDTO dto) {
        // TODO 待实现（接口 8-18）：见《IECMP-接口开发文档-v1.6.md》接口 8-18
        return Result.todo("8-18");
    }

    /**
     * 8-19 视频删除。
     *
     * <p>{@code DELETE /admin/video/{id}} —— 删除视频记录；仅删除平台登记信息，不删除 Jellyfin 中的媒体文件
     *
     * <p>权限：common:video:edit；令牌：是。
     */
    @Operation(summary = "8-19 视频删除", description = "删除视频记录；仅删除平台登记信息，不删除 Jellyfin 中的媒体文件")
    @RequiresPerm("common:video:edit")
    @OperLog(module = "管理端-视频资源", value = "视频删除")
    @DeleteMapping("/admin/video/{id}")
    public Result<Void> deleteVideo(@PathVariable("id") Long id) {
        // TODO 待实现（接口 8-19）：见《IECMP-接口开发文档-v1.6.md》接口 8-19
        return Result.todo("8-19");
    }
}
