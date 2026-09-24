package com.iecmp.seat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.seat.entity.SeatSwap;
import com.iecmp.seat.mapper.SeatSwapMapper;
import com.iecmp.seat.service.SeatSwapService;
import org.springframework.stereotype.Service;

/**
 * 座位调换申请业务实现（{@code seat_swap}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SeatSwapService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class SeatSwapServiceImpl extends ServiceImpl<SeatSwapMapper, SeatSwap>
        implements SeatSwapService {
}
