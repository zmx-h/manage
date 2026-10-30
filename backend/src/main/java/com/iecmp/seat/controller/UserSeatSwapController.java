package com.iecmp.seat.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.seat.dto.SeatSwapDTO;
import com.iecmp.seat.dto.SeatSwapHandleDTO;
import com.iecmp.seat.query.SeatSwapMyPageQuery;
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

/** 用户端-座位调换（5-20 ~ 5-22）。 */
@Tag(name = "用户端-座位调换")
@RestController
@RequiredArgsConstructor
public class UserSeatSwapController {

    private final SeatSwapService seatSwapService;

    @Operation(summary = "5-20 成员发起座位调换", description = "成员向另一位成员发起座位对调申请，需对方确认和负责人审核")
    @OperLog(module = "用户端-座位调换", value = "成员发起座位调换")
    @PostMapping("/user/seat/swap")
    public Result<SeatSwapVO> postSeatSwap(@RequestBody @Valid SeatSwapDTO dto) {
        return Result.success(seatSwapService.createSwap(dto, LoginUserUtil.getUserId()));
    }

    @Operation(summary = "5-21 我的调换申请分页", description = "分页查询当前用户发起或收到的座位调换申请")
    @GetMapping("/user/seat/swap/my/page")
    public Result<PageResult<SeatSwapMyPageVO>> getSeatSwapMyPage(
            @ParameterObject SeatSwapMyPageQuery query) {
        return Result.success(seatSwapService.myPage(query, LoginUserUtil.getUserId()));
    }

    @Operation(summary = "5-22 调换对方确认 / 拒绝", description = "对调方成员处理收到的调换申请")
    @OperLog(module = "用户端-座位调换", value = "调换对方确认 / 拒绝")
    @PutMapping("/user/seat/swap/{id}/handle")
    public Result<Void> putSeatSwapHandle(@PathVariable("id") Long id,
                                          @RequestBody @Valid SeatSwapHandleDTO dto) {
        seatSwapService.handle(id, dto, LoginUserUtil.getUserId());
        return Result.success();
    }
}