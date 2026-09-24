package com.iecmp.seat.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 平面图数据 查询条件。
 *
 * <p>对应接口：5-01（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserSeatPlanQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 指定区域 ID；不传表示返回全部启用的区域 */
    @Schema(description = "指定区域 ID；不传表示返回全部启用的区域")
    private Long areaId;
}
