package com.iecmp.system.enums;

import lombok.Getter;

/**
 * 招新报名状态（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code sys_recruit_apply.status}。
 */
@Getter
public enum RecruitApplyStatusEnum {

    /** 0 待考核。 */
    PENDING_ASSESS(0, "待考核"),

    /** 1 已通过，待发码。 */
    PASSED_PENDING_SEND(1, "已通过待发码"),

    /** 2 未通过。 */
    FAILED(2, "未通过"),

    /** 3 已发码。 */
    CODE_SENT(3, "已发码"),

    /** 4 已注册。 */
    REGISTERED(4, "已注册"),

    /** 5 注册码已过期。 */
    CODE_EXPIRED(5, "注册码已过期");

    private final Integer value;
    private final String label;

    RecruitApplyStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static RecruitApplyStatusEnum of(Integer value) {
        if (value != null) {
            for (RecruitApplyStatusEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        RecruitApplyStatusEnum item = of(value);
        return item == null ? null : item.label;
    }
}
