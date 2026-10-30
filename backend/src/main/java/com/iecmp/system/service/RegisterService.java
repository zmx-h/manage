package com.iecmp.system.service;

import com.iecmp.common.result.PageResult;
import com.iecmp.system.dto.InviteCodeAddDTO;
import com.iecmp.system.dto.RegisterApplyAuditDTO;
import com.iecmp.system.dto.RegisterApplyDTO;
import com.iecmp.system.dto.RegisterDTO;
import com.iecmp.system.query.InviteCodePageQuery;
import com.iecmp.system.query.RegisterApplyPageQuery;
import com.iecmp.system.vo.InviteCodeCreateVO;
import com.iecmp.system.vo.InviteCodePageVO;
import com.iecmp.system.vo.RegisterApplyAuditVO;
import com.iecmp.system.vo.RegisterApplyVO;
import com.iecmp.system.vo.RegisterApplyPageVO;
import com.iecmp.system.vo.RegisterConfigVO;
import com.iecmp.system.vo.RegisterVO;

/**
 * 成员接入与注册审核业务。
 */
public interface RegisterService {

    /** 1-22 注册方式配置。 */
    RegisterConfigVO getConfig();

    /** 1-23 邀请码注册。 */
    RegisterVO register(RegisterDTO dto, String ip);

    /** 1-24 提交注册申请。 */
    RegisterApplyVO apply(RegisterApplyDTO dto, String ip);

    /** 1-25 注册申请分页。 */
    PageResult<RegisterApplyPageVO> pageApply(RegisterApplyPageQuery query);

    /** 1-26 注册申请审核。 */
    RegisterApplyAuditVO auditApply(Long id, RegisterApplyAuditDTO dto);

    /** 1-27 邀请码分页。 */
    PageResult<InviteCodePageVO> pageInvite(InviteCodePageQuery query);

    /** 1-28 批量生成邀请码。 */
    InviteCodeCreateVO createInvite(InviteCodeAddDTO dto);

    /** 1-29 作废邀请码。 */
    void disableInvite(Long id);
}