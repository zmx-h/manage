package com.iecmp.commonm.service.impl;

import com.iecmp.common.constant.CacheKeyConstants;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.result.ResultCode;
import com.iecmp.common.util.CaptchaUtil;
import com.iecmp.commonm.service.CommonSystemService;
import com.iecmp.commonm.vo.CaptchaVO;
import com.iecmp.commonm.vo.HealthVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 公共验证码与健康检查实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonSystemServiceImpl implements CommonSystemService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StringRedisTemplate stringRedisTemplate;
    private final DataSource dataSource;

    @Override
    public CaptchaVO generateCaptcha(String ip) {
        String limitKey = CacheKeyConstants.LIMIT_CAPTCHA_PREFIX
                + (StringUtils.hasText(ip) ? ip.trim() : "unknown");
        Long count = stringRedisTemplate.opsForValue().increment(limitKey);
        if (count != null && count == 1L) {
            stringRedisTemplate.expire(limitKey, CacheKeyConstants.LIMIT_CAPTCHA_TTL_SECONDS, TimeUnit.SECONDS);
        }
        if (count != null && count > CacheKeyConstants.CAPTCHA_LIMIT_PER_MINUTE) {
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS);
        }

        CaptchaUtil.Captcha captcha = CaptchaUtil.generate();
        String captchaKey = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(CacheKeyConstants.CAPTCHA_PREFIX + captchaKey,
                captcha.code(), CacheKeyConstants.CAPTCHA_TTL_SECONDS, TimeUnit.SECONDS);

        CaptchaVO vo = new CaptchaVO();
        vo.setCaptchaKey(captchaKey);
        vo.setCaptchaImage(captcha.imageBase64());
        return vo;
    }

    @Override
    public HealthVO health() {
        String mysql = checkMysql();
        String redis = checkRedis();
        HealthVO vo = new HealthVO();
        vo.setStatus("UP".equals(mysql) && "UP".equals(redis) ? "UP" : "DOWN");
        vo.setVersion("1.0.0");
        vo.setTime(LocalDateTime.now().format(DATE_TIME_FORMATTER));
        vo.setMysql(mysql);
        vo.setRedis(redis);
        return vo;
    }

    private String checkMysql() {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute("SELECT 1");
            return "UP";
        } catch (Exception e) {
            log.warn("MySQL 健康检查失败：{}", e.getMessage());
            return "DOWN";
        }
    }

    private String checkRedis() {
        try {
            stringRedisTemplate.hasKey("iecmp:health:probe");
            return "UP";
        } catch (Exception e) {
            log.warn("Redis 健康检查失败：{}", e.getMessage());
            return "DOWN";
        }
    }
}