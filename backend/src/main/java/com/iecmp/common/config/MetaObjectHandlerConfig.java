package com.iecmp.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.iecmp.common.util.LoginUserUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 审计字段自动填充：{@code create_by / create_time / update_by / update_time}。
 *
 * <p>{@code deleted} 由 {@code @TableLogic} 维护，不在此处填充；
 * 定时任务等无登录用户的场景下，操作人填充为 {@code null}。
 */
@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 插入时，自动填充创建时间和更新时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 插入时，自动填充创建人和更新人
        Long userId = null;
        try {
            userId = LoginUserUtil.getUserId();
        } catch (Exception e) {
            // 忽略未登录时的异常（例如定时任务、注册接口）
        }

        if (userId != null) {
            this.strictInsertFill(metaObject, "createBy", Long.class, userId);
            this.strictInsertFill(metaObject, "updateBy", Long.class, userId);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时，自动填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 更新时，自动填充更新人
        Long userId = null;
        try {
            userId = LoginUserUtil.getUserId();
        } catch (Exception e) {
            // 忽略未登录时的异常
        }

        if (userId != null) {
            this.strictUpdateFill(metaObject, "updateBy", Long.class, userId);
        }
    }
}