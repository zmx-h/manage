package com.iecmp.asset.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资产分类表：表 {@code asset_category}。
 *
 * <p>所属：模块四 · 部门财产。字段以《IECMP-数据库设计.md》与
 * {@code docs/sql/IECMP-数据库建表脚本.sql} 为准。
 *
 * <p>公共审计字段（id / create_by / create_time / update_by / update_time / deleted）
 * 继承自 {@link BaseEntity}，此处只声明业务字段。
 *
 * @author IECMP 后端组
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("asset_category")
public class AssetCategory extends BaseEntity {

    /**
     * 分类名称。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("name")
    private String name;

    /**
     * 父分类 ID，0 表示一级分类。
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 分类编码，唯一。
     */
    @TableField("code")
    private String code;

    /**
     * 排序号，升序。
     */
    @TableField("sort_no")
    private Integer sortNo;
}
