package com.iecmp.commonm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视频资源表：表 {@code sys_video}。
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
@TableName("sys_video")
public class SysVideo extends BaseEntity {

    /**
     * 视频标题。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("title")
    private String title;

    /**
     * 封面图地址（OSS）。
     */
    @TableField("cover_url")
    private String coverUrl;

    /**
     * Jellyfin 媒体项 ID，用于链接失效时重新拼接。
     */
    @TableField("jellyfin_item_id")
    private String jellyfinItemId;

    /**
     * 播放地址（Jellyfin 生成，直链或 HLS 播放列表）。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("play_url")
    private String playUrl;

    /**
     * 时长（秒）。
     */
    @TableField("duration")
    private Integer duration;

    /**
     * 业务归属：1 首页展示 2 学长学姐展板 3 其他。
     */
    @TableField("biz_type")
    private Integer bizType;

    /**
     * 展示排序，升序。
     */
    @TableField("sort_no")
    private Integer sortNo;

    /**
     * 状态：0 下架 1 上架。
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注。
     */
    @TableField("remark")
    private String remark;
}
