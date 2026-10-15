package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 注册申请分页 响应体。
 *
 * <p>对应接口：1-25（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class RegisterApplyPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 申请 ID */
    @Schema(description = "申请 ID")
    private Long applyId;

    /** 姓名 */
    @Schema(description = "姓名")
    private String realName;

    /** 学号 */
    @Schema(description = "学号")
    private String studentNo;

    /** 专业 */
    @Schema(description = "专业")
    private String major;

    /** 手机号 */
    @Schema(description = "手机号")
    private String phone;

    /** 申请理由 */
    @Schema(description = "申请理由")
    private String reason;

    /** 0 待审核，1 已通过，2 已驳回 */
    @Schema(description = "0 待审核，1 已通过，2 已驳回")
    private Integer status;

    /** 提交 IP，便于识别恶意申请 */
    @Schema(description = "提交 IP，便于识别恶意申请")
    private String ip;

    /** 提交时间 */
    @Schema(description = "提交时间")
    private String applyTime;

    /** 审核意见 */
    @Schema(description = "审核意见")
    private String auditRemark;

    /** 通过后生成的用户 ID */
    @Schema(description = "通过后生成的用户 ID")
    private Long createUserId;
}
