package com.iecmp.system;

import com.iecmp.common.context.LoginUser;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.properties.JwtProperties;
import com.iecmp.common.result.ResultCode;
import com.iecmp.common.security.TokenService;
import com.iecmp.system.dto.AdminLoginDTO;
import com.iecmp.system.dto.TokenRefreshDTO;
import com.iecmp.system.dto.UserLoginDTO;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.service.SysUserService;
import com.iecmp.system.service.impl.AuthServiceImpl;
import com.iecmp.system.vo.AdminLoginVO;
import com.iecmp.system.vo.TokenRefreshVO;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final String ADMIN_HASH =
            "$2b$10$u.S87kivBux0RtJ.cjmIjOUfavRCxB5bFGhwX5jpIMSgm1hd07jBe";

    @Mock
    private SysUserService sysUserService;
    @Mock
    private TokenService tokenService;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private Claims claims;

    private AuthServiceImpl authService;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setRefreshRenewThreshold(86400L);
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        authService = new AuthServiceImpl(sysUserService, tokenService, jwtProperties, stringRedisTemplate);
    }

    @Test
    void adminLoginReturnsTokensAndUserInfo() {
        SysUser user = user(1L, "admin", ADMIN_HASH);
        LoginUser loginUser = loginUser(1L, "admin", "超级管理员", "SUPER_ADMIN", "system:user:list");
        when(sysUserService.getByUsername("admin")).thenReturn(user);
        when(valueOperations.get("iecmp:login:fail:admin")).thenReturn(null);
        when(tokenService.loadLoginUser(1L)).thenReturn(loginUser);
        when(tokenService.issue(eq(1L), eq("admin"), eq("超级管理员"), same(loginUser)))
                .thenReturn(new TokenService.TokenPair("access", "refresh", "Bearer", 7200L));

        AdminLoginDTO dto = new AdminLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("123456");

        AdminLoginVO vo = authService.loginAdmin(dto);

        assertEquals("access", vo.getAccessToken());
        assertEquals("refresh", vo.getRefreshToken());
        assertEquals(7200, vo.getExpiresIn());
        assertEquals(Set.of("SUPER_ADMIN"), Set.copyOf(vo.getUserInfo().getRoles()));
        assertEquals(Set.of("system:user:list"), Set.copyOf(vo.getUserInfo().getPermissions()));
        verify(sysUserService).updateById(any(SysUser.class));
        verify(stringRedisTemplate).delete("iecmp:login:fail:admin");
    }

    @Test
    void wrongPasswordIsCountedAndRejected() {
        SysUser user = user(1L, "admin", ADMIN_HASH);
        when(sysUserService.getByUsername("admin")).thenReturn(user);
        when(valueOperations.get("iecmp:login:fail:admin")).thenReturn(null);
        when(valueOperations.increment("iecmp:login:fail:admin")).thenReturn(1L);

        AdminLoginDTO dto = new AdminLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("wrong-password");

        BusinessException error = assertThrows(BusinessException.class, () -> authService.loginAdmin(dto));

        assertEquals(10003, error.getCode());
        verify(stringRedisTemplate).expire("iecmp:login:fail:admin", 600L, java.util.concurrent.TimeUnit.SECONDS);
        verify(tokenService, never()).issue(any(), any(), any(), any());
    }

    @Test
    void userLoginRejectsAccountWithoutMemberRole() {
        SysUser user = user(2L, "manager", ADMIN_HASH);
        LoginUser loginUser = loginUser(2L, "manager", "负责人", "CENTER_HEAD", "budget:period:list");
        when(sysUserService.getByUsername("manager")).thenReturn(user);
        when(valueOperations.get("iecmp:login:fail:manager")).thenReturn(null);
        when(tokenService.loadLoginUser(2L)).thenReturn(loginUser);

        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("manager");
        dto.setPassword("123456");

        BusinessException error = assertThrows(BusinessException.class, () -> authService.loginUser(dto));

        assertEquals(10006, error.getCode());
        verify(tokenService, never()).issue(any(), any(), any(), any());
    }

    @Test
    void refreshRejectsInvalidTokenWithBusinessCode10007() {
        when(tokenService.verifyRefreshToken("bad-token"))
                .thenThrow(new BusinessException(ResultCode.UNAUTHORIZED));

        TokenRefreshDTO dto = new TokenRefreshDTO();
        dto.setRefreshToken("bad-token");

        BusinessException error = assertThrows(BusinessException.class, () -> authService.refreshToken(dto));

        assertEquals(10007, error.getCode());
    }

    @Test
    void refreshRotatesRefreshTokenWhenItIsNearExpiry() {
        LoginUser loginUser = loginUser(1L, "admin", "超级管理员", "SUPER_ADMIN", "system:user:list");
        when(tokenService.verifyRefreshToken("old-refresh")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("1");
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 3600_000L));
        when(tokenService.loadLoginUser(1L)).thenReturn(loginUser);
        when(tokenService.issue(eq(1L), eq("admin"), eq("超级管理员"), same(loginUser)))
                .thenReturn(new TokenService.TokenPair("new-access", "new-refresh", "Bearer", 7200L));

        TokenRefreshDTO dto = new TokenRefreshDTO();
        dto.setRefreshToken("old-refresh");

        TokenRefreshVO vo = authService.refreshToken(dto);

        assertEquals("new-access", vo.getAccessToken());
        assertEquals("new-refresh", vo.getRefreshToken());
        assertNotNull(vo.getExpiresIn());
        assertTrue(vo.getExpiresIn() > 0);
        verify(tokenService).blacklist("old-refresh");
    }

    private static SysUser user(Long id, String username, String password) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName("超级管理员");
        user.setStatus(1);
        return user;
    }

    private static LoginUser loginUser(Long id, String username, String realName,
            String role, String permission) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(id);
        loginUser.setUsername(username);
        loginUser.setRealName(realName);
        loginUser.setRoles(new java.util.TreeSet<>(Set.of(role)));
        loginUser.setPermissions(new java.util.TreeSet<>(Set.of(permission)));
        loginUser.setSuperAdmin("SUPER_ADMIN".equals(role));
        return loginUser;
    }
}