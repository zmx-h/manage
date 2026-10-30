package com.iecmp.commonm.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.commonm.dto.VideoAddDTO;
import com.iecmp.commonm.dto.VideoUpdateDTO;
import com.iecmp.commonm.entity.SysVideo;
import com.iecmp.commonm.enums.CommonErrorCode;
import com.iecmp.commonm.mapper.SysVideoMapper;
import com.iecmp.commonm.query.VideoListQuery;
import com.iecmp.commonm.query.VideoPageQuery;
import com.iecmp.commonm.service.SysVideoService;
import com.iecmp.commonm.vo.VideoCreateVO;
import com.iecmp.commonm.vo.VideoListVO;
import com.iecmp.commonm.vo.VideoPageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

/** 视频资源业务实现（{@code sys_video}）。 */
@Service
public class SysVideoServiceImpl extends ServiceImpl<SysVideoMapper, SysVideo>
        implements SysVideoService {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<VideoListVO> listPublished(VideoListQuery query) {
        int bizType = query.getBizType() == null ? 2 : query.getBizType();
        validateBizType(bizType);
        int limit = query.getLimit() == null ? 20 : Math.min(Math.max(query.getLimit(), 1), 50);
        return list(Wrappers.<SysVideo>lambdaQuery()
                .eq(SysVideo::getStatus, 1)
                .eq(SysVideo::getBizType, bizType)
                .orderByAsc(SysVideo::getSortNo)
                .orderByAsc(SysVideo::getId)
                .last("LIMIT " + limit))
                .stream()
                .map(this::toListVO)
                .toList();
    }

    @Override
    public PageResult<VideoPageVO> pageAdmin(VideoPageQuery query) {
        var wrapper = Wrappers.<SysVideo>lambdaQuery()
                .eq(query.getBizType() != null, SysVideo::getBizType, query.getBizType())
                .eq(query.getStatus() != null, SysVideo::getStatus, query.getStatus())
                .like(StringUtils.hasText(query.getKeyword()), SysVideo::getTitle,
                        query.getKeyword() == null ? null : query.getKeyword().trim())
                .orderByAsc(SysVideo::getSortNo)
                .orderByDesc(SysVideo::getCreateTime);
        IPage<SysVideo> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageResult.of(page, this::toPageVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VideoCreateVO create(VideoAddDTO dto) {
        validateTitle(dto.getTitle());
        validatePlayUrl(dto.getPlayUrl());
        int bizType = dto.getBizType() == null ? 2 : dto.getBizType();
        validateBizType(bizType);
        validateCoverUrl(dto.getCoverUrl());
        validateJellyfinId(dto.getJellyfinItemId());
        validateDuration(dto.getDuration());
        validateStatus(dto.getStatus());
        validateRemark(dto.getRemark());

        SysVideo video = new SysVideo();
        video.setTitle(dto.getTitle().trim());
        video.setPlayUrl(dto.getPlayUrl().trim());
        video.setBizType(bizType);
        video.setCoverUrl(blankToNull(dto.getCoverUrl()));
        video.setJellyfinItemId(blankToNull(dto.getJellyfinItemId()));
        video.setDuration(dto.getDuration() == null ? 0 : dto.getDuration());
        video.setSortNo(dto.getSortNo() == null ? 0 : dto.getSortNo());
        video.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        video.setRemark(blankToNull(dto.getRemark()));
        if (!save(video)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "视频保存失败");
        }
        VideoCreateVO vo = new VideoCreateVO();
        vo.setVideoId(video.getId());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(VideoUpdateDTO dto) {
        SysVideo exists = getById(dto.getVideoId());
        if (exists == null) {
            throw error(CommonErrorCode.E80010);
        }
        validateTitle(dto.getTitle());
        validatePlayUrl(dto.getPlayUrl());
        int bizType = dto.getBizType() == null ? 2 : dto.getBizType();
        validateBizType(bizType);
        validateCoverUrl(dto.getCoverUrl());
        validateJellyfinId(dto.getJellyfinItemId());
        validateDuration(dto.getDuration());
        validateStatus(dto.getStatus());
        validateRemark(dto.getRemark());

        exists.setTitle(dto.getTitle().trim());
        exists.setPlayUrl(dto.getPlayUrl().trim());
        exists.setBizType(bizType);
        exists.setCoverUrl(blankToNull(dto.getCoverUrl()));
        exists.setJellyfinItemId(blankToNull(dto.getJellyfinItemId()));
        exists.setDuration(dto.getDuration() == null ? 0 : dto.getDuration());
        exists.setSortNo(dto.getSortNo() == null ? 0 : dto.getSortNo());
        exists.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        exists.setRemark(blankToNull(dto.getRemark()));
        if (!updateById(exists)) {
            throw error(CommonErrorCode.E80010);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (getById(id) == null) {
            throw error(CommonErrorCode.E80010);
        }
        if (!removeById(id)) {
            throw error(CommonErrorCode.E80010);
        }
    }

    private VideoListVO toListVO(SysVideo video) {
        VideoListVO vo = new VideoListVO();
        vo.setVideoId(video.getId());
        vo.setTitle(video.getTitle());
        vo.setCoverUrl(video.getCoverUrl());
        vo.setPlayUrl(video.getPlayUrl());
        vo.setDuration(video.getDuration());
        vo.setBizType(video.getBizType());
        return vo;
    }

    private VideoPageVO toPageVO(SysVideo video) {
        VideoPageVO vo = new VideoPageVO();
        vo.setVideoId(video.getId());
        vo.setTitle(video.getTitle());
        vo.setCoverUrl(video.getCoverUrl());
        vo.setPlayUrl(video.getPlayUrl());
        vo.setDuration(video.getDuration());
        vo.setBizType(video.getBizType());
        vo.setJellyfinItemId(video.getJellyfinItemId());
        vo.setStatus(video.getStatus());
        vo.setRemark(video.getRemark());
        vo.setCreateTime(video.getCreateTime() == null ? null : video.getCreateTime().format(DATE_TIME_FMT));
        return vo;
    }

    private void validateTitle(String title) {
        if (!StringUtils.hasText(title) || title.trim().length() > 100) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "视频标题不能为空且最长 100 字");
        }
    }

    private void validatePlayUrl(String playUrl) {
        if (!StringUtils.hasText(playUrl)) {
            throw error(CommonErrorCode.E80011);
        }
        String value = playUrl.trim();
        if (value.length() > 512
                || !(value.startsWith("http://") || value.startsWith("https://"))) {
            throw error(CommonErrorCode.E80011);
        }
    }

    private void validateCoverUrl(String coverUrl) {
        if (!StringUtils.hasText(coverUrl)) {
            return;
        }
        String value = coverUrl.trim();
        if (value.length() > 512 || !(value.startsWith("http://") || value.startsWith("https://")
                || value.startsWith("/api/upload/"))) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "封面地址格式不正确");
        }
    }

    private void validateJellyfinId(String id) {
        if (id != null && id.trim().length() > 64) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "Jellyfin 媒体项 ID 最长 64 字符");
        }
    }

    private void validateDuration(Integer duration) {
        if (duration != null && duration < 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "时长不能为负数");
        }
    }

    private void validateStatus(Integer status) {
        if (status != null && status != 0 && status != 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "status 仅支持 0 或 1");
        }
    }

    private void validateRemark(String remark) {
        if (remark != null && remark.length() > 255) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "备注最长 255 字");
        }
    }

    private void validateBizType(int bizType) {
        if (bizType < 1 || bizType > 3) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "bizType 仅支持 1、2、3");
        }
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private BusinessException error(CommonErrorCode code) {
        return new BusinessException(code.getCode(), code.getMsg());
    }
}