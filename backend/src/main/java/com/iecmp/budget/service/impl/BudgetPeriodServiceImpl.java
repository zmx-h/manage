package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.entity.BudgetPeriod;
import com.iecmp.budget.enums.BudgetErrorCode;
import com.iecmp.budget.mapper.BudgetPeriodMapper;
import com.iecmp.budget.service.BudgetPeriodService;
import com.iecmp.budget.vo.BudgetPeriodDetailVO;
import com.iecmp.budget.vo.UserBudgetPeriodPageVO;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 预算周期业务实现（{@code budget_period}）。
 *
 * @author IECMP 后端组
 */
@Service
public class BudgetPeriodServiceImpl extends ServiceImpl<BudgetPeriodMapper, BudgetPeriod>
        implements BudgetPeriodService {

    private static final int MAX_PAGE_SIZE = 100;

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
}
