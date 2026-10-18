package com.iecmp.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis-Plus 配置。
 *
 * <p>插件顺序要求：<b>分页插件必须放在其它插件之前</b>，否则会失效。
 * <ul>
 *   <li>{@code PaginationInnerInterceptor}：物理分页，单页上限 100 条；</li>
 *   <li>{@code OptimisticLockerInnerInterceptor}：{@code budget_period}、{@code seat_info}
 *       两表的 {@code version} 字段乐观锁；</li>
 *   <li>{@code BlockAttackInnerInterceptor}：拦截全表更新 / 删除，防止误操作。</li>
 * </ul>
 */
@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig {

    private static final long MAX_PAGE_SIZE = 100L;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        pagination.setMaxLimit(MAX_PAGE_SIZE);
        interceptor.addInnerInterceptor(pagination);
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }
}
