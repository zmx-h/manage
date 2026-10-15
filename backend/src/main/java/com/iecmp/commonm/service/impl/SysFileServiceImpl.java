package com.iecmp.commonm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.commonm.entity.SysFile;
import com.iecmp.commonm.mapper.SysFileMapper;
import com.iecmp.commonm.service.SysFileService;
import org.springframework.stereotype.Service;

/**
 * 文件业务实现（{@code sys_file}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SysFileService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile>
        implements SysFileService {
}
