package com.iecmp.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.seat.dto.SeatAddDTO;
import com.iecmp.seat.dto.SeatBatchDTO;
import com.iecmp.seat.dto.SeatStatusDTO;
import com.iecmp.seat.dto.SeatUpdateDTO;
import com.iecmp.seat.entity.SeatArea;
import com.iecmp.seat.entity.SeatInfo;
import com.iecmp.seat.enums.SeatErrorCode;
import com.iecmp.seat.mapper.SeatAreaMapper;
import com.iecmp.seat.mapper.SeatInfoMapper;
import com.iecmp.seat.query.SeatListQuery;
import com.iecmp.seat.service.SeatInfoService;
import com.iecmp.seat.vo.SeatAssignVO;
import com.iecmp.seat.vo.SeatBatchVO;
import com.iecmp.seat.vo.SeatCreateVO;
import com.iecmp.seat.vo.SeatListVO;
import com.iecmp.seat.vo.SeatMyVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 工位业务实现（{@code seat_info}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SeatInfoService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
@RequiredArgsConstructor
public class SeatInfoServiceImpl extends ServiceImpl<SeatInfoMapper, SeatInfo>
        implements SeatInfoService {

    private final SeatAreaMapper seatAreaMapper;

    @Override
    public List<SeatListVO> getSeatList(SeatListQuery query) {
        return baseMapper.selectSeatList(query);
    }

    @Override
    public SeatMyVO getMySeat(Long userId) {
        return baseMapper.selectMySeat(userId);
    }

    @Override
    public SeatCreateVO createSeat(SeatAddDTO dto) {
        if (seatAreaMapper.selectById(dto.getAreaId()) == null) {
            throw new BusinessException(SeatErrorCode.E50001.getCode(), SeatErrorCode.E50001.getMsg());
        }
        LambdaQueryWrapper<SeatInfo> dupQuery = new LambdaQueryWrapper<>();
        dupQuery.eq(SeatInfo::getAreaId, dto.getAreaId())
                .eq(SeatInfo::getSeatNo, dto.getSeatNo());
        if (this.count(dupQuery) > 0) {
            throw new BusinessException(SeatErrorCode.E50004.getCode(), SeatErrorCode.E50004.getMsg());
        }
        SeatInfo seat = new SeatInfo();
        seat.setAreaId(dto.getAreaId());
        seat.setSeatNo(dto.getSeatNo());
        seat.setSeatType(dto.getSeatType() != null ? dto.getSeatType() : 1);
        seat.setXRatio(dto.getXRatio() != null ? dto.getXRatio() : BigDecimal.ZERO);
        seat.setYRatio(dto.getYRatio() != null ? dto.getYRatio() : BigDecimal.ZERO);
        seat.setWidthRatio(dto.getWidthRatio() != null ? dto.getWidthRatio() : BigDecimal.ZERO);
        seat.setHeightRatio(dto.getHeightRatio() != null ? dto.getHeightRatio() : BigDecimal.ZERO);
        seat.setRotation(dto.getRotation() != null ? dto.getRotation() : BigDecimal.ZERO);
        seat.setRemark(dto.getRemark());
        seat.setStatus(1);
        this.save(seat);
        SeatCreateVO vo = new SeatCreateVO();
        vo.setSeatId(seat.getId());
        return vo;
    }

    @Override
    public void updateSeat(SeatUpdateDTO dto) {
        SeatInfo seat = this.getById(dto.getSeatId());
        if (seat == null) {
            throw new BusinessException(SeatErrorCode.E50003.getCode(), SeatErrorCode.E50003.getMsg());
        }
        LambdaQueryWrapper<SeatInfo> dupQuery = new LambdaQueryWrapper<>();
        dupQuery.eq(SeatInfo::getAreaId, dto.getAreaId())
                .eq(SeatInfo::getSeatNo, dto.getSeatNo())
                .ne(SeatInfo::getId, dto.getSeatId());
        if (this.count(dupQuery) > 0) {
            throw new BusinessException(SeatErrorCode.E50004.getCode(), SeatErrorCode.E50004.getMsg());
        }
        seat.setAreaId(dto.getAreaId());
        seat.setSeatNo(dto.getSeatNo());
        seat.setSeatType(dto.getSeatType());
        seat.setXRatio(dto.getXRatio());
        seat.setYRatio(dto.getYRatio());
        seat.setWidthRatio(dto.getWidthRatio());
        seat.setHeightRatio(dto.getHeightRatio());
        seat.setRotation(dto.getRotation());
        seat.setRemark(dto.getRemark());
        this.updateById(seat);
    }

    @Override
    public SeatBatchVO batchUpdateSeats(SeatBatchDTO dto) {
        if (seatAreaMapper.selectById(dto.getAreaId()) == null) {
            throw new BusinessException(SeatErrorCode.E50001.getCode(), SeatErrorCode.E50001.getMsg());
        }
        if (dto.getSeats().size() > 200) {
            throw new BusinessException(500, "单次最多200项");
        }
        List<SeatInfo> updateList = new ArrayList<>(dto.getSeats().size());
        for (SeatBatchDTO.SeatDTO item : dto.getSeats()) {
            SeatInfo seat = this.getById(item.getSeatId());
            if (seat == null) {
                throw new BusinessException(SeatErrorCode.E50003.getCode(), SeatErrorCode.E50003.getMsg());
            }
            if (!seat.getAreaId().equals(dto.getAreaId())) {
                throw new BusinessException(SeatErrorCode.E50003.getCode(), SeatErrorCode.E50003.getMsg());
            }
            SeatInfo update = new SeatInfo();
            update.setId(seat.getId());
            update.setXRatio(item.getXRatio());
            update.setYRatio(item.getYRatio());
            update.setWidthRatio(item.getWidthRatio());
            update.setHeightRatio(item.getHeightRatio());
            update.setRotation(item.getRotation());
            updateList.add(update);
        }
        this.updateBatchById(updateList);
        SeatBatchVO vo = new SeatBatchVO();
        vo.setUpdatedCount(updateList.size());
        return vo;
    }

    @Override
    public void deleteSeat(Long id) {
        SeatInfo seat = this.getById(id);
        if (seat == null) {
            throw new BusinessException(SeatErrorCode.E50003.getCode(), SeatErrorCode.E50003.getMsg());
        }
        if (seat.getStatus() != null && seat.getStatus() == 2) {
            throw new BusinessException(SeatErrorCode.E50005.getCode(), SeatErrorCode.E50005.getMsg());
        }
        if (seat.getUserId() != null) {
            throw new BusinessException(SeatErrorCode.E50005.getCode(), SeatErrorCode.E50005.getMsg());
        }
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeatAssignVO assignSeat(Long seatId, Long userId) {
        SeatInfo target = this.getById(seatId);
        if (target == null) {
            throw new BusinessException(SeatErrorCode.E50003.getCode(), SeatErrorCode.E50003.getMsg());
        }
        if (target.getStatus() != null && target.getStatus() == 2) {
            throw new BusinessException(SeatErrorCode.E50005.getCode(), SeatErrorCode.E50005.getMsg());
        }
        if (target.getUserId() != null) {
            throw new BusinessException(SeatErrorCode.E50005.getCode(), SeatErrorCode.E50005.getMsg());
        }
        Long releasedSeatId = null;
        LambdaQueryWrapper<SeatInfo> oldQuery = new LambdaQueryWrapper<>();
        oldQuery.eq(SeatInfo::getUserId, userId);
        SeatInfo oldSeat = this.getOne(oldQuery);
        if (oldSeat != null) {
            oldSeat.setUserId(null);
            oldSeat.setStatus(1);
            this.updateById(oldSeat);
            releasedSeatId = oldSeat.getId();
        }
        target.setUserId(userId);
        target.setStatus(2);
        target.setAssignTime(LocalDateTime.now());
        this.updateById(target);
        SeatAssignVO vo = new SeatAssignVO();
        vo.setSeatId(seatId);
        vo.setReleasedSeatId(releasedSeatId);
        return vo;
    }

    @Override
    public void releaseSeat(Long id) {
        SeatInfo seat = this.getById(id);
        if (seat == null) {
            throw new BusinessException(SeatErrorCode.E50003.getCode(), SeatErrorCode.E50003.getMsg());
        }
        if (seat.getStatus() == null || seat.getStatus() != 2) {
            throw new BusinessException(SeatErrorCode.E50008.getCode(), SeatErrorCode.E50008.getMsg());
        }
        seat.setUserId(null);
        seat.setStatus(1);
        seat.setAssignTime(null);
        this.updateById(seat);
    }

    @Override
    public void updateSeatStatus(Long id, SeatStatusDTO dto) {
        SeatInfo seat = this.getById(id);
        if (seat == null) {
            throw new BusinessException(SeatErrorCode.E50003.getCode(), SeatErrorCode.E50003.getMsg());
        }
        if (dto.getStatus() == 2) {
            throw new BusinessException(SeatErrorCode.E50008.getCode(), SeatErrorCode.E50008.getMsg());
        }
        if (seat.getStatus() != null && seat.getStatus() == 2) {
            throw new BusinessException(SeatErrorCode.E50008.getCode(), SeatErrorCode.E50008.getMsg());
        }
        seat.setStatus(dto.getStatus());
        if (dto.getRemark() != null) {
            seat.setRemark(dto.getRemark());
        }
        this.updateById(seat);
    }
}
