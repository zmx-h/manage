package com.iecmp.seat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.seat.entity.SeatArea;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工位区域表（{@code seat_area}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/seat/SeatAreaMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface SeatAreaMapper extends BaseMapper<SeatArea> {
}
