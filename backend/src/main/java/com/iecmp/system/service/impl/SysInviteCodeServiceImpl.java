package com.iecmp.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.system.entity.SysInviteCode;
import com.iecmp.system.mapper.SysInviteCodeMapper;
import com.iecmp.system.service.SysInviteCodeService;
import org.springframework.stereotype.Service;

/**
 * 注册邀请码业务实现（{@code sys_invite_code}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link SysInviteCodeService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class SysInviteCodeServiceImpl extends ServiceImpl<SysInviteCodeMapper, SysInviteCode>
        implements SysInviteCodeService {
}
