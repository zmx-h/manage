package com.iecmp.duty.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 值日安排分页（后台） 查询条件。
 *
 * <p>对应接口：7-17（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class AdminDutySchedulePageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 限定排班计划 */
    @Schema(description = "限定排班计划")
    private Long planId;

    /** 限定值日人 */
    @Schema(description = "限定值日人")
    private Long userId;

    /** 分组名称筛选 */
    @Schema(description = "分组名称筛选")
    private String groupName;

    /** 0 未完成，1 已完成，2 请假，3 已换班 */
    @Schema(description = "0 未完成，1 已完成，2 请假，3 已换班")
    private Integer status;

    /** 模糊匹配值日人姓名 */
    @Schema(description = "模糊匹配值日人姓名")
    private String keyword;

    /** 值日日期起 */
    @Schema(description = "值日日期起")
    private String beginDate;

    /** 值日日期止 */
    @Schema(description = "值日日期止")
    private String endDate;
}
