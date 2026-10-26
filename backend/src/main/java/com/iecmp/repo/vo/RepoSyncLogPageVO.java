package com.iecmp.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 同步日志分页（后台） 响应体。
 *
 * <p>对应接口：2-16（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author WanZiyu
 */
@Data
public class RepoSyncLogPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 日志 ID */
    @Schema(description = "日志 ID")
    private Long logId;

    /** 成员用户 ID */
    @Schema(description = "成员用户 ID")
    private Long userId;

    /** 成员姓名 */
    @Schema(description = "成员姓名")
    private String realName;

    /** 平台标识 */
    @Schema(description = "平台标识")
    private String platform;

    /** 托管平台账号名 */
    @Schema(description = "托管平台账号名")
    private String accountName;

    /** 1 定时自动 2 管理员手动补采 */
    @Schema(description = "1 定时自动 2 管理员手动补采")
    private Integer syncType;

    /** 0 失败 1 成功 */
    @Schema(description = "0 失败 1 成功")
    private Integer syncResult;

    /** 本次同步到的提交数 */
    @Schema(description = "本次同步到的提交数")
    private Integer commitCount;

    /** 耗时（毫秒） */
    @Schema(description = "耗时（毫秒）")
    private Long costMs;

    /** 错误信息，失败时排查依据 */
    @Schema(description = "错误信息，失败时排查依据")
    private String errorMsg;

    /** 执行时间 */
    @Schema(description = "执行时间")
    private String createTime;
}
