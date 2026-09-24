package com.iecmp.asset.controller;

import com.iecmp.asset.dto.AdminAssetBorrowReturnUpdateDTO;
import com.iecmp.asset.dto.AssetBorrowAuditDTO;
import com.iecmp.asset.query.AssetBorrowPageQuery;
import com.iecmp.asset.service.AssetBorrowService;
import com.iecmp.asset.service.AssetInfoService;
import com.iecmp.asset.service.AssetRecordService;
import com.iecmp.asset.vo.AssetBorrowPageVO;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端-借用审核（asset 模块）。
 *
 * <p>覆盖接口：4-20、4-21、4-22，共 3 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "管理端-借用审核")
@RestController
@RequiredArgsConstructor
public class AdminAssetBorrowController {

    /** AssetBorrow 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetBorrowService assetBorrowService;
    /** AssetInfo 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetInfoService assetInfoService;
    /** AssetRecord 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetRecordService assetRecordService;

    /**
     * 4-20 借用审核分页。
     *
     * <p>{@code GET /admin/asset/borrow/page} —— 管理端分页查询借用 / 领用申请，用于审核排队与在借跟踪
     *
     * <p>权限：asset:borrow:list；令牌：是；分页返回。
     */
    @Operation(summary = "4-20 借用审核分页", description = "管理端分页查询借用 / 领用申请，用于审核排队与在借跟踪")
    @RequiresPerm("asset:borrow:list")
    @GetMapping("/admin/asset/borrow/page")
    public Result<PageResult<AssetBorrowPageVO>> getAssetBorrowPage(@ParameterObject AssetBorrowPageQuery query) {
        // TODO 待实现（接口 4-20）：见《IECMP-接口开发文档-v1.6.md》接口 4-20
        return Result.todo("4-20");
    }

    /**
     * 4-21 借用审核。
     *
     * <p>{@code PUT /admin/asset/borrow/{id}/audit} —— 审核借用 / 领用申请；通过时把资产状态置为「已借出」
     *
     * <p>权限：asset:borrow:audit；令牌：是。
     */
    @Operation(summary = "4-21 借用审核", description = "审核借用 / 领用申请；通过时把资产状态置为「已借出」")
    @RequiresPerm("asset:borrow:audit")
    @OperLog(module = "管理端-借用审核", value = "借用审核")
    @PutMapping("/admin/asset/borrow/{id}/audit")
    public Result<Void> putAssetBorrowAudit(@PathVariable("id") Long id, @RequestBody @Valid AssetBorrowAuditDTO dto) {
        // TODO 待实现（接口 4-21）：见《IECMP-接口开发文档-v1.6.md》接口 4-21
        return Result.todo("4-21");
    }

    /**
     * 4-22 归还登记（后台）。
     *
     * <p>{@code PUT /admin/asset/borrow/{id}/return} —— 管理员代为登记归还，适用于成员未在端上操作或资产由管理员收回的场景
     *
     * <p>权限：asset:borrow:edit；令牌：是。
     */
    @Operation(summary = "4-22 归还登记（后台）", description = "管理员代为登记归还，适用于成员未在端上操作或资产由管理员收回的场景")
    @RequiresPerm("asset:borrow:edit")
    @OperLog(module = "管理端-借用审核", value = "归还登记（后台）")
    @PutMapping("/admin/asset/borrow/{id}/return")
    public Result<Void> putAssetBorrowReturn(@PathVariable("id") Long id, @RequestBody @Valid AdminAssetBorrowReturnUpdateDTO dto) {
        // TODO 待实现（接口 4-22）：见《IECMP-接口开发文档-v1.6.md》接口 4-22
        return Result.todo("4-22");
    }
}
