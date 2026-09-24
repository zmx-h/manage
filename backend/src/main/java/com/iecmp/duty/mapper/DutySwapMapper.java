package com.iecmp.duty.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.duty.entity.DutySwap;
import org.apache.ibatis.annotations.Mapper;

/**
 * 换班申请表（{@code duty_swap}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/duty/DutySwapMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_schedule_id (schedule_id)}</li>
 *   <li>{@code KEY idx_apply_user (apply_user_id)}</li>
 *   <li>{@code KEY idx_target_user (target_user_id)}</li>
 *   <li>{@code KEY idx_target_schedule_id (target_schedule_id)}</li>
 *   <li>{@code KEY idx_status (status)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface DutySwapMapper extends BaseMapper<DutySwap> {
}
