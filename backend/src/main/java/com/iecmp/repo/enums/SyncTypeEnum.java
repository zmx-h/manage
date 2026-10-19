package com.iecmp.repo.enums;

import lombok.Getter;

/**
 * 同步触发方式（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code repo_sync_log.sync_type}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author WanZiyu
 */
@Getter
public enum SyncTypeEnum {

    /** 1 定时自动。 */
    SCHEDULED(1, "定时自动"),

    /** 2 管理员手动补采。 */
    MANUAL(2, "管理员手动补采");

    private final Integer value;
    private final String label;

    SyncTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static SyncTypeEnum of(Integer value) {
        if (value != null) {
            for (SyncTypeEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        SyncTypeEnum item = of(value);
        return item == null ? null : item.label;
    }
}
