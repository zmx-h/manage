package com.iecmp.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.asset.entity.AssetInventoryItem;
import com.iecmp.asset.mapper.AssetInventoryItemMapper;
import com.iecmp.asset.service.AssetInventoryItemService;
import org.springframework.stereotype.Service;

/**
 * 盘点明细业务实现（{@code asset_inventory_item}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link AssetInventoryItemService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class AssetInventoryItemServiceImpl extends ServiceImpl<AssetInventoryItemMapper, AssetInventoryItem>
        implements AssetInventoryItemService {
}
