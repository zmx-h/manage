package com.iecmp.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 OSS 配置。
 *
 * <p>骨架阶段不引入 OSS SDK（见仓库 README「尚未引入的依赖」）；
 * 实现模块八的文件上传接口时再引入并启用本配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    /** 地域节点，如 oss-cn-hangzhou.aliyuncs.com。 */
    private String endpoint;

    /** Bucket 名称。 */
    private String bucket;

    /** AccessKey ID。生产环境通过环境变量注入，不要提交到仓库。 */
    private String accessKeyId;

    /** AccessKey Secret。 */
    private String accessKeySecret;

    /** 公网访问前缀，用于拼接文件外链。 */
    private String urlPrefix;
}
