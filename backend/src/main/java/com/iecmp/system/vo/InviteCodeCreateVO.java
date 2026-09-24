package com.iecmp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 生成邀请码 响应体。
 *
 * <p>对应接口：1-28（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class InviteCodeCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 本次生成的邀请码列表 */
    @Schema(description = "本次生成的邀请码列表")
    private List<String> codes;
}
