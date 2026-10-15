package com.iecmp.repo.client;

import com.iecmp.repo.enums.RepoErrorCode;
import com.iecmp.repo.properties.RepoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 平台客户端注册表：按 {@code repo_account.platform} 取到对应的 {@link RepoPlatformClient}。
 *
 * <p>Spring 会把所有 {@code RepoPlatformClient} 实现注入进来，新增平台只要加一个
 * {@code @Component} 实现类即可，本类无需改动。
 *
 * <p>只配置了 {@code repo.platform} 却没写对应客户端的平台，在启动日志里给出 WARN——
 * 否则成员绑定时才会报错，排查成本高。
 */
@Slf4j
@Component
public class RepoPlatformClientRegistry {

    private final Map<String, RepoPlatformClient> clients = new HashMap<>();

    public RepoPlatformClientRegistry(List<RepoPlatformClient> clientList, RepoProperties repoProperties) {
        for (RepoPlatformClient client : clientList) {
            clients.put(client.platform().toLowerCase(), client);
        }
        for (RepoProperties.Platform platform : repoProperties.getPlatform()) {
            if (platform.getCode() != null && !clients.containsKey(platform.getCode().toLowerCase())) {
                log.warn("平台 {} 已配置在 repo.platform 中，但没有对应的 RepoPlatformClient 实现，绑定该平台会失败",
                        platform.getCode());
            }
        }
    }

    /**
     * 取平台客户端。
     *
     * @throws RepoApiException 平台不支持时抛「调用失败」，与「令牌无效」区分开
     */
    public RepoPlatformClient get(String platform) {
        RepoPlatformClient client = platform == null ? null : clients.get(platform.toLowerCase());
        if (client == null) {
            throw new RepoApiException(RepoApiException.API_FAILED, "不支持的代码托管平台：" + platform);
        }
        return client;
    }

    /** 判断平台是否有客户端实现。 */
    public boolean supports(String platform) {
        return platform != null && clients.containsKey(platform.toLowerCase());
    }

    /** 业务错误码映射：令牌无效 → 20003，其它 → 20007（接口文档 8.2.2 / 8.2.4）。 */
    public static RepoErrorCode toErrorCode(RepoApiException e) {
        return e.isInvalidToken() ? RepoErrorCode.E20003 : RepoErrorCode.E20007;
    }
}
