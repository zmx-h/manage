package com.iecmp.duty.service;

import com.iecmp.common.result.PageResult;
import com.iecmp.duty.dto.DutyScheduleConfirmDTO;
import com.iecmp.duty.query.DutyScheduleCalendarQuery;
import com.iecmp.duty.query.UserDutySchedulePageQuery;
import com.iecmp.duty.vo.DutyScheduleCalendarVO;
import com.iecmp.duty.vo.DutyScheduleMyVO;
import com.iecmp.duty.vo.UserDutySchedulePageVO;

/**
 * 成员端值日查看与确认服务，接口 7-01 ~ 7-04。
 */
public interface UserDutyScheduleService {

    PageResult<UserDutySchedulePageVO> page(UserDutySchedulePageQuery query);

    DutyScheduleMyVO my();

    DutyScheduleCalendarVO calendar(DutyScheduleCalendarQuery query);

    void confirm(Long id, DutyScheduleConfirmDTO dto);
}