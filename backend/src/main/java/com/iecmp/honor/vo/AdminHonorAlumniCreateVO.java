package com.iecmp.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 新增学长 响应体。
 *
 * <p>对应接口：6-06（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminHonorAlumniCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新增信息 ID */
    @Schema(description = "新增信息 ID")
    private Long alumniId;
}
