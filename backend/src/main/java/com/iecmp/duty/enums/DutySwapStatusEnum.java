package com.iecmp.duty.enums;

import lombok.Getter;

/**
 * 换班申请状态（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code duty_swap.status}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum DutySwapStatusEnum {

    /** 0 待确认。 */
    PENDING_CONFIRM(0, "待确认"),

    /** 1 已同意。 */
    AGREED(1, "已同意"),

    /** 2 已拒绝。 */
    TARGET_REJECTED(2, "已拒绝"),

    /** 3 已取消。 */
    CANCELED(3, "已取消");

    private final Integer value;
    private final String label;

    DutySwapStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static DutySwapStatusEnum of(Integer value) {
        if (value != null) {
            for (DutySwapStatusEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        DutySwapStatusEnum item = of(value);
        return item == null ? null : item.label;
    }
}
