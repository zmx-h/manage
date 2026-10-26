package com.iecmp.duty.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.common.result.PageResult;
import com.iecmp.duty.dto.DutyPlanAddDTO;
import com.iecmp.duty.dto.DutyPlanGenerateDTO;
import com.iecmp.duty.dto.DutyPlanPublishDTO;
import com.iecmp.duty.dto.DutyPlanUpdateDTO;
import com.iecmp.duty.entity.DutyPlan;
import com.iecmp.duty.query.DutyPlanPageQuery;
import com.iecmp.duty.vo.DutyPlanCreateVO;
import com.iecmp.duty.vo.DutyPlanGenerateVO;
import com.iecmp.duty.vo.DutyPlanPageVO;

/**
 * 排班计划业务接口（{@code duty_plan}）。
 *
 * <p>骨架阶段只继承 {@code IService} 获得通用 CRUD 能力；
 * 实现具体接口时，把该表的专有业务方法声明在这里，并在 Impl 中实现。
 *
 * @author liuyujie
 * @date 2026-10-26
 */
public interface DutyPlanService extends IService<DutyPlan> {
    PageResult<DutyPlanPageVO> page(DutyPlanPageQuery query);
    DutyPlanCreateVO add(DutyPlanAddDTO dto);
    void putDutyPlan(DutyPlanUpdateDTO dto);
    void deleteDutyPlan(Long id);
    DutyPlanGenerateVO postDutyPlanGenerate( Long id, DutyPlanGenerateDTO dto);
    void putDutyPlanPublish(Long id, DutyPlanPublishDTO dto);
}
