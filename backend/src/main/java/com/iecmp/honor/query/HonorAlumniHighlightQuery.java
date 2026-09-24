package com.iecmp.honor.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 精选轮播 查询条件。
 *
 * <p>对应接口：6-03（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class HonorAlumniHighlightQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 返回条数上限，范围 1~20 */
    @Schema(description = "返回条数上限，范围 1~20")
    private Integer limit;
}
