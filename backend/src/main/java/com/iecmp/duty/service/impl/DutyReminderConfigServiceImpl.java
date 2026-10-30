package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.ResultCode;
import com.iecmp.duty.dto.DutyReminderUpdateDTO;
import com.iecmp.duty.entity.DutyPlan;
import com.iecmp.duty.entity.DutyReminderConfig;
import com.iecmp.duty.enums.DutyErrorCode;
import com.iecmp.duty.mapper.DutyReminderConfigMapper;
import com.iecmp.duty.query.DutyReminderQuery;
import com.iecmp.duty.service.DutyPlanService;
import com.iecmp.duty.service.DutyReminderConfigService;
import com.iecmp.duty.vo.DutyReminderUpdateVO;
import com.iecmp.duty.vo.DutyReminderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 提醒配置业务实现。
 */
@Service
@RequiredArgsConstructor
public class DutyReminderConfigServiceImpl extends ServiceImpl<DutyReminderConfigMapper, DutyReminderConfig>
        implements DutyReminderConfigService {

    private static final int DEFAULT_AHEAD_MINUTES = 30;
    private static final int DEFAULT_CHANNEL = 1;
    private static final int DEFAULT_ENABLED = 1;

    private final DutyPlanService dutyPlanService;

    @Override
    public DutyReminderVO get(DutyReminderQuery query) {
        Long planId = query == null ? null : query.getPlanId();
        if (planId != null) {
            requirePlan(planId);
            DutyReminderConfig specific = find(planId);
            if (specific != null) {
                return toVO(specific, 0);
            }
        }
        DutyReminderConfig global = find(null);
        if (global != null) {
            return toVO(global, 1);
        }
        DutyReminderVO vo = new DutyReminderVO();
        vo.setConfigId(null);
        vo.setPlanId(null);
        vo.setAheadMinutes(DEFAULT_AHEAD_MINUTES);
        vo.setChannel(DEFAULT_CHANNEL);
        vo.setEnabled(DEFAULT_ENABLED);
        vo.setIsGlobal(1);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DutyReminderUpdateVO save(DutyReminderUpdateDTO dto) {
        if (dto.getPlanId() != null) {
            requirePlan(dto.getPlanId());
        }
        int aheadMinutes = dto.getAheadMinutes() == null ? DEFAULT_AHEAD_MINUTES : dto.getAheadMinutes();
        int channel = dto.getChannel() == null ? DEFAULT_CHANNEL : dto.getChannel();
        int enabled = dto.getEnabled() == null ? DEFAULT_ENABLED : dto.getEnabled();
        if (aheadMinutes < 5 || aheadMinutes > 1440) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "提前提醒分钟数必须在 5~1440 之间");
        }
        if (channel != 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "v1 仅支持站内消息提醒");
        }
        if (enabled != 0 && enabled != 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "启用状态必须为 0 或 1");
        }

        DutyReminderConfig config = find(dto.getPlanId());
        if (config == null) {
            config = new DutyReminderConfig();
            config.setPlanId(dto.getPlanId());
        }
        config.setAheadMinutes(aheadMinutes);
        config.setChannel(channel);
        config.setEnabled(enabled);
        if (config.getId() == null) {
            if (!save(config)) {
                throw new BusinessException(ResultCode.SYSTEM_ERROR, "保存提醒配置失败");
            }
        } else if (!updateById(config)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "保存提醒配置失败");
        }

        DutyReminderUpdateVO vo = new DutyReminderUpdateVO();
        vo.setConfigId(config.getId());
        return vo;
    }

    private DutyReminderConfig find(Long planId) {
        var wrapper = Wrappers.<DutyReminderConfig>lambdaQuery();
        if (planId == null) {
            wrapper.isNull(DutyReminderConfig::getPlanId);
        } else {
            wrapper.eq(DutyReminderConfig::getPlanId, planId);
        }
        return getOne(wrapper.orderByAsc(DutyReminderConfig::getId).last("LIMIT 1"));
    }

    private void requirePlan(Long planId) {
        DutyPlan plan = dutyPlanService.getById(planId);
        if (plan == null) {
            throw new BusinessException(DutyErrorCode.E70001.getCode(), DutyErrorCode.E70001.getMsg());
        }
    }

    private DutyReminderVO toVO(DutyReminderConfig config, int isGlobal) {
        DutyReminderVO vo = new DutyReminderVO();
        vo.setConfigId(config.getId());
        vo.setPlanId(config.getPlanId());
        vo.setAheadMinutes(config.getAheadMinutes() == null ? DEFAULT_AHEAD_MINUTES : config.getAheadMinutes());
        vo.setChannel(config.getChannel() == null ? DEFAULT_CHANNEL : config.getChannel());
        vo.setEnabled(config.getEnabled() == null ? DEFAULT_ENABLED : config.getEnabled());
        vo.setIsGlobal(isGlobal);
        return vo;
    }
}