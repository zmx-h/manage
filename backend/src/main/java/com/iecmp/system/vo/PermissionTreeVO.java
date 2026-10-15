package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 权限码树 响应体。
 *
 * <p>对应接口：1-21（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class PermissionTreeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 权限码，叶子节点才返回 */
    @Schema(description = "权限码，叶子节点才返回")
    private String code;

    /** 展示名称 */
    @Schema(description = "展示名称")
    private String label;

    /** 所属模块标识，如 `system`、`budget` */
    @Schema(description = "所属模块标识，如 `system`、`budget`")
    private String module;

    /** 子节点 */
    @Schema(description = "子节点")
    private List<PermissionTreeVO> children;
}
