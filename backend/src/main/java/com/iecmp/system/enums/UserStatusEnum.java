package com.iecmp.system.enums;

import lombok.Getter;

/**
 * 用户状态（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code sys_user.status}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum UserStatusEnum {

    /** 0 禁用。 */
    DISABLED(0, "禁用"),

    /** 1 启用。 */
    ENABLED(1, "启用");

    private final Integer value;
    private final String label;

    UserStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static UserStatusEnum of(Integer value) {
        if (value != null) {
            for (UserStatusEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        UserStatusEnum item = of(value);
        return item == null ? null : item.label;
    }
}
