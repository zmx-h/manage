package com.iecmp.budget.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iecmp.budget.entity.BudgetApply;
import org.apache.ibatis.annotations.Mapper;

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
 *   <li>{@code KEY idx_attach_file_id (attach_file_id)}</li>
 *   <li>{@code KEY idx_status_create (status, create_time)}</li>
 *   <li>{@code KEY idx_create_time (create_time)}</li>
 * </ul>
 *
 * @author IECMP 后端组
 */
@Mapper
public interface BudgetApplyMapper extends BaseMapper<BudgetApply> {
}
