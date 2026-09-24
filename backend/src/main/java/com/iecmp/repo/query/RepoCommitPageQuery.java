package com.iecmp.repo.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 提交记录分页 查询条件。
 *
 * <p>对应接口：2-09（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoCommitPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 限定某个仓库 */
    @Schema(description = "限定某个仓库")
    private Long projectId;

    /** 限定某个分支 */
    @Schema(description = "限定某个分支")
    private String branch;

    /** 模糊匹配提交信息 message */
    @Schema(description = "模糊匹配提交信息 message")
    private String keyword;

    /** 是否包含合并提交：0 不包含 1 包含 */
    @Schema(description = "是否包含合并提交：0 不包含 1 包含")
    private Integer includeMerge;

    /** 提交时间起，格式 yyyy-MM-dd HH:mm:ss */
    @Schema(description = "提交时间起，格式 yyyy-MM-dd HH:mm:ss")
    private String beginTime;

    /** 提交时间止 */
    @Schema(description = "提交时间止")
    private String endTime;
}
