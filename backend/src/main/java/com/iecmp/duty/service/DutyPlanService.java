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
 * 排班计划业务接口（{@code duty_plan}），接口 7-08 ~ 7-13。
 */
public interface DutyPlanService extends IService<DutyPlan> {

    PageResult<DutyPlanPageVO> page(DutyPlanPageQuery query);

    DutyPlanCreateVO add(DutyPlanAddDTO dto);

    void update(DutyPlanUpdateDTO dto);

    void delete(Long id);

    DutyPlanGenerateVO generate(Long id, DutyPlanGenerateDTO dto);

    void publish(Long id, DutyPlanPublishDTO dto);
}