package com.iecmp.budget.enums;

import lombok.Getter;

/**
 * 预算周期状态（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code budget_period.status}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum BudgetPeriodStatusEnum {

    /** 0 草稿。 */
    DRAFT(0, "草稿"),

    /** 1 执行中。 */
    RUNNING(1, "执行中"),

    /** 2 已结束。 */
    FINISHED(2, "已结束");

    private final Integer value;
    private final String label;

    BudgetPeriodStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static BudgetPeriodStatusEnum of(Integer value) {
        if (value != null) {
            for (BudgetPeriodStatusEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        BudgetPeriodStatusEnum item = of(value);
        return item == null ? null : item.label;
    }
}
