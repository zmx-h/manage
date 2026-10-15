package com.iecmp.commonm.enums;

import lombok.Getter;

/**
 * 公告类型（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code sys_notice.notice_type}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum NoticeTypeEnum {

    /** 1 中心公告。 */
    CENTER_NOTICE(1, "中心公告"),

    /** 2 制度。 */
    REGULATION(2, "制度"),

    /** 3 活动。 */
    ACTIVITY(3, "活动"),

    /** 4 其他。 */
    OTHER(4, "其他");

    private final Integer value;
    private final String label;

    NoticeTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static NoticeTypeEnum of(Integer value) {
        if (value != null) {
            for (NoticeTypeEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        NoticeTypeEnum item = of(value);
        return item == null ? null : item.label;
    }
}
