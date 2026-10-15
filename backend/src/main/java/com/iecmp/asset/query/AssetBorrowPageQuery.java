package com.iecmp.asset.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 借用审核分页 查询条件。
 *
 * <p>对应接口：4-20（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetBorrowPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 待审核，1 已通过，2 已驳回，3 已归还，4 已逾期 */
    @Schema(description = "0 待审核，1 已通过，2 已驳回，3 已归还，4 已逾期")
    private Integer status;

    /** 1 借用，2 领用 */
    @Schema(description = "1 借用，2 领用")
    private Integer borrowType;

    /** 限定借用人 */
    @Schema(description = "限定借用人")
    private Long userId;

    /** 限定资产 */
    @Schema(description = "限定资产")
    private Long assetId;

    /** 模糊匹配资产编号 / 资产名称 / 借用人姓名 */
    @Schema(description = "模糊匹配资产编号 / 资产名称 / 借用人姓名")
    private String keyword;

    /** 申请时间起 */
    @Schema(description = "申请时间起")
    private String beginTime;

    /** 申请时间止 */
    @Schema(description = "申请时间止")
    private String endTime;
}
