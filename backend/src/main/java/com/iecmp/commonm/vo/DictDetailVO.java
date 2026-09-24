package com.iecmp.commonm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 数据字典查询 响应体。
 *
 * <p>对应接口：8-04（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DictDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 展示文案，如「在用」 */
    @Schema(description = "展示文案，如「在用」")
    private String label;

    /** 枚举值，如 1 */
    @Schema(description = "枚举值，如 1")
    private Integer value;
}
