package com.iecmp.budget.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.budget.entity.BudgetPeriod;
import com.iecmp.budget.vo.BudgetPeriodDetailVO;
import com.iecmp.budget.vo.UserBudgetPeriodPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 预算周期表（{@code budget_period}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/budget/BudgetPeriodMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_year (year)}</li>
 *   <li>{@code KEY idx_status_public (status, is_public)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface BudgetPeriodMapper extends BaseMapper<BudgetPeriod> {

    /** 3-01 公示预算周期分页 */
    List<UserBudgetPeriodPageVO> selectPublicPage(@Param("year") String year,
                                                   @Param("status") Integer status,
                                                   @Param("offset") Integer offset,
                                                   @Param("pageSize") Integer pageSize);

    /** 3-01 配套总数 */
    Long countPublicPage(@Param("year") String year,
                         @Param("status") Integer status);

    /** 3-02 预算周期详情 */
    BudgetPeriodDetailVO selectDetailById(@Param("id") Long id);
}
