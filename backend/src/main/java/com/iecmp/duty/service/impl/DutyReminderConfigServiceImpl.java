package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.duty.entity.DutyReminderConfig;
import com.iecmp.duty.mapper.DutyReminderConfigMapper;
import com.iecmp.duty.service.DutyReminderConfigService;
import org.springframework.stereotype.Service;

/**
 * 提醒配置业务实现（{@code duty_reminder_config}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link DutyReminderConfigService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class DutyReminderConfigServiceImpl extends ServiceImpl<DutyReminderConfigMapper, DutyReminderConfig>
        implements DutyReminderConfigService {
}
