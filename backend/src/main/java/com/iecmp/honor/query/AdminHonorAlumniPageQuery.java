package com.iecmp.honor.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 学长分页（后台） 查询条件。
 *
 * <p>对应接口：6-05（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminHonorAlumniPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 待审核，1 已通过，2 已驳回 */
    @Schema(description = "0 待审核，1 已通过，2 已驳回")
    private Integer status;

    /** 0 下板，1 上板 */
    @Schema(description = "0 下板，1 上板")
    private Integer isShow;

    /** 毕业年份筛选 */
    @Schema(description = "毕业年份筛选")
    private String graduationYear;

    /** 模糊匹配 name / company / position */
    @Schema(description = "模糊匹配 name / company / position")
    private String keyword;

    /** 限定填写人 */
    @Schema(description = "限定填写人")
    private Long submitUserId;
}
