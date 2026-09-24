package com.iecmp.seat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.seat.entity.SeatArea;
import com.iecmp.seat.mapper.SeatAreaMapper;
import com.iecmp.seat.service.SeatAreaService;
import org.springframework.stereotype.Service;

/**
 * 工位区域业务实现（{@code seat_area}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SeatAreaService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class SeatAreaServiceImpl extends ServiceImpl<SeatAreaMapper, SeatArea>
        implements SeatAreaService {
}
