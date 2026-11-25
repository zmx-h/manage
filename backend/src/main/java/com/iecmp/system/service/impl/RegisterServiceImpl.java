package com.iecmp.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iecmp.common.cache.LocalCacheStore;
import com.iecmp.common.constant.CacheKeyConstants;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.properties.IecmpProperties;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.common.util.PasswordUtil;
import com.iecmp.system.dto.InviteCodeAddDTO;
import com.iecmp.system.dto.RegisterDTO;
import com.iecmp.system.entity.SysInviteCode;
import com.iecmp.system.entity.SysRole;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.entity.SysUserRole;
import com.iecmp.system.enums.InviteCodeStatusEnum;
import com.iecmp.system.enums.SystemErrorCode;
import com.iecmp.system.query.InviteCodePageQuery;
import com.iecmp.system.service.RegisterService;
import com.iecmp.system.service.SysInviteCodeService;
import com.iecmp.system.service.SysRoleService;
import com.iecmp.system.service.SysUserRoleService;
import com.iecmp.system.service.SysUserService;
import com.iecmp.system.vo.InviteCodeCreateVO;
import com.iecmp.system.vo.InviteCodePageVO;
import com.iecmp.system.vo.RegisterConfigVO;
import com.iecmp.system.vo.RegisterVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 成员接入与邀请码业务实现。
 */
@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private static final String MEMBER_ROLE = "MEMBER";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final char[] INVITE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SysInviteCodeService sysInviteCodeService;
    private final SysUserService sysUserService;
    private final SysUserRoleService sysUserRoleService;
    private final SysRoleService sysRoleService;
    private final IecmpProperties iecmpProperties;
    private final LocalCacheStore cacheStore;

    @Override
    public RegisterConfigVO getConfig() {
        IecmpProperties.Register cfg = iecmpProperties.getRegister();
        IecmpProperties.Recruit recruit = iecmpProperties.getRecruit();
        RegisterConfigVO vo = new RegisterConfigVO();
        vo.setInviteEnabled(cfg.getInviteEnabled());
        vo.setCaptchaEnabled(cfg.getCaptchaEnabled());
        vo.setTips(cfg.getTips());
        vo.setRecruitEnabled(recruit.getEnabled());
        vo.setRecruitTips("招新考核通过后，请使用报名邮箱和邮件中的注册码完成注册");
        vo.setRecruitCodeExpireHours(recruit.getCodeExpireHours());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterVO register(RegisterDTO dto) {
        requireEnabled(iecmpProperties.getRegister().getInviteEnabled(), "邀请码注册已关闭");
        verifyCaptcha(dto.getCaptchaKey(), dto.getCaptchaCode());

        String inviteCode = dto.getInviteCode().trim().toUpperCase(Locale.ROOT);
        SysInviteCode invite = sysInviteCodeService.getOne(Wrappers.<SysInviteCode>lambdaQuery()
                .eq(SysInviteCode::getCode, inviteCode)
                .eq(SysInviteCode::getStatus, InviteCodeStatusEnum.VALID.getValue()));
        if (invite == null || invite.getExpireTime() != null
                && !invite.getExpireTime().isAfter(LocalDateTime.now())) {
            throw error(SystemErrorCode.E10028);
        }
        if (invite.getUsedCount() == null || invite.getUsedCount() >= 1) {
            throw error(SystemErrorCode.E10029);
        }

        String username = dto.getUsername().trim();
        String studentNo = dto.getStudentNo().trim();
        ensureUsernameAvailable(username);
        ensureStudentNoAvailable(studentNo);

        boolean consumed = sysInviteCodeService.update(Wrappers.<SysInviteCode>lambdaUpdate()
                .eq(SysInviteCode::getId, invite.getId())
                .eq(SysInviteCode::getStatus, InviteCodeStatusEnum.VALID.getValue())
                .apply("(expire_time IS NULL OR expire_time > NOW())")
                .apply("used_count = 0")
                .setSql("used_count = 1"));
        if (!consumed) {
            throw error(SystemErrorCode.E10029);
        }

        SysUser user = buildUser(username, PasswordUtil.encode(dto.getPassword()),
                dto.getRealName().trim(), studentNo, dto.getMajor(), dto.getPhone());
        sysUserService.save(user);
        assignRoles(user.getId(), List.of(findMemberRole().getId()));

        RegisterVO vo = new RegisterVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        return vo;
    }

    @Override
    public PageResult<InviteCodePageVO> pageInvite(InviteCodePageQuery query) {
        var wrapper = Wrappers.<SysInviteCode>lambdaQuery()
                .eq(query.getStatus() != null, SysInviteCode::getStatus, query.getStatus())
                .like(StringUtils.hasText(query.getKeyword()), SysInviteCode::getCode,
                        StringUtils.hasText(query.getKeyword()) ? query.getKeyword().trim() : null)
                .orderByDesc(SysInviteCode::getCreateTime);
        IPage<SysInviteCode> page = sysInviteCodeService.page(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageResult.of(page, this::toInviteVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InviteCodeCreateVO createInvite(InviteCodeAddDTO dto) {
        int count = dto.getCount() == null ? 1 : dto.getCount();
        if (count < 1 || count > 50) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "生成数量需为 1~50");
        }

        LocalDateTime expireTime = parseDateTime(dto.getExpireTime());
        if (expireTime != null && !expireTime.isAfter(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "过期时间必须晚于当前时间");
        }

        List<SysInviteCode> codes = new ArrayList<>(count);
        Set<String> generated = new HashSet<>();
        while (codes.size() < count) {
            String code = randomInviteCode();
            if (!generated.add(code)
                    || sysInviteCodeService.count(Wrappers.<SysInviteCode>lambdaQuery()
                    .eq(SysInviteCode::getCode, code)) > 0) {
                continue;
            }
            SysInviteCode invite = new SysInviteCode();
            invite.setCode(code);
            invite.setUsedCount(0);
            invite.setExpireTime(expireTime);
            invite.setStatus(InviteCodeStatusEnum.VALID.getValue());
            invite.setRemark(trimToNull(dto.getRemark()));
            codes.add(invite);
        }
        sysInviteCodeService.saveBatch(codes);

        InviteCodeCreateVO vo = new InviteCodeCreateVO();
        vo.setCodes(codes.stream().map(SysInviteCode::getCode).toList());
        return vo;
    }

    @Override
    public void disableInvite(Long id) {
        SysInviteCode invite = sysInviteCodeService.getById(id);
        if (invite == null) {
            throw error(SystemErrorCode.E10033);
        }
        invite.setStatus(InviteCodeStatusEnum.INVALID.getValue());
        sysInviteCodeService.updateById(invite);
    }

    private void requireEnabled(Integer enabled, String message) {
        if (!Integer.valueOf(1).equals(enabled)) {
            throw new BusinessException(ResultCode.FORBIDDEN, message);
        }
    }

    private void verifyCaptcha(String key, String code) {
        if (!Integer.valueOf(1).equals(iecmpProperties.getRegister().getCaptchaEnabled())) {
            return;
        }
        if (!StringUtils.hasText(key) || !StringUtils.hasText(code)) {
            throw error(SystemErrorCode.E10004);
        }
        String cacheKey = CacheKeyConstants.CAPTCHA_PREFIX + key.trim();
        String expected = cacheStore.get(cacheKey);
        cacheStore.delete(cacheKey);
        if (expected == null || !expected.equalsIgnoreCase(code.trim())) {
            throw error(SystemErrorCode.E10004);
        }
    }

    private void ensureUsernameAvailable(String username) {
        if (sysUserService.count(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, username)) > 0) {
            throw error(SystemErrorCode.E10013);
        }
    }

    private void ensureStudentNoAvailable(String studentNo) {
        if (sysUserService.count(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getStudentNo, studentNo)) > 0) {
            throw error(SystemErrorCode.E10014);
        }
    }

    private SysUser buildUser(String username, String encodedPassword, String realName,
            String studentNo, String major, String phone) {
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setRealName(realName);
        user.setStudentNo(studentNo);
        user.setMajor(trimToNull(major));
        user.setPhone(trimToNull(phone));
        user.setStatus(1);
        return user;
    }

    private SysRole findMemberRole() {
        SysRole role = sysRoleService.getOne(Wrappers.<SysRole>lambdaQuery()
                .eq(SysRole::getRoleCode, MEMBER_ROLE)
                .eq(SysRole::getStatus, 1));
        if (role == null) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "系统未配置成员角色");
        }
        return role;
    }

    private void assignRoles(Long userId, List<Long> roleIds) {
        List<SysUserRole> links = roleIds.stream().map(roleId -> {
            SysUserRole link = new SysUserRole();
            link.setUserId(userId);
            link.setRoleId(roleId);
            return link;
        }).toList();
        sysUserRoleService.saveBatch(links);
    }

    private InviteCodePageVO toInviteVO(SysInviteCode invite) {
        InviteCodePageVO vo = new InviteCodePageVO();
        vo.setId(invite.getId());
        vo.setCode(invite.getCode());
        vo.setUsedCount(invite.getUsedCount());
        vo.setExpireTime(formatDateTime(invite.getExpireTime()));
        vo.setStatus(invite.getStatus());
        vo.setCreateTime(formatDateTime(invite.getCreateTime()));
        return vo;
    }

    private String formatDateTime(LocalDateTime time) {
        return time == null ? null : time.format(DATE_TIME_FORMATTER);
    }

    private LocalDateTime parseDateTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return value.contains("T") ? LocalDateTime.parse(value) : LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "时间格式应为 yyyy-MM-dd HH:mm:ss");
        }
    }

    private String randomInviteCode() {
        StringBuilder code = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            code.append(INVITE_CHARS[RANDOM.nextInt(INVITE_CHARS.length)]);
        }
        return code.toString();
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private BusinessException error(SystemErrorCode code) {
        return new BusinessException(code.getCode(), code.getMsg());
    }
}
