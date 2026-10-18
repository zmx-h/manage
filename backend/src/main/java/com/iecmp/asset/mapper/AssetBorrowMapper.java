package com.iecmp.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.asset.entity.AssetBorrow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 借用/领用表（{@code asset_borrow}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/asset/AssetBorrowMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_asset_status (asset_id, status)}</li>
 *   <li>{@code KEY idx_user_status_time (user_id, status, borrow_time)}</li>
 *   <li>{@code KEY idx_status_time (status, borrow_time)}</li>
 *   <li>{@code KEY idx_expect_return_time (expect_return_time)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface AssetBorrowMapper extends BaseMapper<AssetBorrow> {
}
