package com.iecmp.commonm.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.Result;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.commonm.service.SysFileService;
import com.iecmp.commonm.vo.FileUploadBatchVO;
import com.iecmp.commonm.vo.FileUploadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** 用户端-文件上传（commonm 模块）：8-01 ~ 8-03。 */
@Tag(name = "用户端-文件上传")
@RestController
@RequiredArgsConstructor
public class UserFileController {

    private final SysFileService sysFileService;

    @Operation(summary = "8-01 单文件上传", description = "上传单个文件，返回文件信息")
    @PostMapping("/user/file/upload")
    public Result<FileUploadVO> postFileUpload(@RequestPart("file") MultipartFile file,
                                               @RequestParam(value = "bizType", required = false) String bizType) {
        return Result.success(sysFileService.upload(file, bizType, LoginUserUtil.getUserId()));
    }

    @Operation(summary = "8-02 多文件上传", description = "一次上传多个文件，单次最多 9 个")
    @PostMapping("/user/file/upload/batch")
    public Result<List<FileUploadBatchVO>> postFileUploadBatch(@RequestPart("files") List<MultipartFile> files,
                                                               @RequestParam(value = "bizType", required = false) String bizType) {
        return Result.success(sysFileService.uploadBatch(files, bizType, LoginUserUtil.getUserId()));
    }

    @Operation(summary = "8-03 文件删除", description = "仅允许删除本人上传且未被业务引用的文件")
    @OperLog(module = "用户端-文件上传", value = "文件删除")
    @DeleteMapping("/user/file/{id}")
    public Result<Void> deleteFile(@PathVariable("id") Long id) {
        sysFileService.deleteFile(id, LoginUserUtil.getUserId(), LoginUserUtil.isSuperAdmin());
        return Result.success();
    }
}