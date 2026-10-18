package com.iecmp.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.asset.dto.AssetInventoryStatDTO;
import com.iecmp.asset.entity.AssetInventoryItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.Collection;
import java.util.List;

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
    /**
     * 批量生成盘点明细。
     *
     * <p>一次 INSERT 多值，避免盘点任务发起时逐条往返数据库。
     */
    @Insert("<script>INSERT INTO asset_inventory_item (inventory_id, asset_id, check_status) VALUES "
            + "<foreach collection='items' item='item' separator=','>"
            + "(#{item.inventoryId}, #{item.assetId}, #{item.checkStatus})"
            + "</foreach></script>")
    int insertBatch(@Param("items") List<AssetInventoryItem> items);

    /**
     * 批量统计盘点任务的明细数量，供任务分页使用。
     *
     * <p>直接在数据库聚合，避免把每页所有明细都加载到 JVM 再计数。
     */
    @Select("<script>"
            + "SELECT inventory_id, COUNT(*) AS total_count, "
            + "COALESCE(SUM(CASE WHEN check_status = 1 THEN 1 ELSE 0 END), 0) AS normal_count, "
            + "COALESCE(SUM(CASE WHEN check_status IN (2, 3) THEN 1 ELSE 0 END), 0) AS abnormal_count, "
            + "COALESCE(SUM(CASE WHEN check_status IN (1, 2, 3) THEN 1 ELSE 0 END), 0) AS checked_count "
            + "FROM asset_inventory_item "
            + "WHERE deleted = 0 AND inventory_id IN "
            + "<foreach collection='inventoryIds' item='id' open='(' separator=',' close=')'>#{id}</foreach> "
            + "GROUP BY inventory_id"
            + "</script>")
    List<AssetInventoryStatDTO> selectStatByInventoryIds(@Param("inventoryIds") Collection<Long> inventoryIds);
}
