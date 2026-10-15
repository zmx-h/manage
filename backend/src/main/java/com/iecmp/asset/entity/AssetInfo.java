package com.iecmp.asset.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.iecmp.common.base.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资产台账表：表 {@code asset_info}。
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
@TableName(value = "asset_info", autoResultMap = true)
public class AssetInfo extends BaseEntity {

    /**
     * 资产编号，唯一（一物一码）。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("asset_no")
    private String assetNo;

    /**
     * 资产名称。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("name")
    private String name;

    /**
     * 资产分类 ID。
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 品牌。
     */
    @TableField("brand")
    private String brand;

    /**
     * 型号。
     */
    @TableField("model")
    private String model;

    /**
     * 购置价格。
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 购置日期。
     */
    @TableField("purchase_date")
    private LocalDate purchaseDate;

    /**
     * 供应商。
     */
    @TableField("supplier")
    private String supplier;

    /**
     * 来源：1 采购 2 捐赠 3 调拨。
     */
    @TableField("source_type")
    private Integer sourceType;

    /**
     * 关联的预算支出 ID（采购入账闭环）。
     */
    @TableField("budget_expense_id")
    private Long budgetExpenseId;

    /**
     * 状态：1 在用 2 闲置 3 维修中 4 已借出 5 已报废。
     */
    @TableField("status")
    private Integer status;

    /**
     * 保管人 ID。
     */
    @TableField("keeper_id")
    private Long keeperId;

    /**
     * 存放位置文字描述，可与工位联动。
     */
    @TableField("location")
    private String location;

    /**
     * 资产照片地址数组（JSON）。
     */
    @TableField(value = "image_urls", typeHandler = JacksonTypeHandler.class)
    private List<String> imageUrls;

    /**
     * 保修截止日期。
     */
    @TableField("warranty_end_date")
    private LocalDate warrantyEndDate;

    /**
     * 备注。
     */
    @TableField("remark")
    private String remark;
}
