package com.iecmp.seat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iecmp.seat.entity.SeatSwap;
import com.iecmp.seat.query.SeatSwapMyPageQuery;
import com.iecmp.seat.query.SeatSwapPageQuery;
import com.iecmp.seat.vo.SeatSwapMyPageVO;
import com.iecmp.seat.vo.SeatSwapPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 座位调换申请表（{@code seat_swap}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/seat/SeatSwapMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_apply_user (apply_user_id)}</li>
 *   <li>{@code KEY idx_target_user (target_user_id)}</li>
 *   <li>{@code KEY idx_apply_seat_id (apply_seat_id)}</li>
 *   <li>{@code KEY idx_target_seat_id (target_seat_id)}</li>
 *   <li>{@code KEY idx_status (status)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface SeatSwapMapper extends BaseMapper<SeatSwap> {

    IPage<SeatSwapMyPageVO> selectMyPage(Page<?> page, @Param("query") SeatSwapMyPageQuery query, @Param("currentUserId") Long currentUserId);

    IPage<SeatSwapPageVO> selectAdminPage(Page<?> page, @Param("query") SeatSwapPageQuery query);
}
