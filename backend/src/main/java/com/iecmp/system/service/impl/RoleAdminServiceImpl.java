package com.iecmp.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.system.dto.RoleAddDTO;
import com.iecmp.system.dto.RoleUpdateDTO;
import com.iecmp.system.entity.SysRole;
import com.iecmp.system.entity.SysRolePermission;
import com.iecmp.system.entity.SysUserRole;
import com.iecmp.system.enums.SystemErrorCode;
import com.iecmp.system.query.RolePageQuery;
import com.iecmp.system.service.PermissionCatalog;
import com.iecmp.system.service.RoleAdminService;
import com.iecmp.system.service.SysRolePermissionService;
import com.iecmp.system.service.SysRoleService;
import com.iecmp.system.service.SysUserRoleService;
import com.iecmp.system.vo.PermissionTreeVO;
import com.iecmp.system.vo.RoleCreateVO;
import com.iecmp.system.vo.RoleListVO;
import com.iecmp.system.vo.RolePageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/** 管理端角色与权限管理实现。 */
@Service
@RequiredArgsConstructor
public class RoleAdminServiceImpl implements RoleAdminService {

    private static final Set<String> BUILT_IN_CODES =
            Set.of("SUPER_ADMIN", "CENTER_HEAD", "MINISTER", "MEMBER");
    private static final String SUPER_ADMIN = "SUPER_ADMIN";
    private static final Pattern ROLE_CODE_PATTERN = Pattern.compile("^[A-Z][A-Z_]*$");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SysRoleService sysRoleService;
    private final SysRolePermissionService sysRolePermissionService;
    private final SysUserRoleService sysUserRoleService;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<RoleListVO> listEnabled() {
        return sysRoleService.list(Wrappers.<SysRole>lambdaQuery()
                        .eq(SysRole::getStatus, 1)
                        .orderByAsc(SysRole::getSortNo)
                        .orderByAsc(SysRole::getId))
                .stream().map(role -> {
                    RoleListVO vo = new RoleListVO();
                    vo.setRoleId(role.getId());
                    vo.setRoleName(role.getRoleName());
                    vo.setRoleCode(role.getRoleCode());
                    return vo;
                }).toList();
    }

    @Override
    public PageResult<RolePageVO> page(RolePageQuery query) {
        var wrapper = Wrappers.<SysRole>lambdaQuery()
                .eq(query.getStatus() != null, SysRole::getStatus, query.getStatus());
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(w -> w.like(SysRole::getRoleName, keyword)
                    .or().like(SysRole::getRoleCode, keyword));
        }
        wrapper.orderByAsc(SysRole::getSortNo).orderByAsc(SysRole::getId);
        IPage<SysRole> page = sysRoleService.page(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<Long> roleIds = page.getRecords().stream().map(SysRole::getId).toList();
        Map<Long, Integer> permissionCounts = countByRoleId("sys_role_permission", "perm_code", roleIds);
        Map<Long, Integer> userCounts = countByRoleId("sys_user_role", "user_id", roleIds);
        return PageResult.of(page, role -> toPageVO(role,
                permissionCounts.getOrDefault(role.getId(), 0),
                userCounts.getOrDefault(role.getId(), 0)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleCreateVO create(RoleAddDTO dto) {
        String roleName = requireText(dto.getRoleName(), 32, "角色名称");
        String roleCode = validateRoleCode(dto.getRoleCode());
        if (countRoleName(roleName, null) > 0) {
            throw error(SystemErrorCode.E10020);
        }
        if (countRoleCode(roleCode, null) > 0) {
            throw error(SystemErrorCode.E10021);
        }
        Set<String> permCodes = validatePermCodes(dto.getPermCodes());
        SysRole role = new SysRole();
        role.setRoleName(roleName);
        role.setRoleCode(roleCode);
        role.setDataScope(validateDataScope(dto.getDataScope(), 2));
        role.setSortNo(validateSortNo(dto.getSortNo()));
        role.setStatus(validateStatus(dto.getStatus(), 1));
        role.setRemark(validateRemark(dto.getRemark()));
        sysRoleService.save(role);
        savePermissions(role.getId(), permCodes);
        RoleCreateVO vo = new RoleCreateVO();
        vo.setRoleId(role.getId());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RoleUpdateDTO dto) {
        SysRole role = requireRole(dto.getRoleId());
        String roleName = requireText(dto.getRoleName(), 32, "角色名称");
        String roleCode = validateRoleCode(dto.getRoleCode());
        boolean builtIn = BUILT_IN_CODES.contains(role.getRoleCode());
        if (builtIn && !role.getRoleCode().equals(roleCode)) {
            throw error(SystemErrorCode.E10024);
        }
        if (countRoleName(roleName, role.getId()) > 0) {
            throw error(SystemErrorCode.E10020);
        }
        if (!builtIn && countRoleCode(roleCode, role.getId()) > 0) {
            throw error(SystemErrorCode.E10021);
        }
        role.setRoleName(roleName);
        role.setRoleCode(builtIn ? role.getRoleCode() : roleCode);
        role.setDataScope(validateDataScope(dto.getDataScope(), role.getDataScope()));
        role.setSortNo(validateSortNo(dto.getSortNo()));
        role.setStatus(validateStatus(dto.getStatus(), role.getStatus()));
        role.setRemark(validateRemark(dto.getRemark()));
        sysRoleService.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysRole role = requireRole(id);
        if (BUILT_IN_CODES.contains(role.getRoleCode())) {
            throw error(SystemErrorCode.E10026);
        }
        long userCount = sysUserRoleService.count(Wrappers.<SysUserRole>lambdaQuery()
                .eq(SysUserRole::getRoleId, id));
        if (userCount > 0) {
            throw error(SystemErrorCode.E10025);
        }
        sysRolePermissionService.remove(Wrappers.<SysRolePermission>lambdaQuery()
                .eq(SysRolePermission::getRoleId, id));
        sysRoleService.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long id, List<String> permCodes) {
        SysRole role = requireRole(id);
        if (SUPER_ADMIN.equals(role.getRoleCode())) {
            throw error(SystemErrorCode.E10027);
        }
        Set<String> checked = validatePermCodes(permCodes);
        sysRolePermissionService.remove(Wrappers.<SysRolePermission>lambdaQuery()
                .eq(SysRolePermission::getRoleId, id));
        savePermissions(id, checked);
    }

    @Override
    public List<PermissionTreeVO> permissionTree() {
        return PermissionCatalog.tree();
    }

    private void savePermissions(Long roleId, Set<String> permCodes) {
        if (permCodes.isEmpty()) {
            return;
        }
        List<SysRolePermission> links = new ArrayList<>(permCodes.size());
        for (String permCode : permCodes) {
            SysRolePermission link = new SysRolePermission();
            link.setRoleId(roleId);
            link.setPermCode(permCode);
            links.add(link);
        }
        sysRolePermissionService.saveBatch(links);
    }

    private Set<String> validatePermCodes(List<String> permCodes) {
        if (permCodes == null || permCodes.isEmpty()) {
            return Set.of();
        }
        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (String code : permCodes) {
            if (!StringUtils.hasText(code)) {
                throw error(SystemErrorCode.E10022);
            }
            String value = code.trim();
            if (!PermissionCatalog.codes().contains(value)) {
                throw error(SystemErrorCode.E10022);
            }
            result.add(value);
        }
        return result;
    }

    private SysRole requireRole(Long id) {
        SysRole role = id == null ? null : sysRoleService.getById(id);
        if (role == null) {
            throw error(SystemErrorCode.E10015);
        }
        return role;
    }

    private long countRoleName(String roleName, Long excludeId) {
        return sysRoleService.count(Wrappers.<SysRole>lambdaQuery()
                .eq(SysRole::getRoleName, roleName)
                .ne(excludeId != null, SysRole::getId, excludeId));
    }

    private long countRoleCode(String roleCode, Long excludeId) {
        return sysRoleService.count(Wrappers.<SysRole>lambdaQuery()
                .eq(SysRole::getRoleCode, roleCode)
                .ne(excludeId != null, SysRole::getId, excludeId));
    }

    private String validateRoleCode(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "角色编码不能为空");
        }
        String code = value.trim();
        if (code.length() > 32 || !ROLE_CODE_PATTERN.matcher(code).matches()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "角色编码仅支持大写字母和下划线，最长 32 位");
        }
        return code;
    }

    private String requireText(String value, int maxLength, String label) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, label + "不能为空");
        }
        String text = value.trim();
        if (text.length() > maxLength) {
            throw new BusinessException(ResultCode.PARAM_ERROR, label + "最长 " + maxLength + " 字");
        }
        return text;
    }

    private Integer validateDataScope(Integer value, Integer fallback) {
        int result = value == null ? (fallback == null ? 2 : fallback) : value;
        if (result != 1 && result != 2) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "dataScope 仅支持 1 或 2");
        }
        return result;
    }

    private Integer validateStatus(Integer value, Integer fallback) {
        int result = value == null ? (fallback == null ? 1 : fallback) : value;
        if (result != 0 && result != 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "status 仅支持 0 或 1");
        }
        return result;
    }

    private Integer validateSortNo(Integer value) {
        int result = value == null ? 0 : value;
        if (result < 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "排序号不能为负数");
        }
        return result;
    }

    private String validateRemark(String value) {
        if (value == null) {
            return null;
        }
        String remark = value.trim();
        if (remark.length() > 255) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "备注最长 255 字");
        }
        return remark.isEmpty() ? null : remark;
    }

    private Map<Long, Integer> countByRoleId(String table, String countColumn, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Map.of();
        }
        String placeholders = String.join(",", roleIds.stream().map(id -> "?").toList());
        String sql = "SELECT role_id, COUNT(" + countColumn + ") AS cnt FROM " + table
                + " WHERE deleted = 0 AND role_id IN (" + placeholders + ") GROUP BY role_id";
        Map<Long, Integer> result = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            result.put(rs.getLong("role_id"), rs.getInt("cnt"));
        }, roleIds.toArray());
        return result;
    }

    private RolePageVO toPageVO(SysRole role, int permissionCount, int userCount) {
        RolePageVO vo = new RolePageVO();
        vo.setRoleId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleCode(role.getRoleCode());
        vo.setDataScope(role.getDataScope());
        vo.setPermissionCount(permissionCount);
        vo.setUserCount(userCount);
        vo.setStatus(role.getStatus());
        vo.setRemark(role.getRemark());
        vo.setCreateTime(format(role.getCreateTime()));
        return vo;
    }

    private String format(LocalDateTime time) {
        return time == null ? null : time.format(DATE_TIME_FMT);
    }

    private BusinessException error(SystemErrorCode code) {
        return new BusinessException(code.getCode(), code.getMsg());
    }
}