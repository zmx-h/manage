package com.iecmp.budget.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 我的预算申请分页 响应体。
 *
 * <p>对应接口：3-06（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetApplyMyPageVO implements Serializable {

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

    /** 申请标题 */
    @Schema(description = "申请标题")
    private String title;

    /** 申请金额 */
    @Schema(description = "申请金额")
    private BigDecimal amount;

    /** 期望使用日期 */
    @Schema(description = "期望使用日期")
    private String expectDate;

    /** 附件文件 ID */
    @Schema(description = "附件文件 ID")
    private Long attachFileId;

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

    /** 提交时间 */
    @Schema(description = "提交时间")
    private String createTime;
}
