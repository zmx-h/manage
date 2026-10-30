package com.iecmp.honor.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.honor.dto.UserHonorAlumniAddDTO;
import com.iecmp.honor.dto.UserHonorAlumniUpdateDTO;
import com.iecmp.honor.entity.HonorAlumni;
import com.iecmp.honor.enums.HonorErrorCode;
import com.iecmp.honor.query.HonorAlumniMyPageQuery;
import com.iecmp.honor.service.HonorAlumniService;
import com.iecmp.honor.service.HonorSubmitService;
import com.iecmp.honor.vo.HonorAlumniMyPageVO;
import com.iecmp.honor.vo.HonorAlumniUpdateVO;
import com.iecmp.honor.vo.UserHonorAlumniCreateVO;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.service.SysUserService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 成员端学长学姐信息提交实现。
 */
@Service
@RequiredArgsConstructor
public class HonorSubmitServiceImpl implements HonorSubmitService {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final HonorAlumniService honorAlumniService;
    private final SysUserService sysUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserHonorAlumniCreateVO submit(UserHonorAlumniAddDTO dto) {
        validateFields(dto.getName(), dto.getGender(), dto.getGraduationYear(), dto.getMajor(),
                dto.getClassName(), dto.getCompany(), dto.getPosition(), dto.getAvatarUrl(),
                dto.getBrief(), dto.getTags(), true);

        HonorAlumni alumni = new HonorAlumni();
        alumni.setName(dto.getName().trim());
        alumni.setGender(dto.getGender() == null ? 0 : dto.getGender());
        alumni.setGraduationYear(trimToNull(dto.getGraduationYear()));
        alumni.setMajor(trimToNull(dto.getMajor()));
        alumni.setClassName(trimToNull(dto.getClassName()));
        alumni.setCompany(trimToNull(dto.getCompany()));
        alumni.setPosition(trimToNull(dto.getPosition()));
        alumni.setAvatarUrl(trimToNull(dto.getAvatarUrl()));
        alumni.setBrief(trimToNull(dto.getBrief()));
        alumni.setTags(trimToNull(dto.getTags()));
        alumni.setSubmitUserId(LoginUserUtil.getUserId());
        alumni.setStatus(0);
        alumni.setIsShow(0);
        alumni.setSortNo(0);
        if (!honorAlumniService.save(alumni)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "提交失败，请稍后重试");
        }

        UserHonorAlumniCreateVO vo = new UserHonorAlumniCreateVO();
        vo.setAlumniId(alumni.getId());
        vo.setStatus(0);
        return vo;
    }

    @Override
    public PageResult<HonorAlumniMyPageVO> myPage(HonorAlumniMyPageQuery query) {
        HonorAlumniMyPageQuery q = query == null ? new HonorAlumniMyPageQuery() : query;
        Long userId = LoginUserUtil.getUserId();
        Page<HonorAlumni> page = honorAlumniService.page(new Page<>(q.getPageNum(), q.getPageSize()),
                Wrappers.<HonorAlumni>lambdaQuery()
                        .eq(HonorAlumni::getSubmitUserId, userId)
                        .eq(q.getStatus() != null, HonorAlumni::getStatus, q.getStatus())
                        .orderByDesc(HonorAlumni::getCreateTime)
                        .orderByDesc(HonorAlumni::getId));

        Map<Long, String> auditNames = userNames(page.getRecords().stream()
                .map(HonorAlumni::getAuditUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList());
        return PageResult.of(page, alumni -> toMyPageVO(alumni, auditNames));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdraw(Long id) {
        HonorAlumni alumni = getExisting(id);
        if (!Objects.equals(alumni.getSubmitUserId(), LoginUserUtil.getUserId())) {
            throw new BusinessException(HonorErrorCode.E60003.getCode(), HonorErrorCode.E60003.getMsg());
        }
        if (Integer.valueOf(1).equals(alumni.getStatus())) {
            throw new BusinessException(HonorErrorCode.E60004.getCode(), HonorErrorCode.E60004.getMsg());
        }
        if (!Integer.valueOf(0).equals(alumni.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "仅待审核的提交可撤回");
        }
        if (!honorAlumniService.removeById(id)) {
            throw new BusinessException(HonorErrorCode.E60001.getCode(), HonorErrorCode.E60001.getMsg());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HonorAlumniUpdateVO update(Long id, UserHonorAlumniUpdateDTO dto) {
        HonorAlumni alumni = getExisting(id);
        if (!Objects.equals(alumni.getSubmitUserId(), LoginUserUtil.getUserId())) {
            throw new BusinessException(HonorErrorCode.E60003.getCode(), HonorErrorCode.E60003.getMsg());
        }
        if (!Integer.valueOf(0).equals(alumni.getStatus())
                && !Integer.valueOf(1).equals(alumni.getStatus())
                && !Integer.valueOf(2).equals(alumni.getStatus())) {
            throw new BusinessException(HonorErrorCode.E60008.getCode(), HonorErrorCode.E60008.getMsg());
        }

        String name = dto.getName() == null ? alumni.getName() : dto.getName();
        Integer gender = dto.getGender() == null ? alumni.getGender() : dto.getGender();
        String graduationYear = dto.getGraduationYear() == null
                ? alumni.getGraduationYear() : dto.getGraduationYear();
        String major = dto.getMajor() == null ? alumni.getMajor() : dto.getMajor();
        String className = dto.getClassName() == null ? alumni.getClassName() : dto.getClassName();
        String company = dto.getCompany() == null ? alumni.getCompany() : dto.getCompany();
        String position = dto.getPosition() == null ? alumni.getPosition() : dto.getPosition();
        String avatarUrl = dto.getAvatarUrl() == null ? alumni.getAvatarUrl() : dto.getAvatarUrl();
        String brief = dto.getBrief() == null ? alumni.getBrief() : dto.getBrief();
        String tags = dto.getTags() == null ? alumni.getTags() : dto.getTags();
        validateFields(name, gender, graduationYear, major, className, company, position,
                avatarUrl, brief, tags, true);

        alumni.setName(name.trim());
        alumni.setGender(gender == null ? 0 : gender);
        alumni.setGraduationYear(trimToNull(graduationYear));
        alumni.setMajor(trimToNull(major));
        alumni.setClassName(trimToNull(className));
        alumni.setCompany(trimToNull(company));
        alumni.setPosition(trimToNull(position));
        alumni.setAvatarUrl(trimToNull(avatarUrl));
        alumni.setBrief(trimToNull(brief));
        alumni.setTags(trimToNull(tags));
        alumni.setStatus(0);
        alumni.setIsShow(0);
        if (!honorAlumniService.updateById(alumni)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "修改失败，请稍后重试");
        }

        HonorAlumniUpdateVO vo = new HonorAlumniUpdateVO();
        vo.setAlumniId(alumni.getId());
        vo.setStatus(0);
        vo.setIsShow(0);
        return vo;
    }

    private HonorAlumni getExisting(Long id) {
        HonorAlumni alumni = id == null ? null : honorAlumniService.getById(id);
        if (alumni == null) {
            throw new BusinessException(HonorErrorCode.E60001.getCode(), HonorErrorCode.E60001.getMsg());
        }
        return alumni;
    }

    private HonorAlumniMyPageVO toMyPageVO(HonorAlumni alumni, Map<Long, String> auditNames) {
        HonorAlumniMyPageVO vo = new HonorAlumniMyPageVO();
        vo.setAlumniId(alumni.getId());
        vo.setName(alumni.getName());
        vo.setGraduationYear(alumni.getGraduationYear());
        vo.setMajor(alumni.getMajor());
        vo.setCompany(alumni.getCompany());
        vo.setPosition(alumni.getPosition());
        vo.setAvatarUrl(alumni.getAvatarUrl());
        vo.setStatus(alumni.getStatus());
        vo.setIsShow(alumni.getIsShow());
        vo.setAuditUserName(auditNames.get(alumni.getAuditUserId()));
        vo.setAuditTime(alumni.getAuditTime() == null ? null : alumni.getAuditTime().format(DATE_TIME_FMT));
        vo.setAuditRemark(alumni.getAuditRemark());
        vo.setCanWithdraw(Integer.valueOf(0).equals(alumni.getStatus()) ? 1 : 0);
        vo.setCreateTime(alumni.getCreateTime() == null ? null : alumni.getCreateTime().format(DATE_TIME_FMT));
        return vo;
    }

    private Map<Long, String> userNames(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return sysUserService.listByIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName, (a, b) -> a));
    }

    private void validateFields(String name, Integer gender, String graduationYear, String major,
                                String className, String company, String position, String avatarUrl,
                                String brief, String tags, boolean nameRequired) {
        if (nameRequired && !hasText(name)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "姓名不能为空");
        }
        checkLength(name, 32, "姓名");
        if (gender != null && (gender < 0 || gender > 2)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "性别取值必须为 0、1 或 2");
        }
        checkLength(graduationYear, 10, "毕业年份");
        checkLength(major, 50, "专业");
        checkLength(className, 50, "班级");
        checkLength(company, 128, "就职单位");
        checkLength(position, 64, "职位");
        checkLength(avatarUrl, 512, "头像地址");
        checkLength(tags, 255, "标签");
        if (brief != null && brief.length() > 20000) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "个人简介内容过长");
        }
    }

    private static void checkLength(String value, int max, String label) {
        if (value != null && value.trim().length() > max) {
            throw new BusinessException(ResultCode.PARAM_ERROR, label + "长度不能超过 " + max + " 个字符");
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String trimToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }
}