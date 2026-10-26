package com.iecmp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.system.entity.SysUser;

/**
 * 用户业务接口（{@code sys_user}）。
 *
 * @author IECMP 后端组
 */
public interface SysUserService extends IService<SysUser> {

    /** 按登录账号查询未删除用户。 */
    SysUser getByUsername(String username);
}
