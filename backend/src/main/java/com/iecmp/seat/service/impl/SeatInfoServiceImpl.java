package com.iecmp.seat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.seat.entity.SeatInfo;
import com.iecmp.seat.mapper.SeatInfoMapper;
import com.iecmp.seat.service.SeatInfoService;
import org.springframework.stereotype.Service;

/**
 * 工位业务实现（{@code seat_info}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SeatInfoService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class SeatInfoServiceImpl extends ServiceImpl<SeatInfoMapper, SeatInfo>
        implements SeatInfoService {
}
