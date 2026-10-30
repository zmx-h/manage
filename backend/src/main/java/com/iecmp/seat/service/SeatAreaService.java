package com.iecmp.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.seat.entity.SeatArea;
import com.iecmp.seat.vo.AdminSeatPlanVO;
import com.iecmp.seat.vo.UserSeatPlanVO;

import java.util.List;

public interface SeatAreaService extends IService<SeatArea> {
    List<UserSeatPlanVO> getSeatPlan(Long areaId, Long currentUserId);

    List<AdminSeatPlanVO> getPlanData(Long areaId, Integer withDisabled);
}