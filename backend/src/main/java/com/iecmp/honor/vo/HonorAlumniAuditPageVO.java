package com.iecmp.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 待审核信息分页（后台） 响应体。
 *
 * <p>对应接口：6-17（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class HonorAlumniAuditPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 提交记录 ID */
    @Schema(description = "提交记录 ID")
    private Long alumniId;

    /** 姓名 */
    @Schema(description = "姓名")
    private String name;

    /** 0 未知，1 男，2 女 */
    @Schema(description = "0 未知，1 男，2 女")
    private Integer gender;

    /** 毕业年份 */
    @Schema(description = "毕业年份")
    private String graduationYear;

    /** 专业 */
    @Schema(description = "专业")
    private String major;

    /** 班级 */
    @Schema(description = "班级")
    private String className;

    /** 就职单位 */
    @Schema(description = "就职单位")
    private String company;

    /** 职位 */
    @Schema(description = "职位")
    private String position;

    /** 头像地址 */
    @Schema(description = "头像地址")
    private String avatarUrl;

    /** 个人简介 */
    @Schema(description = "个人简介")
    private String brief;

    /** 标签，逗号分隔原串 */
    @Schema(description = "标签，逗号分隔原串")
    private String tags;

    /** 提交人 ID */
    @Schema(description = "提交人 ID")
    private Long submitUserId;

    /** 提交人姓名 */
    @Schema(description = "提交人姓名")
    private String submitUserName;

    /** 提交人年级，便于判断信息可信度 */
    @Schema(description = "提交人年级，便于判断信息可信度")
    private String submitUserGrade;

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

    /** 审核意见 */
    @Schema(description = "审核意见")
    private String auditRemark;

    /** 提交时间 */
    @Schema(description = "提交时间")
    private String createTime;
}
