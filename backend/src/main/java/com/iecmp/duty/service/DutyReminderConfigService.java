package com.iecmp.duty.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.duty.dto.DutyReminderUpdateDTO;
import com.iecmp.duty.entity.DutyReminderConfig;
import com.iecmp.duty.query.DutyReminderQuery;
import com.iecmp.duty.vo.DutyReminderVO;

/**
 * 提醒配置业务接口（{@code duty_reminder_config}）。
 *
 * <p>骨架阶段只继承 {@code IService} 获得通用 CRUD 能力；
 * 实现具体接口时，把该表的专有业务方法声明在这里，并在 Impl 中实现。
 *
 * @author liuyujie
 * @date 2026.10.26
 */
public interface DutyReminderConfigService extends IService<DutyReminderConfig> {
    DutyReminderVO getDutyReminder(DutyReminderQuery query);
    DutyReminderVO putDutyReminder(DutyReminderUpdateDTO dto);
}
