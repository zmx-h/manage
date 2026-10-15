package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 启用 / 禁用用户 请求体。
 *
 * <p>对应接口：1-12（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 禁用，1 启用 */
    @Schema(description = "0 禁用，1 启用")
    @NotNull(message = "0 禁用，1 启用不能为空")
    private Integer status;
}
