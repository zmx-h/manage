package com.iecmp.commonm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 公告分页（前台） 响应体。
 *
 * <p>对应接口：8-05（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserNoticePageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 公告 ID */
    @Schema(description = "公告 ID")
    private Long noticeId;

    /** 公告标题 */
    @Schema(description = "公告标题")
    private String title;

    /** 公告内容（富文本 HTML） */
    @Schema(description = "公告内容（富文本 HTML）")
    private String content;

    /** 公告类型 */
    @Schema(description = "公告类型")
    private Integer noticeType;

    /** 0 普通，1 置顶 */
    @Schema(description = "0 普通，1 置顶")
    private Integer isTop;

    /** 发布时间 */
    @Schema(description = "发布时间")
    private String publishTime;
}
