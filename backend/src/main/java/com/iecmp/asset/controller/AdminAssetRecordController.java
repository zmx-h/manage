package com.iecmp.asset.controller;

import com.iecmp.asset.dto.AssetRecordAddDTO;
import com.iecmp.asset.query.AssetRecordPageQuery;
import com.iecmp.asset.service.AssetRecordService;
import com.iecmp.asset.vo.AssetRecordCreateVO;
import com.iecmp.asset.vo.AssetRecordPageVO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-变动流水（asset 模块）。
 *
 * <p>覆盖接口：4-15、4-16，共 2 个。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-变动流水")
@RestController
@RequiredArgsConstructor
public class AdminAssetRecordController {

    private final AssetRecordService assetRecordService;

    /**
     * 4-15 变动记录登记。
     *
     * <p>{@code POST /admin/asset/record} —— 手动登记一条资产变动流水，用于系统未自动覆盖的场景。
     *
     * <p>权限：asset:record:add；令牌：是。
     */
    @Operation(summary = "4-15 变动记录登记", description = "手动登记一条资产变动流水，用于系统未自动覆盖的场景（如外部调拨、遗留资产补录）")
    @RequiresPerm("asset:record:add")
    @OperLog(module = "管理端-变动流水", value = "变动记录登记")
    @PostMapping("/admin/asset/record")
    public Result<AssetRecordCreateVO> postAssetRecord(@RequestBody @Valid AssetRecordAddDTO dto) {
        return Result.success(assetRecordService.createRecord(dto));
    }

    /**
     * 4-16 变动流水分页。
     *
     * <p>{@code GET /admin/asset/record/page} —— 分页查询资产变动流水，用于资产去向追溯。
     *
     * <p>权限：asset:record:list；令牌：是；分页返回。
     */
    @Operation(summary = "4-16 变动流水分页", description = "分页查询资产变动流水，用于资产去向追溯")
    @RequiresPerm("asset:record:list")
    @GetMapping("/admin/asset/record/page")
    public Result<PageResult<AssetRecordPageVO>> getAssetRecordPage(@ParameterObject AssetRecordPageQuery query) {
        return Result.success(assetRecordService.pageRecords(query));
    }
}
