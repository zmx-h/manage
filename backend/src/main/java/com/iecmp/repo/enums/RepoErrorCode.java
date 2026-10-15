package com.iecmp.repo.enums;

import lombok.Getter;

/**
 * 二 · 代码仓库关联 业务错误码（接口文档 16.1 节）。
 *
 * <p>号段：{@code 20001 ~ 30000}。
 * 抛出方式：{@code throw new BusinessException(RepoErrorCode.XXX.getCode(), RepoErrorCode.XXX.getMsg())}；
 * 或使用 {@code Result.fail(code, msg)}。
 *
 * <p><b>常量命名</b>：统一为 {@code E<错误码>}，保证与接口文档一一对应且不重名；
 * 实现时如需更易读的常量名，可重命名并使用 IDE 重构，数值不可变更。
 *
 * @author IECMP 后端组
 */
@Getter
public enum RepoErrorCode {

    /** 托管账户不存在 */
    E20001(20001, "托管账户不存在"),

    /** 该账户已绑定，请勿重复绑定 */
    E20002(20002, "该账户已绑定，请勿重复绑定"),

    /** 访问令牌无效或已过期 */
    E20003(20003, "访问令牌无效或已过期"),

    /** 仓库不存在 */
    E20004(20004, "仓库不存在"),

    /** 该账户或仓库不属于当前用户 */
    E20005(20005, "该账户或仓库不属于当前用户"),

    /** 同步任务执行中，请稍后再试 */
    E20006(20006, "同步任务执行中，请稍后再试"),

    /** 第三方平台接口调用失败 */
    E20007(20007, "第三方平台接口调用失败"),

    /** 未选择要同步的账户 */
    E20008(20008, "未选择要同步的账户");

    private final Integer code;
    private final String msg;

    RepoErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /** 按错误码反查，查不到返回 null。 */
    public static RepoErrorCode of(Integer code) {
        if (code != null) {
            for (RepoErrorCode item : values()) {
                if (item.code.equals(code)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取错误文案，查不到返回兜底文案。 */
    public static String msgOf(Integer code) {
        RepoErrorCode item = of(code);
        return item == null ? "未知业务错误" : item.msg;
    }
}
