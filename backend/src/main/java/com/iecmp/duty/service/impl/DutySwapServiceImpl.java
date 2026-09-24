package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.duty.entity.DutySwap;
import com.iecmp.duty.mapper.DutySwapMapper;
import com.iecmp.duty.service.DutySwapService;
import org.springframework.stereotype.Service;

/**
 * 换班申请业务实现（{@code duty_swap}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link DutySwapService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class DutySwapServiceImpl extends ServiceImpl<DutySwapMapper, DutySwap>
        implements DutySwapService {
}
