package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.entity.BudgetExpense;
import com.iecmp.budget.mapper.BudgetExpenseMapper;
import com.iecmp.budget.service.BudgetExpenseService;
import com.iecmp.budget.vo.UserBudgetExpensePageVO;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 支出记录业务实现（{@code budget_expense}）。
 *
 * @author IECMP 后端组
 */
@Service
public class BudgetExpenseServiceImpl extends ServiceImpl<BudgetExpenseMapper, BudgetExpense>
        implements BudgetExpenseService {

    private static final int MAX_PAGE_SIZE = 100;

    @Override
    public PageResult<UserBudgetExpensePageVO> expensePage(Long periodId, Long itemId, String keyword,
            String beginDate, String endDate,
            Integer pageNum, Integer pageSize) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null ? 10 : Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);

        Long resolvedPeriodId = periodId;
        if (resolvedPeriodId == null) {
            resolvedPeriodId = baseMapper.selectLatestPublicPeriodId();
        }
        if (resolvedPeriodId == null) {
            return PageResult.empty(safePageNum, safePageSize);
        }

        String safeKeyword = keyword == null ? null : keyword.trim();
        if (safeKeyword != null && safeKeyword.length() > 100) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "关键字长度不能超过 100 个字符");
        }
        String safeBeginDate = normalizeDate(beginDate, "支出开始日期");
        String safeEndDate = normalizeDate(endDate, "支出结束日期");
        if (safeBeginDate != null && safeEndDate != null && safeBeginDate.compareTo(safeEndDate) > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "支出开始日期不能晚于结束日期");
        }

        int offset = (safePageNum - 1) * safePageSize;
        Long total = baseMapper.countPublicPage(
                resolvedPeriodId, itemId, safeKeyword, safeBeginDate, safeEndDate);
        List<UserBudgetExpensePageVO> list = baseMapper.selectPublicPage(
                resolvedPeriodId, itemId, safeKeyword, safeBeginDate, safeEndDate, offset, safePageSize);
        long pages = total == 0 ? 0 : (total + safePageSize - 1) / safePageSize;
        return new PageResult<>(total, (long) safePageNum, (long) safePageSize, pages, list);
    }

    private String normalizeDate(String text, String fieldName) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(text.trim()).toString();
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), fieldName + "格式错误，应为 yyyy-MM-dd");
        }
    }
}
