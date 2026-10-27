package com.iecmp.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.seat.dto.SeatSwapDTO;
import com.iecmp.seat.dto.SeatSwapHandleDTO;
import com.iecmp.seat.entity.SeatInfo;
import com.iecmp.seat.entity.SeatSwap;
import com.iecmp.seat.enums.SeatErrorCode;
import com.iecmp.seat.mapper.SeatInfoMapper;
import com.iecmp.seat.mapper.SeatSwapMapper;
import com.iecmp.seat.query.SeatSwapMyPageQuery;
import com.iecmp.seat.service.SeatSwapService;
import com.iecmp.seat.vo.SeatSwapMyPageVO;
import com.iecmp.seat.vo.SeatSwapVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 座位调换申请业务实现（{@code seat_swap}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SeatSwapService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
@RequiredArgsConstructor
public class SeatSwapServiceImpl extends ServiceImpl<SeatSwapMapper, SeatSwap>
        implements SeatSwapService {

    private final SeatInfoMapper seatInfoMapper;

    @Override
    public SeatSwapVO createSwap(SeatSwapDTO dto, Long userId) {
        if (dto.getTargetUserId().equals(userId)) {
            throw new BusinessException(SeatErrorCode.E50010.getCode(), SeatErrorCode.E50010.getMsg());
        }
        SeatInfo applySeat = seatInfoMapper.selectOne(new LambdaQueryWrapper<SeatInfo>()
                .eq(SeatInfo::getUserId, userId)
                .last("LIMIT 1"));
        if (applySeat == null) {
            throw new BusinessException(SeatErrorCode.E50011.getCode(), SeatErrorCode.E50011.getMsg());
        }
        SeatInfo targetSeat = seatInfoMapper.selectOne(new LambdaQueryWrapper<SeatInfo>()
                .eq(SeatInfo::getUserId, dto.getTargetUserId())
                .last("LIMIT 1"));
        if (targetSeat == null) {
            throw new BusinessException(SeatErrorCode.E50011.getCode(), SeatErrorCode.E50011.getMsg());
        }
        SeatSwap swap = new SeatSwap();
        swap.setApplyUserId(userId);
        swap.setTargetUserId(dto.getTargetUserId());
        swap.setApplySeatId(applySeat.getId());
        swap.setTargetSeatId(targetSeat.getId());
        swap.setReason(dto.getReason());
        swap.setStatus(0);
        this.save(swap);
        SeatSwapVO vo = new SeatSwapVO();
        vo.setSwapId(swap.getId());
        vo.setApplySeatId(applySeat.getId());
        vo.setTargetSeatId(targetSeat.getId());
        return vo;
    }

    @Override
    public PageResult<SeatSwapMyPageVO> mySwapPage(SeatSwapMyPageQuery query, Long currentUserId) {
        Page<SeatSwapMyPageVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SeatSwapMyPageVO> result = baseMapper.selectMyPage(page, query, currentUserId);
        for (SeatSwapMyPageVO vo : result.getRecords()) {
            if (vo.getTargetUserId() != null && vo.getTargetUserId().equals(currentUserId)) {
                vo.setIsTarget(1);
            } else {
                vo.setIsTarget(0);
            }
        }
        return PageResult.of(result);
    }

    @Override
    public void handleSwap(Long id, SeatSwapHandleDTO dto, Long userId) {
        SeatSwap seatSwap = this.getOne(new LambdaQueryWrapper<SeatSwap>()
                .eq(SeatSwap::getId, id)
                .eq(SeatSwap::getDeleted, 0));
        if (seatSwap == null) {
            throw new BusinessException(SeatErrorCode.E50009.getCode(), SeatErrorCode.E50009.getMsg());
        }
        if (!userId.equals(seatSwap.getTargetUserId())) {
            throw new BusinessException(SeatErrorCode.E50012.getCode(), SeatErrorCode.E50012.getMsg());
        }
        if (seatSwap.getStatus() != 0) {
            throw new BusinessException(SeatErrorCode.E50013.getCode(), SeatErrorCode.E50013.getMsg());
        }
        Integer handleStatus = dto.getHandleStatus();
        if (handleStatus != 1 && handleStatus != 2) {
            throw new BusinessException(SeatErrorCode.E50013.getCode(), SeatErrorCode.E50013.getMsg());
        }
        seatSwap.setStatus(handleStatus == 1 ? 1 : 3);
        seatSwap.setHandleRemark(dto.getHandleRemark());
        seatSwap.setHandleTime(LocalDateTime.now());
        this.updateById(seatSwap);
    }
}
