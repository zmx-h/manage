package com.iecmp.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.asset.entity.AssetBorrow;
import com.iecmp.asset.mapper.AssetBorrowMapper;
import com.iecmp.asset.service.AssetBorrowService;
import org.springframework.stereotype.Service;

/**
 * 借用/领用业务实现（{@code asset_borrow}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link AssetBorrowService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class AssetBorrowServiceImpl extends ServiceImpl<AssetBorrowMapper, AssetBorrow>
        implements AssetBorrowService {
}
