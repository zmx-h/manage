package com.iecmp.seat.enums;

import lombok.Getter;

/**
 * 工位状态（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code seat_info.status}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum SeatStatusEnum {

    /** 1 空闲。 */
    FREE(1, "空闲"),

    /** 2 已占用。 */
    OCCUPIED(2, "已占用"),

    /** 3 预留。 */
    RESERVED(3, "预留"),

    /** 4 维修中。 */
    REPAIRING(4, "维修中");

    private final Integer value;
    private final String label;

    SeatStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static SeatStatusEnum of(Integer value) {
        if (value != null) {
            for (SeatStatusEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        SeatStatusEnum item = of(value);
        return item == null ? null : item.label;
    }
}
