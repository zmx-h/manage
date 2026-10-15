package com.iecmp.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.asset.entity.AssetInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产台账表（{@code asset_info}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/asset/AssetInfoMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code UNIQUE KEY uk_asset_no (asset_no, deleted)}</li>
 *   <li>{@code KEY idx_category_id (category_id)}</li>
 *   <li>{@code KEY idx_keeper_id (keeper_id)}</li>
 *   <li>{@code KEY idx_budget_expense_id (budget_expense_id)}</li>
 *   <li>{@code KEY idx_status (status)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface AssetInfoMapper extends BaseMapper<AssetInfo> {
}
