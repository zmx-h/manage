package com.iecmp.commonm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 公告新增 响应体。
 *
 * <p>对应接口：8-07（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class NoticeCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新增公告 ID */
    @Schema(description = "新增公告 ID")
    private Long noticeId;
}
