package com.iecmp.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.seat.dto.SeatAreaAddDTO;
import com.iecmp.seat.dto.SeatAreaUpdateDTO;
import com.iecmp.seat.entity.SeatArea;
import com.iecmp.seat.vo.SeatAreaCreateVO;
import com.iecmp.seat.vo.SeatAreaListVO;
import com.iecmp.seat.vo.UserSeatPlanVO;

import java.util.List;

public interface SeatAreaService extends IService<SeatArea> {
    List<UserSeatPlanVO> getSeatPlan(Long areaId, Long currentUserId);

    List<SeatAreaListVO> getAreaList();

    SeatAreaCreateVO createArea(SeatAreaAddDTO dto);

    void updateArea(SeatAreaUpdateDTO dto);

    void deleteArea(Long id);
}