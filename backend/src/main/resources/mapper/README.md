# Mapper XML 目录

MyBatis-Plus 的 `mapper-locations` 配为 `classpath*:mapper/**/*.xml`（见 application.yml）。

约定：**复杂 SQL 才写 XML**，单表 CRUD 一律用 `BaseMapper` 与 `LambdaQueryWrapper`，不要为了「统一风格」把简单查询也搬进 XML。

建议按模块建子目录，文件名与 Mapper 接口同名：

```
mapper/
├── system/SysUserMapper.xml
├── repo/RepoCommitMapper.xml
├── budget/BudgetPeriodMapper.xml
└── ...
```

## 需要写 XML 的典型场景

| 场景 | 说明 |
| --- | --- |
| 模块二贡献排行 | `repo_commit` 按 `user_id` 聚合 + 关联 `sys_user`，见《数据库设计》3.2 节 |
| 模块二热力图 | 按 `DATE(commit_time)` 分组统计，注意必须带 `project_id` 或 `user_id` + 时间范围 |
| 预算执行看板 | `budget_period` / `budget_item` / `budget_expense` 三表聚合 |
| 工位平面图 | `seat_area` + `seat_info` + `sys_user` 关联，一次查出区域与工位占用情况 |
| 权限码树 | `sys_role_permission` 去重后的 `perm_code` 集合 |
``