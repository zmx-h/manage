package com.iecmp.duty.service;

import com.iecmp.duty.query.DutyStatOverviewQuery;
import com.iecmp.duty.vo.DutyStatOverviewVO;

/**
 * 值日统计看板服务，接口 7-19。
 */
public interface DutyStatService {

    DutyStatOverviewVO overview(DutyStatOverviewQuery query);
}