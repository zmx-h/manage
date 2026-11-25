-- 邀请码改为一次性：删除“最多使用次数”，used_count 语义改为 0 未使用 / 1 已使用。
-- 适用：已按旧版建表脚本初始化过的数据库。

ALTER TABLE `sys_invite_code`
    MODIFY COLUMN `used_count` INT NOT NULL DEFAULT 0 COMMENT '使用状态：0 未使用 1 已使用（一次性）';

ALTER TABLE `sys_invite_code`
    DROP COLUMN `max_use_count`;

-- 历史数据归一：任何已使用过的邀请码都置为已使用。
UPDATE `sys_invite_code` SET `used_count` = 1 WHERE `used_count` > 0;
