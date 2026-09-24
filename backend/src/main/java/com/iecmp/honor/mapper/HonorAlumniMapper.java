package com.iecmp.honor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.honor.entity.HonorAlumni;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学长学姐信息表（{@code honor_alumni}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/honor/HonorAlumniMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_status_show (status, is_show)}</li>
 *   <li>{@code KEY idx_submit_user (submit_user_id)}</li>
 *   <li>{@code KEY idx_graduation_year (graduation_year)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface HonorAlumniMapper extends BaseMapper<HonorAlumni> {
}
