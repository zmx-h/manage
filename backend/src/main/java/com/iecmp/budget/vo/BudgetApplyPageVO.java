package com.iecmp.budget.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 申请审核分页（后台） 响应体。
 *
 * <p>对应接口：3-20（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetApplyPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 申请记录 ID */
    @Schema(description = "申请记录 ID")
    private Long applyId;

    /** 所属周期 ID */
    @Schema(description = "所属周期 ID")
    private Long periodId;

    /** 周期标题 */
    @Schema(description = "周期标题")
    private String periodTitle;

    /** 科目 ID */
    @Schema(description = "科目 ID")
    private Long itemId;

    /** 科目名称 */
    @Schema(description = "科目名称")
    private String itemName;

    /** 申请人 ID */
    @Schema(description = "申请人 ID")
    private Long applyUserId;

    /** 申请人姓名 */
    @Schema(description = "申请人姓名")
    private String applyUserName;

    /** 申请人年级 */
    @Schema(description = "申请人年级")
    private String grade;

    /** 申请标题 */
    @Schema(description = "申请标题")
    private String title;

    /** 申请金额 */
    @Schema(description = "申请金额")
    private BigDecimal amount;

    /** 申请事由 */
    @Schema(description = "申请事由")
    private String reason;

    /** 期望使用日期 */
    @Schema(description = "期望使用日期")
    private String expectDate;

    /** 附件文件 ID */
    @Schema(description = "附件文件 ID")
    private Long attachFileId;

    /** 0 待审核，1 已通过，2 已驳回，3 已撤销 */
    @Schema(description = "0 待审核，1 已通过，2 已驳回，3 已撤销")
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

    /** 提交时间 */
    @Schema(description = "提交时间")
    private String createTime;
}
