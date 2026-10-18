package com.iecmp.repo.util;

import java.util.Locale;
import java.util.Map;

/**
 * 分页通用参数 {@code orderBy} 的解析与白名单校验（接口文档 2.4 节）。
 *
 * <p>接口文档把 {@code orderBy} 定义为「{@code {字段名} {asc|desc}}，各接口限制允许排序的字段白名单」，
 * 所以这里是唯一允许把用户输入拼进 SQL 的地方。做法是：
 * <ol>
 *   <li>只接受白名单里的字段名，映射到代码里写死的列名（含表别名），用户输入永远不会成为 SQL 片段；</li>
 *   <li>方向只认 {@code asc}/{@code desc}，其余一律回退到默认排序；</li>
 *   <li>解析失败不报错，静默用默认排序——排序写错不该让整个列表接口 400。</li>
 * </ol>
 *
 * <p>即便如此，把解析结果传给 XML 时仍走 {@code ${}}（列名无法用 {@code #{}} 占位），
 * 因此<b>务必</b>保证传入的字符串只可能来自本类产出的片段。
 */
public final class RepoSortUtils {

    private RepoSortUtils() {
    }

    /**
     * 解析排序方向。
     *
     * @param orderBy      原始参数，如 {@code last_push_time desc}
     * @param allowedFields 字段名 → 列名（可带表别名）的白名单
     * @return 合法的「列名 + 方向」片段；无法识别时返回 {@code null}，由调用方用默认排序
     */
    public static String resolveClause(String orderBy, Map<String, String> allowedFields) {
        if (orderBy == null || orderBy.isBlank()) {
            return null;
        }
        String[] parts = orderBy.trim().split("\\s+");
        if (parts.length == 0 || parts.length > 2) {
            return null;
        }
        String column = allowedFields.get(parts[0].trim().toLowerCase(Locale.ROOT));
        if (column == null) {
            return null;
        }
        boolean asc = true;
        if (parts.length == 2) {
            String direction = parts[1].trim().toLowerCase(Locale.ROOT);
            if ("desc".equals(direction)) {
                asc = false;
            } else if (!"asc".equals(direction)) {
                return null;
            }
        }
        return column + (asc ? " ASC" : " DESC");
    }

    /**
     * 解析排序，供 {@code QueryWrapper} 使用（列名由调用方从同一份白名单里取）。
     *
     * @return 合法时返回 {@code [列名, 方向]}；无法识别时返回 {@code null}
     */
    public static Sort resolve(String orderBy, Map<String, String> allowedFields) {
        String clause = resolveClause(orderBy, allowedFields);
        if (clause == null) {
            return null;
        }
        int split = clause.lastIndexOf(' ');
        return new Sort(clause.substring(0, split), clause.endsWith("ASC"));
    }

    /** 一条已校验的排序：列名一定来自白名单，方向一定是 ASC / DESC。 */
    public record Sort(String column, boolean asc) {
    }
}
