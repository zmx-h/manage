package com.iecmp.asset.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资产变动流水表：表 {@code asset_record}。
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
@TableName("asset_record")
public class AssetRecord extends BaseEntity {

    /**
     * 资产 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("asset_id")
    private Long assetId;

    /**
     * 变动类型：1 领用 2 借用 3 归还 4 维修 5 报废 6 调拨 7 盘点。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("record_type")
    private Integer recordType;

    /**
     * 变动前使用人 ID。
     */
    @TableField("from_user_id")
    private Long fromUserId;

    /**
     * 变动后使用人 ID。
     */
    @TableField("to_user_id")
    private Long toUserId;

    /**
     * 变动前存放位置。
     */
    @TableField("from_location")
    private String fromLocation;

    /**
     * 变动后存放位置。
     */
    @TableField("to_location")
    private String toLocation;

    /**
     * 变动时间。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("record_time")
    private LocalDateTime recordTime;

    /**
     * 操作人 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("operator_id")
    private Long operatorId;

    /**
     * 备注。
     */
    @TableField("remark")
    private String remark;
}
