package com.iecmp.honor.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 待审核信息分页（后台） 查询条件。
 *
 * <p>对应接口：6-17（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class HonorAlumniAuditPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 待审核（默认），1 已通过，2 已驳回 */
    @Schema(description = "0 待审核（默认），1 已通过，2 已驳回")
    private Integer status;

    /** 限定提交人 */
    @Schema(description = "限定提交人")
    private Long submitUserId;

    /** 提交时间起 */
    @Schema(description = "提交时间起")
    private String beginTime;

    /** 提交时间止 */
    @Schema(description = "提交时间止")
    private String endTime;
}
