package com.iecmp.repo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.result.PageResult;
import com.iecmp.repo.entity.RepoSyncLog;
import com.iecmp.repo.mapper.RepoSyncLogMapper;
import com.iecmp.repo.query.RepoSyncLogPageQuery;
import com.iecmp.repo.service.RepoSyncLogService;
import com.iecmp.repo.util.RepoDateUtils;
import com.iecmp.repo.util.RepoPageUtils;
import com.iecmp.repo.util.RepoSortUtils;
import com.iecmp.repo.vo.RepoSyncLogPageVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 同步日志业务实现（{@code repo_sync_log}）。
 *
 * @author IECMP 后端组
 */
@Service
public class RepoSyncLogServiceImpl extends ServiceImpl<RepoSyncLogMapper, RepoSyncLog>
        implements RepoSyncLogService {

    /**
     * 接口 2-16 允许排序的字段白名单。
     *
     * <p>带表别名 {@code l.}，因为最终 SQL 是日志表左连账户表与用户表，
     * 不加前缀会和 {@code repo_account} 的同名列撞上。
     */
    private static final Map<String, String> SORT_FIELDS = Map.of(
            "create_time", "l.create_time",
            "sync_result", "l.sync_result",
            "commit_count", "l.commit_count",
            "cost_ms", "l.cost_ms");

    @Override
    public PageResult<RepoSyncLogPageVO> pageLogs(RepoSyncLogPageQuery query) {
        // 时间参数在 SQL 里是与 DATETIME 直接比较的字符串，只给到日期时必须补齐时分秒：
        // 否则 endTime=2026-09-21 会被 MySQL 当成 2026-09-21 00:00:00，把当天整天的日志静默漏掉。
        // 接口 2-09 走的是同一条规则（见 RepoCommitServiceImpl.pageMyCommits）。
        normalizeTimeRange(query);

        Page<RepoSyncLogPageVO> page = RepoPageUtils.of(query);
        String orderByClause = RepoSortUtils.resolveClause(query.getOrderBy(), SORT_FIELDS);
        IPage<RepoSyncLogPageVO> result = baseMapper.selectLogPage(page, query, orderByClause);
        return PageResult.of(result);
    }

    /**
     * 把只给到日期的起止时间补齐为完整时间点，起始取 00:00:00、截止取 23:59:59。
     *
     * <p>已经带时分秒的、解析不了的一律原样保留——参数是否合法由接口 2-09 那样的
     * 显式校验负责，这里只做格式补齐，不承担报错职责。
     */
    private void normalizeTimeRange(RepoSyncLogPageQuery query) {
        LocalDateTime begin = RepoDateUtils.parseDateTime(query.getBeginTime(), false);
        if (begin != null) {
            query.setBeginTime(RepoDateUtils.format(begin));
        }
        LocalDateTime end = RepoDateUtils.parseDateTime(query.getEndTime(), true);
        if (end != null) {
            query.setEndTime(RepoDateUtils.format(end));
        }
    }
}
