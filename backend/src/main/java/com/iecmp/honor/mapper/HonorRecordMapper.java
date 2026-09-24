package com.iecmp.honor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.honor.entity.HonorRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 荣誉记录表（{@code honor_record}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/honor/HonorRecordMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_alumni_sort (alumni_id, sort_no)}</li>
 *   <li>{@code KEY idx_level (level)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface HonorRecordMapper extends BaseMapper<HonorRecord> {
}
