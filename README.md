# IECMP 创业中心管理平台

中原工学院软件学院创新创业中心 · 部门管理平台

| 目录 | 内容 |
| --- | --- |
| `docs/` | 三份设计与接口文档（唯一权威来源）+ 可直接执行的 SQL 脚本 |
| `backend/` | Spring Boot 后端（骨架，业务待实现） |
| `frontend/` | Vue 3 前端（骨架，页面待实现） |

## 文档与脚本

| 文档 | 回答的问题 |
| --- | --- |
| `docs/IECMP-项目介绍与技术架构.md` | 系统是什么、怎么搭、为什么这样选 |
| `docs/IECMP-接口开发文档-v1.6.md` | 每个接口怎么调（**接口契约的唯一权威来源**） |
| `docs/IECMP-数据库设计.md` | 数据怎么存（**表结构与 DDL 的唯一权威来源**） |
| `docs/sql/IECMP-数据库建表脚本.sql` | 建库 + 36 张表 DDL，可反复执行 |
| `docs/sql/IECMP-初始数据.sql` | 角色 / 管理员 / 85 个权限码 / 23 类字典，可反复执行 |

初始化数据库：

```bash
mysql -u root -p < docs/sql/IECMP-数据库建表脚本.sql
mysql -u root -p < docs/sql/IECMP-初始数据.sql
```

初始管理员账号 `admin / 123456`（首次登录后请立即改密）。

## 快速开始

后端（JDK 17 + Maven）：

```bash
cd backend
mvn clean package -DskipTests
java -jar target/iecmp-backend-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

接口文档（Swagger UI）：<http://localhost:8080/api/swagger-ui/index.html>

前端（Node 18+）：

```bash
cd frontend
npm install
npm run dev
```

## 当前状态：骨架（已实测跑通）

**已完成且已实测**：完整骨架已按《接口开发文档-v1.6》生成并通过真机验证。

| 项 | 数量 / 结果 |
| --- | --- |
| 后端 Java 文件 | 540 个（`mvn -B -DskipTests compile` 通过） |
| Controller | 54 个，覆盖文档全部 **186** 个接口（166 条路径） |
| 实体 / Mapper / Service / Impl | 36 / 36 / 36 / 36（对应 36 张表） |
| 请求 DTO / 查询 Query / 响应 VO | 81 / 53 / 121 |
| 权限码 | 85 个，与文档、`@RequiresPerm`、初始数据脚本三处**完全一致** |
| 错误码枚举 / 业务枚举 | 8 个错误码枚举 / 37 个业务枚举 |
| 启动实测 | 成功（`Started IecmpApplication in 5.7s`，端口 8080，context-path `/api`） |
| Swagger 实测 | `/api/v3/api-docs` 输出 166 条路径 / 186 个操作 |
| 拦截器实测 | 无令牌访问 `/api/admin/user/page` 返回 `{"code":401,"msg":"未登录或登录已过期"}` |
| 初始账号实测 | 初始数据脚本里的 BCrypt 哈希用 `PasswordUtil.matches("123456", 哈希)` 验证为 **true** |
| 前端 | 83 个文件（`npm install` + `npm run build` + `npx eslint .` 通过，0 error） |

**待实现**：所有 Controller 的**方法体**（后端）与所有页面的**界面与交互**（前端）。

Controller 方法体统一为占位实现：

```java
return Result.todo("1-14");   // 返回 code=500，msg="接口 1-14 尚未实现（骨架占位）"
```

实现某个接口时，把 `Result.todo("x-xx")` 换成真实逻辑即可。全局搜索 `Result.todo(` 可以看到全部待实现接口
（`4-27 资产台账导出` 是唯一例外：方法体为 `void` + `throw new UnsupportedOperationException`）。

前端页面全部是空壳：`src/views/**/index.vue` 只有 `<el-empty>` 占位与接口编号注释；
`src/api/*.js` 只有接口清单注释与写法模板（`export default {}`），实现时按注释补方法即可。

### 本机运行前提

- **MySQL**：先执行 `docs/sql/` 下两个脚本；`application-dev.yml` 默认 `127.0.0.1:3306/iecmp`、`root/123456`，
  与本机不同时改 `application-local.yml`（已被 `.gitignore` 忽略）。
- **Redis**：dev 配置连 `127.0.0.1:6379`（db0）。**本机当前未安装 Redis**，启动前需安装或用 Docker 起一个；
  Redis 只在登录 / 验证码 / 缓存相关逻辑里用，不装也能启动，但相关接口会失败。
- 个人机器差异（数据库口令、Redis 地址等）统一放 `backend/src/main/resources/application-local.yml`。

### 分层与命名约定（详见《项目介绍与技术架构》3.5 节）

```
com.iecmp
├── common/          公共能力（result / exception / interceptor / annotation / aspect / config / enums / util / base）
├── system/          模块一：用户与权限
├── repo/            模块二：代码仓库关联
├── budget/          模块三：部门预算
├── asset/           模块四：部门财产
├── seat/            模块五：部门工位
├── honor/           模块六：学长学姐展板
├── duty/            模块七：值日排班
├── commonm/         模块八：通用支撑（文件 / 字典 / 公告 / 日志 / 视频 / 消息）
└── task/            定时任务
```

每个模块内部：`controller / service / service.impl / mapper / entity / dto / vo`。
命名：实体 `Xxx`（与表名一一对应）、请求体 `XxxDTO`、查询条件 `XxxQuery`、响应体 `XxxVO`。
Controller 按前台 / 后台拆分：`AdminXxxController`（`/admin/**`）与 `XxxController`（`/user/**`、`/common/**`）。

## 两个必须注意的点

1. **逻辑删除不是 0/1**。全库 `deleted` 为 `BIGINT`，删除时写入本行 `id`（`deleted = id`），
   并参与全部唯一索引。实体上已配置 `@TableLogic(value = "0", delval = "id")`，
   **上线前必须用一个真实删除动作打印 SQL 确认**（详见《数据库设计》1.5 节）。
2. **提交记录查询必须带 `project_id`**。`repo_commit` 的 `commit_sha` 不是索引首列，
   只按 `commit_sha` 查询会全表扫描（详见《数据库设计》3.2.3 节）。

## 尚未引入的依赖

- 阿里云 OSS SDK（`com.aliyun.oss:aliyun-sdk-oss`）：实现文件上传模块时再引入。
  JDK 11+ 下需同时引入 `javax.xml.bind:jaxb-api` 与 `org.glassfish.jaxb:jaxb-runtime`，否则启动报
  `NoClassDefFoundError: javax/xml/bind/DatatypeConverter`。
- Jellyfin 无需 SDK，直接按其 REST 接口调用即可（见《项目介绍与技术架构》4.4 节）。
