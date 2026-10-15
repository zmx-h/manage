package com.iecmp.duty.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 换班申请表：表 {@code duty_swap}。
 *
 * <p>所属：模块七 · 值日排班。字段以《IECMP-数据库设计.md》与
 * {@code docs/sql/IECMP-数据库建表脚本.sql} 为准。
 *
 * <p>公共审计字段（id / create_by / create_time / update_by / update_time / deleted）
 * 继承自 {@link BaseEntity}，此处只声明业务字段。
 *
 * @author IECMP 后端组
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("duty_swap")
public class DutySwap extends BaseEntity {

    /**
     * 申请方的值日安排 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("schedule_id")
    private Long scheduleId;

    /**
     * 申请人 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("apply_user_id")
    private Long applyUserId;

    /**
     * 换班对象 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("target_user_id")
    private Long targetUserId;

    /**
     * 对调的目标值日安排 ID。
     */
    @TableField("target_schedule_id")
    private Long targetScheduleId;

    /**
     * 换班理由。
     */
    @TableField("reason")
    private String reason;

    /**
     * 状态：0 待确认 1 已同意 2 已拒绝 3 已取消。
     */
    @TableField("status")
    private Integer status;

    /**
     * 处理时间。
     */
    @TableField("handle_time")
    private LocalDateTime handleTime;

    /**
     * 处理意见。
     */
    @TableField("handle_remark")
    private String handleRemark;
}
