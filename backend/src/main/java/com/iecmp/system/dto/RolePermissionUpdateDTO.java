package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 角色授权 请求体。
 *
 * <p>对应接口：1-20（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RolePermissionUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 权限码列表，传空数组表示清空权限 */
    @Schema(description = "权限码列表，传空数组表示清空权限")
    private List<String> permCodes;
}
