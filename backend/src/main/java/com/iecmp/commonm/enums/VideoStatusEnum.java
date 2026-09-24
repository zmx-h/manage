package com.iecmp.commonm.enums;

import lombok.Getter;

/**
 * 视频上架状态（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code sys_video.status}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum VideoStatusEnum {

    /** 0 下架。 */
    OFFLINE(0, "下架"),

    /** 1 上架。 */
    ONLINE(1, "上架");

    private final Integer value;
    private final String label;

    VideoStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static VideoStatusEnum of(Integer value) {
        if (value != null) {
            for (VideoStatusEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        VideoStatusEnum item = of(value);
        return item == null ? null : item.label;
    }
}
