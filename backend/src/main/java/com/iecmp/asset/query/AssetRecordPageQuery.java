package com.iecmp.asset.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 变动流水分页 查询条件。
 *
 * <p>对应接口：4-16（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetRecordPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 限定某个资产 */
    @Schema(description = "限定某个资产")
    private Long assetId;

    /** 变动类型筛选 */
    @Schema(description = "变动类型筛选")
    private Integer recordType;

    /** 限定操作人 */
    @Schema(description = "限定操作人")
    private Long operatorId;

    /** 模糊匹配资产编号 / 资产名称 */
    @Schema(description = "模糊匹配资产编号 / 资产名称")
    private String keyword;

    /** 变动时间起 */
    @Schema(description = "变动时间起")
    private String beginTime;

    /** 变动时间止 */
    @Schema(description = "变动时间止")
    private String endTime;
}
