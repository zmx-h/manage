package com.iecmp.asset.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 盘点任务分页 查询条件。
 *
 * <p>对应接口：4-24（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AssetInventoryPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 进行中，1 已完成 */
    @Schema(description = "0 进行中，1 已完成")
    private Integer status;

    /** 模糊匹配任务标题 */
    @Schema(description = "模糊匹配任务标题")
    private String keyword;

    /** 开始日期起 */
    @Schema(description = "开始日期起")
    private String beginDate;

    /** 开始日期止 */
    @Schema(description = "开始日期止")
    private String endDate;
}
