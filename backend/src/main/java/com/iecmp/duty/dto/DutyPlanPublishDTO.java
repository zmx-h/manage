package com.iecmp.duty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 发布 / 取消发布 请求体。
 *
 * <p>对应接口：7-13（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyPlanPublishDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 取消发布，1 发布 */
    @Schema(description = "0 取消发布，1 发布")
    @NotNull(message = "0 取消发布，1 发布不能为空")
    private Integer publish;
}
