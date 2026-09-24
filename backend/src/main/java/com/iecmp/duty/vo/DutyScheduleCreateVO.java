package com.iecmp.duty.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 新增值日条目 响应体。
 *
 * <p>对应接口：7-14（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyScheduleCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新增值日条目 ID */
    @Schema(description = "新增值日条目 ID")
    private Long scheduleId;
}
