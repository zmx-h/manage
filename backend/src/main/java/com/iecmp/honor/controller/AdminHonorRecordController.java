package com.iecmp.honor.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.Result;
import com.iecmp.honor.dto.HonorRecordAddDTO;
import com.iecmp.honor.dto.HonorRecordUpdateDTO;
import com.iecmp.honor.service.HonorRecordAdminService;
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
 * 管理端-荣誉记录。
 *
 * <p>覆盖接口：6-11、6-12、6-13，共 3 个。
 */
@Tag(name = "管理端-荣誉记录")
@RestController
@RequiredArgsConstructor
public class AdminHonorRecordController {

    private final HonorRecordAdminService honorRecordAdminService;

    @Operation(summary = "6-11 新增荣誉记录", description = "为指定学长学姐新增一条荣誉记录")
    @RequiresPerm("honor:record:edit")
    @OperLog(module = "管理端-荣誉记录", value = "新增荣誉记录")
    @PostMapping("/admin/honor/record")
    public Result<HonorRecordCreateVO> postHonorRecord(
            @RequestBody @Valid HonorRecordAddDTO dto) {
        return Result.success(honorRecordAdminService.add(dto));
    }

    @Operation(summary = "6-12 修改荣誉记录", description = "修改荣誉记录的名称、级别、获奖日期、说明与证书")
    @RequiresPerm("honor:record:edit")
    @OperLog(module = "管理端-荣誉记录", value = "修改荣誉记录")
    @PutMapping("/admin/honor/record")
    public Result<Void> putHonorRecord(@RequestBody @Valid HonorRecordUpdateDTO dto) {
        honorRecordAdminService.update(dto);
        return Result.success();
    }

    @Operation(summary = "6-13 删除荣誉记录", description = "逻辑删除单条荣誉记录")
    @RequiresPerm("honor:record:edit")
    @OperLog(module = "管理端-荣誉记录", value = "删除荣誉记录")
    @DeleteMapping("/admin/honor/record/{id}")
    public Result<Void> deleteHonorRecord(@PathVariable("id") Long id) {
        honorRecordAdminService.delete(id);
        return Result.success();
    }
}