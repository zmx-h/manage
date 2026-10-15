package com.iecmp.budget.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预算科目表：表 {@code budget_item}。
 *
 * <p>所属：模块三 · 部门预算。字段以《IECMP-数据库设计.md》与
 * {@code docs/sql/IECMP-数据库建表脚本.sql} 为准。
 *
 * <p>公共审计字段（id / create_by / create_time / update_by / update_time / deleted）
 * 继承自 {@link BaseEntity}，此处只声明业务字段。
 *
 * @author IECMP 后端组
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("budget_item")
public class BudgetItem extends BaseEntity {

    /**
     * 所属预算周期 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("period_id")
    private Long periodId;

    /**
     * 科目名称。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("name")
    private String name;

    /**
     * 科目分类：1 设备采购 2 耗材 3 活动 4 差旅 5 其他。
     */
    @TableField("category")
    private Integer category;

    /**
     * 科目预算额度。
     */
    @TableField("budget_amount")
    private BigDecimal budgetAmount;

    /**
     * 已使用额度（冗余汇总，事务内维护）。
     */
    @TableField("used_amount")
    private BigDecimal usedAmount;

    /**
     * 排序号，升序。
     */
    @TableField("sort_no")
    private Integer sortNo;

    /**
     * 备注。
     */
    @TableField("remark")
    private String remark;
}
