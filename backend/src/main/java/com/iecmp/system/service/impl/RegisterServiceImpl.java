package com.iecmp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iecmp.common.constant.CacheKeyConstants;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.properties.IecmpProperties;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.common.util.PasswordUtil;
import com.iecmp.system.dto.InviteCodeAddDTO;
import com.iecmp.system.dto.RegisterApplyAuditDTO;
import com.iecmp.system.dto.RegisterApplyDTO;
import com.iecmp.system.dto.RegisterDTO;
import com.iecmp.system.entity.SysInviteCode;
import com.iecmp.system.entity.SysRegisterApply;
import com.iecmp.system.entity.SysRole;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.entity.SysUserRole;
import com.iecmp.system.enums.InviteCodeStatusEnum;
import com.iecmp.system.enums.RegisterApplyStatusEnum;
import com.iecmp.system.enums.SystemErrorCode;
import com.iecmp.system.query.InviteCodePageQuery;
import com.iecmp.system.query.RegisterApplyPageQuery;
import com.iecmp.system.service.RegisterService;
import com.iecmp.system.service.SysInviteCodeService;
import com.iecmp.system.service.SysRegisterApplyService;
import com.iecmp.system.service.SysRoleService;
import com.iecmp.system.service.SysUserRoleService;
import com.iecmp.system.service.SysUserService;
import com.iecmp.system.vo.InviteCodeCreateVO;
import com.iecmp.system.vo.InviteCodePageVO;
import com.iecmp.system.vo.RegisterApplyAuditVO;
import com.iecmp.system.vo.RegisterApplyPageVO;
import com.iecmp.system.vo.RegisterApplyVO;
import com.iecmp.system.vo.RegisterConfigVO;
import com.iecmp.system.vo.RegisterVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import java.util.concurrent.TimeUnit;

/**
 * 成员接入、注册审核与邀请码业务实现。
 */
@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private static final String MEMBER_ROLE = "MEMBER";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final char[] INVITE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final char[] PASSWORD_CHARS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SysInviteCodeService sysInviteCodeService;
    private final SysRegisterApplyService sysRegisterApplyService;
    private final SysUserService sysUserService;
    private final SysUserRoleService sysUserRoleService;
    private final SysRoleService sysRoleService;
    private final IecmpProperties iecmpProperties;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public RegisterConfigVO getConfig() {
        IecmpProperties.Register cfg = iecmpProperties.getRegister();
        RegisterConfigVO vo = new RegisterConfigVO();
        vo.setInviteEnabled(cfg.getInviteEnabled());
        vo.setApplyEnabled(cfg.getApplyEnabled());
        vo.setCaptchaEnabled(cfg.getCaptchaEnabled());
        vo.setTips(cfg.getTips());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterVO register(RegisterDTO dto, String ip) {
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
        if (invite.getMaxUseCount() == null || invite.getUsedCount() == null
                || invite.getUsedCount() >= invite.getMaxUseCount()) {
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
                .apply("used_count < max_use_count")
                .setSql("used_count = used_count + 1"));
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
    @Transactional(rollbackFor = Exception.class)
    public RegisterApplyVO apply(RegisterApplyDTO dto, String ip) {
        requireEnabled(iecmpProperties.getRegister().getApplyEnabled(), "注册申请已关闭");
        verifyCaptcha(dto.getCaptchaKey(), dto.getCaptchaCode());
        checkApplyRateLimit(ip);

        String studentNo = dto.getStudentNo().trim();
        if (sysUserService.count(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getStudentNo, studentNo)) > 0) {
            throw error(SystemErrorCode.E10034);
        }

        SysRegisterApply apply = sysRegisterApplyService.getOne(Wrappers.<SysRegisterApply>lambdaQuery()
                .eq(SysRegisterApply::getStudentNo, studentNo));
        if (apply == null) {
            apply = new SysRegisterApply();
            apply.setStudentNo(studentNo);
        } else if (Integer.valueOf(RegisterApplyStatusEnum.APPROVED.getValue()).equals(apply.getStatus())) {
            throw error(SystemErrorCode.E10034);
        }

        LocalDateTime now = LocalDateTime.now();
        if (apply.getId() == null) {
            apply.setRealName(dto.getRealName().trim());
            apply.setMajor(trimToNull(dto.getMajor()));
            apply.setPhone(trimToNull(dto.getPhone()));
            apply.setReason(trimToNull(dto.getReason()));
            apply.setStatus(RegisterApplyStatusEnum.PENDING.getValue());
            apply.setIp(ip);
            apply.setApplyTime(now);
            sysRegisterApplyService.save(apply);
        } else {
            Long applyId = apply.getId();
            sysRegisterApplyService.update(new UpdateWrapper<SysRegisterApply>()
                    .eq("id", applyId)
                    .set("real_name", dto.getRealName().trim())
                    .set("major", trimToNull(dto.getMajor()))
                    .set("phone", trimToNull(dto.getPhone()))
                    .set("reason", trimToNull(dto.getReason()))
                    .set("status", RegisterApplyStatusEnum.PENDING.getValue())
                    .set("ip", ip)
                    .set("apply_time", now)
                    .set("audit_user_id", null)
                    .set("audit_time", null)
                    .set("audit_remark", null)
                    .set("create_user_id", null));
        }

        RegisterApplyVO vo = new RegisterApplyVO();
        vo.setApplyId(apply.getId());
        return vo;
    }

    @Override
    public PageResult<RegisterApplyPageVO> pageApply(RegisterApplyPageQuery query) {
        var wrapper = Wrappers.<SysRegisterApply>lambdaQuery()
                .eq(query.getStatus() != null, SysRegisterApply::getStatus, query.getStatus());
        LocalDateTime begin = parseDateTime(query.getBeginTime());
        LocalDateTime end = parseDateTime(query.getEndTime());
        if (begin != null) {
            wrapper.ge(SysRegisterApply::getApplyTime, begin);
        }
        if (end != null) {
            wrapper.le(SysRegisterApply::getApplyTime, end);
        }
        wrapper.orderByDesc(SysRegisterApply::getApplyTime);

        IPage<SysRegisterApply> page = sysRegisterApplyService.page(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageResult.of(page, this::toApplyVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterApplyAuditVO auditApply(Long id, RegisterApplyAuditDTO dto) {
        SysRegisterApply apply = sysRegisterApplyService.getById(id);
        if (apply == null) {
            throw error(SystemErrorCode.E10031);
        }
        if (!RegisterApplyStatusEnum.PENDING.getValue().equals(apply.getStatus())) {
            throw error(SystemErrorCode.E10032);
        }
        if (!RegisterApplyStatusEnum.APPROVED.getValue().equals(dto.getAuditStatus())
                && !RegisterApplyStatusEnum.REJECTED.getValue().equals(dto.getAuditStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "审核结果只能是通过或驳回");
        }

        apply.setAuditUserId(LoginUserUtil.getUserId());
        apply.setAuditTime(LocalDateTime.now());
        apply.setAuditRemark(trimToNull(dto.getAuditRemark()));
        apply.setStatus(dto.getAuditStatus());

        if (RegisterApplyStatusEnum.REJECTED.getValue().equals(dto.getAuditStatus())) {
            sysRegisterApplyService.updateById(apply);
            return new RegisterApplyAuditVO();
        }

        ensureStudentNoAvailable(apply.getStudentNo());
        String username = StringUtils.hasText(dto.getUsername())
                ? dto.getUsername().trim() : apply.getStudentNo();
        if (username.length() < 4 || username.length() > 32) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "登录账号长度需为 4~32 位");
        }
        ensureUsernameAvailable(username);

        List<Long> roleIds = resolveRoleIds(dto.getRoleIds());
        String initialPassword = randomPassword();
        SysUser user = buildUser(username, PasswordUtil.encode(initialPassword), apply.getRealName(),
                apply.getStudentNo(), apply.getMajor(), apply.getPhone());
        sysUserService.save(user);
        assignRoles(user.getId(), roleIds);

        apply.setCreateUserId(user.getId());
        sysRegisterApplyService.updateById(apply);

        RegisterApplyAuditVO vo = new RegisterApplyAuditVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setInitialPassword(initialPassword);
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
        int maxUseCount = dto.getMaxUseCount() == null ? 1 : dto.getMaxUseCount();
        if (count < 1 || count > 50) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "生成数量需为 1~50");
        }
        if (maxUseCount < 1 || maxUseCount > 1000) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "每个邀请码可用次数需为 1~1000");
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
            invite.setMaxUseCount(maxUseCount);
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
        String redisKey = CacheKeyConstants.CAPTCHA_PREFIX + key.trim();
        String expected = stringRedisTemplate.opsForValue().get(redisKey);
        stringRedisTemplate.delete(redisKey);
        if (expected == null || !expected.equalsIgnoreCase(code.trim())) {
            throw error(SystemErrorCode.E10004);
        }
    }

    private void checkApplyRateLimit(String ip) {
        String key = CacheKeyConstants.LIMIT_REGISTER_PREFIX
                + (StringUtils.hasText(ip) ? ip.trim() : "unknown");
        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            stringRedisTemplate.expire(key, CacheKeyConstants.LIMIT_REGISTER_TTL_SECONDS, TimeUnit.SECONDS);
        }
        if (count != null && count > CacheKeyConstants.REGISTER_LIMIT_PER_HOUR) {
            throw error(SystemErrorCode.E10030);
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

    private List<Long> resolveRoleIds(List<Long> roleIds) {
        List<Long> distinct = roleIds == null ? List.of()
                : roleIds.stream().filter(java.util.Objects::nonNull).distinct().toList();
        if (distinct.isEmpty()) {
            return List.of(findMemberRole().getId());
        }
        List<SysRole> roles = sysRoleService.listByIds(distinct);
        if (roles.size() != distinct.size() || roles.stream().anyMatch(role -> !Integer.valueOf(1).equals(role.getStatus()))) {
            throw error(SystemErrorCode.E10015);
        }
        return distinct;
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

    private RegisterApplyPageVO toApplyVO(SysRegisterApply apply) {
        RegisterApplyPageVO vo = new RegisterApplyPageVO();
        vo.setApplyId(apply.getId());
        vo.setRealName(apply.getRealName());
        vo.setStudentNo(apply.getStudentNo());
        vo.setMajor(apply.getMajor());
        vo.setPhone(apply.getPhone());
        vo.setReason(apply.getReason());
        vo.setStatus(apply.getStatus());
        vo.setIp(apply.getIp());
        vo.setApplyTime(formatDateTime(apply.getApplyTime()));
        vo.setAuditRemark(apply.getAuditRemark());
        vo.setCreateUserId(apply.getCreateUserId());
        return vo;
    }

    private InviteCodePageVO toInviteVO(SysInviteCode invite) {
        InviteCodePageVO vo = new InviteCodePageVO();
        vo.setId(invite.getId());
        vo.setCode(invite.getCode());
        vo.setMaxUseCount(invite.getMaxUseCount());
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

    private String randomPassword() {
        StringBuilder password = new StringBuilder(10);
        password.append("ABCDEFGHJKLMNPQRSTUVWXYZ".charAt(RANDOM.nextInt(24)));
        password.append("23456789".charAt(RANDOM.nextInt(8)));
        for (int i = 2; i < 10; i++) {
            password.append(PASSWORD_CHARS[RANDOM.nextInt(PASSWORD_CHARS.length)]);
        }
        return password.toString();
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private BusinessException error(SystemErrorCode code) {
        return new BusinessException(code.getCode(), code.getMsg());
    }
}