package com.iecmp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 生成邀请码 请求体。
 *
 * <p>对应接口：1-28（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class InviteCodeAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 生成数量，范围 1~50 */
    @Schema(description = "生成数量，范围 1~50")
    private Integer count;

    /** 每个邀请码可用次数 */
    @Schema(description = "每个邀请码可用次数")
    private Integer maxUseCount;

    /** 过期时间，不传表示长期有效 */
    @Schema(description = "过期时间，不传表示长期有效")
    private String expireTime;

    /** 备注，如「2026 秋季纳新」 */
    @Schema(description = "备注，如「2026 秋季纳新」")
    private String remark;
}
