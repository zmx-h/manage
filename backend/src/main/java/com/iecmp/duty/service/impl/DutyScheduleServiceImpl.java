package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.duty.dto.DutyScheduleAddDTO;
import com.iecmp.duty.dto.DutyScheduleUpdateDTO;
import com.iecmp.duty.entity.DutyPlan;
import com.iecmp.duty.entity.DutySchedule;
import com.iecmp.duty.mapper.DutyScheduleMapper;
import com.iecmp.duty.query.AdminDutySchedulePageQuery;
import com.iecmp.duty.service.DutyPlanService;
import com.iecmp.duty.service.DutyScheduleService;
import com.iecmp.duty.vo.AdminDutySchedulePageVO;
import com.iecmp.duty.vo.DutyScheduleCreateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 值日安排业务实现（{@code duty_schedule}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link DutyScheduleService} 中声明后在此实现。
 *
 * @author liuyujie
 * @date 2026-10-22
 */
@Service
@RequiredArgsConstructor
public class DutyScheduleServiceImpl extends ServiceImpl<DutyScheduleMapper, DutySchedule>
        implements DutyScheduleService {
    private final DutyPlanService dutyPlanService;
    @Override
    public DutyScheduleCreateVO add(DutyScheduleAddDTO dto) {
        // 1. 防重复校验
        Long count = this.count(new LambdaQueryWrapper<DutySchedule>()
                .eq(DutySchedule::getUserId, dto.getUserId())
                .eq(DutySchedule::getDutyDate, dto.getDutyDate())
        );
        if (count > 0) {
            throw new BusinessException(70014, "该成员当日已有值日安排");
        }

        // 2. 补全任务内容
        String finalTaskContent = dto.getTaskContent();
        if (finalTaskContent == null || finalTaskContent.isEmpty()) {
            DutyPlan plan = dutyPlanService.getById(dto.getPlanId());
            if (plan != null) {
                finalTaskContent = plan.getTaskContent();
            }
        }


        // 3. 组装数据并保存到数据库
        DutySchedule schedule = new DutySchedule();
        schedule.setPlanId(dto.getPlanId());
        schedule.setUserId(dto.getUserId());

        // 把前端的 String 日期，转换成 LocalDate
        LocalDate realDate = LocalDate.parse(dto.getDutyDate());

        // 把转换好的 realDate 塞进去
        schedule.setDutyDate(realDate);
        schedule.setTaskContent(finalTaskContent);
        schedule.setStatus(0);

        // 千万别忘了这一句！把数据存进数据库
        this.save(schedule);

        // 4. 组装返回结果
        DutyScheduleCreateVO vo = new DutyScheduleCreateVO();
        vo.setScheduleId(schedule.getId());
        return vo;
    }

    @Override
    public void update(DutyScheduleUpdateDTO dto) {
        // 1. 查旧数据（必须有这一步，才能判断存不存在）
        DutySchedule schedule = this.getById(dto.getScheduleId());
        if (schedule == null) {
            throw new BusinessException(70006, "值日安排不存在");
        }

        // 2. 把前端传的新数据塞进去
        schedule.setUserId(dto.getUserId());
        schedule.setTaskContent(dto.getTaskContent());
        // 日期是 String，记得转换
        if (dto.getDutyDate() != null) {
            schedule.setDutyDate(LocalDate.parse(dto.getDutyDate()));
        }
        // 如果 DTO 里还有 status，也塞进去（没有就不管）

        // 3. 核心步骤！把修改后的数据存进数据库！
        this.updateById(schedule);
    }

    @Override
    public void delete(Long id) {
        // 1. 查旧数据（必须有这一步，才能判断存不存在）
        DutySchedule schedule = this.getById(id);
        if (schedule == null) {
            throw new BusinessException(70006, "值日安排不存在");
        }
        this.removeById(id);

    }

    @Override
    public PageResult<AdminDutySchedulePageVO> page(AdminDutySchedulePageQuery query) {
        // 1. 创建分页对象（提示：new Page<>(query.getPageNum(), query.getPageSize())）
        Page<DutySchedule> page = new Page<>(query.getPageNum(), query.getPageSize());

        // 2. 构造查询条件（LambdaQueryWrapper）
        LambdaQueryWrapper<DutySchedule> wrapper = new LambdaQueryWrapper<>();

        // 3. 拼装条件（用 if 判空，如果前端传了，才去查）
        // 比如：按状态查 wrapper.eq(query.getStatus() != null, DutySchedule::getStatus, query.getStatus());
        // 比如：按日期范围查 wrapper.ge(...) 和 wrapper.le(...)
        // 比如：按人名查（人名在前端怎么传的？如果是 userId，就直接 eq；如果是 keyword 模糊查人名，这里有点复杂，需要先查 sys_user 表拿 ID）

        // 4. 执行分页查询
        Page<DutySchedule> result = this.page(page, wrapper);

        // 5. 把 Entity 转成 VO 并返回
        return PageResult.of(result, entity -> {
            AdminDutySchedulePageVO vo = new AdminDutySchedulePageVO();
            vo.setScheduleId(entity.getId());
            vo.setPlanId(entity.getPlanId());
            vo.setDutyDate(entity.getDutyDate() != null ? entity.getDutyDate().toString() : null); // 日期转字符串
            vo.setTaskContent(entity.getTaskContent());
            vo.setStatus(entity.getStatus());
            // ... 其他的如果你看着不报错也可以 set
            return vo;
        });
    }
}
