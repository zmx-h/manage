package com.iecmp.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.asset.entity.AssetInfo;
import com.iecmp.asset.mapper.AssetInfoMapper;
import com.iecmp.asset.service.AssetInfoService;
import org.springframework.stereotype.Service;

/**
 * 资产台账业务实现（{@code asset_info}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link AssetInfoService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class AssetInfoServiceImpl extends ServiceImpl<AssetInfoMapper, AssetInfo>
        implements AssetInfoService {
}
