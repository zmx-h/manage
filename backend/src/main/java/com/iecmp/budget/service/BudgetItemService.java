package com.iecmp.budget.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.budget.entity.BudgetItem;
import com.iecmp.budget.vo.BudgetPeriodItemListVO;

import java.util.List;

/**
 * 预算科目业务接口（{@code budget_item}）。
 *
 * @author IECMP 后端组
 */
public interface BudgetItemService extends IService<BudgetItem> {

    /** 3-03 预算科目明细 */
    List<BudgetPeriodItemListVO> itemList(Long periodId);
}
