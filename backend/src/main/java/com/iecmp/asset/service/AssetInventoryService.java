package com.iecmp.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.asset.dto.AssetInventoryAddDTO;
import com.iecmp.asset.dto.AssetInventoryFinishDTO;
import com.iecmp.asset.entity.AssetInventory;
import com.iecmp.asset.query.AssetInventoryPageQuery;
import com.iecmp.asset.vo.AssetInventoryCreateVO;
import com.iecmp.asset.vo.AssetInventoryFinishVO;
import com.iecmp.asset.vo.AssetInventoryPageVO;
import com.iecmp.common.result.PageResult;

/**
 * 盘点任务业务接口（{@code asset_inventory}）。
 *
 * @author IECMP 后端组
 */
public interface AssetInventoryService extends IService<AssetInventory> {

    /** 4-23 发起盘点。 */
    AssetInventoryCreateVO add(AssetInventoryAddDTO dto);

    /** 4-24 盘点任务分页。 */
    PageResult<AssetInventoryPageVO> page(AssetInventoryPageQuery query);

    /** 4-26 结束盘点。 */
    AssetInventoryFinishVO finish(Long id, AssetInventoryFinishDTO dto);
}
