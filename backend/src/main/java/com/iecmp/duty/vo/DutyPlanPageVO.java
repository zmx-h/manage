package com.iecmp.duty.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 排班计划分页（后台） 响应体。
 *
 * <p>对应接口：7-08（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class DutyPlanPageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 排班计划 ID */
    @Schema(description = "排班计划 ID")
    private Long planId;

    /** 计划标题 */
    @Schema(description = "计划标题")
    private String title;

    /** 1 按周轮转，2 分组轮转，3 手工指定 */
    @Schema(description = "1 按周轮转，2 分组轮转，3 手工指定")
    private Integer planType;

    /** 计划开始日期 */
    @Schema(description = "计划开始日期")
    private String startDate;

    /** 计划结束日期 */
    @Schema(description = "计划结束日期")
    private String endDate;

    /** 参与轮转的成员数 */
    @Schema(description = "参与轮转的成员数")
    private Integer memberCount;

    /** 分组数（`planType = 2` 时有值） */
    @Schema(description = "分组数（`planType = 2` 时有值）")
    private Integer groupCount;

    /** 已生成的值日条目数 */
    @Schema(description = "已生成的值日条目数")
    private Integer scheduleCount;

    /** 已完成的条目数 */
    @Schema(description = "已完成的条目数")
    private Integer completedCount;

    /** 0 草稿，1 已发布，2 已结束 */
    @Schema(description = "0 草稿，1 已发布，2 已结束")
    private Integer status;

    /** 发布时间 */
    @Schema(description = "发布时间")
    private String publishTime;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private String createTime;
}
