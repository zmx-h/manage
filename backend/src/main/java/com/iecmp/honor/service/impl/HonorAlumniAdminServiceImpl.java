package com.iecmp.honor.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.honor.dto.AdminHonorAlumniAddDTO;
import com.iecmp.honor.dto.AdminHonorAlumniUpdateDTO;
import com.iecmp.honor.dto.HonorAlumniAuditDTO;
import com.iecmp.honor.dto.HonorAlumniSortDTO;
import com.iecmp.honor.dto.HonorAlumniStatusDTO;
import com.iecmp.honor.entity.HonorAlumni;
import com.iecmp.honor.entity.HonorRecord;
import com.iecmp.honor.enums.HonorErrorCode;
import com.iecmp.honor.query.AdminHonorAlumniPageQuery;
import com.iecmp.honor.query.HonorAlumniAuditPageQuery;
import com.iecmp.honor.service.HonorAlumniAdminService;
import com.iecmp.honor.service.HonorAlumniService;
import com.iecmp.honor.service.HonorRecordService;
import com.iecmp.honor.vo.AdminHonorAlumniCreateVO;
import com.iecmp.honor.vo.AdminHonorAlumniPageVO;
import com.iecmp.honor.vo.HonorAlumniAuditPageVO;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.service.SysUserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 管理端学长学姐信息维护与审核实现。
 */
@Service
@RequiredArgsConstructor
public class HonorAlumniAdminServiceImpl implements HonorAlumniAdminService {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final HonorAlumniService honorAlumniService;
    private final HonorRecordService honorRecordService;
    private final SysUserService sysUserService;

    @Override
    public PageResult<AdminHonorAlumniPageVO> page(AdminHonorAlumniPageQuery query) {
        AdminHonorAlumniPageQuery q = query == null ? new AdminHonorAlumniPageQuery() : query;
        LambdaQueryWrapperBuilder builder = new LambdaQueryWrapperBuilder();
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HonorAlumni> wrapper =
                Wrappers.<HonorAlumni>lambdaQuery()
                        .eq(q.getStatus() != null, HonorAlumni::getStatus, q.getStatus())
                        .eq(q.getIsShow() != null, HonorAlumni::getIsShow, q.getIsShow())
                        .eq(hasText(q.getGraduationYear()), HonorAlumni::getGraduationYear, trim(q.getGraduationYear()))
                        .eq(q.getSubmitUserId() != null, HonorAlumni::getSubmitUserId, q.getSubmitUserId());
        if (hasText(q.getKeyword())) {
            String keyword = trim(q.getKeyword());
            wrapper.and(w -> w.like(HonorAlumni::getName, keyword)
                    .or().like(HonorAlumni::getCompany, keyword)
                    .or().like(HonorAlumni::getPosition, keyword));
        }
        wrapper.orderByDesc(HonorAlumni::getCreateTime).orderByDesc(HonorAlumni::getId);

        Page<HonorAlumni> page = honorAlumniService.page(new Page<>(q.getPageNum(), q.getPageSize()), wrapper);
        List<Long> alumniIds = page.getRecords().stream().map(HonorAlumni::getId).toList();
        Map<Long, Long> honorCounts = countRecordsByAlumni(alumniIds);
        Set<Long> userIds = new HashSet<>();
        page.getRecords().forEach(item -> {
            if (item.getSubmitUserId() != null) {
                userIds.add(item.getSubmitUserId());
            }
            if (item.getAuditUserId() != null) {
                userIds.add(item.getAuditUserId());
            }
        });
        Map<Long, String> userNames = userNames(userIds.stream().toList());
        return PageResult.of(page, alumni -> toPageVO(alumni, honorCounts, userNames));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminHonorAlumniCreateVO add(AdminHonorAlumniAddDTO dto) {
        validateFields(dto.getName(), dto.getGender(), dto.getGraduationYear(), dto.getMajor(),
                dto.getClassName(), dto.getCompany(), dto.getPosition(), dto.getAvatarUrl(),
                dto.getBrief(), dto.getTags());
        validateBinary(dto.getIsShow(), "是否上板");
        if (dto.getSortNo() != null && dto.getSortNo() < 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "排序号不能为负数");
        }

        Long operatorId = LoginUserUtil.getUserId();
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
        alumni.setSubmitUserId(operatorId);
        alumni.setStatus(1);
        alumni.setAuditUserId(operatorId);
        alumni.setAuditTime(LocalDateTime.now());
        alumni.setIsShow(dto.getIsShow() == null ? 0 : dto.getIsShow());
        alumni.setSortNo(dto.getSortNo() == null ? 0 : dto.getSortNo());
        if (!honorAlumniService.save(alumni)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "新增失败，请稍后重试");
        }

        AdminHonorAlumniCreateVO vo = new AdminHonorAlumniCreateVO();
        vo.setAlumniId(alumni.getId());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AdminHonorAlumniUpdateDTO dto) {
        HonorAlumni alumni = getExisting(dto.getAlumniId());
        validateFields(dto.getName(), dto.getGender(), dto.getGraduationYear(), dto.getMajor(),
                dto.getClassName(), dto.getCompany(), dto.getPosition(), dto.getAvatarUrl(),
                dto.getBrief(), dto.getTags());
        if (dto.getSortNo() != null && dto.getSortNo() < 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "排序号不能为负数");
        }

        alumni.setName(dto.getName().trim());
        if (dto.getGender() != null) {
            alumni.setGender(dto.getGender());
        }
        alumni.setGraduationYear(trimToNull(dto.getGraduationYear()));
        alumni.setMajor(trimToNull(dto.getMajor()));
        alumni.setClassName(trimToNull(dto.getClassName()));
        alumni.setCompany(trimToNull(dto.getCompany()));
        alumni.setPosition(trimToNull(dto.getPosition()));
        alumni.setAvatarUrl(trimToNull(dto.getAvatarUrl()));
        alumni.setBrief(trimToNull(dto.getBrief()));
        alumni.setTags(trimToNull(dto.getTags()));
        if (dto.getSortNo() != null) {
            alumni.setSortNo(dto.getSortNo());
        }
        if (!honorAlumniService.updateById(alumni)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "修改失败，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getExisting(id);
        honorRecordService.remove(Wrappers.<HonorRecord>lambdaQuery().eq(HonorRecord::getAlumniId, id));
        if (!honorAlumniService.removeById(id)) {
            throw new BusinessException(HonorErrorCode.E60001.getCode(), HonorErrorCode.E60001.getMsg());
        }
    }

    @Override
    public void updateShowStatus(Long id, HonorAlumniStatusDTO dto) {
        if (dto.getIsShow() == null || (dto.getIsShow() != 0 && dto.getIsShow() != 1)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "上板状态必须为 0 或 1");
        }
        HonorAlumni alumni = getExisting(id);
        if (dto.getIsShow() == 1 && !Integer.valueOf(1).equals(alumni.getStatus())) {
            throw new BusinessException(HonorErrorCode.E60006.getCode(), HonorErrorCode.E60006.getMsg());
        }
        alumni.setIsShow(dto.getIsShow());
        if (!honorAlumniService.updateById(alumni)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "操作失败，请稍后重试");
        }
    }

    @Override
    public void updateSort(Long id, HonorAlumniSortDTO dto) {
        if (dto.getSortNo() == null || dto.getSortNo() < 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "排序号不能为空且不能为负数");
        }
        HonorAlumni alumni = getExisting(id);
        alumni.setSortNo(dto.getSortNo());
        if (!honorAlumniService.updateById(alumni)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "操作失败，请稍后重试");
        }
    }

    @Override
    public PageResult<HonorAlumniAuditPageVO> auditPage(HonorAlumniAuditPageQuery query) {
        HonorAlumniAuditPageQuery q = query == null ? new HonorAlumniAuditPageQuery() : query;
        int status = q.getStatus() == null ? 0 : q.getStatus();
        if (status < 0 || status > 2) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "审核状态取值必须为 0、1 或 2");
        }
        LocalDateTime begin = parseDateTime(q.getBeginTime(), false);
        LocalDateTime end = parseDateTime(q.getEndTime(), true);
        if (begin != null && end != null && begin.isAfter(end)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "提交时间起不能晚于结束时间");
        }

        Page<HonorAlumni> page = honorAlumniService.page(new Page<>(q.getPageNum(), q.getPageSize()),
                Wrappers.<HonorAlumni>lambdaQuery()
                        .eq(HonorAlumni::getStatus, status)
                        .eq(q.getSubmitUserId() != null, HonorAlumni::getSubmitUserId, q.getSubmitUserId())
                        .ge(begin != null, HonorAlumni::getCreateTime, begin)
                        .le(end != null, HonorAlumni::getCreateTime, end)
                        .orderByAsc(HonorAlumni::getCreateTime)
                        .orderByAsc(HonorAlumni::getId));
        Set<Long> userIds = new HashSet<>();
        page.getRecords().forEach(item -> {
            if (item.getSubmitUserId() != null) {
                userIds.add(item.getSubmitUserId());
            }
            if (item.getAuditUserId() != null) {
                userIds.add(item.getAuditUserId());
            }
        });
        Map<Long, SysUser> users = userMap(userIds.stream().toList());
        return PageResult.of(page, alumni -> toAuditPageVO(alumni, users));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, HonorAlumniAuditDTO dto) {
        HonorAlumni alumni = getExisting(id);
        if (!Integer.valueOf(0).equals(alumni.getStatus())) {
            throw new BusinessException(HonorErrorCode.E60002.getCode(), HonorErrorCode.E60002.getMsg());
        }
        if (dto.getAuditStatus() == null
                || (dto.getAuditStatus() != 1 && dto.getAuditStatus() != 2)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "审核结果必须为 1 通过或 2 驳回");
        }
        validateBinary(dto.getIsShow(), "是否上板");
        checkLength(dto.getAuditRemark(), 255, "审核意见");

        alumni.setStatus(dto.getAuditStatus());
        alumni.setIsShow(dto.getAuditStatus() == 1 && Integer.valueOf(1).equals(dto.getIsShow()) ? 1 : 0);
        alumni.setAuditUserId(LoginUserUtil.getUserId());
        alumni.setAuditTime(LocalDateTime.now());
        alumni.setAuditRemark(trimToNull(dto.getAuditRemark()));
        if (!honorAlumniService.updateById(alumni)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "审核失败，请稍后重试");
        }
    }

    private HonorAlumni getExisting(Long id) {
        HonorAlumni alumni = id == null ? null : honorAlumniService.getById(id);
        if (alumni == null) {
            throw new BusinessException(HonorErrorCode.E60001.getCode(), HonorErrorCode.E60001.getMsg());
        }
        return alumni;
    }

    private Map<Long, Long> countRecordsByAlumni(List<Long> alumniIds) {
        if (alumniIds.isEmpty()) {
            return Map.of();
        }
        return honorRecordService.list(Wrappers.<HonorRecord>lambdaQuery()
                        .select(HonorRecord::getAlumniId)
                        .in(HonorRecord::getAlumniId, alumniIds))
                .stream()
                .collect(Collectors.groupingBy(HonorRecord::getAlumniId, Collectors.counting()));
    }

    private AdminHonorAlumniPageVO toPageVO(HonorAlumni alumni, Map<Long, Long> honorCounts,
                                             Map<Long, String> userNames) {
        AdminHonorAlumniPageVO vo = new AdminHonorAlumniPageVO();
        vo.setAlumniId(alumni.getId());
        vo.setName(alumni.getName());
        vo.setGender(alumni.getGender() == null ? 0 : alumni.getGender());
        vo.setGraduationYear(alumni.getGraduationYear());
        vo.setMajor(alumni.getMajor());
        vo.setCompany(alumni.getCompany());
        vo.setPosition(alumni.getPosition());
        vo.setAvatarUrl(alumni.getAvatarUrl());
        vo.setTags(alumni.getTags());
        vo.setHonorCount(honorCounts.getOrDefault(alumni.getId(), 0L).intValue());
        vo.setSubmitUserId(alumni.getSubmitUserId());
        vo.setSubmitUserName(userNames.get(alumni.getSubmitUserId()));
        vo.setStatus(alumni.getStatus());
        vo.setIsShow(alumni.getIsShow());
        vo.setSortNo(alumni.getSortNo() == null ? 0 : alumni.getSortNo());
        vo.setAuditUserName(userNames.get(alumni.getAuditUserId()));
        vo.setAuditTime(formatDateTime(alumni.getAuditTime()));
        vo.setAuditRemark(alumni.getAuditRemark());
        vo.setCreateTime(formatDateTime(alumni.getCreateTime()));
        return vo;
    }

    private HonorAlumniAuditPageVO toAuditPageVO(HonorAlumni alumni, Map<Long, SysUser> users) {
        HonorAlumniAuditPageVO vo = new HonorAlumniAuditPageVO();
        vo.setAlumniId(alumni.getId());
        vo.setName(alumni.getName());
        vo.setGender(alumni.getGender() == null ? 0 : alumni.getGender());
        vo.setGraduationYear(alumni.getGraduationYear());
        vo.setMajor(alumni.getMajor());
        vo.setClassName(alumni.getClassName());
        vo.setCompany(alumni.getCompany());
        vo.setPosition(alumni.getPosition());
        vo.setAvatarUrl(alumni.getAvatarUrl());
        vo.setBrief(alumni.getBrief());
        vo.setTags(alumni.getTags());
        vo.setSubmitUserId(alumni.getSubmitUserId());
        SysUser submitUser = users.get(alumni.getSubmitUserId());
        vo.setSubmitUserName(submitUser == null ? null : submitUser.getRealName());
        vo.setSubmitUserGrade(submitUser == null ? null : submitUser.getGrade());
        vo.setStatus(alumni.getStatus());
        vo.setIsShow(alumni.getIsShow());
        SysUser auditUser = users.get(alumni.getAuditUserId());
        vo.setAuditUserName(auditUser == null ? null : auditUser.getRealName());
        vo.setAuditTime(formatDateTime(alumni.getAuditTime()));
        vo.setAuditRemark(alumni.getAuditRemark());
        vo.setCreateTime(formatDateTime(alumni.getCreateTime()));
        return vo;
    }

    private Map<Long, String> userNames(List<Long> userIds) {
        return userMap(userIds).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getRealName()));
    }

    private Map<Long, SysUser> userMap(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return sysUserService.listByIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, user -> user, (a, b) -> a));
    }

    private void validateFields(String name, Integer gender, String graduationYear, String major,
                                String className, String company, String position, String avatarUrl,
                                String brief, String tags) {
        if (!hasText(name)) {
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

    private static void validateBinary(Integer value, String label) {
        if (value != null && value != 0 && value != 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR, label + "必须为 0 或 1");
        }
    }

    private static void checkLength(String value, int max, String label) {
        if (value != null && value.trim().length() > max) {
            throw new BusinessException(ResultCode.PARAM_ERROR, label + "长度不能超过 " + max + " 个字符");
        }
    }

    private static String formatDateTime(LocalDateTime time) {
        return time == null ? null : time.format(DATE_TIME_FMT);
    }

    private static LocalDateTime parseDateTime(String value, boolean endOfDay) {
        if (!hasText(value)) {
            return null;
        }
        String text = value.trim();
        try {
            if (text.contains("T")) {
                return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            if (text.length() > 10) {
                return LocalDateTime.parse(text, DATE_TIME_FMT);
            }
            LocalDate date = LocalDate.parse(text, DATE_FMT);
            return endOfDay ? date.atTime(23, 59, 59) : date.atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "时间格式不正确，应为 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss");
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    private static String trimToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private static final class LambdaQueryWrapperBuilder {
        private LambdaQueryWrapperBuilder() {
        }
    }
}