package com.iecmp.duty.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.duty.dto.DutySwapDTO;
import com.iecmp.duty.dto.DutySwapHandleDTO;
import com.iecmp.duty.entity.DutySwap;
import com.iecmp.duty.query.DutySwapMyPageQuery;
import com.iecmp.duty.vo.DutySwapMyPageVO;
import com.iecmp.duty.vo.DutySwapVO;
import com.iecmp.common.result.PageResult;

/**
 * 换班申请业务接口（{@code duty_swap}）。
 *
 * <p>骨架阶段只继承 {@code IService} 获得通用 CRUD 能力；
 * 实现具体接口时，把该表的专有业务方法声明在这里，并在 Impl 中实现。
 *
 * @author liuyujie
 * @Date 2026.10.27
 */
public interface DutySwapService extends IService<DutySwap> {

    /** 7-05 发起换班申请 */
    DutySwapVO applySwap(DutySwapDTO dto);

    /** 7-06 我的换班申请分页 */
    PageResult<DutySwapMyPageVO> pageMySwap(DutySwapMyPageQuery query);

    /** 7-07 换班处理（同意/拒绝） */
    void handleSwap(Long id, DutySwapHandleDTO dto);
}
