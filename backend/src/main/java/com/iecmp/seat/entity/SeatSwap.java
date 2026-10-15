package com.iecmp.seat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 座位调换申请表：表 {@code seat_swap}。
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
@TableName("seat_swap")
public class SeatSwap extends BaseEntity {

    /**
     * 申请方用户 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("apply_user_id")
    private Long applyUserId;

    /**
     * 对调方用户 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("target_user_id")
    private Long targetUserId;

    /**
     * 申请方当前工位 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("apply_seat_id")
    private Long applySeatId;

    /**
     * 对调方工位 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("target_seat_id")
    private Long targetSeatId;

    /**
     * 调换理由。
     */
    @TableField("reason")
    private String reason;

    /**
     * 状态：0 待对方确认 1 对方已同意待审核 2 已通过 3 已拒绝 4 已取消。
     */
    @TableField("status")
    private Integer status;

    /**
     * 对方处理时间。
     */
    @TableField("handle_time")
    private LocalDateTime handleTime;

    /**
     * 对方处理意见。
     */
    @TableField("handle_remark")
    private String handleRemark;

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
