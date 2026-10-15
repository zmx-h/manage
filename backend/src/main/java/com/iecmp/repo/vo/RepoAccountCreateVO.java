package com.iecmp.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 绑定代码托管账户 响应体。
 *
 * <p>对应接口：2-02（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoAccountCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新增托管账户 ID */
    @Schema(description = "新增托管账户 ID")
    private Long accountId;

    /** 本次拉取到的仓库数量 */
    @Schema(description = "本次拉取到的仓库数量")
    private Integer projectCount;
}
