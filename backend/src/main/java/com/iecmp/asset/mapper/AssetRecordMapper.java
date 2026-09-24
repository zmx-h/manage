package com.iecmp.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.asset.entity.AssetRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产变动流水表（{@code asset_record}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/asset/AssetRecordMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_asset_time (asset_id, record_time)}</li>
 *   <li>{@code KEY idx_record_type (record_type)}</li>
 *   <li>{@code KEY idx_operator_id (operator_id)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface AssetRecordMapper extends BaseMapper<AssetRecord> {
}
