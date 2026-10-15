package com.iecmp.duty.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.duty.entity.DutyPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 排班计划表（{@code duty_plan}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/duty/DutyPlanMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_status (status)}</li>
 *   <li>{@code KEY idx_date_range (start_date, end_date)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface DutyPlanMapper extends BaseMapper<DutyPlan> {
}
