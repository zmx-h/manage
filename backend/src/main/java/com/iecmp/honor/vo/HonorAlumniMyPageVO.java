package com.iecmp.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 我的提交记录分页 响应体。
 *
 * <p>对应接口：6-15（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class HonorAlumniMyPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 提交记录 ID */
    @Schema(description = "提交记录 ID")
    private Long alumniId;

    /** 姓名 */
    @Schema(description = "姓名")
    private String name;

    /** 毕业年份 */
    @Schema(description = "毕业年份")
    private String graduationYear;

    /** 专业 */
    @Schema(description = "专业")
    private String major;

    /** 就职单位 */
    @Schema(description = "就职单位")
    private String company;

    /** 职位 */
    @Schema(description = "职位")
    private String position;

    /** 头像地址 */
    @Schema(description = "头像地址")
    private String avatarUrl;

    /** 0 待审核，1 已通过，2 已驳回 */
    @Schema(description = "0 待审核，1 已通过，2 已驳回")
    private Integer status;

    /** 0 下板，1 上板 */
    @Schema(description = "0 下板，1 上板")
    private Integer isShow;

    /** 审核人姓名 */
    @Schema(description = "审核人姓名")
    private String auditUserName;

    /** 审核时间 */
    @Schema(description = "审核时间")
    private String auditTime;

    /** 审核意见，驳回原因在此查看 */
    @Schema(description = "审核意见，驳回原因在此查看")
    private String auditRemark;

    /** 是否可撤回：0 否 1 是（仅待审核时为 1） */
    @Schema(description = "是否可撤回：0 否 1 是（仅待审核时为 1）")
    private Integer canWithdraw;

    /** 提交时间 */
    @Schema(description = "提交时间")
    private String createTime;
}
