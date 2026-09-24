package com.iecmp.budget.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 申请审核分页（后台） 查询条件。
 *
 * <p>对应接口：3-20（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetApplyPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 待审核，1 已通过，2 已驳回，3 已撤销 */
    @Schema(description = "0 待审核，1 已通过，2 已驳回，3 已撤销")
    private Integer status;

    /** 限定预算周期 */
    @Schema(description = "限定预算周期")
    private Long periodId;

    /** 限定科目 */
    @Schema(description = "限定科目")
    private Long itemId;

    /** 限定申请人 */
    @Schema(description = "限定申请人")
    private Long applyUserId;

    /** 模糊匹配申请标题 / 申请人姓名 */
    @Schema(description = "模糊匹配申请标题 / 申请人姓名")
    private String keyword;

    /** 提交时间起 */
    @Schema(description = "提交时间起")
    private String beginTime;

    /** 提交时间止 */
    @Schema(description = "提交时间止")
    private String endTime;
}
