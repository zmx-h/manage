package com.iecmp.commonm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.common.result.PageResult;
import com.iecmp.commonm.dto.NoticeAddDTO;
import com.iecmp.commonm.dto.NoticeUpdateDTO;
import com.iecmp.commonm.entity.SysNotice;
import com.iecmp.commonm.query.AdminNoticePageQuery;
import com.iecmp.commonm.query.UserNoticePageQuery;
import com.iecmp.commonm.vo.AdminNoticePageVO;
import com.iecmp.commonm.vo.NoticeCreateVO;
import com.iecmp.commonm.vo.UserNoticePageVO;

/** 公告业务接口（{@code sys_notice}）。 */
public interface SysNoticeService extends IService<SysNotice> {

    /** 8-05 前台公告分页。 */
    PageResult<UserNoticePageVO> userPage(UserNoticePageQuery query);

    /** 8-06 后台公告分页。 */
    PageResult<AdminNoticePageVO> adminPage(AdminNoticePageQuery query);

    /** 8-07 新增公告。 */
    NoticeCreateVO create(NoticeAddDTO dto);

    /** 8-08 修改公告。 */
    void update(NoticeUpdateDTO dto);

    /** 8-09 删除公告。 */
    void delete(Long id);
}