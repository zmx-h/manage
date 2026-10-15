package com.iecmp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色表（{@code sys_role}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/system/SysRoleMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code UNIQUE KEY uk_role_code (role_code, deleted)}</li>
 *   <li>{@code UNIQUE KEY uk_role_name (role_name, deleted)}</li>
 *   <li>{@code KEY idx_status (status)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
}
