package com.iecmp.system.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.properties.IecmpProperties;
import com.iecmp.common.util.PasswordUtil;
import com.iecmp.common.util.RecruitCodeUtil;
import com.iecmp.system.dto.RecruitApplyDTO;
import com.iecmp.system.entity.SysRecruitApply;
import com.iecmp.system.entity.SysRole;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.entity.SysUserRole;
import com.iecmp.system.enums.RecruitApplyStatusEnum;
import com.iecmp.system.enums.SystemErrorCode;
import com.iecmp.system.vo.RegisterVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 招新流程中需要事务保证的写操作。
 *
 * <p>邮件发送在事务外执行，避免 SMTP 抖动导致数据库回滚；发信结果再由本类写回状态。
 */
@Service
@RequiredArgsConstructor
public class RecruitTxService {

    private static final String MEMBER_ROLE = "MEMBER";

    private final SysRecruitApplyService sysRecruitApplyService;
    private final SysUserService sysUserService;
    private final SysUserRoleService sysUserRoleService;
    private final SysRoleService sysRoleService;
    private final IecmpProperties iecmpProperties;

    /** 1-30 保存报名记录。 */
    @Transactional(rollbackFor = Exception.class)
    public Long apply(RecruitApplyDTO dto, String ip) {
        String email = dto.getEmail().trim().toLowerCase();
        String studentNo = trimToNull(dto.getStudentNo());
        LocalDateTime now = LocalDateTime.now();

        if (StringUtils.hasText(ip)) {
            Long recent = sysRecruitApplyService.count(Wrappers.<SysRecruitApply>lambdaQuery()
                    .eq(SysRecruitApply::getIp, ip.trim())
                    .ge(SysRecruitApply::getApplyTime, now.minusHours(1)));
            if (recent != null && recent >= iecmpProperties.getRecruit().getApplyLimitPerHour()) {
                throw error(SystemErrorCode.E10030);
            }
        }
        if (sysRecruitApplyService.count(Wrappers.<SysRecruitApply>lambdaQuery()
                .eq(SysRecruitApply::getEmail, email)) > 0
                || (studentNo != null && sysRecruitApplyService.count(
                Wrappers.<SysRecruitApply>lambdaQuery().eq(SysRecruitApply::getStudentNo, studentNo)) > 0)) {
            throw error(SystemErrorCode.E10036);
        }
        if (sysUserService.count(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getEmail, email)) > 0) {
            throw error(SystemErrorCode.E10043);
        }
        if (studentNo != null && sysUserService.count(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getStudentNo, studentNo)) > 0) {
            throw error(SystemErrorCode.E10014);
        }

        SysRecruitApply apply = new SysRecruitApply();
        apply.setRealName(dto.getRealName().trim());
        apply.setGrade(dto.getGrade().trim());
        apply.setClassName(dto.getClassName().trim());
        apply.setEmail(email);
        apply.setStudentNo(studentNo);
        apply.setPhone(trimToNull(dto.getPhone()));
        apply.setIntro(trimToNull(dto.getIntro()));
        apply.setStatus(RecruitApplyStatusEnum.PENDING_ASSESS.getValue());
        apply.setApplyTime(now);
        apply.setIp(ip);
        try {
            sysRecruitApplyService.save(apply);
        } catch (DuplicateKeyException e) {
            throw error(SystemErrorCode.E10036);
        }
        return apply.getId();
    }

    /** 1-34 落成绩；通过时生成注册码并返回明文，供事务外发信。 */
    @Transactional(rollbackFor = Exception.class)
    public AssessmentResult prepareAssessment(Long id, BigDecimal score, int passScore,
            String remark, Long operatorId) {
        SysRecruitApply apply = requireApply(id);
        RecruitApplyStatusEnum status = RecruitApplyStatusEnum.of(apply.getStatus());
        if (status != RecruitApplyStatusEnum.PENDING_ASSESS && status != RecruitApplyStatusEnum.FAILED) {
            throw error(SystemErrorCode.E10038);
        }

        LocalDateTime now = LocalDateTime.now();
        boolean passed = score.compareTo(BigDecimal.valueOf(passScore)) >= 0;
        if (!passed) {
            boolean updated = sysRecruitApplyService.update(Wrappers.<SysRecruitApply>lambdaUpdate()
                    .eq(SysRecruitApply::getId, id)
                    .in(SysRecruitApply::getStatus, RecruitApplyStatusEnum.PENDING_ASSESS.getValue(),
                            RecruitApplyStatusEnum.FAILED.getValue())
                    .set(SysRecruitApply::getStatus, RecruitApplyStatusEnum.FAILED.getValue())
                    .set(SysRecruitApply::getScore, score)
                    .set(SysRecruitApply::getAssessmentRemark, remark)
                    .set(SysRecruitApply::getAssessmentUserId, operatorId)
                    .set(SysRecruitApply::getAssessmentTime, now));
            if (!updated) {
                throw error(SystemErrorCode.E10038);
            }
            return new AssessmentResult(id, passed, RecruitApplyStatusEnum.FAILED.getValue(),
                    null, null, null);
        }

        String code = RecruitCodeUtil.generate();
        String codeHash = RecruitCodeUtil.hash(code, iecmpProperties.getRecruit().getCodePepper());
        LocalDateTime expireTime = now.plusHours(iecmpProperties.getRecruit().getCodeExpireHours());
        boolean updated = sysRecruitApplyService.update(Wrappers.<SysRecruitApply>lambdaUpdate()
                .eq(SysRecruitApply::getId, id)
                .in(SysRecruitApply::getStatus, RecruitApplyStatusEnum.PENDING_ASSESS.getValue(),
                        RecruitApplyStatusEnum.FAILED.getValue())
                .set(SysRecruitApply::getStatus, RecruitApplyStatusEnum.PASSED_PENDING_SEND.getValue())
                .set(SysRecruitApply::getScore, score)
                .set(SysRecruitApply::getAssessmentRemark, remark)
                .set(SysRecruitApply::getAssessmentUserId, operatorId)
                .set(SysRecruitApply::getAssessmentTime, now)
                .set(SysRecruitApply::getCodeHash, codeHash)
                .set(SysRecruitApply::getCodeCreateTime, now)
                .set(SysRecruitApply::getCodeExpireTime, expireTime)
                .set(SysRecruitApply::getCodeAttempts, 0));
        if (!updated) {
            throw error(SystemErrorCode.E10038);
        }
        return new AssessmentResult(id, true, RecruitApplyStatusEnum.PASSED_PENDING_SEND.getValue(),
                code, codeHash, expireTime);
    }

    /** 1-35 生成新注册码，旧码立即失效。 */
    @Transactional(rollbackFor = Exception.class)
    public ResendResult prepareResend(Long id) {
        SysRecruitApply apply = requireApply(id);
        Integer status = apply.getStatus();
        if (!RecruitApplyStatusEnum.PASSED_PENDING_SEND.getValue().equals(status)
                && !RecruitApplyStatusEnum.CODE_SENT.getValue().equals(status)
                && !RecruitApplyStatusEnum.CODE_EXPIRED.getValue().equals(status)) {
            throw error(SystemErrorCode.E10038);
        }
        LocalDateTime now = LocalDateTime.now();
        if (apply.getCodeSentTime() != null && apply.getCodeSentTime()
                .plusSeconds(iecmpProperties.getRecruit().getCodeResendSeconds()).isAfter(now)) {
            throw error(SystemErrorCode.E10041);
        }

        String code = RecruitCodeUtil.generate();
        String codeHash = RecruitCodeUtil.hash(code, iecmpProperties.getRecruit().getCodePepper());
        LocalDateTime expireTime = now.plusHours(iecmpProperties.getRecruit().getCodeExpireHours());
        boolean updated = sysRecruitApplyService.update(Wrappers.<SysRecruitApply>lambdaUpdate()
                .eq(SysRecruitApply::getId, id)
                .in(SysRecruitApply::getStatus, RecruitApplyStatusEnum.PASSED_PENDING_SEND.getValue(),
                        RecruitApplyStatusEnum.CODE_SENT.getValue(),
                        RecruitApplyStatusEnum.CODE_EXPIRED.getValue())
                .set(SysRecruitApply::getStatus, RecruitApplyStatusEnum.PASSED_PENDING_SEND.getValue())
                .set(SysRecruitApply::getCodeHash, codeHash)
                .set(SysRecruitApply::getCodeCreateTime, now)
                .set(SysRecruitApply::getCodeExpireTime, expireTime)
                .set(SysRecruitApply::getCodeAttempts, 0));
        if (!updated) {
            throw error(SystemErrorCode.E10038);
        }
        return new ResendResult(id, apply.getEmail(), apply.getRealName(), code, codeHash, expireTime);
    }

    /** 记录一次发信结果；成功时把状态推进到「已发码」。 */
    @Transactional(rollbackFor = Exception.class)
    public void recordSendResult(Long id, String codeHash, boolean sent) {
        var wrapper = Wrappers.<SysRecruitApply>lambdaUpdate()
                .eq(SysRecruitApply::getId, id)
                .eq(SysRecruitApply::getCodeHash, codeHash)
                .in(SysRecruitApply::getStatus, RecruitApplyStatusEnum.PASSED_PENDING_SEND.getValue(),
                        RecruitApplyStatusEnum.CODE_SENT.getValue())
                .set(SysRecruitApply::getCodeSentTime, LocalDateTime.now())
                .setSql("code_send_count = code_send_count + 1");
        if (sent) {
            wrapper.set(SysRecruitApply::getStatus, RecruitApplyStatusEnum.CODE_SENT.getValue());
        }
        sysRecruitApplyService.update(wrapper);
    }

    /** 输错注册码：累加连续错误次数，返回是否已达上限。 */
    @Transactional(rollbackFor = Exception.class)
    public boolean increaseAttempts(Long id) {
        sysRecruitApplyService.update(Wrappers.<SysRecruitApply>lambdaUpdate()
                .eq(SysRecruitApply::getId, id)
                .eq(SysRecruitApply::getStatus, RecruitApplyStatusEnum.CODE_SENT.getValue())
                .setSql("code_attempts = code_attempts + 1"));
        SysRecruitApply latest = sysRecruitApplyService.getById(id);
        int attempts = latest == null || latest.getCodeAttempts() == null ? 0 : latest.getCodeAttempts();
        return attempts >= iecmpProperties.getRecruit().getCodeMaxAttempts();
    }

    /** 把已过期的注册码标记为过期状态。 */
    @Transactional(rollbackFor = Exception.class)
    public void markExpired(Long id) {
        sysRecruitApplyService.update(Wrappers.<SysRecruitApply>lambdaUpdate()
                .eq(SysRecruitApply::getId, id)
                .eq(SysRecruitApply::getStatus, RecruitApplyStatusEnum.CODE_SENT.getValue())
                .le(SysRecruitApply::getCodeExpireTime, LocalDateTime.now())
                .set(SysRecruitApply::getStatus, RecruitApplyStatusEnum.CODE_EXPIRED.getValue()));
    }

    /** 1-31 原子消费注册码并创建账号。 */
    @Transactional(rollbackFor = Exception.class)
    public RegisterVO register(Long applyId, String codeHash, String username, String rawPassword) {
        SysRecruitApply apply = requireApply(applyId);
        if (!RecruitApplyStatusEnum.CODE_SENT.getValue().equals(apply.getStatus())
                || apply.getCodeExpireTime() == null
                || !apply.getCodeExpireTime().isAfter(LocalDateTime.now())
                || !codeHash.equals(apply.getCodeHash())) {
            throw error(SystemErrorCode.E10039);
        }
        String email = apply.getEmail();
        if (sysUserService.count(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getEmail, email)) > 0) {
            throw error(SystemErrorCode.E10043);
        }
        if (sysUserService.count(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username)) > 0) {
            throw error(SystemErrorCode.E10013);
        }
        if (StringUtils.hasText(apply.getStudentNo())
                && sysUserService.count(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getStudentNo, apply.getStudentNo())) > 0) {
            throw error(SystemErrorCode.E10014);
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(PasswordUtil.encode(rawPassword));
        user.setRealName(apply.getRealName());
        user.setStudentNo(apply.getStudentNo());
        user.setGrade(apply.getGrade());
        user.setClassName(apply.getClassName());
        user.setEmail(email);
        user.setPhone(apply.getPhone());
        user.setStatus(1);
        try {
            sysUserService.save(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(SystemErrorCode.E10013.getCode(), "账号、邮箱或学号已被占用");
        }
        SysRole member = sysRoleService.getOne(Wrappers.<SysRole>lambdaQuery()
                .eq(SysRole::getRoleCode, MEMBER_ROLE)
                .eq(SysRole::getStatus, 1));
        if (member == null) {
            throw new BusinessException(com.iecmp.common.result.ResultCode.SYSTEM_ERROR,
                    "系统未配置成员角色");
        }
        SysUserRole link = new SysUserRole();
        link.setUserId(user.getId());
        link.setRoleId(member.getId());
        sysUserRoleService.save(link);

        boolean consumed = sysRecruitApplyService.update(Wrappers.<SysRecruitApply>lambdaUpdate()
                .eq(SysRecruitApply::getId, applyId)
                .eq(SysRecruitApply::getStatus, RecruitApplyStatusEnum.CODE_SENT.getValue())
                .eq(SysRecruitApply::getCodeHash, codeHash)
                .gt(SysRecruitApply::getCodeExpireTime, LocalDateTime.now())
                .set(SysRecruitApply::getStatus, RecruitApplyStatusEnum.REGISTERED.getValue())
                .set(SysRecruitApply::getCreateUserId, user.getId()));
        if (!consumed) {
            throw error(SystemErrorCode.E10039);
        }

        RegisterVO vo = new RegisterVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        return vo;
    }

    private SysRecruitApply requireApply(Long id) {
        SysRecruitApply apply = id == null ? null : sysRecruitApplyService.getById(id);
        if (apply == null) {
            throw error(SystemErrorCode.E10037);
        }
        return apply;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private BusinessException error(SystemErrorCode code) {
        return new BusinessException(code.getCode(), code.getMsg());
    }

    /** 成绩落库结果。 */
    public record AssessmentResult(Long applyId, boolean passed, Integer status,
                                   String code, String codeHash, LocalDateTime expireTime) {
    }

    /** 补发注册码结果。 */
    public record ResendResult(Long applyId, String email, String realName,
                               String code, String codeHash, LocalDateTime expireTime) {
    }
}
