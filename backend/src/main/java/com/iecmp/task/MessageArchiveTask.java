package com.iecmp.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务：站内消息归档（架构文档 4.6 节）。
 *
 * <p>频率：每月 1 次。清理 12 个月前的 {@code sys_message}，按 {@code create_time} 范围操作。
 */
@Slf4j
@Component
public class MessageArchiveTask {

    /** 每月 1 日 04:30 执行。 */
    @Scheduled(cron = "0 30 4 1 * ?")
    public void archive() {
        // TODO 模块八：注入 SysMessageService 后实现
        log.info("[骨架] MessageArchiveTask.archive 尚未实现");
    }
}
