package com.iecmp.commonm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.common.result.PageResult;
import com.iecmp.commonm.dto.MessageReadDTO;
import com.iecmp.commonm.entity.SysMessage;
import com.iecmp.commonm.query.MessagePageQuery;
import com.iecmp.commonm.vo.MessagePageVO;
import com.iecmp.commonm.vo.MessageReadVO;
import com.iecmp.commonm.vo.MessageUnreadCountVO;

/**
 * 站内消息业务接口（{@code sys_message}）。
 */
public interface SysMessageService extends IService<SysMessage> {

    /** 8-20 当前用户消息分页。 */
    PageResult<MessagePageVO> pageMy(Long userId, MessagePageQuery query);

    /** 8-21 当前用户未读消息统计。 */
    MessageUnreadCountVO unreadCount(Long userId);

    /** 8-22 标记当前用户消息已读。 */
    MessageReadVO markRead(Long userId, MessageReadDTO dto);
}