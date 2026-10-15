package com.iecmp.commonm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站内消息表（补充表）：表 {@code sys_message}。
 *
 * <p>所属：模块八 · 通用支撑。字段以《IECMP-数据库设计.md》与
 * {@code docs/sql/IECMP-数据库建表脚本.sql} 为准。
 *
 * <p>公共审计字段（id / create_by / create_time / update_by / update_time / deleted）
 * 继承自 {@link BaseEntity}，此处只声明业务字段。
 *
 * @author IECMP 后端组
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message")
public class SysMessage extends BaseEntity {

    /**
     * 接收人 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("receiver_id")
    private Long receiverId;

    /**
     * 消息标题。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("title")
    private String title;

    /**
     * 消息内容。
     */
    @TableField("content")
    private String content;

    /**
     * 消息类型：1 值日提醒 2 审核结果 3 系统通知。
     */
    @TableField("msg_type")
    private Integer msgType;

    /**
     * 关联业务类型，如 duty_schedule / budget_apply。
     */
    @TableField("biz_type")
    private String bizType;

    /**
     * 关联业务主键，用于点击跳转与去重。
     */
    @TableField("biz_id")
    private Long bizId;

    /**
     * 是否已读：0 未读 1 已读。
     */
    @TableField("is_read")
    private Integer isRead;

    /**
     * 阅读时间。
     */
    @TableField("read_time")
    private LocalDateTime readTime;
}
