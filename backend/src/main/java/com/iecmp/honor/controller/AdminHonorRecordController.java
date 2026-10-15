package com.iecmp.honor.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.Result;
import com.iecmp.honor.dto.HonorRecordAddDTO;
import com.iecmp.honor.dto.HonorRecordUpdateDTO;
import com.iecmp.honor.service.HonorAlumniService;
import com.iecmp.honor.service.HonorRecordService;
import com.iecmp.honor.vo.HonorRecordCreateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-荣誉记录（honor 模块）。
 *
 * <p>覆盖接口：6-11、6-12、6-13，共 3 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-荣誉记录")
@RestController
@RequiredArgsConstructor
public class AdminHonorRecordController {

    /** HonorRecord 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final HonorRecordService honorRecordService;
    /** HonorAlumni 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final HonorAlumniService honorAlumniService;

    /**
     * 6-11 新增荣誉记录。
     *
     * <p>{@code POST /admin/honor/record} —— 为指定学长学姐新增一条荣誉记录
     *
     * <p>权限：honor:record:edit；令牌：是。
     */
    @Operation(summary = "6-11 新增荣誉记录", description = "为指定学长学姐新增一条荣誉记录")
    @RequiresPerm("honor:record:edit")
    @OperLog(module = "管理端-荣誉记录", value = "新增荣誉记录")
    @PostMapping("/admin/honor/record")
    public Result<HonorRecordCreateVO> postHonorRecord(@RequestBody @Valid HonorRecordAddDTO dto) {
        // TODO 待实现（接口 6-11）：见《IECMP-接口开发文档-v1.6.md》接口 6-11
        return Result.todo("6-11");
    }

    /**
     * 6-12 修改荣誉记录。
     *
     * <p>{@code PUT /admin/honor/record} —— 修改荣誉记录的名称、级别、获奖日期、说明与证书
     *
     * <p>权限：honor:record:edit；令牌：是。
     */
    @Operation(summary = "6-12 修改荣誉记录", description = "修改荣誉记录的名称、级别、获奖日期、说明与证书")
    @RequiresPerm("honor:record:edit")
    @OperLog(module = "管理端-荣誉记录", value = "修改荣誉记录")
    @PutMapping("/admin/honor/record")
    public Result<Void> putHonorRecord(@RequestBody @Valid HonorRecordUpdateDTO dto) {
        // TODO 待实现（接口 6-12）：见《IECMP-接口开发文档-v1.6.md》接口 6-12
        return Result.todo("6-12");
    }

    /**
     * 6-13 删除荣誉记录。
     *
     * <p>{@code DELETE /admin/honor/record/{id}} —— 逻辑删除单条荣誉记录
     *
     * <p>权限：honor:record:edit；令牌：是。
     */
    @Operation(summary = "6-13 删除荣誉记录", description = "逻辑删除单条荣誉记录")
    @RequiresPerm("honor:record:edit")
    @OperLog(module = "管理端-荣誉记录", value = "删除荣誉记录")
    @DeleteMapping("/admin/honor/record/{id}")
    public Result<Void> deleteHonorRecord(@PathVariable("id") Long id) {
        // TODO 待实现（接口 6-13）：见《IECMP-接口开发文档-v1.6.md》接口 6-13
        return Result.todo("6-13");
    }
}
