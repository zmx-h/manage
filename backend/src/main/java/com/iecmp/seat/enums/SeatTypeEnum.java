package com.iecmp.seat.enums;

import lombok.Getter;

/**
 * 工位类型（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code seat_info.seat_type}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum SeatTypeEnum {

    /** 1 工位。 */
    WORKSTATION(1, "工位"),

    /** 2 会议室。 */
    MEETING_ROOM(2, "会议室"),

    /** 3 储物柜。 */
    LOCKER(3, "储物柜"),

    /** 4 其他。 */
    OTHER(4, "其他");

    private final Integer value;
    private final String label;

    SeatTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static SeatTypeEnum of(Integer value) {
        if (value != null) {
            for (SeatTypeEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        SeatTypeEnum item = of(value);
        return item == null ? null : item.label;
    }
}
