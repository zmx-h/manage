package com.iecmp.commonm.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.commonm.entity.SysOperLog;
import com.iecmp.commonm.mapper.SysOperLogMapper;
import com.iecmp.commonm.query.LogPageQuery;
import com.iecmp.commonm.service.SysOperLogService;
import com.iecmp.commonm.vo.LogPageVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** 操作日志业务实现（{@code sys_oper_log}）。 */
@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog>
        implements SysOperLogService {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResult<LogPageVO> pageQuery(LogPageQuery query) {
        var wrapper = Wrappers.<SysOperLog>lambdaQuery()
                .eq(StringUtils.hasText(query.getModule()), SysOperLog::getModule,
                        query.getModule() == null ? null : query.getModule().trim())
                .eq(StringUtils.hasText(query.getOperation()), SysOperLog::getOperation,
                        query.getOperation() == null ? null : query.getOperation().trim())
                .like(StringUtils.hasText(query.getOperatorName()), SysOperLog::getOperatorName,
                        query.getOperatorName() == null ? null : query.getOperatorName().trim())
                .eq(query.getStatus() != null, SysOperLog::getStatus, query.getStatus())
                .ge(StringUtils.hasText(query.getBeginTime()), SysOperLog::getCreateTime,
                        parseDateTime(query.getBeginTime()))
                .le(StringUtils.hasText(query.getEndTime()), SysOperLog::getCreateTime,
                        parseDateTime(query.getEndTime()))
                .orderByDesc(SysOperLog::getCreateTime)
                .orderByDesc(SysOperLog::getId);
        IPage<SysOperLog> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageResult.of(page, this::toVO);
    }

    private LogPageVO toVO(SysOperLog log) {
        LogPageVO vo = new LogPageVO();
        vo.setLogId(log.getId());
        vo.setModule(log.getModule());
        vo.setOperation(log.getOperation());
        vo.setRequestUri(log.getRequestUri());
        vo.setRequestParam(log.getRequestParam());
        vo.setResponseMsg(log.getResponseMsg());
        vo.setOperatorId(log.getOperatorId());
        vo.setOperatorName(log.getOperatorName());
        vo.setIp(log.getIp());
        vo.setCostMs(log.getCostMs());
        vo.setStatus(log.getStatus());
        vo.setCreateTime(log.getCreateTime() == null ? null : log.getCreateTime().format(DATE_TIME_FMT));
        return vo;
    }

    private LocalDateTime parseDateTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return value.contains("T") ? LocalDateTime.parse(value) : LocalDateTime.parse(value.trim(), DATE_TIME_FMT);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "时间格式应为 yyyy-MM-dd HH:mm:ss");
        }
    }
}