package com.iecmp.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.seat.dto.SeatAddDTO;
import com.iecmp.seat.dto.SeatBatchDTO;
import com.iecmp.seat.dto.SeatStatusDTO;
import com.iecmp.seat.dto.SeatUpdateDTO;
import com.iecmp.seat.entity.SeatInfo;
import com.iecmp.seat.query.SeatListQuery;
import com.iecmp.seat.vo.SeatAssignVO;
import com.iecmp.seat.vo.SeatBatchVO;
import com.iecmp.seat.vo.SeatCreateVO;
import com.iecmp.seat.vo.SeatListVO;
import com.iecmp.seat.vo.SeatMyVO;
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

    SeatCreateVO createSeat(SeatAddDTO dto);

    void updateSeat(SeatUpdateDTO dto);

    SeatBatchVO batchUpdateSeats(SeatBatchDTO dto);

    void deleteSeat(Long id);

    SeatAssignVO assignSeat(Long seatId, Long userId);

    void releaseSeat(Long id);

    void updateSeatStatus(Long id, SeatStatusDTO dto);
}
