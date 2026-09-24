package com.iecmp.seat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 分配工位 请求体。
 *
 * <p>对应接口：5-15（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class SeatAssignDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 被分配成员的用户 ID */
    @Schema(description = "被分配成员的用户 ID")
    @NotNull(message = "被分配成员的用户 ID不能为空")
    private Long userId;
}
