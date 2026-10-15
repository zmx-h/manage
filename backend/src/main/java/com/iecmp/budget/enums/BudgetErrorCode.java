package com.iecmp.budget.enums;

import lombok.Getter;

/**
 * 三 · 部门预算 业务错误码（接口文档 16.1 节）。
 *
 * <p>号段：{@code 30001 ~ 40000}。
 * 抛出方式：{@code throw new BusinessException(BudgetErrorCode.XXX.getCode(), BudgetErrorCode.XXX.getMsg())}；
 * 或使用 {@code Result.fail(code, msg)}。
 *
 * <p><b>常量命名</b>：统一为 {@code E<错误码>}，保证与接口文档一一对应且不重名；
 * 实现时如需更易读的常量名，可重命名并使用 IDE 重构，数值不可变更。
 *
 * @author IECMP 后端组
 */
@Getter
public enum BudgetErrorCode {

    /** 预算周期不存在 */
    E30001(30001, "预算周期不存在"),

    /** 该预算周期已结束，无法提交申请 */
    E30002(30002, "该预算周期已结束，无法提交申请"),

    /** 预算科目不存在 */
    E30003(30003, "预算科目不存在"),

    /** 该科目不属于所选预算周期 */
    E30004(30004, "该科目不属于所选预算周期"),

    /** 申请金额必须大于 0 */
    E30005(30005, "申请金额必须大于 0"),

    /** 申请金额超出科目剩余额度 */
    E30006(30006, "申请金额超出科目剩余额度"),

    /** 预算申请不存在 */
    E30007(30007, "预算申请不存在"),

    /** 无权访问该预算申请 */
    E30008(30008, "无权访问该预算申请"),

    /** 当前状态不允许撤销 */
    E30009(30009, "当前状态不允许撤销"),

    /** 该申请已审核，无法重复审核 */
    E30010(30010, "该申请已审核，无法重复审核"),

    /** 支出金额超出周期剩余额度 */
    E30011(30011, "支出金额超出周期剩余额度"),

    /** 支出记录不存在 */
    E30012(30012, "支出记录不存在"),

    /** 该科目已被申请或支出引用，无法删除 */
    E30013(30013, "该科目已被申请或支出引用，无法删除"),

    /** 该周期下存在科目或申请，无法删除 */
    E30014(30014, "该周期下存在科目或申请，无法删除"),

    /** 该预算周期已结束，无法登记支出 */
    E30015(30015, "该预算周期已结束，无法登记支出"),

    /** 周期总额度不得低于已使用额度 */
    E30016(30016, "周期总额度不得低于已使用额度"),

    /** 科目额度不得低于已使用额度 */
    E30017(30017, "科目额度不得低于已使用额度"),

    /** 预算周期状态流转不合法 */
    E30018(30018, "预算周期状态流转不合法"),

    /** 存在待审核的申请，无法结束周期 */
    E30019(30019, "存在待审核的申请，无法结束周期"),

    /** 该功能在 v1.0 未开放 */
    E30020(30020, "该功能在 v1.0 未开放");

    private final Integer code;
    private final String msg;

    BudgetErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /** 按错误码反查，查不到返回 null。 */
    public static BudgetErrorCode of(Integer code) {
        if (code != null) {
            for (BudgetErrorCode item : values()) {
                if (item.code.equals(code)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取错误文案，查不到返回兜底文案。 */
    public static String msgOf(Integer code) {
        BudgetErrorCode item = of(code);
        return item == null ? "未知业务错误" : item.msg;
    }
}
