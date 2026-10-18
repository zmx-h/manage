package com.iecmp.budget.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.budget.entity.BudgetPeriod;
import com.iecmp.budget.vo.BudgetPeriodDetailVO;
import com.iecmp.budget.vo.UserBudgetPeriodPageVO;
import com.iecmp.common.result.PageResult;

/**
 * 预算周期业务接口（{@code budget_period}）。
 *
 * @author IECMP 后端组
 */
public interface BudgetPeriodService extends IService<BudgetPeriod> {

    /** 3-01 公示预算周期分页 */
    PageResult<UserBudgetPeriodPageVO> periodPage(String year, Integer status, Integer pageNum, Integer pageSize);

    /** 3-02 预算周期详情 */
    BudgetPeriodDetailVO periodDetail(Long id);
}
