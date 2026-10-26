package com.iecmp.repo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 手动补采（后台） 请求体。
 *
 * <p>对应接口：2-17（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author WanZiyu
 */
@Data
public class RepoSyncDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 待补采的账户 ID 列表；传空数组表示全部有效账户 */
    @Schema(description = "待补采的账户 ID 列表；传空数组表示全部有效账户")
    private List<Long> accountIds;

    /** 采集范围：0 增量补采（从 `repo_account.last_sync_time` 起拉）1 全量补采（忽略上次时间，重拉全部历史提交） */
    @Schema(description = "采集范围：0 增量补采（从 `repo_account.last_sync_time` 起拉）1 全量补采（忽略上次时间，重拉全部历史提交）")
    private Integer fullSync;
}
