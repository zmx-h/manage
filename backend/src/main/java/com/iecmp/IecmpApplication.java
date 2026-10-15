package com.iecmp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * IECMP 创业中心管理平台 · 启动类。
 *
 * <p>接口文档：{@code http://localhost:8080/api/swagger-ui/index.html}
 *
 * @author 后端组
 */
@EnableAsync
@EnableScheduling
@MapperScan("com.iecmp.**.mapper")
@SpringBootApplication
public class IecmpApplication {

    public static void main(String[] args) {
        SpringApplication.run(IecmpApplication.class, args);
    }
}
