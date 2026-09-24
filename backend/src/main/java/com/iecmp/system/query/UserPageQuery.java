package com.iecmp.system.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 用户分页查询 查询条件。
 *
 * <p>对应接口：1-08（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 模糊匹配 realName / username / studentNo */
    @Schema(description = "模糊匹配 realName / username / studentNo")
    private String keyword;

    /** 0 禁用，1 启用 */
    @Schema(description = "0 禁用，1 启用")
    private Integer status;

    /** 角色编码筛选 */
    @Schema(description = "角色编码筛选")
    private String roleCode;

    /** 年级筛选 */
    @Schema(description = "年级筛选")
    private String grade;

    /** 创建时间起，格式 yyyy-MM-dd HH:mm:ss */
    @Schema(description = "创建时间起，格式 yyyy-MM-dd HH:mm:ss")
    private String beginTime;

    /** 创建时间止 */
    @Schema(description = "创建时间止")
    private String endTime;
}
