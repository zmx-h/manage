package com.iecmp.budget.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 提交预算申请 响应体。
 *
 * <p>对应接口：3-05（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class BudgetApplyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 申请记录 ID */
    @Schema(description = "申请记录 ID")
    private Long applyId;
}
