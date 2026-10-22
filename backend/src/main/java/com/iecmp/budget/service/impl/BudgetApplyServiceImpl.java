package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.dto.BudgetApplyAuditDTO;
import com.iecmp.budget.dto.BudgetApplyDTO;
import com.iecmp.budget.entity.BudgetApply;
import com.iecmp.budget.entity.BudgetItem;
import com.iecmp.budget.entity.BudgetPeriod;
import com.iecmp.budget.enums.BudgetErrorCode;
import com.iecmp.budget.mapper.BudgetApplyMapper;
import com.iecmp.budget.mapper.BudgetItemMapper;
import com.iecmp.budget.mapper.BudgetPeriodMapper;
import com.iecmp.budget.query.BudgetApplyPageQuery;
import com.iecmp.budget.service.BudgetApplyService;
import com.iecmp.budget.vo.BudgetApplyDetailVO;
import com.iecmp.budget.vo.BudgetApplyMyPageVO;
import com.iecmp.budget.vo.BudgetApplyPageVO;
import com.iecmp.budget.vo.BudgetApplyVO;
import com.iecmp.budget.vo.BudgetStatMyVO;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预算申请业务实现（{@code budget_apply}）。
 *
 * @author IECMP 后端组
 */
@Service
@RequiredArgsConstructor
public class BudgetApplyServiceImpl extends ServiceImpl<BudgetApplyMapper, BudgetApply>
        implements BudgetApplyService {

    private final BudgetPeriodMapper periodMapper;

    private final BudgetItemMapper itemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BudgetApplyVO createApply(BudgetApplyDTO dto, Long currentUserId) {
        BudgetPeriod period = periodMapper.selectById(dto.getPeriodId());
        if (period == null) {
            throw new BusinessException(BudgetErrorCode.E30001.getCode(), BudgetErrorCode.E30001.getMsg());
        }
        if (period.getStatus() == 2) {
            throw new BusinessException(BudgetErrorCode.E30002.getCode(), BudgetErrorCode.E30002.getMsg());
        }

        BudgetItem item = itemMapper.selectForUpdate(dto.getItemId());
        if (item == null) {
            throw new BusinessException(BudgetErrorCode.E30003.getCode(), BudgetErrorCode.E30003.getMsg());
        }
        if (item.getPeriodId() == null || !item.getPeriodId().equals(dto.getPeriodId())) {
            throw new BusinessException(BudgetErrorCode.E30004.getCode(), BudgetErrorCode.E30004.getMsg());
        }
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(BudgetErrorCode.E30005.getCode(), BudgetErrorCode.E30005.getMsg());
        }
        BigDecimal remain = item.getBudgetAmount().subtract(item.getUsedAmount());
        if (dto.getAmount().compareTo(remain) > 0) {
            throw new BusinessException(BudgetErrorCode.E30006.getCode(), BudgetErrorCode.E30006.getMsg());
        }

        BudgetApply apply = new BudgetApply();
        apply.setPeriodId(dto.getPeriodId());
        apply.setItemId(dto.getItemId());
        apply.setApplyUserId(currentUserId);
        apply.setTitle(dto.getTitle());
        apply.setAmount(dto.getAmount());
        apply.setReason(dto.getReason());
        if (dto.getExpectDate() != null && !dto.getExpectDate().isEmpty()) {
            try {
                apply.setExpectDate(LocalDate.parse(dto.getExpectDate()));
            } catch (java.time.format.DateTimeParseException e) {
                throw new BusinessException(400, "期望使用日期格式错误，应为 yyyy-MM-dd");
            }
        }
        apply.setAttachFileId(dto.getAttachFileId());
        apply.setStatus(0);
        baseMapper.insert(apply);

        BudgetApplyVO vo = new BudgetApplyVO();
        vo.setApplyId(apply.getId());
        return vo;
    }

    @Override
    public PageResult<BudgetApplyMyPageVO> myApplyPage(Long currentUserId, Integer status, Long periodId,
            String beginTime, String endTime,
            Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Long total = baseMapper.countMyPage(currentUserId, status, periodId, beginTime, endTime);
        List<BudgetApplyMyPageVO> list = baseMapper.selectMyPage(
                currentUserId, status, periodId, beginTime, endTime, offset, pageSize);
        long pages = total == 0 ? 0 : (total + pageSize - 1) / pageSize;
        return new PageResult<>(total, (long) pageNum, (long) pageSize, pages, list);
    }

    @Override
    public BudgetApplyDetailVO applyDetail(Long applyId, Long currentUserId) {
        BudgetApplyDetailVO vo = baseMapper.selectDetailById(applyId);
        if (vo == null) {
            throw new BusinessException(BudgetErrorCode.E30007.getCode(), BudgetErrorCode.E30007.getMsg());
        }
        if (!vo.getApplyUserId().equals(currentUserId)) {
            throw new BusinessException(BudgetErrorCode.E30008.getCode(), BudgetErrorCode.E30008.getMsg());
        }
        List<BudgetApplyDetailVO.ExpenseListVO> expenseList = baseMapper.selectExpenseByApplyId(applyId);
        vo.setExpenseList(expenseList);
        return vo;
    }

    @Override
    public void cancelApply(Long applyId, Long currentUserId) {
        BudgetApply apply = baseMapper.selectById(applyId);
        if (apply == null) {
            throw new BusinessException(BudgetErrorCode.E30007.getCode(), BudgetErrorCode.E30007.getMsg());
        }
        if (!apply.getApplyUserId().equals(currentUserId)) {
            throw new BusinessException(BudgetErrorCode.E30008.getCode(), BudgetErrorCode.E30008.getMsg());
        }
        if (apply.getStatus() != 0) {
            throw new BusinessException(BudgetErrorCode.E30009.getCode(), BudgetErrorCode.E30009.getMsg());
        }
        apply.setStatus(3);
        baseMapper.updateById(apply);
    }

    @Override
    public BudgetStatMyVO myStat(Long currentUserId, String year) {
        if (year == null || year.isEmpty()) {
            year = String.valueOf(LocalDate.now().getYear());
        }
        return baseMapper.selectMyStat(currentUserId, year);
    }

    @Override
    public PageResult<BudgetApplyPageVO> adminApplyPage(BudgetApplyPageQuery query) {
        int pageNum = query.getPageNum();
        int pageSize = query.getPageSize();
        int offset = (pageNum - 1) * pageSize;
        Long total = baseMapper.countAdminPage(
                query.getStatus(), query.getPeriodId(), query.getItemId(),
                query.getApplyUserId(), query.getKeyword(),
                query.getBeginTime(), query.getEndTime());
        List<BudgetApplyPageVO> list = baseMapper.selectAdminPage(
                query.getStatus(), query.getPeriodId(), query.getItemId(),
                query.getApplyUserId(), query.getKeyword(),
                query.getBeginTime(), query.getEndTime(),
                offset, pageSize);
        long pages = total == 0 ? 0 : (total + pageSize - 1) / pageSize;
        return new PageResult<>(total, (long) pageNum, (long) pageSize, pages, list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditApply(Long id, BudgetApplyAuditDTO dto, Long currentUserId) {
        BudgetApply apply = baseMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException(BudgetErrorCode.E30007.getCode(), BudgetErrorCode.E30007.getMsg());
        }
        if (apply.getStatus() != 0) {
            throw new BusinessException(BudgetErrorCode.E30010.getCode(), BudgetErrorCode.E30010.getMsg());
        }
        if (dto.getAuditStatus() == 1) {
            BigDecimal actualApproved = dto.getApprovedAmount() != null
                    ? dto.getApprovedAmount() : apply.getAmount();
            BudgetItem item = itemMapper.selectById(apply.getItemId());
            if (item != null) {
                BigDecimal remain = item.getBudgetAmount().subtract(item.getUsedAmount());
                if (actualApproved.compareTo(remain) > 0) {
                    throw new BusinessException(BudgetErrorCode.E30006.getCode(), BudgetErrorCode.E30006.getMsg());
                }
            }
            apply.setStatus(1);
            if (dto.getApprovedAmount() != null) {
                apply.setApprovedAmount(dto.getApprovedAmount());
            }
        } else {
            apply.setStatus(2);
        }
        apply.setAuditUserId(currentUserId);
        apply.setAuditTime(LocalDateTime.now());
        apply.setAuditRemark(dto.getAuditRemark());
        baseMapper.updateById(apply);
    }
}
