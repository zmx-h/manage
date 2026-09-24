package com.iecmp.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 注册邀请码表：表 {@code sys_invite_code}。
 *
 * <p>所属：模块一 · 用户与权限。字段以《IECMP-数据库设计.md》与
 * {@code docs/sql/IECMP-数据库建表脚本.sql} 为准。
 *
 * <p>公共审计字段（id / create_by / create_time / update_by / update_time / deleted）
 * 继承自 {@link BaseEntity}，此处只声明业务字段。
 *
 * @author IECMP 后端组
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_invite_code")
public class SysInviteCode extends BaseEntity {

    /**
     * 邀请码，8 位大写字母数字。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("code")
    private String code;

    /**
     * 可用次数。
     */
    @TableField("max_use_count")
    private Integer maxUseCount;

    /**
     * 已使用次数。
     */
    @TableField("used_count")
    private Integer usedCount;

    /**
     * 过期时间，NULL 表示长期有效。
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 状态：0 作废 1 有效。
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注，如「2026 秋季纳新」。
     */
    @TableField("remark")
    private String remark;
}
