package com.iecmp.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务：值日提醒（架构文档 4.6 节）。
 *
 * <p>频率：每分钟扫描。查 {@code duty_reminder_config}（{@code enabled = 1}），
 * 到点向 {@code sys_message} 写入提醒（{@code msg_type = 1} 值日提醒）。
 *
 * <p>幂等性：以「计划 + 日期 + 接收人」去重，避免重复推送。
 * 第一期只做站内消息（{@code channel = 1}），短信 / 邮件后续再扩展。
 */
@Slf4j
@Component
public class DutyReminderTask {

    /** 每分钟执行。 */
    @Scheduled(cron = "0 * * * * ?")
    public void remind() {
        // TODO 模块七 + 模块八：注入 DutyReminderConfigService / DutyScheduleService / SysMessageService 后实现
        log.debug("[骨架] DutyReminderTask.remind 尚未实现");
    }
}
