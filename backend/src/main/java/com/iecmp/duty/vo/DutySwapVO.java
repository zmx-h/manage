package com.iecmp.duty.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 发起换班申请 响应体。
 *
 * <p>对应接口：7-05（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP liuyujie
 * @Date 2026.10.27
 */
@Data
public class DutySwapVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 换班申请 ID */
    @Schema(description = "换班申请 ID")
    private Long swapId;
}
