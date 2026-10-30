package com.iecmp.commonm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.commonm.dto.MessageReadDTO;
import com.iecmp.commonm.entity.SysMessage;
import com.iecmp.commonm.enums.CommonErrorCode;
import com.iecmp.commonm.mapper.SysMessageMapper;
import com.iecmp.commonm.query.MessagePageQuery;
import com.iecmp.commonm.service.SysMessageService;
import com.iecmp.commonm.vo.MessagePageVO;
import com.iecmp.commonm.vo.MessageReadVO;
import com.iecmp.commonm.vo.MessageUnreadCountVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 站内消息业务实现。
 */
@Service
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage>
        implements SysMessageService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResult<MessagePageVO> pageMy(Long userId, MessagePageQuery query) {
        var wrapper = Wrappers.<SysMessage>lambdaQuery()
                .eq(SysMessage::getReceiverId, userId)
                .eq(query.getIsRead() != null, SysMessage::getIsRead, query.getIsRead())
                .eq(query.getMsgType() != null, SysMessage::getMsgType, query.getMsgType());
        LocalDateTime begin = parseDateTime(query.getBeginTime());
        LocalDateTime end = parseDateTime(query.getEndTime());
        if (begin != null) {
            wrapper.ge(SysMessage::getCreateTime, begin);
        }
        if (end != null) {
            wrapper.le(SysMessage::getCreateTime, end);
        }
        wrapper.orderByDesc(SysMessage::getCreateTime);
        IPage<SysMessage> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageResult.of(page, this::toPageVO);
    }

    @Override
    public MessageUnreadCountVO unreadCount(Long userId) {
        QueryWrapper<SysMessage> wrapper = new QueryWrapper<SysMessage>()
                .select("msg_type", "COUNT(*) AS cnt")
                .eq("receiver_id", userId)
                .eq("is_read", 0)
                .groupBy("msg_type");
        List<Map<String, Object>> rows = listMaps(wrapper);
        int duty = 0;
        int audit = 0;
        int system = 0;
        for (Map<String, Object> row : rows) {
            int count = row.get("cnt") instanceof Number number ? number.intValue() : 0;
            Integer type = row.get("msg_type") instanceof Number number ? number.intValue() : null;
            if (Integer.valueOf(1).equals(type)) {
                duty = count;
            } else if (Integer.valueOf(2).equals(type)) {
                audit = count;
            } else if (Integer.valueOf(3).equals(type)) {
                system = count;
            }
        }
        MessageUnreadCountVO vo = new MessageUnreadCountVO();
        vo.setTotal(duty + audit + system);
        vo.setDutyCount(duty);
        vo.setAuditCount(audit);
        vo.setSystemCount(system);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageReadVO markRead(Long userId, MessageReadDTO dto) {
        boolean all = Integer.valueOf(1).equals(dto.getAll());
        if (all) {
            return markAllRead(userId, dto.getMsgType());
        }
        List<Long> ids = dto.getMessageIds() == null ? List.of() : dto.getMessageIds().stream()
                .filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            throw error(CommonErrorCode.E80012);
        }
        if (ids.size() > 100) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "单次最多标记 100 条消息");
        }

        List<SysMessage> messages = listByIds(ids);
        if (messages.size() != ids.size()) {
            throw error(CommonErrorCode.E80012);
        }
        if (messages.stream().anyMatch(message -> !userId.equals(message.getReceiverId()))) {
            throw error(CommonErrorCode.E80013);
        }

        LocalDateTime now = LocalDateTime.now();
        int updated = (int) messages.stream()
                .filter(message -> Integer.valueOf(0).equals(message.getIsRead()))
                .count();
        if (updated > 0) {
            update(Wrappers.<SysMessage>lambdaUpdate()
                    .in(SysMessage::getId, ids)
                    .eq(SysMessage::getReceiverId, userId)
                    .eq(SysMessage::getIsRead, 0)
                    .set(SysMessage::getIsRead, 1)
                    .set(SysMessage::getReadTime, now));
        }
        MessageReadVO vo = new MessageReadVO();
        vo.setUpdatedCount(updated);
        return vo;
    }

    private MessageReadVO markAllRead(Long userId, Integer msgType) {
        var wrapper = Wrappers.<SysMessage>lambdaQuery()
                .eq(SysMessage::getReceiverId, userId)
                .eq(SysMessage::getIsRead, 0)
                .eq(msgType != null, SysMessage::getMsgType, msgType);
        long updated = count(wrapper);
        if (updated > 0) {
            var update = Wrappers.<SysMessage>lambdaUpdate()
                    .eq(SysMessage::getReceiverId, userId)
                    .eq(SysMessage::getIsRead, 0)
                    .eq(msgType != null, SysMessage::getMsgType, msgType)
                    .set(SysMessage::getIsRead, 1)
                    .set(SysMessage::getReadTime, LocalDateTime.now());
            update(update);
        }
        MessageReadVO vo = new MessageReadVO();
        vo.setUpdatedCount((int) updated);
        return vo;
    }

    private MessagePageVO toPageVO(SysMessage message) {
        MessagePageVO vo = new MessagePageVO();
        vo.setMessageId(message.getId());
        vo.setTitle(message.getTitle());
        vo.setContent(message.getContent());
        vo.setMsgType(message.getMsgType());
        vo.setBizType(message.getBizType());
        vo.setBizId(message.getBizId());
        vo.setIsRead(message.getIsRead());
        vo.setCreateTime(message.getCreateTime() == null
                ? null : message.getCreateTime().format(DATE_TIME_FORMATTER));
        return vo;
    }

    private LocalDateTime parseDateTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return value.contains("T") ? LocalDateTime.parse(value) : LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "时间格式应为 yyyy-MM-dd HH:mm:ss");
        }
    }

    private BusinessException error(CommonErrorCode code) {
        return new BusinessException(code.getCode(), code.getMsg());
    }
}