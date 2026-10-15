-- ============================================================================
--  IECMP 创业中心管理平台 · 初始数据脚本
-- ----------------------------------------------------------------------------
--  生成来源 ：IECMP-数据库设计.md（V1.2 第 4 章）—— 本文件由文档生成，请勿单独修改
--  数据库   ：MySQL 8.0+ / InnoDB / utf8mb4 / utf8mb4_general_ci
--  前置条件 ：必须先执行 IECMP-数据库建表脚本.sql（36 张表已建好）
--  内容     ：4.1 内置角色 → 4.2 超级管理员账号 → 4.3 权限码与角色授权 → 4.4 数据字典
--  幂等说明 ：全部写入语句使用 INSERT IGNORE，可反复执行；重复执行不会产生
--            重复数据，也不会报唯一键冲突，只会把「新增但缺失」的数据补上。
--            ⚠ 若要「收回」某个已下发的权限码，需手工 DELETE 后再重跑（见 4.3 节说明）。
--  初始账号 ：admin / 123456（BCrypt cost 10，务必首次登录后立即改密）
--             若后端用 jBCrypt（只认 $2a$），把脚本里的 $2b$ 改成 $2a$ 即可。
-- ============================================================================

SET NAMES utf8mb4;

USE `iecmp`;


-- ============================================================================
--  4.1 内置角色
-- ============================================================================

INSERT IGNORE INTO `sys_role` (`role_name`, `role_code`, `data_scope`, `sort_no`, `status`, `remark`) VALUES
('超级管理员', 'SUPER_ADMIN', 1, 1, 1, '系统级配置、角色授权、账号体系维护'),
('中心负责人', 'CENTER_HEAD', 1, 2, 1, '预算管理、采购与支出登记、审核各类申请、分配工位'),
('部长',       'MINISTER',    1, 3, 1, '协助管理本方向事务，如荣誉审核、值日排班'),
('成员',       'MEMBER',      2, 4, 1, '提交申请、查看公示、维护个人信息');

-- ============================================================================
--  4.2 超级管理员账号
-- ============================================================================

INSERT IGNORE INTO `sys_user` (`username`, `password`, `real_name`, `status`, `remark`) VALUES
('admin', '$2b$10$u.S87kivBux0RtJ.cjmIjOUfavRCxB5bFGhwX5jpIMSgm1hd07jBe', '超级管理员', 1, '初始账号，首次登录后请立即修改密码');

INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`)
SELECT u.`id`, r.`id` FROM `sys_user` u, `sys_role` r
WHERE u.`username` = 'admin' AND r.`role_code` = 'SUPER_ADMIN';

-- ============================================================================
--  4.3 权限码与角色授权
-- ============================================================================

DROP TEMPORARY TABLE IF EXISTS `tmp_perm`;
CREATE TEMPORARY TABLE `tmp_perm` (
  `code` VARCHAR(64) NOT NULL PRIMARY KEY,
  `grp`  VARCHAR(32) NOT NULL COMMENT '所属模块'
);

INSERT INTO `tmp_perm` (`code`, `grp`) VALUES
-- 模块一 用户与权限
('system:user:list','system'), ('system:user:query','system'), ('system:user:add','system'),
('system:user:edit','system'), ('system:user:reset','system'), ('system:user:remove','system'),
('system:role:list','system'), ('system:role:add','system'), ('system:role:edit','system'),
('system:role:remove','system'), ('system:role:auth','system'),
('system:register:list','system'), ('system:register:audit','system'),
('system:invite:list','system'), ('system:invite:add','system'), ('system:invite:remove','system'),
('system:log:list','system'),
-- 模块二 代码仓库关联
('repo:account:list','repo'), ('repo:stat:list','repo'), ('repo:sync:list','repo'), ('repo:sync:exec','repo'),
-- 模块三 部门预算
('budget:period:list','budget'), ('budget:period:add','budget'), ('budget:period:edit','budget'),
('budget:period:remove','budget'), ('budget:period:audit','budget'), ('budget:period:public','budget'),
('budget:collect:exec','budget'), ('budget:item:edit','budget'), ('budget:apply:list','budget'),
('budget:apply:audit','budget'), ('budget:expense:add','budget'), ('budget:expense:list','budget'),
('budget:expense:remove','budget'), ('budget:stat:list','budget'),
-- 模块四 部门财产
('asset:info:list','asset'), ('asset:info:query','asset'), ('asset:info:add','asset'),
('asset:info:edit','asset'), ('asset:info:remove','asset'), ('asset:info:assign','asset'),
('asset:info:export','asset'), ('asset:category:edit','asset'), ('asset:record:add','asset'),
('asset:record:list','asset'), ('asset:borrow:list','asset'), ('asset:borrow:audit','asset'),
('asset:borrow:edit','asset'), ('asset:inventory:add','asset'), ('asset:inventory:list','asset'),
('asset:inventory:exec','asset'),
-- 模块五 部门工位
('seat:area:list','seat'), ('seat:area:add','seat'), ('seat:area:edit','seat'),
('seat:area:remove','seat'), ('seat:seat:list','seat'), ('seat:seat:add','seat'), ('seat:seat:edit','seat'),
('seat:seat:remove','seat'), ('seat:seat:assign','seat'), ('seat:apply:list','seat'),
('seat:apply:audit','seat'), ('seat:swap:list','seat'), ('seat:swap:audit','seat'),
-- 模块六 学长学姐展板
('honor:alumni:list','honor'), ('honor:alumni:add','honor'), ('honor:alumni:edit','honor'),
('honor:alumni:remove','honor'), ('honor:alumni:audit','honor'), ('honor:record:edit','honor'),
-- 模块七 值日排班
('duty:plan:list','duty'), ('duty:plan:add','duty'), ('duty:plan:edit','duty'),
('duty:plan:remove','duty'), ('duty:plan:generate','duty'), ('duty:plan:publish','duty'),
('duty:schedule:list','duty'), ('duty:schedule:edit','duty'), ('duty:stat:list','duty'),
('duty:reminder:config','duty'),
-- 模块八 通用支撑
('common:notice:list','common'), ('common:notice:edit','common'), ('common:stat:list','common'),
('common:video:list','common'), ('common:video:edit','common');

-- 超级管理员：全部权限码
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `perm_code`)
SELECT r.`id`, t.`code`
FROM `sys_role` r CROSS JOIN `tmp_perm` t
WHERE r.`role_code` = 'SUPER_ADMIN';

-- 中心负责人：除「账户体系维护与角色授权」外的全部权限
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `perm_code`)
SELECT r.`id`, t.`code`
FROM `sys_role` r CROSS JOIN `tmp_perm` t
WHERE r.`role_code` = 'CENTER_HEAD'
  AND t.`code` NOT IN (
    'system:user:add','system:user:edit','system:user:reset','system:user:remove',
    'system:role:list','system:role:add','system:role:edit','system:role:remove','system:role:auth',
    'system:invite:add','system:invite:remove',
    'budget:collect:exec'
  );

-- 部长：资产、荣誉、值日、公告与看板
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `perm_code`)
SELECT r.`id`, t.`code`
FROM `sys_role` r CROSS JOIN `tmp_perm` t
WHERE r.`role_code` = 'MINISTER'
  AND t.`code` IN (
    'asset:info:list','asset:info:query','asset:borrow:list','asset:borrow:audit','asset:borrow:edit',
    'asset:record:add','asset:record:list','asset:inventory:list','asset:inventory:exec',
    'honor:alumni:list','honor:alumni:add','honor:alumni:edit','honor:alumni:audit','honor:record:edit',
    'duty:plan:list','duty:plan:edit','duty:plan:generate','duty:plan:publish',
    'duty:schedule:list','duty:schedule:edit','duty:stat:list','duty:reminder:config',
    'common:notice:list','common:notice:edit','common:stat:list','common:video:list'
  );

DROP TEMPORARY TABLE IF EXISTS `tmp_perm`;

-- ============================================================================
--  4.4 数据字典
-- ============================================================================

INSERT IGNORE INTO `sys_dict` (`dict_type`, `dict_label`, `dict_value`, `sort_no`, `remark`) VALUES
-- 资产状态
('assetStatus','在用',1,1,NULL), ('assetStatus','闲置',2,2,NULL), ('assetStatus','维修中',3,3,NULL),
('assetStatus','已借出',4,4,NULL), ('assetStatus','已报废',5,5,NULL),
-- 资产分类
('assetCategory','电脑设备',1,1,NULL), ('assetCategory','办公家具',2,2,NULL),
('assetCategory','实验器材',3,3,NULL), ('assetCategory','耗材',4,4,NULL), ('assetCategory','其他',5,5,NULL),
-- 预算科目分类
('budgetCategory','设备采购',1,1,NULL), ('budgetCategory','耗材',2,2,NULL), ('budgetCategory','活动',3,3,NULL),
('budgetCategory','差旅',4,4,NULL), ('budgetCategory','其他',5,5,NULL),
-- 预算周期状态
('budgetPeriodStatus','草稿',0,1,NULL), ('budgetPeriodStatus','执行中',1,2,NULL), ('budgetPeriodStatus','已结束',2,3,NULL),
-- 预算申请状态
('budgetApplyStatus','待审核',0,1,NULL), ('budgetApplyStatus','已通过',1,2,NULL),
('budgetApplyStatus','已驳回',2,3,NULL), ('budgetApplyStatus','已撤销',3,4,NULL),
-- 资产来源
('assetSourceType','采购',1,1,NULL), ('assetSourceType','捐赠',2,2,NULL), ('assetSourceType','调拨',3,3,NULL),
-- 借用状态
('assetBorrowStatus','待审核',0,1,NULL), ('assetBorrowStatus','已通过',1,2,NULL), ('assetBorrowStatus','已驳回',2,3,NULL),
('assetBorrowStatus','已归还',3,4,NULL), ('assetBorrowStatus','已逾期',4,5,NULL),
-- 资产变动类型
('assetRecordType','领用',1,1,NULL), ('assetRecordType','借用',2,2,NULL), ('assetRecordType','归还',3,3,NULL),
('assetRecordType','维修',4,4,NULL), ('assetRecordType','报废',5,5,NULL), ('assetRecordType','调拨',6,6,NULL),
('assetRecordType','盘点',7,7,NULL),
-- 盘点结果
('assetCheckStatus','未盘点',0,1,NULL), ('assetCheckStatus','正常',1,2,NULL),
('assetCheckStatus','盘亏',2,3,NULL), ('assetCheckStatus','位置不符',3,4,NULL),
-- 工位类型
('seatType','工位',1,1,NULL), ('seatType','会议室',2,2,NULL), ('seatType','储物柜',3,3,NULL), ('seatType','其他',4,4,NULL),
-- 工位状态
('seatStatus','空闲',1,1,NULL), ('seatStatus','已占用',2,2,NULL), ('seatStatus','预留',3,3,NULL), ('seatStatus','维修中',4,4,NULL),
-- 座位调换状态
('seatSwapStatus','待对方确认',0,1,NULL), ('seatSwapStatus','对方已同意待审核',1,2,NULL),
('seatSwapStatus','已通过',2,3,NULL), ('seatSwapStatus','已拒绝',3,4,NULL), ('seatSwapStatus','已取消',4,5,NULL),
-- 荣誉级别
('honorLevel','国家级',1,1,NULL), ('honorLevel','省级',2,2,NULL), ('honorLevel','校级',3,3,NULL),
('honorLevel','企业及其他',4,4,NULL),
-- 展板审核状态
('honorAlumniStatus','待审核',0,1,NULL), ('honorAlumniStatus','已通过',1,2,NULL), ('honorAlumniStatus','已驳回',2,3,NULL),
-- 排班方式
('dutyPlanType','按周轮转',1,1,NULL), ('dutyPlanType','分组轮转',2,2,NULL), ('dutyPlanType','手工指定',3,3,NULL),
-- 排班计划状态
('dutyPlanStatus','草稿',0,1,NULL), ('dutyPlanStatus','已发布',1,2,NULL), ('dutyPlanStatus','已结束',2,3,NULL),
-- 值日完成状态
('dutyStatus','未完成',0,1,NULL), ('dutyStatus','已完成',1,2,NULL),
('dutyStatus','请假',2,3,NULL), ('dutyStatus','已换班',3,4,NULL),
-- 公告类型
('noticeType','中心公告',1,1,NULL), ('noticeType','制度',2,2,NULL), ('noticeType','活动',3,3,NULL), ('noticeType','其他',4,4,NULL),
-- 视频业务归属
('videoBizType','首页展示',1,1,NULL), ('videoBizType','学长学姐展板',2,2,NULL), ('videoBizType','其他',3,3,NULL),
-- 站内消息类型
('msgType','值日提醒',1,1,NULL), ('msgType','审核结果',2,2,NULL), ('msgType','系统通知',3,3,NULL),
-- 换班申请状态
('dutySwapStatus','待确认',0,1,NULL), ('dutySwapStatus','已同意',1,2,NULL),
('dutySwapStatus','已拒绝',2,3,NULL), ('dutySwapStatus','已取消',3,4,NULL),
-- 注册申请状态
('registerApplyStatus','待审核',0,1,NULL), ('registerApplyStatus','已通过',1,2,NULL),
('registerApplyStatus','已驳回',2,3,NULL),
-- 公告状态
('noticeStatus','草稿',0,1,NULL), ('noticeStatus','已发布',1,2,NULL), ('noticeStatus','已下架',2,3,NULL);

-- ============================================================================
--  自检（可选执行，仅查询、不写数据）
-- ============================================================================

-- 应返回 36
SELECT COUNT(*) AS table_count FROM information_schema.tables
WHERE table_schema = 'iecmp';

-- 应返回 4 个角色
SELECT role_code, role_name, data_scope FROM sys_role ORDER BY sort_no;

-- SUPER_ADMIN 应为 85
SELECT r.role_code, COUNT(*) AS perm_count
FROM sys_role r JOIN sys_role_permission p ON p.role_id = r.id
WHERE p.deleted = 0
GROUP BY r.role_code
ORDER BY perm_count DESC;

-- 应为 23 个类型、91 条
SELECT COUNT(DISTINCT dict_type) AS type_count, COUNT(*) AS item_count FROM sys_dict WHERE deleted = 0;
