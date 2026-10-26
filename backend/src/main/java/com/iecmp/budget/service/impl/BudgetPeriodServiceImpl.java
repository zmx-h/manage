package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.entity.BudgetPeriod;
import com.iecmp.budget.enums.BudgetErrorCode;
import com.iecmp.budget.mapper.BudgetApplyMapper;
import com.iecmp.budget.mapper.BudgetExpenseMapper;
import com.iecmp.budget.mapper.BudgetItemMapper;
import com.iecmp.budget.mapper.BudgetPeriodMapper;
import com.iecmp.budget.query.BudgetStatOverviewQuery;
import com.iecmp.budget.service.BudgetPeriodService;
import com.iecmp.budget.vo.BudgetPeriodDetailVO;
import com.iecmp.budget.vo.BudgetStatOverviewVO;
import com.iecmp.budget.vo.UserBudgetPeriodPageVO;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 预算周期业务实现（{@code budget_period}）。
 *
 * @author IECMP 后端组
 */
@Service
@RequiredArgsConstructor
public class BudgetPeriodServiceImpl extends ServiceImpl<BudgetPeriodMapper, BudgetPeriod>
        implements BudgetPeriodService {

    private static final int MAX_PAGE_SIZE = 100;

    private final BudgetApplyMapper budgetApplyMapper;
    private final BudgetExpenseMapper budgetExpenseMapper;
    private final BudgetItemMapper budgetItemMapper;

    @Override
    public PageResult<UserBudgetPeriodPageVO> periodPage(String year, Integer status,
            Integer pageNum, Integer pageSize) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null ? 10 : Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);
        int offset = (safePageNum - 1) * safePageSize;
        Long total = baseMapper.countPublicPage(year, status);
        List<UserBudgetPeriodPageVO> list = baseMapper.selectPublicPage(year, status, offset, safePageSize);
        long pages = total == 0 ? 0 : (total + safePageSize - 1) / safePageSize;
        return new PageResult<>(total, (long) safePageNum, (long) safePageSize, pages, list);
    }

    @Override
    public BudgetPeriodDetailVO periodDetail(Long id) {
        BudgetPeriodDetailVO vo = baseMapper.selectDetailById(id);
        if (vo == null) {
            throw new BusinessException(BudgetErrorCode.E30001.getCode(), BudgetErrorCode.E30001.getMsg());
        }
        return vo;
    }

    @Override
    public BudgetStatOverviewVO statOverview(BudgetStatOverviewQuery query) {
        Long periodId = query == null ? null : query.getPeriodId();
        BudgetPeriod period = resolvePeriod(periodId);
        Long resolvedPeriodId = period.getId();

        BigDecimal totalAmount = amountOrZero(period.getTotalAmount());
        BigDecimal usedAmount = amountOrZero(period.getUsedAmount());

        List<BudgetStatOverviewVO.ItemStatVO> itemStats = budgetItemMapper.selectItemStats(resolvedPeriodId);
        if (itemStats == null) {
            itemStats = List.of();
        }

        BudgetStatOverviewVO.ApplyStatVO applyStats = budgetApplyMapper.selectApplyStat(resolvedPeriodId);
        if (applyStats == null) {
            applyStats = new BudgetStatOverviewVO.ApplyStatVO();
        }
        applyStats.setPendingCount(countOrZero(applyStats.getPendingCount()));
        applyStats.setApprovedCount(countOrZero(applyStats.getApprovedCount()));
        applyStats.setRejectedCount(countOrZero(applyStats.getRejectedCount()));
        applyStats.setApprovedAmount(amountOrZero(applyStats.getApprovedAmount()));

        List<BudgetStatOverviewVO.MonthTrendVO> monthTrend = budgetExpenseMapper.selectMonthTrend(resolvedPeriodId);
        if (monthTrend == null) {
            monthTrend = List.of();
        }

        BudgetStatOverviewVO vo = new BudgetStatOverviewVO();
        vo.setPeriodId(resolvedPeriodId);
        vo.setPeriodTitle(period.getTitle());
        vo.setTotalAmount(totalAmount);
        vo.setUsedAmount(usedAmount);
        vo.setRemainAmount(totalAmount.subtract(usedAmount));
        vo.setExecutionRate(executionRate(usedAmount, totalAmount));
        vo.setItemStats(itemStats);
        vo.setApplyStats(applyStats);
        vo.setMonthTrend(monthTrend);
        vo.setOverBudgetItems(findOverBudgetItems(itemStats, totalAmount));
        return vo;
    }

    private BudgetPeriod resolvePeriod(Long periodId) {
        BudgetPeriod period;
        if (periodId == null) {
            period = getOne(Wrappers.<BudgetPeriod>lambdaQuery()
                    .eq(BudgetPeriod::getStatus, 1)
                    .orderByDesc(BudgetPeriod::getStartDate)
                    .orderByDesc(BudgetPeriod::getId)
                    .last("LIMIT 1"));
        } else {
            period = getById(periodId);
        }
        if (period == null) {
            throw new BusinessException(BudgetErrorCode.E30001.getCode(), BudgetErrorCode.E30001.getMsg());
        }
        return period;
    }

    private List<String> findOverBudgetItems(List<BudgetStatOverviewVO.ItemStatVO> itemStats,
            BigDecimal totalAmount) {
        BigDecimal allocatedAmount = itemStats.stream()
                .map(item -> amountOrZero(item.getBudgetAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (allocatedAmount.compareTo(totalAmount) <= 0) {
            return List.of();
        }
        List<String> names = new ArrayList<>();
        for (BudgetStatOverviewVO.ItemStatVO item : itemStats) {
            if (item.getItemName() != null) {
                names.add(item.getItemName());
            }
        }
        return names;
    }

    private static BigDecimal executionRate(BigDecimal usedAmount, BigDecimal totalAmount) {
        if (totalAmount.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return usedAmount.multiply(BigDecimal.valueOf(100))
                .divide(totalAmount, 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal amountOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static Integer countOrZero(Integer value) {
        return value == null ? 0 : value;
    }
}