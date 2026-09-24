package com.iecmp.commonm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 多文件上传 响应体。
 *
 * <p>对应接口：8-02（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class FileUploadBatchVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文件主键 */
    @Schema(description = "文件主键")
    private Long fileId;

    /** 原始文件名 */
    @Schema(description = "原始文件名")
    private String name;

    /** 访问地址（OSS 完整 URL） */
    @Schema(description = "访问地址（OSS 完整 URL）")
    private String url;

    /** 文件大小（字节） */
    @Schema(description = "文件大小（字节）")
    private Long size;

    /** MIME 类型 */
    @Schema(description = "MIME 类型")
    private String contentType;

    /** 业务归属 */
    @Schema(description = "业务归属")
    private String bizType;
}
