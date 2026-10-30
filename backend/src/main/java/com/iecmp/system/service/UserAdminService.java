package com.iecmp.system.service;

import com.iecmp.common.result.PageResult;
import com.iecmp.system.dto.UserAddDTO;
import com.iecmp.system.dto.UserUpdateDTO;
import com.iecmp.system.query.UserPageQuery;
import com.iecmp.system.vo.UserCreateVO;
import com.iecmp.system.vo.UserDetailVO;
import com.iecmp.system.vo.UserPageVO;

/** 管理端用户管理（1-08 ~ 1-14）。 */
public interface UserAdminService {

    PageResult<UserPageVO> page(UserPageQuery query);

    UserDetailVO detail(Long id);

    UserCreateVO create(UserAddDTO dto, boolean operatorSuperAdmin);

    void update(UserUpdateDTO dto, boolean operatorSuperAdmin);

    void changeStatus(Long id, Integer status, boolean operatorSuperAdmin);

    void resetPassword(Long id, String newPassword);

    void delete(Long id, boolean operatorSuperAdmin);
}