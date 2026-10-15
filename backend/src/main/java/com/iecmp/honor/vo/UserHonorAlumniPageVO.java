package com.iecmp.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 展板学长分页 响应体。
 *
 * <p>对应接口：6-01（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserHonorAlumniPageVO implements Serializable {

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

    /** 班级 */
    @Schema(description = "班级")
    private String className;

    /** 就职单位 */
    @Schema(description = "就职单位")
    private String company;

    /** 职位 */
    @Schema(description = "职位")
    private String position;

    /** 头像地址（OSS） */
    @Schema(description = "头像地址（OSS）")
    private String avatarUrl;

    /** 个人简介（列表返回摘要，最长 100 字） */
    @Schema(description = "个人简介（列表返回摘要，最长 100 字）")
    private String brief;

    /** 标签数组（由 `tags` 字段按逗号拆分） */
    @Schema(description = "标签数组（由 `tags` 字段按逗号拆分）")
    private List<String> tags;

    /** 荣誉记录数量 */
    @Schema(description = "荣誉记录数量")
    private Integer honorCount;

    /** 展示排序号 */
    @Schema(description = "展示排序号")
    private Integer sortNo;
}
