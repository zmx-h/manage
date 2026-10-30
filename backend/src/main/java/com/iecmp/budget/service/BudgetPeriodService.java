package com.iecmp.budget.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.budget.dto.BudgetPeriodAddDTO;
import com.iecmp.budget.dto.BudgetPeriodUpdateDTO;
import com.iecmp.budget.entity.BudgetPeriod;
import com.iecmp.budget.query.AdminBudgetPeriodPageQuery;
import com.iecmp.budget.query.BudgetStatOverviewQuery;
import com.iecmp.budget.vo.AdminBudgetPeriodPageVO;
import com.iecmp.budget.vo.BudgetPeriodCreateVO;
import com.iecmp.budget.vo.BudgetPeriodDetailVO;
import com.iecmp.budget.vo.BudgetStatOverviewVO;
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

    BudgetStatOverviewVO statOverview(BudgetStatOverviewQuery query);

    /** 3-10 后台预算周期分页 */
    PageResult<AdminBudgetPeriodPageVO> adminPeriodPage(AdminBudgetPeriodPageQuery query);

    /** 3-11 新增预算周期（起草） */
    BudgetPeriodCreateVO createPeriod(BudgetPeriodAddDTO dto);

    /** 3-12 修改预算周期 */
    void updatePeriod(BudgetPeriodUpdateDTO dto);

    /** 3-13 删除预算周期 */
    void deletePeriod(Long id);

    /** 3-14 周期状态流转 */
    void changeStatus(Long id, Integer status);

    /** 3-15 公示 / 取消公示 */
    void changePublic(Long id, Integer isPublic);

    /** 3-16 发起预算征集（v1.0 预留） */
    void collect(Long id);
}