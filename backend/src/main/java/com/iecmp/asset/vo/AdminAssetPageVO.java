package com.iecmp.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 资产分页（后台） 响应体。
 *
 * <p>对应接口：4-08（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminAssetPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 资产 ID */
    @Schema(description = "资产 ID")
    private Long assetId;

    /** 资产编号 */
    @Schema(description = "资产编号")
    private String assetNo;

    /** 资产名称 */
    @Schema(description = "资产名称")
    private String name;

    /** 分类 ID */
    @Schema(description = "分类 ID")
    private Long categoryId;

    /** 分类名称 */
    @Schema(description = "分类名称")
    private String categoryName;

    /** 品牌 */
    @Schema(description = "品牌")
    private String brand;

    /** 型号 */
    @Schema(description = "型号")
    private String model;

    /** 购置价格 */
    @Schema(description = "购置价格")
    private BigDecimal price;

    /** 购置日期 */
    @Schema(description = "购置日期")
    private String purchaseDate;

    /** 1 采购，2 捐赠，3 调拨 */
    @Schema(description = "1 采购，2 捐赠，3 调拨")
    private Integer sourceType;

    /** 资产状态 */
    @Schema(description = "资产状态")
    private Integer status;

    /** 保管人 ID */
    @Schema(description = "保管人 ID")
    private Long keeperId;

    /** 保管人姓名 */
    @Schema(description = "保管人姓名")
    private String keeperName;

    /** 存放位置 */
    @Schema(description = "存放位置")
    private String location;

    /** 关联的预算支出 ID（采购入账来源） */
    @Schema(description = "关联的预算支出 ID（采购入账来源）")
    private Long budgetExpenseId;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private String createTime;
}
