package com.iecmp.seat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 我的工位申请分页 响应体。
 *
 * <p>对应接口：5-05（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatApplyMyPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 工位申请 ID */
    @Schema(description = "工位申请 ID")
    private Long applyId;

    /** 意向区域 ID */
    @Schema(description = "意向区域 ID")
    private Long areaId;

    /** 区域名称 */
    @Schema(description = "区域名称")
    private String areaName;

    /** 意向工位 ID；为空表示仅意向区域 */
    @Schema(description = "意向工位 ID；为空表示仅意向区域")
    private Long seatId;

    /** 意向工位编号 */
    @Schema(description = "意向工位编号")
    private String seatNo;

    /** 申请理由 */
    @Schema(description = "申请理由")
    private String reason;

    /** 0 待审核，1 已通过，2 已驳回，3 已撤销 */
    @Schema(description = "0 待审核，1 已通过，2 已驳回，3 已撤销")
    private Integer status;

    /** 审核人姓名 */
    @Schema(description = "审核人姓名")
    private String auditUserName;

    /** 审核时间 */
    @Schema(description = "审核时间")
    private String auditTime;

    /** 审核意见 */
    @Schema(description = "审核意见")
    private String auditRemark;

    /** 申请时间 */
    @Schema(description = "申请时间")
    private String createTime;
}
