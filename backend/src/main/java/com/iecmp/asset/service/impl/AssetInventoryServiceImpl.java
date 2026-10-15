package com.iecmp.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.asset.entity.AssetInventory;
import com.iecmp.asset.mapper.AssetInventoryMapper;
import com.iecmp.asset.service.AssetInventoryService;
import org.springframework.stereotype.Service;

/**
 * 盘点任务业务实现（{@code asset_inventory}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link AssetInventoryService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class AssetInventoryServiceImpl extends ServiceImpl<AssetInventoryMapper, AssetInventory>
        implements AssetInventoryService {
}
