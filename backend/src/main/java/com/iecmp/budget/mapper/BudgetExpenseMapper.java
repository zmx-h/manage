package com.iecmp.budget.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.budget.entity.BudgetExpense;
import com.iecmp.budget.vo.AdminBudgetExpensePageVO;
import com.iecmp.budget.vo.BudgetStatOverviewVO;
import com.iecmp.budget.vo.UserBudgetExpensePageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 支出记录表（{@code budget_expense}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/budget/BudgetExpenseMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_item_id (item_id)}</li>
 *   <li>{@code KEY idx_period_item (period_id, item_id)}</li>
 *   <li>{@code KEY idx_apply_id (apply_id)}</li>
 *   <li>{@code KEY idx_handler_id (handler_id)}</li>
 *   <li>{@code KEY idx_voucher_file_id (voucher_file_id)}</li>
 *   <li>{@code KEY idx_expense_date (expense_date)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface BudgetExpenseMapper extends BaseMapper<BudgetExpense> {

    /** 3-04 支出明细公示分页 */
    List<UserBudgetExpensePageVO> selectPublicPage(@Param("periodId") Long periodId,
                                                   @Param("itemId") Long itemId,
                                                   @Param("keyword") String keyword,
                                                   @Param("beginDate") String beginDate,
                                                   @Param("endDate") String endDate,
                                                   @Param("offset") Integer offset,
                                                   @Param("pageSize") Integer pageSize);

    /** 3-04 配套总数 */
    Long countPublicPage(@Param("periodId") Long periodId,
                         @Param("itemId") Long itemId,
                         @Param("keyword") String keyword,
                         @Param("beginDate") String beginDate,
                         @Param("endDate") String endDate);

    /** 3-23 支出分页（后台） */
    List<AdminBudgetExpensePageVO> selectAdminPage(@Param("periodId") Long periodId,
                                                   @Param("itemId") Long itemId,
                                                   @Param("handlerId") Long handlerId,
                                                   @Param("keyword") String keyword,
                                                   @Param("beginDate") String beginDate,
                                                   @Param("endDate") String endDate,
                                                   @Param("offset") Integer offset,
                                                   @Param("pageSize") Integer pageSize);

    /** 3-23 配套总数 */
    Long countAdminPage(@Param("periodId") Long periodId,
                        @Param("itemId") Long itemId,
                        @Param("handlerId") Long handlerId,
                        @Param("keyword") String keyword,
                        @Param("beginDate") String beginDate,
                        @Param("endDate") String endDate);

    /** 3-25 月度支出趋势 */
    List<BudgetStatOverviewVO.MonthTrendVO> selectMonthTrend(@Param("periodId") Long periodId);
}
