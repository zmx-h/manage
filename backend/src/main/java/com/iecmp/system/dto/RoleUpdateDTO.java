package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 修改角色 请求体。
 *
 * <p>对应接口：1-18（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RoleUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 角色 ID */
    @Schema(description = "角色 ID")
    @NotNull(message = "角色 ID不能为空")
    private Long roleId;

    /** 角色名称，最长 32 字 */
    @Schema(description = "角色名称，最长 32 字")
    @NotBlank(message = "角色名称，最长 32 字不能为空")
    private String roleName;

    /** 角色编码，大写字母加下划线，全局唯一 */
    @Schema(description = "角色编码，大写字母加下划线，全局唯一")
    @NotBlank(message = "角色编码，大写字母加下划线，全局唯一不能为空")
    private String roleCode;

    /** 1 全部数据，2 本人数据 */
    @Schema(description = "1 全部数据，2 本人数据")
    private Integer dataScope;

    /** 排序号，升序 */
    @Schema(description = "排序号，升序")
    private Integer sortNo;

    /** 0 停用，1 启用 */
    @Schema(description = "0 停用，1 启用")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
