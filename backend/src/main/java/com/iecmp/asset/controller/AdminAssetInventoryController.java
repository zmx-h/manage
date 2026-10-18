package com.iecmp.asset.controller;

import com.iecmp.asset.dto.AssetInventoryAddDTO;
import com.iecmp.asset.dto.AssetInventoryFinishDTO;
import com.iecmp.asset.dto.AssetInventoryItemUpdateDTO;
import com.iecmp.asset.query.AssetInventoryPageQuery;
import com.iecmp.asset.service.AssetInfoService;
import com.iecmp.asset.service.AssetInventoryItemService;
import com.iecmp.asset.service.AssetInventoryService;
import com.iecmp.asset.vo.AssetInventoryCreateVO;
import com.iecmp.asset.vo.AssetInventoryFinishVO;
import com.iecmp.asset.vo.AssetInventoryPageVO;
import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-资产盘点（asset 模块）。
 *
 * <p>覆盖接口：4-23、4-24、4-25、4-26，共 4 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-资产盘点")
@RestController
@RequiredArgsConstructor
public class AdminAssetInventoryController {

    /** AssetInventory 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetInventoryService assetInventoryService;
    /** AssetInventoryItem 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetInventoryItemService assetInventoryItemService;
    /** AssetInfo 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetInfoService assetInfoService;

    /**
     * 4-23 发起盘点。
     *
     * <p>{@code POST /admin/asset/inventory} —— 创建盘点任务，系统自动为待盘点资产生成明细行
     *
     * <p>权限：asset:inventory:add；令牌：是。
     */
    @Operation(summary = "4-23 发起盘点", description = "创建盘点任务，系统自动为待盘点资产生成明细行")
    @RequiresPerm("asset:inventory:add")
    @OperLog(module = "管理端-资产盘点", value = "发起盘点")
    @PostMapping("/admin/asset/inventory")
    public Result<AssetInventoryCreateVO> postAssetInventory(@RequestBody @Valid AssetInventoryAddDTO dto) {
        return Result.success(assetInventoryService.add(dto));
    }

    /**
     * 4-24 盘点任务分页。
     *
     * <p>{@code GET /admin/asset/inventory/page} —— 分页查询盘点任务及完成进度
     *
     * <p>权限：asset:inventory:list；令牌：是；分页返回。
     */
    @Operation(summary = "4-24 盘点任务分页", description = "分页查询盘点任务及完成进度")
    @RequiresPerm("asset:inventory:list")
    @GetMapping("/admin/asset/inventory/page")
    public Result<PageResult<AssetInventoryPageVO>> getAssetInventoryPage(@ParameterObject AssetInventoryPageQuery query) {
        return Result.success(assetInventoryService.page(query));
    }

    /**
     * 4-25 盘点明细登记。
     *
     * <p>{@code PUT /admin/asset/inventory/item/{id}} —— 逐项登记盘点结果，可单条登记或批量提交
     *
     * <p>权限：asset:inventory:exec；令牌：是。
     */
    @Operation(summary = "4-25 盘点明细登记", description = "逐项登记盘点结果，可单条登记或批量提交")
    @RequiresPerm("asset:inventory:exec")
    @OperLog(module = "管理端-资产盘点", value = "盘点明细登记")
    @PutMapping("/admin/asset/inventory/item/{id}")
    public Result<Void> putAssetInventoryItem(@PathVariable("id") Long id, @RequestBody @Valid AssetInventoryItemUpdateDTO dto) {
        assetInventoryItemService.updateItem(id, dto);
        return Result.success();
    }

    /**
     * 4-26 结束盘点。
     *
     * <p>{@code PUT /admin/asset/inventory/{id}/finish} —— 结束盘点任务，汇总统计并生成差异结果
     *
     * <p>权限：asset:inventory:exec；令牌：是。
     */
    @Operation(summary = "4-26 结束盘点", description = "结束盘点任务，汇总统计并生成差异结果")
    @RequiresPerm("asset:inventory:exec")
    @OperLog(module = "管理端-资产盘点", value = "结束盘点")
    @PutMapping("/admin/asset/inventory/{id}/finish")
    public Result<AssetInventoryFinishVO> putAssetInventoryFinish(@PathVariable("id") Long id, @RequestBody @Valid AssetInventoryFinishDTO dto) {
        return Result.success(assetInventoryService.finish(id, dto));
    }
}
