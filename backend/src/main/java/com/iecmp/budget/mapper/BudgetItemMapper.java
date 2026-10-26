package com.iecmp.budget.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.budget.entity.BudgetItem;
import com.iecmp.budget.vo.BudgetPeriodItemListVO;
import com.iecmp.budget.vo.BudgetStatOverviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 预算科目表（{@code budget_item}）数据访问接口。
 *
 * <p>通用 CRUD 由 {@code BaseMapper} 提供；复杂查询（多表关联、统计聚合）
 * 建议写在 {@code resources/mapper/budget/BudgetItemMapper.xml} 中，方法在此声明。
 *
 * <p>本表索引：
 * <ul>
 *   <li>{@code PRIMARY KEY (id)}</li>
 *   <li>{@code KEY idx_period_category (period_id, category)}</li>
 *   <li>{@code KEY idx_period_sort (period_id, sort_no)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface BudgetItemMapper extends BaseMapper<BudgetItem> {

    /** 3-03 预算科目明细 */
    List<BudgetPeriodItemListVO> selectByPeriodId(@Param("periodId") Long periodId);

    /** 3-05 提交申请时锁住科目，查剩余额度 */
    BudgetItem selectForUpdate(@Param("id") Long id);

    /** 3-25 科目维度统计 */
    List<BudgetStatOverviewVO.ItemStatVO> selectItemStats(@Param("periodId") Long periodId);
}
