package com.iecmp.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务：资产借用逾期检查（架构文档 4.6 节）。
 *
 * <p>频率：每日 1 次。把超过 {@code expect_return_time} 仍未归还、
 * 且状态为「已通过」（{@code asset_borrow.status = 1}）的记录置为「已逾期」（{@code status = 4}）。
 */
@Slf4j
@Component
public class AssetOverdueTask {

    /** 每日 02:00 执行。 */
    @Scheduled(cron = "0 0 2 * * ?")
    public void markOverdue() {
        // TODO 模块四：注入 AssetBorrowService 后实现（仅更新状态为已通过的记录）
        log.info("[骨架] AssetOverdueTask.markOverdue 尚未实现");
    }
}
