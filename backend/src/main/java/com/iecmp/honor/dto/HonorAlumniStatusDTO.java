package com.iecmp.honor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 上板 / 下板 请求体。
 *
 * <p>对应接口：6-09（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class HonorAlumniStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 下板，1 上板 */
    @Schema(description = "0 下板，1 上板")
    @NotNull(message = "0 下板，1 上板不能为空")
    private Integer isShow;
}
