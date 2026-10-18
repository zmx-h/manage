package com.iecmp.budget.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.iecmp.common.base.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预算周期表：表 {@code budget_period}。
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
@TableName("budget_period")
public class BudgetPeriod extends BaseEntity {

    /**
     * 周期标题，如「2026 年度预算」。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("title")
    private String title;

    /**
     * 所属年度，如 2026。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("year")
    private String year;

    /**
     * 预算总额度。
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 已使用额度（冗余汇总，事务内维护）。
     */
    @TableField("used_amount")
    private BigDecimal usedAmount;

    /**
     * 状态：0 草稿 1 执行中 2 已结束。
     */
    @TableField("status")
    private Integer status;

    /**
     * 周期开始日期。
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 周期结束日期。
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 是否公示：0 不公示 1 公示中。
     */
    @TableField("is_public")
    private Integer isPublic;

    /**
     * 公示时间。
     */
    @TableField("public_time")
    private LocalDateTime publicTime;

    /**
     * 周期说明。
     */
    @TableField("description")
    private String description;

    /**
     * 乐观锁版本号。
     */
    @TableField("version")
    @Version
    private Integer version;
}