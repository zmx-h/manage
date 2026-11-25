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

    /** 注册入口配置（接口 1-22）。 */
    private Register register = new Register();

    /** 招新报名与邮箱注册码配置（接口 1-30 ~ 1-35）。 */
    private Recruit recruit = new Recruit();

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

    @Data
    public static class Register {

        /** 1 开启邀请码注册，0 关闭。 */
        private Integer inviteEnabled = 1;

        /** 1 需要图形验证码，0 关闭。 */
        private Integer captchaEnabled = 0;

        /** 注册页提示文案。 */
        private String tips = "";
    }

    @Data
    public static class Recruit {

        /** 1 开放外部招新网页报名，0 关闭。 */
        private Integer enabled = 1;

        /** 注册码有效期（小时）。 */
        private Integer codeExpireHours = 24;

        /** 单个注册码允许的连续输错次数。 */
        private Integer codeMaxAttempts = 5;

        /** 同一报名记录两次发码的最小间隔（秒）。 */
        private Integer codeResendSeconds = 60;

        /** 同一 IP 每小时最多提交报名次数。 */
        private Integer applyLimitPerHour = 5;

        /** 默认通过分（接口 1-34 未显式传 passScore 时使用）。 */
        private Integer passScore = 60;

        /** 注册码哈希用的 pepper，必须与部署环境一致且不可外泄。 */
        private String codePepper = "dev-only-change-me";

        /** 1 实际发信；0 只写日志。 */
        private Integer mailEnabled = 0;

        /** 发件人显示名称。 */
        private String mailFromName = "IECMP 创业中心";
    }
}
