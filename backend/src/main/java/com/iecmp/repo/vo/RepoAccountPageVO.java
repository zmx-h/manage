package com.iecmp.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 账户与仓库分页（后台） 响应体。
 *
 * <p>对应接口：2-13（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RepoAccountPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 托管账户 ID */
    @Schema(description = "托管账户 ID")
    private Long accountId;

    /** 成员用户 ID */
    @Schema(description = "成员用户 ID")
    private Long userId;

    /** 成员姓名 */
    @Schema(description = "成员姓名")
    private String realName;

    /** 年级 */
    @Schema(description = "年级")
    private String grade;

    /** 平台标识：gitee / github */
    @Schema(description = "平台标识：gitee / github")
    private String platform;

    /** 托管平台账号名 */
    @Schema(description = "托管平台账号名")
    private String accountName;

    /** 0 失效，1 有效 */
    @Schema(description = "0 失效，1 有效")
    private Integer status;

    /** 已采集仓库数 */
    @Schema(description = "已采集仓库数")
    private Integer projectCount;

    /** 已采集提交数 */
    @Schema(description = "已采集提交数")
    private Integer commitCount;

    /** 最近同步时间 */
    @Schema(description = "最近同步时间")
    private String lastSyncTime;

    /** 绑定时间 */
    @Schema(description = "绑定时间")
    private String bindTime;
}
