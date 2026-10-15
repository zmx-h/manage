package com.iecmp.budget.enums;

import lombok.Getter;

/**
 * 预算科目类别（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code budget_item.category}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum BudgetItemCategoryEnum {

    /** 1 设备采购。 */
    EQUIPMENT(1, "设备采购"),

    /** 2 耗材。 */
    CONSUMABLE(2, "耗材"),

    /** 3 活动。 */
    ACTIVITY(3, "活动"),

    /** 4 差旅。 */
    TRAVEL(4, "差旅"),

    /** 5 其他。 */
    OTHER(5, "其他");

    private final Integer value;
    private final String label;

    BudgetItemCategoryEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static BudgetItemCategoryEnum of(Integer value) {
        if (value != null) {
            for (BudgetItemCategoryEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        BudgetItemCategoryEnum item = of(value);
        return item == null ? null : item.label;
    }
}
