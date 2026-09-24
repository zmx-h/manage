package com.iecmp.commonm.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 操作日志分页 查询条件。
 *
 * <p>对应接口：8-12（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class LogPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 模块标识，如 system / budget / asset */
    @Schema(description = "模块标识，如 system / budget / asset")
    private String module;

    /** 操作类型，如 add / edit / remove / audit */
    @Schema(description = "操作类型，如 add / edit / remove / audit")
    private String operation;

    /** 操作人姓名，模糊匹配 */
    @Schema(description = "操作人姓名，模糊匹配")
    private String operatorName;

    /** 0 失败，1 成功 */
    @Schema(description = "0 失败，1 成功")
    private Integer status;

    /** 操作时间起 */
    @Schema(description = "操作时间起")
    private String beginTime;

    /** 操作时间止 */
    @Schema(description = "操作时间止")
    private String endTime;
}
