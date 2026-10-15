package com.iecmp.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 精选轮播 响应体。
 *
 * <p>对应接口：6-03（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class HonorAlumniHighlightVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 学长学姐信息 ID */
    @Schema(description = "学长学姐信息 ID")
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

    /** 个人简介摘要 */
    @Schema(description = "个人简介摘要")
    private String brief;
}
