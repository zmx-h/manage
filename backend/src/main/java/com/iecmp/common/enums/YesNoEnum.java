package com.iecmp.common.enums;

import lombok.Getter;

/**
 * 通用「是 / 否」枚举（接口文档 2.5 节：布尔语义统一用 integer，1 是、0 否）。
 */
@Getter
public enum YesNoEnum {

    /** 否。 */
    NO(0, "否"),

    /** 是。 */
    YES(1, "是");

    private final Integer value;
    private final String label;

    YesNoEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 是否为「是」。 */
    public static boolean isYes(Integer value) {
        return YES.value.equals(value);
    }
}
