package com.iecmp.commonm.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 公告分页（前台） 查询条件。
 *
 * <p>对应接口：8-05（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserNoticePageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 1 中心公告，2 制度，3 活动，4 其他 */
    @Schema(description = "1 中心公告，2 制度，3 活动，4 其他")
    private Integer noticeType;

    /** 模糊匹配标题 */
    @Schema(description = "模糊匹配标题")
    private String keyword;
}
