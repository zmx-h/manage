package com.iecmp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.system.entity.SysRegisterApply;
import org.apache.ibatis.annotations.Mapper;

/**
 * 注册申请表（{@code sys_register_apply}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/system/SysRegisterApplyMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code UNIQUE KEY uk_student_no (student_no, deleted)}</li>
 *   <li>{@code KEY idx_status (status)}</li>
 *   <li>{@code KEY idx_apply_time (apply_time)}</li>
 *   <li>{@code KEY idx_ip (ip)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface SysRegisterApplyMapper extends BaseMapper<SysRegisterApply> {
}
