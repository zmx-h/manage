package com.iecmp.system.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 角色分页查询 查询条件。
 *
 * <p>对应接口：1-16（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RolePageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 0 停用，1 启用 */
    @Schema(description = "0 停用，1 启用")
    private Integer status;
}
