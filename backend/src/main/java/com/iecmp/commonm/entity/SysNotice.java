package com.iecmp.commonm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告表：表 {@code sys_notice}。
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
@TableName("sys_notice")
public class SysNotice extends BaseEntity {

    /**
     * 公告标题。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("title")
    private String title;

    /**
     * 公告内容（富文本 HTML）。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("content")
    private String content;

    /**
     * 公告类型：1 中心公告 2 制度 3 活动 4 其他。
     */
    @TableField("notice_type")
    private Integer noticeType;

    /**
     * 是否置顶：0 普通 1 置顶。
     */
    @TableField("is_top")
    private Integer isTop;

    /**
     * 发布时间。
     */
    @TableField("publish_time")
    private LocalDateTime publishTime;

    /**
     * 状态：0 草稿 1 已发布 2 已下架。
     */
    @TableField("status")
    private Integer status;
}
