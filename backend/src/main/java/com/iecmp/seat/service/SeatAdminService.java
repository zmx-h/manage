package com.iecmp.seat.service;

import com.iecmp.common.result.PageResult;
import com.iecmp.seat.dto.SeatAddDTO;
import com.iecmp.seat.dto.SeatApplyAuditDTO;
import com.iecmp.seat.dto.SeatAreaAddDTO;
import com.iecmp.seat.dto.SeatAreaUpdateDTO;
import com.iecmp.seat.dto.SeatAssignDTO;
import com.iecmp.seat.dto.SeatBatchDTO;
import com.iecmp.seat.dto.SeatPlanDragDTO;
import com.iecmp.seat.dto.SeatStatusDTO;
import com.iecmp.seat.dto.SeatUpdateDTO;
import com.iecmp.seat.query.AdminSeatPlanQuery;
import com.iecmp.seat.query.SeatApplyPageQuery;
import com.iecmp.seat.query.SeatSwapPageQuery;
import com.iecmp.seat.vo.AdminSeatPlanVO;
import com.iecmp.seat.vo.SeatApplyPageVO;
import com.iecmp.seat.vo.SeatAreaCreateVO;
import com.iecmp.seat.vo.SeatAreaListVO;
import com.iecmp.seat.vo.SeatAssignVO;
import com.iecmp.seat.vo.SeatBatchVO;
import com.iecmp.seat.vo.SeatCreateVO;
import com.iecmp.seat.vo.SeatPlanDragVO;
import com.iecmp.seat.vo.SeatSwapPageVO;

import java.util.List;

/** 管理端工位业务（5-07 ~ 5-19、5-23 ~ 5-26）。 */
public interface SeatAdminService {

    List<SeatAreaListVO> areaList();

    SeatAreaCreateVO createArea(SeatAreaAddDTO dto);

    void updateArea(SeatAreaUpdateDTO dto);

    void deleteArea(Long id);

    SeatCreateVO createSeat(SeatAddDTO dto);

    void updateSeat(SeatUpdateDTO dto);

    SeatBatchVO saveLayout(SeatBatchDTO dto);

    void deleteSeat(Long id);

    SeatAssignVO assignSeat(Long id, SeatAssignDTO dto);

    void releaseSeat(Long id);

    void changeSeatStatus(Long id, SeatStatusDTO dto);

    List<AdminSeatPlanVO> plan(AdminSeatPlanQuery query);

    SeatPlanDragVO drag(SeatPlanDragDTO dto);

    PageResult<SeatApplyPageVO> applyPage(SeatApplyPageQuery query);

    void auditApply(Long id, SeatApplyAuditDTO dto, Long operatorUserId);

    PageResult<SeatSwapPageVO> swapPage(SeatSwapPageQuery query);

    void auditSwap(Long id, com.iecmp.seat.dto.SeatSwapAuditDTO dto, Long operatorUserId);
}