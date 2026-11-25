package com.iecmp.system.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 招新报名分页 查询条件。
 *
 * <p>对应接口：1-32。
 */
@Data
public class RecruitApplyPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 待考核 1 已通过待发码 2 未通过 3 已发码 4 已注册 5 注册码已过期 */
    @Schema(description = "报名状态")
    private Integer status;

    /** 报名时间起，格式 yyyy-MM-dd HH:mm:ss */
    @Schema(description = "报名时间起，格式 yyyy-MM-dd HH:mm:ss")
    private String beginTime;

    /** 报名时间止 */
    @Schema(description = "报名时间止")
    private String endTime;
}
