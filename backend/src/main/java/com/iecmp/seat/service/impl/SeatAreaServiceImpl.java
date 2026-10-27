package com.iecmp.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.seat.entity.SeatArea;
import com.iecmp.seat.mapper.SeatAreaMapper;
import com.iecmp.seat.mapper.SeatInfoMapper;
import com.iecmp.seat.service.SeatAreaService;
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
}