package com.iecmp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联表（{@code sys_user_role}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/system/SysUserRoleMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code UNIQUE KEY uk_user_role (user_id, role_id, deleted)}</li>
 *   <li>{@code KEY idx_role_id (role_id)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}
