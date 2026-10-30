package com.iecmp.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.common.result.PageResult;
import com.iecmp.seat.dto.SeatSwapAuditDTO;
import com.iecmp.seat.dto.SeatSwapDTO;
import com.iecmp.seat.dto.SeatSwapHandleDTO;
import com.iecmp.seat.entity.SeatSwap;
import com.iecmp.seat.query.SeatSwapMyPageQuery;
import com.iecmp.seat.query.SeatSwapPageQuery;
import com.iecmp.seat.vo.SeatSwapMyPageVO;
import com.iecmp.seat.vo.SeatSwapPageVO;
import com.iecmp.seat.vo.SeatSwapVO;

/**
 * 座位调换申请业务接口（{@code seat_swap}）。
 *
 * <p>骨架阶段只继承 {@code IService} 获得通用 CRUD 能力；
 * 实现具体接口时，把该表的专有业务方法声明在这里，并在 Impl 中实现。
 *
 * @author IECMP 后端组
 */
public interface SeatSwapService extends IService<SeatSwap> {

    SeatSwapVO createSwap(SeatSwapDTO dto, Long userId);

    PageResult<SeatSwapMyPageVO> mySwapPage(SeatSwapMyPageQuery query, Long currentUserId);

    void handleSwap(Long id, SeatSwapHandleDTO dto, Long userId);

    PageResult<SeatSwapPageVO> pageSwap(SeatSwapPageQuery query);

    void auditSwap(Long id, SeatSwapAuditDTO dto, Long auditUserId);
}
