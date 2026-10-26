package com.iecmp.system.service.impl;

import com.iecmp.common.constant.CacheKeyConstants;
import com.iecmp.common.context.LoginUser;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.properties.JwtProperties;
import com.iecmp.common.result.ResultCode;
import com.iecmp.common.security.TokenService;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.system.dto.AdminLoginDTO;
import com.iecmp.system.dto.TokenRefreshDTO;
import com.iecmp.system.dto.UserLoginDTO;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.enums.SystemErrorCode;
import com.iecmp.system.service.AuthService;
import com.iecmp.system.service.SysUserService;
import com.iecmp.system.vo.AdminLoginVO;
import com.iecmp.system.vo.InfoVO;
import com.iecmp.system.vo.TokenRefreshVO;
import com.iecmp.system.vo.UserLoginVO;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 认证与令牌业务实现。
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String MEMBER = "MEMBER";
    private static final Set<String> ADMIN_ROLES = Set.of("SUPER_ADMIN", "CENTER_HEAD", "MINISTER");
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SysUserService sysUserService;
    private final TokenService tokenService;
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public AdminLoginVO loginAdmin(AdminLoginDTO dto) {
        AuthenticatedUser authenticated = authenticate(dto.getUsername(), dto.getPassword(),
                dto.getCaptchaKey(), dto.getCaptchaCode());
        if (!canLoginAdmin(authenticated.loginUser())) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "该账号无后台登录权限");
        }

        TokenService.TokenPair pair = issue(authenticated);
        AdminLoginVO vo = new AdminLoginVO();
        vo.setAccessToken(pair.accessToken());
        vo.setRefreshToken(pair.refreshToken());
        vo.setTokenType(pair.tokenType());
        vo.setExpiresIn(pair.expiresIn().intValue());
        vo.setUserInfo(toAdminUserInfo(authenticated));
        return vo;
    }

    @Override
    public UserLoginVO loginUser(UserLoginDTO dto) {
        AuthenticatedUser authenticated = authenticate(dto.getUsername(), dto.getPassword(),
                dto.getCaptchaKey(), dto.getCaptchaCode());
        if (!authenticated.loginUser().getRoles().contains(MEMBER)) {
            throw new BusinessException(SystemErrorCode.E10006.getCode(), SystemErrorCode.E10006.getMsg());
        }

        TokenService.TokenPair pair = issue(authenticated);
        UserLoginVO vo = new UserLoginVO();
        vo.setAccessToken(pair.accessToken());
        vo.setRefreshToken(pair.refreshToken());
        vo.setTokenType(pair.tokenType());
        vo.setExpiresIn(pair.expiresIn().intValue());
        vo.setUserInfo(toUserInfo(authenticated));
        return vo;
    }

    @Override
    public void logout(String authorizationHeader) {
        String token = resolveToken(authorizationHeader);
        if (StringUtils.hasText(token)) {
            tokenService.blacklist(token);
        }
    }

    @Override
    public TokenRefreshVO refreshToken(TokenRefreshDTO dto) {
        Claims claims;
        try {
            claims = tokenService.verifyRefreshToken(dto.getRefreshToken());
        } catch (BusinessException e) {
            throw refreshTokenInvalid();
        }

        Long userId;
        try {
            userId = Long.valueOf(claims.getSubject());
        } catch (NumberFormatException e) {
            throw refreshTokenInvalid();
        }

        LoginUser loginUser;
        try {
            loginUser = tokenService.loadLoginUser(userId);
        } catch (BusinessException e) {
            throw refreshTokenInvalid();
        }
        if (loginUser == null) {
            throw refreshTokenInvalid();
        }

        TokenService.TokenPair pair = tokenService.issue(
                loginUser.getUserId(), loginUser.getUsername(), loginUser.getRealName(), loginUser);
        TokenRefreshVO vo = new TokenRefreshVO();
        vo.setAccessToken(pair.accessToken());
        vo.setExpiresIn(pair.expiresIn().intValue());

        long refreshRemainSeconds = claims.getExpiration() == null
                ? 0L
                : (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000L;
        if (refreshRemainSeconds <= jwtProperties.getRefreshRenewThreshold()) {
            vo.setRefreshToken(pair.refreshToken());
            tokenService.blacklist(dto.getRefreshToken());
        }
        return vo;
    }

    @Override
    public InfoVO getCurrentUserInfo() {
        LoginUser loginUser = LoginUserUtil.getLoginUser();
        SysUser user = sysUserService.getById(loginUser.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        InfoVO vo = new InfoVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setStudentNo(user.getStudentNo());
        vo.setGrade(user.getGrade());
        vo.setMajor(user.getMajor());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setRoles(new ArrayList<>(loginUser.getRoles()));
        vo.setPermissions(new ArrayList<>(loginUser.getPermissions()));
        vo.setLastLoginTime(user.getLastLoginTime() == null
                ? null
                : user.getLastLoginTime().format(DATE_TIME_FORMATTER));
        return vo;
    }

    private AuthenticatedUser authenticate(String rawUsername, String password,
            String captchaKey, String captchaCode) {
        String username = rawUsername == null ? "" : rawUsername.trim();
        verifyCaptcha(captchaKey, captchaCode);

        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            throw new BusinessException(SystemErrorCode.E10001.getCode(), SystemErrorCode.E10001.getMsg());
        }
        if (!Integer.valueOf(1).equals(user.getStatus())) {
            throw new BusinessException(SystemErrorCode.E10002.getCode(), SystemErrorCode.E10002.getMsg());
        }

        String failKey = failKey(username);
        if (loginLocked(failKey)) {
            throw new BusinessException(SystemErrorCode.E10005.getCode(), SystemErrorCode.E10005.getMsg());
        }
        if (!com.iecmp.common.util.PasswordUtil.matches(password, user.getPassword())) {
            if (recordLoginFailure(failKey) >= CacheKeyConstants.LOGIN_FAIL_MAX_TIMES) {
                throw new BusinessException(SystemErrorCode.E10005.getCode(), SystemErrorCode.E10005.getMsg());
            }
            throw new BusinessException(SystemErrorCode.E10003.getCode(), SystemErrorCode.E10003.getMsg());
        }

        stringRedisTemplate.delete(failKey);
        LocalDateTime loginTime = LocalDateTime.now();
        SysUser patch = new SysUser();
        patch.setId(user.getId());
        patch.setLastLoginTime(loginTime);
        sysUserService.updateById(patch);
        user.setLastLoginTime(loginTime);

        LoginUser loginUser = tokenService.loadLoginUser(user.getId());
        if (loginUser == null) {
            throw new BusinessException(SystemErrorCode.E10001.getCode(), SystemErrorCode.E10001.getMsg());
        }
        return new AuthenticatedUser(user, loginUser);
    }

    private TokenService.TokenPair issue(AuthenticatedUser authenticated) {
        LoginUser loginUser = authenticated.loginUser();
        return tokenService.issue(loginUser.getUserId(), loginUser.getUsername(),
                loginUser.getRealName(), loginUser);
    }

    private void verifyCaptcha(String captchaKey, String captchaCode) {
        if (!StringUtils.hasText(captchaKey) && !StringUtils.hasText(captchaCode)) {
            return;
        }
        if (!StringUtils.hasText(captchaKey) || !StringUtils.hasText(captchaCode)) {
            throw new BusinessException(SystemErrorCode.E10004.getCode(), SystemErrorCode.E10004.getMsg());
        }
        String redisKey = CacheKeyConstants.CAPTCHA_PREFIX + captchaKey.trim();
        String expected = stringRedisTemplate.opsForValue().get(redisKey);
        stringRedisTemplate.delete(redisKey);
        if (expected == null || !expected.equalsIgnoreCase(captchaCode.trim())) {
            throw new BusinessException(SystemErrorCode.E10004.getCode(), SystemErrorCode.E10004.getMsg());
        }
    }

    private boolean loginLocked(String failKey) {
        String value = stringRedisTemplate.opsForValue().get(failKey);
        if (!StringUtils.hasText(value)) {
            return false;
        }
        try {
            return Integer.parseInt(value) >= CacheKeyConstants.LOGIN_FAIL_MAX_TIMES;
        } catch (NumberFormatException e) {
            stringRedisTemplate.delete(failKey);
            return false;
        }
    }

    private long recordLoginFailure(String failKey) {
        Long count = stringRedisTemplate.opsForValue().increment(failKey);
        if (count != null && count == 1L) {
            stringRedisTemplate.expire(failKey, CacheKeyConstants.LOGIN_FAIL_TTL_SECONDS, TimeUnit.SECONDS);
        }
        return count == null ? 0L : count;
    }

    private boolean canLoginAdmin(LoginUser loginUser) {
        if (loginUser.isSuperAdmin()) {
            return true;
        }
        if (loginUser.getRoles() != null && loginUser.getRoles().stream().anyMatch(ADMIN_ROLES::contains)) {
            return true;
        }
        return loginUser.getPermissions() != null && !loginUser.getPermissions().isEmpty();
    }

    private AdminLoginVO.UserInfoVO toAdminUserInfo(AuthenticatedUser authenticated) {
        SysUser user = authenticated.user();
        LoginUser loginUser = authenticated.loginUser();
        AdminLoginVO.UserInfoVO vo = new AdminLoginVO.UserInfoVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setRoles(new ArrayList<>(loginUser.getRoles()));
        vo.setPermissions(new ArrayList<>(loginUser.getPermissions()));
        return vo;
    }

    private UserLoginVO.UserInfoVO toUserInfo(AuthenticatedUser authenticated) {
        SysUser user = authenticated.user();
        LoginUser loginUser = authenticated.loginUser();
        UserLoginVO.UserInfoVO vo = new UserLoginVO.UserInfoVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setRoles(new ArrayList<>(loginUser.getRoles()));
        vo.setPermissions(new ArrayList<>(loginUser.getPermissions()));
        return vo;
    }

    private String resolveToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            return null;
        }
        String prefix = jwtProperties.getPrefix();
        if (StringUtils.hasText(prefix) && authorizationHeader.startsWith(prefix)) {
            return authorizationHeader.substring(prefix.length()).trim();
        }
        return authorizationHeader.trim();
    }

    private String failKey(String username) {
        return CacheKeyConstants.LOGIN_FAIL_PREFIX + username.toLowerCase(Locale.ROOT);
    }

    private BusinessException refreshTokenInvalid() {
        return new BusinessException(SystemErrorCode.E10007.getCode(), SystemErrorCode.E10007.getMsg());
    }

    private record AuthenticatedUser(SysUser user, LoginUser loginUser) {
    }
}
