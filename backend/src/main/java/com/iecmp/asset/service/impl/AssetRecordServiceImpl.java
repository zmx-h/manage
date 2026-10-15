package com.iecmp.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.asset.entity.AssetRecord;
import com.iecmp.asset.mapper.AssetRecordMapper;
import com.iecmp.asset.service.AssetRecordService;
import org.springframework.stereotype.Service;

/**
 * 资产变动流水业务实现（{@code asset_record}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link AssetRecordService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class AssetRecordServiceImpl extends ServiceImpl<AssetRecordMapper, AssetRecord>
        implements AssetRecordService {
}
