package com.iecmp.seat.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 调换申请分页（后台） 查询条件。
 *
 * <p>对应接口：5-23（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatSwapPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 待对方确认，1 对方已同意待审核，2 已通过，3 已拒绝，4 已取消 */
    @Schema(description = "0 待对方确认，1 对方已同意待审核，2 已通过，3 已拒绝，4 已取消")
    private Integer status;

    /** 限定参与调换的成员（申请方或对调方） */
    @Schema(description = "限定参与调换的成员（申请方或对调方）")
    private Long userId;

    /** 模糊匹配双方姓名 / 工位编号 */
    @Schema(description = "模糊匹配双方姓名 / 工位编号")
    private String keyword;

    /** 发起时间起 */
    @Schema(description = "发起时间起")
    private String beginTime;

    /** 发起时间止 */
    @Schema(description = "发起时间止")
    private String endTime;
}
