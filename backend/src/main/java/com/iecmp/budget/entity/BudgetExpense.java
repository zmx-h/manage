package com.iecmp.budget.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 支出记录表：表 {@code budget_expense}。
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
@TableName("budget_expense")
public class BudgetExpense extends BaseEntity {

    /**
     * 所属预算周期 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("period_id")
    private Long periodId;

    /**
     * 所属预算科目 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("item_id")
    private Long itemId;

    /**
     * 关联的预算申请 ID，可为空（无申请的直接支出）。
     */
    @TableField("apply_id")
    private Long applyId;

    /**
     * 支出标题。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("title")
    private String title;

    /**
     * 支出金额。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 支出日期。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("expense_date")
    private LocalDate expenseDate;

    /**
     * 凭证文件 ID（发票 / 收据，sys_file.id）。
     */
    @TableField("voucher_file_id")
    private Long voucherFileId;

    /**
     * 经办人（管理员）ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("handler_id")
    private Long handlerId;

    /**
     * 备注。
     */
    @TableField("remark")
    private String remark;
}
