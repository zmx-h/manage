package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.duty.dto.DutyReminderUpdateDTO;
import com.iecmp.duty.entity.DutyReminderConfig;
import com.iecmp.duty.mapper.DutyReminderConfigMapper;
import com.iecmp.duty.query.DutyReminderQuery;
import com.iecmp.duty.service.DutyReminderConfigService;
import com.iecmp.duty.vo.DutyReminderVO;
import org.springframework.stereotype.Service;

/**
 * 提醒配置业务实现（{@code duty_reminder_config}）。
 *
 * <p>骨架阶段为空实现，只提供通用 CRUD；业务方法在 {@link DutyReminderConfigService} 中声明后在此实现。
 *
 * @author IECMP 后端组
 */
@Service
public class DutyReminderConfigServiceImpl extends ServiceImpl<DutyReminderConfigMapper, DutyReminderConfig>
        implements DutyReminderConfigService {
    @Override
    public DutyReminderVO getDutyReminder(DutyReminderQuery query) {
        // 从 query 中取出 planId
        Long planId = query.getPlanId();

        // 1. 去数据库查
        LambdaQueryWrapper<DutyReminderConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DutyReminderConfig::getPlanId, planId);
        DutyReminderConfig config = this.getOne(wrapper);

        // 2. 如果查不到，返回默认值
        if (config == null) {
            DutyReminderVO defaultVo = new DutyReminderVO();
            defaultVo.setPlanId(planId);
            defaultVo.setAheadMinutes(30); // 默认提前30分钟
            defaultVo.setEnabled(1);       // 默认开启
            return defaultVo;
        }

        // 3. 查到了，转 VO 返回
        DutyReminderVO vo = new DutyReminderVO();
        vo.setConfigId(config.getId()); // 假设你的实体类主键是 id
        vo.setPlanId(config.getPlanId());
        vo.setAheadMinutes(config.getAheadMinutes());
        vo.setEnabled(config.getEnabled());
        return vo;
    }

    @Override
    public DutyReminderVO putDutyReminder(DutyReminderUpdateDTO dto) {
        // 1. 先拿着前端传的 planId 去数据库查一下，看看有没有配过
        LambdaQueryWrapper<DutyReminderConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DutyReminderConfig::getPlanId, dto.getPlanId());
        DutyReminderConfig config = this.getOne(wrapper);

        // 2. 如果查到了，说明是“更新”（Update）
        if (config != null) {
            config.setAheadMinutes(dto.getAheadMinutes());
            config.setEnabled(dto.getEnabled());
            this.updateById(config); // 更新到数据库

            // 把实体转成 VO 返回给前端
            DutyReminderVO vo = new DutyReminderVO();
            vo.setConfigId(config.getId());
            vo.setPlanId(config.getPlanId());
            vo.setAheadMinutes(config.getAheadMinutes());
            vo.setEnabled(config.getEnabled());
            return vo;
        }

        // 3. 如果没查到，说明是“新增”（Insert）
        DutyReminderConfig newConfig = new DutyReminderConfig();
        newConfig.setPlanId(dto.getPlanId());
        newConfig.setAheadMinutes(dto.getAheadMinutes());
        newConfig.setEnabled(dto.getEnabled());
        this.save(newConfig); // 插入到数据库

        // 把实体转成 VO 返回给前端
        DutyReminderVO vo = new DutyReminderVO();
        vo.setConfigId(newConfig.getId()); // save 后会自动回填 ID
        vo.setPlanId(newConfig.getPlanId());
        vo.setAheadMinutes(newConfig.getAheadMinutes());
        vo.setEnabled(newConfig.getEnabled());
        return vo;
    }


}
