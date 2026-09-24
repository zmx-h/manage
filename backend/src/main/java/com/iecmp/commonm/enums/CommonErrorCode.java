package com.iecmp.commonm.enums;

import lombok.Getter;

/**
 * 八 · 文件与通用支撑 业务错误码（接口文档 16.1 节）。
 *
 * <p>号段：{@code 80001 ~ 90000}。
 * 抛出方式：{@code throw new BusinessException(CommonErrorCode.XXX.getCode(), CommonErrorCode.XXX.getMsg())}；
 * 或使用 {@code Result.fail(code, msg)}。
 *
 * <p><b>常量命名</b>：统一为 {@code E<错误码>}，保证与接口文档一一对应且不重名；
 * 实现时如需更易读的常量名，可重命名并使用 IDE 重构，数值不可变更。
 *
 * @author IECMP 后端组
 */
@Getter
public enum CommonErrorCode {

    /** 上传文件为空 */
    E80001(80001, "上传文件为空"),

    /** 文件类型不支持 */
    E80002(80002, "文件类型不支持"),

    /** 文件大小超出限制 */
    E80003(80003, "文件大小超出限制"),

    /** 文件不存在 */
    E80004(80004, "文件不存在"),

    /** 文件已被业务引用，无法删除 */
    E80005(80005, "文件已被业务引用，无法删除"),

    /** 只能删除本人上传的文件 */
    E80006(80006, "只能删除本人上传的文件"),

    /** 公告不存在 */
    E80007(80007, "公告不存在"),

    /** 定时发布时间必须晚于当前时间 */
    E80009(80009, "定时发布时间必须晚于当前时间"),

    /** 视频记录不存在 */
    E80010(80010, "视频记录不存在"),

    /** 播放地址格式不正确 */
    E80011(80011, "播放地址格式不正确"),

    /** 消息不存在 */
    E80012(80012, "消息不存在"),

    /** 无权操作该消息 */
    E80013(80013, "无权操作该消息");

    private final Integer code;
    private final String msg;

    CommonErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /** 按错误码反查，查不到返回 null。 */
    public static CommonErrorCode of(Integer code) {
        if (code != null) {
            for (CommonErrorCode item : values()) {
                if (item.code.equals(code)) {
                    return item;
                }
            }
        }
        return null;
    }

    /** 取错误文案，查不到返回兜底文案。 */
    public static String msgOf(Integer code) {
        CommonErrorCode item = of(code);
        return item == null ? "未知业务错误" : item.msg;
    }
}
