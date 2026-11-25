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
import com.iecmp.common.context.LoginUser;
import com.iecmp.common.context.UserContext;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private static final int MAX_NAME_LENGTH = 64;
    private static final int MAX_REMARK_LENGTH = 255;
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("9999999999.99");

    private final BudgetPeriodMapper periodMapper;
    private final BudgetApplyMapper budgetApplyMapper;
    private final BudgetExpenseMapper budgetExpenseMapper;

    @Override
    public List<BudgetPeriodItemListVO> itemList(Long periodId) {
        BudgetPeriod period = periodId == null ? null : periodMapper.selectById(periodId);
        if (period == null) {
            throw new BusinessException(BudgetErrorCode.E30001.getCode(), BudgetErrorCode.E30001.getMsg());
        }
        boolean visible = period.getIsPublic() != null && period.getIsPublic() == 1
                && period.getStatus() != null && period.getStatus() != 0;
        if (!visible && !canManageBudget()) {
            throw new BusinessException(BudgetErrorCode.E30001.getCode(), BudgetErrorCode.E30001.getMsg());
        }
        return baseMapper.selectByPeriodId(periodId);
    }

    @Override
    public BudgetItemCreateVO createItem(BudgetItemAddDTO dto) {
        BudgetPeriod period = requirePeriod(dto.getPeriodId());
        ensurePeriodEditable(period);
        String name = validateName(dto.getName());
        BigDecimal budgetAmount = validateAmount(dto.getBudgetAmount());
        String remark = validateRemark(dto.getRemark());
        ensureNameUnique(period.getId(), name, null);
        ensureAllocatedWithinPeriod(period, budgetAmount, null);

        BudgetItem item = new BudgetItem();
        item.setPeriodId(period.getId());
        item.setName(name);
        item.setCategory(validateCategory(dto.getCategory()));
        item.setBudgetAmount(budgetAmount);
        item.setUsedAmount(BigDecimal.ZERO);
        item.setSortNo(dto.getSortNo() != null ? dto.getSortNo() : 0);
        item.setRemark(remark);
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
        BudgetPeriod period = requirePeriod(item.getPeriodId());
        ensurePeriodEditable(period);
        String name = validateName(dto.getName());
        BigDecimal budgetAmount = validateAmount(dto.getBudgetAmount());
        BigDecimal usedAmount = amountOrZero(item.getUsedAmount());
        if (budgetAmount.compareTo(usedAmount) < 0) {
            throw new BusinessException(BudgetErrorCode.E30017.getCode(), BudgetErrorCode.E30017.getMsg());
        }
        ensureNameUnique(period.getId(), name, item.getId());
        ensureAllocatedWithinPeriod(period, budgetAmount, item.getId());

        item.setName(name);
        item.setCategory(validateCategory(dto.getCategory()));
        item.setBudgetAmount(budgetAmount);
        item.setSortNo(dto.getSortNo());
        item.setRemark(validateRemark(dto.getRemark()));
        updateById(item);
    }

    @Override
    public void deleteItem(Long id) {
        BudgetItem item = getById(id);
        if (item == null) {
            throw new BusinessException(BudgetErrorCode.E30003.getCode(), BudgetErrorCode.E30003.getMsg());
        }
        ensurePeriodEditable(requirePeriod(item.getPeriodId()));
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

    /** 管理员可查看草稿 / 未公示周期的科目，普通成员仅能查看已公示且非草稿周期。 */
    private boolean canManageBudget() {
        LoginUser user = UserContext.get();
        if (user == null) {
            return false;
        }
        if (user.isSuperAdmin()) {
            return true;
        }
        var permissions = user.getPermissions();
        return permissions != null && (permissions.contains("budget:item:edit")
                || permissions.contains("budget:period:list")
                || permissions.contains("budget:period:edit"));
    }

    private BudgetPeriod requirePeriod(Long periodId) {
        BudgetPeriod period = periodId == null ? null : periodMapper.selectById(periodId);
        if (period == null) {
            throw new BusinessException(BudgetErrorCode.E30001.getCode(), BudgetErrorCode.E30001.getMsg());
        }
        return period;
    }

    private void ensurePeriodEditable(BudgetPeriod period) {
        if (period.getStatus() != null && period.getStatus() == 2) {
            throw new BusinessException(BudgetErrorCode.E30026.getCode(), BudgetErrorCode.E30026.getMsg());
        }
    }

    private String validateName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "科目名称不能为空");
        }
        String name = value.trim();
        if (name.length() > MAX_NAME_LENGTH) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "科目名称最长 " + MAX_NAME_LENGTH + " 字");
        }
        return name;
    }

    private Integer validateCategory(Integer category) {
        if (category == null) {
            return 5;
        }
        if (category < 1 || category > 5) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "科目分类仅支持 1 ~ 5");
        }
        return category;
    }

    private BigDecimal validateAmount(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(BudgetErrorCode.E30023.getCode(), BudgetErrorCode.E30023.getMsg());
        }
        if (value.compareTo(MAX_AMOUNT) > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "科目预算额度不能超过 " + MAX_AMOUNT);
        }
        if (value.stripTrailingZeros().scale() > 2) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "科目预算额度最多保留 2 位小数");
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private String validateRemark(String value) {
        if (value == null) {
            return null;
        }
        String remark = value.trim();
        if (remark.length() > MAX_REMARK_LENGTH) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "备注最长 " + MAX_REMARK_LENGTH + " 字");
        }
        return remark.isEmpty() ? null : remark;
    }

    private void ensureNameUnique(Long periodId, String name, Long excludeItemId) {
        long count = baseMapper.selectCount(Wrappers.<BudgetItem>lambdaQuery()
                .eq(BudgetItem::getPeriodId, periodId)
                .eq(BudgetItem::getName, name)
                .ne(excludeItemId != null, BudgetItem::getId, excludeItemId));
        if (count > 0) {
            throw new BusinessException(BudgetErrorCode.E30022.getCode(), BudgetErrorCode.E30022.getMsg());
        }
    }

    /** 校验「其余科目额度合计 + 本次额度」不超过周期总额度。 */
    private void ensureAllocatedWithinPeriod(BudgetPeriod period, BigDecimal newAmount, Long excludeItemId) {
        List<BudgetItem> items = baseMapper.selectList(Wrappers.<BudgetItem>lambdaQuery()
                .eq(BudgetItem::getPeriodId, period.getId())
                .ne(excludeItemId != null, BudgetItem::getId, excludeItemId));
        BigDecimal allocated = items.stream()
                .map(item -> amountOrZero(item.getBudgetAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = amountOrZero(period.getTotalAmount());
        if (allocated.add(newAmount).compareTo(total) > 0) {
            throw new BusinessException(BudgetErrorCode.E30021.getCode(), BudgetErrorCode.E30021.getMsg());
        }
    }

    private static BigDecimal amountOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
