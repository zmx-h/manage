package com.iecmp.commonm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 标记消息已读 响应体。
 *
 * <p>对应接口：8-22（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class MessageReadVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 实际由未读变为已读的消息数 */
    @Schema(description = "实际由未读变为已读的消息数")
    private Integer updatedCount;
}
