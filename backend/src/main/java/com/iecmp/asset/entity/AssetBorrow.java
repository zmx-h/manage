package com.iecmp.asset.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iecmp.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 借用/领用表：表 {@code asset_borrow}。
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
@TableName("asset_borrow")
public class AssetBorrow extends BaseEntity {

    /**
     * 资产 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("asset_id")
    private Long assetId;

    /**
     * 类型：1 借用 2 领用。
     */
    @TableField("borrow_type")
    private Integer borrowType;

    /**
     * 借用人 ID。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用途说明。
     */
    @TableField("purpose")
    private String purpose;

    /**
     * 借用时间。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("borrow_time")
    private LocalDateTime borrowTime;

    /**
     * 预计归还时间。
     */
    @TableField("expect_return_time")
    private LocalDateTime expectReturnTime;

    /**
     * 实际归还时间。
     */
    @TableField("actual_return_time")
    private LocalDateTime actualReturnTime;

    /**
     * 状态：0 待审核 1 已通过 2 已驳回 3 已归还 4 已逾期。
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
