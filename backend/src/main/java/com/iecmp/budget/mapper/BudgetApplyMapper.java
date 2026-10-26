package com.iecmp.budget.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.budget.entity.BudgetApply;
import com.iecmp.budget.vo.BudgetApplyDetailVO;
import com.iecmp.budget.vo.BudgetApplyMyPageVO;
import com.iecmp.budget.vo.BudgetApplyPageVO;
import com.iecmp.budget.vo.BudgetStatMyVO;
import com.iecmp.budget.vo.BudgetStatOverviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

import java.util.List;

/**
 * 预算申请表（{@code budget_apply}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/budget/BudgetApplyMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_period_id (period_id)}</li>
 *   <li>{@code KEY idx_item_id (item_id)}</li>
 *   <li>{@code KEY idx_apply_user_status (apply_user_id, status)}</li>
 *   <li>{@code KEY idx_apply_user_time (apply_user_id, create_time)}</li>
 *   <li>{@code KEY idx_attach_file_id (attach_file_id)}</li>
 *   <li>{@code KEY idx_status_create (status, create_time)}</li>
 *   <li>{@code KEY idx_create_time (create_time)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface BudgetApplyMapper extends BaseMapper<BudgetApply> {

    /** 3-06 我的申请分页 */
    List<BudgetApplyMyPageVO> selectMyPage(@Param("applyUserId") Long applyUserId,
                                           @Param("status") Integer status,
                                           @Param("periodId") Long periodId,
                                           @Param("beginTime") String beginTime,
                                           @Param("endTime") String endTime,
                                           @Param("offset") Integer offset,
                                           @Param("pageSize") Integer pageSize);

    /** 3-06 配套总数 */
    Long countMyPage(@Param("applyUserId") Long applyUserId,
                     @Param("status") Integer status,
                     @Param("periodId") Long periodId,
                     @Param("beginTime") String beginTime,
                     @Param("endTime") String endTime);

    /** 3-07 申请详情 */
    BudgetApplyDetailVO selectDetailById(@Param("applyId") Long applyId);

    /** 3-07 关联支出 */
    List<BudgetApplyDetailVO.ExpenseListVO> selectExpenseByApplyId(@Param("applyId") Long applyId);

    /** 3-09 我的经费汇总 */
    BudgetStatMyVO selectMyStat(@Param("applyUserId") Long applyUserId,
                                @Param("beginTime") LocalDateTime beginTime,
                                @Param("endTime") LocalDateTime endTime);
/** 3-20 管理端申请审核分页 */
    List<BudgetApplyPageVO> selectAdminPage(@Param("status") Integer status,
                                              @Param("periodId") Long periodId,
                                              @Param("itemId") Long itemId,
                                              @Param("applyUserId") Long applyUserId,
                                              @Param("keyword") String keyword,
                                              @Param("beginTime") String beginTime,
                                              @Param("endTime") String endTime,
                                              @Param("offset") Integer offset,
                                              @Param("pageSize") Integer pageSize);

    /** 3-20 配套总数 */
    Long countAdminPage(@Param("status") Integer status,
                          @Param("periodId") Long periodId,
                          @Param("itemId") Long itemId,
                          @Param("applyUserId") Long applyUserId,
                          @Param("keyword") String keyword,
                          @Param("beginTime") String beginTime,
                          @Param("endTime") String endTime);

    /** 3-25 申请统计 */
    BudgetStatOverviewVO.ApplyStatVO selectApplyStat(@Param("periodId") Long periodId);
}
