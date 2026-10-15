package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 角色分页查询 响应体。
 *
 * <p>对应接口：1-16（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RolePageVO implements Serializable {

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

    /** 1 全部数据，2 本人数据 */
    @Schema(description = "1 全部数据，2 本人数据")
    private Integer dataScope;

    /** 已分配权限码数量 */
    @Schema(description = "已分配权限码数量")
    private Integer permissionCount;

    /** 关联用户数量 */
    @Schema(description = "关联用户数量")
    private Integer userCount;

    /** 0 停用，1 启用 */
    @Schema(description = "0 停用，1 启用")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private String createTime;
}
