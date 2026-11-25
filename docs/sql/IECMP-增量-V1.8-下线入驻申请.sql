-- ============================================================================
-- IECMP 增量脚本 V1.8：下线入驻申请通道
-- 适用版本：已执行 V1.7（含 sys_register_apply）的存量数据库
-- 执行说明：本脚本只做清理，可重复执行；新库直接执行最新建表脚本即可
-- ============================================================================

USE `iecmp`;

-- 1. 删除注册申请表
DROP TABLE IF EXISTS `sys_register_apply`;

-- 2. 回收入驻申请相关权限码
DELETE FROM `sys_role_permission`
WHERE `perm_code` IN ('system:register:list', 'system:register:audit');

-- 3. 删除入驻申请状态字典
DELETE FROM `sys_dict`
WHERE `dict_type` = 'registerApplyStatus';

-- 4. 自检
SELECT COUNT(*) AS table_count
FROM information_schema.tables
WHERE table_schema = 'iecmp';

SELECT COUNT(*) AS register_perm_count
FROM `sys_role_permission`
WHERE `perm_code` IN ('system:register:list', 'system:register:audit');

SELECT COUNT(*) AS register_dict_count
FROM `sys_dict`
WHERE `dict_type` = 'registerApplyStatus';
