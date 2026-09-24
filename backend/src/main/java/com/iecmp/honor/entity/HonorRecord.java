package com.iecmp.honor.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 荣誉记录表：表 {@code honor_record}。
 *
 * <p>所属：模块六 · 学长学姐展板。字段以《IECMP-数据库设计.md》与
 * {@code docs/sql/IECMP-数据库建表脚本.sql} 为准。
 *
 * <p>公共审计字段（id / create_by / create_time / update_by / update_time / deleted）
 * 继承自 {@link BaseEntity}，此处只声明业务字段。
 *
 * @author IECMP 后端组
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("honor_record")
public class HonorRecord extends BaseEntity {

    /**
     * 所属学长学姐 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("alumni_id")
    private Long alumniId;

    /**
     * 荣誉名称。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("title")
    private String title;

    /**
     * 级别：1 国家级 2 省级 3 校级 4 企业及其他。
     */
    @TableField("level")
    private Integer level;

    /**
     * 获奖日期。
     */
    @TableField("award_date")
    private LocalDate awardDate;

    /**
     * 荣誉说明。
     */
    @TableField("description")
    private String description;

    /**
     * 证书图片/PDF 地址（OSS）。
     */
    @TableField("certificate_url")
    private String certificateUrl;

    /**
     * 排序号，升序。
     */
    @TableField("sort_no")
    private Integer sortNo;
}
