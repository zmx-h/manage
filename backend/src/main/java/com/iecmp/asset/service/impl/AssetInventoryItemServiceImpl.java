package com.iecmp.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.asset.dto.AssetInventoryItemUpdateDTO;
import com.iecmp.asset.entity.AssetInventory;
import com.iecmp.asset.entity.AssetInventoryItem;
import com.iecmp.asset.enums.AssetErrorCode;
import com.iecmp.asset.enums.InventoryCheckStatusEnum;
import com.iecmp.asset.enums.InventoryStatusEnum;
import com.iecmp.asset.mapper.AssetInventoryItemMapper;
import com.iecmp.asset.mapper.AssetInventoryMapper;
import com.iecmp.asset.service.AssetInventoryItemService;
import com.iecmp.common.context.UserContext;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 盘点明细业务实现（{@code asset_inventory_item}）。
 *
 * @author IECMP 后端组
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetInventoryItemServiceImpl extends ServiceImpl<AssetInventoryItemMapper, AssetInventoryItem>
        implements AssetInventoryItemService {

    /** 直接注入盘点任务 Mapper，避免与 AssetInventoryService 循环依赖。 */
    private final AssetInventoryMapper assetInventoryMapper;

    // ============================== 4-25 盘点明细登记 ==============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateItem(Long id, AssetInventoryItemUpdateDTO dto) {
        // 1. 明细存在性校验
        AssetInventoryItem item = this.getById(id);
        if (item == null) {
            throw new BusinessException(AssetErrorCode.E40014.getCode(), AssetErrorCode.E40014.getMsg());
        }

        // 2. 关联盘点任务未结束
        AssetInventory inventory = assetInventoryMapper.selectById(item.getInventoryId());
        if (inventory == null) {
            throw new BusinessException(AssetErrorCode.E40012.getCode(), AssetErrorCode.E40012.getMsg());
        }
        if (InventoryStatusEnum.COMPLETED.getValue().equals(inventory.getStatus())) {
            throw new BusinessException(AssetErrorCode.E40013.getCode(), AssetErrorCode.E40013.getMsg());
        }

        // 3. 校验盘点结果并更新
        Integer checkStatus = dto.getCheckStatus();
        if (InventoryCheckStatusEnum.of(checkStatus) == null
                || InventoryCheckStatusEnum.UNCHECKED.getValue().equals(checkStatus)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "盘点结果只能为 1 正常、2 盘亏、3 位置不符");
        }
        item.setCheckStatus(checkStatus);
        item.setCheckUserId(UserContext.getUserIdOrDefault());
        item.setCheckTime(LocalDateTime.now());
        item.setRemark(dto.getRemark());
        this.updateById(item);
    }
}
