package com.iecmp.seat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.seat.entity.SeatApply;
import com.iecmp.seat.mapper.SeatApplyMapper;
import com.iecmp.seat.service.SeatApplyService;
import org.springframework.stereotype.Service;

/**
 * 工位申请业务实现（{@code seat_apply}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SeatApplyService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class SeatApplyServiceImpl extends ServiceImpl<SeatApplyMapper, SeatApply>
        implements SeatApplyService {
}
