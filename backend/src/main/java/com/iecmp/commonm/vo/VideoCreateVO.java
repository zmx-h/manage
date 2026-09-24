package com.iecmp.commonm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 新增视频 响应体。
 *
 * <p>对应接口：8-17（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class VideoCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新增视频 ID */
    @Schema(description = "新增视频 ID")
    private Long videoId;
}
