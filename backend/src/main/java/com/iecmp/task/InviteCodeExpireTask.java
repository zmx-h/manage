package com.iecmp.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务：邀请码过期清理（架构文档 4.6 节）。
 *
 * <p>频率：每日 1 次。把 {@code sys_invite_code} 中 {@code status = 1} 且
 * {@code expire_time < now()} 的记录置为「已作废」（{@code status = 0}）。
 */
@Slf4j
@Component
public class InviteCodeExpireTask {

    /** 每日 03:00 执行。 */
    @Scheduled(cron = "0 0 3 * * ?")
    public void expireCodes() {
        // TODO 模块一：注入 SysInviteCodeService 后实现
        log.info("[骨架] InviteCodeExpireTask.expireCodes 尚未实现");
    }
}
