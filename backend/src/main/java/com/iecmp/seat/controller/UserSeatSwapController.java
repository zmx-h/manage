package com.iecmp.seat.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.seat.dto.SeatSwapDTO;
import com.iecmp.seat.dto.SeatSwapHandleDTO;
import com.iecmp.seat.query.SeatSwapMyPageQuery;
import com.iecmp.seat.service.SeatInfoService;
import com.iecmp.seat.service.SeatSwapService;
import com.iecmp.seat.vo.SeatSwapMyPageVO;
import com.iecmp.seat.vo.SeatSwapVO;
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
 * 用户端-座位调换（seat 模块）。
 *
 * <p>覆盖接口：5-20、5-21、5-22，共 3 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-座位调换")
@RestController
@RequiredArgsConstructor
public class UserSeatSwapController {

    /** SeatSwap 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SeatSwapService seatSwapService;
    /** SeatInfo 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SeatInfoService seatInfoService;

    /**
     * 5-20 成员发起座位调换。
     *
     * <p>{@code POST /user/seat/swap} —— 成员向另一位成员发起座位对调申请，需「对方确认 + 负责人审核」两步完成
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "5-20 成员发起座位调换", description = "成员向另一位成员发起座位对调申请，需「对方确认 + 负责人审核」两步完成")
    @OperLog(module = "用户端-座位调换", value = "成员发起座位调换")
    @PostMapping("/user/seat/swap")
    public Result<SeatSwapVO> postSeatSwap(@RequestBody @Valid SeatSwapDTO dto) {
        return Result.success(seatSwapService.createSwap(dto, LoginUserUtil.getUserId()));
    }

    /**
     * 5-21 我的调换申请分页。
     *
     * <p>{@code GET /user/seat/swap/my/page} —— 分页查询当前用户**发起或收到**的座位调换申请
     *
     * <p>权限：登录即可；令牌：是；分页返回。
     */
    @Operation(summary = "5-21 我的调换申请分页", description = "分页查询当前用户**发起或收到**的座位调换申请")
    @GetMapping("/user/seat/swap/my/page")
    public Result<PageResult<SeatSwapMyPageVO>> getSeatSwapMyPage(@ParameterObject SeatSwapMyPageQuery query) {
        return Result.success(seatSwapService.mySwapPage(query, LoginUserUtil.getUserId()));
    }

    /**
     * 5-22 调换对方确认 / 拒绝。
     *
     * <p>{@code PUT /user/seat/swap/{id}/handle} —— 对调方成员处理收到的调换申请；同意后进入负责人审核环节
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "5-22 调换对方确认 / 拒绝", description = "对调方成员处理收到的调换申请；同意后进入负责人审核环节")
    @OperLog(module = "用户端-座位调换", value = "调换对方确认 / 拒绝")
    @PutMapping("/user/seat/swap/{id}/handle")
    public Result<Void> putSeatSwapHandle(@PathVariable("id") Long id, @RequestBody @Valid SeatSwapHandleDTO dto) {
        seatSwapService.handleSwap(id, dto, LoginUserUtil.getUserId());
        return Result.success(null);
    }
}
