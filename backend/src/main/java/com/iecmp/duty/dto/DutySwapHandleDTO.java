package com.iecmp.duty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 换班处理（同意 / 拒绝） 请求体。
 *
 * <p>对应接口：7-07（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutySwapHandleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 1 同意，2 拒绝 */
    @Schema(description = "1 同意，2 拒绝")
    @NotNull(message = "1 同意，2 拒绝不能为空")
    private Integer handleStatus;

    /** 处理意见 */
    @Schema(description = "处理意见")
    private String handleRemark;
}
