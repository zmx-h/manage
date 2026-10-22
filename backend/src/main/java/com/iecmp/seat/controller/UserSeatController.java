package com.iecmp.seat.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.common.util.LoginUserUtil;
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

import java.util.List;

@Tag(name = "用户端-工位与申请")
@RestController
@RequiredArgsConstructor
public class UserSeatController {

    private final SeatAreaService seatAreaService;
    private final SeatInfoService seatInfoService;
    private final SeatApplyService seatApplyService;

    @GetMapping("/user/seat/plan")
    public Result<List<UserSeatPlanVO>> getSeatPlan(@ParameterObject UserSeatPlanQuery query) {
        // 现在这里调它，绝对不会再报 401 了，因为拦截器已经塞进去了
        Long currentUserId = LoginUserUtil.getUserId();
        return Result.success(seatAreaService.getSeatPlan(query.getAreaId(), currentUserId));
    }

    @Operation(summary = "5-02 工位列表", description = "以列表形式返回工位信息，供不支持平面图的场景（如移动端）或搜索使用")
    @GetMapping("/user/seat/list")
    public Result<SeatListVO> getSeatList(@ParameterObject SeatListQuery query) {
        return Result.todo("5-02");
    }

    @Operation(summary = "5-03 我的工位", description = "查询当前登录用户的工位；未分配时返回 null")
    @GetMapping("/user/seat/my")
    public Result<SeatMyVO> getSeatMy() {
        return Result.todo("5-03");
    }

    @Operation(summary = "5-04 提交工位申请", description = "成员申请工位；可指定具体工位，也可只填意向区域")
    @OperLog(module = "用户端-工位与申请", value = "提交工位申请")
    @PostMapping("/user/seat/apply")
    public Result<SeatApplyVO> postSeatApply(@RequestBody @Valid SeatApplyDTO dto) {
        return Result.todo("5-04");
    }

    @Operation(summary = "5-05 我的工位申请分页", description = "分页查询当前登录用户提交的工位申请")
    @GetMapping("/user/seat/apply/my/page")
    public Result<PageResult<SeatApplyMyPageVO>> getSeatApplyMyPage(@ParameterObject SeatApplyMyPageQuery query) {
        return Result.todo("5-05");
    }

    @Operation(summary = "5-06 撤销工位申请", description = "撤销尚未审核的工位申请")
    @OperLog(module = "用户端-工位与申请", value = "撤销工位申请")
    @PutMapping("/user/seat/apply/{id}/cancel")
    public Result<Void> putSeatApplyCancel(@PathVariable("id") Long id) {
        return Result.todo("5-06");
    }
}