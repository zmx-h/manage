package com.iecmp.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.seat.dto.SeatPlanDragDTO;
import com.iecmp.seat.entity.SeatInfo;
import com.iecmp.seat.query.SeatListQuery;
import com.iecmp.seat.vo.SeatListVO;
import com.iecmp.seat.vo.SeatMyVO;
import com.iecmp.seat.vo.SeatPlanDragVO;
import java.util.List;

/**
 * 工位业务接口（{@code seat_info}）。
 *
 * <p>骨架阶段只继承 {@code IService} 获得通用 CRUD 能力；
 * 实现具体接口时，把该表的专有业务方法声明在这里，并在 Impl 中实现。
 *
 * @author IECMP 后端组
 */
public interface SeatInfoService extends IService<SeatInfo> {

    List<SeatListVO> getSeatList(SeatListQuery query);

    SeatMyVO getMySeat(Long userId);

    SeatPlanDragVO drag(SeatPlanDragDTO dto);
}
