package com.iecmp.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 上传体积与本地存储配置（接口文档 2.9 节）。
 *
 * <p>生产环境可将 {@code url-prefix} 指向 OSS/CDN；本地联调默认写入
 * {@code upload.local-dir}，由 {@code /upload/**} 静态资源映射对外提供访问。
 */
@Data
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {

    /** 图片上限（字节），默认 5MB。 */
    private Long maxImageSize = 5L * 1024 * 1024;

    /** 文档上限（字节），默认 20MB。 */
    private Long maxDocSize = 20L * 1024 * 1024;

    /** 本地文件根目录，默认 ./upload。 */
    private String localDir = "./upload";

    /** 文件访问前缀，默认 /api/upload（包含 context-path）。 */
    private String urlPrefix = "/api/upload";
}