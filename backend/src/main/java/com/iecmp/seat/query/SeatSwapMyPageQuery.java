package com.iecmp.seat.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 我的调换申请分页 查询条件。
 *
 * <p>对应接口：5-21（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatSwapMyPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 查询视角：0 全部（我发起 + 我收到） 1 我发起的 2 我收到的 */
    @Schema(description = "查询视角：0 全部（我发起 + 我收到） 1 我发起的 2 我收到的")
    private Integer role;

    /** 0 待对方确认，1 对方已同意待审核，2 已通过，3 已拒绝，4 已取消 */
    @Schema(description = "0 待对方确认，1 对方已同意待审核，2 已通过，3 已拒绝，4 已取消")
    private Integer status;
}
