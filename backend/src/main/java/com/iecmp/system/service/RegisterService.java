package com.iecmp.system.service;

import com.iecmp.common.result.PageResult;
import com.iecmp.system.dto.InviteCodeAddDTO;
import com.iecmp.system.dto.RegisterDTO;
import com.iecmp.system.query.InviteCodePageQuery;
import com.iecmp.system.vo.InviteCodeCreateVO;
import com.iecmp.system.vo.InviteCodePageVO;
import com.iecmp.system.vo.RegisterConfigVO;
import com.iecmp.system.vo.RegisterVO;

/**
 * 成员接入与邀请码业务。
 */
public interface RegisterService {

    /** 1-22 注册方式配置。 */
    RegisterConfigVO getConfig();

    /** 1-23 邀请码注册。 */
    RegisterVO register(RegisterDTO dto);

    /** 1-27 邀请码分页。 */
    PageResult<InviteCodePageVO> pageInvite(InviteCodePageQuery query);

    /** 1-28 批量生成邀请码。 */
    InviteCodeCreateVO createInvite(InviteCodeAddDTO dto);

    /** 1-29 作废邀请码。 */
    void disableInvite(Long id);
}
