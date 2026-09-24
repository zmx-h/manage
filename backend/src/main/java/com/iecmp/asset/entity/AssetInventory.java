package com.iecmp.asset.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 盘点任务表：表 {@code asset_inventory}。
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
@TableName("asset_inventory")
public class AssetInventory extends BaseEntity {

    /**
     * 盘点任务标题。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("title")
    private String title;

    /**
     * 盘点开始时间。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("begin_time")
    private LocalDateTime beginTime;

    /**
     * 盘点结束时间。
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 状态：0 进行中 1 已完成。
     */
    @TableField("status")
    private Integer status;

    /**
     * 应盘点总数（结束盘点时回填）。
     */
    @TableField("total_count")
    private Integer totalCount;

    /**
     * 正常数（结束盘点时回填）。
     */
    @TableField("normal_count")
    private Integer normalCount;

    /**
     * 异常数（结束盘点时回填）。
     */
    @TableField("abnormal_count")
    private Integer abnormalCount;
}
