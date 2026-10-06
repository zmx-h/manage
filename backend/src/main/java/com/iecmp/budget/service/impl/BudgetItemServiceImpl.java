package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.entity.BudgetItem;
import com.iecmp.budget.mapper.BudgetItemMapper;
import com.iecmp.budget.service.BudgetItemService;
import com.iecmp.budget.vo.BudgetPeriodItemListVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 预算科目业务实现（{@code budget_item}）。
 *
 * @author IECMP 后端组
 */
@Service
public class BudgetItemServiceImpl extends ServiceImpl<BudgetItemMapper, BudgetItem>
        implements BudgetItemService {

    @Override
    public List<BudgetPeriodItemListVO> itemList(Long periodId) {
        return baseMapper.selectByPeriodId(periodId);
    }
}
