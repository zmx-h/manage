package com.iecmp.commonm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Data;

/**
 * 公告新增 请求体。
 *
 * <p>对应接口：8-07（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class NoticeAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 标题，最长 100 字 */
    @Schema(description = "标题，最长 100 字")
    @NotBlank(message = "标题，最长 100 字不能为空")
    private String title;

    /** 内容，富文本 HTML */
    @Schema(description = "内容，富文本 HTML")
    @NotBlank(message = "内容，富文本 HTML不能为空")
    private String content;

    /** 1 中心公告，2 制度，3 活动，4 其他 */
    @Schema(description = "1 中心公告，2 制度，3 活动，4 其他")
    private Integer noticeType;

    /** 0 普通，1 置顶 */
    @Schema(description = "0 普通，1 置顶")
    private Integer isTop;

    /** 0 草稿，1 立即发布 */
    @Schema(description = "0 草稿，1 立即发布")
    private Integer status;

    /** 定时发布时间，需晚于当前时间 */
    @Schema(description = "定时发布时间，需晚于当前时间")
    private String publishTime;
}
