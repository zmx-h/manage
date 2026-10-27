package com.iecmp.duty.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.context.UserContext;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.duty.dto.DutySwapDTO;
import com.iecmp.duty.dto.DutySwapHandleDTO;
import com.iecmp.duty.entity.DutySchedule;
import com.iecmp.duty.entity.DutySwap;
import com.iecmp.duty.mapper.DutySwapMapper;
import com.iecmp.duty.query.DutySwapMyPageQuery;
import com.iecmp.duty.service.DutyScheduleService;
import com.iecmp.duty.service.DutySwapService;
import com.iecmp.duty.vo.DutySwapMyPageVO;
import com.iecmp.duty.vo.DutySwapVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 换班申请业务实现（{@code duty_swap}）。
 *
 * @author liuyujie
 */
@Service
@RequiredArgsConstructor
public class DutySwapServiceImpl extends ServiceImpl<DutySwapMapper, DutySwap> implements DutySwapService {

    private final DutyScheduleService dutyScheduleService;

    // ================= 7-05 发起换班申请 =================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DutySwapVO applySwap(DutySwapDTO dto) {

        Long currentUserId = UserContext.getUserIdOrDefault();

        // 1. 校验自己的值日安排
        DutySchedule mySchedule = dutyScheduleService.getById(dto.getScheduleId());
        if (mySchedule == null) {
            throw new BusinessException(70006, "值日安排不存在");
        }
        if (!mySchedule.getUserId().equals(currentUserId)) {
            throw new BusinessException(70012, "只能发起自己的值日换班");
        }
        if (mySchedule.getStatus() != 0) {
            throw new BusinessException(70008, "当前值日状态不允许换班");
        }

        // 2. 不能和自己换班
        if (currentUserId.equals(dto.getTargetUserId())) {
            throw new BusinessException(70010, "不能与自己换班");
        }

        // 3. 如果传了对调的目标，校验对方
        if (dto.getTargetScheduleId() != null) {
            DutySchedule targetSchedule = dutyScheduleService.getById(dto.getTargetScheduleId());
            if (targetSchedule == null || !targetSchedule.getUserId().equals(dto.getTargetUserId())) {
                throw new BusinessException(70011, "目标值日不存在或不属于对方");
            }
            if (targetSchedule.getStatus() != 0) {
                throw new BusinessException(70008, "对方的该条值日状态不允许对调");
            }
        }

        // 4. 防重复校验
        Long existCount = this.count(new LambdaQueryWrapper<DutySwap>()
                .eq(DutySwap::getScheduleId, dto.getScheduleId())
                .eq(DutySwap::getStatus, 0));
        if (existCount > 0) {
            throw new BusinessException(70012, "该值日已存在待处理的换班申请，请勿重复发起");
        }

        // 5. 组装数据并保存到数据库
        DutySwap swap = new DutySwap();
        swap.setScheduleId(dto.getScheduleId());
        swap.setApplyUserId(currentUserId);
        swap.setTargetUserId(dto.getTargetUserId());
        swap.setTargetScheduleId(dto.getTargetScheduleId());
        swap.setReason(dto.getReason());
        swap.setStatus(0);

        // 千万别忘了这一句！把数据存进数据库
        this.save(swap);

        // 6. 组装返回结果
        DutySwapVO vo = new DutySwapVO();
        vo.setSwapId(swap.getId());
        return vo;
    }

    // ================= 7-06 我的换班申请分页 =================
    @Override
    public PageResult<DutySwapMyPageVO> pageMySwap(DutySwapMyPageQuery query) {
        Long currentUserId = UserContext.getUserIdOrDefault();

        Page<DutySwap> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<DutySwap> wrapper = new LambdaQueryWrapper<>();

        // 视角控制：0 全部，1 我发起的，2 我收到的
        if (query.getRole() != null && query.getRole() == 1) {
            wrapper.eq(DutySwap::getApplyUserId, currentUserId);
        } else if (query.getRole() != null && query.getRole() == 2) {
            wrapper.eq(DutySwap::getTargetUserId, currentUserId);
        } else {
            wrapper.and(w -> w.eq(DutySwap::getApplyUserId, currentUserId)
                    .or().eq(DutySwap::getTargetUserId, currentUserId));
        }

        // 按状态筛选
        if (query.getStatus() != null) {
            wrapper.eq(DutySwap::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(DutySwap::getId);
        Page<DutySwap> result = this.page(page, wrapper);

        // 把 Entity 转成 VO 并返回
        return PageResult.of(result, entity -> {
            DutySwapMyPageVO vo = new DutySwapMyPageVO();
            vo.setSwapId(entity.getId());
            vo.setScheduleId(entity.getScheduleId());
            vo.setApplyUserId(entity.getApplyUserId());
            vo.setTargetUserId(entity.getTargetUserId());
            vo.setTargetScheduleId(entity.getTargetScheduleId());
            vo.setReason(entity.getReason());
            vo.setStatus(entity.getStatus());
            vo.setHandleTime(entity.getHandleTime() != null ? entity.getHandleTime().toString() : null);
            vo.setHandleRemark(entity.getHandleRemark());
            vo.setCreateTime(entity.getCreateTime() != null ? entity.getCreateTime().toString() : null);

            // isTarget: 判断当前用户是不是被换班的人
            vo.setIsTarget(currentUserId.equals(entity.getTargetUserId()) ? 1 : 0);
            return vo;
        });
    }

    // ================= 7-07 换班处理（同意/拒绝） =================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleSwap(Long id, DutySwapHandleDTO dto) {
        Long currentUserId = UserContext.getUserIdOrDefault();

        DutySwap swap = this.getById(id);
        if (swap == null) {
            throw new BusinessException(70009, "换班申请不存在");
        }

        // 1. 只能处理发给自己的申请
        if (!swap.getTargetUserId().equals(currentUserId)) {
            throw new BusinessException(70012, "仅换班对象可处理该申请");
        }

        // 2. 只能处理待确认的申请
        if (swap.getStatus() != 0) {
            throw new BusinessException(70013,"该申请已被处理");
        }

        // 3. 更新申请状态和处理时间
        swap.setStatus(dto.getHandleStatus());
        swap.setHandleTime(LocalDateTime.now());
        swap.setHandleRemark(dto.getHandleRemark());

        // 4. 核心步骤！如果同意，需要同步交换/修改 值日安排 的负责人
        if (dto.getHandleStatus() == 1) {
            DutySchedule applySchedule = dutyScheduleService.getById(swap.getScheduleId());

            if (swap.getTargetScheduleId() != null) {
                // 有对调目标：双方互换值日
                DutySchedule targetSchedule = dutyScheduleService.getById(swap.getTargetScheduleId());
                if (targetSchedule != null && applySchedule != null) {
                    Long tempUserId = applySchedule.getUserId();
                    applySchedule.setUserId(targetSchedule.getUserId());
                    targetSchedule.setUserId(tempUserId);

                    dutyScheduleService.updateById(applySchedule);
                    dutyScheduleService.updateById(targetSchedule);
                }
            } else {
                // 无对调目标：单向代班，把申请人这条值日的负责人改为对方
                if (applySchedule != null) {
                    applySchedule.setUserId(swap.getTargetUserId());
                    dutyScheduleService.updateById(applySchedule);
                }
            }
        }

        // 把修改后的申请存进数据库
        this.updateById(swap);
    }
}