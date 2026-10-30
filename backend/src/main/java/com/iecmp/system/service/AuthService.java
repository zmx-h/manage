package com.iecmp.system.service;

import com.iecmp.system.dto.AdminLoginDTO;
import com.iecmp.system.dto.InfoUpdateDTO;
import com.iecmp.system.dto.PasswordUpdateDTO;
import com.iecmp.system.dto.TokenRefreshDTO;
import com.iecmp.system.dto.UserLoginDTO;
import com.iecmp.system.vo.AdminLoginVO;
import com.iecmp.system.vo.InfoVO;
import com.iecmp.system.vo.TokenRefreshVO;
import com.iecmp.system.vo.UserLoginVO;

/**
 * 认证与令牌业务。
 */
public interface AuthService {

    /** 1-01 后台登录。 */
    AdminLoginVO loginAdmin(AdminLoginDTO dto);

    /** 1-02 前台登录。 */
    UserLoginVO loginUser(UserLoginDTO dto);

    /** 1-03 退出登录。 */
    void logout(String authorizationHeader);

    /** 1-04 刷新令牌。 */
    TokenRefreshVO refreshToken(TokenRefreshDTO dto);

    /** 1-05 当前用户信息。 */
    InfoVO getCurrentUserInfo();

    /** 1-06 修改当前用户资料。 */
    void updateCurrentUserInfo(InfoUpdateDTO dto);

    /** 1-07 修改当前用户密码，并使当前 accessToken 失效。 */
    void updatePassword(PasswordUpdateDTO dto, String authorizationHeader);
}
