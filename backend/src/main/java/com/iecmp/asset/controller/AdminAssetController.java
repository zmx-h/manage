package com.iecmp.asset.controller;

import com.iecmp.asset.dto.AssetAddDTO;
import com.iecmp.asset.dto.AssetAssignDTO;
import com.iecmp.asset.dto.AssetStatusDTO;
import com.iecmp.asset.dto.AssetUpdateDTO;
import com.iecmp.asset.query.AdminAssetPageQuery;
import com.iecmp.asset.query.AssetExportQuery;
import com.iecmp.asset.service.AssetBorrowService;
import com.iecmp.asset.service.AssetCategoryService;
import com.iecmp.asset.service.AssetInfoService;
import com.iecmp.asset.service.AssetRecordService;
import com.iecmp.asset.vo.AdminAssetDetailVO;
import com.iecmp.asset.vo.AdminAssetPageVO;
import com.iecmp.asset.vo.AssetCreateVO;
import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-资产台账（asset 模块）。
 *
 * <p>覆盖接口：4-08、4-09、4-10、4-11、4-12、4-13、4-14、4-27，共 8 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-资产台账")
@RestController
@RequiredArgsConstructor
public class AdminAssetController {

    /** AssetInfo 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetInfoService assetInfoService;
    /** AssetCategory 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetCategoryService assetCategoryService;
    /** AssetRecord 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetRecordService assetRecordService;
    /** AssetBorrow 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetBorrowService assetBorrowService;

    /**
     * 4-08 资产分页（后台）。
     *
     * <p>{@code GET /admin/asset/page} —— 管理端分页查询资产台账，支持更多筛选维度
     *
     * <p>权限：asset:info:list；令牌：是；分页返回。
     */
    @Operation(summary = "4-08 资产分页（后台）", description = "管理端分页查询资产台账，支持更多筛选维度")
    @RequiresPerm("asset:info:list")
    @GetMapping("/admin/asset/page")
    public Result<PageResult<AdminAssetPageVO>> getAssetPage(@ParameterObject AdminAssetPageQuery query) {
        return Result.success(assetInfoService.page(query));
    }

    /**
     * 4-09 资产详情（后台）。
     *
     * <p>{@code GET /admin/asset/{id}} —— 查询资产完整信息、变动流水与借用历史，用于编辑回显与追溯
     *
     * <p>权限：asset:info:query；令牌：是。
     */
    @Operation(summary = "4-09 资产详情（后台）", description = "查询资产完整信息、变动流水与借用历史，用于编辑回显与追溯")
    @RequiresPerm("asset:info:query")
    @GetMapping("/admin/asset/{id}")
    public Result<AdminAssetDetailVO> getAsset(@PathVariable("id") Long id) {
        return Result.success(assetInfoService.detail(id));
    }

    /**
     * 4-10 新增资产。
     *
     * <p>{@code POST /admin/asset} —— 台账入账。若资产来自预算采购，须填写 `budgetExpenseId` 形成「采购 → 入账」闭环
     *
     * <p>权限：asset:info:add；令牌：是。
     */
    @Operation(summary = "4-10 新增资产", description = "台账入账。若资产来自预算采购，须填写 `budgetExpenseId` 形成「采购 → 入账」闭环")
    @RequiresPerm("asset:info:add")
    @OperLog(module = "管理端-资产台账", value = "新增资产")
    @PostMapping("/admin/asset")
    public Result<AssetCreateVO> postAsset(@RequestBody @Valid AssetAddDTO dto) {
        return Result.success(assetInfoService.add(dto));
    }

    /**
     * 4-11 修改资产。
     *
     * <p>{@code PUT /admin/asset} —— 修改资产基础信息；状态变更请用接口 `4-13`，保管人与位置分配请用接口 `4-14
     *
     * <p>权限：asset:info:edit；令牌：是。
     */
    @Operation(summary = "4-11 修改资产", description = "修改资产基础信息；状态变更请用接口 `4-13`，保管人与位置分配请用接口 `4-14")
    @RequiresPerm("asset:info:edit")
    @OperLog(module = "管理端-资产台账", value = "修改资产")
    @PutMapping("/admin/asset")
    public Result<Void> putAsset(@RequestBody @Valid AssetUpdateDTO dto) {
        assetInfoService.update(dto);
        return Result.success();
    }

    /**
     * 4-12 删除资产。
     *
     * <p>{@code DELETE /admin/asset/{id}} —— 逻辑删除资产；存在未归还借用记录时禁止删除
     *
     * <p>权限：asset:info:remove；令牌：是。
     */
    @Operation(summary = "4-12 删除资产", description = "逻辑删除资产；存在未归还借用记录时禁止删除")
    @RequiresPerm("asset:info:remove")
    @OperLog(module = "管理端-资产台账", value = "删除资产")
    @DeleteMapping("/admin/asset/{id}")
    public Result<Void> deleteAsset(@PathVariable("id") Long id) {
        assetInfoService.remove(id);
        return Result.success();
    }

    /**
     * 4-13 资产状态变更。
     *
     * <p>{@code PUT /admin/asset/{id}/status} —— 变更资产状态并自动写入一条变动流水
     *
     * <p>权限：asset:info:edit；令牌：是。
     */
    @Operation(summary = "4-13 资产状态变更", description = "变更资产状态并自动写入一条变动流水")
    @RequiresPerm("asset:info:edit")
    @OperLog(module = "管理端-资产台账", value = "资产状态变更")
    @PutMapping("/admin/asset/{id}/status")
    public Result<Void> putAssetStatus(@PathVariable("id") Long id, @RequestBody @Valid AssetStatusDTO dto) {
        assetInfoService.changeStatus(id, dto);
        return Result.success();
    }

    /**
     * 4-14 分配保管人与存放位置。
     *
     * <p>{@code PUT /admin/asset/{id}/assign} —— 指定资产的保管人与存放位置，并写入一条「调拨」流水
     *
     * <p>权限：asset:info:assign；令牌：是。
     */
    @Operation(summary = "4-14 分配保管人与存放位置", description = "指定资产的保管人与存放位置，并写入一条「调拨」流水")
    @RequiresPerm("asset:info:assign")
    @OperLog(module = "管理端-资产台账", value = "分配保管人与存放位置")
    @PutMapping("/admin/asset/{id}/assign")
    public Result<Void> putAssetAssign(@PathVariable("id") Long id, @RequestBody @Valid AssetAssignDTO dto) {
        assetInfoService.assign(id, dto);
        return Result.success();
    }

    /**
     * 4-27 资产台账导出。
     *
     * <p>{@code GET /admin/asset/export} —— 按筛选条件导出资产台账为 Excel 文件，用于线下归档与上报
     *
     * <p>权限：asset:info:export；令牌：是。
     */
    @Operation(summary = "4-27 资产台账导出", description = "按筛选条件导出资产台账为 Excel 文件，用于线下归档与上报")
    @RequiresPerm("asset:info:export")
    @GetMapping("/admin/asset/export")
    public void exportAsset(@ParameterObject AssetExportQuery query, HttpServletResponse response) {
        assetInfoService.export(query, response);
    }
}
