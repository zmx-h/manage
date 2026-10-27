package com.iecmp.budget.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.budget.dto.BudgetApplyAuditDTO;
import com.iecmp.budget.dto.BudgetApplyDTO;
import com.iecmp.budget.entity.BudgetApply;
import com.iecmp.budget.query.BudgetApplyPageQuery;
import com.iecmp.budget.vo.BudgetApplyDetailVO;
import com.iecmp.budget.vo.BudgetApplyMyPageVO;
import com.iecmp.budget.vo.BudgetApplyPageVO;
import com.iecmp.budget.vo.BudgetApplyVO;
import com.iecmp.budget.vo.BudgetStatMyVO;
import com.iecmp.common.result.PageResult;

/**
 * 预算申请业务接口（{@code budget_apply}）。
 *
 * @author IECMP 后端组
 */
public interface BudgetApplyService extends IService<BudgetApply> {

    /** 3-05 提交预算申请 */
    BudgetApplyVO createApply(BudgetApplyDTO dto, Long currentUserId);

    /** 3-06 我的预算申请分页 */
    PageResult<BudgetApplyMyPageVO> myApplyPage(Long currentUserId, Integer status, Long periodId,
                                                 String beginTime, String endTime,
                                                 Integer pageNum, Integer pageSize);

    /** 3-07 预算申请详情 */
    BudgetApplyDetailVO applyDetail(Long applyId, Long currentUserId);

    /** 3-08 撤销预算申请 */
    void cancelApply(Long applyId, Long currentUserId);

    /** 3-09 我的经费汇总 */
    BudgetStatMyVO myStat(Long currentUserId, String year);

    /** 3-20 申请审核分页（后台） */
    PageResult<BudgetApplyPageVO> adminApplyPage(BudgetApplyPageQuery query);

    /** 3-21 申请审核（通过 / 驳回） */
    void auditApply(Long id, BudgetApplyAuditDTO dto, Long currentUserId);
}
