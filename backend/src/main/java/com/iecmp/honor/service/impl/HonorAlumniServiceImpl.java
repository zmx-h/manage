package com.iecmp.honor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.honor.entity.HonorAlumni;
import com.iecmp.honor.mapper.HonorAlumniMapper;
import com.iecmp.honor.service.HonorAlumniService;
import org.springframework.stereotype.Service;

/**
 * 学长学姐信息业务实现（{@code honor_alumni}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link HonorAlumniService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class HonorAlumniServiceImpl extends ServiceImpl<HonorAlumniMapper, HonorAlumni>
        implements HonorAlumniService {
}
