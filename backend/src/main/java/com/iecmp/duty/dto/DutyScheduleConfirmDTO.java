package com.iecmp.duty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 值日完成确认 请求体。
 *
 * <p>对应接口：7-04（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyScheduleConfirmDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
