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

/** 座位调换申请业务接口。 */
public interface SeatSwapService extends IService<SeatSwap> {

    /** 5-20 成员发起座位调换 */
    SeatSwapVO createSwap(SeatSwapDTO dto, Long currentUserId);

    /** 5-21 我的调换申请分页 */
    PageResult<SeatSwapMyPageVO> myPage(SeatSwapMyPageQuery query, Long currentUserId);

    /** 5-22 调换对方确认 / 拒绝 */
    void handle(Long id, SeatSwapHandleDTO dto, Long currentUserId);

    /** 5-23 调换申请分页（后台） */
    PageResult<SeatSwapPageVO> adminPage(SeatSwapPageQuery query);

    /** 5-24 调换申请审核 */
    void audit(Long id, SeatSwapAuditDTO dto, Long currentUserId);
}