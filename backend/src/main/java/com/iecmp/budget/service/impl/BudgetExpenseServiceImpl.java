package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.dto.BudgetExpenseAddDTO;
import com.iecmp.budget.entity.BudgetApply;
import com.iecmp.budget.entity.BudgetExpense;
import com.iecmp.budget.entity.BudgetItem;
import com.iecmp.budget.entity.BudgetPeriod;
import com.iecmp.budget.enums.BudgetErrorCode;
import com.iecmp.budget.mapper.BudgetApplyMapper;
import com.iecmp.budget.mapper.BudgetExpenseMapper;
import com.iecmp.budget.mapper.BudgetItemMapper;
import com.iecmp.budget.mapper.BudgetPeriodMapper;
import com.iecmp.budget.query.AdminBudgetExpensePageQuery;
import com.iecmp.budget.service.BudgetExpenseService;
import com.iecmp.budget.vo.AdminBudgetExpensePageVO;
import com.iecmp.budget.vo.BudgetExpenseCreateVO;
import com.iecmp.budget.vo.UserBudgetExpensePageVO;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 支出记录业务实现（{@code budget_expense}）。
 *
 * @author IECMP 后端组
 */
@Service
@RequiredArgsConstructor
public class BudgetExpenseServiceImpl extends ServiceImpl<BudgetExpenseMapper, BudgetExpense>
        implements BudgetExpenseService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("9999999999.99");
    private final BudgetPeriodMapper budgetPeriodMapper;
    private final BudgetItemMapper budgetItemMapper;
    private final BudgetApplyMapper budgetApplyMapper;

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

    @Override
    public PageResult<AdminBudgetExpensePageVO> adminExpensePage(AdminBudgetExpensePageQuery query) {
        int pageNum = query.getPageNum();
        int pageSize = query.getPageSize();
        int offset = (pageNum - 1) * pageSize;
        Long total = baseMapper.countAdminPage(
                query.getPeriodId(), query.getItemId(), query.getHandlerId(),
                query.getKeyword(), query.getBeginDate(), query.getEndDate());
        List<AdminBudgetExpensePageVO> list = baseMapper.selectAdminPage(
                query.getPeriodId(), query.getItemId(), query.getHandlerId(),
                query.getKeyword(), query.getBeginDate(), query.getEndDate(),
                offset, pageSize);
        long pages = total == 0 ? 0 : (total + pageSize - 1) / pageSize;
        return new PageResult<>(total, (long) pageNum, (long) pageSize, pages, list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BudgetExpenseCreateVO createExpense(BudgetExpenseAddDTO dto, Long currentUserId) {
        BudgetPeriod period = budgetPeriodMapper.selectForUpdate(dto.getPeriodId());
        if (period == null) {
            throw new BusinessException(BudgetErrorCode.E30001.getCode(), BudgetErrorCode.E30001.getMsg());
        }
        if (period.getStatus() == 2) {
            throw new BusinessException(BudgetErrorCode.E30015.getCode(), BudgetErrorCode.E30015.getMsg());
        }
        BudgetItem item = budgetItemMapper.selectForUpdate(dto.getItemId());
        if (item == null) {
            throw new BusinessException(BudgetErrorCode.E30003.getCode(), BudgetErrorCode.E30003.getMsg());
        }
        if (item.getPeriodId() == null || !item.getPeriodId().equals(period.getId())) {
            throw new BusinessException(BudgetErrorCode.E30004.getCode(), BudgetErrorCode.E30004.getMsg());
        }
        BigDecimal amount = dto.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(BudgetErrorCode.E30011.getCode(), BudgetErrorCode.E30011.getMsg());
        }
        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "支出金额不能超过 " + MAX_AMOUNT);
        }
        if (amount.stripTrailingZeros().scale() > 2) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "支出金额最多保留 2 位小数");
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);

        if (dto.getApplyId() != null) {
            BudgetApply apply = budgetApplyMapper.selectById(dto.getApplyId());
            if (apply == null) {
                throw new BusinessException(BudgetErrorCode.E30007.getCode(), BudgetErrorCode.E30007.getMsg());
            }
            if (!period.getId().equals(apply.getPeriodId()) || !item.getId().equals(apply.getItemId())) {
                throw new BusinessException(BudgetErrorCode.E30028.getCode(), BudgetErrorCode.E30028.getMsg());
            }
            if (apply.getStatus() == null || apply.getStatus() != 1) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "关联的预算申请未通过审核，无法登记支出");
            }
            BigDecimal approved = apply.getApprovedAmount() != null ? apply.getApprovedAmount() : apply.getAmount();
            BigDecimal registered = baseMapper.selectExpenseAmountByApplyId(dto.getApplyId());
            BigDecimal available = amountOrZero(approved).subtract(amountOrZero(registered));
            if (amount.compareTo(available) > 0) {
                throw new BusinessException(BudgetErrorCode.E30025.getCode(), BudgetErrorCode.E30025.getMsg());
            }
        }
        BigDecimal periodRemain = amountOrZero(period.getTotalAmount())
                .subtract(amountOrZero(period.getUsedAmount())).max(BigDecimal.ZERO);
        if (amount.compareTo(periodRemain) > 0) {
            throw new BusinessException(BudgetErrorCode.E30011.getCode(), BudgetErrorCode.E30011.getMsg());
        }
        BigDecimal itemRemain = amountOrZero(item.getBudgetAmount())
                .subtract(amountOrZero(item.getUsedAmount())).max(BigDecimal.ZERO);
        if (amount.compareTo(itemRemain) > 0) {
            throw new BusinessException(BudgetErrorCode.E30011.getCode(), BudgetErrorCode.E30011.getMsg());
        }
        BudgetExpense expense = new BudgetExpense();
        expense.setPeriodId(dto.getPeriodId());
        expense.setItemId(dto.getItemId());
        expense.setApplyId(dto.getApplyId());
        expense.setTitle(dto.getTitle());
        expense.setAmount(amount);
        try {
            expense.setExpenseDate(LocalDate.parse(dto.getExpenseDate().trim()));
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "支出日期格式错误，应为 yyyy-MM-dd");
        }
        expense.setVoucherFileId(dto.getVoucherFileId());
        expense.setRemark(dto.getRemark());
        expense.setHandlerId(currentUserId);
        baseMapper.insert(expense);

        period.setUsedAmount(amountOrZero(period.getUsedAmount()).add(amount));
        budgetPeriodMapper.updateById(period);

        item.setUsedAmount(amountOrZero(item.getUsedAmount()).add(amount));
        budgetItemMapper.updateById(item);

        BudgetExpenseCreateVO vo = new BudgetExpenseCreateVO();
        vo.setExpenseId(expense.getId());
        vo.setPeriodUsedAmount(period.getUsedAmount());
        vo.setItemUsedAmount(item.getUsedAmount());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExpense(Long id) {
        BudgetExpense expense = getById(id);
        if (expense == null) {
            throw new BusinessException(BudgetErrorCode.E30012.getCode(), BudgetErrorCode.E30012.getMsg());
        }
        BigDecimal amount = expense.getAmount();
        baseMapper.deleteById(id);

        BudgetPeriod period = budgetPeriodMapper.selectById(expense.getPeriodId());
        if (period != null) {
            period.setUsedAmount(amountOrZero(period.getUsedAmount()).subtract(amount).max(BigDecimal.ZERO));
            budgetPeriodMapper.updateById(period);
        }

        BudgetItem item = budgetItemMapper.selectById(expense.getItemId());
        if (item != null) {
            item.setUsedAmount(amountOrZero(item.getUsedAmount()).subtract(amount).max(BigDecimal.ZERO));
            budgetItemMapper.updateById(item);
        }
    }

    private static BigDecimal amountOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
