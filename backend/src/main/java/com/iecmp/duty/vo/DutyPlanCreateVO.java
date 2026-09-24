package com.iecmp.duty.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 新增排班计划 响应体。
 *
 * <p>对应接口：7-09（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyPlanCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新增计划 ID */
    @Schema(description = "新增计划 ID")
    private Long planId;
}
