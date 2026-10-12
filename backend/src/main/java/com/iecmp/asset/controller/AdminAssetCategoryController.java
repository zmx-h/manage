package com.iecmp.asset.controller;

import com.iecmp.asset.dto.AssetCategoryAddDTO;
import com.iecmp.asset.dto.AssetCategoryUpdateDTO;
import com.iecmp.asset.service.AssetCategoryService;
import com.iecmp.asset.service.AssetInfoService;
import com.iecmp.asset.vo.AssetCategoryCreateVO;
import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.annotation.RequiresPerm;
import com.iecmp.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-资产分类（asset 模块）。
 *
 * <p>覆盖接口：4-17、4-18、4-19，共 3 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-资产分类")
@RestController
@RequiredArgsConstructor
public class AdminAssetCategoryController {

    /** AssetCategory 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetCategoryService assetCategoryService;
    /** AssetInfo 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetInfoService assetInfoService;

    /**
     * 4-17 新增资产分类。
     *
     * <p>{@code POST /admin/asset/category} —— 新增资产分类，支持二级分类（`parentId` 指向父分类）
     *
     * <p>权限：asset:category:edit；令牌：是。
     */
    @Operation(summary = "4-17 新增资产分类", description = "新增资产分类，支持二级分类（`parentId` 指向父分类）")
    @RequiresPerm("asset:category:edit")
    @OperLog(module = "管理端-资产分类", value = "新增资产分类")
    @PostMapping("/admin/asset/category")
    public Result<AssetCategoryCreateVO> postAssetCategory(@RequestBody @Valid AssetCategoryAddDTO dto) {
        return Result.success(assetCategoryService.add(dto));
    }

    /**
     * 4-18 修改资产分类。
     *
     * <p>{@code PUT /admin/asset/category} —— 修改分类名称、父分类与排序
     *
     * <p>权限：asset:category:edit；令牌：是。
     */
    @Operation(summary = "4-18 修改资产分类", description = "修改分类名称、父分类与排序")
    @RequiresPerm("asset:category:edit")
    @OperLog(module = "管理端-资产分类", value = "修改资产分类")
    @PutMapping("/admin/asset/category")
    public Result<Void> putAssetCategory(@RequestBody @Valid AssetCategoryUpdateDTO dto) {
        assetCategoryService.update(dto);
        return Result.success();
    }

    /**
     * 4-19 删除资产分类。
     *
     * <p>{@code DELETE /admin/asset/category/{id}} —— 逻辑删除分类；存在子分类或已被资产引用时禁止删除
     *
     * <p>权限：asset:category:edit；令牌：是。
     */
    @Operation(summary = "4-19 删除资产分类", description = "逻辑删除分类；存在子分类或已被资产引用时禁止删除")
    @RequiresPerm("asset:category:edit")
    @OperLog(module = "管理端-资产分类", value = "删除资产分类")
    @DeleteMapping("/admin/asset/category/{id}")
    public Result<Void> deleteAssetCategory(@PathVariable("id") Long id) {
        assetCategoryService.remove(id);
        return Result.success();
    }
}
