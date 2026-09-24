package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 新增角色 响应体。
 *
 * <p>对应接口：1-17（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RoleCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新增角色 ID */
    @Schema(description = "新增角色 ID")
    private Long roleId;
}
