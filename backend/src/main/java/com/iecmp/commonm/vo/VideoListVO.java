package com.iecmp.commonm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 前台展示视频列表 响应体。
 *
 * <p>对应接口：8-15（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class VideoListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 视频记录 ID */
    @Schema(description = "视频记录 ID")
    private Long videoId;

    /** 视频标题 */
    @Schema(description = "视频标题")
    private String title;

    /** 封面图地址 */
    @Schema(description = "封面图地址")
    private String coverUrl;

    /** 播放地址（Jellyfin 生成，直链或 HLS 播放列表） */
    @Schema(description = "播放地址（Jellyfin 生成，直链或 HLS 播放列表）")
    private String playUrl;

    /** 时长（秒） */
    @Schema(description = "时长（秒）")
    private Integer duration;

    /** 业务归属 */
    @Schema(description = "业务归属")
    private Integer bizType;
}
