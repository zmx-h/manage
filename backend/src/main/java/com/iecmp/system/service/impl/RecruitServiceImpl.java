package com.iecmp.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.properties.IecmpProperties;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.common.util.RecruitCodeUtil;
import com.iecmp.system.dto.RecruitApplyDTO;
import com.iecmp.system.dto.RecruitAssessmentDTO;
import com.iecmp.system.dto.RecruitRegisterDTO;
import com.iecmp.system.entity.SysRecruitApply;
import com.iecmp.system.enums.RecruitApplyStatusEnum;
import com.iecmp.system.enums.SystemErrorCode;
import com.iecmp.system.query.RecruitApplyPageQuery;
import com.iecmp.system.service.RecruitMailService;
import com.iecmp.system.service.RecruitService;
import com.iecmp.system.service.RecruitTxService;
import com.iecmp.system.service.SysRecruitApplyService;
import com.iecmp.system.vo.RecruitApplyPageVO;
import com.iecmp.system.vo.RecruitApplySubmitVO;
import com.iecmp.system.vo.RecruitApplyVO;
import com.iecmp.system.vo.RecruitAssessmentVO;
import com.iecmp.system.vo.RecruitCodeVO;
import com.iecmp.system.vo.RegisterVO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** 招新报名、考核与邮箱注册码业务实现。 */
@Service
@RequiredArgsConstructor
public class RecruitServiceImpl implements RecruitService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SysRecruitApplyService sysRecruitApplyService;
    private final RecruitTxService recruitTxService;
    private final RecruitMailService recruitMailService;
    private final IecmpProperties iecmpProperties;

    @Override
    public RecruitApplySubmitVO apply(RecruitApplyDTO dto, String ip) {
        requireRecruitEnabled();
        Long applyId = recruitTxService.apply(dto, ip);
        RecruitApplySubmitVO vo = new RecruitApplySubmitVO();
        vo.setApplyId(applyId);
        vo.setStatus(RecruitApplyStatusEnum.PENDING_ASSESS.getValue());
        return vo;
    }

    @Override
    public RegisterVO register(RecruitRegisterDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();
        String code = dto.getCode().trim().toUpperCase();
        SysRecruitApply apply = sysRecruitApplyService.getOne(Wrappers.<SysRecruitApply>lambdaQuery()
                .eq(SysRecruitApply::getEmail, email)
                .orderByDesc(SysRecruitApply::getId)
                .last("LIMIT 1"), false);
        if (apply == null) {
            throw error(SystemErrorCode.E10039);
        }
        if (RecruitApplyStatusEnum.REGISTERED.getValue().equals(apply.getStatus())) {
            throw error(SystemErrorCode.E10043);
        }
        if (RecruitApplyStatusEnum.PENDING_ASSESS.getValue().equals(apply.getStatus())
                || RecruitApplyStatusEnum.PASSED_PENDING_SEND.getValue().equals(apply.getStatus())
                || RecruitApplyStatusEnum.FAILED.getValue().equals(apply.getStatus())) {
            throw error(SystemErrorCode.E10038);
        }
        if (RecruitApplyStatusEnum.CODE_EXPIRED.getValue().equals(apply.getStatus())
                || apply.getCodeExpireTime() == null
                || !apply.getCodeExpireTime().isAfter(LocalDateTime.now())) {
            recruitTxService.markExpired(apply.getId());
            throw error(SystemErrorCode.E10039);
        }
        if (apply.getCodeAttempts() != null
                && apply.getCodeAttempts() >= iecmpProperties.getRecruit().getCodeMaxAttempts()) {
            throw error(SystemErrorCode.E10040);
        }
        String pepper = iecmpProperties.getRecruit().getCodePepper();
        if (!RecruitCodeUtil.matches(code, apply.getCodeHash(), pepper)) {
            boolean reachedLimit = recruitTxService.increaseAttempts(apply.getId());
            throw error(reachedLimit ? SystemErrorCode.E10040 : SystemErrorCode.E10039);
        }
        String username = dto.getUsername().trim();
        if (username.length() < 4 || username.length() > 32) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "登录账号长度需为 4~32 位");
        }
        return recruitTxService.register(apply.getId(), RecruitCodeUtil.hash(code, pepper),
                username, dto.getPassword());
    }

    @Override
    public PageResult<RecruitApplyPageVO> page(RecruitApplyPageQuery query) {
        var wrapper = Wrappers.<SysRecruitApply>lambdaQuery()
                .eq(query.getStatus() != null, SysRecruitApply::getStatus, query.getStatus());
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(w -> w.like(SysRecruitApply::getRealName, keyword)
                    .or().like(SysRecruitApply::getEmail, keyword)
                    .or().like(SysRecruitApply::getStudentNo, keyword)
                    .or().like(SysRecruitApply::getClassName, keyword));
        }
        LocalDateTime begin = parseDateTime(query.getBeginTime());
        LocalDateTime end = parseDateTime(query.getEndTime());
        if (begin != null) {
            wrapper.ge(SysRecruitApply::getApplyTime, begin);
        }
        if (end != null) {
            wrapper.le(SysRecruitApply::getApplyTime, end);
        }
        wrapper.orderByDesc(SysRecruitApply::getApplyTime).orderByDesc(SysRecruitApply::getId);
        IPage<SysRecruitApply> page = sysRecruitApplyService.page(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageResult.of(page, this::toPageVO);
    }

    @Override
    public RecruitApplyVO detail(Long id) {
        SysRecruitApply apply = requireApply(id);
        RecruitApplyVO vo = new RecruitApplyVO();
        vo.setApplyId(apply.getId());
        vo.setStudentNo(apply.getStudentNo());
        vo.setRealName(apply.getRealName());
        vo.setGrade(apply.getGrade());
        vo.setClassName(apply.getClassName());
        vo.setEmail(apply.getEmail());
        vo.setPhone(apply.getPhone());
        vo.setIntro(apply.getIntro());
        vo.setStatus(apply.getStatus());
        vo.setScore(apply.getScore());
        vo.setAssessmentRemark(apply.getAssessmentRemark());
        vo.setAssessmentUserId(apply.getAssessmentUserId());
        vo.setAssessmentTime(format(apply.getAssessmentTime()));
        vo.setCodeCreateTime(format(apply.getCodeCreateTime()));
        vo.setCodeSentTime(format(apply.getCodeSentTime()));
        vo.setCodeExpireTime(format(apply.getCodeExpireTime()));
        vo.setCodeSendCount(apply.getCodeSendCount());
        vo.setCodeAttempts(apply.getCodeAttempts());
        vo.setCodeExpired(isCodeExpired(apply));
        vo.setCreateUserId(apply.getCreateUserId());
        vo.setIp(apply.getIp());
        vo.setApplyTime(format(apply.getApplyTime()));
        return vo;
    }

    @Override
    public RecruitAssessmentVO assess(Long id, RecruitAssessmentDTO dto) {
        int passScore = dto.getPassScore() == null
                ? iecmpProperties.getRecruit().getPassScore() : dto.getPassScore();
        RecruitTxService.AssessmentResult result = recruitTxService.prepareAssessment(
                id, dto.getScore(), passScore, trimToNull(dto.getAssessmentRemark()),
                LoginUserUtil.getUserId());

        RecruitAssessmentVO vo = new RecruitAssessmentVO();
        vo.setApplyId(result.applyId());
        vo.setScore(dto.getScore());
        vo.setPassScore(passScore);
        vo.setPassed(result.passed());
        vo.setCodeExpireTime(format(result.expireTime()));
        if (!result.passed()) {
            vo.setStatus(result.status());
            vo.setMailSent(false);
            return vo;
        }

        SysRecruitApply apply = requireApply(id);
        RecruitMailService.MailResult mail = recruitMailService.sendCode(
                apply.getEmail(), apply.getRealName(), result.code(), result.expireTime());
        recruitTxService.recordSendResult(id, result.codeHash(), mail.sent());
        vo.setStatus(mail.sent()
                ? RecruitApplyStatusEnum.CODE_SENT.getValue()
                : RecruitApplyStatusEnum.PASSED_PENDING_SEND.getValue());
        vo.setMailSent(mail.sent());
        vo.setMailError(mail.error());
        return vo;
    }

    @Override
    public RecruitCodeVO resendCode(Long id) {
        RecruitTxService.ResendResult result = recruitTxService.prepareResend(id);
        RecruitMailService.MailResult mail = recruitMailService.sendCode(
                result.email(), result.realName(), result.code(), result.expireTime());
        recruitTxService.recordSendResult(result.applyId(), result.codeHash(), mail.sent());

        RecruitCodeVO vo = new RecruitCodeVO();
        vo.setApplyId(result.applyId());
        vo.setStatus(mail.sent()
                ? RecruitApplyStatusEnum.CODE_SENT.getValue()
                : RecruitApplyStatusEnum.PASSED_PENDING_SEND.getValue());
        vo.setMailSent(mail.sent());
        vo.setMailError(mail.error());
        vo.setCodeExpireTime(format(result.expireTime()));
        if (!mail.sent()) {
            vo.setCode(result.code());
        }
        return vo;
    }

    private void requireRecruitEnabled() {
        if (!Integer.valueOf(1).equals(iecmpProperties.getRecruit().getEnabled())) {
            throw error(SystemErrorCode.E10035);
        }
    }

    private SysRecruitApply requireApply(Long id) {
        SysRecruitApply apply = id == null ? null : sysRecruitApplyService.getById(id);
        if (apply == null) {
            throw error(SystemErrorCode.E10037);
        }
        return apply;
    }

    private RecruitApplyPageVO toPageVO(SysRecruitApply apply) {
        RecruitApplyPageVO vo = new RecruitApplyPageVO();
        vo.setApplyId(apply.getId());
        vo.setStudentNo(apply.getStudentNo());
        vo.setRealName(apply.getRealName());
        vo.setGrade(apply.getGrade());
        vo.setClassName(apply.getClassName());
        vo.setEmail(apply.getEmail());
        vo.setPhone(apply.getPhone());
        vo.setIntro(apply.getIntro());
        vo.setStatus(apply.getStatus());
        vo.setScore(apply.getScore());
        vo.setAssessmentRemark(apply.getAssessmentRemark());
        vo.setAssessmentTime(format(apply.getAssessmentTime()));
        vo.setCodeCreateTime(format(apply.getCodeCreateTime()));
        vo.setCodeSentTime(format(apply.getCodeSentTime()));
        vo.setCodeExpireTime(format(apply.getCodeExpireTime()));
        vo.setCodeSendCount(apply.getCodeSendCount());
        vo.setCodeExpired(isCodeExpired(apply));
        vo.setCreateUserId(apply.getCreateUserId());
        vo.setIp(apply.getIp());
        vo.setApplyTime(format(apply.getApplyTime()));
        return vo;
    }

    private Boolean isCodeExpired(SysRecruitApply apply) {
        return apply.getCodeExpireTime() != null
                && !apply.getCodeExpireTime().isAfter(LocalDateTime.now());
    }

    private String format(LocalDateTime time) {
        return time == null ? null : time.format(DATE_TIME_FORMATTER);
    }

    private LocalDateTime parseDateTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return value.contains("T") ? LocalDateTime.parse(value)
                    : LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "时间格式应为 yyyy-MM-dd HH:mm:ss");
        }
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private BusinessException error(SystemErrorCode code) {
        return new BusinessException(code.getCode(), code.getMsg());
    }
}
