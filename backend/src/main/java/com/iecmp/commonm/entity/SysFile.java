package com.iecmp.commonm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件表：表 {@code sys_file}。
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
@TableName("sys_file")
public class SysFile extends BaseEntity {

    /**
     * 原始文件名。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("name")
    private String name;

    /**
     * 访问地址（OSS 完整 URL）。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("url")
    private String url;

    /**
     * OSS 对象键，如 upload/avatar/2026/09/{uuid}.png。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("object_key")
    private String objectKey;

    /**
     * 存储桶名称，便于迁移时重算 URL。
     */
    @TableField("bucket")
    private String bucket;

    /**
     * 文件大小（字节）。
     */
    @TableField("size")
    private Long size;

    /**
     * MIME 类型。
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 文件哈希（预留：秒传与去重）。
     */
    @TableField("file_hash")
    private String fileHash;

    /**
     * 业务归属：avatar/asset/honor/budget/seat/notice/video/common。
     */
    @TableField("biz_type")
    private String bizType;

    /**
     * 上传人 ID。
     */
    @TableField("upload_user_id")
    private Long uploadUserId;
}
