package com.iecmp.common.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询公共参数（接口文档 2.4 节）。
 *
 * <p>所有分页接口的查询对象都继承本类，各接口只需声明自身额外的筛选条件。
 * 注意 {@code orderBy} 的字段白名单由各接口自行校验，禁止直接拼接到 SQL。
 */
@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 页码，从 1 开始。 */
    @Schema(description = "页码，从 1 开始", example = "1", defaultValue = "1")
    private Integer pageNum = 1;

    /** 每页条数，范围 1~100。 */
    @Schema(description = "每页条数，范围 1~100", example = "10", defaultValue = "10")
    private Integer pageSize = 10;

    /** 模糊搜索关键字，匹配字段见各接口说明。 */
    @Schema(description = "模糊搜索关键字，匹配字段见各接口说明")
    private String keyword;

    /** 排序，格式 {字段名} {asc|desc}，字段白名单由各接口限制。 */
    @Schema(description = "排序，格式 {字段名} {asc|desc}", example = "create_time desc")
    private String orderBy;

    /** 页码兜底：null、0、负数统一按第 1 页处理。 */
    public Integer getPageNum() {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    /** 每页条数兜底：null 取 10，越界值收敛到 1~100。 */
    public Integer getPageSize() {
        if (pageSize == null) {
            return 10;
        }
        return Math.min(Math.max(pageSize, 1), 100);
    }
}
