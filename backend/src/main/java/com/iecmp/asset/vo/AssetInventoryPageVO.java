package com.iecmp.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 盘点任务分页 响应体。
 *
 * <p>对应接口：4-24（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetInventoryPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 盘点任务 ID */
    @Schema(description = "盘点任务 ID")
    private Long inventoryId;

    /** 任务标题 */
    @Schema(description = "任务标题")
    private String title;

    /** 开始时间 */
    @Schema(description = "开始时间")
    private String beginTime;

    /** 结束时间 */
    @Schema(description = "结束时间")
    private String endTime;

    /** 0 进行中，1 已完成 */
    @Schema(description = "0 进行中，1 已完成")
    private Integer status;

    /** 应盘点总数 */
    @Schema(description = "应盘点总数")
    private Integer totalCount;

    /** 已盘点数量（`checkStatus ≠ 0` 的明细数） */
    @Schema(description = "已盘点数量（`checkStatus ≠ 0` 的明细数）")
    private Integer checkedCount;

    /** 正常数量 */
    @Schema(description = "正常数量")
    private Integer normalCount;

    /** 异常数量（盘亏 + 位置不符） */
    @Schema(description = "异常数量（盘亏 + 位置不符）")
    private Integer abnormalCount;

    /** 盘点进度（百分比数值） */
    @Schema(description = "盘点进度（百分比数值）")
    private BigDecimal progressRate;
}
