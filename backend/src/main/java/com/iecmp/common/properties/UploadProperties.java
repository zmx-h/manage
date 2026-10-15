package com.iecmp.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 上传体积限制（接口文档 2.9 节）。
 */
@Data
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {

    /** 图片上限（字节），默认 5MB。 */
    private Long maxImageSize = 5L * 1024 * 1024;

    /** 文档上限（字节），默认 20MB。 */
    private Long maxDocSize = 20L * 1024 * 1024;
}
