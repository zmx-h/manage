package com.iecmp.asset.enums;

import lombok.Getter;

/**
 * 资产来源（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code asset_info.source_type}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum AssetSourceTypeEnum {

    /** 1 采购。 */
    PURCHASE(1, "采购"),

    /** 2 捐赠。 */
    DONATION(2, "捐赠"),

    /** 3 调拨。 */
    ALLOCATION(3, "调拨");

    private final Integer value;
    private final String label;

    AssetSourceTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static AssetSourceTypeEnum of(Integer value) {
        if (value != null) {
            for (AssetSourceTypeEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        AssetSourceTypeEnum item = of(value);
        return item == null ? null : item.label;
    }
}
