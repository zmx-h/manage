package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.entity.BudgetItem;
import com.iecmp.budget.entity.BudgetPeriod;
import com.iecmp.budget.enums.BudgetErrorCode;
import com.iecmp.budget.mapper.BudgetItemMapper;
import com.iecmp.budget.mapper.BudgetPeriodMapper;
import com.iecmp.budget.service.BudgetItemService;
import com.iecmp.budget.vo.BudgetPeriodItemListVO;
import com.iecmp.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 预算科目业务实现（{@code budget_item}）。
 *
 * @author IECMP 后端组
 */
@Service
@RequiredArgsConstructor
public class BudgetItemServiceImpl extends ServiceImpl<BudgetItemMapper, BudgetItem>
        implements BudgetItemService {

    private final BudgetPeriodMapper periodMapper;

    @Override
    public List<BudgetPeriodItemListVO> itemList(Long periodId) {
        BudgetPeriod period = periodMapper.selectOne(Wrappers.<BudgetPeriod>lambdaQuery()
                .eq(BudgetPeriod::getId, periodId)
                .eq(BudgetPeriod::getIsPublic, 1)
                .ne(BudgetPeriod::getStatus, 0));
        if (period == null) {
            throw new BusinessException(BudgetErrorCode.E30001.getCode(), BudgetErrorCode.E30001.getMsg());
        }
        return baseMapper.selectByPeriodId(periodId);
    }
}
