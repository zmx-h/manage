package com.iecmp.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.asset.entity.AssetCategory;
import com.iecmp.asset.mapper.AssetCategoryMapper;
import com.iecmp.asset.service.AssetCategoryService;
import org.springframework.stereotype.Service;

/**
 * 资产分类业务实现（{@code asset_category}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link AssetCategoryService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class AssetCategoryServiceImpl extends ServiceImpl<AssetCategoryMapper, AssetCategory>
        implements AssetCategoryService {
}
