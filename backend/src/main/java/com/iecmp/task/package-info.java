/**
 * 定时任务（架构文档 4.6 节）。
 *
 * <p>单机部署用 {@code @Scheduled} 即可；多实例部署时必须引入分布式锁
 * （Redis {@code SETNX}）或改用调度中心，否则任务会重复执行。
 */
package com.iecmp.task;
