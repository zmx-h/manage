package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.dto.BudgetItemAddDTO;
import com.iecmp.budget.dto.BudgetItemUpdateDTO;
import com.iecmp.budget.entity.BudgetApply;
import com.iecmp.budget.entity.BudgetExpense;
import com.iecmp.budget.entity.BudgetItem;
import com.iecmp.budget.entity.BudgetPeriod;
import com.iecmp.budget.enums.BudgetErrorCode;
import com.iecmp.budget.mapper.BudgetApplyMapper;
import com.iecmp.budget.mapper.BudgetExpenseMapper;
import com.iecmp.budget.mapper.BudgetItemMapper;
import com.iecmp.budget.mapper.BudgetPeriodMapper;
import com.iecmp.budget.service.BudgetItemService;
import com.iecmp.budget.vo.BudgetItemCreateVO;
import com.iecmp.budget.vo.BudgetPeriodItemListVO;
import com.iecmp.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private final BudgetApplyMapper budgetApplyMapper;
    private final BudgetExpenseMapper budgetExpenseMapper;

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

    @Override
    public BudgetItemCreateVO createItem(BudgetItemAddDTO dto) {
        BudgetPeriod period = periodMapper.selectById(dto.getPeriodId());
        if (period == null) {
            throw new BusinessException(BudgetErrorCode.E30001.getCode(), BudgetErrorCode.E30001.getMsg());
        }
        BudgetItem item = new BudgetItem();
        item.setPeriodId(dto.getPeriodId());
        item.setName(dto.getName());
        item.setCategory(dto.getCategory() != null ? dto.getCategory() : 5);
        item.setBudgetAmount(dto.getBudgetAmount());
        item.setUsedAmount(BigDecimal.ZERO);
        item.setSortNo(dto.getSortNo() != null ? dto.getSortNo() : 0);
        item.setRemark(dto.getRemark());
        baseMapper.insert(item);
        BudgetItemCreateVO vo = new BudgetItemCreateVO();
        vo.setItemId(item.getId());
        return vo;
    }

    @Override
    public void updateItem(BudgetItemUpdateDTO dto) {
        BudgetItem item = getById(dto.getItemId());
        if (item == null) {
            throw new BusinessException(BudgetErrorCode.E30003.getCode(), BudgetErrorCode.E30003.getMsg());
        }
        if (dto.getBudgetAmount().compareTo(item.getUsedAmount()) < 0) {
            throw new BusinessException(BudgetErrorCode.E30017.getCode(), BudgetErrorCode.E30017.getMsg());
        }
        item.setName(dto.getName());
        item.setCategory(dto.getCategory());
        item.setBudgetAmount(dto.getBudgetAmount());
        item.setSortNo(dto.getSortNo());
        item.setRemark(dto.getRemark());
        updateById(item);
    }

    @Override
    public void deleteItem(Long id) {
        BudgetItem item = getById(id);
        if (item == null) {
            throw new BusinessException(BudgetErrorCode.E30003.getCode(), BudgetErrorCode.E30003.getMsg());
        }
        long applyCount = budgetApplyMapper.selectCount(
                new LambdaQueryWrapper<BudgetApply>().eq(BudgetApply::getItemId, id));
        if (applyCount > 0) {
            throw new BusinessException(BudgetErrorCode.E30013.getCode(), BudgetErrorCode.E30013.getMsg());
        }
        long expenseCount = budgetExpenseMapper.selectCount(
                new LambdaQueryWrapper<BudgetExpense>().eq(BudgetExpense::getItemId, id));
        if (expenseCount > 0) {
            throw new BusinessException(BudgetErrorCode.E30013.getCode(), BudgetErrorCode.E30013.getMsg());
        }
        baseMapper.deleteById(id);
    }
}
