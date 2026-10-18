package com.iecmp.repo.client.model;

import lombok.Data;

/**
 * 第三方平台的账号信息，对应令牌校验接口（Gitee / GitHub 的 {@code GET /user}）。
 *
 * <p>绑定账户时用它一次拿到两样东西：<b>令牌是否有效</b>，以及写库用的
 * {@code accountName}（平台 login）与 {@code avatarUrl}。
 */
@Data
public class RepoUserInfo {

    /** 平台账号名（login），写入 {@code repo_account.account_name}。 */
    private String login;

    /** 平台昵称 / 姓名，仅用于日志排查，不落库。 */
    private String name;

    /** 平台头像外链，写入 {@code repo_account.avatar_url}。 */
    private String avatarUrl;
}
