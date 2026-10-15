package com.iecmp.repo.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 我的仓库分页 查询条件。
 *
 * <p>对应接口：2-07（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoProjectPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 模糊匹配 repoName / ownerName */
    @Schema(description = "模糊匹配 repoName / ownerName")
    private String keyword;

    /** 平台标识：gitee / github */
    @Schema(description = "平台标识：gitee / github")
    private String platform;

    /** 0 不统计，1 统计 */
    @Schema(description = "0 不统计，1 统计")
    private Integer tracked;

    /** 限定某个托管账户 */
    @Schema(description = "限定某个托管账户")
    private Long accountId;
}
