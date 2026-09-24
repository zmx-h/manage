package com.iecmp.duty.enums;

import lombok.Getter;

/**
 * 排班方式（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code duty_plan.plan_type}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum DutyPlanTypeEnum {

    /** 1 按周轮转。 */
    WEEKLY_ROTATION(1, "按周轮转"),

    /** 2 分组轮转。 */
    GROUP_ROTATION(2, "分组轮转"),

    /** 3 手工指定。 */
    MANUAL_ASSIGN(3, "手工指定");

    private final Integer value;
    private final String label;

    DutyPlanTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static DutyPlanTypeEnum of(Integer value) {
        if (value != null) {
            for (DutyPlanTypeEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        DutyPlanTypeEnum item = of(value);
        return item == null ? null : item.label;
    }
}
