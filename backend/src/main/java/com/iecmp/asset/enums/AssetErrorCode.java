package com.iecmp.asset.enums;

import lombok.Getter;

/**
 * 四 · 部门财产 业务错误码（接口文档 16.1 节）。
 *
 * <p>号段：{@code 40001 ~ 50000}。
 * 抛出方式：{@code throw new BusinessException(AssetErrorCode.XXX.getCode(), AssetErrorCode.XXX.getMsg())}；
 * 或使用 {@code Result.fail(code, msg)}。
 *
 * <p><b>常量命名</b>：统一为 {@code E<错误码>}，保证与接口文档一一对应且不重名；
 * 实现时如需更易读的常量名，可重命名并使用 IDE 重构，数值不可变更。
 *
 * @author IECMP 后端组
 */
@Getter
public enum AssetErrorCode {

    /** 资产不存在 */
    E40001(40001, "资产不存在"),

    /** 资产编号已存在 */
    E40002(40002, "资产编号已存在"),

    /** 资产分类不存在 */
    E40003(40003, "资产分类不存在"),

    /** 该分类下存在资产，无法删除 */
    E40004(40004, "该分类下存在资产，无法删除"),

    /** 该分类存在子分类，无法删除 */
    E40005(40005, "该分类存在子分类，无法删除"),

    /** 资产状态不允许该操作 */
    E40006(40006, "资产状态不允许该操作"),

    /** 该资产当前已被借出 */
    E40007(40007, "该资产当前已被借出"),

    /** 借用记录不存在 */
    E40008(40008, "借用记录不存在"),

    /** 只能归还本人借用的资产 */
    E40009(40009, "只能归还本人借用的资产"),

    /** 该借用记录状态不允许归还 */
    E40010(40010, "该借用记录状态不允许归还"),

    /** 领用记录无需归还 */
    E40011(40011, "领用记录无需归还"),

    /** 盘点任务不存在 */
    E40012(40012, "盘点任务不存在"),

    /** 该盘点任务已结束 */
    E40013(40013, "该盘点任务已结束"),

    /** 盘点明细不存在 */
    E40014(40014, "盘点明细不存在"),

    /** 资产已报废，无法借用 */
    E40015(40015, "资产已报废，无法借用"),

    /** 导出数据量超出上限 */
    E40016(40016, "导出数据量超出上限"),

    /** 该借用记录状态不允许审核 */
    E40017(40017, "该借用记录状态不允许审核"),

    /** 分类编码已存在 */
    E40018(40018, "分类编码已存在");

    private final Integer code;
    private final String msg;

    AssetErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /** 按错误码反查，查不到返回 null。 */
    public static AssetErrorCode of(Integer code) {
        if (code != null) {
            for (AssetErrorCode item : values()) {
                if (item.code.equals(code)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取错误文案，查不到返回兜底文案。 */
    public static String msgOf(Integer code) {
        AssetErrorCode item = of(code);
        return item == null ? "未知业务错误" : item.msg;
    }
}
