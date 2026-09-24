package com.iecmp.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Jellyfin 媒体服务配置（架构文档 4.4 节）。
 *
 * <p>Jellyfin 无需 SDK，直接调用其 REST 接口生成播放直链后写入
 * {@code sys_video.play_url}，前端播放时只读数据库，不实时调用 Jellyfin。
 */
@Data
@Component
@ConfigurationProperties(prefix = "jellyfin")
public class JellyfinProperties {

    /** Jellyfin 服务地址，如 http://192.168.1.10:8096；留空表示未配置。 */
    private String baseUrl;

    /** API Key，管理员在 Jellyfin 后台生成。 */
    private String apiKey;

    /** 是否在生成 play_url 时追加 api_key 查询参数。 */
    private Boolean appendApiKey = true;
}
