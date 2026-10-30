package com.iecmp.duty.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.common.result.PageResult;
import com.iecmp.duty.dto.DutySwapDTO;
import com.iecmp.duty.dto.DutySwapHandleDTO;
import com.iecmp.duty.entity.DutySwap;
import com.iecmp.duty.query.DutySwapMyPageQuery;
import com.iecmp.duty.vo.DutySwapMyPageVO;
import com.iecmp.duty.vo.DutySwapVO;

/**
 * 换班申请业务接口（{@code duty_swap}），接口 7-05 ~ 7-07。
 */
public interface DutySwapService extends IService<DutySwap> {

    DutySwapVO create(DutySwapDTO dto);

    PageResult<DutySwapMyPageVO> myPage(DutySwapMyPageQuery query);

    void handle(Long id, DutySwapHandleDTO dto);
}