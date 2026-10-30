package com.iecmp.commonm.service;

import com.iecmp.commonm.vo.HomeOverviewVO;
import com.iecmp.commonm.vo.StatDashboardVO;

/** 首页与后台统计看板聚合服务。 */
public interface CommonDashboardService {

    /** 8-10 前台首页看板。 */
    HomeOverviewVO homeOverview(Long userId);

    /** 8-11 后台统计看板。 */
    StatDashboardVO adminDashboard();
}