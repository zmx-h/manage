package com.iecmp.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.common.result.PageResult;
import com.iecmp.seat.dto.SeatApplyDTO;
import com.iecmp.seat.entity.SeatApply;
import com.iecmp.seat.query.SeatApplyMyPageQuery;
import com.iecmp.seat.vo.SeatApplyMyPageVO;
import com.iecmp.seat.vo.SeatApplyVO;

/**
 * 工位申请业务接口（{@code seat_apply}）。
 *
 * <p>骨架阶段只继承 {@code IService} 获得通用 CRUD 能力；
 * 实现具体接口时，把该表的专有业务方法声明在这里，并在 Impl 中实现。
 *
 * @author IECMP 后端组
 */
public interface SeatApplyService extends IService<SeatApply> {

    SeatApplyVO createApply(SeatApplyDTO dto, Long userId);

    PageResult<SeatApplyMyPageVO> myApplyPage(SeatApplyMyPageQuery query, Long userId);

    void cancelApply(Long id, Long userId);
}
