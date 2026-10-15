package com.iecmp.commonm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.commonm.entity.SysOperLog;
import com.iecmp.commonm.mapper.SysOperLogMapper;
import com.iecmp.commonm.service.SysOperLogService;
import org.springframework.stereotype.Service;

/**
 * 操作日志业务实现（{@code sys_oper_log}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SysOperLogService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog>
        implements SysOperLogService {
}
