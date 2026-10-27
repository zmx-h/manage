package com.iecmp.duty.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.common.result.PageResult;
import com.iecmp.duty.dto.*;
import com.iecmp.duty.entity.DutySchedule;
import com.iecmp.duty.query.AdminDutySchedulePageQuery;
import com.iecmp.duty.query.DutyScheduleCalendarQuery;
import com.iecmp.duty.query.DutyStatOverviewQuery;
import com.iecmp.duty.query.UserDutySchedulePageQuery;
import com.iecmp.duty.vo.*;

/**
 * 值日安排业务接口（{@code duty_schedule}）。
 *
 * <p>骨架阶段只继承 {@code IService} 获得通用 CRUD 能力；
 * 实现具体接口时，把该表的专有业务方法声明在这里，并在 Impl 中实现。
 *
 * @author liuyujie
 * @date 2026-10-27
 */
public interface DutyScheduleService extends IService<DutySchedule> {
    public DutyScheduleCreateVO add(DutyScheduleAddDTO dto);
    public void update(DutyScheduleUpdateDTO dto);
    public void delete(Long id);
    public PageResult<AdminDutySchedulePageVO> page(AdminDutySchedulePageQuery query);
    void changeStatus(Long id, DutyScheduleStatusDTO dto);
    /** 7-01 已发布计划下的值日安排分页 */
    PageResult<UserDutySchedulePageVO> pagePublic(UserDutySchedulePageQuery query);

    /** 7-02 当前用户的下次值日与待办统计 */
    DutyScheduleMyVO getMyDuty();

    /** 7-03 按月返回值日排布 */
    DutyScheduleCalendarVO getCalendar(DutyScheduleCalendarQuery query);

    /** 7-04 值日完成确认 */
    void confirm(Long id, DutyScheduleConfirmDTO dto);

    /** 7-19 值日统计看板 */
    DutyStatOverviewVO getStatOverview(DutyStatOverviewQuery query);
}
