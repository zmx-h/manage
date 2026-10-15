package com.iecmp.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 统一分页返回结构（接口文档 2.4 节）。
 *
 * <p>{@code data} 字段固定包含 {@code total / pageNum / pageSize / pages / list} 五个字段。
 *
 * @param <T> 列表元素类型
 */
@Data
@Schema(description = "分页返回结构")
public class PageResult<T> implements Serializable {

    @Schema(description = "总记录数", example = "42")
    private Long total;

    @Schema(description = "当前页码", example = "1")
    private Long pageNum;

    @Schema(description = "每页条数", example = "10")
    private Long pageSize;

    @Schema(description = "总页数", example = "5")
    private Long pages;

    @Schema(description = "当前页数据列表")
    private List<T> list;

    public PageResult() {
        this.list = new ArrayList<>();
    }

    public PageResult(Long total, Long pageNum, Long pageSize, Long pages, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pages;
        this.list = list == null ? new ArrayList<>() : list;
    }

    /** 由 MyBatis-Plus 分页对象直接转换。 */
    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(page.getTotal(), page.getCurrent(), page.getSize(), page.getPages(), page.getRecords());
    }

    /** 由 MyBatis-Plus 分页对象 + 自定义映射函数转换（Entity → VO）。 */
    public static <E, T> PageResult<T> of(IPage<E> page, Function<E, T> mapper) {
        List<T> list = page.getRecords().stream().map(mapper).toList();
        return new PageResult<>(page.getTotal(), page.getCurrent(), page.getSize(), page.getPages(), list);
    }

    /** 空分页（如未查询到数据）。 */
    public static <T> PageResult<T> empty(long pageNum, long pageSize) {
        return new PageResult<>(0L, pageNum, pageSize, 0L, new ArrayList<>());
    }
}
