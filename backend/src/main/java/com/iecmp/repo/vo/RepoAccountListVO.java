package com.iecmp.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 我的绑定账户列表 响应体。
 *
 * <p>对应接口：2-03（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoAccountListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 托管账户 ID */
    @Schema(description = "托管账户 ID")
    private Long accountId;

    /** 平台标识：gitee / github */
    @Schema(description = "平台标识：gitee / github")
    private String platform;

    /** 平台展示名称 */
    @Schema(description = "平台展示名称")
    private String platformName;

    /** 托管平台账号名 */
    @Schema(description = "托管平台账号名")
    private String accountName;

    /** 平台头像地址（平台外链，不转存 OSS） */
    @Schema(description = "平台头像地址（平台外链，不转存 OSS）")
    private String avatarUrl;

    /** 0 失效，1 有效 */
    @Schema(description = "0 失效，1 有效")
    private Integer status;

    /** 已采集的仓库数量 */
    @Schema(description = "已采集的仓库数量")
    private Integer projectCount;

    /** 最近同步时间 */
    @Schema(description = "最近同步时间")
    private String lastSyncTime;

    /** 绑定时间 */
    @Schema(description = "绑定时间")
    private String bindTime;
}
