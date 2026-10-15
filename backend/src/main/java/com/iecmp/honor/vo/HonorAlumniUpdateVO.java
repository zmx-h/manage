package com.iecmp.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 重新提交我的信息 响应体。
 *
 * <p>对应接口：6-19（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class HonorAlumniUpdateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 提交记录 ID */
    @Schema(description = "提交记录 ID")
    private Long alumniId;

    /** 审核状态，修改后固定重置为 0 待审核 */
    @Schema(description = "审核状态，修改后固定重置为 0 待审核")
    private Integer status;

    /** 展示状态，修改后固定重置为 0 下板 */
    @Schema(description = "展示状态，修改后固定重置为 0 下板")
    private Integer isShow;
}
