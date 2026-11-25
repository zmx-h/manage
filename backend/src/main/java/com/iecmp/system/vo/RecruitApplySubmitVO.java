package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/** 招新报名提交 响应体（接口 1-30）。 */
@Data
public class RecruitApplySubmitVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 报名记录 ID */
    @Schema(description = "报名记录 ID")
    private Long applyId;

    /** 报名状态 */
    @Schema(description = "报名状态")
    private Integer status;
}
