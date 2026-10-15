package com.iecmp.commonm.controller;

import com.iecmp.common.annotation.OperLog;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.Result;
import com.iecmp.commonm.dto.MessageReadDTO;
import com.iecmp.commonm.query.MessagePageQuery;
import com.iecmp.commonm.service.SysMessageService;
import com.iecmp.commonm.vo.MessagePageVO;
import com.iecmp.commonm.vo.MessageReadVO;
import com.iecmp.commonm.vo.MessageUnreadCountVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端-站内消息（commonm 模块）。
 *
 * <p>覆盖接口：8-20、8-21、8-22，共 3 个。
 *
 * <p><b>骨架说明</b>：所有方法体都是占位实现，统一返回
 * {@code Result.todo("x-xx")}（code = 500，msg = 接口 x-xx 尚未实现）。
 * 全局搜索 {@code Result.todo} 可定位全部待实现接口。
 *
 * @author IECMP 后端组
 */
@Tag(name = "用户端-站内消息")
@RestController
@RequiredArgsConstructor
public class UserMessageController {

    /** SysMessage 业务接口（骨架阶段仅注入，方法体待实现时使用）。 */
    private final SysMessageService sysMessageService;

    /**
     * 8-20 我的消息分页。
     *
     * <p>{@code GET /user/message/page} —— 分页查询当前登录用户的站内消息，用于消息中心列表页
     *
     * <p>权限：登录即可；令牌：是；分页返回。
     */
    @Operation(summary = "8-20 我的消息分页", description = "分页查询当前登录用户的站内消息，用于消息中心列表页")
    @GetMapping("/user/message/page")
    public Result<PageResult<MessagePageVO>> getMessagePage(@ParameterObject MessagePageQuery query) {
        // TODO 待实现（接口 8-20）：见《IECMP-接口开发文档-v1.6.md》接口 8-20
        return Result.todo("8-20");
    }

    /**
     * 8-21 未读消息数。
     *
     * <p>{@code GET /user/message/unread/count} —— 返回当前用户未读消息总数与分类数量，用于导航栏红点与消息中心分组
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "8-21 未读消息数", description = "返回当前用户未读消息总数与分类数量，用于导航栏红点与消息中心分组")
    @GetMapping("/user/message/unread/count")
    public Result<MessageUnreadCountVO> getMessageUnreadCount() {
        // TODO 待实现（接口 8-21）：见《IECMP-接口开发文档-v1.6.md》接口 8-21
        return Result.todo("8-21");
    }

    /**
     * 8-22 标记消息已读。
     *
     * <p>{@code PUT /user/message/read} —— 把指定消息标记为已读，也支持一键把本人全部（或某类型）消息标记已读
     *
     * <p>权限：登录即可；令牌：是。
     */
    @Operation(summary = "8-22 标记消息已读", description = "把指定消息标记为已读，也支持一键把本人全部（或某类型）消息标记已读")
    @OperLog(module = "用户端-站内消息", value = "标记消息已读")
    @PutMapping("/user/message/read")
    public Result<MessageReadVO> putMessageRead(@RequestBody @Valid MessageReadDTO dto) {
        // TODO 待实现（接口 8-22）：见《IECMP-接口开发文档-v1.6.md》接口 8-22
        return Result.todo("8-22");
    }
}
