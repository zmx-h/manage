package com.iecmp.budget.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.budget.entity.BudgetItem;
import com.iecmp.budget.mapper.BudgetItemMapper;
import com.iecmp.budget.service.BudgetItemService;
import org.springframework.stereotype.Service;

/**
 * 预算科目业务实现（{@code budget_item}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link BudgetItemService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class BudgetItemServiceImpl extends ServiceImpl<BudgetItemMapper, BudgetItem>
        implements BudgetItemService {
}
