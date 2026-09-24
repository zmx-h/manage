package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.duty.entity.DutySchedule;
import com.iecmp.duty.mapper.DutyScheduleMapper;
import com.iecmp.duty.service.DutyScheduleService;
import org.springframework.stereotype.Service;

/**
 * 值日安排业务实现（{@code duty_schedule}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link DutyScheduleService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class DutyScheduleServiceImpl extends ServiceImpl<DutyScheduleMapper, DutySchedule>
        implements DutyScheduleService {
}
