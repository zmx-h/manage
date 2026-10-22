package com.iecmp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.system.entity.SysUser;

/**
 * 用户业务接口（{@code sys_user}）。
 *
 * <p>骨架阶段只继承 {@code IService} 获得通用 CRUD 能力；
 * 实现具体接口时，把该表的专有业务方法声明在这里，并在 Impl 中实现。
 *
 * @author IECMP 后端组
 */
public interface SysUserService extends IService<SysUser> {
    SysUser getByUsername(String username);//登录的时候先去数据库查人  这个根据用户名去数据库查有没有这个用户
}
