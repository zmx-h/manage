package com.iecmp.commonm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 视频分页（后台） 响应体。
 *
 * <p>对应接口：8-16（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class VideoPageVO implements Serializable {

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

    /** 业务归属：1 首页展示，2 学长学姐展板，3 其他 */
    @Schema(description = "业务归属：1 首页展示，2 学长学姐展板，3 其他")
    private Integer bizType;

    /** Jellyfin 媒体项 ID，用于链接失效时重新拼接 */
    @Schema(description = "Jellyfin 媒体项 ID，用于链接失效时重新拼接")
    private String jellyfinItemId;

    /** 0 下架，1 上架 */
    @Schema(description = "0 下架，1 上架")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private String createTime;
}
