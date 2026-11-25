package com.iecmp.system;

import com.iecmp.common.cache.LocalCacheStore;
import com.iecmp.common.context.UserContext;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.properties.IecmpProperties;
import com.iecmp.system.dto.RegisterDTO;
import com.iecmp.system.entity.SysInviteCode;
import com.iecmp.system.entity.SysRole;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.service.SysInviteCodeService;
import com.iecmp.system.service.SysRoleService;
import com.iecmp.system.service.SysUserRoleService;
import com.iecmp.system.service.SysUserService;
import com.iecmp.system.service.impl.RegisterServiceImpl;
import com.iecmp.system.vo.RegisterConfigVO;
import com.iecmp.system.vo.RegisterVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterServiceImplTest {

    @Mock
    private SysInviteCodeService inviteCodeService;
    @Mock
    private SysUserService userService;
    @Mock
    private SysUserRoleService userRoleService;
    @Mock
    private SysRoleService roleService;

    private LocalCacheStore cacheStore;
    private IecmpProperties properties;
    private RegisterServiceImpl service;

    @BeforeEach
    void setUp() {
        properties = new IecmpProperties();
        cacheStore = new LocalCacheStore();
        service = new RegisterServiceImpl(inviteCodeService, userService,
                userRoleService, roleService, properties, cacheStore);
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void configReadsDeploymentSwitches() {
        properties.getRegister().setInviteEnabled(1);
        properties.getRegister().setCaptchaEnabled(1);
        properties.getRegister().setTips("internal only");

        RegisterConfigVO vo = service.getConfig();

        assertEquals(1, vo.getInviteEnabled());
        assertEquals(1, vo.getCaptchaEnabled());
        assertEquals("internal only", vo.getTips());
    }

    @Test
    void registerConsumesInviteAndCreatesMember() {
        SysInviteCode invite = new SysInviteCode();
        invite.setId(3L);
        invite.setCode("ABCD2345");
        invite.setUsedCount(0);
        invite.setStatus(1);
        invite.setExpireTime(LocalDateTime.now().plusDays(1));
        SysRole member = new SysRole();
        member.setId(4L);
        member.setRoleCode("MEMBER");
        member.setStatus(1);
        when(inviteCodeService.getOne(any())).thenReturn(invite);
        when(userService.count(any())).thenReturn(0L);
        when(inviteCodeService.update(any())).thenReturn(true);
        when(roleService.getOne(any())).thenReturn(member);
        when(userService.save(any(SysUser.class))).thenAnswer(invocation -> {
            SysUser user = invocation.getArgument(0);
            user.setId(9L);
            return true;
        });
        when(userRoleService.saveBatch(any())).thenReturn(true);

        RegisterDTO dto = new RegisterDTO();
        dto.setInviteCode("abcd2345");
        dto.setUsername("member01");
        dto.setPassword("abc12345");
        dto.setRealName("张三");
        dto.setStudentNo("20260001");

        RegisterVO vo = service.register(dto);

        assertEquals(9L, vo.getUserId());
        assertEquals("member01", vo.getUsername());
        verify(inviteCodeService).update(any());
        verify(userRoleService).saveBatch(any());
    }

    @Test
    void registerRejectsExistingUsername() {
        SysInviteCode invite = new SysInviteCode();
        invite.setId(3L);
        invite.setCode("ABCD2345");
        invite.setUsedCount(0);
        invite.setStatus(1);
        when(inviteCodeService.getOne(any())).thenReturn(invite);
        when(userService.count(any())).thenReturn(1L);

        RegisterDTO dto = new RegisterDTO();
        dto.setInviteCode("ABCD2345");
        dto.setUsername("member01");
        dto.setPassword("abc12345");
        dto.setRealName("张三");
        dto.setStudentNo("20260001");

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.register(dto));

        assertEquals(10013, error.getCode());
        verify(userService, never()).save(any(SysUser.class));
    }

}
