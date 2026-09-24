package com.iecmp.duty.enums;

import lombok.Getter;

/**
 * 七 · 值日排班 业务错误码（接口文档 16.1 节）。
 *
 * <p>号段：{@code 70001 ~ 80000}。
 * 抛出方式：{@code throw new BusinessException(DutyErrorCode.XXX.getCode(), DutyErrorCode.XXX.getMsg())}；
 * 或使用 {@code Result.fail(code, msg)}。
 *
 * <p><b>常量命名</b>：统一为 {@code E<错误码>}，保证与接口文档一一对应且不重名；
 * 实现时如需更易读的常量名，可重命名并使用 IDE 重构，数值不可变更。
 *
 * @author IECMP 后端组
 */
@Getter
public enum DutyErrorCode {

    /** 排班计划不存在 */
    E70001(70001, "排班计划不存在"),

    /** 该计划已发布，不可重复发布 */
    E70002(70002, "该计划已发布，不可重复发布"),

    /** 计划起止日期不合法 */
    E70003(70003, "计划起止日期不合法"),

    /** 参与轮转的成员为空，无法生成排班 */
    E70004(70004, "参与轮转的成员为空，无法生成排班"),

    /** 该计划尚无值日安排，无法发布 */
    E70005(70005, "该计划尚无值日安排，无法发布"),

    /** 值日安排不存在 */
    E70006(70006, "值日安排不存在"),

    /** 只能确认本人的值日 */
    E70007(70007, "只能确认本人的值日"),

    /** 该值日已完成确认 */
    E70008(70008, "该值日已完成确认"),

    /** 换班申请不存在 */
    E70009(70009, "换班申请不存在"),

    /** 不能与自己换班 */
    E70010(70010, "不能与自己换班"),

    /** 目标值日不存在或不属于对方 */
    E70011(70011, "目标值日不存在或不属于对方"),

    /** 仅换班对象可处理该申请 */
    E70012(70012, "仅换班对象可处理该申请"),

    /** 该成员当日已有值日安排 */
    E70014(70014, "该成员当日已有值日安排");

    private final Integer code;
    private final String msg;

    DutyErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /** 按错误码反查，查不到返回 null。 */
    public static DutyErrorCode of(Integer code) {
        if (code != null) {
            for (DutyErrorCode item : values()) {
                if (item.code.equals(code)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取错误文案，查不到返回兜底文案。 */
    public static String msgOf(Integer code) {
        DutyErrorCode item = of(code);
        return item == null ? "未知业务错误" : item.msg;
    }
}
