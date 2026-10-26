package com.iecmp.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 手动补采（后台） 响应体。
 *
 * <p>对应接口：2-17（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author WanZiyu
 */
@Data
public class RepoSyncVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 本次处理的账户总数 */
    @Schema(description = "本次处理的账户总数")
    private Integer totalAccounts;

    /** 成功的账户数 */
    @Schema(description = "成功的账户数")
    private Integer successCount;

    /** 失败的账户数 */
    @Schema(description = "失败的账户数")
    private Integer failCount;

    /** 全部账户合计新增提交数 */
    @Schema(description = "全部账户合计新增提交数")
    private Integer commitCount;

    /** 总耗时（毫秒） */
    @Schema(description = "总耗时（毫秒）")
    private Long costMs;
}
