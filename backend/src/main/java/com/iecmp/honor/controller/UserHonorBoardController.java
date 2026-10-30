package com.iecmp.honor.controller;

import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.honor.query.HonorAlumniHighlightQuery;
import com.iecmp.honor.query.UserHonorAlumniPageQuery;
import com.iecmp.honor.service.HonorBoardService;
import com.iecmp.honor.vo.HonorAlumniDetailVO;
import com.iecmp.honor.vo.HonorAlumniHighlightVO;
import com.iecmp.honor.vo.HonorStatVO;
import com.iecmp.honor.vo.UserHonorAlumniPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端-学长学姐展板。
 *
 * <p>覆盖接口：6-01、6-02、6-03、6-04，共 4 个。
 */
@Tag(name = "用户端-学长学姐展板")
@RestController
@RequiredArgsConstructor
public class UserHonorBoardController {

    private final HonorBoardService honorBoardService;

    @Operation(summary = "6-01 展板学长分页", description = "成员端查询展板上的学长学姐信息，用于展板列表页")
    @GetMapping("/user/honor/alumni/page")
    public Result<PageResult<UserHonorAlumniPageVO>> getHonorAlumniPage(
            @ParameterObject UserHonorAlumniPageQuery query) {
        return Result.success(honorBoardService.page(query));
    }

    @Operation(summary = "6-02 学长详情（荣誉时间轴）", description = "查询学长学姐详情与荣誉记录列表，供详情页渲染荣誉时间轴")
    @GetMapping("/user/honor/alumni/{id}")
    public Result<HonorAlumniDetailVO> getHonorAlumni(@PathVariable("id") Long id) {
        return Result.success(honorBoardService.detail(id));
    }

    @Operation(summary = "6-03 精选轮播", description = "返回用于首页轮播的精选学长学姐，取排序号靠前的前若干条")
    @GetMapping("/user/honor/alumni/highlight")
    public Result<List<HonorAlumniHighlightVO>> getHonorAlumniHighlight(
            @ParameterObject HonorAlumniHighlightQuery query) {
        return Result.success(honorBoardService.highlight(query));
    }

    @Operation(summary = "6-04 展板统计", description = "返回展板的人数、荣誉数与年份分布，用于展板页头部统计区")
    @GetMapping("/user/honor/stat")
    public Result<HonorStatVO> getHonorStat() {
        return Result.success(honorBoardService.stat());
    }
}