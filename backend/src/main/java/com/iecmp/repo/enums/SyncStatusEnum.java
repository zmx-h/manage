package com.iecmp.repo.enums;

import lombok.Getter;

/**
 * 采集状态（接口文档 16.2 节）。
 *
 * <p>对应字段：{@code repo_account.sync_status}。
 * <p>对应字段：{@code repo_project.sync_status}。
 *
 * <p>可直接用 {@code @EnumValue}（MyBatis-Plus）或自定义 TypeHandler 与数据库 integer 互转；
 * 前端展示文案也可由 {@code sys_dict} 数据字典（接口 8-04）下发。
 *
 * @author IECMP 后端组
 */
@Getter
public enum SyncStatusEnum {

    /** 0 未采集。 */
    NOT_SYNCED(0, "未采集"),

    /** 1 采集中。 */
    SYNCING(1, "采集中"),

    /** 2 采集正常。 */
    SYNC_OK(2, "采集正常"),

    /** 3 采集失败。 */
    SYNC_FAILED(3, "采集失败");

    private final Integer value;
    private final String label;

    SyncStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    /** 按值反查，查不到返回 null。 */
    public static SyncStatusEnum of(Integer value) {
        if (value != null) {
            for (SyncStatusEnum item : values()) {
                if (item.value.equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取值对应的展示文案。 */
    public static String labelOf(Integer value) {
        SyncStatusEnum item = of(value);
        return item == null ? null : item.label;
    }
}
