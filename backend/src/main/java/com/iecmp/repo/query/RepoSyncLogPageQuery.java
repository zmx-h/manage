package com.iecmp.repo.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 同步日志分页（后台） 查询条件。
 *
 * <p>对应接口：2-16（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author WanZiyu
 */
@Data
public class RepoSyncLogPageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 限定某个成员 */
    @Schema(description = "限定某个成员")
    private Long userId;

    /** 1 定时自动 2 管理员手动补采 */
    @Schema(description = "1 定时自动 2 管理员手动补采")
    private Integer syncType;

    /** 0 失败 1 成功 */
    @Schema(description = "0 失败 1 成功")
    private Integer syncResult;

    /** 执行时间起 */
    @Schema(description = "执行时间起")
    private String beginTime;

    /** 执行时间止 */
    @Schema(description = "执行时间止")
    private String endTime;
}
