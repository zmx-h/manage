package com.iecmp.duty.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.common.result.PageResult;
import com.iecmp.duty.dto.DutyScheduleAddDTO;
import com.iecmp.duty.dto.DutyScheduleStatusDTO;
import com.iecmp.duty.dto.DutyScheduleUpdateDTO;
import com.iecmp.duty.entity.DutySchedule;
import com.iecmp.duty.query.AdminDutySchedulePageQuery;
import com.iecmp.duty.vo.AdminDutySchedulePageVO;
import com.iecmp.duty.vo.DutyScheduleCreateVO;

/**
 * 值日安排业务接口（{@code duty_schedule}），接口 7-14 ~ 7-18。
 */
public interface DutyScheduleService extends IService<DutySchedule> {

    DutyScheduleCreateVO add(DutyScheduleAddDTO dto);

    void update(DutyScheduleUpdateDTO dto);

    void delete(Long id);

    PageResult<AdminDutySchedulePageVO> page(AdminDutySchedulePageQuery query);

    void updateStatus(Long id, DutyScheduleStatusDTO dto);
}