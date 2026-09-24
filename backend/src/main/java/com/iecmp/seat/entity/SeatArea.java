package com.iecmp.seat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工位区域表：表 {@code seat_area}。
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
@TableName("seat_area")
public class SeatArea extends BaseEntity {

    /**
     * 区域名称，如「A 区开放工位」。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("name")
    private String name;

    /**
     * 平面图底图地址（OSS）。
     */
    @TableField("plan_image_url")
    private String planImageUrl;

    /**
     * 底图宽度（像素），坐标换算基准。
     */
    @TableField("width")
    private Integer width;

    /**
     * 底图高度（像素），坐标换算基准。
     */
    @TableField("height")
    private Integer height;

    /**
     * 区域说明。
     */
    @TableField("description")
    private String description;

    /**
     * 排序号，升序。
     */
    @TableField("sort_no")
    private Integer sortNo;
}
