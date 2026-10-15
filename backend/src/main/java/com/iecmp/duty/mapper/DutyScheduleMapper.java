package com.iecmp.duty.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.duty.entity.DutySchedule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 值日安排表（{@code duty_schedule}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/duty/DutyScheduleMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code UNIQUE KEY uk_plan_date_user (plan_id, duty_date, user_id, deleted)}</li>
 *   <li>{@code KEY idx_duty_date_user (duty_date, user_id)}</li>
 *   <li>{@code KEY idx_user_date (user_id, duty_date)}</li>
 *   <li>{@code KEY idx_status (status)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface DutyScheduleMapper extends BaseMapper<DutySchedule> {
}
