package com.iecmp.duty.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.duty.entity.DutyReminderConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提醒配置表（{@code duty_reminder_config}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/duty/DutyReminderConfigMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code UNIQUE KEY uk_plan_id (plan_id, deleted)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface DutyReminderConfigMapper extends BaseMapper<DutyReminderConfig> {
}
