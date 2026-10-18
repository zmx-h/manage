package com.iecmp.repo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.common.util.LoginUserUtil;
import com.iecmp.repo.entity.RepoProject;
import com.iecmp.repo.enums.ProjectTrackedEnum;
import com.iecmp.repo.enums.RepoErrorCode;
import com.iecmp.repo.mapper.RepoProjectMapper;
import com.iecmp.repo.model.RepoIdCount;
import com.iecmp.repo.query.RepoProjectPageQuery;
import com.iecmp.repo.service.RepoProjectService;
import com.iecmp.repo.util.RepoDateUtils;
import com.iecmp.repo.util.RepoPageUtils;
import com.iecmp.repo.util.RepoSortUtils;
import com.iecmp.repo.vo.RepoProjectPageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仓库业务实现（{@code repo_project}）。
 *
 * @author IECMP 后端组
 */
@Slf4j
@Service
public class RepoProjectServiceImpl extends ServiceImpl<RepoProjectMapper, RepoProject>
        implements RepoProjectService {

    /**
     * 接口 2-07 允许排序的字段白名单。
     *
     * <p>这里用的是<b>不带表别名</b>的列名——查询是 {@code repo_project} 单表，
     * 加别名反而会让 MyBatis-Plus 生成的 SQL 找不到列。
     */
    private static final Map<String, String> SORT_FIELDS = Map.of(
            "repo_name", "repo_name",
            "star_count", "star_count",
            "fork_count", "fork_count",
            "last_push_time", "last_push_time",
            "create_time", "create_time");

    @Override
    public PageResult<RepoProjectPageVO> pageMyProjects(RepoProjectPageQuery query) {
        Long userId = LoginUserUtil.getUserId();

        QueryWrapper<RepoProject> wrapper = new QueryWrapper<>();
        // 只查自己的仓库：user_id 走 idx_user_create_time，不依赖前端传参
        wrapper.eq("user_id", userId);
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(w -> w.like("repo_name", keyword).or().like("owner_name", keyword));
        }
        if (StringUtils.hasText(query.getPlatform())) {
            wrapper.eq("platform", query.getPlatform().trim());
        }
        if (query.getTracked() != null) {
            wrapper.eq("tracked", query.getTracked());
        }
        if (query.getAccountId() != null) {
            wrapper.eq("account_id", query.getAccountId());
        }

        RepoSortUtils.Sort sort = RepoSortUtils.resolve(query.getOrderBy(), SORT_FIELDS);
        if (sort == null) {
            // 接口文档 2.4 节：orderBy 默认 create_time desc
            wrapper.orderByDesc("create_time").orderByDesc("id");
        } else if (sort.asc()) {
            wrapper.orderByAsc(sort.column());
        } else {
            wrapper.orderByDesc(sort.column());
        }

        Page<RepoProject> page = RepoPageUtils.of(query);
        IPage<RepoProject> result = baseMapper.selectPage(page, wrapper);

        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getPages(),
                toVoList(result.getRecords()));
    }

    @Override
    public void updateTracked(Long projectId, Integer tracked) {
        Long userId = LoginUserUtil.getUserId();

        // DTO 只校验了非空，取值合法性在这一层兜住，避免脏数据进 tracked 列
        if (ProjectTrackedEnum.of(tracked) == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "tracked 只能为 0 或 1");
        }

        RepoProject project = baseMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(RepoErrorCode.E20004.getCode(), RepoErrorCode.E20004.getMsg());
        }
        if (!userId.equals(project.getUserId())) {
            // 接口文档 8.3.2：错误码 20005 该仓库不在当前账户下
            throw new BusinessException(RepoErrorCode.E20005.getCode(), RepoErrorCode.E20005.getMsg());
        }

        // 只改标记，不动 repo_commit：接口文档 8.3.2 明确关闭统计不删除已采集的提交记录
        RepoProject update = new RepoProject();
        update.setId(projectId);
        update.setTracked(tracked);
        baseMapper.updateById(update);
    }

    /**
     * 转 VO 并回填 {@code commitCount}。
     *
     * <p>接口文档 8.3.1 要求「列表查询时统一批量聚合，避免逐行查询」，
     * 因此先按当前页的仓库 ID 一次分组查出计数，再在内存里逐行填。
     */
    private List<RepoProjectPageVO> toVoList(List<RepoProject> records) {
        Map<Long, Integer> commitCounts = countCommits(records);
        return records.stream().map(project -> {
            RepoProjectPageVO vo = new RepoProjectPageVO();
            vo.setProjectId(project.getId());
            vo.setRepoName(project.getRepoName());
            vo.setRepoUrl(project.getRepoUrl());
            vo.setOwnerName(project.getOwnerName());
            vo.setLanguage(project.getLanguage());
            vo.setDefaultBranch(project.getDefaultBranch());
            vo.setStarCount(project.getStarCount());
            vo.setForkCount(project.getForkCount());
            vo.setLastPushTime(RepoDateUtils.format(project.getLastPushTime()));
            vo.setTracked(project.getTracked());
            vo.setSyncStatus(project.getSyncStatus());
            vo.setCommitCount(commitCounts.getOrDefault(project.getId(), 0));
            return vo;
        }).toList();
    }

    /** 当前页仓库的提交数：空页直接跳过查询，避免拼出 {@code IN ()} 这种非法 SQL。 */
    private Map<Long, Integer> countCommits(List<RepoProject> records) {
        if (records.isEmpty()) {
            return Map.of();
        }
        List<Long> projectIds = records.stream().map(RepoProject::getId).toList();
        List<RepoIdCount> counts = baseMapper.selectCommitCounts(projectIds);
        Map<Long, Integer> map = new HashMap<>(counts.size() * 2);
        for (RepoIdCount item : counts) {
            map.put(item.getId(), item.getCount());
        }
        return map;
    }
}
