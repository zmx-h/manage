package com.iecmp.seat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.seat.entity.SeatApply;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工位申请表（{@code seat_apply}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/seat/SeatApplyMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_user_id (user_id)}</li>
 *   <li>{@code KEY idx_area_id (area_id)}</li>
 *   <li>{@code KEY idx_seat_id (seat_id)}</li>
 *   <li>{@code KEY idx_status (status)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface SeatApplyMapper extends BaseMapper<SeatApply> {
}
