package com.iecmp.seat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.seat.entity.SeatInfo;
import com.iecmp.seat.query.SeatListQuery;
import com.iecmp.seat.vo.AdminSeatPlanVO;
import com.iecmp.seat.vo.SeatListVO;
import com.iecmp.seat.vo.SeatMyVO;
import com.iecmp.seat.vo.SeatPlanDragVO;
import com.iecmp.seat.vo.UserSeatPlanVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 工位表（{@code seat_info}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/seat/SeatInfoMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code UNIQUE KEY uk_area_seat_no (area_id, seat_no, deleted)}</li>
 *   <li>{@code KEY idx_user_id (user_id)}</li>
 *   <li>{@code KEY idx_status (status)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface SeatInfoMapper extends BaseMapper<SeatInfo> {

    /**
     * 5-01 平面图数据：按区域 ID 批量查询工位，LEFT JOIN sys_user 取使用人姓名。
     *
     * <p>返回结果含 {@code areaId} / {@code userId}（{@link com.fasterxml.jackson.annotation.JsonIgnore}）
     * 以便 Service 层分组与判断 isMine。{@code isMine} 不在 SQL 中写死，由 Service 层填充。
     *
     * @param areaIds 区域 ID 列表，非空
     */
    List<UserSeatPlanVO.SeatVO> selectSeatListWithOccupant(@Param("areaIds") List<Long> areaIds);

    List<SeatListVO> selectSeatList(SeatListQuery query);

    SeatMyVO selectMySeat(@Param("userId") Long userId);

    List<AdminSeatPlanVO.SeatVO> selectAdminPlanSeats(@Param("areaIds") List<Long> areaIds);

    List<SeatPlanDragVO.FromSeatVO> selectSeatBriefByIds(@Param("seatIds") List<Long> seatIds);
}
