package com.iecmp.commonm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 标记消息已读 请求体。
 *
 * <p>对应接口：8-22（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class MessageReadDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 待标记的消息 ID 数组，单次最多 100 条 */
    @Schema(description = "待标记的消息 ID 数组，单次最多 100 条")
    private List<Long> messageIds;

    /** 是否全部已读：0 否 1 是（只作用于本人消息） */
    @Schema(description = "是否全部已读：0 否 1 是（只作用于本人消息）")
    private Integer all;

    /** 仅配合 `all = 1` 使用：只把该类型的消息标记已读 */
    @Schema(description = "仅配合 `all = 1` 使用：只把该类型的消息标记已读")
    private Integer msgType;
}
