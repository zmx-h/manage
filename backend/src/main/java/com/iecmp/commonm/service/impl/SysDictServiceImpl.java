package com.iecmp.commonm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.commonm.entity.SysDict;
import com.iecmp.commonm.mapper.SysDictMapper;
import com.iecmp.commonm.service.SysDictService;
import org.springframework.stereotype.Service;

/**
 * 数据字典（补充）业务实现（{@code sys_dict}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SysDictService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict>
        implements SysDictService {
}
