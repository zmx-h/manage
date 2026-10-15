package com.iecmp.duty.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.duty.dto.DutySwapDTO;
import com.iecmp.duty.dto.DutySwapHandleDTO;
import com.iecmp.duty.query.DutySwapMyPageQuery;
import com.iecmp.duty.service.DutyScheduleService;
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
 * 用户端-换班申请（duty 模块）。
 *
 * <p>覆盖接口：7-05、7-06、7-07，共 3 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-换班申请")
@RestController
@RequiredArgsConstructor
public class UserDutySwapController {

    /** DutySwap 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final DutySwapService dutySwapService;
    /** DutySchedule 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final DutyScheduleService dutyScheduleService;

    /**
     * 7-05 发起换班申请。
     *
     * <p>{@code POST /user/duty/swap} —— 成员向另一位成员发起换班申请，需对方同意即生效
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "7-05 发起换班申请", description = "成员向另一位成员发起换班申请，需对方同意即生效")
    @OperLog(module = "用户端-换班申请", value = "发起换班申请")
    @PostMapping("/user/duty/swap")
    public Result<DutySwapVO> postDutySwap(@RequestBody @Valid DutySwapDTO dto) {
        // TODO 待实现（接口 7-05）：见《IECMP-接口开发文档-v1.6.md》接口 7-05
        return Result.todo("7-05");
    }

    /**
     * 7-06 我的换班申请分页。
     *
     * <p>{@code GET /user/duty/swap/my/page} —— 分页查询当前用户**发起或收到**的换班申请
     *
     * <p>权限：登录即可；令牌：是；分页返回。
     */
    @Operation(summary = "7-06 我的换班申请分页", description = "分页查询当前用户**发起或收到**的换班申请")
    @GetMapping("/user/duty/swap/my/page")
    public Result<PageResult<DutySwapMyPageVO>> getDutySwapMyPage(@ParameterObject DutySwapMyPageQuery query) {
        // TODO 待实现（接口 7-06）：见《IECMP-接口开发文档-v1.6.md》接口 7-06
        return Result.todo("7-06");
    }

    /**
     * 7-07 换班处理（同意 / 拒绝）。
     *
     * <p>{@code PUT /user/duty/swap/{id}/handle} —— 换班对象处理收到的申请；同意后换班立即生效
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "7-07 换班处理（同意 / 拒绝）", description = "换班对象处理收到的申请；同意后换班立即生效")
    @OperLog(module = "用户端-换班申请", value = "换班处理（同意 / 拒绝）")
    @PutMapping("/user/duty/swap/{id}/handle")
    public Result<Void> putDutySwapHandle(@PathVariable("id") Long id, @RequestBody @Valid DutySwapHandleDTO dto) {
        // TODO 待实现（接口 7-07）：见《IECMP-接口开发文档-v1.6.md》接口 7-07
        return Result.todo("7-07");
    }
}
