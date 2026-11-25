package com.iecmp.system.service;

import com.iecmp.common.result.PageResult;
import com.iecmp.system.dto.RecruitApplyDTO;
import com.iecmp.system.dto.RecruitAssessmentDTO;
import com.iecmp.system.dto.RecruitRegisterDTO;
import com.iecmp.system.query.RecruitApplyPageQuery;
import com.iecmp.system.vo.RecruitApplyPageVO;
import com.iecmp.system.vo.RecruitApplySubmitVO;
import com.iecmp.system.vo.RecruitApplyVO;
import com.iecmp.system.vo.RecruitAssessmentVO;
import com.iecmp.system.vo.RecruitCodeVO;
import com.iecmp.system.vo.RegisterVO;

/** 招新报名、考核与邮箱注册码业务接口（接口 1-30 ~ 1-35）。 */
public interface RecruitService {

    /** 1-30 外部招新网页提交报名。 */
    RecruitApplySubmitVO apply(RecruitApplyDTO dto, String ip);

    /** 1-31 使用邮箱与注册码完成注册。 */
    RegisterVO register(RecruitRegisterDTO dto);

    /** 1-32 招新报名分页。 */
    PageResult<RecruitApplyPageVO> page(RecruitApplyPageQuery query);

    /** 1-33 招新报名详情。 */
    RecruitApplyVO detail(Long id);

    /** 1-34 录入一轮考核成绩，通过时自动发码。 */
    RecruitAssessmentVO assess(Long id, RecruitAssessmentDTO dto);

    /** 1-35 补发注册码。 */
    RecruitCodeVO resendCode(Long id);
}
