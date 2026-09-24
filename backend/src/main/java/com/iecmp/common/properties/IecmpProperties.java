package com.iecmp.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 平台自定义配置（架构文档 7.3 节配置项清单）。
 */
@Data
@Component
@ConfigurationProperties(prefix = "iecmp")
public class IecmpProperties {

    /** 跨域配置。 */
    private Cors cors = new Cors();

    /** 鉴权配置。 */
    private Auth auth = new Auth();

    @Data
    public static class Cors {

        /** 允许的前端来源，逗号分隔。 */
        private List<String> allowedOrigins = new ArrayList<>();
    }

    @Data
    public static class Auth {

        /** 无需登录即可访问的路径（不含 context-path），{@code **} 表示任意多级。 */
        private List<String> ignoreUrls = new ArrayList<>();

        /**
         * 令牌可选路径：带了令牌就解析并写入上下文，没带也放行。
         *
         * <p>对应接口文档中「是否需令牌 = 可选」的接口，如学长学姐展板公示、
         * 前台展示视频列表。
         */
        private List<String> optionalUrls = new ArrayList<>();
    }
}
