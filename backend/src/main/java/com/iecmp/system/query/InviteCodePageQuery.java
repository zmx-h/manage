package com.iecmp.system.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 邀请码分页 查询条件。
 *
 * <p>对应接口：1-27（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class InviteCodePageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 作废，1 有效 */
    @Schema(description = "0 作废，1 有效")
    private Integer status;

    /** 模糊匹配邀请码 */
    @Schema(description = "模糊匹配邀请码")
    private String keyword;
}
