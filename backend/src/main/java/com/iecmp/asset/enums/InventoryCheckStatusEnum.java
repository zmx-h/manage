package com.iecmp.asset.enums;

import lombok.Getter;

/**
 * 盘点明细结果（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code asset_inventory_item.check_status}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum InventoryCheckStatusEnum {

    /** 0 未盘点。 */
    UNCHECKED(0, "未盘点"),

    /** 1 正常。 */
    NORMAL(1, "正常"),

    /** 2 盘亏。 */
    LOSS(2, "盘亏"),

    /** 3 位置不符。 */
    MISMATCH(3, "位置不符");

    private final Integer value;
    private final String label;

    InventoryCheckStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static InventoryCheckStatusEnum of(Integer value) {
        if (value != null) {
            for (InventoryCheckStatusEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        InventoryCheckStatusEnum item = of(value);
        return item == null ? null : item.label;
    }
}
