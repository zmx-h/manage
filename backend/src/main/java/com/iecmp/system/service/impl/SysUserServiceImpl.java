package com.iecmp.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.mapper.SysUserMapper;
import com.iecmp.system.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * 用户业务实现（{@code sys_user}）。
 *
 * @author IECMP 后端组
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    @Override
    public SysUser getByUsername(String username) {
        return getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
    }
}
