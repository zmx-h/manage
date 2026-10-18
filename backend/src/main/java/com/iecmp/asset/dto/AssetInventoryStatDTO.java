package com.iecmp.asset.dto;

import lombok.Data;

/**
 * 盘点任务明细统计结果。
 *
 * <p>仅用于 Mapper 聚合查询的内部返回，不直接暴露给前端。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetInventoryStatDTO {

    /** 盘点任务 ID */
    private Long inventoryId;

    /** 明细总数 */
    private Integer totalCount;

    /** 已盘点数 */
    private Integer checkedCount;

    /** 正常数 */
    private Integer normalCount;

    /** 异常数（盘盈 + 盘亏） */
    private Integer abnormalCount;
}