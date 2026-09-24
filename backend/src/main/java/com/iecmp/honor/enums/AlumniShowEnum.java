package com.iecmp.honor.enums;

import lombok.Getter;

/**
 * 展板是否上板（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code honor_alumni.is_show}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum AlumniShowEnum {

    /** 0 下板。 */
    OFF_BOARD(0, "下板"),

    /** 1 上板。 */
    ON_BOARD(1, "上板");

    private final Integer value;
    private final String label;

    AlumniShowEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static AlumniShowEnum of(Integer value) {
        if (value != null) {
            for (AlumniShowEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        AlumniShowEnum item = of(value);
        return item == null ? null : item.label;
    }
}
