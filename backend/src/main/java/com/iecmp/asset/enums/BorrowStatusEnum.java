package com.iecmp.asset.enums;

import lombok.Getter;

/**
 * 借用记录状态（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code asset_borrow.status}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum BorrowStatusEnum {

    /** 0 待审核。 */
    PENDING(0, "待审核"),

    /** 1 已通过。 */
    APPROVED(1, "已通过"),

    /** 2 已驳回。 */
    REJECTED(2, "已驳回"),

    /** 3 已归还。 */
    RETURNED(3, "已归还"),

    /** 4 已逾期。 */
    OVERDUE(4, "已逾期");

    private final Integer value;
    private final String label;

    BorrowStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static BorrowStatusEnum of(Integer value) {
        if (value != null) {
            for (BorrowStatusEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        BorrowStatusEnum item = of(value);
        return item == null ? null : item.label;
    }
}
