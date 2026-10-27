package com.iecmp.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.seat.dto.SeatApplyDTO;
import com.iecmp.seat.entity.SeatApply;
import com.iecmp.seat.entity.SeatArea;
import com.iecmp.seat.entity.SeatInfo;
import com.iecmp.seat.enums.SeatErrorCode;
import com.iecmp.seat.mapper.SeatApplyMapper;
import com.iecmp.seat.mapper.SeatAreaMapper;
import com.iecmp.seat.mapper.SeatInfoMapper;
import com.iecmp.seat.query.SeatApplyMyPageQuery;
import com.iecmp.seat.service.SeatApplyService;
import com.iecmp.seat.vo.SeatApplyMyPageVO;
import com.iecmp.seat.vo.SeatApplyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatApplyServiceImpl extends ServiceImpl<SeatApplyMapper, SeatApply>
        implements SeatApplyService {

    private final SeatAreaMapper seatAreaMapper;
    private final SeatInfoMapper seatInfoMapper;

    @Override
    //去数据库查询 当前用户 是否有没有审核状态的工位申请 如果有就出错误码 不允许有重复提交的申请
    public SeatApplyVO createApply(SeatApplyDTO dto, Long userId) {
        long count = this.count(new LambdaQueryWrapper<SeatApply>()
                .eq(SeatApply::getUserId, userId)
                .eq(SeatApply::getStatus, 0));
        if (count > 0) {
            throw new BusinessException(SeatErrorCode.E50008.getCode(), SeatErrorCode.E50008.getMsg());
        }

        if (seatAreaMapper.selectById(dto.getAreaId()) == null) {
            throw new BusinessException(SeatErrorCode.E50001.getCode(), SeatErrorCode.E50001.getMsg());
        }

        if (dto.getSeatId() != null) {
            SeatInfo seat = seatInfoMapper.selectById(dto.getSeatId());
            if (seat == null) {
                throw new BusinessException(SeatErrorCode.E50003.getCode(), SeatErrorCode.E50003.getMsg());
            }
            if (seat.getStatus() == 2) {
                throw new BusinessException(SeatErrorCode.E50005.getCode(), SeatErrorCode.E50005.getMsg());
            }
        }

        SeatApply apply = new SeatApply();
        apply.setUserId(userId);
        apply.setAreaId(dto.getAreaId());
        apply.setSeatId(dto.getSeatId());
        apply.setReason(dto.getReason());
        apply.setStatus(0);
        this.save(apply);

        SeatApplyVO vo = new SeatApplyVO();
        vo.setApplyId(apply.getId());
        return vo;
    }

    @Override
    public PageResult<SeatApplyMyPageVO> myApplyPage(SeatApplyMyPageQuery query, Long userId) {
        Page<SeatApplyMyPageVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        return PageResult.of(baseMapper.selectMyPage(page, userId, query));
    }

    @Override
    public void cancelApply(Long id, Long userId) {
        SeatApply apply = this.getById(id);
        if (apply == null) {
            throw new BusinessException(SeatErrorCode.E50006.getCode(), SeatErrorCode.E50006.getMsg());
        }
        if (!apply.getUserId().equals(userId)) {
            throw new BusinessException(SeatErrorCode.E50007.getCode(), SeatErrorCode.E50007.getMsg());
        }
        if (apply.getStatus() != 0) {
            throw new BusinessException(SeatErrorCode.E50008.getCode(), SeatErrorCode.E50008.getMsg());
        }
        apply.setStatus(3);
        this.updateById(apply);
    }
}
