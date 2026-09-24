package com.iecmp.commonm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据字典表（补充表）：表 {@code sys_dict}。
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
@TableName("sys_dict")
public class SysDict extends BaseEntity {

    /**
     * 字典类型，如 assetStatus。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("dict_type")
    private String dictType;

    /**
     * 展示文案，如「在用」。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("dict_label")
    private String dictLabel;

    /**
     * 枚举值，如 1。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("dict_value")
    private Integer dictValue;

    /**
     * 排序号，升序。
     */
    @TableField("sort_no")
    private Integer sortNo;

    /**
     * 状态：0 停用 1 启用。
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注。
     */
    @TableField("remark")
    private String remark;
}
