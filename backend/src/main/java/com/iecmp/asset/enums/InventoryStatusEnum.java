package com.iecmp.asset.enums;

import lombok.Getter;

/**
 * 盘点任务状态（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code asset_inventory.status}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum InventoryStatusEnum {

    /** 0 进行中。 */
    IN_PROGRESS(0, "进行中"),

    /** 1 已完成。 */
    COMPLETED(1, "已完成");

    private final Integer value;
    private final String label;

    InventoryStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static InventoryStatusEnum of(Integer value) {
        if (value != null) {
            for (InventoryStatusEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        InventoryStatusEnum item = of(value);
        return item == null ? null : item.label;
    }
}
