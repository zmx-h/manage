package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.entity.BudgetExpense;
import com.iecmp.budget.mapper.BudgetExpenseMapper;
import com.iecmp.budget.service.BudgetExpenseService;
import com.iecmp.budget.vo.UserBudgetExpensePageVO;
import com.iecmp.common.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 支出记录业务实现（{@code budget_expense}）。
 *
 * @author IECMP 后端组
 */
@Service
public class BudgetExpenseServiceImpl extends ServiceImpl<BudgetExpenseMapper, BudgetExpense>
        implements BudgetExpenseService {

    @Override
    public PageResult<UserBudgetExpensePageVO> expensePage(Long periodId, Long itemId, String keyword,
            String beginDate, String endDate,
            Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Long total = baseMapper.countPublicPage(periodId, itemId, keyword, beginDate, endDate);
        List<UserBudgetExpensePageVO> list = baseMapper.selectPublicPage(
                periodId, itemId, keyword, beginDate, endDate, offset, pageSize);
        long pages = total == 0 ? 0 : (total + pageSize - 1) / pageSize;
        return new PageResult<>(total, (long) pageNum, (long) pageSize, pages, list);
    }
}
