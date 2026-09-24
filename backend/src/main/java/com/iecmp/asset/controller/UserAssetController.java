package com.iecmp.asset.controller;

import com.iecmp.asset.dto.AssetBorrowDTO;
import com.iecmp.asset.dto.UserAssetBorrowReturnUpdateDTO;
import com.iecmp.asset.query.AssetBorrowMyPageQuery;
import com.iecmp.asset.query.UserAssetPageQuery;
import com.iecmp.asset.service.AssetBorrowService;
import com.iecmp.asset.service.AssetCategoryService;
import com.iecmp.asset.service.AssetInfoService;
import com.iecmp.asset.service.AssetRecordService;
import com.iecmp.asset.vo.AssetBorrowMyPageVO;
import com.iecmp.asset.vo.AssetBorrowVO;
import com.iecmp.asset.vo.AssetCategoryTreeVO;
import com.iecmp.asset.vo.AssetStatOverviewVO;
import com.iecmp.asset.vo.UserAssetDetailVO;
import com.iecmp.asset.vo.UserAssetPageVO;
import com.iecmp.common.annotation.OperLog;
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
 * 用户端-资产台账与借用（asset 模块）。
 *
 * <p>覆盖接口：4-01、4-02、4-03、4-04、4-05、4-06、4-07，共 7 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-资产台账与借用")
@RestController
@RequiredArgsConstructor
public class UserAssetController {

    /** AssetInfo 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetInfoService assetInfoService;
    /** AssetCategory 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetCategoryService assetCategoryService;
    /** AssetBorrow 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetBorrowService assetBorrowService;
    /** AssetRecord 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final AssetRecordService assetRecordService;

    /**
     * 4-01 资产分页（前台）。
     *
     * <p>{@code GET /user/asset/page} —— 成员端分页查询资产台账，一物一码、单件单记录
     *
     * <p>权限：登录即可；令牌：是；分页返回。
     */
    @Operation(summary = "4-01 资产分页（前台）", description = "成员端分页查询资产台账，一物一码、单件单记录")
    @GetMapping("/user/asset/page")
    public Result<PageResult<UserAssetPageVO>> getAssetPage(@ParameterObject UserAssetPageQuery query) {
        // TODO 待实现（接口 4-01）：见《IECMP-接口开发文档-v1.6.md》接口 4-01
        return Result.todo("4-01");
    }

    /**
     * 4-02 资产详情（前台）。
     *
     * <p>{@code GET /user/asset/{id}} —— 查询资产完整信息与最近变动流水，用于详情页
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "4-02 资产详情（前台）", description = "查询资产完整信息与最近变动流水，用于详情页")
    @GetMapping("/user/asset/{id}")
    public Result<UserAssetDetailVO> getAsset(@PathVariable("id") Long id) {
        // TODO 待实现（接口 4-02）：见《IECMP-接口开发文档-v1.6.md》接口 4-02
        return Result.todo("4-02");
    }

    /**
     * 4-03 资产分类树。
     *
     * <p>{@code GET /user/asset/category/tree} —— 返回资产分类树，用于资产列表的分类筛选与表单选择
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "4-03 资产分类树", description = "返回资产分类树，用于资产列表的分类筛选与表单选择")
    @GetMapping("/user/asset/category/tree")
    public Result<AssetCategoryTreeVO> getAssetCategoryTree() {
        // TODO 待实现（接口 4-03）：见《IECMP-接口开发文档-v1.6.md》接口 4-03
        return Result.todo("4-03");
    }

    /**
     * 4-04 资产总览统计。
     *
     * <p>{@code GET /user/asset/stat/overview} —— 返回资产的数量与金额总览，用于成员端资产页头部卡片
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "4-04 资产总览统计", description = "返回资产的数量与金额总览，用于成员端资产页头部卡片")
    @GetMapping("/user/asset/stat/overview")
    public Result<AssetStatOverviewVO> getAssetStatOverview() {
        // TODO 待实现（接口 4-04）：见《IECMP-接口开发文档-v1.6.md》接口 4-04
        return Result.todo("4-04");
    }

    /**
     * 4-05 借用 / 领用申请。
     *
     * <p>{@code POST /user/asset/borrow} —— 成员提交资产借用或领用申请；借用需审核，领用视同消耗不归还
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "4-05 借用 / 领用申请", description = "成员提交资产借用或领用申请；借用需审核，领用视同消耗不归还")
    @OperLog(module = "用户端-资产台账与借用", value = "借用 / 领用申请")
    @PostMapping("/user/asset/borrow")
    public Result<AssetBorrowVO> postAssetBorrow(@RequestBody @Valid AssetBorrowDTO dto) {
        // TODO 待实现（接口 4-05）：见《IECMP-接口开发文档-v1.6.md》接口 4-05
        return Result.todo("4-05");
    }

    /**
     * 4-06 我的借用记录。
     *
     * <p>{@code GET /user/asset/borrow/my/page} —— 分页查询当前登录用户的借用 / 领用记录
     *
     * <p>权限：登录即可；令牌：是；分页返回。
     */
    @Operation(summary = "4-06 我的借用记录", description = "分页查询当前登录用户的借用 / 领用记录")
    @GetMapping("/user/asset/borrow/my/page")
    public Result<PageResult<AssetBorrowMyPageVO>> getAssetBorrowMyPage(@ParameterObject AssetBorrowMyPageQuery query) {
        // TODO 待实现（接口 4-06）：见《IECMP-接口开发文档-v1.6.md》接口 4-06
        return Result.todo("4-06");
    }

    /**
     * 4-07 归还登记（本人）。
     *
     * <p>{@code PUT /user/asset/borrow/{id}/return} —— 借用人在成员端登记归还，归还后资产状态恢复为「在用」
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "4-07 归还登记（本人）", description = "借用人在成员端登记归还，归还后资产状态恢复为「在用」")
    @OperLog(module = "用户端-资产台账与借用", value = "归还登记（本人）")
    @PutMapping("/user/asset/borrow/{id}/return")
    public Result<Void> putAssetBorrowReturn(@PathVariable("id") Long id, @RequestBody @Valid UserAssetBorrowReturnUpdateDTO dto) {
        // TODO 待实现（接口 4-07）：见《IECMP-接口开发文档-v1.6.md》接口 4-07
        return Result.todo("4-07");
    }
}
