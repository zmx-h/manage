package com.iecmp.commonm.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 公告分页（后台） 查询条件。
 *
 * <p>对应接口：8-06（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminNoticePageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 草稿，1 已发布，2 已下架 */
    @Schema(description = "0 草稿，1 已发布，2 已下架")
    private Integer status;

    /** 公告类型 */
    @Schema(description = "公告类型")
    private Integer noticeType;

    /** 创建时间起 */
    @Schema(description = "创建时间起")
    private String beginTime;

    /** 创建时间止 */
    @Schema(description = "创建时间止")
    private String endTime;
}
