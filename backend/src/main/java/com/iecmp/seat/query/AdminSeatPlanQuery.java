package com.iecmp.seat.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 平面图数据（后台） 查询条件。
 *
 * <p>对应接口：5-25（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminSeatPlanQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 指定区域 ID；不传表示返回全部区域 */
    @Schema(description = "指定区域 ID；不传表示返回全部区域")
    private Long areaId;

    /** 是否包含停用区域：0 不包含 1 包含 */
    @Schema(description = "是否包含停用区域：0 不包含 1 包含")
    private Integer withDisabled;
}
