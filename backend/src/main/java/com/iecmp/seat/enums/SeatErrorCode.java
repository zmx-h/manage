package com.iecmp.seat.enums;

import lombok.Getter;

/**
 * 五 · 部门工位 业务错误码（接口文档 16.1 节）。
 *
 * <p>号段：{@code 50001 ~ 60000}。
 * 抛出方式：{@code throw new BusinessException(SeatErrorCode.XXX.getCode(), SeatErrorCode.XXX.getMsg())}；
 * 或使用 {@code Result.fail(code, msg)}。
 *
 * <p><b>常量命名</b>：统一为 {@code E<错误码>}，保证与接口文档一一对应且不重名；
 * 实现时如需更易读的常量名，可重命名并使用 IDE 重构，数值不可变更。
 *
 * @author IECMP 后端组
 */
@Getter
public enum SeatErrorCode {

    /** 工位区域不存在 */
    E50001(50001, "工位区域不存在"),

    /** 该区域下存在工位，无法删除 */
    E50002(50002, "该区域下存在工位，无法删除"),

    /** 工位不存在 */
    E50003(50003, "工位不存在"),

    /** 工位编号在该区域内已存在 */
    E50004(50004, "工位编号在该区域内已存在"),

    /** 该工位已被占用 */
    E50005(50005, "该工位已被占用"),

    /** 工位申请不存在 */
    E50006(50006, "工位申请不存在"),

    /** 无权访问该工位申请 */
    E50007(50007, "无权访问该工位申请"),

    /** 当前状态不允许该操作 */
    E50008(50008, "当前状态不允许该操作"),

    /** 座位调换申请不存在 */
    E50009(50009, "座位调换申请不存在"),

    /** 不能与自己调换工位 */
    E50010(50010, "不能与自己调换工位"),

    /** 对调方工位不存在或已释放 */
    E50011(50011, "对调方工位不存在或已释放"),

    /** 仅对调方可处理该申请 */
    E50012(50012, "仅对调方可处理该申请"),

    /** 调换申请前置状态不满足 */
    E50013(50013, "调换申请前置状态不满足"),

    /** 平面图底图尺寸未设置 */
    E50014(50014, "平面图底图尺寸未设置"),

    /** 源工位当前无人，无法拖动 */
    E50015(50015, "源工位当前无人，无法拖动"),

    /** 源工位与目标工位不能相同 */
    E50016(50016, "源工位与目标工位不能相同"),

    /** 目标工位为预留或维修中，不可占用 */
    E50017(50017, "目标工位为预留或维修中，不可占用");

    private final Integer code;
    private final String msg;

    SeatErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /** 按错误码反查，查不到返回 null。 */
    public static SeatErrorCode of(Integer code) {
        if (code != null) {
            for (SeatErrorCode item : values()) {
                if (item.code.equals(code)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取错误文案，查不到返回兜底文案。 */
    public static String msgOf(Integer code) {
        SeatErrorCode item = of(code);
        return item == null ? "未知业务错误" : item.msg;
    }
}
