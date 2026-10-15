package com.iecmp.duty.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 排班计划分页（后台） 查询条件。
 *
 * <p>对应接口：7-08（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyPlanPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 模糊匹配计划标题 */
    @Schema(description = "模糊匹配计划标题")
    private String keyword;

    /** 1 按周轮转，2 分组轮转，3 手工指定 */
    @Schema(description = "1 按周轮转，2 分组轮转，3 手工指定")
    private Integer planType;

    /** 0 草稿，1 已发布，2 已结束 */
    @Schema(description = "0 草稿，1 已发布，2 已结束")
    private Integer status;
}
