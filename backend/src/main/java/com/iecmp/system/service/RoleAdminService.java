package com.iecmp.system.service;

import com.iecmp.common.result.PageResult;
import com.iecmp.system.dto.RoleAddDTO;
import com.iecmp.system.dto.RoleUpdateDTO;
import com.iecmp.system.query.RolePageQuery;
import com.iecmp.system.vo.PermissionTreeVO;
import com.iecmp.system.vo.RoleCreateVO;
import com.iecmp.system.vo.RoleListVO;
import com.iecmp.system.vo.RolePageVO;

import java.util.List;

/** 管理端角色与权限管理（1-15 ~ 1-21）。 */
public interface RoleAdminService {

    List<RoleListVO> listEnabled();

    PageResult<RolePageVO> page(RolePageQuery query);

    RoleCreateVO create(RoleAddDTO dto);

    void update(RoleUpdateDTO dto);

    void delete(Long id);

    void assignPermissions(Long id, List<String> permCodes);

    List<PermissionTreeVO> permissionTree();
}