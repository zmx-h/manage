package com.iecmp.duty.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.duty.dto.DutyReminderUpdateDTO;
import com.iecmp.duty.entity.DutyReminderConfig;
import com.iecmp.duty.query.DutyReminderQuery;
import com.iecmp.duty.vo.DutyReminderUpdateVO;
import com.iecmp.duty.vo.DutyReminderVO;

/**
 * 提醒配置业务接口（{@code duty_reminder_config}），接口 7-20 ~ 7-21。
 */
public interface DutyReminderConfigService extends IService<DutyReminderConfig> {

    DutyReminderVO get(DutyReminderQuery query);

    DutyReminderUpdateVO save(DutyReminderUpdateDTO dto);
}