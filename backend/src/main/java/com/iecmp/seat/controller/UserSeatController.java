package com.iecmp.seat.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.seat.dto.SeatApplyDTO;
import com.iecmp.seat.query.SeatApplyMyPageQuery;
import com.iecmp.seat.query.SeatListQuery;
import com.iecmp.seat.query.UserSeatPlanQuery;
import com.iecmp.seat.service.SeatApplyService;
import com.iecmp.seat.service.SeatAreaService;
import com.iecmp.seat.service.SeatInfoService;
import com.iecmp.seat.vo.SeatApplyMyPageVO;
import com.iecmp.seat.vo.SeatApplyVO;
import com.iecmp.seat.vo.SeatListVO;
import com.iecmp.seat.vo.SeatMyVO;
import com.iecmp.seat.vo.UserSeatPlanVO;
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
 * 用户端-工位与申请（seat 模块）。
 *
 * <p>覆盖接口：5-01、5-02、5-03、5-04、5-05、5-06，共 6 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-工位与申请")
@RestController
@RequiredArgsConstructor
public class UserSeatController {

    /** SeatArea 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SeatAreaService seatAreaService;
    /** SeatInfo 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SeatInfoService seatInfoService;
    /** SeatApply 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SeatApplyService seatApplyService;

    /**
     * 5-01 平面图数据。
     *
     * <p>{@code GET /user/seat/plan} —— 一次返回区域、底图尺寸与全部工位坐标及状态，供前端渲染可交互平面图
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "5-01 平面图数据", description = "一次返回区域、底图尺寸与全部工位坐标及状态，供前端渲染可交互平面图")
    @GetMapping("/user/seat/plan")
    public Result<UserSeatPlanVO> getSeatPlan(@ParameterObject UserSeatPlanQuery query) {
        // TODO 待实现（接口 5-01）：见《IECMP-接口开发文档-v1.6.md》接口 5-01
        return Result.todo("5-01");
    }

    /**
     * 5-02 工位列表。
     *
     * <p>{@code GET /user/seat/list} —— 以列表形式返回工位信息，供不支持平面图的场景（如移动端）或搜索使用
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "5-02 工位列表", description = "以列表形式返回工位信息，供不支持平面图的场景（如移动端）或搜索使用")
    @GetMapping("/user/seat/list")
    public Result<SeatListVO> getSeatList(@ParameterObject SeatListQuery query) {
        // TODO 待实现（接口 5-02）：见《IECMP-接口开发文档-v1.6.md》接口 5-02
        return Result.todo("5-02");
    }

    /**
     * 5-03 我的工位。
     *
     * <p>{@code GET /user/seat/my} —— 查询当前登录用户的工位；未分配时返回 null
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "5-03 我的工位", description = "查询当前登录用户的工位；未分配时返回 null")
    @GetMapping("/user/seat/my")
    public Result<SeatMyVO> getSeatMy() {
        // TODO 待实现（接口 5-03）：见《IECMP-接口开发文档-v1.6.md》接口 5-03
        return Result.todo("5-03");
    }

    /**
     * 5-04 提交工位申请。
     *
     * <p>{@code POST /user/seat/apply} —— 成员申请工位；可指定具体工位，也可只填意向区域
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "5-04 提交工位申请", description = "成员申请工位；可指定具体工位，也可只填意向区域")
    @OperLog(module = "用户端-工位与申请", value = "提交工位申请")
    @PostMapping("/user/seat/apply")
    public Result<SeatApplyVO> postSeatApply(@RequestBody @Valid SeatApplyDTO dto) {
        // TODO 待实现（接口 5-04）：见《IECMP-接口开发文档-v1.6.md》接口 5-04
        return Result.todo("5-04");
    }

    /**
     * 5-05 我的工位申请分页。
     *
     * <p>{@code GET /user/seat/apply/my/page} —— 分页查询当前登录用户提交的工位申请
     *
     * <p>权限：登录即可；令牌：是；分页返回。
     */
    @Operation(summary = "5-05 我的工位申请分页", description = "分页查询当前登录用户提交的工位申请")
    @GetMapping("/user/seat/apply/my/page")
    public Result<PageResult<SeatApplyMyPageVO>> getSeatApplyMyPage(@ParameterObject SeatApplyMyPageQuery query) {
        // TODO 待实现（接口 5-05）：见《IECMP-接口开发文档-v1.6.md》接口 5-05
        return Result.todo("5-05");
    }

    /**
     * 5-06 撤销工位申请。
     *
     * <p>{@code PUT /user/seat/apply/{id}/cancel} —— 撤销尚未审核的工位申请
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "5-06 撤销工位申请", description = "撤销尚未审核的工位申请")
    @OperLog(module = "用户端-工位与申请", value = "撤销工位申请")
    @PutMapping("/user/seat/apply/{id}/cancel")
    public Result<Void> putSeatApplyCancel(@PathVariable("id") Long id) {
        // TODO 待实现（接口 5-06）：见《IECMP-接口开发文档-v1.6.md》接口 5-06
        return Result.todo("5-06");
    }
}
