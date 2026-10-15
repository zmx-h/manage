package com.iecmp.duty.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.iecmp.common.base.BaseEntity;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 排班计划表：表 {@code duty_plan}。
 *
 * <p>所属：模块七 · 值日排班。字段以《IECMP-数据库设计.md》与
 * {@code docs/sql/IECMP-数据库建表脚本.sql} 为准。
 *
 * <p>公共审计字段（id / create_by / create_time / update_by / update_time / deleted）
 * 继承自 {@link BaseEntity}，此处只声明业务字段。
 *
 * @author IECMP 后端组
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "duty_plan", autoResultMap = true)
public class DutyPlan extends BaseEntity {

    /**
     * 计划标题。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("title")
    private String title;

    /**
     * 轮转方式：1 按周轮转 2 分组轮转 3 手工指定。
     */
    @TableField("plan_type")
    private Integer planType;

    /**
     * 计划开始日期。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 计划结束日期。
     *
     * <p>数据库层面 NOT NULL，新增时必须赋值。
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 参与轮转的成员 ID 数组（JSON）。
     */
    @TableField(value = "member_ids", typeHandler = JacksonTypeHandler.class)
    private List<Long> memberIds;

    /**
     * 分组配置（JSON）。
     */
    @TableField(value = "group_config", typeHandler = JacksonTypeHandler.class)
    private List<GroupConfig> groupConfig;

    /**
     * 默认值日内容。
     */
    @TableField("task_content")
    private String taskContent;

    /**
     * 状态：0 草稿 1 已发布 2 已结束。
     */
    @TableField("status")
    private Integer status;

    /**
     * 发布时间。
     */
    @TableField("publish_time")
    private LocalDateTime publishTime;

    /**
     * 备注。
     */
    @TableField("remark")
    private String remark;

    /**
     * 分组配置元素：字段值与 {@code duty_plan.group_config} 的 JSON 结构一一对应。
     *
     * <p>示例：{@code [{"name":"甲组","memberIds":[12,15]},{"name":"乙组","memberIds":[18,23]}]}
     */
    @Data
    public static class GroupConfig implements java.io.Serializable {

        private static final long serialVersionUID = 1L;

        /** 分组名称，如「甲组」。 */
        private String name;

        /** 组内成员 ID 数组。 */
        private List<Long> memberIds;
    }
}
