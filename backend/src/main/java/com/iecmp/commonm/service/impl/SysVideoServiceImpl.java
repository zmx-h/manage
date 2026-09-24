package com.iecmp.commonm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.commonm.entity.SysVideo;
import com.iecmp.commonm.mapper.SysVideoMapper;
import com.iecmp.commonm.service.SysVideoService;
import org.springframework.stereotype.Service;

/**
 * 视频资源业务实现（{@code sys_video}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SysVideoService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class SysVideoServiceImpl extends ServiceImpl<SysVideoMapper, SysVideo>
        implements SysVideoService {
}
