package com.iecmp.budget.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 支出分页（后台） 响应体。
 *
 * <p>对应接口：3-23（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminBudgetExpensePageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 支出记录 ID */
    @Schema(description = "支出记录 ID")
    private Long expenseId;

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

    /** 支出标题 */
    @Schema(description = "支出标题")
    private String title;

    /** 支出金额 */
    @Schema(description = "支出金额")
    private BigDecimal amount;

    /** 支出日期 */
    @Schema(description = "支出日期")
    private String expenseDate;

    /** 凭证文件 ID */
    @Schema(description = "凭证文件 ID")
    private Long voucherFileId;

    /** 经办人 ID */
    @Schema(description = "经办人 ID")
    private Long handlerId;

    /** 经办人姓名 */
    @Schema(description = "经办人姓名")
    private String handlerName;

    /** 关联的申请 ID */
    @Schema(description = "关联的申请 ID")
    private Long applyId;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 登记时间 */
    @Schema(description = "登记时间")
    private String createTime;
}
