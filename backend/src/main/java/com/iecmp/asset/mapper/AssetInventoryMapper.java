package com.iecmp.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.asset.entity.AssetInventory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 盘点任务表（{@code asset_inventory}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/asset/AssetInventoryMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_status (status)}</li>
 *   <li>{@code KEY idx_begin_time (begin_time)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface AssetInventoryMapper extends BaseMapper<AssetInventory> {
}
