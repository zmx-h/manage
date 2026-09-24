package com.iecmp.duty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 新增值日条目 请求体。
 *
 * <p>对应接口：7-14（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyScheduleAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 所属排班计划 ID */
    @Schema(description = "所属排班计划 ID")
    @NotNull(message = "所属排班计划 ID不能为空")
    private Long planId;

    /** 值日日期，格式 yyyy-MM-dd */
    @Schema(description = "值日日期，格式 yyyy-MM-dd")
    @NotBlank(message = "值日日期，格式 yyyy-MM-dd不能为空")
    private String dutyDate;

    /** 值日人 ID */
    @Schema(description = "值日人 ID")
    @NotNull(message = "值日人 ID不能为空")
    private Long userId;

    /** 分组名称 */
    @Schema(description = "分组名称")
    private String groupName;

    /** 值日内容；不传则取计划的默认值日内容 */
    @Schema(description = "值日内容；不传则取计划的默认值日内容")
    private String taskContent;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
