package com.iecmp.repo.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iecmp.common.base.PageQuery;

/**
 * 分页参数兜底（接口文档 2.4 节）。
 *
 * <p>{@code PageQuery} 的默认值是 1 / 10，但查询参数由前端拼在 URL 上，
 * 传 {@code pageNum=0}、{@code pageSize=9999} 都不会被校验拦住。这里统一收敛：
 * 页码至少为 1，每页条数落在 1~100——项目没有给 {@code PaginationInnerInterceptor}
 * 设 {@code maxLimit}，超限请求必须在这一层挡掉，否则一次请求能把整表拉走。
 */
public final class RepoPageUtils {

    /** 每页条数上限，与接口文档 2.4 节一致。 */
    public static final int MAX_PAGE_SIZE = 100;

    /** 每页条数默认值，与接口文档 2.4 节一致。 */
    public static final int DEFAULT_PAGE_SIZE = 10;

    private RepoPageUtils() {
    }

    /** 由通用分页参数构造 MyBatis-Plus 分页对象，越界值直接夹紧。 */
    public static <T> Page<T> of(PageQuery query) {
        long pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1L : query.getPageNum();
        long pageSize = query.getPageSize() == null ? DEFAULT_PAGE_SIZE : query.getPageSize();
        pageSize = Math.min(Math.max(pageSize, 1L), MAX_PAGE_SIZE);
        return new Page<>(pageNum, pageSize);
    }
}
