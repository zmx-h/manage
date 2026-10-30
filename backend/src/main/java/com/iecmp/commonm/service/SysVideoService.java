package com.iecmp.commonm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.common.result.PageResult;
import com.iecmp.commonm.dto.VideoAddDTO;
import com.iecmp.commonm.dto.VideoUpdateDTO;
import com.iecmp.commonm.entity.SysVideo;
import com.iecmp.commonm.query.VideoListQuery;
import com.iecmp.commonm.query.VideoPageQuery;
import com.iecmp.commonm.vo.VideoCreateVO;
import com.iecmp.commonm.vo.VideoListVO;
import com.iecmp.commonm.vo.VideoPageVO;

import java.util.List;

/** 视频资源业务接口（{@code sys_video}）。 */
public interface SysVideoService extends IService<SysVideo> {

    /** 8-15 前台展示视频。 */
    List<VideoListVO> listPublished(VideoListQuery query);

    /** 8-16 后台视频分页。 */
    PageResult<VideoPageVO> pageAdmin(VideoPageQuery query);

    /** 8-17 新增视频。 */
    VideoCreateVO create(VideoAddDTO dto);

    /** 8-18 修改视频。 */
    void update(VideoUpdateDTO dto);

    /** 8-19 删除视频。 */
    void delete(Long id);
}