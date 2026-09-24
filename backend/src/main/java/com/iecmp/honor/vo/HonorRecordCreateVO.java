package com.iecmp.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 新增荣誉记录 响应体。
 *
 * <p>对应接口：6-11（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class HonorRecordCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新增荣誉记录 ID */
    @Schema(description = "新增荣誉记录 ID")
    private Long recordId;
}
