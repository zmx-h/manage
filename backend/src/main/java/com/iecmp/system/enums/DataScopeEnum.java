package com.iecmp.system.enums;

import lombok.Getter;

/**
 * 角色数据范围（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code sys_role.data_scope}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum DataScopeEnum {

    /** 1 全部数据。 */
    ALL_DATA(1, "全部数据"),

    /** 2 本人数据。 */
    SELF_DATA(2, "本人数据");

    private final Integer value;
    private final String label;

    DataScopeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static DataScopeEnum of(Integer value) {
        if (value != null) {
            for (DataScopeEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        DataScopeEnum item = of(value);
        return item == null ? null : item.label;
    }
}
