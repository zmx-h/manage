package com.iecmp.honor.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 展板学长分页 查询条件。
 *
 * <p>对应接口：6-01（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserHonorAlumniPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 毕业年份筛选，如 2024 */
    @Schema(description = "毕业年份筛选，如 2024")
    private String graduationYear;

    /** 专业筛选 */
    @Schema(description = "专业筛选")
    private String major;

    /** 模糊匹配 name / company / position */
    @Schema(description = "模糊匹配 name / company / position")
    private String keyword;
}
