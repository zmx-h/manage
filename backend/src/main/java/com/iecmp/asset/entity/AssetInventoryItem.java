package com.iecmp.asset.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 盘点明细表：表 {@code asset_inventory_item}。
 *
 * <p>所属：模块四 · 部门财产。字段以《IECMP-数据库设计.md》与
 * {@code docs/sql/IECMP-数据库建表脚本.sql} 为准。
 *
 * <p>公共审计字段（id / create_by / create_time / update_by / update_time / deleted）
 * 继承自 {@link BaseEntity}，此处只声明业务字段。
 *
 * @author IECMP 后端组
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("asset_inventory_item")
public class AssetInventoryItem extends BaseEntity {

    /**
     * 盘点任务 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("inventory_id")
    private Long inventoryId;

    /**
     * 资产 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("asset_id")
    private Long assetId;

    /**
     * 盘点结果：0 未盘点 1 正常 2 盘亏 3 位置不符。
     */
    @TableField("check_status")
    private Integer checkStatus;

    /**
     * 盘点人 ID。
     */
    @TableField("check_user_id")
    private Long checkUserId;

    /**
     * 盘点时间。
     */
    @TableField("check_time")
    private LocalDateTime checkTime;

    /**
     * 备注（差异说明）。
     */
    @TableField("remark")
    private String remark;
}
