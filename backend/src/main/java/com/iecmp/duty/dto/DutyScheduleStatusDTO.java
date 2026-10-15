package com.iecmp.duty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 值日状态变更 请求体。
 *
 * <p>对应接口：7-18（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyScheduleStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 目标状态：0 未完成，1 已完成，2 请假 */
    @Schema(description = "目标状态：0 未完成，1 已完成，2 请假")
    @NotNull(message = "目标状态：0 未完成，1 已完成，2 请假不能为空")
    private Integer status;

    /** 变更原因，如「已请假，另行补值」 */
    @Schema(description = "变更原因，如「已请假，另行补值」")
    private String remark;
}
