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
}
