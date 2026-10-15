package com.iecmp.seat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工位申请表：表 {@code seat_apply}。
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
@TableName("seat_apply")
public class SeatApply extends BaseEntity {

    /**
     * 申请人 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 意向区域 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("area_id")
    private Long areaId;

    /**
     * 意向工位 ID，NULL 表示仅意向区域。
     */
    @TableField("seat_id")
    private Long seatId;

    /**
     * 实际分配工位 ID，审核通过时写入（可能不同于意向工位）。
     */
    @TableField("assign_seat_id")
    private Long assignSeatId;

    /**
     * 申请理由。
     */
    @TableField("reason")
    private String reason;

    /**
     * 状态：0 待审核 1 已通过 2 已驳回 3 已撤销。
     */
    @TableField("status")
    private Integer status;

    /**
     * 审核人 ID。
     */
    @TableField("audit_user_id")
    private Long auditUserId;

    /**
     * 审核时间。
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;

    /**
     * 审核意见。
     */
    @TableField("audit_remark")
    private String auditRemark;
}
