package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.dto.BudgetPeriodAddDTO;
import com.iecmp.budget.dto.BudgetPeriodUpdateDTO;
import com.iecmp.budget.entity.BudgetApply;
import com.iecmp.budget.entity.BudgetItem;
import com.iecmp.budget.entity.BudgetPeriod;
import com.iecmp.budget.enums.BudgetErrorCode;
import com.iecmp.budget.mapper.BudgetApplyMapper;
import com.iecmp.budget.mapper.BudgetExpenseMapper;
import com.iecmp.budget.mapper.BudgetItemMapper;
import com.iecmp.budget.mapper.BudgetPeriodMapper;
import com.iecmp.budget.query.AdminBudgetPeriodPageQuery;
import com.iecmp.budget.query.BudgetStatOverviewQuery;
import com.iecmp.budget.service.BudgetPeriodService;
import com.iecmp.budget.vo.AdminBudgetPeriodPageVO;
import com.iecmp.budget.vo.BudgetPeriodCreateVO;
import com.iecmp.budget.vo.BudgetPeriodDetailVO;
import com.iecmp.budget.vo.BudgetStatOverviewVO;
import com.iecmp.budget.vo.UserBudgetPeriodPageVO;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
    private static final Pattern YEAR_PATTERN = Pattern.compile("^\\d{4}$");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final BudgetApplyMapper budgetApplyMapper;
    private final BudgetExpenseMapper budgetExpenseMapper;
    private final BudgetItemMapper budgetItemMapper;
    private final JdbcTemplate jdbcTemplate;

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
            throw error(BudgetErrorCode.E30001);
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

    @Override
    public PageResult<AdminBudgetPeriodPageVO> adminPeriodPage(AdminBudgetPeriodPageQuery query) {
        var wrapper = Wrappers.<BudgetPeriod>lambdaQuery()
                .eq(StringUtils.hasText(query.getYear()), BudgetPeriod::getYear,
                        query.getYear() == null ? null : query.getYear().trim())
                .eq(query.getStatus() != null, BudgetPeriod::getStatus, query.getStatus())
                .eq(query.getIsPublic() != null, BudgetPeriod::getIsPublic, query.getIsPublic());
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(BudgetPeriod::getTitle, query.getKeyword().trim());
        }
        wrapper.orderByDesc(BudgetPeriod::getYear)
                .orderByDesc(BudgetPeriod::getStartDate)
                .orderByDesc(BudgetPeriod::getId);
        IPage<BudgetPeriod> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<Long> ids = page.getRecords().stream().map(BudgetPeriod::getId).toList();
        Map<Long, Integer> itemCounts = countByPeriod("budget_item", ids);
        Map<Long, Integer> applyCounts = countByPeriod("budget_apply", ids);
        return PageResult.of(page, period -> toAdminPageVO(period,
                itemCounts.getOrDefault(period.getId(), 0),
                applyCounts.getOrDefault(period.getId(), 0)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BudgetPeriodCreateVO createPeriod(BudgetPeriodAddDTO dto) {
        String title = requireText(dto.getTitle(), 100, "周期标题");
        String year = validateYear(dto.getYear());
        BigDecimal totalAmount = validateAmount(dto.getTotalAmount());
        LocalDate startDate = parseDate(dto.getStartDate(), "开始日期");
        LocalDate endDate = parseDate(dto.getEndDate(), "结束日期");
        validateDateRange(startDate, endDate);

        BudgetPeriod period = new BudgetPeriod();
        period.setTitle(title);
        period.setYear(year);
        period.setTotalAmount(totalAmount);
        period.setUsedAmount(BigDecimal.ZERO);
        period.setStatus(0);
        period.setIsPublic(0);
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        period.setDescription(validateDescription(dto.getDescription()));
        save(period);

        BudgetPeriodCreateVO vo = new BudgetPeriodCreateVO();
        vo.setPeriodId(period.getId());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePeriod(BudgetPeriodUpdateDTO dto) {
        BudgetPeriod period = requirePeriod(dto.getPeriodId());
        BigDecimal totalAmount = validateAmount(dto.getTotalAmount());
        BigDecimal usedAmount = amountOrZero(period.getUsedAmount());
        if (totalAmount.compareTo(usedAmount) < 0) {
            throw error(BudgetErrorCode.E30016);
        }
        LocalDate startDate = parseDate(dto.getStartDate(), "开始日期");
        LocalDate endDate = parseDate(dto.getEndDate(), "结束日期");
        validateDateRange(startDate, endDate);

        period.setTitle(requireText(dto.getTitle(), 100, "周期标题"));
        period.setYear(validateYear(dto.getYear()));
        period.setTotalAmount(totalAmount);
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        period.setDescription(validateDescription(dto.getDescription()));
        updateById(period);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePeriod(Long id) {
        requirePeriod(id);
        long itemCount = budgetItemMapper.selectCount(Wrappers.<BudgetItem>lambdaQuery()
                .eq(BudgetItem::getPeriodId, id));
        long applyCount = budgetApplyMapper.selectCount(Wrappers.<BudgetApply>lambdaQuery()
                .eq(BudgetApply::getPeriodId, id));
        if (itemCount > 0 || applyCount > 0) {
            throw error(BudgetErrorCode.E30014);
        }
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, Integer status) {
        BudgetPeriod period = requirePeriod(id);
        if (status == null || (status != 1 && status != 2)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "目标状态仅支持 1 执行中或 2 已结束");
        }
        int current = period.getStatus() == null ? 0 : period.getStatus();
        if ((status == 1 && current != 0) || (status == 2 && current != 1)) {
            throw error(BudgetErrorCode.E30018);
        }
        if (status == 2) {
            long pending = budgetApplyMapper.selectCount(Wrappers.<BudgetApply>lambdaQuery()
                    .eq(BudgetApply::getPeriodId, id)
                    .eq(BudgetApply::getStatus, 0));
            if (pending > 0) {
                throw error(BudgetErrorCode.E30019);
            }
        }
        period.setStatus(status);
        updateById(period);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePublic(Long id, Integer isPublic) {
        BudgetPeriod period = requirePeriod(id);
        if (isPublic == null || (isPublic != 0 && isPublic != 1)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "isPublic 仅支持 0 或 1");
        }
        period.setIsPublic(isPublic);
        period.setPublicTime(isPublic == 1 ? LocalDateTime.now() : null);
        updateById(period);
    }

    @Override
    public void collect(Long id) {
        requirePeriod(id);
        throw error(BudgetErrorCode.E30020);
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
            throw error(BudgetErrorCode.E30001);
        }
        return period;
    }

    private BudgetPeriod requirePeriod(Long id) {
        BudgetPeriod period = id == null ? null : getById(id);
        if (period == null) {
            throw error(BudgetErrorCode.E30001);
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

    private AdminBudgetPeriodPageVO toAdminPageVO(BudgetPeriod period, int itemCount, int applyCount) {
        BigDecimal total = amountOrZero(period.getTotalAmount());
        BigDecimal used = amountOrZero(period.getUsedAmount());
        AdminBudgetPeriodPageVO vo = new AdminBudgetPeriodPageVO();
        vo.setPeriodId(period.getId());
        vo.setTitle(period.getTitle());
        vo.setYear(period.getYear());
        vo.setTotalAmount(total);
        vo.setUsedAmount(used);
        vo.setRemainAmount(total.subtract(used));
        vo.setExecutionRate(executionRate(used, total));
        vo.setStatus(period.getStatus());
        vo.setIsPublic(period.getIsPublic());
        vo.setStartDate(period.getStartDate() == null ? null : period.getStartDate().toString());
        vo.setEndDate(period.getEndDate() == null ? null : period.getEndDate().toString());
        vo.setItemCount(itemCount);
        vo.setApplyCount(applyCount);
        vo.setCreateTime(format(period.getCreateTime()));
        return vo;
    }

    private Map<Long, Integer> countByPeriod(String table, List<Long> periodIds) {
        if (periodIds == null || periodIds.isEmpty()) {
            return Map.of();
        }
        String placeholders = String.join(",", periodIds.stream().map(id -> "?").toList());
        String sql = "SELECT period_id, COUNT(*) AS cnt FROM " + table
                + " WHERE deleted = 0 AND period_id IN (" + placeholders + ") GROUP BY period_id";
        Map<Long, Integer> result = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            result.put(rs.getLong("period_id"), rs.getInt("cnt"));
        }, periodIds.toArray());
        return result;
    }

    private String validateYear(String value) {
        if (value == null || !YEAR_PATTERN.matcher(value.trim()).matches()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "所属年度格式应为 yyyy");
        }
        return value.trim();
    }

    private BigDecimal validateAmount(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "预算总额度必须大于 0");
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private String requireText(String value, int maxLength, String label) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, label + "不能为空");
        }
        String text = value.trim();
        if (text.length() > maxLength) {
            throw new BusinessException(ResultCode.PARAM_ERROR, label + "最长 " + maxLength + " 字");
        }
        return text;
    }

    private String validateDescription(String value) {
        if (value == null) {
            return null;
        }
        String text = value.trim();
        if (text.length() > 500) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "周期说明最长 500 字");
        }
        return text.isEmpty() ? null : text;
    }

    private LocalDate parseDate(String value, String label) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR, label + "格式应为 yyyy-MM-dd");
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "结束日期不能早于开始日期");
        }
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

    private String format(LocalDateTime time) {
        return time == null ? null : time.format(DATE_TIME_FMT);
    }

    private BusinessException error(BudgetErrorCode code) {
        return new BusinessException(code.getCode(), code.getMsg());
    }
}