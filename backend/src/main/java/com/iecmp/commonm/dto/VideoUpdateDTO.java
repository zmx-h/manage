package com.iecmp.commonm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * 视频修改 请求体。
 *
 * <p>对应接口：8-18（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class VideoUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 视频记录 ID */
    @Schema(description = "视频记录 ID")
    @NotNull(message = "视频记录 ID不能为空")
    private Long videoId;

    /** 视频标题，最长 100 字 */
    @Schema(description = "视频标题，最长 100 字")
    @NotBlank(message = "视频标题，最长 100 字不能为空")
    private String title;

    /** 播放地址，必须以 `http://` 或 `https://` 开头 */
    @Schema(description = "播放地址，必须以 `http://` 或 `https://` 开头")
    @NotBlank(message = "播放地址，必须以 `http://` 或 `https://` 开头不能为空")
    private String playUrl;

    /** 1 首页展示，2 学长学姐展板，3 其他 */
    @Schema(description = "1 首页展示，2 学长学姐展板，3 其他")
    @NotNull(message = "1 首页展示，2 学长学姐展板，3 其他不能为空")
    private Integer bizType;

    /** 封面图地址，可先调用接口 8-01 上传后填入 */
    @Schema(description = "封面图地址，可先调用接口 8-01 上传后填入")
    private String coverUrl;

    /** Jellyfin 媒体项 ID，用于链接失效时重新拼接 */
    @Schema(description = "Jellyfin 媒体项 ID，用于链接失效时重新拼接")
    private String jellyfinItemId;

    /** 时长（秒） */
    @Schema(description = "时长（秒）")
    private Integer duration;

    /** 展示排序，升序 */
    @Schema(description = "展示排序，升序")
    private Integer sortNo;

    /** 0 下架，1 上架 */
    @Schema(description = "0 下架，1 上架")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
