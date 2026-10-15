package com.iecmp.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务：操作日志归档（架构文档 4.6 节）。
 *
 * <p>频率：每月 1 次。把 12 个月前的 {@code sys_oper_log} 归档或清理，按 {@code create_time} 范围操作。
 * 视数据量决定是否启用；关闭时只需注释掉 {@code @Scheduled}。
 */
@Slf4j
@Component
public class OperLogArchiveTask {

    /** 每月 1 日 04:00 执行。 */
    @Scheduled(cron = "0 0 4 1 * ?")
    public void archive() {
        // TODO 模块八：注入 SysOperLogService 后实现
        log.info("[骨架] OperLogArchiveTask.archive 尚未实现");
    }
}
