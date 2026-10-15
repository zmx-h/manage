package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 角色下拉列表 响应体。
 *
 * <p>对应接口：1-15（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RoleListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 角色 ID */
    @Schema(description = "角色 ID")
    private Long roleId;

    /** 角色名称 */
    @Schema(description = "角色名称")
    private String roleName;

    /** 角色编码 */
    @Schema(description = "角色编码")
    private String roleCode;
}
