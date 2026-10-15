package com.iecmp.asset.enums;

import lombok.Getter;

/**
 * 资产变动类型（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code asset_record.record_type}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum AssetRecordTypeEnum {

    /** 1 领用。 */
    RECEIVE(1, "领用"),

    /** 2 借用。 */
    BORROW(2, "借用"),

    /** 3 归还。 */
    RETURN(3, "归还"),

    /** 4 维修。 */
    REPAIR(4, "维修"),

    /** 5 报废。 */
    SCRAP(5, "报废"),

    /** 6 调拨。 */
    ALLOCATION(6, "调拨"),

    /** 7 盘点。 */
    INVENTORY(7, "盘点");

    private final Integer value;
    private final String label;

    AssetRecordTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static AssetRecordTypeEnum of(Integer value) {
        if (value != null) {
            for (AssetRecordTypeEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        AssetRecordTypeEnum item = of(value);
        return item == null ? null : item.label;
    }
}
