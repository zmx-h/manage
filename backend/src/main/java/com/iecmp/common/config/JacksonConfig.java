package com.iecmp.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * JSON 序列化配置（接口文档 2.5 节数据格式规范）。
 *
 * <p>日期时间统一 {@code yyyy-MM-dd HH:mm:ss}、日期统一 {@code yyyy-MM-dd}、时区 GMT+8。
 * 全局时间格式已在 application.yml 中配置，此处只补充 {@code LocalDate} 的自定义格式。
 *
 * <p><b>注意</b>：主键为自增 BIGINT，JSON 以 number 返回（接口文档 2.5 节）。
 * 若将来 ID 超过 JS 安全整数范围，再启用下方 {@link ToStringSerializer}（改为字符串返回），
 * 届时属于破坏性变更，需同步通知前端。
 */
@Configuration
public class JacksonConfig {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME))
                .serializerByType(LocalDate.class, new LocalDateSerializer(DATE))
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME))
                .deserializerByType(LocalDate.class, new LocalDateDeserializer(DATE));
    }

    /** 备用序列化器：需要把 Long 主键序列化为字符串时启用。 */
    public static ToStringSerializer longToStringSerializer() {
        return ToStringSerializer.instance;
    }

    /** 备用 ObjectMapper，供异步任务或三方接口调用复用同一套日期格式。 */
    public static ObjectMapper newObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        return mapper;
    }
}
