package com.iecmp.commonm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.commonm.entity.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志表（{@code sys_oper_log}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/commonm/SysOperLogMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_module_operation (module, operation)}</li>
 *   <li>{@code KEY idx_operator_id (operator_id)}</li>
 *   <li>{@code KEY idx_status (status)}</li>
 *   <li>{@code KEY idx_create_time (create_time)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
}
