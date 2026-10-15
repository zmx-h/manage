package com.iecmp.commonm.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 视频分页（后台） 查询条件。
 *
 * <p>对应接口：8-16（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class VideoPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 业务归属 */
    @Schema(description = "业务归属")
    private Integer bizType;

    /** 0 下架，1 上架 */
    @Schema(description = "0 下架，1 上架")
    private Integer status;

    /** 模糊匹配标题 */
    @Schema(description = "模糊匹配标题")
    private String keyword;
}
