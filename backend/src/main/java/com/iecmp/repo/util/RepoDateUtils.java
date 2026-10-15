package com.iecmp.repo.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;

/**
 * 模块二日期工具：对外时间字符串的解析与格式化，以及趋势图的周期分桶。
 *
 * <p>接口 2-10 / 2-15 的 {@code date} 标签与分桶规则集中在这里，保证「按日 / 按周 / 按月」
 * 三种粒度下前端拿到的标签格式一致，且区间内没有提交的时间点也会补 0（折线图不断点）。
 */
public final class RepoDateUtils {

    /** 对外返回的时间格式，与全局 Jackson 配置一致。 */
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("yyyy-MM");

    /** 粒度：按日。 */
    public static final int GRANULARITY_DAY = 1;

    /** 粒度：按周。 */
    public static final int GRANULARITY_WEEK = 2;

    /** 粒度：按月。 */
    public static final int GRANULARITY_MONTH = 3;

    /** ISO 周字段（周一为一周之始，与 {@code yyyy-第w周} 的直觉一致）。 */
    private static final WeekFields ISO_WEEK = WeekFields.ISO;

    private RepoDateUtils() {
    }

    /** 格式化为 {@code yyyy-MM-dd HH:mm:ss}，入参为 null 时返回 null。 */
    public static String format(LocalDateTime time) {
        return time == null ? null : DATE_TIME.format(time);
    }

    /** 格式化为 {@code yyyy-MM-dd}，入参为 null 时返回 null。 */
    public static String formatDate(LocalDate date) {
        return date == null ? null : DATE.format(date);
    }

    /**
     * 解析日期，兼容 {@code yyyy-MM-dd} 与 {@code yyyy-MM-dd HH:mm:ss} 两种写法。
     *
     * @return 解析失败返回 null，由调用方决定用默认值还是报参数错误
     */
    public static LocalDate parseDate(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String trimmed = text.trim();
        try {
            if (trimmed.length() > 10) {
                return LocalDateTime.parse(trimmed, DATE_TIME).toLocalDate();
            }
            return LocalDate.parse(trimmed, DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 解析时间点，兼容 {@code yyyy-MM-dd} 与 {@code yyyy-MM-dd HH:mm:ss}。
     *
     * @param endOfDay 只给到日期时，是否取当天 23:59:59（结束时间用 true，起始时间用 false）
     */
    public static LocalDateTime parseDateTime(String text, boolean endOfDay) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String trimmed = text.trim();
        try {
            if (trimmed.length() > 10) {
                return LocalDateTime.parse(trimmed, DATE_TIME);
            }
            LocalDate date = LocalDate.parse(trimmed, DATE);
            return endOfDay ? date.atTime(23, 59, 59) : date.atStartOfDay();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** 解析统计年份，非 4 位数字返回 null。 */
    public static Integer parseYear(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String trimmed = text.trim();
        if (trimmed.length() != 4) {
            return null;
        }
        try {
            return Integer.valueOf(trimmed);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 求某一天所属周期的标签。
     *
     * <ul>
     *   <li>按日：{@code yyyy-MM-dd}</li>
     *   <li>按周：{@code yyyy-第w周}，年份取 ISO 周所属年（跨年周不会被拆到两个年份）</li>
     *   <li>按月：{@code yyyy-MM}</li>
     * </ul>
     */
    public static String periodLabel(LocalDate date, int granularity) {
        if (date == null) {
            return null;
        }
        return switch (normalizeGranularity(granularity)) {
            case GRANULARITY_WEEK -> date.get(ISO_WEEK.weekBasedYear()) + "-第" + date.get(ISO_WEEK.weekOfWeekBasedYear()) + "周";
            case GRANULARITY_MONTH -> MONTH.format(date);
            default -> DATE.format(date);
        };
    }

    /**
     * 按粒度枚举 [begin, end] 区间内的全部周期标签，用于给没有提交的时间点补 0。
     *
     * <p>按周分桶时，首个标签对应 {@code begin} 所在的那一周（可能从 begin 之前开始），
     * 这是折线图期望的行为——否则第一周会被截断成一个偏小的值。
     */
    public static List<String> periodLabels(LocalDate begin, LocalDate end, int granularity) {
        List<String> labels = new ArrayList<>();
        if (begin == null || end == null || begin.isAfter(end)) {
            return labels;
        }
        int normalized = normalizeGranularity(granularity);
        String previous = null;
        for (LocalDate cursor = begin; !cursor.isAfter(end); cursor = cursor.plusDays(1)) {
            String label = periodLabel(cursor, normalized);
            if (!label.equals(previous)) {
                labels.add(label);
                previous = label;
            }
        }
        return labels;
    }

    /** 把粒度参数收敛到 1 / 2 / 3，非法值按「按日」处理。 */
    public static int normalizeGranularity(Integer granularity) {
        if (granularity == null) {
            return GRANULARITY_DAY;
        }
        return switch (granularity) {
            case GRANULARITY_WEEK -> GRANULARITY_WEEK;
            case GRANULARITY_MONTH -> GRANULARITY_MONTH;
            default -> GRANULARITY_DAY;
        };
    }
}
