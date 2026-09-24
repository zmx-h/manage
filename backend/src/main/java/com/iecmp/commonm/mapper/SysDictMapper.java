package com.iecmp.commonm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.commonm.entity.SysDict;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据字典表（补充表）（{@code sys_dict}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/commonm/SysDictMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code UNIQUE KEY uk_type_value (dict_type, dict_value, deleted)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface SysDictMapper extends BaseMapper<SysDict> {
}
