package com.iecmp.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 变动流水分页 响应体。
 *
 * <p>对应接口：4-16（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetRecordPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 流水记录 ID */
    @Schema(description = "流水记录 ID")
    private Long recordId;

    /** 资产 ID */
    @Schema(description = "资产 ID")
    private Long assetId;

    /** 资产编号 */
    @Schema(description = "资产编号")
    private String assetNo;

    /** 资产名称 */
    @Schema(description = "资产名称")
    private String assetName;

    /** 1 领用，2 借用，3 归还，4 维修，5 报废，6 调拨，7 盘点 */
    @Schema(description = "1 领用，2 借用，3 归还，4 维修，5 报废，6 调拨，7 盘点")
    private Integer recordType;

    /** 变动前使用人 ID */
    @Schema(description = "变动前使用人 ID")
    private Long fromUserId;

    /** 变动前使用人姓名 */
    @Schema(description = "变动前使用人姓名")
    private String fromUserName;

    /** 变动后使用人 ID */
    @Schema(description = "变动后使用人 ID")
    private Long toUserId;

    /** 变动后使用人姓名 */
    @Schema(description = "变动后使用人姓名")
    private String toUserName;

    /** 变动前存放位置 */
    @Schema(description = "变动前存放位置")
    private String fromLocation;

    /** 变动后存放位置 */
    @Schema(description = "变动后存放位置")
    private String toLocation;

    /** 变动时间 */
    @Schema(description = "变动时间")
    private String recordTime;

    /** 操作人 ID */
    @Schema(description = "操作人 ID")
    private Long operatorId;

    /** 操作人姓名 */
    @Schema(description = "操作人姓名")
    private String operatorName;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
