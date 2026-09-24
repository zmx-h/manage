package com.iecmp.honor.controller;

import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.honor.query.HonorAlumniHighlightQuery;
import com.iecmp.honor.query.UserHonorAlumniPageQuery;
import com.iecmp.honor.service.HonorAlumniService;
import com.iecmp.honor.service.HonorRecordService;
import com.iecmp.honor.vo.HonorAlumniDetailVO;
import com.iecmp.honor.vo.HonorAlumniHighlightVO;
import com.iecmp.honor.vo.HonorStatVO;
import com.iecmp.honor.vo.UserHonorAlumniPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端-学长学姐展板（honor 模块）。
 *
 * <p>覆盖接口：6-01、6-02、6-03、6-04，共 4 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-学长学姐展板")
@RestController
@RequiredArgsConstructor
public class UserHonorBoardController {

    /** HonorAlumni 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final HonorAlumniService honorAlumniService;
    /** HonorRecord 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final HonorRecordService honorRecordService;

    /**
     * 6-01 展板学长分页。
     *
     * <p>{@code GET /user/honor/alumni/page} —— 成员端查询展板上的学长学姐信息，用于展板列表页
     *
     * <p>权限：登录即可；令牌：可选；分页返回。
     */
    @Operation(summary = "6-01 展板学长分页", description = "成员端查询展板上的学长学姐信息，用于展板列表页")
    @GetMapping("/user/honor/alumni/page")
    public Result<PageResult<UserHonorAlumniPageVO>> getHonorAlumniPage(@ParameterObject UserHonorAlumniPageQuery query) {
        // TODO 待实现（接口 6-01）：见《IECMP-接口开发文档-v1.6.md》接口 6-01
        return Result.todo("6-01");
    }

    /**
     * 6-02 学长详情（荣誉时间轴）。
     *
     * <p>{@code GET /user/honor/alumni/{id}} —— 查询学长学姐详情与荣誉记录列表，供详情页渲染荣誉时间轴
     *
     * <p>权限：登录即可；令牌：可选。
     */
    @Operation(summary = "6-02 学长详情（荣誉时间轴）", description = "查询学长学姐详情与荣誉记录列表，供详情页渲染荣誉时间轴")
    @GetMapping("/user/honor/alumni/{id}")
    public Result<HonorAlumniDetailVO> getHonorAlumni(@PathVariable("id") Long id) {
        // TODO 待实现（接口 6-02）：见《IECMP-接口开发文档-v1.6.md》接口 6-02
        return Result.todo("6-02");
    }

    /**
     * 6-03 精选轮播。
     *
     * <p>{@code GET /user/honor/alumni/highlight} —— 返回用于首页轮播的精选学长学姐，取排序号靠前的前若干条
     *
     * <p>权限：登录即可；令牌：可选。
     */
    @Operation(summary = "6-03 精选轮播", description = "返回用于首页轮播的精选学长学姐，取排序号靠前的前若干条")
    @GetMapping("/user/honor/alumni/highlight")
    public Result<HonorAlumniHighlightVO> getHonorAlumniHighlight(@ParameterObject HonorAlumniHighlightQuery query) {
        // TODO 待实现（接口 6-03）：见《IECMP-接口开发文档-v1.6.md》接口 6-03
        return Result.todo("6-03");
    }

    /**
     * 6-04 展板统计。
     *
     * <p>{@code GET /user/honor/stat} —— 返回展板的人数、荣誉数与年份分布，用于展板页头部统计区
     *
     * <p>权限：登录即可；令牌：可选。
     */
    @Operation(summary = "6-04 展板统计", description = "返回展板的人数、荣誉数与年份分布，用于展板页头部统计区")
    @GetMapping("/user/honor/stat")
    public Result<HonorStatVO> getHonorStat() {
        // TODO 待实现（接口 6-04）：见《IECMP-接口开发文档-v1.6.md》接口 6-04
        return Result.todo("6-04");
    }
}
