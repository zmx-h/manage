package com.iecmp.commonm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecmp.common.constant.CacheKeyConstants;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.ResultCode;
import com.iecmp.commonm.entity.SysDict;
import com.iecmp.commonm.mapper.SysDictMapper;
import com.iecmp.commonm.service.SysDictService;
import com.iecmp.commonm.vo.DictDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/** 数据字典业务实现（{@code sys_dict}）。 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict>
        implements SysDictService {

    private static final TypeReference<List<DictDetailVO>> TYPE = new TypeReference<>() { };

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<DictDetailVO> listByType(String type) {
        if (!StringUtils.hasText(type) || type.length() > 64) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "字典类型不合法");
        }
        String normalized = type.trim();
        String cacheKey = CacheKeyConstants.DICT_PREFIX + normalized;
        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (StringUtils.hasText(cached)) {
                return objectMapper.readValue(cached, TYPE);
            }
        } catch (Exception e) {
            log.warn("读取字典缓存失败，回退数据库: {}", normalized, e);
        }

        List<DictDetailVO> result = list(Wrappers.<SysDict>lambdaQuery()
                .eq(SysDict::getDictType, normalized)
                .eq(SysDict::getStatus, 1)
                .orderByAsc(SysDict::getSortNo)
                .orderByAsc(SysDict::getId))
                .stream()
                .map(item -> {
                    DictDetailVO vo = new DictDetailVO();
                    vo.setLabel(item.getDictLabel());
                    vo.setValue(item.getDictValue());
                    return vo;
                })
                .toList();

        try {
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(result),
                    CacheKeyConstants.DICT_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("写入字典缓存失败: {}", normalized, e);
        }
        return result;
    }
}