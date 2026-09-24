package com.iecmp.duty.query;

import com.iecmp.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 值日安排分页（公示） 查询条件。
 *
 * <p>对应接口：7-01（详见《IECMP-接口开发文档-v1.6.md》）。
 *
 * @author IECMP 后端组
 */
@Data
public class UserDutySchedulePageQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 排班计划 ID */
    @Schema(description = "排班计划 ID")
    private Long planId;

    /** 限定某个成员 */
    @Schema(description = "限定某个成员")
    private Long userId;

    /** 分组名称筛选 */
    @Schema(description = "分组名称筛选")
    private String groupName;

    /** 0 未完成，1 已完成，2 请假，3 已换班 */
    @Schema(description = "0 未完成，1 已完成，2 请假，3 已换班")
    private Integer status;

    /** 值日日期起，格式 yyyy-MM-dd */
    @Schema(description = "值日日期起，格式 yyyy-MM-dd")
    private String beginDate;

    /** 值日日期止 */
    @Schema(description = "值日日期止")
    private String endDate;
}
