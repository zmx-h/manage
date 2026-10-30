package com.iecmp.duty.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.duty.dto.DutySwapDTO;
import com.iecmp.duty.dto.DutySwapHandleDTO;
import com.iecmp.duty.query.DutySwapMyPageQuery;
import com.iecmp.duty.service.DutySwapService;
import com.iecmp.duty.vo.DutySwapMyPageVO;
import com.iecmp.duty.vo.DutySwapVO;
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
 * 用户端-换班申请。
 *
 * <p>覆盖接口：7-05、7-06、7-07，共 3 个。
 */
@Tag(name = "用户端-换班申请")
@RestController
@RequiredArgsConstructor
public class UserDutySwapController {

    private final DutySwapService dutySwapService;

    @Operation(summary = "7-05 发起换班申请", description = "成员向另一位成员发起换班申请，需对方同意即生效")
    @OperLog(module = "用户端-换班申请", value = "发起换班申请")
    @PostMapping("/user/duty/swap")
    public Result<DutySwapVO> postDutySwap(@RequestBody @Valid DutySwapDTO dto) {
        return Result.success(dutySwapService.create(dto));
    }

    @Operation(summary = "7-06 我的换班申请分页", description = "分页查询当前用户发起或收到的换班申请")
    @GetMapping("/user/duty/swap/my/page")
    public Result<PageResult<DutySwapMyPageVO>> getDutySwapMyPage(
            @ParameterObject DutySwapMyPageQuery query) {
        return Result.success(dutySwapService.myPage(query));
    }

    @Operation(summary = "7-07 换班处理（同意 / 拒绝）", description = "换班对象处理收到的申请；同意后换班立即生效")
    @OperLog(module = "用户端-换班申请", value = "换班处理（同意 / 拒绝）")
    @PutMapping("/user/duty/swap/{id}/handle")
    public Result<Void> putDutySwapHandle(@PathVariable("id") Long id,
                                          @RequestBody @Valid DutySwapHandleDTO dto) {
        dutySwapService.handle(id, dto);
        return Result.success();
    }
}