package com.iecmp.honor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 调整展示排序 请求体。
 *
 * <p>对应接口：6-10（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class HonorAlumniSortDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 排序号，升序排列 */
    @Schema(description = "排序号，升序排列")
    @NotNull(message = "排序号，升序排列不能为空")
    private Integer sortNo;
}
