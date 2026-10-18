package com.iecmp.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.asset.dto.AssetRecordAddDTO;
import com.iecmp.asset.entity.AssetInfo;
import com.iecmp.asset.entity.AssetRecord;
import com.iecmp.asset.enums.AssetErrorCode;
import com.iecmp.asset.enums.AssetRecordTypeEnum;
import com.iecmp.asset.mapper.AssetInfoMapper;
import com.iecmp.asset.mapper.AssetRecordMapper;
import com.iecmp.asset.query.AssetRecordPageQuery;
import com.iecmp.asset.service.AssetRecordService;
import com.iecmp.asset.vo.AssetRecordCreateVO;
import com.iecmp.asset.vo.AssetRecordPageVO;
import com.iecmp.common.context.UserContext;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.PageResult;
import com.iecmp.common.result.ResultCode;
import com.iecmp.system.entity.SysUser;
import com.iecmp.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 资产变动流水业务实现（{@code asset_record}）。
 *
 * @author IECMP 后端组
 */
@Service
@RequiredArgsConstructor
public class AssetRecordServiceImpl extends ServiceImpl<AssetRecordMapper, AssetRecord>
        implements AssetRecordService {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final AssetInfoMapper assetInfoMapper;
    private final SysUserService sysUserService;

    @Override
    public AssetRecordCreateVO createRecord(AssetRecordAddDTO dto) {
        if (AssetRecordTypeEnum.of(dto.getRecordType()) == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                    "变动类型只能为 1~7 之间的有效值");
        }
        AssetInfo asset = assetInfoMapper.selectById(dto.getAssetId());
        if (asset == null) {
            throw new BusinessException(AssetErrorCode.E40001.getCode(), AssetErrorCode.E40001.getMsg());
        }

        AssetRecord entity = new AssetRecord();
        entity.setAssetId(dto.getAssetId());
        entity.setRecordType(dto.getRecordType());
        entity.setFromUserId(dto.getFromUserId());
        entity.setToUserId(dto.getToUserId());
        entity.setFromLocation(dto.getFromLocation());
        entity.setToLocation(dto.getToLocation());
        entity.setRecordTime(parseDateTime(dto.getRecordTime(), LocalDateTime.now()));
        entity.setOperatorId(UserContext.getUserIdOrDefault());
        entity.setRemark(dto.getRemark());
        this.save(entity);

        AssetRecordCreateVO vo = new AssetRecordCreateVO();
        vo.setRecordId(entity.getId());
        return vo;
    }

    @Override
    public PageResult<AssetRecordPageVO> pageRecords(AssetRecordPageQuery query) {
        if (query.getRecordType() != null && AssetRecordTypeEnum.of(query.getRecordType()) == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                    "变动类型只能为 1~7 之间的有效值");
        }

        LambdaQueryWrapper<AssetRecord> wrapper = Wrappers.lambdaQuery();
        if (query.getAssetId() != null) wrapper.eq(AssetRecord::getAssetId, query.getAssetId());
        if (query.getRecordType() != null) wrapper.eq(AssetRecord::getRecordType, query.getRecordType());
        if (query.getOperatorId() != null) wrapper.eq(AssetRecord::getOperatorId, query.getOperatorId());
        if (query.getBeginTime() != null && !query.getBeginTime().isBlank()) {
            wrapper.ge(AssetRecord::getRecordTime, parseDateTime(query.getBeginTime(), null));
        }
        if (query.getEndTime() != null && !query.getEndTime().isBlank()) {
            wrapper.le(AssetRecord::getRecordTime, parseEndDateTime(query.getEndTime()));
        }
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            String keyword = query.getKeyword().trim();
            if (keyword.length() > 100) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "关键字长度不能超过 100 个字符");
            }
            wrapper.apply("EXISTS (SELECT 1 FROM asset_info ai "
                    + "WHERE ai.id = asset_record.asset_id AND ai.deleted = 0 "
                    + "AND (ai.asset_no LIKE {0} OR ai.name LIKE {0}))", "%" + keyword + "%");
        }
        wrapper.orderByDesc(AssetRecord::getRecordTime).orderByDesc(AssetRecord::getId);

        Page<AssetRecord> result = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        Set<Long> assetIds = new HashSet<>();
        Set<Long> userIds = new HashSet<>();
        for (AssetRecord record : result.getRecords()) {
            assetIds.add(record.getAssetId());
            if (record.getFromUserId() != null) userIds.add(record.getFromUserId());
            if (record.getToUserId() != null) userIds.add(record.getToUserId());
            if (record.getOperatorId() != null) userIds.add(record.getOperatorId());
        }
        Map<Long, AssetInfo> assetMap = getAssetMap(assetIds);
        Map<Long, String> userNameMap = getUserNameMap(userIds);

        return PageResult.of(result, record -> {
            AssetRecordPageVO vo = new AssetRecordPageVO();
            vo.setRecordId(record.getId());
            vo.setAssetId(record.getAssetId());
            AssetInfo asset = assetMap.get(record.getAssetId());
            vo.setAssetNo(asset == null ? null : asset.getAssetNo());
            vo.setAssetName(asset == null ? null : asset.getName());
            vo.setRecordType(record.getRecordType());
            vo.setFromUserId(record.getFromUserId());
            vo.setFromUserName(userNameMap.get(record.getFromUserId()));
            vo.setToUserId(record.getToUserId());
            vo.setToUserName(userNameMap.get(record.getToUserId()));
            vo.setFromLocation(record.getFromLocation());
            vo.setToLocation(record.getToLocation());
            vo.setRecordTime(record.getRecordTime() == null ? null : record.getRecordTime().format(DATE_TIME_FMT));
            vo.setOperatorId(record.getOperatorId());
            vo.setOperatorName(userNameMap.get(record.getOperatorId()));
            vo.setRemark(record.getRemark());
            return vo;
        });
    }

    private Map<Long, AssetInfo> getAssetMap(Collection<Long> assetIds) {
        if (assetIds == null || assetIds.isEmpty()) return Collections.emptyMap();
        List<AssetInfo> list = assetInfoMapper.selectBatchIds(assetIds);
        Map<Long, AssetInfo> map = new HashMap<>();
        for (AssetInfo asset : list) map.put(asset.getId(), asset);
        return map;
    }

    private Map<Long, String> getUserNameMap(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return Collections.emptyMap();
        List<SysUser> users = sysUserService.listByIds(userIds);
        Map<Long, String> map = new HashMap<>();
        for (SysUser user : users) map.put(user.getId(), user.getRealName());
        return map;
    }

    /** 解析时间：空值使用默认值；只给日期时按当天 00:00:00 处理。 */
    private LocalDateTime parseDateTime(String text, LocalDateTime defaultValue) {
        if (text == null || text.isBlank()) return defaultValue;
        String value = text.trim();
        try {
            return LocalDateTime.parse(value, DATE_TIME_FMT);
        } catch (DateTimeParseException ignore) {
            try {
                return LocalDate.parse(value, DATE_FMT).atStartOfDay();
            } catch (DateTimeParseException e) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "时间格式错误，应为 yyyy-MM-dd HH:mm:ss");
            }
        }
    }

    /** 解析结束时间：只给日期时覆盖当天 23:59:59。 */
    private LocalDateTime parseEndDateTime(String text) {
        if (text == null || text.isBlank()) return null;
        String value = text.trim();
        try {
            return LocalDateTime.parse(value, DATE_TIME_FMT);
        } catch (DateTimeParseException ignore) {
            try {
                return LocalDate.parse(value, DATE_FMT).atTime(LocalTime.of(23, 59, 59));
            } catch (DateTimeParseException e) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "时间格式错误，应为 yyyy-MM-dd HH:mm:ss");
            }
        }
    }
}
