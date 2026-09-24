package com.iecmp.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 学长详情（荣誉时间轴） 响应体。
 *
 * <p>对应接口：6-02（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class HonorAlumniDetailVO implements Serializable {

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

    /** 头像地址 */
    @Schema(description = "头像地址")
    private String avatarUrl;

    /** 个人简介（完整内容） */
    @Schema(description = "个人简介（完整内容）")
    private String brief;

    /** 标签数组 */
    @Schema(description = "标签数组")
    private List<String> tags;

    /** 荣誉记录数组，按 `awardDate` 倒序，结构见下表 */
    @Schema(description = "荣誉记录数组，按 `awardDate` 倒序，结构见下表")
    private List<RecordVO> records;

    /**
     * RecordVO：学长详情（荣誉时间轴） 响应体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class RecordVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 荣誉记录 ID */
        @Schema(description = "荣誉记录 ID")
        private Long recordId;

        /** 荣誉名称 */
        @Schema(description = "荣誉名称")
        private String title;

        /** 1 国家级，2 省级，3 校级，4 企业及其他 */
        @Schema(description = "1 国家级，2 省级，3 校级，4 企业及其他")
        private Integer level;

        /** 获奖日期 */
        @Schema(description = "获奖日期")
        private String awardDate;

        /** 荣誉说明 */
        @Schema(description = "荣誉说明")
        private String description;

        /** 证书图片 / PDF 地址（OSS） */
        @Schema(description = "证书图片 / PDF 地址（OSS）")
        private String certificateUrl;
    }
}
