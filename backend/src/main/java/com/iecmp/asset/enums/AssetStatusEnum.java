package com.iecmp.asset.enums;

import lombok.Getter;

/**
 * 资产状态（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code asset_info.status}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum AssetStatusEnum {

    /** 1 在用。 */
    IN_USE(1, "在用"),

    /** 2 闲置。 */
    IDLE(2, "闲置"),

    /** 3 维修中。 */
    REPAIRING(3, "维修中"),

    /** 4 已借出。 */
    BORROWED(4, "已借出"),

    /** 5 已报废。 */
    SCRAPPED(5, "已报废");

    private final Integer value;
    private final String label;

    AssetStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static AssetStatusEnum of(Integer value) {
        if (value != null) {
            for (AssetStatusEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        AssetStatusEnum item = of(value);
        return item == null ? null : item.label;
    }
}
