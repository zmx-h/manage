package com.iecmp.commonm.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.commonm.dto.NoticeAddDTO;
import com.iecmp.commonm.dto.NoticeUpdateDTO;
import com.iecmp.commonm.entity.SysNotice;
import com.iecmp.commonm.enums.CommonErrorCode;
import com.iecmp.commonm.mapper.SysNoticeMapper;
import com.iecmp.commonm.query.AdminNoticePageQuery;
import com.iecmp.commonm.query.UserNoticePageQuery;
import com.iecmp.commonm.service.SysNoticeService;
import com.iecmp.commonm.vo.AdminNoticePageVO;
import com.iecmp.commonm.vo.NoticeCreateVO;
import com.iecmp.commonm.vo.UserNoticePageVO;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/** 公告业务实现（{@code sys_notice}）。 */
@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice>
        implements SysNoticeService {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FMT_MINUTE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final SysUserService sysUserService;

    @Override
    public PageResult<UserNoticePageVO> userPage(UserNoticePageQuery query) {
        var wrapper = Wrappers.<SysNotice>lambdaQuery()
                .eq(SysNotice::getStatus, 1)
                .le(SysNotice::getPublishTime, LocalDateTime.now())
                .eq(query.getNoticeType() != null, SysNotice::getNoticeType, query.getNoticeType())
                .like(StringUtils.hasText(query.getKeyword()), SysNotice::getTitle,
                        query.getKeyword() == null ? null : query.getKeyword().trim())
                .orderByDesc(SysNotice::getIsTop)
                .orderByDesc(SysNotice::getPublishTime);
        IPage<SysNotice> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageResult.of(page, this::toUserVO);
    }

    @Override
    public PageResult<AdminNoticePageVO> adminPage(AdminNoticePageQuery query) {
        var wrapper = Wrappers.<SysNotice>lambdaQuery()
                .eq(query.getStatus() != null, SysNotice::getStatus, query.getStatus())
                .eq(query.getNoticeType() != null, SysNotice::getNoticeType, query.getNoticeType())
                .like(StringUtils.hasText(query.getKeyword()), SysNotice::getTitle,
                        query.getKeyword() == null ? null : query.getKeyword().trim())
                .ge(StringUtils.hasText(query.getBeginTime()), SysNotice::getCreateTime,
                        parseDateTime(query.getBeginTime()))
                .le(StringUtils.hasText(query.getEndTime()), SysNotice::getCreateTime,
                        parseDateTime(query.getEndTime()))
                .orderByDesc(SysNotice::getIsTop)
                .orderByDesc(SysNotice::getCreateTime);
        IPage<SysNotice> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        Map<Long, SysUser> users = loadUsers(page.getRecords().stream()
                .map(SysNotice::getCreateBy).filter(Objects::nonNull).distinct().toList());
        return PageResult.of(page, notice -> toAdminVO(notice, users));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NoticeCreateVO create(NoticeAddDTO dto) {
        validateTitleAndContent(dto.getTitle(), dto.getContent());
        Integer status = dto.getStatus() == null ? 1 : dto.getStatus();
        if (status != 0 && status != 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "status 仅支持 0 草稿或 1 发布");
        }
        Integer noticeType = dto.getNoticeType() == null ? 1 : dto.getNoticeType();
        Integer isTop = dto.getIsTop() == null ? 0 : dto.getIsTop();
        validateNoticeType(noticeType);
        validateTop(isTop);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime publishTime = StringUtils.hasText(dto.getPublishTime())
                ? parseDateTime(dto.getPublishTime()) : now;
        if (publishTime.isBefore(now)) {
            throw error(CommonErrorCode.E80009);
        }

        SysNotice notice = new SysNotice();
        notice.setTitle(dto.getTitle().trim());
        notice.setContent(dto.getContent());
        notice.setNoticeType(noticeType);
        notice.setIsTop(isTop);
        notice.setStatus(status);
        notice.setPublishTime(publishTime);
        if (!save(notice)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "公告保存失败");
        }
        NoticeCreateVO vo = new NoticeCreateVO();
        vo.setNoticeId(notice.getId());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(NoticeUpdateDTO dto) {
        SysNotice exists = getById(dto.getNoticeId());
        if (exists == null) {
            throw error(CommonErrorCode.E80007);
        }
        validateTitleAndContent(dto.getTitle(), dto.getContent());
        Integer noticeType = dto.getNoticeType() == null ? exists.getNoticeType() : dto.getNoticeType();
        Integer isTop = dto.getIsTop() == null ? exists.getIsTop() : dto.getIsTop();
        Integer status = dto.getStatus() == null ? exists.getStatus() : dto.getStatus();
        validateNoticeType(noticeType);
        validateTop(isTop);
        if (status < 0 || status > 2) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "status 仅支持 0、1、2");
        }
        LocalDateTime publishTime = exists.getPublishTime();
        if (StringUtils.hasText(dto.getPublishTime())) {
            publishTime = parseDateTime(dto.getPublishTime());
            if (publishTime.isBefore(LocalDateTime.now())) {
                throw error(CommonErrorCode.E80009);
            }
        }

        exists.setTitle(dto.getTitle().trim());
        exists.setContent(dto.getContent());
        exists.setNoticeType(noticeType);
        exists.setIsTop(isTop);
        exists.setStatus(status);
        exists.setPublishTime(publishTime);
        if (!updateById(exists)) {
            throw error(CommonErrorCode.E80007);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (getById(id) == null) {
            throw error(CommonErrorCode.E80007);
        }
        if (!removeById(id)) {
            throw error(CommonErrorCode.E80007);
        }
    }

    private UserNoticePageVO toUserVO(SysNotice notice) {
        UserNoticePageVO vo = new UserNoticePageVO();
        vo.setNoticeId(notice.getId());
        vo.setTitle(notice.getTitle());
        vo.setContent(notice.getContent());
        vo.setNoticeType(notice.getNoticeType());
        vo.setIsTop(notice.getIsTop());
        vo.setPublishTime(format(notice.getPublishTime()));
        return vo;
    }

    private AdminNoticePageVO toAdminVO(SysNotice notice, Map<Long, SysUser> users) {
        AdminNoticePageVO vo = new AdminNoticePageVO();
        vo.setNoticeId(notice.getId());
        vo.setTitle(notice.getTitle());
        vo.setContent(notice.getContent());
        vo.setNoticeType(notice.getNoticeType());
        vo.setIsTop(notice.getIsTop());
        vo.setPublishTime(format(notice.getPublishTime()));
        vo.setStatus(notice.getStatus());
        SysUser user = notice.getCreateBy() == null ? null : users.get(notice.getCreateBy());
        vo.setCreateBy(user == null ? null : user.getRealName());
        vo.setCreateTime(format(notice.getCreateTime()));
        return vo;
    }

    private Map<Long, SysUser> loadUsers(List<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return sysUserService.listByIds(ids).stream()
                .collect(Collectors.toMap(SysUser::getId, Function.identity(), (a, b) -> a));
    }

    private void validateTitleAndContent(String title, String content) {
        if (!StringUtils.hasText(title) || title.trim().length() > 100) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "标题不能为空且最长 100 字");
        }
        if (!StringUtils.hasText(content)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "内容不能为空");
        }
    }

    private void validateNoticeType(Integer noticeType) {
        if (noticeType == null || noticeType < 1 || noticeType > 4) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "公告类型仅支持 1~4");
        }
    }

    private void validateTop(Integer isTop) {
        if (isTop == null || (isTop != 0 && isTop != 1)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "isTop 仅支持 0 或 1");
        }
    }

    private String format(LocalDateTime time) {
        return time == null ? null : time.format(DATE_TIME_FMT);
    }

    private LocalDateTime parseDateTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return value.contains("T") ? LocalDateTime.parse(value) : LocalDateTime.parse(value.trim(), DATE_TIME_FMT);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(value.trim(), DATE_TIME_FMT_MINUTE);
            } catch (DateTimeParseException ignored) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "时间格式应为 yyyy-MM-dd HH:mm:ss");
            }
        }
    }

    private BusinessException error(CommonErrorCode code) {
        return new BusinessException(code.getCode(), code.getMsg());
    }
}