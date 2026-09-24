package com.iecmp.duty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 修改排班计划 请求体。
 *
 * <p>对应接口：7-10（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyPlanUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 排班计划 ID */
    @Schema(description = "排班计划 ID")
    @NotNull(message = "排班计划 ID不能为空")
    private Long planId;

    /** 计划标题，最长 100 字 */
    @Schema(description = "计划标题，最长 100 字")
    @NotBlank(message = "计划标题，最长 100 字不能为空")
    private String title;

    /** 1 按周轮转，2 分组轮转，3 手工指定 */
    @Schema(description = "1 按周轮转，2 分组轮转，3 手工指定")
    private Integer planType;

    /** 计划开始日期，格式 yyyy-MM-dd */
    @Schema(description = "计划开始日期，格式 yyyy-MM-dd")
    @NotBlank(message = "计划开始日期，格式 yyyy-MM-dd不能为空")
    private String startDate;

    /** 计划结束日期，需晚于开始日期 */
    @Schema(description = "计划结束日期，需晚于开始日期")
    @NotBlank(message = "计划结束日期，需晚于开始日期不能为空")
    private String endDate;

    /** 参与轮转的成员 ID 数组，至少一人 */
    @Schema(description = "参与轮转的成员 ID 数组，至少一人")
    @NotEmpty(message = "参与轮转的成员 ID 数组，至少一人不能为空")
    private List<Long> memberIds;

    /** 分组配置，`planType = 2` 时必填；元素含 `name`、`memberIds` */
    @Schema(description = "分组配置，`planType = 2` 时必填；元素含 `name`、`memberIds`")
    @Valid
    private List<GroupConfigDTO> groupConfig;

    /** 默认值日内容 */
    @Schema(description = "默认值日内容")
    private String taskContent;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /**
     * GroupConfigDTO：修改排班计划 请求体的嵌套结构，字段含义见接口文档。
     */
    @Data
    public static class GroupConfigDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** name（字段含义见接口文档） */
        @Schema(description = "name（字段含义见接口文档）")
        private String name;

        /** memberIds（字段含义见接口文档） */
        @Schema(description = "memberIds（字段含义见接口文档）")
        private List<Long> memberIds;
    }
}
