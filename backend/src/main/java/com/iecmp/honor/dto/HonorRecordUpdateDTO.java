package com.iecmp.honor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 修改荣誉记录 请求体。
 *
 * <p>对应接口：6-12（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class HonorRecordUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 荣誉记录 ID */
    @Schema(description = "荣誉记录 ID")
    @NotNull(message = "荣誉记录 ID不能为空")
    private Long recordId;

    /** 所属学长学姐 ID */
    @Schema(description = "所属学长学姐 ID")
    @NotNull(message = "所属学长学姐 ID不能为空")
    private Long alumniId;

    /** 荣誉名称，最长 200 字 */
    @Schema(description = "荣誉名称，最长 200 字")
    @NotBlank(message = "荣誉名称，最长 200 字不能为空")
    private String title;

    /** 1 国家级，2 省级，3 校级，4 企业及其他 */
    @Schema(description = "1 国家级，2 省级，3 校级，4 企业及其他")
    private Integer level;

    /** 获奖日期，格式 yyyy-MM-dd */
    @Schema(description = "获奖日期，格式 yyyy-MM-dd")
    private String awardDate;

    /** 荣誉说明，最长 500 字 */
    @Schema(description = "荣誉说明，最长 500 字")
    private String description;

    /** 证书图片 / PDF 地址 */
    @Schema(description = "证书图片 / PDF 地址")
    private String certificateUrl;

    /** 排序号，升序 */
    @Schema(description = "排序号，升序")
    private Integer sortNo;
}
