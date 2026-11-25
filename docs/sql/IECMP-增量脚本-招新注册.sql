-- ============================================================================
--  IECMP 创业中心管理平台 · 增量脚本：招新报名与邮箱注册码
-- ----------------------------------------------------------------------------
--  适用环境 ：已按 V1.1 建库、不能整库重建的环境
--  执行顺序 ：1) 本脚本 → 2) 重启后端
--  幂等说明 ：`ALTER TABLE ... ADD COLUMN` 不具备幂等性，重复执行会报
--             Duplicate column；其余语句均为 INSERT IGNORE，可反复执行。
--  对应文档 ：IECMP-数据库设计.md V1.3、IECMP-接口开发文档-v1.6.md V1.7（1-30 ~ 1-35）
-- ============================================================================

SET NAMES utf8mb4;

USE `iecmp`;


-- ----------------------------------------------------------------------------
--  1. 用户表新增「班级」字段
-- ----------------------------------------------------------------------------
ALTER TABLE `sys_user`
  ADD COLUMN `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级' AFTER `grade`;


-- ----------------------------------------------------------------------------
--  2. 招新报名表
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `sys_recruit_apply`;
CREATE TABLE `sys_recruit_apply` (
  `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_no`         VARCHAR(20)           DEFAULT NULL   COMMENT '学号，可选',
  `real_name`          VARCHAR(32)  NOT NULL                COMMENT '姓名',
  `grade`              VARCHAR(10)  NOT NULL                COMMENT '年级',
  `class_name`         VARCHAR(50)  NOT NULL                COMMENT '班级',
  `email`              VARCHAR(64)  NOT NULL                COMMENT '邮箱（小写）',
  `phone`              VARCHAR(20)           DEFAULT NULL   COMMENT '手机号',
  `intro`              VARCHAR(500)          DEFAULT NULL   COMMENT '个人简介',
  `status`             TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0 待考核 1 已通过待发码 2 未通过 3 已发码 4 已注册 5 注册码已过期',
  `score`              DECIMAL(5,2)          DEFAULT NULL   COMMENT '考核成绩',
  `assessment_remark`  VARCHAR(255)          DEFAULT NULL   COMMENT '成绩备注',
  `assessment_user_id` BIGINT                DEFAULT NULL   COMMENT '录入成绩的管理员 ID',
  `assessment_time`    DATETIME              DEFAULT NULL   COMMENT '录入成绩时间',
  `code_hash`          CHAR(64)              DEFAULT NULL   COMMENT '注册码 HMAC-SHA256(code+pepper)',
  `code_expire_time`   DATETIME              DEFAULT NULL   COMMENT '注册码过期时间',
  `code_create_time`   DATETIME              DEFAULT NULL   COMMENT '注册码生成时间',
  `code_sent_time`     DATETIME              DEFAULT NULL   COMMENT '最近发信时间',
  `code_send_count`    INT          NOT NULL DEFAULT 0      COMMENT '累计发信次数',
  `code_attempts`      INT          NOT NULL DEFAULT 0      COMMENT '连续输错次数',
  `create_user_id`     BIGINT                DEFAULT NULL   COMMENT '注册成功后生成的用户 ID',
  `ip`                 VARCHAR(64)           DEFAULT NULL   COMMENT '报名来源 IP',
  `apply_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `create_by`          BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`          BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`            BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见设计文档 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email` (`email`, `deleted`),
  UNIQUE KEY `uk_student_no` (`student_no`, `deleted`),
  UNIQUE KEY `uk_code_hash` (`code_hash`, `deleted`),
  KEY `idx_status_apply_time` (`status`, `apply_time`),
  KEY `idx_email_status` (`email`, `status`),
  KEY `idx_ip_apply_time` (`ip`, `apply_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='招新报名表';


-- ----------------------------------------------------------------------------
--  3. 新增权限码（超级管理员自动获得；中心负责人默认不含账号体系权限）
-- ----------------------------------------------------------------------------
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `perm_code`)
SELECT r.`id`, t.`code`
FROM `sys_role` r
CROSS JOIN (
  SELECT 'system:recruit:list' AS `code`
  UNION ALL SELECT 'system:recruit:assess'
  UNION ALL SELECT 'system:recruit:send'
) t
WHERE r.`role_code` = 'SUPER_ADMIN';


-- ----------------------------------------------------------------------------
--  4. 新增数据字典：招新报名状态
-- ----------------------------------------------------------------------------
INSERT IGNORE INTO `sys_dict` (`dict_type`, `dict_label`, `dict_value`, `sort_no`, `remark`) VALUES
('recruitApplyStatus','待考核',0,1,NULL),
('recruitApplyStatus','已通过待发码',1,2,NULL),
('recruitApplyStatus','未通过',2,3,NULL),
('recruitApplyStatus','已发码',3,4,NULL),
('recruitApplyStatus','已注册',4,5,NULL),
('recruitApplyStatus','注册码已过期',5,6,NULL);
