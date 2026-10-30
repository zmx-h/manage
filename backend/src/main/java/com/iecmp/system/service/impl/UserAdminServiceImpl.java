package com.iecmp.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.common.util.PasswordUtil;
import com.iecmp.system.dto.UserAddDTO;
import com.iecmp.system.dto.UserUpdateDTO;
import com.iecmp.system.entity.SysRole;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.entity.SysUserRole;
import com.iecmp.system.enums.SystemErrorCode;
import com.iecmp.system.query.UserPageQuery;
import com.iecmp.system.service.SysRoleService;
import com.iecmp.system.service.SysUserRoleService;
import com.iecmp.system.service.SysUserService;
import com.iecmp.system.service.UserAdminService;
import com.iecmp.system.vo.UserCreateVO;
import com.iecmp.system.vo.UserDetailVO;
import com.iecmp.system.vo.UserPageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** 管理端用户管理实现。 */
@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {

    private static final String SUPER_ADMIN = "SUPER_ADMIN";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{6,32}$");

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final SysUserRoleService sysUserRoleService;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public PageResult<UserPageVO> page(UserPageQuery query) {
        var wrapper = Wrappers.<SysUser>lambdaQuery()
                .eq(query.getStatus() != null, SysUser::getStatus, query.getStatus())
                .eq(StringUtils.hasText(query.getGrade()), SysUser::getGrade,
                        query.getGrade() == null ? null : query.getGrade().trim())
                .ge(StringUtils.hasText(query.getBeginTime()), SysUser::getCreateTime,
                        parseDateTime(query.getBeginTime()))
                .le(StringUtils.hasText(query.getEndTime()), SysUser::getCreateTime,
                        parseDateTime(query.getEndTime()));
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(w -> w.like(SysUser::getRealName, keyword)
                    .or().like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getStudentNo, keyword));
        }
        if (StringUtils.hasText(query.getRoleCode())) {
            SysRole role = sysRoleService.getOne(Wrappers.<SysRole>lambdaQuery()
                    .eq(SysRole::getRoleCode, query.getRoleCode().trim()), false);
            if (role == null) {
                return PageResult.empty(query.getPageNum(), query.getPageSize());
            }
            List<Long> userIds = sysUserRoleService.list(Wrappers.<SysUserRole>lambdaQuery()
                            .eq(SysUserRole::getRoleId, role.getId()))
                    .stream().map(SysUserRole::getUserId).filter(Objects::nonNull).distinct().toList();
            if (userIds.isEmpty()) {
                return PageResult.empty(query.getPageNum(), query.getPageSize());
            }
            wrapper.in(SysUser::getId, userIds);
        }
        wrapper.orderByDesc(SysUser::getCreateTime).orderByDesc(SysUser::getId);
        IPage<SysUser> page = sysUserService.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        Map<Long, List<SysRole>> roleMap = loadRoleMap(page.getRecords().stream().map(SysUser::getId).toList());
        return PageResult.of(page, user -> toPageVO(user, roleMap.getOrDefault(user.getId(), List.of())));
    }

    @Override
    public UserDetailVO detail(Long id) {
        SysUser user = requireUser(id);
        List<SysRole> roles = loadRoleMap(List.of(id)).getOrDefault(id, List.of());
        UserDetailVO vo = new UserDetailVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setStudentNo(user.getStudentNo());
        vo.setGrade(user.getGrade());
        vo.setMajor(user.getMajor());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setStatus(user.getStatus());
        vo.setRoleCodes(roles.stream().map(SysRole::getRoleCode).toList());
        vo.setRoleNames(roles.stream().map(SysRole::getRoleName).toList());
        vo.setRoleIds(roles.stream().map(SysRole::getId).toList());
        vo.setLastLoginTime(format(user.getLastLoginTime()));
        vo.setCreateTime(format(user.getCreateTime()));
        vo.setRemark(user.getRemark());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCreateVO create(UserAddDTO dto, boolean operatorSuperAdmin) {
        String username = dto.getUsername() == null ? "" : dto.getUsername().trim();
        if (username.length() < 4 || username.length() > 32) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "登录账号长度需为 4~32 位");
        }
        validateProfile(dto.getRealName(), dto.getStudentNo(), dto.getGrade(), dto.getMajor(),
                dto.getPhone(), dto.getEmail(), dto.getRemark(), null);
        if (sysUserService.count(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username)) > 0) {
            throw error(SystemErrorCode.E10013);
        }
        if (StringUtils.hasText(dto.getStudentNo())
                && sysUserService.count(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getStudentNo, dto.getStudentNo().trim())) > 0) {
            throw error(SystemErrorCode.E10014);
        }
        if (StringUtils.hasText(dto.getPhone())
                && sysUserService.count(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getPhone, dto.getPhone().trim())) > 0) {
            throw error(SystemErrorCode.E10008);
        }
        if (StringUtils.hasText(dto.getEmail())
                && sysUserService.count(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getEmail, dto.getEmail().trim().toLowerCase())) > 0) {
            throw error(SystemErrorCode.E10009);
        }
        List<SysRole> roles = requireRoles(dto.getRoleIds(), operatorSuperAdmin);
        int status = dto.getStatus() == null ? 1 : dto.getStatus();
        validateStatus(status);

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(PasswordUtil.encode(DEFAULT_PASSWORD));
        user.setRealName(dto.getRealName().trim());
        user.setStudentNo(trimToNull(dto.getStudentNo()));
        user.setGrade(trimToNull(dto.getGrade()));
        user.setMajor(trimToNull(dto.getMajor()));
        user.setPhone(trimToNull(dto.getPhone()));
        user.setEmail(trimToNullLower(dto.getEmail()));
        user.setStatus(status);
        user.setRemark(trimToNull(dto.getRemark()));
        if (!sysUserService.save(user)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "用户保存失败");
        }
        assignRoles(user.getId(), roles);
        UserCreateVO vo = new UserCreateVO();
        vo.setUserId(user.getId());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserUpdateDTO dto, boolean operatorSuperAdmin) {
        SysUser user = requireUser(dto.getUserId());
        List<SysRole> currentRoles = loadRoleMap(List.of(user.getId())).getOrDefault(user.getId(), List.of());
        boolean targetSuper = currentRoles.stream().anyMatch(role -> SUPER_ADMIN.equals(role.getRoleCode()));
        if (targetSuper && !operatorSuperAdmin) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        validateProfile(dto.getRealName(), dto.getStudentNo(), dto.getGrade(), dto.getMajor(),
                dto.getPhone(), dto.getEmail(), dto.getRemark(), user.getId());
        if (StringUtils.hasText(dto.getStudentNo())
                && sysUserService.count(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getStudentNo, dto.getStudentNo().trim())
                .ne(SysUser::getId, user.getId())) > 0) {
            throw error(SystemErrorCode.E10014);
        }
        if (StringUtils.hasText(dto.getPhone())
                && sysUserService.count(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getPhone, dto.getPhone().trim())
                .ne(SysUser::getId, user.getId())) > 0) {
            throw error(SystemErrorCode.E10008);
        }
        if (StringUtils.hasText(dto.getEmail())
                && sysUserService.count(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getEmail, dto.getEmail().trim().toLowerCase())
                .ne(SysUser::getId, user.getId())) > 0) {
            throw error(SystemErrorCode.E10009);
        }
        List<SysRole> roles = requireRoles(dto.getRoleIds(), operatorSuperAdmin);
        int status = dto.getStatus() == null ? user.getStatus() : dto.getStatus();
        validateStatus(status);
        if (targetSuper && status != 1) {
            throw error(SystemErrorCode.E10016);
        }

        user.setRealName(dto.getRealName().trim());
        user.setStudentNo(trimToNull(dto.getStudentNo()));
        user.setGrade(trimToNull(dto.getGrade()));
        user.setMajor(trimToNull(dto.getMajor()));
        user.setPhone(trimToNull(dto.getPhone()));
        user.setEmail(trimToNullLower(dto.getEmail()));
        user.setStatus(status);
        user.setRemark(trimToNull(dto.getRemark()));
        if (!sysUserService.updateById(user)) {
            throw error(SystemErrorCode.E10012);
        }
        replaceRoles(user.getId(), roles);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, Integer status, boolean operatorSuperAdmin) {
        validateStatus(status);
        SysUser user = requireUser(id);
        List<SysRole> roles = loadRoleMap(List.of(id)).getOrDefault(id, List.of());
        if (status == 0 && roles.stream().anyMatch(role -> SUPER_ADMIN.equals(role.getRoleCode()))) {
            throw error(SystemErrorCode.E10016);
        }
        user.setStatus(status);
        if (!sysUserService.updateById(user)) {
            throw error(SystemErrorCode.E10012);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id, String newPassword) {
        SysUser user = requireUser(id);
        String password = StringUtils.hasText(newPassword) ? newPassword.trim() : DEFAULT_PASSWORD;
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "密码需为 6~32 位且同时包含字母和数字");
        }
        user.setPassword(PasswordUtil.encode(password));
        if (!sysUserService.updateById(user)) {
            throw error(SystemErrorCode.E10012);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, boolean operatorSuperAdmin) {
        SysUser user = requireUser(id);
        List<SysRole> roles = loadRoleMap(List.of(id)).getOrDefault(id, List.of());
        if (roles.stream().anyMatch(role -> SUPER_ADMIN.equals(role.getRoleCode()))) {
            throw error(SystemErrorCode.E10019);
        }
        if (count("SELECT COUNT(*) FROM asset_borrow WHERE user_id = ? AND status IN (1, 4) "
                + "AND actual_return_time IS NULL AND deleted = 0", id) > 0) {
            throw error(SystemErrorCode.E10017);
        }
        int pending = 0;
        pending += count("SELECT COUNT(*) FROM asset_borrow WHERE user_id = ? AND status = 0 AND deleted = 0", id);
        pending += count("SELECT COUNT(*) FROM budget_apply WHERE apply_user_id = ? AND status = 0 AND deleted = 0", id);
        pending += count("SELECT COUNT(*) FROM seat_apply WHERE user_id = ? AND status = 0 AND deleted = 0", id);
        pending += count("SELECT COUNT(*) FROM seat_swap WHERE (apply_user_id = ? OR target_user_id = ?) "
                + "AND status IN (0, 1) AND deleted = 0", id, id);
        pending += count("SELECT COUNT(*) FROM duty_swap WHERE (apply_user_id = ? OR target_user_id = ?) "
                + "AND status = 0 AND deleted = 0", id, id);
        pending += count("SELECT COUNT(*) FROM honor_alumni WHERE submit_user_id = ? AND status = 0 AND deleted = 0", id);
        if (pending > 0) {
            throw error(SystemErrorCode.E10018);
        }
        if (!sysUserService.removeById(id)) {
            throw error(SystemErrorCode.E10012);
        }
    }

    private SysUser requireUser(Long id) {
        SysUser user = id == null ? null : sysUserService.getById(id);
        if (user == null) {
            throw error(SystemErrorCode.E10012);
        }
        return user;
    }

    private List<SysRole> requireRoles(List<Long> roleIds, boolean operatorSuperAdmin) {
        List<Long> ids = roleIds == null ? List.of() : roleIds.stream()
                .filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            throw error(SystemErrorCode.E10015);
        }
        List<SysRole> roles = sysRoleService.listByIds(ids);
        if (roles.size() != ids.size()) {
            throw error(SystemErrorCode.E10015);
        }
        if (!operatorSuperAdmin && roles.stream().anyMatch(role -> SUPER_ADMIN.equals(role.getRoleCode()))) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return roles;
    }

    private void assignRoles(Long userId, List<SysRole> roles) {
        List<SysUserRole> links = roles.stream().map(role -> {
            SysUserRole link = new SysUserRole();
            link.setUserId(userId);
            link.setRoleId(role.getId());
            return link;
        }).toList();
        if (!sysUserRoleService.saveBatch(links)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "用户角色保存失败");
        }
    }

    private void replaceRoles(Long userId, List<SysRole> roles) {
        sysUserRoleService.remove(Wrappers.<SysUserRole>lambdaQuery().eq(SysUserRole::getUserId, userId));
        assignRoles(userId, roles);
    }

    private Map<Long, List<SysRole>> loadRoleMap(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        List<SysUserRole> links = sysUserRoleService.list(Wrappers.<SysUserRole>lambdaQuery()
                .in(SysUserRole::getUserId, userIds));
        if (links.isEmpty()) {
            return Map.of();
        }
        Set<Long> roleIds = links.stream().map(SysUserRole::getRoleId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, SysRole> roles = sysRoleService.listByIds(roleIds).stream()
                .collect(Collectors.toMap(SysRole::getId, Function.identity(), (a, b) -> a));
        Map<Long, List<SysRole>> result = new HashMap<>();
        for (SysUserRole link : links) {
            SysRole role = roles.get(link.getRoleId());
            if (role != null) {
                result.computeIfAbsent(link.getUserId(), key -> new ArrayList<>()).add(role);
            }
        }
        result.values().forEach(list -> list.sort(Comparator.comparing(SysRole::getSortNo,
                Comparator.nullsLast(Integer::compareTo))));
        return result;
    }

    private UserPageVO toPageVO(SysUser user, List<SysRole> roles) {
        UserPageVO vo = new UserPageVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setStudentNo(user.getStudentNo());
        vo.setGrade(user.getGrade());
        vo.setMajor(user.getMajor());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setStatus(user.getStatus());
        vo.setRoleCodes(roles.stream().map(SysRole::getRoleCode).toList());
        vo.setRoleNames(roles.stream().map(SysRole::getRoleName).toList());
        vo.setLastLoginTime(format(user.getLastLoginTime()));
        vo.setCreateTime(format(user.getCreateTime()));
        return vo;
    }

    private void validateProfile(String realName, String studentNo, String grade, String major,
                                 String phone, String email, String remark, Long excludeId) {
        if (!StringUtils.hasText(realName) || realName.trim().length() > 32) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "姓名不能为空且最长 32 字");
        }
        if (studentNo != null && studentNo.trim().length() > 20) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "学号最长 20 位");
        }
        if (grade != null && grade.trim().length() > 10) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "年级最长 10 字");
        }
        if (major != null && major.trim().length() > 50) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "专业最长 50 字");
        }
        if (StringUtils.hasText(phone) && !PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "手机号格式不正确");
        }
        if (StringUtils.hasText(email)) {
            String value = email.trim();
            if (value.length() > 64 || !EMAIL_PATTERN.matcher(value).matches()) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "邮箱格式不正确");
            }
        }
        if (remark != null && remark.length() > 255) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "备注最长 255 字");
        }
    }

    private void validateStatus(Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "status 仅支持 0 或 1");
        }
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String trimToNullLower(String value) {
        return StringUtils.hasText(value) ? value.trim().toLowerCase() : null;
    }

    private String format(LocalDateTime time) {
        return time == null ? null : time.format(DATE_TIME_FMT);
    }

    private LocalDateTime parseDateTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return value.contains("T") ? LocalDateTime.parse(value) : LocalDateTime.parse(value.trim(), DATE_TIME_FMT);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "时间格式应为 yyyy-MM-dd HH:mm:ss");
        }
    }

    private int count(String sql, Object... args) {
        Number value = jdbcTemplate.queryForObject(sql, Number.class, args);
        return value == null ? 0 : value.intValue();
    }

    private BusinessException error(SystemErrorCode code) {
        return new BusinessException(code.getCode(), code.getMsg());
    }
}