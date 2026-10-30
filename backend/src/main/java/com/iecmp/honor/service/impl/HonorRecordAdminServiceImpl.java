package com.iecmp.honor.service.impl;

import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.ResultCode;
import com.iecmp.honor.dto.HonorRecordAddDTO;
import com.iecmp.honor.dto.HonorRecordUpdateDTO;
import com.iecmp.honor.entity.HonorAlumni;
import com.iecmp.honor.entity.HonorRecord;
import com.iecmp.honor.enums.HonorErrorCode;
import com.iecmp.honor.service.HonorAlumniService;
import com.iecmp.honor.service.HonorRecordAdminService;
import com.iecmp.honor.service.HonorRecordService;
import com.iecmp.honor.vo.HonorRecordCreateVO;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 管理端荣誉记录维护实现。
 */
@Service
@RequiredArgsConstructor
public class HonorRecordAdminServiceImpl implements HonorRecordAdminService {

    private final HonorRecordService honorRecordService;
    private final HonorAlumniService honorAlumniService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HonorRecordCreateVO add(HonorRecordAddDTO dto) {
        requireAlumni(dto.getAlumniId());
        validate(dto.getTitle(), dto.getLevel(), dto.getAwardDate(), dto.getDescription(),
                dto.getCertificateUrl(), dto.getSortNo());

        HonorRecord record = new HonorRecord();
        record.setAlumniId(dto.getAlumniId());
        record.setTitle(dto.getTitle().trim());
        record.setLevel(dto.getLevel() == null ? 4 : dto.getLevel());
        record.setAwardDate(parseDate(dto.getAwardDate()));
        record.setDescription(trimToNull(dto.getDescription()));
        record.setCertificateUrl(trimToNull(dto.getCertificateUrl()));
        record.setSortNo(dto.getSortNo() == null ? 0 : dto.getSortNo());
        if (!honorRecordService.save(record)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "新增失败，请稍后重试");
        }

        HonorRecordCreateVO vo = new HonorRecordCreateVO();
        vo.setRecordId(record.getId());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(HonorRecordUpdateDTO dto) {
        HonorRecord record = dto.getRecordId() == null ? null : honorRecordService.getById(dto.getRecordId());
        if (record == null || !record.getAlumniId().equals(dto.getAlumniId())) {
            throw new BusinessException(HonorErrorCode.E60005.getCode(), HonorErrorCode.E60005.getMsg());
        }
        requireAlumni(dto.getAlumniId());
        validate(dto.getTitle(), dto.getLevel(), dto.getAwardDate(), dto.getDescription(),
                dto.getCertificateUrl(), dto.getSortNo());

        record.setTitle(dto.getTitle().trim());
        if (dto.getLevel() != null) {
            record.setLevel(dto.getLevel());
        }
        record.setAwardDate(parseDate(dto.getAwardDate()));
        record.setDescription(trimToNull(dto.getDescription()));
        record.setCertificateUrl(trimToNull(dto.getCertificateUrl()));
        if (dto.getSortNo() != null) {
            record.setSortNo(dto.getSortNo());
        }
        if (!honorRecordService.updateById(record)) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "修改失败，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        HonorRecord record = id == null ? null : honorRecordService.getById(id);
        if (record == null) {
            throw new BusinessException(HonorErrorCode.E60005.getCode(), HonorErrorCode.E60005.getMsg());
        }
        if (!honorRecordService.removeById(id)) {
            throw new BusinessException(HonorErrorCode.E60005.getCode(), HonorErrorCode.E60005.getMsg());
        }
    }

    private void requireAlumni(Long alumniId) {
        HonorAlumni alumni = alumniId == null ? null : honorAlumniService.getById(alumniId);
        if (alumni == null) {
            throw new BusinessException(HonorErrorCode.E60001.getCode(), HonorErrorCode.E60001.getMsg());
        }
    }

    private void validate(String title, Integer level, String awardDate, String description,
                          String certificateUrl, Integer sortNo) {
        if (title == null || title.trim().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "荣誉名称不能为空");
        }
        if (title.trim().length() > 200) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "荣誉名称长度不能超过 200 个字符");
        }
        if (level != null && (level < 1 || level > 4)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "荣誉级别必须为 1、2、3 或 4");
        }
        parseDate(awardDate);
        if (description != null && description.trim().length() > 500) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "荣誉说明长度不能超过 500 个字符");
        }
        if (certificateUrl != null && certificateUrl.trim().length() > 512) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "证书地址长度不能超过 512 个字符");
        }
        if (sortNo != null && sortNo < 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "排序号不能为负数");
        }
    }

    private static LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "获奖日期格式不正确，应为 yyyy-MM-dd");
        }
    }

    private static String trimToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}