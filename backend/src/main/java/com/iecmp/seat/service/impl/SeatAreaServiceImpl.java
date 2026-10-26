package com.iecmp.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.seat.dto.SeatAreaAddDTO;
import com.iecmp.seat.dto.SeatAreaUpdateDTO;
import com.iecmp.seat.entity.SeatArea;
import com.iecmp.seat.entity.SeatInfo;
import com.iecmp.seat.enums.SeatErrorCode;
import com.iecmp.seat.mapper.SeatAreaMapper;
import com.iecmp.seat.mapper.SeatInfoMapper;
import com.iecmp.seat.service.SeatAreaService;
import com.iecmp.seat.vo.SeatAreaCreateVO;
import com.iecmp.seat.vo.SeatAreaListVO;
import com.iecmp.seat.vo.UserSeatPlanVO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatAreaServiceImpl extends ServiceImpl<SeatAreaMapper, SeatArea>
        implements SeatAreaService {

    private final SeatInfoMapper seatInfoMapper;

    @Override
    public List<UserSeatPlanVO> getSeatPlan(Long areaId, Long currentUserId) {
        LambdaQueryWrapper<SeatArea> areaQuery = new LambdaQueryWrapper<>();
        if (areaId != null) {
            areaQuery.eq(SeatArea::getId, areaId);
        }
        List<SeatArea> areas = this.list(areaQuery);
        if (areas.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> areaIds = areas.stream().map(SeatArea::getId).collect(Collectors.toList());
        List<UserSeatPlanVO.SeatVO> seats = seatInfoMapper.selectSeatListWithOccupant(areaIds);

        for (UserSeatPlanVO.SeatVO seat : seats) {
            seat.setIsMine(currentUserId.equals(seat.getUserId()) ? 1 : 0);
        }

        Map<Long, List<UserSeatPlanVO.SeatVO>> seatMap = seats.stream()
                .collect(Collectors.groupingBy(UserSeatPlanVO.SeatVO::getAreaId));

        List<UserSeatPlanVO> result = new ArrayList<>(areas.size());
        for (SeatArea area : areas) {
            UserSeatPlanVO vo = new UserSeatPlanVO();
            vo.setAreaId(area.getId());
            vo.setName(area.getName());
            vo.setPlanImageUrl(area.getPlanImageUrl());
            vo.setWidth(area.getWidth());
            vo.setHeight(area.getHeight());
            vo.setSeats(seatMap.getOrDefault(area.getId(), Collections.emptyList()));
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<SeatAreaListVO> getAreaList() {
        LambdaQueryWrapper<SeatArea> query = new LambdaQueryWrapper<>();
        query.orderByAsc(SeatArea::getSortNo);
        List<SeatArea> areas = this.list(query);

        List<Map<String, Object>> counts = seatInfoMapper.countSeatsGroupByArea();
        Map<Long, Integer> seatCountMap = counts.stream().collect(Collectors.toMap(
                m -> ((Number) m.get("areaId")).longValue(),
                m -> ((Number) m.get("cnt")).intValue()
        ));

        List<SeatAreaListVO> result = new ArrayList<>(areas.size());
        for (SeatArea area : areas) {
            SeatAreaListVO vo = new SeatAreaListVO();
            vo.setAreaId(area.getId());
            vo.setName(area.getName());
            vo.setPlanImageUrl(area.getPlanImageUrl());
            vo.setWidth(area.getWidth());
            vo.setHeight(area.getHeight());
            vo.setDescription(area.getDescription());
            vo.setSeatCount(seatCountMap.getOrDefault(area.getId(), 0));
            vo.setSortNo(area.getSortNo());
            result.add(vo);
        }
        return result;
    }

    @Override
    public SeatAreaCreateVO createArea(SeatAreaAddDTO dto) {
        SeatArea area = new SeatArea();
        area.setName(dto.getName());
        area.setPlanImageUrl(dto.getPlanImageUrl());
        area.setWidth(dto.getWidth() != null ? dto.getWidth() : 0);
        area.setHeight(dto.getHeight() != null ? dto.getHeight() : 0);
        area.setDescription(dto.getDescription());
        area.setSortNo(dto.getSortNo());
        this.save(area);
        SeatAreaCreateVO vo = new SeatAreaCreateVO();
        vo.setAreaId(area.getId());
        return vo;
    }

    @Override
    public void updateArea(SeatAreaUpdateDTO dto) {
        SeatArea area = this.getById(dto.getAreaId());
        if (area == null) {
            throw new BusinessException(SeatErrorCode.E50001.getCode(), SeatErrorCode.E50001.getMsg());
        }
        if (dto.getPlanImageUrl() != null || dto.getWidth() != null || dto.getHeight() != null) {
            if (dto.getWidth() == null || dto.getWidth() <= 0
                    || dto.getHeight() == null || dto.getHeight() <= 0) {
                throw new BusinessException(SeatErrorCode.E50014.getCode(), SeatErrorCode.E50014.getMsg());
            }
        }
        area.setName(dto.getName());
        area.setPlanImageUrl(dto.getPlanImageUrl());
        area.setWidth(dto.getWidth());
        area.setHeight(dto.getHeight());
        area.setDescription(dto.getDescription());
        area.setSortNo(dto.getSortNo());
        this.updateById(area);
    }

    @Override
    public void deleteArea(Long id) {
        SeatArea area = this.getById(id);
        if (area == null) {
            throw new BusinessException(SeatErrorCode.E50001.getCode(), SeatErrorCode.E50001.getMsg());
        }
        LambdaQueryWrapper<SeatInfo> seatQuery = new LambdaQueryWrapper<>();
        seatQuery.eq(SeatInfo::getAreaId, id);//指定查询条件
        long count = seatInfoMapper.selectCount(seatQuery);
        if (count > 0) {
            throw new BusinessException(SeatErrorCode.E50002.getCode(), SeatErrorCode.E50002.getMsg());
        }
        this.removeById(id);
    }
}