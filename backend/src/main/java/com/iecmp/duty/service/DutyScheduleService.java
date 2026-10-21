package com.iecmp.duty.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.duty.dto.DutyScheduleAddDTO;
import com.iecmp.duty.entity.DutySchedule;
import com.iecmp.duty.vo.DutyScheduleCreateVO;

/**
 * 值日安排业务接口（{@code duty_schedule}）。
 *
 * <p>骨架阶段只继承 {@code IService} 获得通用 CRUD 能力；
 * 实现具体接口时，把该表的专有业务方法声明在这里，并在 Impl 中实现。
 *
 * @author liuyujie
 * @date 2026-10-21
 */
public interface DutyScheduleService extends IService<DutySchedule> {
    public DutyScheduleCreateVO add(DutyScheduleAddDTO dto);
}
