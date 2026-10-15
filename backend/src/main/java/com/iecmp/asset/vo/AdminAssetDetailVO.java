package com.iecmp.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * 资产详情（后台） 响应体。
 *
 * <p>对应接口：4-09（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminAssetDetailVO implements Serializable {

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

    /** 供应商 */
    @Schema(description = "供应商")
    private String supplier;

    /** 1 采购，2 捐赠，3 调拨 */
    @Schema(description = "1 采购，2 捐赠，3 调拨")
    private Integer sourceType;

    /** 关联的预算支出 ID（采购入账来源） */
    @Schema(description = "关联的预算支出 ID（采购入账来源）")
    private Long budgetExpenseId;

    /** 1 在用，2 闲置，3 维修中，4 已借出，5 已报废 */
    @Schema(description = "1 在用，2 闲置，3 维修中，4 已借出，5 已报废")
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

    /** 资产照片地址数组（OSS） */
    @Schema(description = "资产照片地址数组（OSS）")
    private List<String> imageUrls;

    /** 保修截止日期 */
    @Schema(description = "保修截止日期")
    private String warrantyEndDate;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 全部变动流水，元素含 `recordId`、`recordType`、`fromUserName`、`toUserName`、`fromLocation`、`toLocation`、`recordTime`、`operatorName`、`remark` */
    @Schema(description = "全部变动流水，元素含 `recordId`、`recordType`、`fromUserName`、`toUserName`、`fromLocation`、`toLocation`、`recordTime`、`operatorName`、`remark`")
    private List<RecordVO> records;

    /** 历史借用记录，元素含 `borrowId`、`userName`、`borrowTime`、`actualReturnTime`、`status` */
    @Schema(description = "历史借用记录，元素含 `borrowId`、`userName`、`borrowTime`、`actualReturnTime`、`status`")
    private List<BorrowHistoryVO> borrowHistory;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * RecordVO：资产详情（后台） 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class RecordVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** recordId（字段含义见接口文档） */
        @Schema(description = "recordId（字段含义见接口文档）")
        private Long recordId;

        /** recordType（字段含义见接口文档） */
        @Schema(description = "recordType（字段含义见接口文档）")
        private Integer recordType;

        /** fromUserName（字段含义见接口文档） */
        @Schema(description = "fromUserName（字段含义见接口文档）")
        private String fromUserName;

        /** toUserName（字段含义见接口文档） */
        @Schema(description = "toUserName（字段含义见接口文档）")
        private String toUserName;

        /** fromLocation（字段含义见接口文档） */
        @Schema(description = "fromLocation（字段含义见接口文档）")
        private String fromLocation;

        /** toLocation（字段含义见接口文档） */
        @Schema(description = "toLocation（字段含义见接口文档）")
        private String toLocation;

        /** recordTime（字段含义见接口文档） */
        @Schema(description = "recordTime（字段含义见接口文档）")
        private String recordTime;

        /** operatorName（字段含义见接口文档） */
        @Schema(description = "operatorName（字段含义见接口文档）")
        private String operatorName;

        /** remark（字段含义见接口文档） */
        @Schema(description = "remark（字段含义见接口文档）")
        private String remark;
    }

    /**
     * BorrowHistoryVO：资产详情（后台） 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class BorrowHistoryVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** borrowId（字段含义见接口文档） */
        @Schema(description = "borrowId（字段含义见接口文档）")
        private Long borrowId;

        /** userName（字段含义见接口文档） */
        @Schema(description = "userName（字段含义见接口文档）")
        private String userName;

        /** borrowTime（字段含义见接口文档） */
        @Schema(description = "borrowTime（字段含义见接口文档）")
        private String borrowTime;

        /** actualReturnTime（字段含义见接口文档） */
        @Schema(description = "actualReturnTime（字段含义见接口文档）")
        private String actualReturnTime;

        /** status（字段含义见接口文档） */
        @Schema(description = "status（字段含义见接口文档）")
        private Integer status;
    }
}
