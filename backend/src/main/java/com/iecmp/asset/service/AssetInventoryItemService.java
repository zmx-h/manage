package com.iecmp.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.asset.dto.AssetInventoryItemUpdateDTO;
import com.iecmp.asset.entity.AssetInventoryItem;

/**
 * 盘点明细业务接口（{@code asset_inventory_item}）。
 *
 * @author IECMP 后端组
 */
public interface AssetInventoryItemService extends IService<AssetInventoryItem> {

    /** 4-25 盘点明细登记。 */
    void updateItem(Long id, AssetInventoryItemUpdateDTO dto);
}
