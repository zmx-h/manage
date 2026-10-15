package com.iecmp.commonm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.commonm.entity.SysNotice;
import com.iecmp.commonm.mapper.SysNoticeMapper;
import com.iecmp.commonm.service.SysNoticeService;
import org.springframework.stereotype.Service;

/**
 * 公告业务实现（{@code sys_notice}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SysNoticeService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice>
        implements SysNoticeService {
}
