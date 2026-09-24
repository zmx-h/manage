package com.iecmp.duty.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 提醒配置表：表 {@code duty_reminder_config}。
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
@TableName("duty_reminder_config")
public class DutyReminderConfig extends BaseEntity {

    /**
     * 所属排班计划 ID，NULL 表示全局配置。
     */
    @TableField("plan_id")
    private Long planId;

    /**
     * 提前提醒分钟数。
     */
    @TableField("ahead_minutes")
    private Integer aheadMinutes;

    /**
     * 提醒渠道：1 站内消息。
     */
    @TableField("channel")
    private Integer channel;

    /**
     * 是否启用：0 停用 1 启用。
     */
    @TableField("enabled")
    private Integer enabled;
}
