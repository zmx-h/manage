package com.iecmp.duty.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 值日安排表：表 {@code duty_schedule}。
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
@TableName("duty_schedule")
public class DutySchedule extends BaseEntity {

    /**
     * 所属排班计划 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("plan_id")
    private Long planId;

    /**
     * 值日日期。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("duty_date")
    private LocalDate dutyDate;

    /**
     * 值日人 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 所属分组名称。
     */
    @TableField("group_name")
    private String groupName;

    /**
     * 值日内容。
     */
    @TableField("task_content")
    private String taskContent;

    /**
     * 状态：0 未完成 1 已完成 2 请假 3 已换班。
     */
    @TableField("status")
    private Integer status;

    /**
     * 完成确认时间。
     */
    @TableField("confirm_time")
    private LocalDateTime confirmTime;

    /**
     * 备注。
     */
    @TableField("remark")
    private String remark;
}
