package com.iecmp.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 我的同步状态 响应体。
 *
 * <p>对应接口：2-06（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoSyncStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 托管账户 ID */
    @Schema(description = "托管账户 ID")
    private Long accountId;

    /** 平台标识：gitee / github */
    @Schema(description = "平台标识：gitee / github")
    private String platform;

    /** 托管平台账号名 */
    @Schema(description = "托管平台账号名")
    private String accountName;

    /** 采集状态：0 未采集，1 采集中，2 采集正常，3 采集失败 */
    @Schema(description = "采集状态：0 未采集，1 采集中，2 采集正常，3 采集失败")
    private Integer syncStatus;

    /** 最近一次自动采集完成时间 */
    @Schema(description = "最近一次自动采集完成时间")
    private String lastSyncTime;

    /** 最近一次采集结果：0 失败 1 成功 */
    @Schema(description = "最近一次采集结果：0 失败 1 成功")
    private Integer lastSyncResult;

    /** 最近一次自动采集新增的提交数 */
    @Schema(description = "最近一次自动采集新增的提交数")
    private Integer newCommitCount;

    /** 采集失败原因；采集正常时为空 */
    @Schema(description = "采集失败原因；采集正常时为空")
    private String errorMsg;

    /** 下一次自动采集时间（由定时任务配置计算，见 5.2 节） */
    @Schema(description = "下一次自动采集时间（由定时任务配置计算，见 5.2 节）")
    private String nextSyncTime;
}
