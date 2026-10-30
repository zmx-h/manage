package com.iecmp.system;

import com.iecmp.common.context.LoginUser;
import com.iecmp.common.context.UserContext;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.properties.IecmpProperties;
import com.iecmp.system.dto.RegisterApplyAuditDTO;
import com.iecmp.system.dto.RegisterApplyDTO;
import com.iecmp.system.dto.RegisterDTO;
import com.iecmp.system.entity.SysInviteCode;
import com.iecmp.system.entity.SysRegisterApply;
import com.iecmp.system.entity.SysRole;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.service.SysInviteCodeService;
import com.iecmp.system.service.SysRegisterApplyService;
import com.iecmp.system.service.SysRoleService;
import com.iecmp.system.service.SysUserRoleService;
import com.iecmp.system.service.SysUserService;
import com.iecmp.system.service.impl.RegisterServiceImpl;
import com.iecmp.system.vo.RegisterApplyAuditVO;
import com.iecmp.system.vo.RegisterConfigVO;
import com.iecmp.system.vo.RegisterVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterServiceImplTest {

    @Mock
    private SysInviteCodeService inviteCodeService;
    @Mock
    private SysRegisterApplyService registerApplyService;
    @Mock
    private SysUserService userService;
    @Mock
    private SysUserRoleService userRoleService;
    @Mock
    private SysRoleService roleService;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private IecmpProperties properties;
    private RegisterServiceImpl service;

    @BeforeEach
    void setUp() {
        properties = new IecmpProperties();
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        service = new RegisterServiceImpl(inviteCodeService, registerApplyService,
                userService, userRoleService, roleService, properties, redisTemplate);
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void configReadsDeploymentSwitches() {
        properties.getRegister().setInviteEnabled(1);
        properties.getRegister().setApplyEnabled(0);
        properties.getRegister().setCaptchaEnabled(1);
        properties.getRegister().setTips("internal only");

        RegisterConfigVO vo = service.getConfig();

        assertEquals(1, vo.getInviteEnabled());
        assertEquals(0, vo.getApplyEnabled());
        assertEquals(1, vo.getCaptchaEnabled());
        assertEquals("internal only", vo.getTips());
    }

    @Test
    void registerConsumesInviteAndCreatesMember() {
        SysInviteCode invite = new SysInviteCode();
        invite.setId(3L);
        invite.setCode("ABCD2345");
        invite.setMaxUseCount(2);
        invite.setUsedCount(1);
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

        RegisterVO vo = service.register(dto, "127.0.0.1");

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
        invite.setMaxUseCount(2);
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
                () -> service.register(dto, "127.0.0.1"));

        assertEquals(10013, error.getCode());
        verify(userService, never()).save(any(SysUser.class));
    }

    @Test
    void repeatedApplyOverwritesPendingApplication() {
        when(valueOperations.increment(anyString())).thenReturn(1L);
        when(userService.count(any())).thenReturn(0L);
        SysRegisterApply existing = new SysRegisterApply();
        existing.setId(8L);
        existing.setStudentNo("20260002");
        when(registerApplyService.getOne(any())).thenReturn(existing);
        when(registerApplyService.update(any())).thenReturn(true);

        RegisterApplyDTO dto = new RegisterApplyDTO();
        dto.setRealName("李四");
        dto.setStudentNo("20260002");
        dto.setReason("想加入");
        dto.setCaptchaKey("captcha-key");
        dto.setCaptchaCode("ABCD");

        var vo = service.apply(dto, "127.0.0.1");

        assertEquals(8L, vo.getApplyId());
        verify(registerApplyService).update(any());
        verify(registerApplyService, never()).save(any(SysRegisterApply.class));
    }

    @Test
    void applyRejectsFourthSubmissionFromSameIp() {
        when(valueOperations.increment(anyString())).thenReturn(4L);

        RegisterApplyDTO dto = new RegisterApplyDTO();
        dto.setRealName("李四");
        dto.setStudentNo("20260002");
        dto.setCaptchaKey("captcha-key");
        dto.setCaptchaCode("ABCD");

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.apply(dto, "127.0.0.1"));

        assertEquals(10030, error.getCode());
    }

    @Test
    void auditCreatesUserAndReturnsInitialPasswordOnce() {
        LoginUser auditor = new LoginUser();
        auditor.setUserId(1L);
        UserContext.set(auditor);

        SysRegisterApply apply = new SysRegisterApply();
        apply.setId(11L);
        apply.setStudentNo("20260003");
        apply.setRealName("王五");
        apply.setStatus(0);
        SysRole member = new SysRole();
        member.setId(4L);
        member.setRoleCode("MEMBER");
        member.setStatus(1);
        when(registerApplyService.getById(11L)).thenReturn(apply);
        when(userService.count(any())).thenReturn(0L);
        when(roleService.getOne(any())).thenReturn(member);
        when(userService.save(any(SysUser.class))).thenAnswer(invocation -> {
            SysUser user = invocation.getArgument(0);
            user.setId(12L);
            return true;
        });
        when(userRoleService.saveBatch(any())).thenReturn(true);

        RegisterApplyAuditDTO dto = new RegisterApplyAuditDTO();
        dto.setAuditStatus(1);

        RegisterApplyAuditVO vo = service.auditApply(11L, dto);

        assertEquals(12L, vo.getUserId());
        assertEquals("20260003", vo.getUsername());
        assertNotNull(vo.getInitialPassword());
        assertTrue(vo.getInitialPassword().length() >= 6);
        verify(registerApplyService).updateById(apply);
    }
}