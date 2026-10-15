package com.iecmp.commonm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.commonm.entity.SysMessage;
import com.iecmp.commonm.mapper.SysMessageMapper;
import com.iecmp.commonm.service.SysMessageService;
import org.springframework.stereotype.Service;

/**
 * 站内消息（补充）业务实现（{@code sys_message}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SysMessageService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage>
        implements SysMessageService {
}
