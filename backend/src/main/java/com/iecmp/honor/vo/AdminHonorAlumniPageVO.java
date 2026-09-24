package com.iecmp.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 学长分页（后台） 响应体。
 *
 * <p>对应接口：6-05（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminHonorAlumniPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 学长学姐信息 ID */
    @Schema(description = "学长学姐信息 ID")
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

    /** 就职单位 */
    @Schema(description = "就职单位")
    private String company;

    /** 职位 */
    @Schema(description = "职位")
    private String position;

    /** 头像地址 */
    @Schema(description = "头像地址")
    private String avatarUrl;

    /** 标签，逗号分隔原串 */
    @Schema(description = "标签，逗号分隔原串")
    private String tags;

    /** 荣誉记录数量 */
    @Schema(description = "荣誉记录数量")
    private Integer honorCount;

    /** 填写人 ID */
    @Schema(description = "填写人 ID")
    private Long submitUserId;

    /** 填写人姓名 */
    @Schema(description = "填写人姓名")
    private String submitUserName;

    /** 0 待审核，1 已通过，2 已驳回 */
    @Schema(description = "0 待审核，1 已通过，2 已驳回")
    private Integer status;

    /** 0 下板，1 上板 */
    @Schema(description = "0 下板，1 上板")
    private Integer isShow;

    /** 展示排序号 */
    @Schema(description = "展示排序号")
    private Integer sortNo;

    /** 审核人姓名 */
    @Schema(description = "审核人姓名")
    private String auditUserName;

    /** 审核时间 */
    @Schema(description = "审核时间")
    private String auditTime;

    /** 审核意见 */
    @Schema(description = "审核意见")
    private String auditRemark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private String createTime;
}
