package com.iecmp.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.seat.entity.SeatArea;
import com.iecmp.seat.mapper.SeatAreaMapper;
import com.iecmp.seat.mapper.SeatInfoMapper;
import com.iecmp.seat.service.SeatAreaService;
import com.iecmp.seat.vo.AdminSeatPlanVO;
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
    public List<AdminSeatPlanVO> getPlanData(Long areaId, Integer withDisabled) {
        LambdaQueryWrapper<SeatArea> areaQuery = new LambdaQueryWrapper<>();
        if (areaId != null) {
            areaQuery.eq(SeatArea::getId, areaId);
        }
        areaQuery.orderByAsc(SeatArea::getSortNo);
        List<SeatArea> areas = this.list(areaQuery);
        if (areas.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> areaIds = areas.stream().map(SeatArea::getId).collect(Collectors.toList());
        List<AdminSeatPlanVO.SeatVO> seats = seatInfoMapper.selectAdminPlanSeats(areaIds);
        Map<Long, List<AdminSeatPlanVO.SeatVO>> seatMap = seats.stream()
                .collect(Collectors.groupingBy(AdminSeatPlanVO.SeatVO::getAreaId));

        List<AdminSeatPlanVO> result = new ArrayList<>(areas.size());
        for (SeatArea area : areas) {
            List<AdminSeatPlanVO.SeatVO> areaSeats = seatMap.getOrDefault(area.getId(), Collections.emptyList());
            AdminSeatPlanVO vo = new AdminSeatPlanVO();
            vo.setAreaId(area.getId());
            vo.setName(area.getName());
            vo.setPlanImageUrl(area.getPlanImageUrl());
            vo.setWidth(area.getWidth());
            vo.setHeight(area.getHeight());
            vo.setSortNo(area.getSortNo());

            AdminSeatPlanVO.SeatStatVO stat = new AdminSeatPlanVO.SeatStatVO();
            int free = 0, used = 0, reserved = 0, repairing = 0;
            for (AdminSeatPlanVO.SeatVO seat : areaSeats) {
                int st = seat.getStatus();
                if (st == 1) {
                    free++;
                } else if (st == 2) {
                    used++;
                } else if (st == 3) {
                    reserved++;
                } else if (st == 4) {
                    repairing++;
                }
            }
            stat.setTotal(areaSeats.size());
            stat.setFree(free);
            stat.setUsed(used);
            stat.setReserved(reserved);
            stat.setRepairing(repairing);

            vo.setSeatStat(stat);
            vo.setSeats(areaSeats);
            result.add(vo);
        }
        return result;
    }
}