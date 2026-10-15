package com.iecmp.budget.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预算申请表：表 {@code budget_apply}。
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
@TableName("budget_apply")
public class BudgetApply extends BaseEntity {

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
     * 申请人 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("apply_user_id")
    private Long applyUserId;

    /**
     * 申请标题。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("title")
    private String title;

    /**
     * 申请金额。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 批准金额（部分批准时小于申请金额；未填视为全额批准）。
     */
    @TableField("approved_amount")
    private BigDecimal approvedAmount;

    /**
     * 申请事由。
     */
    @TableField("reason")
    private String reason;

    /**
     * 期望使用日期。
     */
    @TableField("expect_date")
    private LocalDate expectDate;

    /**
     * 附件文件 ID（sys_file.id）。
     */
    @TableField("attach_file_id")
    private Long attachFileId;

    /**
     * 状态：0 待审核 1 已通过 2 已驳回 3 已撤销。
     */
    @TableField("status")
    private Integer status;

    /**
     * 审核人 ID。
     */
    @TableField("audit_user_id")
    private Long auditUserId;

    /**
     * 审核时间。
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;

    /**
     * 审核意见。
     */
    @TableField("audit_remark")
    private String auditRemark;
}
