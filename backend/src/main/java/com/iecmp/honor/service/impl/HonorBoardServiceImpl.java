package com.iecmp.honor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.honor.entity.HonorAlumni;
import com.iecmp.honor.entity.HonorRecord;
import com.iecmp.honor.enums.HonorErrorCode;
import com.iecmp.honor.query.HonorAlumniHighlightQuery;
import com.iecmp.honor.query.UserHonorAlumniPageQuery;
import com.iecmp.honor.service.HonorAlumniService;
import com.iecmp.honor.service.HonorBoardService;
import com.iecmp.honor.service.HonorRecordService;
import com.iecmp.honor.vo.HonorAlumniDetailVO;
import com.iecmp.honor.vo.HonorAlumniHighlightVO;
import com.iecmp.honor.vo.HonorStatVO;
import com.iecmp.honor.vo.UserHonorAlumniPageVO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 学长学姐展板前台查询实现。
 */
@Service
@RequiredArgsConstructor
public class HonorBoardServiceImpl implements HonorBoardService {

    private static final int BRIEF_SUMMARY_LENGTH = 100;
    private static final int DEFAULT_HIGHLIGHT_LIMIT = 6;
    private static final int MAX_HIGHLIGHT_LIMIT = 20;

    private final HonorAlumniService honorAlumniService;
    private final HonorRecordService honorRecordService;

    @Override
    public PageResult<UserHonorAlumniPageVO> page(UserHonorAlumniPageQuery query) {
        UserHonorAlumniPageQuery q = query == null ? new UserHonorAlumniPageQuery() : query;
        LambdaQueryWrapper<HonorAlumni> wrapper = visibleWrapper();
        wrapper.eq(hasText(q.getGraduationYear()), HonorAlumni::getGraduationYear, trim(q.getGraduationYear()))
                .eq(hasText(q.getMajor()), HonorAlumni::getMajor, trim(q.getMajor()));
        if (hasText(q.getKeyword())) {
            String keyword = trim(q.getKeyword());
            wrapper.and(w -> w.like(HonorAlumni::getName, keyword)
                    .or().like(HonorAlumni::getCompany, keyword)
                    .or().like(HonorAlumni::getPosition, keyword));
        }
        wrapper.orderByAsc(HonorAlumni::getSortNo)
                .orderByDesc(HonorAlumni::getGraduationYear)
                .orderByDesc(HonorAlumni::getId);

        Page<HonorAlumni> page = honorAlumniService.page(
                new Page<>(q.getPageNum(), q.getPageSize()), wrapper);
        Map<Long, Long> honorCounts = countRecordsByAlumni(
                page.getRecords().stream().map(HonorAlumni::getId).toList());
        return PageResult.of(page, alumni -> toPageVO(alumni,
                honorCounts.getOrDefault(alumni.getId(), 0L).intValue()));
    }

    @Override
    public HonorAlumniDetailVO detail(Long id) {
        HonorAlumni alumni = id == null ? null : honorAlumniService.getById(id);
        if (alumni == null || !isVisible(alumni)) {
            throw new BusinessException(HonorErrorCode.E60001.getCode(), HonorErrorCode.E60001.getMsg());
        }
        HonorAlumniDetailVO vo = new HonorAlumniDetailVO();
        vo.setAlumniId(alumni.getId());
        vo.setName(alumni.getName());
        vo.setGender(defaultInt(alumni.getGender()));
        vo.setGraduationYear(alumni.getGraduationYear());
        vo.setMajor(alumni.getMajor());
        vo.setClassName(alumni.getClassName());
        vo.setCompany(alumni.getCompany());
        vo.setPosition(alumni.getPosition());
        vo.setAvatarUrl(alumni.getAvatarUrl());
        vo.setBrief(alumni.getBrief());
        vo.setTags(splitTags(alumni.getTags()));

        List<HonorRecord> records = honorRecordService.list(Wrappers.<HonorRecord>lambdaQuery()
                .eq(HonorRecord::getAlumniId, alumni.getId())
                .orderByDesc(HonorRecord::getAwardDate)
                .orderByAsc(HonorRecord::getSortNo)
                .orderByDesc(HonorRecord::getId));
        vo.setRecords(records.stream().map(this::toRecordVO).toList());
        return vo;
    }

    @Override
    public List<HonorAlumniHighlightVO> highlight(HonorAlumniHighlightQuery query) {
        int limit = query == null || query.getLimit() == null
                ? DEFAULT_HIGHLIGHT_LIMIT
                : Math.min(Math.max(query.getLimit(), 1), MAX_HIGHLIGHT_LIMIT);
        List<HonorAlumni> list = honorAlumniService.list(visibleWrapper()
                .orderByAsc(HonorAlumni::getSortNo)
                .orderByDesc(HonorAlumni::getGraduationYear)
                .orderByDesc(HonorAlumni::getId)
                .last("LIMIT " + limit));
        return list.stream().map(this::toHighlightVO).toList();
    }

    @Override
    public HonorStatVO stat() {
        List<HonorAlumni> visible = honorAlumniService.list(visibleWrapper());
        HonorStatVO vo = new HonorStatVO();
        vo.setAlumniCount(visible.size());
        vo.setCompanyCount((int) visible.stream()
                .map(HonorAlumni::getCompany)
                .filter(HonorBoardServiceImpl::hasText)
                .map(String::trim)
                .distinct()
                .count());

        Map<String, Long> yearCounts = new TreeMap<>(Comparator.reverseOrder());
        visible.stream()
                .map(HonorAlumni::getGraduationYear)
                .filter(HonorBoardServiceImpl::hasText)
                .map(String::trim)
                .forEach(year -> yearCounts.merge(year, 1L, Long::sum));
        vo.setYearStats(yearCounts.entrySet().stream().map(entry -> {
            HonorStatVO.YearStatVO item = new HonorStatVO.YearStatVO();
            item.setYear(entry.getKey());
            item.setCount(String.valueOf(entry.getValue()));
            return item;
        }).toList());

        List<Long> alumniIds = visible.stream().map(HonorAlumni::getId).toList();
        List<HonorRecord> records = alumniIds.isEmpty() ? List.of()
                : honorRecordService.list(Wrappers.<HonorRecord>lambdaQuery()
                        .in(HonorRecord::getAlumniId, alumniIds));
        vo.setHonorCount(records.size());
        Map<Integer, Long> levelCounts = records.stream()
                .map(HonorRecord::getLevel)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(level -> level, TreeMap::new, Collectors.counting()));
        vo.setLevelStats(levelCounts.entrySet().stream().map(entry -> {
            HonorStatVO.LevelStatVO item = new HonorStatVO.LevelStatVO();
            item.setLevel(entry.getKey());
            item.setCount(String.valueOf(entry.getValue()));
            return item;
        }).toList());
        return vo;
    }

    private Map<Long, Long> countRecordsByAlumni(List<Long> alumniIds) {
        if (alumniIds.isEmpty()) {
            return Map.of();
        }
        return honorRecordService.list(Wrappers.<HonorRecord>lambdaQuery()
                        .select(HonorRecord::getAlumniId)
                        .in(HonorRecord::getAlumniId, alumniIds))
                .stream()
                .collect(Collectors.groupingBy(HonorRecord::getAlumniId, Collectors.counting()));
    }

    private LambdaQueryWrapper<HonorAlumni> visibleWrapper() {
        return Wrappers.<HonorAlumni>lambdaQuery()
                .eq(HonorAlumni::getStatus, 1)
                .eq(HonorAlumni::getIsShow, 1);
    }

    private boolean isVisible(HonorAlumni alumni) {
        return Integer.valueOf(1).equals(alumni.getStatus())
                && Integer.valueOf(1).equals(alumni.getIsShow());
    }

    private UserHonorAlumniPageVO toPageVO(HonorAlumni alumni, int honorCount) {
        UserHonorAlumniPageVO vo = new UserHonorAlumniPageVO();
        vo.setAlumniId(alumni.getId());
        vo.setName(alumni.getName());
        vo.setGender(defaultInt(alumni.getGender()));
        vo.setGraduationYear(alumni.getGraduationYear());
        vo.setMajor(alumni.getMajor());
        vo.setClassName(alumni.getClassName());
        vo.setCompany(alumni.getCompany());
        vo.setPosition(alumni.getPosition());
        vo.setAvatarUrl(alumni.getAvatarUrl());
        vo.setBrief(summary(alumni.getBrief()));
        vo.setTags(splitTags(alumni.getTags()));
        vo.setHonorCount(honorCount);
        vo.setSortNo(defaultInt(alumni.getSortNo()));
        return vo;
    }

    private HonorAlumniHighlightVO toHighlightVO(HonorAlumni alumni) {
        HonorAlumniHighlightVO vo = new HonorAlumniHighlightVO();
        vo.setAlumniId(alumni.getId());
        vo.setName(alumni.getName());
        vo.setGraduationYear(alumni.getGraduationYear());
        vo.setMajor(alumni.getMajor());
        vo.setCompany(alumni.getCompany());
        vo.setPosition(alumni.getPosition());
        vo.setAvatarUrl(alumni.getAvatarUrl());
        vo.setBrief(summary(alumni.getBrief()));
        return vo;
    }

    private HonorAlumniDetailVO.RecordVO toRecordVO(HonorRecord record) {
        HonorAlumniDetailVO.RecordVO vo = new HonorAlumniDetailVO.RecordVO();
        vo.setRecordId(record.getId());
        vo.setTitle(record.getTitle());
        vo.setLevel(record.getLevel());
        vo.setAwardDate(record.getAwardDate() == null ? null : record.getAwardDate().toString());
        vo.setDescription(record.getDescription());
        vo.setCertificateUrl(record.getCertificateUrl());
        return vo;
    }

    private static List<String> splitTags(String tags) {
        if (!hasText(tags)) {
            return List.of();
        }
        Set<String> values = new LinkedHashSet<>();
        Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(HonorBoardServiceImpl::hasText)
                .forEach(values::add);
        return new ArrayList<>(values);
    }

    private static String summary(String text) {
        if (!hasText(text)) {
            return null;
        }
        String value = text.trim();
        return value.length() <= BRIEF_SUMMARY_LENGTH
                ? value : value.substring(0, BRIEF_SUMMARY_LENGTH);
    }

    private static int defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}