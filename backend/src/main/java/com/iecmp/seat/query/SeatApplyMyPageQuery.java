package com.iecmp.seat.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 我的工位申请分页 查询条件。
 *
 * <p>对应接口：5-05（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatApplyMyPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 待审核，1 已通过，2 已驳回，3 已撤销 */
    @Schema(description = "0 待审核，1 已通过，2 已驳回，3 已撤销")
    private Integer status;

    /** 申请时间起 */
    @Schema(description = "申请时间起")
    private String beginTime;

    /** 申请时间止 */
    @Schema(description = "申请时间止")
    private String endTime;
}
