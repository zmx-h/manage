package com.iecmp.commonm.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.Result;
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

/**
 * 用户端-文件上传（commonm 模块）。
 *
 * <p>覆盖接口：8-01、8-02、8-03，共 3 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-文件上传")
@RestController
@RequiredArgsConstructor
public class UserFileController {

    /** SysFile 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysFileService sysFileService;

    /**
     * 8-01 单文件上传。
     *
     * <p>{@code POST /user/file/upload} —— 上传单个文件，返回文件信息；业务表只保存返回的 fileId 与 url
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "8-01 单文件上传", description = "上传单个文件，返回文件信息；业务表只保存返回的 fileId 与 url")
    @PostMapping("/user/file/upload")
    public Result<FileUploadVO> postFileUpload(@RequestPart("file") MultipartFile file, @RequestParam(value = "bizType", required = false) String bizType) {
        // TODO 待实现（接口 8-01）：见《IECMP-接口开发文档-v1.6.md》
        return Result.todo("8-01");
    }

    /**
     * 8-02 多文件上传。
     *
     * <p>{@code POST /user/file/upload/batch} —— 一次上传多个文件，用于资产图片、荣誉证书等场景
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "8-02 多文件上传", description = "一次上传多个文件，用于资产图片、荣誉证书等场景")
    @PostMapping("/user/file/upload/batch")
    public Result<FileUploadBatchVO> postFileUploadBatch(@RequestPart("files") List<MultipartFile> files, @RequestParam(value = "bizType", required = false) String bizType) {
        // TODO 待实现（接口 8-02）：见《IECMP-接口开发文档-v1.6.md》
        return Result.todo("8-02");
    }

    /**
     * 8-03 文件删除。
     *
     * <p>{@code DELETE /user/file/{id}} —— 删除文件记录与物理文件，仅允许删除本人上传且未被业务引用的文件
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "8-03 文件删除", description = "删除文件记录与物理文件，仅允许删除本人上传且未被业务引用的文件")
    @OperLog(module = "用户端-文件上传", value = "文件删除")
    @DeleteMapping("/user/file/{id}")
    public Result<Void> deleteFile(@PathVariable("id") Long id) {
        // TODO 待实现（接口 8-03）：见《IECMP-接口开发文档-v1.6.md》接口 8-03
        return Result.todo("8-03");
    }
}
