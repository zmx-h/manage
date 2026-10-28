package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.entity.BudgetPeriod;
import com.iecmp.budget.enums.BudgetErrorCode;
import com.iecmp.budget.enums.BudgetPeriodStatusEnum;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 预算周期业务实现（{@code budget_period}）。
 *
 * @author IECMP 后端组
 */
@Service
@RequiredArgsConstructor
public class BudgetPeriodServiceImpl extends ServiceImpl<BudgetPeriodMapper, BudgetPeriod>
        implements BudgetPeriodService {

    private final BudgetItemMapper budgetItemMapper;
    private final BudgetApplyMapper budgetApplyMapper;
    private final BudgetExpenseMapper budgetExpenseMapper;

    @Override
    public PageResult<UserBudgetPeriodPageVO> periodPage(String year, Integer status,
            Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Long total = baseMapper.countPublicPage(year, status);
        List<UserBudgetPeriodPageVO> list = baseMapper.selectPublicPage(year, status, offset, pageSize);
        long pages = total == 0 ? 0 : (total + pageSize - 1) / pageSize;
        return new PageResult<>(total, (long) pageNum, (long) pageSize, pages, list);
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
        Long periodId = query.getPeriodId();
        BudgetPeriod period;
        if (periodId == null) {
            period = this.getOne(new LambdaQueryWrapper<BudgetPeriod>()
                    .eq(BudgetPeriod::getStatus, BudgetPeriodStatusEnum.RUNNING.getValue())
                    .orderByDesc(BudgetPeriod::getStartDate)
                    .orderByDesc(BudgetPeriod::getId)
                    .last("LIMIT 1"));
        } else {
            period = this.getById(periodId);
        }
        if (period == null) {
            throw new BusinessException(BudgetErrorCode.E30001.getCode(), BudgetErrorCode.E30001.getMsg());
        }

        BudgetStatOverviewVO vo = new BudgetStatOverviewVO();
        vo.setPeriodId(period.getId());
        vo.setPeriodTitle(period.getTitle());
        BigDecimal total = period.getTotalAmount() == null ? BigDecimal.ZERO : period.getTotalAmount();
        BigDecimal used = period.getUsedAmount() == null ? BigDecimal.ZERO : period.getUsedAmount();
        vo.setTotalAmount(total);
        vo.setUsedAmount(used);
        vo.setRemainAmount(total.subtract(used));
        vo.setExecutionRate(total.signum() == 0
                ? BigDecimal.ZERO
                : used.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP));

        List<BudgetStatOverviewVO.ItemStatVO> itemStats = budgetItemMapper.selectItemStats(period.getId());
        vo.setItemStats(itemStats);

        BudgetStatOverviewVO.ApplyStatVO applyStat = budgetApplyMapper.selectApplyStat(period.getId());
        if (applyStat == null) {
            applyStat = new BudgetStatOverviewVO.ApplyStatVO();
        }
        if (applyStat.getPendingCount() == null) {
            applyStat.setPendingCount(0);
        }
        if (applyStat.getApprovedCount() == null) {
            applyStat.setApprovedCount(0);
        }
        if (applyStat.getRejectedCount() == null) {
            applyStat.setRejectedCount(0);
        }
        if (applyStat.getApprovedAmount() == null) {
            applyStat.setApprovedAmount(BigDecimal.ZERO);
        }
        vo.setApplyStats(applyStat);

        vo.setMonthTrend(budgetExpenseMapper.selectMonthTrend(period.getId()));

        BigDecimal itemAmountSum = itemStats.stream()
                .map(BudgetStatOverviewVO.ItemStatVO::getBudgetAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setOverBudgetItems(itemAmountSum.compareTo(total) > 0
                ? itemStats.stream().map(BudgetStatOverviewVO.ItemStatVO::getItemName).collect(Collectors.toList())
                : new ArrayList<>());
        return vo;
    }
}
