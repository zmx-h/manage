package com.iecmp.commonm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.common.result.PageResult;
import com.iecmp.commonm.entity.SysOperLog;
import com.iecmp.commonm.query.LogPageQuery;
import com.iecmp.commonm.vo.LogPageVO;

/** 操作日志业务接口（{@code sys_oper_log}）。 */
public interface SysOperLogService extends IService<SysOperLog> {

    /** 8-12 操作日志分页。 */
    PageResult<LogPageVO> pageQuery(LogPageQuery query);
}