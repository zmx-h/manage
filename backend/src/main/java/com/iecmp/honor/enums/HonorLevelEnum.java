package com.iecmp.honor.enums;

import lombok.Getter;

/**
 * 荣誉级别（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code honor_record.level}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum HonorLevelEnum {

    /** 1 国家级。 */
    NATIONAL(1, "国家级"),

    /** 2 省级。 */
    PROVINCIAL(2, "省级"),

    /** 3 校级。 */
    SCHOOL(3, "校级"),

    /** 4 企业及其他。 */
    ENTERPRISE(4, "企业及其他");

    private final Integer value;
    private final String label;

    HonorLevelEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static HonorLevelEnum of(Integer value) {
        if (value != null) {
            for (HonorLevelEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        HonorLevelEnum item = of(value);
        return item == null ? null : item.label;
    }
}
