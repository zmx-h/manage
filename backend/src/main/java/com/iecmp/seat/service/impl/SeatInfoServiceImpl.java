package com.iecmp.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.seat.dto.SeatPlanDragDTO;
import com.iecmp.seat.entity.SeatInfo;
import com.iecmp.seat.enums.SeatErrorCode;
import com.iecmp.seat.mapper.SeatInfoMapper;
import com.iecmp.seat.query.SeatListQuery;
import com.iecmp.seat.service.SeatInfoService;
import com.iecmp.seat.vo.SeatListVO;
import com.iecmp.seat.vo.SeatMyVO;
import com.iecmp.seat.vo.SeatPlanDragVO;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
public class SeatInfoServiceImpl extends ServiceImpl<SeatInfoMapper, SeatInfo>
        implements SeatInfoService {

    @Override
    public List<SeatListVO> getSeatList(SeatListQuery query) {
        return baseMapper.selectSeatList(query);
    }

    @Override
    public SeatMyVO getMySeat(Long userId) {
        return baseMapper.selectMySeat(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeatPlanDragVO drag(SeatPlanDragDTO dto) {
        if (dto.getFromSeatId().equals(dto.getToSeatId())) {
            throw new BusinessException(SeatErrorCode.E50016.getCode(), SeatErrorCode.E50016.getMsg());
        }
        SeatInfo from = this.getOne(new LambdaQueryWrapper<SeatInfo>()
                .eq(SeatInfo::getId, dto.getFromSeatId())
                .eq(SeatInfo::getDeleted, 0));
        if (from == null) {
            throw new BusinessException(SeatErrorCode.E50003.getCode(), SeatErrorCode.E50003.getMsg());
        }
        if (from.getUserId() == null || from.getStatus() != 2) {
            throw new BusinessException(SeatErrorCode.E50015.getCode(), SeatErrorCode.E50015.getMsg());
        }
        SeatInfo to = this.getOne(new LambdaQueryWrapper<SeatInfo>()
                .eq(SeatInfo::getId, dto.getToSeatId())
                .eq(SeatInfo::getDeleted, 0));
        if (to == null) {
            throw new BusinessException(SeatErrorCode.E50003.getCode(), SeatErrorCode.E50003.getMsg());
        }
        if (to.getStatus() == 3 || to.getStatus() == 4) {
            throw new BusinessException(SeatErrorCode.E50017.getCode(), SeatErrorCode.E50017.getMsg());
        }

        String action;
        if (to.getUserId() == null || to.getStatus() == 1) {
            action = "move";
            SeatInfo fromUpd = new SeatInfo();
            fromUpd.setId(from.getId());
            fromUpd.setVersion(from.getVersion());
            fromUpd.setStatus(1);
            boolean fromOk = this.update(fromUpd, new LambdaUpdateWrapper<SeatInfo>()
                    .eq(SeatInfo::getId, from.getId())
                    .set(SeatInfo::getUserId, null)
                    .set(SeatInfo::getAssignTime, null));
            if (!fromOk) {
                throw new BusinessException(SeatErrorCode.E50005.getCode(), SeatErrorCode.E50005.getMsg());
            }
            SeatInfo toUpd = new SeatInfo();
            toUpd.setId(to.getId());
            toUpd.setVersion(to.getVersion());
            toUpd.setStatus(2);
            toUpd.setUserId(from.getUserId());
            toUpd.setAssignTime(LocalDateTime.now());
            if (!this.updateById(toUpd)) {
                throw new BusinessException(SeatErrorCode.E50005.getCode(), SeatErrorCode.E50005.getMsg());
            }
        } else {
            action = "swap";
            SeatInfo fromUpd = new SeatInfo();
            fromUpd.setId(from.getId());
            fromUpd.setVersion(from.getVersion());
            fromUpd.setUserId(to.getUserId());
            fromUpd.setAssignTime(to.getAssignTime());
            if (!this.updateById(fromUpd)) {
                throw new BusinessException(SeatErrorCode.E50005.getCode(), SeatErrorCode.E50005.getMsg());
            }
            SeatInfo toUpd = new SeatInfo();
            toUpd.setId(to.getId());
            toUpd.setVersion(to.getVersion());
            toUpd.setUserId(from.getUserId());
            toUpd.setAssignTime(from.getAssignTime());
            if (!this.updateById(toUpd)) {
                throw new BusinessException(SeatErrorCode.E50005.getCode(), SeatErrorCode.E50005.getMsg());
            }
        }

        List<SeatPlanDragVO.FromSeatVO> briefs = baseMapper.selectSeatBriefByIds(Arrays.asList(from.getId(), to.getId()));
        SeatPlanDragVO vo = new SeatPlanDragVO();
        vo.setAction(action);
        for (SeatPlanDragVO.FromSeatVO brief : briefs) {
            if (brief.getSeatId().equals(from.getId())) {
                vo.setFromSeat(brief);
            } else if (brief.getSeatId().equals(to.getId())) {
                vo.setToSeat(brief);
            }
        }
        return vo;
    }
}
