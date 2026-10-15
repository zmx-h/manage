package com.iecmp.commonm.enums;

import lombok.Getter;

/**
 * 站内消息类型（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code sys_message.msg_type}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum MessageTypeEnum {

    /** 1 值日提醒。 */
    DUTY_REMINDER(1, "值日提醒"),

    /** 2 审核结果。 */
    AUDIT_RESULT(2, "审核结果"),

    /** 3 系统通知。 */
    SYSTEM_NOTICE(3, "系统通知");

    private final Integer value;
    private final String label;

    MessageTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static MessageTypeEnum of(Integer value) {
        if (value != null) {
            for (MessageTypeEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        MessageTypeEnum item = of(value);
        return item == null ? null : item.label;
    }
}
