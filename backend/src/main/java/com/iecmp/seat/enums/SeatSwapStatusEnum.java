package com.iecmp.seat.enums;

import lombok.Getter;

/**
 * 座位调换状态（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code seat_swap.status}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum SeatSwapStatusEnum {

    /** 0 待对方确认。 */
    WAIT_TARGET_CONFIRM(0, "待对方确认"),

    /** 1 对方已同意待审核。 */
    TARGET_AGREED(1, "对方已同意待审核"),

    /** 2 已通过。 */
    APPROVED(2, "已通过"),

    /** 3 已拒绝。 */
    TARGET_REJECTED(3, "已拒绝"),

    /** 4 已取消。 */
    CANCELED(4, "已取消");

    private final Integer value;
    private final String label;

    SeatSwapStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static SeatSwapStatusEnum of(Integer value) {
        if (value != null) {
            for (SeatSwapStatusEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        SeatSwapStatusEnum item = of(value);
        return item == null ? null : item.label;
    }
}
