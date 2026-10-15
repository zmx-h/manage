package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 邀请码分页 响应体。
 *
 * <p>对应接口：1-27（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class InviteCodePageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 邀请码 ID */
    @Schema(description = "邀请码 ID")
    private Long id;

    /** 邀请码，8 位大写字母数字 */
    @Schema(description = "邀请码，8 位大写字母数字")
    private String code;

    /** 可用次数 */
    @Schema(description = "可用次数")
    private Integer maxUseCount;

    /** 已使用次数 */
    @Schema(description = "已使用次数")
    private Integer usedCount;

    /** 过期时间，为空表示长期有效 */
    @Schema(description = "过期时间，为空表示长期有效")
    private String expireTime;

    /** 0 作废，1 有效 */
    @Schema(description = "0 作废，1 有效")
    private Integer status;

    /** 生成时间 */
    @Schema(description = "生成时间")
    private String createTime;
}
