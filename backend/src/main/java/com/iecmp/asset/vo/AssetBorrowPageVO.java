package com.iecmp.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 借用审核分页 响应体。
 *
 * <p>对应接口：4-20（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetBorrowPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 借用记录 ID */
    @Schema(description = "借用记录 ID")
    private Long borrowId;

    /** 资产 ID */
    @Schema(description = "资产 ID")
    private Long assetId;

    /** 资产编号 */
    @Schema(description = "资产编号")
    private String assetNo;

    /** 资产名称 */
    @Schema(description = "资产名称")
    private String assetName;

    /** 1 借用，2 领用 */
    @Schema(description = "1 借用，2 领用")
    private Integer borrowType;

    /** 借用人 ID */
    @Schema(description = "借用人 ID")
    private Long userId;

    /** 借用人姓名 */
    @Schema(description = "借用人姓名")
    private String userName;

    /** 年级 */
    @Schema(description = "年级")
    private String grade;

    /** 用途说明 */
    @Schema(description = "用途说明")
    private String purpose;

    /** 借用时间 */
    @Schema(description = "借用时间")
    private String borrowTime;

    /** 预计归还时间 */
    @Schema(description = "预计归还时间")
    private String expectReturnTime;

    /** 实际归还时间 */
    @Schema(description = "实际归还时间")
    private String actualReturnTime;

    /** 0 待审核，1 已通过，2 已驳回，3 已归还，4 已逾期 */
    @Schema(description = "0 待审核，1 已通过，2 已驳回，3 已归还，4 已逾期")
    private Integer status;

    /** 审核人 ID */
    @Schema(description = "审核人 ID")
    private Long auditUserId;

    /** 审核人姓名 */
    @Schema(description = "审核人姓名")
    private String auditUserName;

    /** 审核时间 */
    @Schema(description = "审核时间")
    private String auditTime;

    /** 审核意见 */
    @Schema(description = "审核意见")
    private String auditRemark;
}
