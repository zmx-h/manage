package com.iecmp.honor.enums;

import lombok.Getter;

/**
 * 六 · 学长学姐展板 业务错误码（接口文档 16.1 节）。
 *
 * <p>号段：{@code 60001 ~ 70000}。
 * 抛出方式：{@code throw new BusinessException(HonorErrorCode.XXX.getCode(), HonorErrorCode.XXX.getMsg())}；
 * 或使用 {@code Result.fail(code, msg)}。
 *
 * <p><b>常量命名</b>：统一为 {@code E<错误码>}，保证与接口文档一一对应且不重名；
 * 实现时如需更易读的常量名，可重命名并使用 IDE 重构，数值不可变更。
 *
 * @author IECMP 后端组
 */
@Getter
public enum HonorErrorCode {

    /** 学长学姐信息不存在 */
    E60001(60001, "学长学姐信息不存在"),

    /** 该信息已审核，无法重复审核 */
    E60002(60002, "该信息已审核，无法重复审核"),

    /** 无权撤回该提交 */
    E60003(60003, "无权撤回该提交"),

    /** 已审核通过的信息不可撤回 */
    E60004(60004, "已审核通过的信息不可撤回"),

    /** 荣誉记录不存在 */
    E60005(60005, "荣誉记录不存在"),

    /** 当前状态不允许上板 */
    E60006(60006, "当前状态不允许上板"),

    /** 当前状态不允许修改 */
    E60008(60008, "当前状态不允许修改");

    private final Integer code;
    private final String msg;

    HonorErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /** 按错误码反查，查不到返回 null。 */
    public static HonorErrorCode of(Integer code) {
        if (code != null) {
            for (HonorErrorCode item : values()) {
                if (item.code.equals(code)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取错误文案，查不到返回兜底文案。 */
    public static String msgOf(Integer code) {
        HonorErrorCode item = of(code);
        return item == null ? "未知业务错误" : item.msg;
    }
}
