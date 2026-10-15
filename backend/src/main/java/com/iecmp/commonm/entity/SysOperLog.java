package com.iecmp.commonm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 操作日志表：表 {@code sys_oper_log}。
 *
 * <p>所属：模块八 · 通用支撑。字段以《IECMP-数据库设计.md》与
 * {@code docs/sql/IECMP-数据库建表脚本.sql} 为准。
 *
 * <p>本表<b>没有</b>完整审计字段（无 create_by / update_by / update_time / deleted），
 * 因此不继承 {@code BaseEntity}，字段全部单独声明。
 *
 * @author IECMP 后端组
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模块标识，如 system / budget / asset。
     */
    @TableField("module")
    private String module;

    /**
     * 操作类型，如 add / edit / remove / audit。
     */
    @TableField("operation")
    private String operation;

    /**
     * 请求地址。
     */
    @TableField("request_uri")
    private String requestUri;

    /**
     * 请求方法：GET/POST/PUT/DELETE。
     */
    @TableField("request_method")
    private String requestMethod;

    /**
     * 请求参数摘要（已脱敏、已截断）。
     */
    @TableField("request_param")
    private String requestParam;

    /**
     * 返回信息摘要。
     */
    @TableField("response_msg")
    private String responseMsg;

    /**
     * 操作人 ID。
     */
    @TableField("operator_id")
    private Long operatorId;

    /**
     * 操作人姓名（冗余，避免用户改名后追溯困难）。
     */
    @TableField("operator_name")
    private String operatorName;

    /**
     * 来源 IP。
     */
    @TableField("ip")
    private String ip;

    /**
     * 耗时（毫秒）。
     */
    @TableField("cost_ms")
    private Long costMs;

    /**
     * 执行结果：0 失败 1 成功。
     */
    @TableField("status")
    private Integer status;

    /**
     * 操作时间。
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
