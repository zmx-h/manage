package com.iecmp.seat.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 工位申请分页（后台） 查询条件。
 *
 * <p>对应接口：5-18（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatApplyPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 待审核，1 已通过，2 已驳回，3 已撤销 */
    @Schema(description = "0 待审核，1 已通过，2 已驳回，3 已撤销")
    private Integer status;

    /** 限定意向区域 */
    @Schema(description = "限定意向区域")
    private Long areaId;

    /** 限定申请人 */
    @Schema(description = "限定申请人")
    private Long userId;

    /** 模糊匹配申请人姓名 / 工位编号 */
    @Schema(description = "模糊匹配申请人姓名 / 工位编号")
    private String keyword;

    /** 申请时间起 */
    @Schema(description = "申请时间起")
    private String beginTime;

    /** 申请时间止 */
    @Schema(description = "申请时间止")
    private String endTime;
}
