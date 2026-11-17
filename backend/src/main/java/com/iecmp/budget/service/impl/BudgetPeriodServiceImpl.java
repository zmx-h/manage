package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.dto.BudgetPeriodAddDTO;
import com.iecmp.budget.dto.BudgetPeriodPublicDTO;
import com.iecmp.budget.dto.BudgetPeriodStatusDTO;
import com.iecmp.budget.dto.BudgetPeriodUpdateDTO;
import com.iecmp.budget.entity.BudgetApply;
import com.iecmp.budget.entity.BudgetItem;
import com.iecmp.budget.entity.BudgetPeriod;
import com.iecmp.budget.enums.BudgetApplyStatusEnum;
import com.iecmp.budget.enums.BudgetErrorCode;
import com.iecmp.budget.enums.BudgetPeriodStatusEnum;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
    public PageResult<AdminBudgetPeriodPageVO> adminPeriodPage(AdminBudgetPeriodPageQuery query) {
        int pageNum = query.getPageNum();
        int pageSize = query.getPageSize();
        int offset = (pageNum - 1) * pageSize;
        Long total = baseMapper.countAdminPage(
                query.getKeyword(), query.getYear(), query.getStatus(), query.getIsPublic());
        List<AdminBudgetPeriodPageVO> list = baseMapper.selectAdminPage(
                query.getKeyword(), query.getYear(), query.getStatus(), query.getIsPublic(),
                offset, pageSize);
        long pages = total == 0 ? 0 : (total + pageSize - 1) / pageSize;
        return new PageResult<>(total, (long) pageNum, (long) pageSize, pages, list);
    }

    @Override
    public BudgetPeriodCreateVO createPeriod(BudgetPeriodAddDTO dto) {
        LocalDate startDate = parseDate(dto.getStartDate());
        LocalDate endDate = parseDate(dto.getEndDate());
        checkDateRange(startDate, endDate);

        BudgetPeriod period = new BudgetPeriod();
        period.setTitle(dto.getTitle());
        period.setYear(dto.getYear());
        period.setTotalAmount(dto.getTotalAmount());
        period.setUsedAmount(BigDecimal.ZERO);
        period.setStatus(BudgetPeriodStatusEnum.DRAFT.getValue());
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        period.setIsPublic(0);
        period.setDescription(dto.getDescription());
        save(period);

        BudgetPeriodCreateVO vo = new BudgetPeriodCreateVO();
        vo.setPeriodId(period.getId());
        return vo;
    }

    @Override
    public void updatePeriod(BudgetPeriodUpdateDTO dto) {
        BudgetPeriod period = getById(dto.getPeriodId());
        if (period == null) {
            throw new BusinessException(BudgetErrorCode.E30001.getCode(), BudgetErrorCode.E30001.getMsg());
        }
        BigDecimal usedAmount = period.getUsedAmount() == null ? BigDecimal.ZERO : period.getUsedAmount();
        if (dto.getTotalAmount().compareTo(usedAmount) < 0) {
            throw new BusinessException(BudgetErrorCode.E30016.getCode(), BudgetErrorCode.E30016.getMsg());
        }

        LocalDate startDate = parseDate(dto.getStartDate());
        LocalDate endDate = parseDate(dto.getEndDate());
        // 未传的字段沿用库中原值（update-strategy = not_null 跳过 null 字段），校验用合并后的生效值
        LocalDate effectiveStart = startDate != null ? startDate : period.getStartDate();
        LocalDate effectiveEnd = endDate != null ? endDate : period.getEndDate();
        checkDateRange(effectiveStart, effectiveEnd);

        period.setTitle(dto.getTitle());
        period.setYear(dto.getYear());
        period.setTotalAmount(dto.getTotalAmount());
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        period.setDescription(dto.getDescription());
        updateById(period);
    }

    @Override
    public void deletePeriod(Long id) {
        BudgetPeriod period = getById(id);
        if (period == null) {
            throw new BusinessException(BudgetErrorCode.E30001.getCode(), BudgetErrorCode.E30001.getMsg());
        }
        long itemCount = budgetItemMapper.selectCount(
                new LambdaQueryWrapper<BudgetItem>().eq(BudgetItem::getPeriodId, id));
        if (itemCount > 0) {
            throw new BusinessException(BudgetErrorCode.E30014.getCode(), BudgetErrorCode.E30014.getMsg());
        }
        long applyCount = budgetApplyMapper.selectCount(
                new LambdaQueryWrapper<BudgetApply>().eq(BudgetApply::getPeriodId, id));
        if (applyCount > 0) {
            throw new BusinessException(BudgetErrorCode.E30014.getCode(), BudgetErrorCode.E30014.getMsg());
        }
        baseMapper.deleteById(id);
    }

    @Override
    public void changeStatus(Long id, BudgetPeriodStatusDTO dto) {
        BudgetPeriod period = getById(id);
        if (period == null) {
            throw new BusinessException(BudgetErrorCode.E30001.getCode(), BudgetErrorCode.E30001.getMsg());
        }
        Integer current = period.getStatus();
        Integer target = dto.getStatus();
        if (BudgetPeriodStatusEnum.RUNNING.getValue().equals(target)) {
            // 仅允许 草稿 → 执行中（已结束为终态、执行中不可回到草稿），且至少存在一个科目
            if (!BudgetPeriodStatusEnum.DRAFT.getValue().equals(current)) {
                throw new BusinessException(BudgetErrorCode.E30018.getCode(), BudgetErrorCode.E30018.getMsg());
            }
            long itemCount = budgetItemMapper.selectCount(
                    new LambdaQueryWrapper<BudgetItem>().eq(BudgetItem::getPeriodId, id));
            if (itemCount == 0) {
                throw new BusinessException(BudgetErrorCode.E30018.getCode(), BudgetErrorCode.E30018.getMsg());
            }
        } else if (BudgetPeriodStatusEnum.FINISHED.getValue().equals(target)) {
            // 仅允许 执行中 → 已结束，且无待审核申请
            if (!BudgetPeriodStatusEnum.RUNNING.getValue().equals(current)) {
                throw new BusinessException(BudgetErrorCode.E30018.getCode(), BudgetErrorCode.E30018.getMsg());
            }
            long pendingCount = budgetApplyMapper.selectCount(
                    new LambdaQueryWrapper<BudgetApply>()
                            .eq(BudgetApply::getPeriodId, id)
                            .eq(BudgetApply::getStatus, BudgetApplyStatusEnum.PENDING.getValue()));
            if (pendingCount > 0) {
                throw new BusinessException(BudgetErrorCode.E30019.getCode(), BudgetErrorCode.E30019.getMsg());
            }
        } else {
            // 目标状态只能是 1（执行中）或 2（已结束）
            throw new BusinessException(BudgetErrorCode.E30018.getCode(), BudgetErrorCode.E30018.getMsg());
        }
        period.setStatus(target);
        updateById(period);
    }

    @Override
    public void changePublic(Long id, BudgetPeriodPublicDTO dto) {
        BudgetPeriod period = getById(id);
        if (period == null) {
            throw new BusinessException(BudgetErrorCode.E30001.getCode(), BudgetErrorCode.E30001.getMsg());
        }
        // 公示时写入首次公示时间；取消公示不清空 publicTime（文档 9.4.6）
        if (Integer.valueOf(1).equals(dto.getIsPublic())
                && !Integer.valueOf(1).equals(period.getIsPublic())) {
            period.setPublicTime(LocalDateTime.now());
        }
        period.setIsPublic(dto.getIsPublic());
        updateById(period);
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

    /** 解析 yyyy-MM-dd 日期，空串返回 null，格式非法抛 400。 */
    private LocalDate parseDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new BusinessException(400, "日期格式错误，需为 yyyy-MM-dd");
        }
    }

    /** 结束日期需晚于开始日期（两者都有值时校验，文档 9.4.2）。 */
    private void checkDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && !endDate.isAfter(startDate)) {
            throw new BusinessException(400, "周期结束日期需晚于开始日期");
        }
    }
}
