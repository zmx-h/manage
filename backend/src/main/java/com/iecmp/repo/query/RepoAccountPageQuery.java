package com.iecmp.repo.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 账户与仓库分页（后台） 查询条件。
 *
 * <p>对应接口：2-13（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoAccountPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 模糊匹配 accountName / realName */
    @Schema(description = "模糊匹配 accountName / realName")
    private String keyword;

    /** 平台标识：gitee / github */
    @Schema(description = "平台标识：gitee / github")
    private String platform;

    /** 0 失效，1 有效 */
    @Schema(description = "0 失效，1 有效")
    private Integer status;

    /** 年级筛选 */
    @Schema(description = "年级筛选")
    private String grade;
}
