-- ============================================================================
--  IECMP 创业中心管理平台 · 数据库建表脚本
-- ----------------------------------------------------------------------------
--  生成来源 ：IECMP-数据库设计.md（V1.2 第 3 章）—— 本文件由文档生成，请勿单独修改；
--            表结构变更须先改文档，再重新生成本文件。
--  数据库   ：MySQL 8.0+ / InnoDB / utf8mb4 / utf8mb4_general_ci
--  内容     ：建库语句 + 36 张表 DDL（模块一 用户与权限 → 模块八 通用支撑）
--  执行顺序 ：1) 本脚本  →  2) IECMP-初始数据.sql
--  幂等说明 ：每张表 DDL 前带 DROP TABLE IF EXISTS，脚本可反复执行，
--            但【会清空已有数据】，仅用于初始化或重建，禁止在生产库随意执行。
--  设计约定 ：1.5 节——逻辑删除不用 0/1，而是把 deleted 置为本行 id，
--            并让 deleted 参与唯一索引，从而允许「删除后同名数据重新插入」。
--            1.6 节——不建物理外键，表与表的顺序无依赖要求。
-- ============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------------------------------------------------------
--  创建数据库
-- ----------------------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS `iecmp`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE `iecmp`;


-- ============================================================================
--  3.1 模块一：用户与权限（6 张）
-- ============================================================================

-- ---- 3.1.1 `sys_user` 用户表 ----

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT           COMMENT '主键',
  `username`        VARCHAR(32)  NOT NULL                          COMMENT '登录账号，4~32 位',
  `password`        VARCHAR(100) NOT NULL                          COMMENT '密码，BCrypt 加盐哈希',
  `real_name`       VARCHAR(32)  NOT NULL                          COMMENT '姓名',
  `student_no`      VARCHAR(20)           DEFAULT NULL             COMMENT '学号，全局唯一',
  `grade`           VARCHAR(10)           DEFAULT NULL             COMMENT '年级，如 2023',
  `major`           VARCHAR(50)           DEFAULT NULL             COMMENT '专业',
  `phone`           VARCHAR(20)           DEFAULT NULL             COMMENT '手机号',
  `email`           VARCHAR(64)           DEFAULT NULL             COMMENT '邮箱',
  `avatar_url`      VARCHAR(512)          DEFAULT NULL             COMMENT '头像地址（OSS）',
  `status`          TINYINT      NOT NULL DEFAULT 1                COMMENT '状态：0 禁用 1 启用',
  `last_login_time` DATETIME              DEFAULT NULL             COMMENT '最近登录时间',
  `remark`          VARCHAR(255)          DEFAULT NULL             COMMENT '备注',
  `create_by`       BIGINT                DEFAULT NULL             COMMENT '创建人 ID',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT                DEFAULT NULL             COMMENT '更新人 ID',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         BIGINT       NOT NULL DEFAULT 0                COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`, `deleted`),
  UNIQUE KEY `uk_student_no` (`student_no`, `deleted`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- ---- 3.1.2 `sys_role` 角色表 ----

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT           COMMENT '主键',
  `role_name`  VARCHAR(32)  NOT NULL                          COMMENT '角色名称，最长 32 字',
  `role_code`  VARCHAR(32)  NOT NULL                          COMMENT '角色编码：SUPER_ADMIN / CENTER_HEAD / MINISTER / MEMBER',
  `data_scope` TINYINT      NOT NULL DEFAULT 2                COMMENT '数据范围：1 全部数据 2 本人数据',
  `sort_no`    INT          NOT NULL DEFAULT 0                COMMENT '排序号，升序',
  `status`     TINYINT      NOT NULL DEFAULT 1                COMMENT '状态：0 停用 1 启用',
  `remark`     VARCHAR(255)          DEFAULT NULL             COMMENT '备注',
  `create_by`  BIGINT                DEFAULT NULL             COMMENT '创建人 ID',
  `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`  BIGINT                DEFAULT NULL             COMMENT '更新人 ID',
  `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`    BIGINT       NOT NULL DEFAULT 0                COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`, `deleted`),
  UNIQUE KEY `uk_role_name` (`role_name`, `deleted`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

-- ---- 3.1.3 `sys_user_role` 用户角色关联表 ----

DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`     BIGINT   NOT NULL                COMMENT '用户 ID',
  `role_id`     BIGINT   NOT NULL                COMMENT '角色 ID',
  `create_by`   BIGINT            DEFAULT NULL   COMMENT '创建人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`   BIGINT            DEFAULT NULL   COMMENT '更新人 ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     BIGINT   NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`, `deleted`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

-- ---- 3.1.4 `sys_role_permission` 角色权限关联表 ----

DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id`     BIGINT      NOT NULL                COMMENT '角色 ID',
  `perm_code`   VARCHAR(64) NOT NULL                COMMENT '权限码，格式 模块:资源:操作，如 system:user:list',
  `create_by`   BIGINT               DEFAULT NULL   COMMENT '创建人 ID',
  `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`   BIGINT               DEFAULT NULL   COMMENT '更新人 ID',
  `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     BIGINT      NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`, `perm_code`, `deleted`),
  KEY `idx_perm_code` (`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限关联表';

-- ---- 3.1.5 `sys_invite_code` 注册邀请码表 ----

DROP TABLE IF EXISTS `sys_invite_code`;
CREATE TABLE `sys_invite_code` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code`          VARCHAR(16)  NOT NULL                COMMENT '邀请码，8 位大写字母数字',
  `max_use_count` INT          NOT NULL DEFAULT 1      COMMENT '可用次数',
  `used_count`    INT          NOT NULL DEFAULT 0      COMMENT '已使用次数',
  `expire_time`   DATETIME              DEFAULT NULL   COMMENT '过期时间，NULL 表示长期有效',
  `status`        TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：0 作废 1 有效',
  `remark`        VARCHAR(255)          DEFAULT NULL   COMMENT '备注，如「2026 秋季纳新」',
  `create_by`     BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`     BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`, `deleted`),
  KEY `idx_status_expire` (`status`, `expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='注册邀请码表';

-- ---- 3.1.6 `sys_register_apply` 注册申请表 ----

DROP TABLE IF EXISTS `sys_register_apply`;
CREATE TABLE `sys_register_apply` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_no`     VARCHAR(20)  NOT NULL                COMMENT '学号，全局唯一',
  `real_name`      VARCHAR(32)  NOT NULL                COMMENT '真实姓名',
  `major`          VARCHAR(50)           DEFAULT NULL   COMMENT '专业',
  `phone`          VARCHAR(20)           DEFAULT NULL   COMMENT '手机号',
  `reason`         VARCHAR(200)          DEFAULT NULL   COMMENT '申请理由，最长 200 字',
  `status`         TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0 待审核 1 已通过 2 已驳回',
  `audit_user_id`  BIGINT                DEFAULT NULL   COMMENT '审核人 ID',
  `audit_time`     DATETIME              DEFAULT NULL   COMMENT '审核时间',
  `audit_remark`   VARCHAR(255)          DEFAULT NULL   COMMENT '审核意见',
  `create_user_id` BIGINT                DEFAULT NULL   COMMENT '通过后生成的用户 ID',
  `ip`             VARCHAR(64)           DEFAULT NULL   COMMENT '提交来源 IP',
  `apply_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `create_by`      BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`      BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_no` (`student_no`, `deleted`),
  KEY `idx_status` (`status`),
  KEY `idx_apply_time` (`apply_time`),
  KEY `idx_ip` (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='注册申请表';

-- ============================================================================
--  3.2 模块二：代码仓库关联（4 张）
-- ============================================================================

-- ---- 3.2.1 `repo_account` 托管账户表 ----

DROP TABLE IF EXISTS `repo_account`;
CREATE TABLE `repo_account` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`        BIGINT       NOT NULL                COMMENT '所属用户 ID',
  `platform`       VARCHAR(16)  NOT NULL                COMMENT '平台：gitee / github',
  `account_name`   VARCHAR(64)  NOT NULL                COMMENT '托管平台账号名',
  `access_token`   VARCHAR(512) NOT NULL                COMMENT '个人访问令牌，AES 加密存储',
  `avatar_url`     VARCHAR(512)          DEFAULT NULL   COMMENT '平台头像地址（直接用平台外链，不转存 OSS）',
  `status`         TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：0 失效 1 有效',
  `sync_status`    TINYINT      NOT NULL DEFAULT 0      COMMENT '采集状态：0 未采集 1 采集中 2 采集正常 3 采集失败',
  `last_sync_time` DATETIME              DEFAULT NULL   COMMENT '最近采集完成时间',
  `bind_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  `create_by`      BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`      BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_platform_account` (`user_id`, `platform`, `account_name`, `deleted`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='托管账户表';

-- ---- 3.2.2 `repo_project` 仓库表 ----

DROP TABLE IF EXISTS `repo_project`;
CREATE TABLE `repo_project` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id`      BIGINT       NOT NULL                COMMENT '所属托管账户 ID',
  `user_id`         BIGINT       NOT NULL                COMMENT '所属用户 ID（冗余，便于按用户统计）',
  `platform`        VARCHAR(16)  NOT NULL                COMMENT '平台：gitee / github',
  `repo_name`       VARCHAR(128) NOT NULL                COMMENT '仓库名称',
  `repo_url`        VARCHAR(512) NOT NULL                COMMENT '仓库地址',
  `owner_name`      VARCHAR(64)           DEFAULT NULL   COMMENT '仓库所有者',
  `language`        VARCHAR(32)           DEFAULT NULL   COMMENT '主要语言',
  `default_branch`  VARCHAR(64)           DEFAULT NULL   COMMENT '默认分支',
  `star_count`      INT          NOT NULL DEFAULT 0      COMMENT 'Star 数',
  `fork_count`      INT          NOT NULL DEFAULT 0      COMMENT 'Fork 数',
  `last_push_time`  DATETIME              DEFAULT NULL   COMMENT '最近推送时间',
  `tracked`         TINYINT      NOT NULL DEFAULT 1      COMMENT '是否纳入统计：0 不统计 1 统计',
  `sync_status`     TINYINT      NOT NULL DEFAULT 0      COMMENT '采集状态：0 未采集 1 采集中 2 采集正常 3 采集失败',
  `create_by`       BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_repo` (`account_id`, `repo_url`, `deleted`),
  KEY `idx_user_create_time` (`user_id`, `create_time`),
  KEY `idx_tracked` (`tracked`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓库表';

-- ---- 3.2.3 `repo_commit` 提交记录表 ----

DROP TABLE IF EXISTS `repo_commit`;
CREATE TABLE `repo_commit` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id`    BIGINT       NOT NULL                COMMENT '所属仓库 ID',
  `user_id`       BIGINT       NOT NULL                COMMENT '所属用户 ID（冗余，便于按用户统计）',
  `commit_sha`    VARCHAR(64)  NOT NULL                COMMENT '提交 SHA',
  `branch`        VARCHAR(128)          DEFAULT NULL   COMMENT '所属分支（统计范围为所有分支）',
  `author_name`   VARCHAR(64)           DEFAULT NULL   COMMENT '提交作者名',
  `author_email`  VARCHAR(128)          DEFAULT NULL   COMMENT '提交作者邮箱',
  `message`       VARCHAR(1024)         DEFAULT NULL   COMMENT '提交信息',
  `commit_time`   DATETIME     NOT NULL                COMMENT '提交时间',
  `add_lines`     INT          NOT NULL DEFAULT 0      COMMENT '新增行数',
  `del_lines`     INT          NOT NULL DEFAULT 0      COMMENT '删除行数',
  `is_merge`      TINYINT      NOT NULL DEFAULT 0      COMMENT '是否合并提交：0 否 1 是',
  `create_by`     BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`     BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_sha` (`project_id`, `commit_sha`, `deleted`),
  KEY `idx_user_commit_time` (`user_id`, `commit_time`),
  KEY `idx_project_commit_time` (`project_id`, `commit_time`),
  KEY `idx_commit_time` (`commit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='提交记录表';

-- ---- 3.2.4 `repo_sync_log` 同步日志表 ----

DROP TABLE IF EXISTS `repo_sync_log`;
CREATE TABLE `repo_sync_log` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id`   BIGINT        NOT NULL                COMMENT '托管账户 ID',
  `user_id`      BIGINT        NOT NULL                COMMENT '所属用户 ID',
  `sync_type`    TINYINT       NOT NULL DEFAULT 1      COMMENT '采集类型：1 定时自动 2 管理员手动补采',
  `sync_result`  TINYINT       NOT NULL DEFAULT 0      COMMENT '采集结果：0 失败 1 成功',
  `commit_count` INT           NOT NULL DEFAULT 0      COMMENT '本次采集到的提交数',
  `cost_ms`      BIGINT        NOT NULL DEFAULT 0      COMMENT '耗时（毫秒）',
  `error_msg`    VARCHAR(1024)          DEFAULT NULL   COMMENT '错误信息',
  `create_by`    BIGINT                 DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`    BIGINT                 DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      BIGINT        NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_account_create` (`account_id`, `create_time`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_sync_result` (`sync_result`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='同步日志表';

-- ============================================================================
--  3.3 模块三：部门预算（4 张）
-- ============================================================================

-- ---- 3.3.1 `budget_period` 预算周期表 ----

DROP TABLE IF EXISTS `budget_period`;
CREATE TABLE `budget_period` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title`        VARCHAR(100)  NOT NULL                COMMENT '周期标题，如「2026 年度预算」',
  `year`         VARCHAR(10)   NOT NULL                COMMENT '所属年度，如 2026',
  `total_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00   COMMENT '预算总额度',
  `used_amount`  DECIMAL(12,2) NOT NULL DEFAULT 0.00   COMMENT '已使用额度（冗余汇总，事务内维护）',
  `status`       TINYINT       NOT NULL DEFAULT 0      COMMENT '状态：0 草稿 1 执行中 2 已结束',
  `start_date`   DATE                   DEFAULT NULL   COMMENT '周期开始日期',
  `end_date`     DATE                   DEFAULT NULL   COMMENT '周期结束日期',
  `is_public`    TINYINT       NOT NULL DEFAULT 0      COMMENT '是否公示：0 不公示 1 公示中',
  `public_time`  DATETIME               DEFAULT NULL   COMMENT '公示时间',
  `description`  VARCHAR(500)           DEFAULT NULL   COMMENT '周期说明',
  `version`      INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
  `create_by`    BIGINT                 DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`    BIGINT                 DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      BIGINT        NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_year` (`year`),
  KEY `idx_public_start` (`is_public`, `start_date`),
  KEY `idx_public_time` (`is_public`, `public_time`),
  KEY `idx_status_public` (`status`, `is_public`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='预算周期表';

-- ---- 3.3.2 `budget_item` 预算科目表 ----

DROP TABLE IF EXISTS `budget_item`;
CREATE TABLE `budget_item` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `period_id`     BIGINT        NOT NULL                COMMENT '所属预算周期 ID',
  `name`          VARCHAR(64)   NOT NULL                COMMENT '科目名称',
  `category`      TINYINT       NOT NULL DEFAULT 5      COMMENT '科目分类：1 设备采购 2 耗材 3 活动 4 差旅 5 其他',
  `budget_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00   COMMENT '科目预算额度',
  `used_amount`   DECIMAL(12,2) NOT NULL DEFAULT 0.00   COMMENT '已使用额度（冗余汇总，事务内维护）',
  `sort_no`       INT           NOT NULL DEFAULT 0      COMMENT '排序号，升序',
  `remark`        VARCHAR(255)           DEFAULT NULL   COMMENT '备注',
  `create_by`     BIGINT                 DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`     BIGINT                 DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       BIGINT        NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_period_category` (`period_id`, `category`),
  KEY `idx_period_sort` (`period_id`, `sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='预算科目表';

-- ---- 3.3.3 `budget_apply` 预算申请表 ----

DROP TABLE IF EXISTS `budget_apply`;
CREATE TABLE `budget_apply` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `period_id`     BIGINT        NOT NULL                COMMENT '所属预算周期 ID',
  `item_id`       BIGINT        NOT NULL                COMMENT '所属预算科目 ID',
  `apply_user_id` BIGINT        NOT NULL                COMMENT '申请人 ID',
  `title`         VARCHAR(100)  NOT NULL                COMMENT '申请标题',
  `amount`        DECIMAL(12,2) NOT NULL                COMMENT '申请金额',
  `approved_amount` DECIMAL(12,2)         DEFAULT NULL   COMMENT '批准金额（部分批准时小于申请金额；未填视为全额批准）',
  `reason`        VARCHAR(500)           DEFAULT NULL   COMMENT '申请事由',
  `expect_date`   DATE                   DEFAULT NULL   COMMENT '期望使用日期',
  `attach_file_id` BIGINT                DEFAULT NULL   COMMENT '附件文件 ID（sys_file.id）',
  `status`        TINYINT       NOT NULL DEFAULT 0      COMMENT '状态：0 待审核 1 已通过 2 已驳回 3 已撤销',
  `audit_user_id` BIGINT                 DEFAULT NULL   COMMENT '审核人 ID',
  `audit_time`    DATETIME               DEFAULT NULL   COMMENT '审核时间',
  `audit_remark`  VARCHAR(255)           DEFAULT NULL   COMMENT '审核意见',
  `create_by`     BIGINT                 DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`     BIGINT                 DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       BIGINT        NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_period_id` (`period_id`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_apply_user_status` (`apply_user_id`, `status`),
  KEY `idx_apply_user_time` (`apply_user_id`, `create_time`),
  KEY `idx_attach_file_id` (`attach_file_id`),
  KEY `idx_status_create` (`status`, `create_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='预算申请表';

-- ---- 3.3.4 `budget_expense` 支出记录表 ----

DROP TABLE IF EXISTS `budget_expense`;
CREATE TABLE `budget_expense` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `period_id`       BIGINT        NOT NULL                COMMENT '所属预算周期 ID',
  `item_id`         BIGINT        NOT NULL                COMMENT '所属预算科目 ID',
  `apply_id`        BIGINT                 DEFAULT NULL   COMMENT '关联的预算申请 ID，可为空（无申请的直接支出）',
  `title`           VARCHAR(100)  NOT NULL                COMMENT '支出标题',
  `amount`          DECIMAL(12,2) NOT NULL                COMMENT '支出金额',
  `expense_date`    DATE          NOT NULL                COMMENT '支出日期',
  `voucher_file_id` BIGINT                 DEFAULT NULL   COMMENT '凭证文件 ID（发票 / 收据，sys_file.id）',
  `handler_id`      BIGINT        NOT NULL                COMMENT '经办人（管理员）ID',
  `remark`          VARCHAR(500)           DEFAULT NULL   COMMENT '备注',
  `create_by`       BIGINT                 DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT                 DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         BIGINT        NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_period_item` (`period_id`, `item_id`),
  KEY `idx_period_date` (`period_id`, `expense_date`),
  KEY `idx_apply_id` (`apply_id`),
  KEY `idx_handler_id` (`handler_id`),
  KEY `idx_voucher_file_id` (`voucher_file_id`),
  KEY `idx_expense_date` (`expense_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='支出记录表';

-- ============================================================================
--  3.4 模块四：部门财产（6 张）
-- ============================================================================

-- ---- 3.4.1 `asset_category` 资产分类表 ----

DROP TABLE IF EXISTS `asset_category`;
CREATE TABLE `asset_category` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`        VARCHAR(64)  NOT NULL                COMMENT '分类名称',
  `parent_id`   BIGINT       NOT NULL DEFAULT 0      COMMENT '父分类 ID，0 表示一级分类',
  `code`        VARCHAR(32)           DEFAULT NULL   COMMENT '分类编码，唯一',
  `sort_no`     INT          NOT NULL DEFAULT 0      COMMENT '排序号，升序',
  `create_by`   BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`   BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`, `deleted`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资产分类表';

-- ---- 3.4.2 `asset_info` 资产台账表 ----

DROP TABLE IF EXISTS `asset_info`;
CREATE TABLE `asset_info` (
  `id`                BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `asset_no`          VARCHAR(32)   NOT NULL                COMMENT '资产编号，唯一（一物一码）',
  `name`              VARCHAR(100)  NOT NULL                COMMENT '资产名称',
  `category_id`       BIGINT                 DEFAULT NULL   COMMENT '资产分类 ID',
  `brand`             VARCHAR(64)            DEFAULT NULL   COMMENT '品牌',
  `model`             VARCHAR(64)            DEFAULT NULL   COMMENT '型号',
  `price`             DECIMAL(12,2) NOT NULL DEFAULT 0.00   COMMENT '购置价格',
  `purchase_date`     DATE                   DEFAULT NULL   COMMENT '购置日期',
  `supplier`          VARCHAR(128)           DEFAULT NULL   COMMENT '供应商',
  `source_type`       TINYINT       NOT NULL DEFAULT 1      COMMENT '来源：1 采购 2 捐赠 3 调拨',
  `budget_expense_id` BIGINT                 DEFAULT NULL   COMMENT '关联的预算支出 ID（采购入账闭环）',
  `status`            TINYINT       NOT NULL DEFAULT 1      COMMENT '状态：1 在用 2 闲置 3 维修中 4 已借出 5 已报废',
  `keeper_id`         BIGINT                 DEFAULT NULL   COMMENT '保管人 ID',
  `location`          VARCHAR(128)           DEFAULT NULL   COMMENT '存放位置文字描述，可与工位联动',
  `image_urls`        JSON                   DEFAULT NULL   COMMENT '资产照片地址数组（JSON）',
  `warranty_end_date` DATE                   DEFAULT NULL   COMMENT '保修截止日期',
  `remark`            VARCHAR(500)           DEFAULT NULL   COMMENT '备注',
  `create_by`         BIGINT                 DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`         BIGINT                 DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`           BIGINT        NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_asset_no` (`asset_no`, `deleted`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_keeper_id` (`keeper_id`),
  KEY `idx_budget_expense_id` (`budget_expense_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资产台账表';

-- ---- 3.4.3 `asset_borrow` 借用/领用表 ----

DROP TABLE IF EXISTS `asset_borrow`;
CREATE TABLE `asset_borrow` (
  `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `asset_id`           BIGINT       NOT NULL                COMMENT '资产 ID',
  `borrow_type`        TINYINT      NOT NULL DEFAULT 1      COMMENT '类型：1 借用 2 领用',
  `user_id`            BIGINT       NOT NULL                COMMENT '借用人 ID',
  `purpose`            VARCHAR(255)          DEFAULT NULL   COMMENT '用途说明',
  `borrow_time`        DATETIME     NOT NULL                COMMENT '借用时间',
  `expect_return_time` DATETIME              DEFAULT NULL   COMMENT '预计归还时间',
  `actual_return_time` DATETIME              DEFAULT NULL   COMMENT '实际归还时间',
  `status`             TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0 待审核 1 已通过 2 已驳回 3 已归还 4 已逾期',
  `audit_user_id`      BIGINT                DEFAULT NULL   COMMENT '审核人 ID',
  `audit_time`         DATETIME              DEFAULT NULL   COMMENT '审核时间',
  `audit_remark`       VARCHAR(255)          DEFAULT NULL   COMMENT '审核意见',
  `create_by`          BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`          BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`            BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_asset_status` (`asset_id`, `status`),
  KEY `idx_user_status` (`user_id`, `status`),
  KEY `idx_status` (`status`),
  KEY `idx_expect_return_time` (`expect_return_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资产借用/领用表';

-- ---- 3.4.4 `asset_record` 资产变动流水表 ----

DROP TABLE IF EXISTS `asset_record`;
CREATE TABLE `asset_record` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `asset_id`      BIGINT       NOT NULL                COMMENT '资产 ID',
  `record_type`   TINYINT      NOT NULL                COMMENT '变动类型：1 领用 2 借用 3 归还 4 维修 5 报废 6 调拨 7 盘点',
  `from_user_id`  BIGINT                DEFAULT NULL   COMMENT '变动前使用人 ID',
  `to_user_id`    BIGINT                DEFAULT NULL   COMMENT '变动后使用人 ID',
  `from_location` VARCHAR(128)          DEFAULT NULL   COMMENT '变动前存放位置',
  `to_location`   VARCHAR(128)          DEFAULT NULL   COMMENT '变动后存放位置',
  `record_time`   DATETIME     NOT NULL                COMMENT '变动时间',
  `operator_id`   BIGINT       NOT NULL                COMMENT '操作人 ID',
  `remark`        VARCHAR(500)          DEFAULT NULL   COMMENT '备注',
  `create_by`     BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`     BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_asset_time` (`asset_id`, `record_time`),
  KEY `idx_record_time` (`record_time`),
  KEY `idx_record_type_time` (`record_type`, `record_time`),
  KEY `idx_operator_time` (`operator_id`, `record_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资产变动流水表';

-- ---- 3.4.5 `asset_inventory` 盘点任务表 ----

DROP TABLE IF EXISTS `asset_inventory`;
CREATE TABLE `asset_inventory` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title`          VARCHAR(100) NOT NULL                COMMENT '盘点任务标题',
  `begin_time`     DATETIME     NOT NULL                COMMENT '盘点开始时间',
  `end_time`       DATETIME              DEFAULT NULL   COMMENT '盘点结束时间',
  `status`         TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0 进行中 1 已完成',
  `total_count`    INT          NOT NULL DEFAULT 0      COMMENT '应盘点总数（结束盘点时回填）',
  `normal_count`   INT          NOT NULL DEFAULT 0      COMMENT '正常数（结束盘点时回填）',
  `abnormal_count` INT          NOT NULL DEFAULT 0      COMMENT '异常数（结束盘点时回填）',
  `create_by`      BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`      BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_status_begin_time` (`status`, `begin_time`),
  KEY `idx_begin_time` (`begin_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='盘点任务表';

-- ---- 3.4.6 `asset_inventory_item` 盘点明细表 ----

DROP TABLE IF EXISTS `asset_inventory_item`;
CREATE TABLE `asset_inventory_item` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `inventory_id`  BIGINT       NOT NULL                COMMENT '盘点任务 ID',
  `asset_id`      BIGINT       NOT NULL                COMMENT '资产 ID',
  `check_status`  TINYINT      NOT NULL DEFAULT 0      COMMENT '盘点结果：0 未盘点 1 正常 2 盘亏 3 位置不符',
  `check_user_id` BIGINT                DEFAULT NULL   COMMENT '盘点人 ID',
  `check_time`    DATETIME              DEFAULT NULL   COMMENT '盘点时间',
  `remark`        VARCHAR(255)          DEFAULT NULL   COMMENT '备注（差异说明）',
  `create_by`     BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`     BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inventory_asset` (`inventory_id`, `asset_id`, `deleted`),
  KEY `idx_asset_id` (`asset_id`),
  KEY `idx_check_status` (`check_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='盘点明细表';

-- ============================================================================
--  3.5 模块五：部门工位（4 张）
-- ============================================================================

-- ---- 3.5.1 `seat_area` 工位区域表 ----

DROP TABLE IF EXISTS `seat_area`;
CREATE TABLE `seat_area` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`           VARCHAR(64)  NOT NULL                COMMENT '区域名称，如「A 区开放工位」',
  `plan_image_url` VARCHAR(512)          DEFAULT NULL   COMMENT '平面图底图地址（OSS）',
  `width`          INT          NOT NULL DEFAULT 0      COMMENT '底图宽度（像素），坐标换算基准',
  `height`         INT          NOT NULL DEFAULT 0      COMMENT '底图高度（像素），坐标换算基准',
  `description`    VARCHAR(500)          DEFAULT NULL   COMMENT '区域说明',
  `sort_no`        INT          NOT NULL DEFAULT 0      COMMENT '排序号，升序',
  `create_by`      BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`      BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工位区域表';

-- ---- 3.5.2 `seat_info` 工位表 ----

DROP TABLE IF EXISTS `seat_info`;
CREATE TABLE `seat_info` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `area_id`      BIGINT        NOT NULL                COMMENT '所属区域 ID',
  `seat_no`      VARCHAR(32)   NOT NULL                COMMENT '工位编号，如 A-01',
  `seat_type`    TINYINT       NOT NULL DEFAULT 1      COMMENT '类型：1 工位 2 会议室 3 储物柜 4 其他',
  `x_ratio`      DECIMAL(8,4)  NOT NULL DEFAULT 0      COMMENT 'X 坐标占底图宽度百分比（0~100）',
  `y_ratio`      DECIMAL(8,4)  NOT NULL DEFAULT 0      COMMENT 'Y 坐标占底图高度百分比（0~100）',
  `width_ratio`  DECIMAL(8,4)  NOT NULL DEFAULT 0      COMMENT '宽度占底图宽度百分比',
  `height_ratio` DECIMAL(8,4)  NOT NULL DEFAULT 0      COMMENT '高度占底图高度百分比',
  `rotation`     DECIMAL(6,2)  NOT NULL DEFAULT 0      COMMENT '旋转角度（度）',
  `status`       TINYINT       NOT NULL DEFAULT 1      COMMENT '状态：1 空闲 2 已占用 3 预留 4 维修中',
  `user_id`      BIGINT                 DEFAULT NULL   COMMENT '使用人 ID，NULL 表示无人使用',
  `assign_time`  DATETIME               DEFAULT NULL   COMMENT '分配时间',
  `remark`       VARCHAR(255)           DEFAULT NULL   COMMENT '备注',
  `version`      INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
  `create_by`    BIGINT                 DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`    BIGINT                 DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      BIGINT        NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_area_seat_no` (`area_id`, `seat_no`, `deleted`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工位表';

-- ---- 3.5.3 `seat_apply` 工位申请表 ----

DROP TABLE IF EXISTS `seat_apply`;
CREATE TABLE `seat_apply` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`       BIGINT       NOT NULL                COMMENT '申请人 ID',
  `area_id`       BIGINT       NOT NULL                COMMENT '意向区域 ID',
  `seat_id`       BIGINT                DEFAULT NULL   COMMENT '意向工位 ID，NULL 表示仅意向区域',
  `assign_seat_id` BIGINT               DEFAULT NULL   COMMENT '实际分配工位 ID，审核通过时写入（可能不同于意向工位）',
  `reason`        VARCHAR(500)          DEFAULT NULL   COMMENT '申请理由',
  `status`        TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0 待审核 1 已通过 2 已驳回 3 已撤销',
  `audit_user_id` BIGINT                DEFAULT NULL   COMMENT '审核人 ID',
  `audit_time`    DATETIME              DEFAULT NULL   COMMENT '审核时间',
  `audit_remark`  VARCHAR(255)          DEFAULT NULL   COMMENT '审核意见',
  `create_by`     BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`     BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_area_id` (`area_id`),
  KEY `idx_seat_id` (`seat_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工位申请表';

-- ---- 3.5.4 `seat_swap` 座位调换申请表 ----

DROP TABLE IF EXISTS `seat_swap`;
CREATE TABLE `seat_swap` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `apply_user_id`  BIGINT       NOT NULL                COMMENT '申请方用户 ID',
  `target_user_id` BIGINT       NOT NULL                COMMENT '对调方用户 ID',
  `apply_seat_id`  BIGINT       NOT NULL                COMMENT '申请方当前工位 ID',
  `target_seat_id` BIGINT       NOT NULL                COMMENT '对调方工位 ID',
  `reason`         VARCHAR(500)          DEFAULT NULL   COMMENT '调换理由',
  `status`         TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0 待对方确认 1 对方已同意待审核 2 已通过 3 已拒绝 4 已取消',
  `handle_time`    DATETIME              DEFAULT NULL   COMMENT '对方处理时间',
  `handle_remark`  VARCHAR(255)          DEFAULT NULL   COMMENT '对方处理意见',
  `audit_user_id`  BIGINT                DEFAULT NULL   COMMENT '审核人 ID',
  `audit_time`     DATETIME              DEFAULT NULL   COMMENT '审核时间',
  `audit_remark`   VARCHAR(255)          DEFAULT NULL   COMMENT '审核意见',
  `create_by`      BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`      BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_apply_user` (`apply_user_id`),
  KEY `idx_target_user` (`target_user_id`),
  KEY `idx_apply_seat_id` (`apply_seat_id`),
  KEY `idx_target_seat_id` (`target_seat_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='座位调换申请表';

-- ============================================================================
--  3.6 模块六：学长学姐展板（2 张）
-- ============================================================================

-- ---- 3.6.1 `honor_alumni` 学长学姐信息表 ----

DROP TABLE IF EXISTS `honor_alumni`;
CREATE TABLE `honor_alumni` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`            VARCHAR(32)  NOT NULL                COMMENT '姓名',
  `gender`          TINYINT      NOT NULL DEFAULT 0      COMMENT '性别：0 未知 1 男 2 女',
  `graduation_year` VARCHAR(10)           DEFAULT NULL   COMMENT '毕业年份，如 2024',
  `major`           VARCHAR(50)           DEFAULT NULL   COMMENT '专业',
  `class_name`      VARCHAR(50)           DEFAULT NULL   COMMENT '班级',
  `company`         VARCHAR(128)          DEFAULT NULL   COMMENT '就职单位',
  `position`        VARCHAR(64)           DEFAULT NULL   COMMENT '职位',
  `avatar_url`      VARCHAR(512)          DEFAULT NULL   COMMENT '头像地址（OSS）',
  `brief`           TEXT                  DEFAULT NULL   COMMENT '个人简介',
  `tags`            VARCHAR(255)          DEFAULT NULL   COMMENT '标签，逗号分隔',
  `submit_user_id`  BIGINT                DEFAULT NULL   COMMENT '填写人 ID（成员自填）',
  `status`          TINYINT      NOT NULL DEFAULT 0      COMMENT '审核状态：0 待审核 1 已通过 2 已驳回',
  `audit_user_id`   BIGINT                DEFAULT NULL   COMMENT '审核人 ID',
  `audit_time`      DATETIME              DEFAULT NULL   COMMENT '审核时间',
  `audit_remark`    VARCHAR(255)          DEFAULT NULL   COMMENT '审核意见',
  `is_show`         TINYINT      NOT NULL DEFAULT 0      COMMENT '上板状态：0 下板 1 上板',
  `sort_no`         INT          NOT NULL DEFAULT 0      COMMENT '展示排序，升序',
  `create_by`       BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_status_show` (`status`, `is_show`),
  KEY `idx_submit_user` (`submit_user_id`),
  KEY `idx_graduation_year` (`graduation_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学长学姐信息表';

-- ---- 3.6.2 `honor_record` 荣誉记录表 ----

DROP TABLE IF EXISTS `honor_record`;
CREATE TABLE `honor_record` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `alumni_id`       BIGINT       NOT NULL                COMMENT '所属学长学姐 ID',
  `title`           VARCHAR(200) NOT NULL                COMMENT '荣誉名称',
  `level`           TINYINT      NOT NULL DEFAULT 4      COMMENT '级别：1 国家级 2 省级 3 校级 4 企业及其他',
  `award_date`      DATE                  DEFAULT NULL   COMMENT '获奖日期',
  `description`     VARCHAR(500)          DEFAULT NULL   COMMENT '荣誉说明',
  `certificate_url` VARCHAR(512)          DEFAULT NULL   COMMENT '证书图片/PDF 地址（OSS）',
  `sort_no`         INT          NOT NULL DEFAULT 0      COMMENT '排序号，升序',
  `create_by`       BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_alumni_sort` (`alumni_id`, `sort_no`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='荣誉记录表';

-- ============================================================================
--  3.7 模块七：值日排班（4 张）
-- ============================================================================

-- ---- 3.7.1 `duty_plan` 排班计划表 ----

DROP TABLE IF EXISTS `duty_plan`;
CREATE TABLE `duty_plan` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title`        VARCHAR(100) NOT NULL                COMMENT '计划标题',
  `plan_type`    TINYINT      NOT NULL DEFAULT 2      COMMENT '轮转方式：1 按周轮转 2 分组轮转 3 手工指定',
  `start_date`   DATE         NOT NULL                COMMENT '计划开始日期',
  `end_date`     DATE         NOT NULL                COMMENT '计划结束日期',
  `member_ids`   JSON                  DEFAULT NULL   COMMENT '参与轮转的成员 ID 数组（JSON）',
  `group_config` JSON                  DEFAULT NULL   COMMENT '分组配置（JSON）',
  `task_content` VARCHAR(500)          DEFAULT NULL   COMMENT '默认值日内容',
  `status`       TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0 草稿 1 已发布 2 已结束',
  `publish_time` DATETIME              DEFAULT NULL   COMMENT '发布时间',
  `remark`       VARCHAR(255)          DEFAULT NULL   COMMENT '备注',
  `create_by`    BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`    BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_date_range` (`start_date`, `end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='排班计划表';

-- ---- 3.7.2 `duty_schedule` 值日安排表 ----

DROP TABLE IF EXISTS `duty_schedule`;
CREATE TABLE `duty_schedule` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id`      BIGINT       NOT NULL                COMMENT '所属排班计划 ID',
  `duty_date`    DATE         NOT NULL                COMMENT '值日日期',
  `user_id`      BIGINT       NOT NULL                COMMENT '值日人 ID',
  `group_name`   VARCHAR(32)           DEFAULT NULL   COMMENT '所属分组名称',
  `task_content` VARCHAR(500)          DEFAULT NULL   COMMENT '值日内容',
  `status`       TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0 未完成 1 已完成 2 请假 3 已换班',
  `confirm_time` DATETIME              DEFAULT NULL   COMMENT '完成确认时间',
  `remark`       VARCHAR(255)          DEFAULT NULL   COMMENT '备注',
  `create_by`    BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`    BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_date_user` (`plan_id`, `duty_date`, `user_id`, `deleted`),
  KEY `idx_duty_date_user` (`duty_date`, `user_id`),
  KEY `idx_user_date` (`user_id`, `duty_date`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='值日安排表';

-- ---- 3.7.3 `duty_swap` 换班申请表 ----

DROP TABLE IF EXISTS `duty_swap`;
CREATE TABLE `duty_swap` (
  `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `schedule_id`        BIGINT       NOT NULL                COMMENT '申请方的值日安排 ID',
  `apply_user_id`      BIGINT       NOT NULL                COMMENT '申请人 ID',
  `target_user_id`     BIGINT       NOT NULL                COMMENT '换班对象 ID',
  `target_schedule_id` BIGINT                DEFAULT NULL   COMMENT '对调的目标值日安排 ID',
  `reason`             VARCHAR(500)          DEFAULT NULL   COMMENT '换班理由',
  `status`             TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0 待确认 1 已同意 2 已拒绝 3 已取消',
  `handle_time`        DATETIME              DEFAULT NULL   COMMENT '处理时间',
  `handle_remark`      VARCHAR(255)          DEFAULT NULL   COMMENT '处理意见',
  `create_by`          BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`          BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`            BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_schedule_id` (`schedule_id`),
  KEY `idx_apply_user` (`apply_user_id`),
  KEY `idx_target_user` (`target_user_id`),
  KEY `idx_target_schedule_id` (`target_schedule_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='换班申请表';

-- ---- 3.7.4 `duty_reminder_config` 提醒配置表 ----

DROP TABLE IF EXISTS `duty_reminder_config`;
CREATE TABLE `duty_reminder_config` (
  `id`             BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id`        BIGINT            DEFAULT NULL   COMMENT '所属排班计划 ID，NULL 表示全局配置',
  `ahead_minutes`  INT      NOT NULL DEFAULT 30      COMMENT '提前提醒分钟数',
  `channel`        TINYINT  NOT NULL DEFAULT 1       COMMENT '提醒渠道：1 站内消息',
  `enabled`        TINYINT  NOT NULL DEFAULT 1       COMMENT '是否启用：0 停用 1 启用',
  `create_by`      BIGINT            DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`      BIGINT            DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIGINT   NOT NULL DEFAULT 0       COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_id` (`plan_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='提醒配置表';

-- ============================================================================
--  3.8 模块八：通用支撑（6 张）
-- ============================================================================

-- ---- 3.8.1 `sys_file` 文件表 ----

DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`           VARCHAR(255) NOT NULL                COMMENT '原始文件名',
  `url`            VARCHAR(512) NOT NULL                COMMENT '访问地址（OSS 完整 URL）',
  `object_key`     VARCHAR(512) NOT NULL                COMMENT 'OSS 对象键，如 upload/avatar/2026/09/{uuid}.png',
  `bucket`         VARCHAR(128)          DEFAULT NULL   COMMENT '存储桶名称，便于迁移时重算 URL',
  `size`           BIGINT       NOT NULL DEFAULT 0      COMMENT '文件大小（字节）',
  `content_type`   VARCHAR(128)          DEFAULT NULL   COMMENT 'MIME 类型',
  `file_hash`      VARCHAR(64)           DEFAULT NULL   COMMENT '文件哈希（预留：秒传与去重）',
  `biz_type`       VARCHAR(32)  NOT NULL DEFAULT 'common' COMMENT '业务归属：avatar/asset/honor/budget/seat/notice/video/common',
  `upload_user_id` BIGINT                DEFAULT NULL   COMMENT '上传人 ID',
  `create_by`      BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`      BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_object_key` (`object_key`(191)),
  KEY `idx_upload_user` (`upload_user_id`),
  KEY `idx_biz_type` (`biz_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文件表';

-- ---- 3.8.2 `sys_notice` 公告表 ----

DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title`        VARCHAR(100) NOT NULL                COMMENT '公告标题',
  `content`      MEDIUMTEXT   NOT NULL                COMMENT '公告内容（富文本 HTML）',
  `notice_type`  TINYINT      NOT NULL DEFAULT 1      COMMENT '公告类型：1 中心公告 2 制度 3 活动 4 其他',
  `is_top`       TINYINT      NOT NULL DEFAULT 0      COMMENT '是否置顶：0 普通 1 置顶',
  `publish_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `status`       TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：0 草稿 1 已发布 2 已下架',
  `create_by`    BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`    BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_status_top_time` (`status`, `is_top`, `publish_time`),
  KEY `idx_notice_type` (`notice_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公告表';

-- ---- 3.8.3 `sys_oper_log` 操作日志表 ----

DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `module`        VARCHAR(32)            DEFAULT NULL   COMMENT '模块标识，如 system / budget / asset',
  `operation`     VARCHAR(32)            DEFAULT NULL   COMMENT '操作类型，如 add / edit / remove / audit',
  `request_uri`   VARCHAR(255)           DEFAULT NULL   COMMENT '请求地址',
  `request_method` VARCHAR(8)            DEFAULT NULL   COMMENT '请求方法：GET/POST/PUT/DELETE',
  `request_param` VARCHAR(2000)          DEFAULT NULL   COMMENT '请求参数摘要（已脱敏、已截断）',
  `response_msg`  VARCHAR(500)           DEFAULT NULL   COMMENT '返回信息摘要',
  `operator_id`   BIGINT                 DEFAULT NULL   COMMENT '操作人 ID',
  `operator_name` VARCHAR(32)            DEFAULT NULL   COMMENT '操作人姓名（冗余，避免用户改名后追溯困难）',
  `ip`            VARCHAR(64)            DEFAULT NULL   COMMENT '来源 IP',
  `cost_ms`       BIGINT        NOT NULL DEFAULT 0      COMMENT '耗时（毫秒）',
  `status`        TINYINT       NOT NULL DEFAULT 1      COMMENT '执行结果：0 失败 1 成功',
  `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_module_operation` (`module`, `operation`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志表';

-- ---- 3.8.4 `sys_video` 视频资源表 ----

DROP TABLE IF EXISTS `sys_video`;
CREATE TABLE `sys_video` (
  `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title`            VARCHAR(100) NOT NULL                COMMENT '视频标题',
  `cover_url`        VARCHAR(512)          DEFAULT NULL   COMMENT '封面图地址（OSS）',
  `jellyfin_item_id` VARCHAR(64)           DEFAULT NULL   COMMENT 'Jellyfin 媒体项 ID，用于链接失效时重新拼接',
  `play_url`         VARCHAR(512) NOT NULL                COMMENT '播放地址（Jellyfin 生成，直链或 HLS 播放列表）',
  `duration`         INT          NOT NULL DEFAULT 0      COMMENT '时长（秒）',
  `biz_type`         TINYINT      NOT NULL DEFAULT 2      COMMENT '业务归属：1 首页展示 2 学长学姐展板 3 其他',
  `sort_no`          INT          NOT NULL DEFAULT 0      COMMENT '展示排序，升序',
  `status`           TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：0 下架 1 上架',
  `remark`           VARCHAR(255)          DEFAULT NULL   COMMENT '备注',
  `create_by`        BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`        BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`          BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_biz_status_sort` (`biz_type`, `status`, `sort_no`),
  KEY `idx_jellyfin_item_id` (`jellyfin_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='视频资源表';

-- ---- 3.8.5 `sys_dict` 数据字典表（补充表） ----

DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
  `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dict_type`   VARCHAR(64) NOT NULL                COMMENT '字典类型，如 assetStatus',
  `dict_label`  VARCHAR(64) NOT NULL                COMMENT '展示文案，如「在用」',
  `dict_value`  INT         NOT NULL                COMMENT '枚举值，如 1',
  `sort_no`     INT         NOT NULL DEFAULT 0      COMMENT '排序号，升序',
  `status`      TINYINT     NOT NULL DEFAULT 1      COMMENT '状态：0 停用 1 启用',
  `remark`      VARCHAR(255)         DEFAULT NULL   COMMENT '备注',
  `create_by`   BIGINT               DEFAULT NULL   COMMENT '创建人 ID',
  `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`   BIGINT               DEFAULT NULL   COMMENT '更新人 ID',
  `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     BIGINT      NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_value` (`dict_type`, `dict_value`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据字典表';

-- ---- 3.8.6 `sys_message` 站内消息表（补充表） ----

DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `receiver_id` BIGINT       NOT NULL                COMMENT '接收人 ID',
  `title`       VARCHAR(100) NOT NULL                COMMENT '消息标题',
  `content`     VARCHAR(500)          DEFAULT NULL   COMMENT '消息内容',
  `msg_type`    TINYINT      NOT NULL DEFAULT 3      COMMENT '消息类型：1 值日提醒 2 审核结果 3 系统通知',
  `biz_type`    VARCHAR(32)           DEFAULT NULL   COMMENT '关联业务类型，如 duty_schedule / budget_apply',
  `biz_id`      BIGINT                DEFAULT NULL   COMMENT '关联业务主键，用于点击跳转与去重',
  `is_read`     TINYINT      NOT NULL DEFAULT 0      COMMENT '是否已读：0 未读 1 已读',
  `read_time`   DATETIME              DEFAULT NULL   COMMENT '阅读时间',
  `create_by`   BIGINT                DEFAULT NULL   COMMENT '创建人 ID',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`   BIGINT                DEFAULT NULL   COMMENT '更新人 ID',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     BIGINT       NOT NULL DEFAULT 0      COMMENT '删除标记：0 未删除；已删除时写入本行 id（见 1.5 节）',
  PRIMARY KEY (`id`),
  KEY `idx_receiver_read_time` (`receiver_id`, `is_read`, `create_time`),
  KEY `idx_biz` (`biz_type`, `biz_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='站内消息表';

SET FOREIGN_KEY_CHECKS = 1;
