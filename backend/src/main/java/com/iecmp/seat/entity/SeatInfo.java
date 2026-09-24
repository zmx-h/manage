package com.iecmp.seat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.iecmp.common.base.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工位表：表 {@code seat_info}。
 *
 * <p>所属：模块五 · 部门工位。字段以《IECMP-数据库设计.md》与
 * {@code docs/sql/IECMP-数据库建表脚本.sql} 为准。
 *
 * <p>公共审计字段（id / create_by / create_time / update_by / update_time / deleted）
 * 继承自 {@link BaseEntity}，此处只声明业务字段。
 *
 * @author IECMP 后端组
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("seat_info")
public class SeatInfo extends BaseEntity {

    /**
     * 所属区域 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("area_id")
    private Long areaId;

    /**
     * 工位编号，如 A-01。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("seat_no")
    private String seatNo;

    /**
     * 类型：1 工位 2 会议室 3 储物柜 4 其他。
     */
    @TableField("seat_type")
    private Integer seatType;

    /**
     * X 坐标占底图宽度百分比（0~100）。
     */
    @TableField("x_ratio")
    private BigDecimal xRatio;

    /**
     * Y 坐标占底图高度百分比（0~100）。
     */
    @TableField("y_ratio")
    private BigDecimal yRatio;

    /**
     * 宽度占底图宽度百分比。
     */
    @TableField("width_ratio")
    private BigDecimal widthRatio;

    /**
     * 高度占底图高度百分比。
     */
    @TableField("height_ratio")
    private BigDecimal heightRatio;

    /**
     * 旋转角度（度）。
     */
    @TableField("rotation")
    private BigDecimal rotation;

    /**
     * 状态：1 空闲 2 已占用 3 预留 4 维修中。
     */
    @TableField("status")
    private Integer status;

    /**
     * 使用人 ID，NULL 表示无人使用。
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 分配时间。
     */
    @TableField("assign_time")
    private LocalDateTime assignTime;

    /**
     * 备注。
     */
    @TableField("remark")
    private String remark;

    /**
     * 乐观锁版本号。
     */
    @TableField("version")
    @Version
    private Integer version;
}
