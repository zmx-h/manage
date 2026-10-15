package com.iecmp.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.asset.entity.AssetInventoryItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 盘点明细表（{@code asset_inventory_item}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/asset/AssetInventoryItemMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code UNIQUE KEY uk_inventory_asset (inventory_id, asset_id, deleted)}</li>
 *   <li>{@code KEY idx_asset_id (asset_id)}</li>
 *   <li>{@code KEY idx_check_status (check_status)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface AssetInventoryItemMapper extends BaseMapper<AssetInventoryItem> {
}
