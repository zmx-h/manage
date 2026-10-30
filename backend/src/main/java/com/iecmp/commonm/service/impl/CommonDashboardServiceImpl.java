package com.iecmp.commonm.service.impl;

import com.iecmp.commonm.service.CommonDashboardService;
import com.iecmp.commonm.vo.HomeOverviewVO;
import com.iecmp.commonm.vo.StatDashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 首页与后台统计看板聚合实现，使用一次查询聚合各模块只读数据。 */
@Service
@RequiredArgsConstructor
public class CommonDashboardServiceImpl implements CommonDashboardService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final JdbcTemplate jdbcTemplate;

    @Override
    public HomeOverviewVO homeOverview(Long userId) {
        HomeOverviewVO vo = new HomeOverviewVO();
        vo.setMySeat(mySeat(userId));
        vo.setNextDuty(nextDuty(userId));
        vo.setBorrowingCount(intValue("""
                SELECT COUNT(*) FROM asset_borrow
                WHERE user_id = ? AND borrow_type = 1 AND actual_return_time IS NULL
                  AND status IN (1, 4) AND deleted = 0
                """, userId));
        vo.setBudgetApplyPending(intValue("""
                SELECT COUNT(*) FROM budget_apply
                WHERE apply_user_id = ? AND status = 0 AND deleted = 0
                """, userId));
        vo.setBudgetApprovedAmount(decimalValue("""
                SELECT COALESCE(SUM(COALESCE(approved_amount, amount)), 0) FROM budget_apply
                WHERE apply_user_id = ? AND status = 1 AND deleted = 0
                  AND YEAR(COALESCE(audit_time, create_time)) = YEAR(CURDATE())
                """, userId));
        vo.setCommitCountThisMonth(intValue("""
                SELECT COUNT(*) FROM repo_commit
                WHERE user_id = ? AND deleted = 0 AND commit_time >= ?
                """, userId, LocalDate.now().withDayOfMonth(1).atStartOfDay()));
        vo.setUnreadMsgCount(intValue("""
                SELECT COUNT(*) FROM sys_message
                WHERE receiver_id = ? AND is_read = 0 AND deleted = 0
                """, userId));
        vo.setTopNotices(topNotices());
        return vo;
    }

    @Override
    public StatDashboardVO adminDashboard() {
        StatDashboardVO vo = new StatDashboardVO();
        vo.setUserCount(intValue("SELECT COUNT(*) FROM sys_user WHERE status = 1 AND deleted = 0"));
        vo.setAssetStat(assetStat());
        vo.setBudgetStat(budgetStat());
        vo.setTodayDuty(todayDuty());
        vo.setRepoCommitCountThisMonth(intValue("""
                SELECT COUNT(*) FROM repo_commit
                WHERE deleted = 0 AND commit_time >= ?
                """, LocalDate.now().withDayOfMonth(1).atStartOfDay()));
        vo.setPendingTodo(pendingTodo());
        return vo;
    }

    private HomeOverviewVO.MySeatVO mySeat(Long userId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT s.id, s.seat_no, a.name AS area_name
                FROM seat_info s JOIN seat_area a ON a.id = s.area_id
                WHERE s.user_id = ? AND s.deleted = 0 AND a.deleted = 0
                ORDER BY s.assign_time DESC, s.id DESC LIMIT 1
                """, userId);
        if (rows.isEmpty()) {
            return null;
        }
        Map<String, Object> row = rows.get(0);
        HomeOverviewVO.MySeatVO seat = new HomeOverviewVO.MySeatVO();
        seat.setSeatId(longValue(row.get("id")));
        seat.setSeatNo(stringValue(row.get("seat_no")));
        seat.setAreaName(stringValue(row.get("area_name")));
        return seat;
    }

    private HomeOverviewVO.NextDutyVO nextDuty(Long userId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT duty_date, task_content, group_name
                FROM duty_schedule
                WHERE user_id = ? AND duty_date >= CURDATE() AND status = 0 AND deleted = 0
                ORDER BY duty_date ASC, id ASC LIMIT 1
                """, userId);
        if (rows.isEmpty()) {
            return null;
        }
        Map<String, Object> row = rows.get(0);
        HomeOverviewVO.NextDutyVO duty = new HomeOverviewVO.NextDutyVO();
        Object date = row.get("duty_date");
        duty.setDutyDate(date instanceof Date d ? d.toLocalDate().format(DATE_FMT) : stringValue(date));
        duty.setTaskContent(stringValue(row.get("task_content")));
        duty.setGroupName(stringValue(row.get("group_name")));
        return duty;
    }

    private List<HomeOverviewVO.TopNoticeVO> topNotices() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT id, title, publish_time FROM sys_notice
                WHERE status = 1 AND is_top = 1 AND publish_time <= NOW() AND deleted = 0
                ORDER BY publish_time DESC, id DESC LIMIT 5
                """);
        List<HomeOverviewVO.TopNoticeVO> result = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            HomeOverviewVO.TopNoticeVO notice = new HomeOverviewVO.TopNoticeVO();
            notice.setNoticeId(longValue(row.get("id")));
            notice.setTitle(stringValue(row.get("title")));
            Object time = row.get("publish_time");
            notice.setPublishTime(time instanceof java.sql.Timestamp ts
                    ? ts.toLocalDateTime().format(DATE_TIME_FMT) : stringValue(time));
            result.add(notice);
        }
        return result;
    }

    private StatDashboardVO.AssetStatVO assetStat() {
        Map<String, Object> row = jdbcTemplate.queryForMap("""
                SELECT COUNT(*) AS total,
                       COALESCE(SUM(status = 1), 0) AS in_use,
                       COALESCE(SUM(status = 2), 0) AS idle,
                       COALESCE(SUM(status = 3), 0) AS repair,
                       COALESCE(SUM(status = 5), 0) AS scrapped
                FROM asset_info WHERE deleted = 0
                """);
        StatDashboardVO.AssetStatVO stat = new StatDashboardVO.AssetStatVO();
        stat.setTotal(intValue(row.get("total")));
        stat.setInUse(intValue(row.get("in_use")));
        stat.setIdle(intValue(row.get("idle")));
        stat.setRepair(intValue(row.get("repair")));
        stat.setScrapped(intValue(row.get("scrapped")));
        return stat;
    }

    private StatDashboardVO.BudgetStatVO budgetStat() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT title, total_amount, used_amount FROM budget_period
                WHERE status = 1 AND deleted = 0
                ORDER BY start_date DESC, id DESC LIMIT 1
                """);
        StatDashboardVO.BudgetStatVO stat = new StatDashboardVO.BudgetStatVO();
        if (rows.isEmpty()) {
            stat.setTotalAmount(BigDecimal.ZERO);
            stat.setUsedAmount(BigDecimal.ZERO);
            stat.setExecutionRate(BigDecimal.ZERO);
            return stat;
        }
        Map<String, Object> row = rows.get(0);
        BigDecimal total = decimalValue(row.get("total_amount"));
        BigDecimal used = decimalValue(row.get("used_amount"));
        stat.setPeriodTitle(stringValue(row.get("title")));
        stat.setTotalAmount(total);
        stat.setUsedAmount(used);
        stat.setExecutionRate(total.signum() == 0 ? BigDecimal.ZERO
                : used.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP));
        return stat;
    }

    private List<StatDashboardVO.TodayDutyVO> todayDuty() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT s.user_id, u.real_name, s.group_name, s.status
                FROM duty_schedule s LEFT JOIN sys_user u ON u.id = s.user_id
                WHERE s.duty_date = CURDATE() AND s.deleted = 0
                ORDER BY s.id ASC
                """);
        List<StatDashboardVO.TodayDutyVO> result = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            StatDashboardVO.TodayDutyVO duty = new StatDashboardVO.TodayDutyVO();
            duty.setUserId(longValue(row.get("user_id")));
            duty.setRealName(stringValue(row.get("real_name")));
            duty.setGroupName(stringValue(row.get("group_name")));
            duty.setStatus(intValue(row.get("status")));
            result.add(duty);
        }
        return result;
    }

    private StatDashboardVO.PendingTodoVO pendingTodo() {
        StatDashboardVO.PendingTodoVO todo = new StatDashboardVO.PendingTodoVO();
        todo.setBudgetApply(intValue("SELECT COUNT(*) FROM budget_apply WHERE status = 0 AND deleted = 0"));
        todo.setAssetBorrow(intValue("SELECT COUNT(*) FROM asset_borrow WHERE status = 0 AND deleted = 0"));
        todo.setSeatApply(intValue("SELECT COUNT(*) FROM seat_apply WHERE status = 0 AND deleted = 0"));
        todo.setSeatSwap(intValue("SELECT COUNT(*) FROM seat_swap WHERE status IN (0, 1) AND deleted = 0"));
        todo.setHonorAudit(intValue("SELECT COUNT(*) FROM honor_alumni WHERE status = 0 AND deleted = 0"));
        todo.setRegisterApply(intValue("SELECT COUNT(*) FROM sys_register_apply WHERE status = 0 AND deleted = 0"));
        return todo;
    }

    private int intValue(String sql, Object... args) {
        Number value = jdbcTemplate.queryForObject(sql, Number.class, args);
        return value == null ? 0 : value.intValue();
    }

    private int intValue(Object value) {
        return value instanceof Number n ? n.intValue() : 0;
    }

    private Long longValue(Object value) {
        return value instanceof Number n ? n.longValue() : null;
    }

    private BigDecimal decimalValue(String sql, Object... args) {
        BigDecimal value = jdbcTemplate.queryForObject(sql, BigDecimal.class, args);
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal decimalValue(Object value) {
        return value instanceof BigDecimal d ? d
                : value instanceof Number n ? BigDecimal.valueOf(n.doubleValue()) : BigDecimal.ZERO;
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}