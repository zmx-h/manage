# IECMP 创业中心管理平台 接口开发文档

| 项目 | 内容 |
| --- | --- |
| 系统名称 | IECMP（Innovation & Entrepreneurship Center Management Platform）创业中心管理平台 |
| 建设单位 | 中原工学院软件学院创新创业中心 |
| 文档名称 | 接口开发文档 |
| 文档版本 | V1.6 |
| 编写日期 | 2026-09-22 |
| 编写人员 | 后端组 |
| 后端技术栈 | Spring Boot + MyBatis-Plus + MySQL 8 + Redis |
| 前端技术栈 | Vue 3 + Axios |
| 文档状态 | **完整版 V1.6**：全局规范、模块与接口总览、数据模型、文件存储方案已定稿；**8 个模块共 186 个接口的明细已全部编写完成**（含 V1.4 新增的 `5-25`、`5-26`、`6-19`、`8-20` ~ `8-22` 共 6 个接口） |

## 修订记录

| 日期 | 版本 | 说明 | 编写人 |
| --- | --- | --- | --- |
| 2026-09-21 | V0.1 | 初版草案：全局规范、8 个模块接口清单、数据模型，模块一接口明细 | 后端组 |
| 2026-09-21 | V0.2 | 合入第一轮评审意见：确认响应码 / 分页 / 主键 / 认证 / Jellyfin 用途；新增成员接入、注册与邀请码、座位调换、荣誉审核、视频资源接口 | 后端组 |
| 2026-09-22 | V0.3 | 确认模块二关联方式与统计范围、预算随时申请、视频归入公共模块并命名为「学长学姐展板」；补全模块八 19 个接口明细 | 后端组 |
| 2026-09-22 | V1.0 | 按《系统接口规范》模板重构全文：统一单接口明细模板；模块八接口编号重排为连续 `8-01` ~ `8-19`；修正响应码体系概念冲突、表头写法不统一、错误码重复等问题；会议过程记录移入附录；新增模块二～模块七规范化占位章节 | 后端组 |
| 2026-09-22 | V1.1 | 文件存储介质由「本地磁盘 + 静态资源映射」变更为**阿里云 OSS**（后端中转上传、Bucket 公共读）：更新 2.9 文件上传规范、3.4 FileVO 说明、5.8 `sys_file` 字段（`path` → `object_key`，新增 `bucket`）、14.1 文件存储约定；登记 A19 确认事项并关闭 Q4 待确认项 | 后端组 |
| 2026-09-23 | V1.2 | 接口明细全面**自包含化**：清除全部「与 x.y.z 一致」「在 x.y.z 基础上增加 / 去掉」的指针式写法（涉及 1-02、1-09、1-11、1-18、8-01、8-02、8-06、8-08、8-16、8-18 共 10 个接口），改为在本接口内完整列出请求参数与返回字段；分页返回明确列出 `total` / `pageNum` / `pageSize` / `pages` / `list` 五个外壳字段；2.10 节新增「禁止用引用替代正文」的编写要求 | 后端组 |
| 2026-09-23 | V1.3 | **补齐模块二～模块七共 132 个接口的完整明细**，全文 180 个接口明细 100% 完成：新增 8 个章节（含 6 张补充数据表）、错误码总表补齐 20001~79999 号段共 90 个业务错误码、数据模型补充 `sys_dict` 与 `sys_message`、第 15 章由「编写计划」改为「完成情况」 | 后端组 |
| 2026-09-24 | V1.4 | 第三轮评审修订（合计 186 个接口）：① **模块二改为全自动采集**——用户端只读的「我的同步状态」（`2-06`）替代原手动同步，5.2 新增 `repo_account.sync_status`，同步日志口径改为「1 定时自动 / 2 管理员手动补采」，后台原「全量同步」更名「手动补采（`2-17`）」并限定为兜底场景；② **模块五新增管理端可拖拽平面图**——新增 `5-25`（平面图数据·后台）与 `5-26`（拖动移动 / 互换），明确「布局改坐标（`5-13`）／座位改使用人（`5-26`）」分工；③ **补齐站内消息接口** `8-20` ~ `8-22`（此前只有 `sys_message` 表与写入方，成员端无处查看）；④ **补齐荣誉信息再修改链路** `6-19`（被驳回 / 已通过后在原记录上修改并重新送审）；⑤ 全文一致性修订：清单名称与明细名称逐条对齐、权限码文案统一（清除「可匿名」「限本人上传」等混合写法）、章节分隔符统一、16.1 补充 50015~50017 / 60008 / 80012~80013、16.3 补 `seat:seat:list`、4 个失效错误码标注「保留」、新增 5-04 待审核去重规则 | 后端组 |
| 2026-09-24 | V1.5 | 第四轮一致性校验修订（接口数量不变，仍为 186 个）：① 修正 `5-24` 调换审核的 `auditStatus` 取值（原 `2 通过 / 3 拒绝` → 统一为全平台一致的 `1 通过 / 2 驳回`），并在 3.2 节把「记录状态」与「审核动作入参」两套同名枚举拆开说明，消除歧义；② 为 `1-15`、`1-21` 两个「`/admin` 路径 + 登录即可」的元数据接口补充设计说明，避免被误读为越权；③ 修正文末页脚残留的旧版本号；④ 新增待确认事项 Q10（成员自助请假）、Q11（借用申请撤销）、Q12（展板与视频命名） | 后端组 |
| 2026-09-24 | V1.6 | 与《数据库设计》V1.2 对齐的第五轮修订（接口数量不变，仍为 186 个）：① **修正 `deleted` 语义描述**——2.6 节与 3.2 节原写「0 未删除 / 1 已删除」，与数据库实际约定（删除时把 `deleted` 置为本行 `id`，且 `deleted` 参与全部 18 个唯一索引）矛盾，现统一更正并指向《数据库设计》1.5 节；② 6.3 节「仅当账号从未产生任何业务数据时，才允许**物理删除**」与 `1-14` 自身写明的「逻辑删除」自相矛盾，已改为逻辑删除并说明账号名 / 学号随即被释放；③ `1-14` 补「删除后账号名可被重新注册、历史数据仍按原 `id` 保留」的说明；④ `1-22` 补「注册开关存放在后端 `application.yml`、无配置表、也无管理端修改接口」的说明；⑤ `1-26` 补「`initialPassword` 明文只在响应中出现一次、数据库只存 BCrypt 哈希」的说明；⑥ `2-17` 的 `fullSync` 参数补明确语义（0 增量：从 `repo_account.last_sync_time` 起拉；1 全量：忽略上次时间重拉全部历史），并说明它**不落库**、以及何时必须传 `1`；⑦ 修正修订记录表格被空行割裂成两张表的问题 | 后端组 |

> **变更约定**：本文档为接口设计的唯一权威来源。任何字段、路径、错误码、枚举的变更，须先更新本文档并在修订记录中登记，再改动代码。

---

## 目录

- [第 1 章 总则](#第-1-章-总则)
  - 1.1 编写目的与适用范围
  - 1.2 术语与阅读约定
  - 1.3 服务地址与环境
  - 1.4 认证与鉴权
  - 1.5 传输安全与敏感数据处理
  - 1.6 其它公共内容
- [第 2 章 全局接口规范](#第-2-章-全局接口规范)
  - 2.1 URL 路径规范
  - 2.2 请求方法与语义
  - 2.3 统一响应结构
  - 2.4 分页规范
  - 2.5 数据格式规范
  - 2.6 命名规范
  - 2.7 权限码规范
  - 2.8 状态码与错误码
  - 2.9 文件上传规范
  - 2.10 接口明细编写模板
- [第 3 章 通用数据结构](#第-3-章-通用数据结构)
- [第 4 章 模块与接口总览](#第-4-章-模块与接口总览)
- [第 5 章 数据模型总览](#第-5-章-数据模型总览)
- [第 6 章 成员接入方案（注册与账号生命周期）](#第-6-章-成员接入方案)
- [第 7 章 模块一：用户与权限管理（system）](#第-7-章-模块一用户与权限管理system)
- [第 8 章 模块二：代码仓库关联（repo）](#第-8-章-模块二代码仓库关联repo)
- [第 9 章 模块三：部门预算管理（budget）](#第-9-章-模块三部门预算管理budget)
- [第 10 章 模块四：部门财产管理（asset）](#第-10-章-模块四部门财产管理asset)
- [第 11 章 模块五：部门工位管理（seat）](#第-11-章-模块五部门工位管理seat)
- [第 12 章 模块六：学长学姐展板（honor）](#第-12-章-模块六学长学姐展板honor)
- [第 13 章 模块七：值日排班管理（duty）](#第-13-章-模块七值日排班管理duty)
- [第 14 章 模块八：通用支撑（common）](#第-14-章-模块八通用支撑common)
- [第 15 章 接口明细完成情况](#第-15-章-接口明细完成情况)
- [第 16 章 附录](#第-16-章-附录)

---

## 第 1 章 总则

### 1.1 编写目的与适用范围

本文档详细定义了 IECMP 创业中心管理平台对外提供的接口规范与调用方法，作为前端、后端、各模块负责人与测试人员协同开发的统一依据。

本文档覆盖以下接口范围：

1. 平台登录、令牌刷新与退出登录接口规范；
2. 成员注册、邀请码与账号生命周期管理接口规范；
3. 业务模块接口规范：代码仓库关联、部门预算管理、部门财产管理、部门工位管理、学长学姐展板、值日排班管理；
4. 通用支撑接口规范：文件上传、数据字典、公告、数据看板、操作日志、视频资源。

本文档**不覆盖**以下内容：页面视觉与交互规范、部署与运维手册、数据库建表脚本。上述内容请查阅各自的独立文档。

### 1.2 术语与阅读约定

**术语表**

| 术语 | 含义 |
| --- | --- |
| 前台 / 成员端 | 面向部门成员的页面，接口统一以 `/user/**` 为前缀 |
| 后台 / 管理端 | 面向负责人、管理员的页面，接口统一以 `/admin/**` 为前缀 |
| 公共接口 | 无需登录即可访问的接口，统一以 `/common/**` 为前缀 |
| 令牌 | 登录成功后下发的 JWT 字符串，用于身份校验 |
| 接口编号 | 形如 `1-01`，即「模块序号-接口序号」，全文唯一；编号一经确定不随章节调整而变化 |
| 必须 / 非必须 | 参数或字段是否必填；为保持全文一致，统一使用该组表述，不使用「是 / 否」 |
| `data` | 统一响应体中承载业务数据的字段 |

**阅读约定**

1. 每个接口的「返回数据」章节**只描述 `data` 字段的内部结构**；`code` 与 `msg` 遵循 2.3 节统一约定，不再重复列出。
2. 分页接口的通用请求参数见 2.4 节，各接口只列出自身特有的查询条件。
3. 标注「**待补充**」的内容为尚未确定的约定，填写前请先与后端组确认。
4. 引用其他章节写作「见 x.y 节」，引用其他接口写作「接口 x-xx」。

### 1.3 服务地址与环境

> 对应《系统接口规范》模板中的「测试地址」章节。

| 环境 | 地址 | 说明 |
| --- | --- | --- |
| 开发环境（后端） | `http://localhost:8080` | 本机启动 Spring Boot 服务 |
| 开发环境（前端） | `http://localhost:5173` | Vite 开发服务器，已配置跨域白名单 |
| 测试环境 | `https://<FRP 域名>` **待补充** | 由 FRP 内网穿透映射，域名确定后回填此处 |
| 生产环境 | `https://<正式域名>` **待补充** | 上线前确定，必须启用 HTTPS |

**约定**

1. 本文档中的接口地址一律以**相对路径**书写（如 `/admin/login`），完整地址 = 服务地址 + 相对路径。
2. 若在网关或 `server.servlet.context-path` 追加统一前缀（如 `/api`），由前端统一拼接，本文档不重复书写该前缀。
3. 服务可用性可通过接口 `8-14`（健康检查）探测。
4. 播放资源地址（Jellyfin）的域名必须与 FRP 映射保持一致，否则内网地址在公网无法播放，详见 5.9 节。

### 1.4 认证与鉴权

> 对应《系统接口规范》模板中的「签名方法」章节。本平台**不使用 `sign` 参数签名机制**，改用 **JWT 令牌 + HTTPS** 方案。

1. 除 `/common/**` 与 `/user/login`、`/admin/login` 外，所有接口必须携带令牌。
2. 请求头固定如下：

| 参数名称 | 参数值 | 是否必须 | 说明 |
| --- | --- | --- | --- |
| Content-Type | application/json | 必须（文件上传接口除外） | 文件上传使用 multipart/form-data |
| Authorization | Bearer {accessToken} | 必须 | 令牌以 `Bearer ` 开头，`Bearer` 与令牌之间为一个空格 |

3. 令牌策略：`accessToken` 有效期 2 小时，`refreshToken` 有效期 7 天；令牌临近过期时由前端调用接口 `1-04` 静默续期。
4. 退出登录时，当前令牌写入 Redis 黑名单直至自然过期。
5. 权限校验：`/admin/**` 接口需具备对应权限码（见 2.7），无权限时返回业务码 `403`。
6. 前台 `/user/**` 接口不校验权限码，仅校验登录状态；数据可见性由「本人数据 / 公示数据」规则控制。
7. 登录保护：同一账号 5 分钟内连续失败 5 次，锁定 10 分钟。

### 1.5 传输安全与敏感数据处理

> 对应《系统接口规范》模板中的「加密解密」章节。本平台**不采用**「全字段加密请求 + 响应体解密」方案，改为以下约定。

| 项目 | 约定 |
| --- | --- |
| 传输层加密 | 生产环境全量 HTTPS（TLS），避免明文传输；开发环境允许 HTTP |
| 登录密码 | 前端以明文提交，服务端使用 BCrypt 加盐存储；**不自行实现前端加密算法** |
| 第三方令牌 | 成员绑定的 Gitee / GitHub `AccessToken` 使用 AES 加密后落库，接口返回时一律脱敏 |
| 敏感字段脱敏 | 手机号、邮箱、令牌在列表展示与操作日志中脱敏处理 |
| 参数签名 | 本平台不做参数签名验签，防篡改依赖 HTTPS 与令牌校验 |

### 1.6 其它公共内容

> 对应《系统接口规范》模板中的「其它公共内容」章节。

| 项目 | 约定 |
| --- | --- |
| 字符编码 | 全文 UTF-8 |
| 请求体格式 | `application/json;charset=UTF-8`，文件上传为 `multipart/form-data` |
| 跨域 | 开发环境允许 `http://localhost:5173`；生产环境同域部署或由 Nginx 统一转发 |
| 限流 | 登录、验证码、注册、同步类接口做频率限制，超限返回 `429` |
| 操作日志 | `/admin/**` 的写操作统一记录操作日志：操作人、IP、入参摘要、执行结果 |
| 幂等性 | 审核、状态流转类接口在服务端校验当前状态，状态不符时返回业务错误码，避免重复提交 |
| 并发控制 | 预算金额、工位分配使用乐观锁（`version` 字段）或唯一索引兜底 |
| 逻辑删除 | 删除接口默认逻辑删除；被引用的数据禁止删除并返回业务错误码 |
| 时间范围 | 统一使用 `beginTime` / `endTime` 命名，端点是否包含以各接口说明为准 |

---

## 第 2 章 全局接口规范

### 2.1 URL 路径规范

| 前缀 | 面向对象 | 说明 |
| --- | --- | --- |
| `/user/**` | 前台（成员端） | 成员登录后可见的查询、申请、公示类接口 |
| `/admin/**` | 后台（管理端） | 负责人 / 管理员的新增、修改、删除、审核类接口 |
| `/common/**` | 公共 | 无需登录的字典、验证码、健康检查等接口 |

**约定**

1. 路径统一小写，多单词使用短横线连接，例如 `/admin/budget-item`。
2. 路径中不出现动词，动作由 HTTP 方法表达；确有语义动作时使用名词化子资源，如 `/admin/user/{id}/status`。
3. 路径参数统一使用花括号包裹，如 `{id}`。
4. 生产部署时可在网关或 `server.servlet.context-path` 追加统一前缀（如 `/api`），本文档不重复书写该前缀。
5. 需要版本化时在统一前缀后追加 `/v1`，不在业务路径中体现。

### 2.2 请求方法与语义

| 方法 | 语义 | 幂等 | 典型场景 |
| --- | --- | --- | --- |
| GET | 查询 | 是 | 分页、详情、统计、图表 |
| POST | 新增 / 提交动作 | 否 | 新增记录、提交申请、触发同步 |
| PUT | 更新 / 状态变更 | 是 | 修改记录、审核、启停、分配 |
| DELETE | 删除（默认逻辑删除） | 是 | 删除记录、解绑账户 |

### 2.3 统一响应结构

除文件流下载外，所有接口统一返回如下三段式结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| code | integer | 必须 | - | 业务状态码，`200` 表示成功，其余见 2.8 节 | format: int32 |
| data | object | 非必须 | null | 业务数据；失败时为 null | - |
| msg | string | 非必须 | - | 提示信息；成功返回 `success`，失败返回可直接展示的中文提示 | - |

**成功示例**

```json
{
  "code": 200,
  "data": { "id": 1 },
  "msg": "success"
}
```

**失败示例**

```json
{
  "code": 10003,
  "data": null,
  "msg": "用户名或密码错误"
}
```

> **说明**：后文各接口的「返回数据」章节仅描述 `data` 字段的内部结构，`code` 与 `msg` 遵循本节约定，不再重复列出。

### 2.4 分页规范

**请求参数**（查询类接口通用，各接口只额外列出自身参数）

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| pageNum | integer | 非必须 | 1 | 页码，从 1 开始 | format: int32，最小值 1 |
| pageSize | integer | 非必须 | 10 | 每页条数 | format: int32，范围 1~100 |
| keyword | string | 非必须 | - | 模糊搜索关键字，匹配字段见各接口说明 | - |
| orderBy | string | 非必须 | create_time desc | 排序，格式 `{字段名} {asc 或 desc}` | 各接口限制允许排序的字段白名单 |

**返回结构**（`data` 字段）

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| total | integer | 必须 | - | 总记录数 | format: int64 |
| pageNum | integer | 必须 | - | 当前页码 | format: int32 |
| pageSize | integer | 必须 | - | 每页条数 | format: int32 |
| pages | integer | 必须 | - | 总页数 | format: int32 |
| list | array | 必须 | - | 当前页数据列表，元素结构见各接口 | - |

**分页响应示例**

```json
{
  "code": 200,
  "data": {
    "total": 42,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 5,
    "list": []
  },
  "msg": "success"
}
```

### 2.5 数据格式规范

| 类型 | 约定 | 示例 |
| --- | --- | --- |
| 日期时间 | `yyyy-MM-dd HH:mm:ss`，时区 GMT+8，字符串传输 | `2026-09-21 14:30:00` |
| 日期 | `yyyy-MM-dd` | `2026-09-21` |
| 金额 | `decimal(12,2)`，JSON 以 number 传输，最多两位小数 | `1500.00` |
| 布尔语义 | 统一使用 integer：1 是，0 否 | `"isPublic": 1` |
| 状态类字段 | 统一使用 integer 枚举，见 3.2 节与 16.2 节 | `"status": 1` |
| 空值 | 无值返回 `null`，不返回空字符串；列表无数据返回空数组 `[]` | - |
| 长文本 | 富文本以 HTML 字符串传输，前端需做 XSS 过滤 | - |
| 主键 | 数据库自增 bigint，JSON 以 number 返回 | `"id": 1024` |

### 2.6 命名规范

1. 请求与响应字段统一使用小驼峰（lowerCamelCase）。
2. 数据库表名使用下划线小写并带模块前缀：`sys_`、`repo_`、`budget_`、`asset_`、`seat_`、`honor_`、`duty_`。
3. 每个业务表固定包含审计字段：`create_by`、`create_time`、`update_by`、`update_time`、`deleted`（逻辑删除标记：0 未删除；已删除时写入本行 `id`，见《数据库设计》1.5 节）。
4. 公共查询参数命名统一：时间范围 `beginTime` / `endTime`，状态 `status`，关键字 `keyword`。

### 2.7 权限码规范

1. 格式：`模块:资源:操作`，全小写冒号分隔，例如 `system:user:list`。
2. 操作枚举：`list`（分页 / 列表）、`query`（详情）、`add`（新增）、`edit`（修改）、`remove`（删除）、`audit`（审核）、`assign`（分配）、`exec`（执行 / 触发）、`export`（导出）、`generate`（生成）、`publish`（发布）、`public`（公示）、`reset`（重置）、`auth`（授权）、`config`（配置）。
3. 前台 `/user/**` 接口不分配权限码，仅要求登录；数据可见性由「本人数据 / 公示数据」规则控制。
4. 权限码总表见 16.3 节。

### 2.8 状态码与错误码

本节明确区分两个概念，避免混用：

**（一）HTTP 状态码**

除文件流下载外，所有接口的 HTTP 状态码统一返回 `200`，以便前端统一处理。仅当请求未到达业务层（如网关超时、连接失败）时，才由容器或网关返回 `502` / `504` 等状态码。

**（二）业务状态码 `code`**

业务结果由响应体中的 `code` 字段表达，分为「成功」「通用错误」「模块业务错误」三类：

| code | 分类 | 含义 | 处理建议 |
| --- | --- | --- | --- |
| 200 | 成功 | 请求成功 | 正常处理 `data` |
| 400 | 通用错误 | 参数校验失败 | 前端提示 `msg` 并定位表单字段 |
| 401 | 通用错误 | 未登录或登录已过期 | 前端清空令牌并跳转登录页 |
| 403 | 通用错误 | 权限不足 | 前端提示无权限 |
| 404 | 通用错误 | 资源不存在 | 提示记录已被删除 |
| 405 | 通用错误 | 请求方法不支持 | 检查接口定义 |
| 429 | 通用错误 | 请求过于频繁 | 稍后重试 |
| 500 | 通用错误 | 服务器内部错误 | 上报后端排查 |
| 10001 ~ 89999 | 模块业务错误 | 各模块业务错误码，取值见 16.1 节 | 按 `msg` 提示用户；前端如需区分场景，可按码分支处理 |

**（三）模块业务错误码分段**

| 段位 | 归属模块 |
| --- | --- |
| 10001 ~ 19999 | 模块一 · 用户与权限 |
| 20001 ~ 29999 | 模块二 · 代码仓库关联 |
| 30001 ~ 39999 | 模块三 · 部门预算 |
| 40001 ~ 49999 | 模块四 · 部门财产 |
| 50001 ~ 59999 | 模块五 · 部门工位 |
| 60001 ~ 69999 | 模块六 · 学长学姐展板 |
| 70001 ~ 79999 | 模块七 · 值日排班 |
| 80001 ~ 89999 | 模块八 · 文件与通用支撑 |

具体清单见 16.1 节。

### 2.9 文件上传规范

1. 请求方式 `POST`，`Content-Type: multipart/form-data`。
2. 参数名固定为 `file`（批量上传为 `files`），可附加 `bizType` 标识业务归属（如 `asset`、`honor`、`budget`）。
3. 限制：图片 ≤ 5MB（jpg / jpeg / png / webp / gif），文档 ≤ 20MB（pdf / doc / docx / xls / xlsx）。
4. 返回统一为文件信息对象（见 3.4 节 FileVO）；业务表仅保存 `fileId` 与 `url`，便于后续更换存储介质。
5. **存储介质**：**阿里云 OSS（对象存储）**。**上传链路为「后端中转」**——前端将文件提交至平台后端，由后端携带 AccessKey 上传至 OSS，再将可访问 URL 返回前端；前端全程不接触 AccessKey。
6. **对象键（objectKey）约定**：`upload/{bizType}/{yyyy}/{MM}/{uuid}.{ext}`，例如 `upload/avatar/2026/09/9f2c1a7b.png`。
7. **访问方式**：Bucket 为**公共读**，文件对外可直接访问，URL 形如 `https://{bucket}.{endpoint}/upload/{bizType}/{yyyy}/{MM}/{uuid}.{ext}`。文件名使用 UUID 并保留原始扩展名，避免被枚举猜测。
8. **安全约束**：
   - 后端上传前须做 MIME 类型与文件头校验（不能只判断扩展名），并强制重命名为 UUID；
   - 后端记录 `sys_file.object_key`；删除文件时按 `object_key` 精确删除 OSS 对象；
   - AccessKey 仅存放于后端配置（环境变量或配置中心），**严禁下发前端、严禁提交进代码仓库**。
9. 字段语义变更：原 `sys_file.path`（本地磁盘路径）**重命名为 `object_key`**，并新增 `bucket` 字段记录存储桶名称，见 5.8 节。

### 2.10 接口明细编写模板

本文档每个接口统一按下列结构编写，团队新增接口必须保持一致。该模板在《系统接口规范》模板的「功能描述 / 接口方式 / 接口地址 / 接口方法 / 请求字段 / 返回数据」六段式基础上，补充了「接口编号 / 权限码 / 错误码 / 响应示例」，以适配内部前后端协作需要。

**（一）基本信息表**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | `1-01`（模块序号-接口序号，与第 4 章总览一致） |
| 接口名称 | 后台登录 |
| Path | `/admin/login` |
| Method | `POST` |
| 接口描述 | 一句话说明接口作用 |
| 权限码 | `system:user:list` 或「登录即可」或「无（公开接口）」 |
| 是否需令牌 | 是 / 否 |

**（二）请求参数**（按需出现；无参数时统一写作「无」）

- Headers 表：`| 参数名称 | 参数值 | 是否必须 | 说明 |`，仅在需要特别说明时列出。
- 路径参数表：`| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |`
- Query 参数表 / Body 参数表：`| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |`

**（三）返回数据**

统一使用 `| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |`，仅描述 `data` 字段的内部结构。

**（四）错误码**（可选）

一行文本，列出本接口可能返回的模块业务错误码及含义。

**（五）响应示例**（可选，返回关键数据的接口须提供）

完整 JSON 示例，必须包含 `code` / `data` / `msg` 三个字段。

**编写要求**

1. 「是否必须」列统一填 `必须` / `非必须`，禁止使用「是 / 否」。
2. 类型统一使用小写：`string`、`integer`、`number`、`boolean`、`object`、`array`、`file`。
3. 「其他信息」列用于标注 `format: int32` / `format: int64` 等约束，无内容时留空。
4. **接口明细必须自包含**。禁止用「与 x.y.z 一致」「在 x.y.z 基础上增加 / 去掉」这类**指向另一个接口**的写法来替代正文——请求参数与返回数据必须在本接口内完整列出，读者读一个接口就能知道该传什么、会收到什么，无需跳到别处。
5. **确实不需要的参数或字段，直接省略即可**，不要用一句含糊的话带过。若某字段「不可修改」或「由其他接口负责」，用引用块单独说明一句即可。
6. **共享结构**（分页对象、文件信息对象等）需在本接口内列出字段清单，可另加一句「字段含义见 x.y 节」作为补充，但**不能只写引用**。
7. 引用其他章节写作「见 x.y 节」，引用其他接口写作「接口 x-xx」——仅用于**补充说明**，不得作为内容的替代。

---

## 第 3 章 通用数据结构

本章定义被多个模块复用的数据结构。各接口的返回数据如引用本章结构，将在接口明细中直接写明。

### 3.1 分页对象

分页请求参数与返回结构见 2.4 节。

### 3.2 通用枚举

| 枚举字段 | 取值 | 说明 |
| --- | --- | --- |
| status（通用启停） | 0 / 1 | 0 停用（禁用），1 启用（正常） |
| status（审核类记录状态） | 0 / 1 / 2 / 3 | 0 待审核，1 已通过，2 已驳回，3 已撤销 |
| auditStatus（审核动作入参） | 1 / 2 | 1 通过，2 驳回；**仅作为各模块审核接口的请求参数**，与记录自身的 `status` 不是同一套取值，服务端负责映射 |
| deleted（删除标记） | 0 / 非 0 | 0 未删除；已删除时写入本行 `id`（**不是 1**）。该值参与唯一索引，使删除后同一业务值可被重新占用，详见《数据库设计》1.5 节。前端不感知此字段 |
| isPublic（公示标记） | 0 / 1 | 0 不公示，1 公示中 |

各模块自有枚举在对应章节列出，汇总见 16.2 节。

### 3.3 统一响应对象

见 2.3 节。

### 3.4 通用文件对象 FileVO

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| fileId | integer | 必须 | - | 文件主键 | format: int64 |
| name | string | 必须 | - | 原始文件名 | - |
| url | string | 必须 | - | 访问地址；接入 OSS 后为 OSS 完整 URL（Bucket 公共读） | - |
| size | integer | 必须 | - | 文件大小（字节） | format: int64 |
| contentType | string | 非必须 | - | MIME 类型 | - |
| bizType | string | 非必须 | - | 业务归属标识 | - |

---

## 第 4 章 模块与接口总览

本章给出平台全部 8 个模块的划分、接口清单与优先级建议。第 7 章、第 8~14 章为各模块的接口明细，未编写明细的模块在第 8~13 章以规范化占位章节说明。

### 4.1 模块划分

| 编号 | 模块 | 表前缀 | 对应需求文档条目 | 说明 |
| --- | --- | --- | --- | --- |
| 模块一 | 用户与权限管理 | `sys_` | 功能需求 7 | 账号、角色、权限、登录认证，是所有模块的基础 |
| 模块二 | 代码仓库关联 | `repo_` | 功能需求 1 | Gitee / GitHub 账户绑定、提交记录采集、进度与贡献度图表 |
| 模块三 | 部门预算管理 | `budget_` | 功能需求 2 | 预算周期、科目、申请、审核、支出、公示 |
| 模块四 | 部门财产管理 | `asset_` | 功能需求 3 | 资产台账、分类、领用借用、变动流水、盘点，与预算联动 |
| 模块五 | 部门工位管理 | `seat_` | 功能需求 4 | 区域平面图、工位坐标、申请与分配 |
| 模块六 | 学长学姐展板 | `honor_` | 功能需求 5 | 已毕业学长学姐的信息与荣誉记录，前台动态展板 |
| 模块七 | 值日排班管理 | `duty_` | 功能需求 6 | 排班计划、自动排班、公示、换班、提醒 |
| 模块八 | 通用支撑 | `sys_file` 等 | 功能需求 8 | 文件上传、字典、公告、看板、操作日志、视频资源 |

### 4.2 模块一：用户与权限管理（`/user`、`/admin`、`/common`）

明细见第 7 章。

| 接口编号 | 接口名称 | 方法 | 路径 | 权限码 |
| --- | --- | --- | --- | --- |
| 1-01 | 后台登录 | POST | `/admin/login` | 无（公开接口） |
| 1-02 | 前台登录 | POST | `/user/login` | 无（公开接口） |
| 1-03 | 退出登录 | POST | `/user/logout` | 登录即可 |
| 1-04 | 刷新令牌 | POST | `/user/token/refresh` | 无（凭 refreshToken） |
| 1-05 | 当前用户信息 | GET | `/user/info` | 登录即可 |
| 1-06 | 修改个人资料 | PUT | `/user/info` | 登录即可 |
| 1-07 | 修改密码 | PUT | `/user/password` | 登录即可 |
| 1-08 | 用户分页查询 | GET | `/admin/user/page` | `system:user:list` |
| 1-09 | 用户详情 | GET | `/admin/user/{id}` | `system:user:query` |
| 1-10 | 新增用户 | POST | `/admin/user` | `system:user:add` |
| 1-11 | 修改用户 | PUT | `/admin/user` | `system:user:edit` |
| 1-12 | 启用 / 禁用用户 | PUT | `/admin/user/{id}/status` | `system:user:edit` |
| 1-13 | 重置密码 | PUT | `/admin/user/{id}/password/reset` | `system:user:reset` |
| 1-14 | 删除用户 | DELETE | `/admin/user/{id}` | `system:user:remove` |
| 1-15 | 角色下拉列表 | GET | `/admin/role/list` | 登录即可 |
| 1-16 | 角色分页查询 | GET | `/admin/role/page` | `system:role:list` |
| 1-17 | 新增角色 | POST | `/admin/role` | `system:role:add` |
| 1-18 | 修改角色 | PUT | `/admin/role` | `system:role:edit` |
| 1-19 | 删除角色 | DELETE | `/admin/role/{id}` | `system:role:remove` |
| 1-20 | 角色授权 | PUT | `/admin/role/{id}/permission` | `system:role:auth` |
| 1-21 | 权限码树 | GET | `/admin/permission/tree` | 登录即可 |
| 1-22 | 注册方式配置查询 | GET | `/common/register/config` | 无（公开接口） |
| 1-23 | 邀请码注册 | POST | `/user/register` | 无（公开接口） |
| 1-24 | 提交注册申请 | POST | `/common/register/apply` | 无（公开接口） |
| 1-25 | 注册申请分页 | GET | `/admin/register/apply/page` | `system:register:list` |
| 1-26 | 注册申请审核 | PUT | `/admin/register/apply/{id}/audit` | `system:register:audit` |
| 1-27 | 邀请码分页 | GET | `/admin/invite-code/page` | `system:invite:list` |
| 1-28 | 生成邀请码 | POST | `/admin/invite-code` | `system:invite:add` |
| 1-29 | 作废邀请码 | DELETE | `/admin/invite-code/{id}` | `system:invite:remove` |

### 4.3 模块二：代码仓库关联（`/user/repo`、`/admin/repo`）

明细见第 8 章。

| 接口编号 | 接口名称 | 方法 | 路径 | 权限码 |
| --- | --- | --- | --- | --- |
| 2-01 | 支持的平台列表 | GET | `/user/repo/platform/list` | 登录即可 |
| 2-02 | 绑定代码托管账户 | POST | `/user/repo/account` | 登录即可 |
| 2-03 | 我的绑定账户列表 | GET | `/user/repo/account/list` | 登录即可 |
| 2-04 | 更新绑定账户令牌 | PUT | `/user/repo/account` | 登录即可 |
| 2-05 | 解绑账户 | DELETE | `/user/repo/account/{id}` | 登录即可 |
| 2-06 | 我的同步状态 | GET | `/user/repo/sync/status` | 登录即可 |
| 2-07 | 我的仓库分页 | GET | `/user/repo/project/page` | 登录即可 |
| 2-08 | 开启 / 关闭仓库统计 | PUT | `/user/repo/project/{id}/track` | 登录即可 |
| 2-09 | 提交记录分页 | GET | `/user/repo/commit/page` | 登录即可 |
| 2-10 | 提交-日期趋势图 | GET | `/user/repo/commit/chart` | 登录即可 |
| 2-11 | 贡献热力图 | GET | `/user/repo/commit/heatmap` | 登录即可 |
| 2-12 | 个人贡献概览 | GET | `/user/repo/stat/overview` | 登录即可 |
| 2-13 | 账户与仓库分页（后台） | GET | `/admin/repo/account/page` | `repo:account:list` |
| 2-14 | 成员贡献排行（后台） | GET | `/admin/repo/stat/rank` | `repo:stat:list` |
| 2-15 | 中心提交趋势（后台） | GET | `/admin/repo/stat/trend` | `repo:stat:list` |
| 2-16 | 同步日志分页（后台） | GET | `/admin/repo/sync/log/page` | `repo:sync:list` |
| 2-17 | 手动补采（后台） | POST | `/admin/repo/sync` | `repo:sync:exec` |

> **口径说明**：核心指标为**提交数量**，按日期聚合渲染 GitHub 风格活跃度热力图（提交越多颜色越深）；统计范围为**所有分支**的提交，但本期只采集当前绑定账户**本人名下**的仓库，组织仓库与他人仓库暂不纳入；代码行数与活跃天数一并采集入库，作为贡献度算法的候选指标，权重待团队会议确定。
>
> **采集方式**：全部由后台定时任务自动完成（绑定账户后立即采集首次数据，此后每日增量采集），**成员端只读、不提供手动同步**；合并提交默认不计入统计。管理员可用 `2-17` 手动补采。

### 4.4 模块三：部门预算管理（`/user/budget`、`/admin/budget`）

明细见第 9 章。

| 接口编号 | 接口名称 | 方法 | 路径 | 权限码 |
| --- | --- | --- | --- | --- |
| 3-01 | 公示预算周期分页 | GET | `/user/budget/period/page` | 登录即可 |
| 3-02 | 预算周期详情 | GET | `/user/budget/period/{id}` | 登录即可 |
| 3-03 | 预算科目明细 | GET | `/user/budget/period/{id}/item/list` | 登录即可 |
| 3-04 | 支出明细公示 | GET | `/user/budget/expense/page` | 登录即可 |
| 3-05 | 提交预算申请 | POST | `/user/budget/apply` | 登录即可 |
| 3-06 | 我的预算申请分页 | GET | `/user/budget/apply/my/page` | 登录即可 |
| 3-07 | 预算申请详情 | GET | `/user/budget/apply/{id}` | 登录即可 |
| 3-08 | 撤销预算申请 | PUT | `/user/budget/apply/{id}/cancel` | 登录即可 |
| 3-09 | 我的经费汇总 | GET | `/user/budget/stat/my` | 登录即可 |
| 3-10 | 预算周期分页（后台） | GET | `/admin/budget/period/page` | `budget:period:list` |
| 3-11 | 新增预算周期（起草） | POST | `/admin/budget/period` | `budget:period:add` |
| 3-12 | 修改预算周期 | PUT | `/admin/budget/period` | `budget:period:edit` |
| 3-13 | 删除预算周期 | DELETE | `/admin/budget/period/{id}` | `budget:period:remove` |
| 3-14 | 周期状态流转 | PUT | `/admin/budget/period/{id}/status` | `budget:period:audit` |
| 3-15 | 公示 / 取消公示 | PUT | `/admin/budget/period/{id}/public` | `budget:period:public` |
| 3-16 | 发起预算征集（v1.1 预留） | POST | `/admin/budget/period/{id}/collect` | `budget:collect:exec` |
| 3-17 | 新增预算科目 | POST | `/admin/budget/item` | `budget:item:edit` |
| 3-18 | 修改预算科目 | PUT | `/admin/budget/item` | `budget:item:edit` |
| 3-19 | 删除预算科目 | DELETE | `/admin/budget/item/{id}` | `budget:item:edit` |
| 3-20 | 申请审核分页（后台） | GET | `/admin/budget/apply/page` | `budget:apply:list` |
| 3-21 | 申请审核（通过 / 驳回） | PUT | `/admin/budget/apply/{id}/audit` | `budget:apply:audit` |
| 3-22 | 登记支出 | POST | `/admin/budget/expense` | `budget:expense:add` |
| 3-23 | 支出分页（后台） | GET | `/admin/budget/expense/page` | `budget:expense:list` |
| 3-24 | 删除支出记录 | DELETE | `/admin/budget/expense/{id}` | `budget:expense:remove` |
| 3-25 | 预算执行看板 | GET | `/admin/budget/stat/overview` | `budget:stat:list` |

> **口径说明**：预算由管理员统一管理，成员仅提交申请，且在预算周期**执行期间可随时提交**（无集中征集窗口）；采购与报销统一由管理员登记支出，形成「申请 → 审核 → 拨款 → 支出 → 公示」的完整资金链路，任何一笔资金流动均可追溯到申请人、审核人与凭证。

### 4.5 模块四：部门财产管理（`/user/asset`、`/admin/asset`）

明细见第 10 章。

| 接口编号 | 接口名称 | 方法 | 路径 | 权限码 |
| --- | --- | --- | --- | --- |
| 4-01 | 资产分页（前台） | GET | `/user/asset/page` | 登录即可 |
| 4-02 | 资产详情（前台） | GET | `/user/asset/{id}` | 登录即可 |
| 4-03 | 资产分类树 | GET | `/user/asset/category/tree` | 登录即可 |
| 4-04 | 资产总览统计 | GET | `/user/asset/stat/overview` | 登录即可 |
| 4-05 | 借用 / 领用申请 | POST | `/user/asset/borrow` | 登录即可 |
| 4-06 | 我的借用记录 | GET | `/user/asset/borrow/my/page` | 登录即可 |
| 4-07 | 归还登记（本人） | PUT | `/user/asset/borrow/{id}/return` | 登录即可 |
| 4-08 | 资产分页（后台） | GET | `/admin/asset/page` | `asset:info:list` |
| 4-09 | 资产详情（后台） | GET | `/admin/asset/{id}` | `asset:info:query` |
| 4-10 | 新增资产 | POST | `/admin/asset` | `asset:info:add` |
| 4-11 | 修改资产 | PUT | `/admin/asset` | `asset:info:edit` |
| 4-12 | 删除资产 | DELETE | `/admin/asset/{id}` | `asset:info:remove` |
| 4-13 | 资产状态变更 | PUT | `/admin/asset/{id}/status` | `asset:info:edit` |
| 4-14 | 分配保管人与存放位置 | PUT | `/admin/asset/{id}/assign` | `asset:info:assign` |
| 4-15 | 变动记录登记 | POST | `/admin/asset/record` | `asset:record:add` |
| 4-16 | 变动流水分页 | GET | `/admin/asset/record/page` | `asset:record:list` |
| 4-17 | 新增资产分类 | POST | `/admin/asset/category` | `asset:category:edit` |
| 4-18 | 修改资产分类 | PUT | `/admin/asset/category` | `asset:category:edit` |
| 4-19 | 删除资产分类 | DELETE | `/admin/asset/category/{id}` | `asset:category:edit` |
| 4-20 | 借用审核分页 | GET | `/admin/asset/borrow/page` | `asset:borrow:list` |
| 4-21 | 借用审核 | PUT | `/admin/asset/borrow/{id}/audit` | `asset:borrow:audit` |
| 4-22 | 归还登记（后台） | PUT | `/admin/asset/borrow/{id}/return` | `asset:borrow:edit` |
| 4-23 | 发起盘点 | POST | `/admin/asset/inventory` | `asset:inventory:add` |
| 4-24 | 盘点任务分页 | GET | `/admin/asset/inventory/page` | `asset:inventory:list` |
| 4-25 | 盘点明细登记 | PUT | `/admin/asset/inventory/item/{id}` | `asset:inventory:exec` |
| 4-26 | 结束盘点 | PUT | `/admin/asset/inventory/{id}/finish` | `asset:inventory:exec` |
| 4-27 | 资产台账导出 | GET | `/admin/asset/export` | `asset:info:export` |

> **口径说明**：一物一码（单件单记录），借用需审核；所有变动（领用 / 借用 / 归还 / 维修 / 报废 / 调拨 / 盘点）统一写入变动流水表，保证资产去向可追溯。

### 4.6 模块五：部门工位管理（`/user/seat`、`/admin/seat`）

明细见第 11 章。

| 接口编号 | 接口名称 | 方法 | 路径 | 权限码 |
| --- | --- | --- | --- | --- |
| 5-01 | 平面图数据 | GET | `/user/seat/plan` | 登录即可 |
| 5-02 | 工位列表 | GET | `/user/seat/list` | 登录即可 |
| 5-03 | 我的工位 | GET | `/user/seat/my` | 登录即可 |
| 5-04 | 提交工位申请 | POST | `/user/seat/apply` | 登录即可 |
| 5-05 | 我的工位申请分页 | GET | `/user/seat/apply/my/page` | 登录即可 |
| 5-06 | 撤销工位申请 | PUT | `/user/seat/apply/{id}/cancel` | 登录即可 |
| 5-07 | 区域列表（后台） | GET | `/admin/seat/area/list` | `seat:area:list` |
| 5-08 | 新增区域（上传平面图） | POST | `/admin/seat/area` | `seat:area:add` |
| 5-09 | 修改区域 | PUT | `/admin/seat/area` | `seat:area:edit` |
| 5-10 | 删除区域 | DELETE | `/admin/seat/area/{id}` | `seat:area:remove` |
| 5-11 | 新增工位 | POST | `/admin/seat` | `seat:seat:add` |
| 5-12 | 修改工位 | PUT | `/admin/seat` | `seat:seat:edit` |
| 5-13 | 批量保存工位布局坐标 | PUT | `/admin/seat/batch` | `seat:seat:edit` |
| 5-14 | 删除工位 | DELETE | `/admin/seat/{id}` | `seat:seat:remove` |
| 5-15 | 分配工位 | PUT | `/admin/seat/{id}/assign` | `seat:seat:assign` |
| 5-16 | 释放工位 | PUT | `/admin/seat/{id}/release` | `seat:seat:assign` |
| 5-17 | 工位状态变更 | PUT | `/admin/seat/{id}/status` | `seat:seat:edit` |
| 5-18 | 工位申请分页（后台） | GET | `/admin/seat/apply/page` | `seat:apply:list` |
| 5-19 | 工位申请审核 | PUT | `/admin/seat/apply/{id}/audit` | `seat:apply:audit` |
| 5-20 | 成员发起座位调换 | POST | `/user/seat/swap` | 登录即可 |
| 5-21 | 我的调换申请分页 | GET | `/user/seat/swap/my/page` | 登录即可 |
| 5-22 | 调换对方确认 / 拒绝 | PUT | `/user/seat/swap/{id}/handle` | 登录即可 |
| 5-23 | 调换申请分页（后台） | GET | `/admin/seat/swap/page` | `seat:swap:list` |
| 5-24 | 调换申请审核（负责人） | PUT | `/admin/seat/swap/{id}/audit` | `seat:swap:audit` |
| 5-25 | 平面图数据（后台） | GET | `/admin/seat/plan` | `seat:seat:list` |
| 5-26 | 平面图拖拽调整（移动 / 互换） | POST | `/admin/seat/plan/drag` | `seat:seat:assign` |

> **口径说明**：负责人可直接分配工位（接口 `5-15`），并可在管理端平面图上**直接拖动调整**——拖到空工位即移动、拖到已占用工位即互换（接口 `5-25`、`5-26`）；成员之间调换需「对方确认 + 负责人审核」两步完成，避免私下换座导致平面图与实际不符；平面图按「上传底图 + 百分比坐标」实现。

### 4.7 模块六：学长学姐展板（`/user/honor`、`/admin/honor`）

明细见第 12 章。

| 接口编号 | 接口名称 | 方法 | 路径 | 权限码 |
| --- | --- | --- | --- | --- |
| 6-01 | 展板学长分页 | GET | `/user/honor/alumni/page` | 登录即可 |
| 6-02 | 学长详情（荣誉时间轴） | GET | `/user/honor/alumni/{id}` | 登录即可 |
| 6-03 | 精选轮播 | GET | `/user/honor/alumni/highlight` | 登录即可 |
| 6-04 | 展板统计 | GET | `/user/honor/stat` | 登录即可 |
| 6-05 | 学长分页（后台） | GET | `/admin/honor/alumni/page` | `honor:alumni:list` |
| 6-06 | 新增学长 | POST | `/admin/honor/alumni` | `honor:alumni:add` |
| 6-07 | 修改学长 | PUT | `/admin/honor/alumni` | `honor:alumni:edit` |
| 6-08 | 删除学长 | DELETE | `/admin/honor/alumni/{id}` | `honor:alumni:remove` |
| 6-09 | 上板 / 下板 | PUT | `/admin/honor/alumni/{id}/status` | `honor:alumni:edit` |
| 6-10 | 调整展示排序 | PUT | `/admin/honor/alumni/{id}/sort` | `honor:alumni:edit` |
| 6-11 | 新增荣誉记录 | POST | `/admin/honor/record` | `honor:record:edit` |
| 6-12 | 修改荣誉记录 | PUT | `/admin/honor/record` | `honor:record:edit` |
| 6-13 | 删除荣誉记录 | DELETE | `/admin/honor/record/{id}` | `honor:record:edit` |
| 6-14 | 成员提交学长信息 | POST | `/user/honor/alumni` | 登录即可 |
| 6-15 | 我的提交记录分页 | GET | `/user/honor/alumni/my/page` | 登录即可 |
| 6-16 | 撤回提交 | PUT | `/user/honor/alumni/{id}/withdraw` | 登录即可 |
| 6-17 | 待审核信息分页（后台） | GET | `/admin/honor/alumni/audit/page` | `honor:alumni:audit` |
| 6-18 | 审核通过 / 驳回 | PUT | `/admin/honor/alumni/{id}/audit` | `honor:alumni:audit` |
| 6-19 | 重新提交我的信息 | PUT | `/user/honor/alumni/{id}` | 登录即可 |

> **口径说明**：6-01 ~ 6-04 为前台公示接口，仅返回「审核通过且已上板（`isShow = 1`）」的数据；成员提交（接口 `6-14`）走审核流程（接口 `6-18`），审核通过后才进入公示集合；被驳回或需要补充资料时，成员用接口 `6-19` 在原记录上修改并重新送审。

### 4.8 模块七：值日排班管理（`/user/duty`、`/admin/duty`）

明细见第 13 章。

| 接口编号 | 接口名称 | 方法 | 路径 | 权限码 |
| --- | --- | --- | --- | --- |
| 7-01 | 值日安排分页（公示） | GET | `/user/duty/schedule/page` | 登录即可 |
| 7-02 | 我的值日（含待办） | GET | `/user/duty/schedule/my` | 登录即可 |
| 7-03 | 值日日历（月视图） | GET | `/user/duty/schedule/calendar` | 登录即可 |
| 7-04 | 值日完成确认 | PUT | `/user/duty/schedule/{id}/confirm` | 登录即可 |
| 7-05 | 发起换班申请 | POST | `/user/duty/swap` | 登录即可 |
| 7-06 | 我的换班申请分页 | GET | `/user/duty/swap/my/page` | 登录即可 |
| 7-07 | 换班处理（同意 / 拒绝） | PUT | `/user/duty/swap/{id}/handle` | 登录即可 |
| 7-08 | 排班计划分页（后台） | GET | `/admin/duty/plan/page` | `duty:plan:list` |
| 7-09 | 新增排班计划 | POST | `/admin/duty/plan` | `duty:plan:add` |
| 7-10 | 修改排班计划 | PUT | `/admin/duty/plan` | `duty:plan:edit` |
| 7-11 | 删除排班计划 | DELETE | `/admin/duty/plan/{id}` | `duty:plan:remove` |
| 7-12 | 自动生成排班 | POST | `/admin/duty/plan/{id}/generate` | `duty:plan:generate` |
| 7-13 | 发布 / 取消发布 | PUT | `/admin/duty/plan/{id}/publish` | `duty:plan:publish` |
| 7-14 | 新增值日条目 | POST | `/admin/duty/schedule` | `duty:schedule:edit` |
| 7-15 | 修改值日条目 | PUT | `/admin/duty/schedule` | `duty:schedule:edit` |
| 7-16 | 删除值日条目 | DELETE | `/admin/duty/schedule/{id}` | `duty:schedule:edit` |
| 7-17 | 值日安排分页（后台） | GET | `/admin/duty/schedule/page` | `duty:schedule:list` |
| 7-18 | 值日状态变更 | PUT | `/admin/duty/schedule/{id}/status` | `duty:schedule:edit` |
| 7-19 | 值日统计看板 | GET | `/admin/duty/stat/overview` | `duty:stat:list` |
| 7-20 | 提醒配置查询 | GET | `/admin/duty/reminder` | `duty:reminder:config` |
| 7-21 | 提醒配置保存 | PUT | `/admin/duty/reminder` | `duty:reminder:config` |

> **口径说明**：v1 以**分组轮转**为主（`planType = 2`），发布后成员端公示；提醒渠道先做站内消息，邮件 / 微信推送列入 v1.1。

### 4.9 模块八：通用支撑（`/user/file`、`/common`、`/admin`）

明细见第 14 章。

> **编号调整说明**：本模块接口编号在 V1.0 已重排为连续的 `8-01` ~ `8-19`，按「文件 → 字典与基础能力 → 公告 → 看板 → 日志 → 视频」的业务分组排列。原 `8-12`（图形验证码）现为 `8-13`，原 `8-14` ~ `8-18`（视频相关）现为 `8-15` ~ `8-19`，原 `8-19`（后台公告分页）现为 `8-06`。V1.4 新增站内消息三个接口 `8-20` ~ `8-22`（见 14.8 节），编号续排、不影响既有编号。

| 接口编号 | 接口名称 | 方法 | 路径 | 权限码 |
| --- | --- | --- | --- | --- |
| 8-01 | 单文件上传 | POST | `/user/file/upload` | 登录即可 |
| 8-02 | 多文件上传 | POST | `/user/file/upload/batch` | 登录即可 |
| 8-03 | 文件删除 | DELETE | `/user/file/{id}` | 登录即可 |
| 8-04 | 数据字典查询 | GET | `/common/dict/{type}` | 无（公开接口） |
| 8-05 | 公告分页（前台） | GET | `/user/notice/page` | 登录即可 |
| 8-06 | 公告分页（后台） | GET | `/admin/notice/page` | `common:notice:list` |
| 8-07 | 公告新增 | POST | `/admin/notice` | `common:notice:edit` |
| 8-08 | 公告修改 | PUT | `/admin/notice` | `common:notice:edit` |
| 8-09 | 公告删除 | DELETE | `/admin/notice/{id}` | `common:notice:edit` |
| 8-10 | 前台首页看板 | GET | `/user/home/overview` | 登录即可 |
| 8-11 | 后台统计看板 | GET | `/admin/stat/dashboard` | `common:stat:list` |
| 8-12 | 操作日志分页 | GET | `/admin/log/page` | `system:log:list` |
| 8-13 | 图形验证码 | GET | `/common/captcha` | 无（公开接口） |
| 8-14 | 健康检查 | GET | `/common/health` | 无（公开接口） |
| 8-15 | 前台展示视频列表 | GET | `/user/video/list` | 登录即可 |
| 8-16 | 视频分页（后台） | GET | `/admin/video/page` | `common:video:list` |
| 8-17 | 新增视频 | POST | `/admin/video` | `common:video:edit` |
| 8-18 | 视频修改 | PUT | `/admin/video` | `common:video:edit` |
| 8-19 | 视频删除 | DELETE | `/admin/video/{id}` | `common:video:edit` |
| 8-20 | 我的消息分页 | GET | `/user/message/page` | 登录即可 |
| 8-21 | 未读消息数 | GET | `/user/message/unread/count` | 登录即可 |
| 8-22 | 标记消息已读 | PUT | `/user/message/read` | 登录即可 |

> **口径说明**：视频文件由 Jellyfin 管理，平台只保存 Jellyfin 生成的播放 URL（`playUrl`）与封面地址，前端直接读取 URL 播放，不经过平台后端转发流媒体。接入流程见 5.9 节。

### 4.10 模块依赖与优先级建议

**依赖关系**

- 模块一为全部模块提供账号与权限基础；
- 模块四通过 `budgetExpenseId` 依赖模块三；
- 模块五、模块七依赖模块一的成员数据；
- 模块八为所有模块提供文件与日志能力，其中文件接口（8-01）被预算凭证、资产图片、学长学姐头像、平面图底图、荣誉证书等复用，建议最先实现。

**工期建议（4 周）**

| 周次 | 建议内容 |
| --- | --- |
| 第 1 周 | 模块一 + 模块八基础（文件、字典、验证码） |
| 第 2 周 | 模块三、模块四 |
| 第 3 周 | 模块七、模块五 |
| 第 4 周 | 模块六、模块二、联调与文档收尾 |

**风险提示**：模块二（代码仓库关联）受外部平台接口影响最大，建议尽早申请 Gitee / GitHub 令牌做技术验证。

---

## 第 5 章 数据模型总览

> 所有业务表统一包含审计字段：`id`（bigint 自增主键）、`create_by`、`create_time`、`update_by`、`update_time`、`deleted`。本章各表仅列出业务字段。

### 5.1 模块一：用户与权限

| 表名 | 中文名 | 说明 |
| --- | --- | --- |
| `sys_user` | 用户表 | 一套账号，覆盖成员与管理员 |
| `sys_role` | 角色表 | 角色与数据范围 |
| `sys_user_role` | 用户角色关联表 | 支持一人多角色 |
| `sys_role_permission` | 角色权限关联表 | 角色与权限码映射 |
| `sys_invite_code` | 注册邀请码表 | 一次性邀请码，控制注册入口 |
| `sys_register_apply` | 注册申请表 | 无邀请码时的注册申请与审核记录 |

**字段说明**

- `sys_user`：`username`（唯一，登录名）、`password`（BCrypt 加密）、`real_name`、`student_no`、`grade`（年级）、`major`（专业）、`phone`、`email`、`avatar_url`、`status`（0 禁用 / 1 启用）、`last_login_time`、`remark`
- `sys_role`：`role_name`、`role_code`（`SUPER_ADMIN` / `CENTER_HEAD` / `MINISTER` / `MEMBER`）、`data_scope`（1 全部数据 / 2 本人数据）、`sort_no`、`status`、`remark`
- `sys_role_permission`：`role_id`、`perm_code`（对应 2.7 节权限码）
- `sys_user_role`：`user_id`、`role_id`
- `sys_invite_code`：`code`（唯一）、`max_use_count`（可用次数，默认 1）、`used_count`、`expire_time`、`status`（0 作废 / 1 有效）、`remark`、`create_by`
- `sys_register_apply`：`student_no`、`real_name`、`major`、`phone`、`reason`（申请理由）、`status`（0 待审核 / 1 已通过 / 2 已驳回）、`audit_user_id`、`audit_time`、`audit_remark`、`create_user_id`（通过后生成）、`ip`、`apply_time`

> **账号离场规则**：优先使用「停用」（`sys_user.status = 0`），停用即踢下线且保留历史数据；仅当账号从未产生任何业务数据时才允许删除。详见 6.3 节。

### 5.2 模块二：代码仓库关联

| 表名 | 中文名 | 说明 |
| --- | --- | --- |
| `repo_account` | 托管账户表 | 成员绑定的 Gitee / GitHub 账户 |
| `repo_project` | 仓库表 | 账户下的仓库及是否纳入统计 |
| `repo_commit` | 提交记录表 | 提交明细，图表与统计的数据源 |
| `repo_sync_log` | 同步日志表 | 定时自动采集 / 管理员手动补采的执行记录 |

**字段说明**

- `repo_account`：`user_id`、`platform`（gitee / github）、`account_name`、`access_token`（AES 加密存储）、`avatar_url`、`status`、`sync_status`（0 未采集 / 1 采集中 / 2 采集正常 / 3 采集失败）、`last_sync_time`、`bind_time`
- `repo_project`：`account_id`、`user_id`、`platform`、`repo_name`、`repo_url`、`owner_name`、`language`、`default_branch`、`star_count`、`fork_count`、`last_push_time`、`tracked`（0 不统计 / 1 统计）、`sync_status`（0 未采集 / 1 采集中 / 2 采集正常 / 3 采集失败）
- `repo_commit`：`project_id`、`user_id`、`commit_sha`、`branch`、`author_name`、`author_email`、`message`、`commit_time`、`add_lines`、`del_lines`、`is_merge`
- `repo_sync_log`：`account_id`、`user_id`、`sync_type`（1 定时自动 / 2 管理员手动补采）、`sync_result`（0 失败 / 1 成功）、`commit_count`、`cost_ms`、`error_msg`

**补充说明**

1. **采集是自动的**：定时任务每日凌晨遍历 `repo_account` 中 `status = 1` 的账户做增量采集（以 `commit_sha` 去重），失败自动重试并把结果写入 `repo_sync_log`；成员绑定账户（`2-02`）或更新令牌（`2-04`）时额外触发一次即时采集。成员端不提供手动同步入口，`2-06` 只读状态。定时任务设计见《IECMP-项目介绍与技术架构.md》4.6 节。
2. v1 的热力图与趋势图直接对 `repo_commit` 聚合查询；数据量增大后再引入 `repo_commit_daily` 汇总表（接口结构不变）。
2. 活跃度热力图按「日期 → 当日提交数量」聚合，前端按提交数量分级着色（提交越多颜色越深）；代码行数（`add_lines` / `del_lines`）与活跃天数一并采集入库，作为贡献度算法的候选指标，权重待开会确定。
3. 统计范围为**所有分支**的提交；采集时需遍历仓库分支分别拉取提交（`GET /repos/{owner}/{repo}/commits?sha={branch}`），首次全量采集耗时较长，需按仓库维度限流并写入 `repo_sync_log`。

### 5.3 模块三：部门预算

| 表名 | 中文名 | 说明 |
| --- | --- | --- |
| `budget_period` | 预算周期表 | 季度 / 年度预算主体 |
| `budget_item` | 预算科目表 | 周期下的科目与额度 |
| `budget_apply` | 预算申请表 | 成员提交的经费申请 |
| `budget_expense` | 支出记录表 | 实际支出，与财产联动 |

**字段说明**

- `budget_period`：`title`、`year`、`total_amount`、`used_amount`、`status`（0 草稿 / 1 执行中，成员可随时提交申请 / 2 已结束）、`start_date`、`end_date`、`is_public`、`public_time`、`description`
- `budget_item`：`period_id`、`name`、`category`（1 设备采购 / 2 耗材 / 3 活动 / 4 差旅 / 5 其他）、`budget_amount`、`used_amount`、`sort_no`、`remark`
- `budget_apply`：`period_id`、`item_id`、`apply_user_id`、`title`、`amount`、`reason`、`expect_date`、`attach_file_id`、`status`（0 待审核 / 1 已通过 / 2 已驳回 / 3 已撤销）、`audit_user_id`、`audit_time`、`audit_remark`
- `budget_expense`：`period_id`、`item_id`、`apply_id`、`title`、`amount`、`expense_date`、`voucher_file_id`、`handler_id`、`remark`

### 5.4 模块四：部门财产

| 表名 | 中文名 | 说明 |
| --- | --- | --- |
| `asset_category` | 资产分类表 | 支持二级分类 |
| `asset_info` | 资产台账表 | 一物一码，单件单记录 |
| `asset_borrow` | 借用 / 领用表 | 申请、审核、归还 |
| `asset_record` | 资产变动流水表 | 领用 / 借用 / 归还 / 维修 / 报废 / 调拨 / 盘点 |
| `asset_inventory` | 盘点任务表 | 一次盘点任务 |
| `asset_inventory_item` | 盘点明细表 | 逐项盘点结果与差异 |

**字段说明**

- `asset_category`：`name`、`parent_id`、`code`、`sort_no`
- `asset_info`：`asset_no`（唯一）、`name`、`category_id`、`brand`、`model`、`price`、`purchase_date`、`supplier`、`source_type`（1 采购 / 2 捐赠 / 3 调拨）、`budget_expense_id`（关联预算支出）、`status`（1 在用 / 2 闲置 / 3 维修中 / 4 已借出 / 5 已报废）、`keeper_id`（保管人）、`location`（存放位置文字描述，可与工位联动）、`image_urls`（JSON 数组）、`warranty_end_date`、`remark`
- `asset_borrow`：`asset_id`、`borrow_type`（1 借用 / 2 领用）、`user_id`、`purpose`、`borrow_time`、`expect_return_time`、`actual_return_time`、`status`（0 待审核 / 1 已通过 / 2 已驳回 / 3 已归还 / 4 已逾期）、`audit_user_id`、`audit_time`、`audit_remark`
- `asset_record`：`asset_id`、`record_type`、`from_user_id`、`to_user_id`、`from_location`、`to_location`、`record_time`、`operator_id`、`remark`
- `asset_inventory`：`title`、`begin_time`、`end_time`、`status`（0 进行中 / 1 已完成）、`total_count`、`normal_count`、`abnormal_count`
- `asset_inventory_item`：`inventory_id`、`asset_id`、`check_status`（0 未盘点 / 1 正常 / 2 盘亏 / 3 位置不符）、`check_user_id`、`check_time`、`remark`

### 5.5 模块五：部门工位

| 表名 | 中文名 | 说明 |
| --- | --- | --- |
| `seat_area` | 工位区域表 | 区域与平面图底图 |
| `seat_info` | 工位表 | 工位坐标、类型与状态 |
| `seat_apply` | 工位申请表 | 成员提交的工位申请 |
| `seat_swap` | 座位调换申请表 | 成员间调换工位 |

**字段说明**

- `seat_area`：`name`、`plan_image_url`（平面图底图）、`width`、`height`、`description`、`sort_no`
- `seat_info`：`area_id`、`seat_no`、`seat_type`（1 工位 / 2 会议室 / 3 储物柜 / 4 其他）、`x_ratio`、`y_ratio`、`width_ratio`、`height_ratio`、`rotation`（坐标均为相对底图的百分比，前端按比例渲染）、`status`（1 空闲 / 2 已占用 / 3 预留 / 4 维修中）、`user_id`（使用人）、`assign_time`、`remark`
- `seat_apply`：`user_id`、`area_id`、`seat_id`（可为空，表示仅意向区域）、`reason`、`status`（0 待审核 / 1 已通过 / 2 已驳回 / 3 已撤销）、`audit_user_id`、`audit_time`、`audit_remark`
- `seat_swap`：`apply_user_id`、`target_user_id`（对调对象）、`apply_seat_id`（申请方当前工位）、`target_seat_id`（对调方工位）、`reason`、`status`（0 待对方确认 / 1 对方已同意待负责人审核 / 2 已通过 / 3 已拒绝 / 4 已取消）、`handle_time`、`handle_remark`、`audit_user_id`、`audit_time`、`audit_remark`

> **流程说明**：负责人可直接分配工位（接口 5-15）；成员间调换需「对方确认 + 负责人审核」两步，任一步拒绝即终止并保留申请记录。

### 5.6 模块六：学长学姐展板

| 表名 | 中文名 | 说明 |
| --- | --- | --- |
| `honor_alumni` | 学长学姐信息表 | 成员自填、管理员审核 |
| `honor_record` | 荣誉记录表 | 与学长一对多 |

**字段说明**

- `honor_alumni`：`name`、`gender`、`graduation_year`、`major`、`class_name`、`company`、`position`、`avatar_url`、`brief`（个人简介）、`tags`（逗号分隔标签）、`submit_user_id`（填写人，成员自填）、`status`（0 待审核 / 1 已通过 / 2 已驳回）、`audit_user_id`、`audit_time`、`audit_remark`、`is_show`（0 下板 / 1 上板）、`sort_no`
- `honor_record`：`alumni_id`、`title`（荣誉名称）、`level`（1 国家级 / 2 省级 / 3 校级 / 4 企业及其他）、`award_date`、`description`、`certificate_url`、`sort_no`

> **流程说明**：成员填写（接口 6-14）→ 管理员审核（接口 6-18）→ 上板公示；审核前可撤回（接口 6-16），审核通过后再次修改需重新提交审核。

### 5.7 模块七：值日排班

| 表名 | 中文名 | 说明 |
| --- | --- | --- |
| `duty_plan` | 排班计划表 | 轮转规则与周期 |
| `duty_schedule` | 值日安排表 | 具体到人、到日的值日条目 |
| `duty_swap` | 换班申请表 | 成员间换班记录 |
| `duty_reminder_config` | 提醒配置表 | 提醒时机与渠道 |

**字段说明**

- `duty_plan`：`title`、`plan_type`（1 按周轮转 / 2 分组轮转 / 3 手工指定）、`start_date`、`end_date`、`member_ids`（JSON 数组，参与轮转成员）、`group_config`（JSON，分组配置）、`task_content`（默认值日内容）、`status`（0 草稿 / 1 已发布 / 2 已结束）、`publish_time`、`remark`
- `duty_schedule`：`plan_id`、`duty_date`、`user_id`、`group_name`、`task_content`、`status`（0 未完成 / 1 已完成 / 2 请假 / 3 已换班）、`confirm_time`、`remark`
- `duty_swap`：`schedule_id`、`apply_user_id`、`target_user_id`、`target_schedule_id`（对调的目标值日）、`reason`、`status`（0 待确认 / 1 已同意 / 2 已拒绝 / 3 已取消）、`handle_time`、`handle_remark`
- `duty_reminder_config`：`plan_id`（可为空，表示全局配置）、`ahead_minutes`（提前提醒分钟数）、`channel`（1 站内消息）、`enabled`

> **流程说明**：v1 默认使用分组轮转（`plan_type = 2`），按周轮转与手工指定作为备选；提醒渠道 v1 仅站内消息。

### 5.8 模块八：通用支撑

| 表名 | 中文名 | 说明 |
| --- | --- | --- |
| `sys_file` | 文件表 | 文件元信息 |
| `sys_notice` | 公告表 | 公告与发布状态 |
| `sys_oper_log` | 操作日志表 | 后台写操作审计 |
| `sys_video` | 视频资源表 | Jellyfin 播放链接登记 |
| `sys_dict` | 数据字典表 | 接口 `8-04` 的数据源 |
| `sys_message` | 站内消息表 | 值日提醒等站内消息的接收箱 |

**字段说明**

- `sys_file`：`name`（原始文件名）、`url`（OSS 访问地址）、`object_key`（OSS 对象键）、`bucket`（存储桶名称）、`size`、`content_type`、`biz_type`、`upload_user_id`
- `sys_notice`：`title`、`content`、`notice_type`、`is_top`、`publish_time`、`status`
- `sys_oper_log`：`module`、`operation`、`request_uri`、`request_param`、`response_msg`、`operator_id`、`operator_name`、`ip`、`cost_ms`、`status`
- `sys_video`：`title`、`cover_url`、`jellyfin_item_id`（Jellyfin 媒体项标识）、`play_url`（Jellyfin 生成的播放地址）、`duration`（时长秒）、`biz_type`（1 首页展示 / 2 学长学姐展板 / 3 其他）、`sort_no`、`status`（0 下架 / 1 上架）、`remark`
- `sys_dict`：`dict_type`（字典类型，如 `assetStatus`）、`dict_label`（展示文案）、`dict_value`（枚举值）、`sort_no`、`status`、`remark`；`(dict_type, dict_value)` 唯一
- `sys_message`：`receiver_id`（接收人）、`title`、`content`、`msg_type`（1 值日提醒 / 2 审核结果 / 3 系统通知）、`biz_type`（关联业务类型）、`biz_id`（关联业务主键）、`is_read`、`read_time`

**补充说明**

1. **`sys_dict` 与 `sys_message` 为设计补齐**：接口 `8-04` 要求「按类型返回枚举列表，结果在 Redis 缓存 1 小时，枚举变更时后端主动清理缓存」，说明必然存在存放枚举的表；模块七要求「提醒渠道 v1 先做站内消息」，说明必然存在存放消息的表。两张表的完整 DDL 见《IECMP-数据库设计.md》3.8 节。
2. **视频不做流媒体转发**：平台仅保存 Jellyfin 生成的 `play_url`，前端读取后直接播放；视频归属由 `biz_type` 决定。
3. **消息去重**：定时任务生成值日提醒前，先按「`receiver_id` + `biz_type` + `biz_id`」判断是否已存在，避免重复提醒。

### 5.9 视频接入方式（Jellyfin）

Jellyfin 是自建的媒体服务器：把视频文件放进它的媒体库，Jellyfin 会为每个视频生成一个可访问的播放链接（直链或 HLS 播放列表）。平台不负责视频存储与转码，只做「链接登记 + 页面展示」。

| 步骤 | 操作方 | 说明 |
| --- | --- | --- |
| 1 上传视频 | 管理员 | 将视频文件放入 Jellyfin 媒体库（按目录规范拷入或通过其管理页面上传） |
| 2 等待入库 | Jellyfin | Jellyfin 刮削元数据并生成媒体项，得到播放链接与封面 |
| 3 取得链接 | 管理员 | 在 Jellyfin 中取得播放地址（或媒体项 ID，由平台按模板拼接） |
| 4 录入平台 | 管理员 | 调用接口 8-17 新增视频：标题、封面、`playUrl`、时长、`bizType` |
| 5 前端展示 | 平台前端 | 调用接口 8-15 拉取列表，使用 `playUrl` 播放（HLS 需前端引入播放器） |

**注意事项**

1. 播放链接中的域名必须与 FRP 映射一致，否则内网地址在公网无法播放；建议后台配置统一的媒体地址前缀。
2. Jellyfin 生成的链接可能带访问令牌或有效期参数：若链接会失效，则保存「媒体项 ID」并在展示时由平台按配置拼接。本文档字段同时保留 `jellyfinItemId` 与 `playUrl`，两种做法都支持。
3. v1 采用管理员手工录入链接；后续若要自动同步（v1.1），只需新增同步任务，接口结构不变。

---

## 第 6 章 成员接入方案

平台仅限部门内部人员使用，为避免完全开放注册带来的恶意注册与审核负担，采用「邀请码注册为主 + 注册申请审核为辅」的双通道。

### 6.1 双通道注册

| 通道 | 流程 | 适用场景 |
| --- | --- | --- |
| 邀请码注册（主） | 管理员批量生成一次性邀请码 → 群内分发 → 成员凭「邀请码 + 学号 + 姓名」自助注册，注册即启用 | 纳新、批量入组 |
| 注册申请审核（辅） | 成员提交注册申请（学号、姓名、专业、理由）→ 管理员后台一键通过 / 驳回 → 通过后系统自动创建账号并展示初始密码 | 漏发邀请码、临时补录 |

**相关接口**

| 环节 | 接口 |
| --- | --- |
| 查询当前开放的注册通道 | `1-22` 注册方式配置查询 |
| 邀请码注册 | `1-23` 邀请码注册 |
| 提交注册申请 | `1-24` 提交注册申请 |
| 注册申请审核 | `1-25` 注册申请分页、`1-26` 注册申请审核 |
| 邀请码管理 | `1-27` 邀请码分页、`1-28` 生成邀请码、`1-29` 作废邀请码 |
| 图形验证码 | `8-13` 图形验证码 |

### 6.2 防滥用措施

1. 邀请码一次性使用、可设有效期与使用次数，使用后立即失效，泄露风险可控。
2. 注册申请需填写图形验证码（接口 8-13）；同一 IP 每小时最多提交 3 次；同一学号全局唯一，重复提交覆盖旧申请而不新增记录。
3. 申请信息与部门名单匹配时高亮提示，管理员优先通过。
4. 所有注册行为记录日志（IP、时间、来源），便于事后追溯。

### 6.3 账号离场规则

1. 成员离开部门使用「停用」（接口 1-12）而非删除：停用后令牌立即失效、无法再登录，但历史数据（预算申请、借用记录、值日记录）保持完整可追溯。
2. 仅当账号从未产生任何业务数据时，才允许删除账号（接口 `1-14`）。注意该删除是**逻辑删除**（把 `deleted` 置为本行 `id`），账号名与学号随即被释放、可重新注册，但历史数据行仍留在库中。
3. 停用账号在用户列表中以独立状态展示并支持筛选，可随时恢复。

---

## 第 7 章 模块一：用户与权限管理（system）

### 7.1 模块概述

- **职责**：账号管理、角色与权限、登录认证、个人信息维护，为其余 7 个模块提供身份与权限基础。
- **使用方**：管理端（负责人 / 管理员）与成员端共用一套账号表，通过角色决定可访问的接口前缀。
- **密码约定**：密码通过 HTTPS 明文传输，服务端 BCrypt 加盐存储，不自行实现前端加密算法（见 1.5 节）。
- **数据模型**：见 5.1 节。
- **接口清单**：见 4.2 节（`1-01` ~ `1-29`，共 29 个接口）。

### 7.2 认证与个人信息（1-01 ~ 1-07）

#### 7.2.1 后台登录（1-01）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-01 |
| 接口名称 | 后台登录 |
| Path | `/admin/login` |
| Method | `POST` |
| 接口描述 | 管理端账号登录，校验通过后返回双令牌与用户信息 |
| 权限码 | 无（公开接口） |
| 是否需令牌 | 否 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| username | string | 必须 | - | 登录账号，4~32 位 | - |
| password | string | 必须 | - | 登录密码，6~32 位 | - |
| captchaKey | string | 非必须 | - | 验证码标识，开启验证码时必填 | - |
| captchaCode | string | 非必须 | - | 验证码内容，开启验证码时必填 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| accessToken | string | 必须 | - | 访问令牌，后续放入 Authorization 头 | - |
| refreshToken | string | 必须 | - | 刷新令牌，仅用于换取新 accessToken | - |
| tokenType | string | 必须 | Bearer | 令牌类型 | - |
| expiresIn | integer | 必须 | - | accessToken 有效期（秒），默认 7200 | format: int32 |
| userInfo | object | 必须 | - | 用户信息，结构见下表 | - |

`userInfo` 结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| userId | integer | 必须 | - | 用户 ID | format: int64 |
| username | string | 必须 | - | 登录账号 | - |
| realName | string | 必须 | - | 姓名 | - |
| avatarUrl | string | 非必须 | - | 头像地址 | - |
| roles | array | 必须 | - | 角色编码列表，如 `["CENTER_HEAD"]` | - |
| permissions | array | 必须 | - | 权限码列表，如 `["system:user:list"]` | - |

**错误码**：10001 账号不存在；10002 账号被禁用；10003 用户名或密码错误；10004 验证码错误或已过期；10005 账号已锁定，请稍后再试。

**响应示例**

```json
{
  "code": 200,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.xxx",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.yyy",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "userInfo": {
      "userId": 1,
      "username": "admin",
      "realName": "张三",
      "avatarUrl": "https://iecmp-files.oss-cn-hangzhou.aliyuncs.com/upload/avatar/2026/09/9f2c1a7b.png",
      "roles": ["CENTER_HEAD"],
      "permissions": ["system:user:list", "budget:period:add"]
    }
  },
  "msg": "success"
}
```

#### 7.2.2 前台登录（1-02）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-02 |
| 接口名称 | 前台登录 |
| Path | `/user/login` |
| Method | `POST` |
| 接口描述 | 成员端登录，仅允许成员角色登录；校验通过后返回双令牌与用户信息 |
| 权限码 | 无（公开接口） |
| 是否需令牌 | 否 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| username | string | 必须 | - | 登录账号，4~32 位 | - |
| password | string | 必须 | - | 登录密码，6~32 位 | - |
| captchaKey | string | 非必须 | - | 验证码标识，开启验证码时必填 | - |
| captchaCode | string | 非必须 | - | 验证码内容，开启验证码时必填 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| accessToken | string | 必须 | - | 访问令牌，后续放入 Authorization 头 | - |
| refreshToken | string | 必须 | - | 刷新令牌，仅用于换取新 accessToken | - |
| tokenType | string | 必须 | Bearer | 令牌类型 | - |
| expiresIn | integer | 必须 | - | accessToken 有效期（秒），默认 7200 | format: int32 |
| userInfo | object | 必须 | - | 用户信息，结构见下表 | - |

`userInfo` 结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| userId | integer | 必须 | - | 用户 ID | format: int64 |
| username | string | 必须 | - | 登录账号 | - |
| realName | string | 必须 | - | 姓名 | - |
| avatarUrl | string | 非必须 | - | 头像地址 | - |
| roles | array | 必须 | - | 角色编码列表，成员账号为 `["MEMBER"]` | - |
| permissions | array | 必须 | - | 权限码列表，用于前端渲染菜单与按钮 | - |

**错误码**：10001 账号不存在；10002 账号被禁用；10003 用户名或密码错误；10006 该账号无前台登录权限。

#### 7.2.3 退出登录（1-03）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-03 |
| 接口名称 | 退出登录 |
| Path | `/user/logout` |
| Method | `POST` |
| 接口描述 | 注销当前令牌，令牌写入 Redis 黑名单直至自然过期 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**：无（Headers 携带 Authorization）。

**返回数据**：`data` 为 `null`。

**响应示例**

```json
{ "code": 200, "data": null, "msg": "success" }
```

#### 7.2.4 刷新令牌（1-04）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-04 |
| 接口名称 | 刷新令牌 |
| Path | `/user/token/refresh` |
| Method | `POST` |
| 接口描述 | 使用 refreshToken 换取新的 accessToken；refreshToken 剩余有效期不足 1 天时一并下发新 refreshToken |
| 权限码 | 无（凭 refreshToken） |
| 是否需令牌 | 否 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| refreshToken | string | 必须 | - | 登录时下发的刷新令牌 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| accessToken | string | 必须 | - | 新的访问令牌 | - |
| refreshToken | string | 非必须 | - | 新的刷新令牌，仅在原令牌临近过期时返回 | - |
| expiresIn | integer | 必须 | - | 新的有效期（秒） | format: int32 |

**错误码**：10007 刷新令牌无效或已过期。

#### 7.2.5 当前用户信息（1-05）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-05 |
| 接口名称 | 当前用户信息 |
| Path | `/user/info` |
| Method | `GET` |
| 接口描述 | 获取当前登录用户的基本信息、角色与权限码，用于前端渲染菜单与按钮 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**：无。

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| userId | integer | 必须 | - | 用户 ID | format: int64 |
| username | string | 必须 | - | 登录账号 | - |
| realName | string | 必须 | - | 姓名 | - |
| studentNo | string | 非必须 | - | 学号 | - |
| grade | string | 非必须 | - | 年级，如 2023 | - |
| major | string | 非必须 | - | 专业 | - |
| phone | string | 非必须 | - | 手机号 | - |
| email | string | 非必须 | - | 邮箱 | - |
| avatarUrl | string | 非必须 | - | 头像地址 | - |
| roles | array | 必须 | - | 角色编码列表 | - |
| permissions | array | 必须 | - | 权限码列表 | - |
| lastLoginTime | string | 非必须 | - | 最近登录时间 | - |

#### 7.2.6 修改个人资料（1-06）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-06 |
| 接口名称 | 修改个人资料 |
| Path | `/user/info` |
| Method | `PUT` |
| 接口描述 | 修改当前登录用户的个人资料，账号与角色不可自行修改 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| realName | string | 非必须 | - | 姓名，最长 32 字 | - |
| phone | string | 非必须 | - | 手机号，11 位 | - |
| email | string | 非必须 | - | 邮箱 | - |
| avatarUrl | string | 非必须 | - | 头像地址，来源于文件上传接口 8-01 | - |

**返回数据**：`data` 为 `null`。

**错误码**：10008 手机号已被占用；10009 邮箱已被占用。

#### 7.2.7 修改密码（1-07）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-07 |
| 接口名称 | 修改密码 |
| Path | `/user/password` |
| Method | `PUT` |
| 接口描述 | 修改当前登录用户密码，校验原密码；修改后当前令牌失效，需重新登录 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| oldPassword | string | 必须 | - | 原密码 | - |
| newPassword | string | 必须 | - | 新密码，6~32 位，需包含字母与数字 | - |
| confirmPassword | string | 必须 | - | 确认新密码，须与 newPassword 一致 | - |

**返回数据**：`data` 为 `null`。

**错误码**：10010 原密码错误；10011 两次输入的新密码不一致。

### 7.3 用户管理（1-08 ~ 1-14）

#### 7.3.1 用户分页查询（1-08）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-08 |
| 接口名称 | 用户分页查询 |
| Path | `/admin/user/page` |
| Method | `GET` |
| 接口描述 | 分页查询用户列表，支持按姓名 / 账号 / 学号模糊搜索与状态、角色筛选 |
| 权限码 | `system:user:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| keyword | string | 非必须 | - | 模糊匹配 realName / username / studentNo | - |
| status | integer | 非必须 | - | 0 禁用，1 启用 | format: int32 |
| roleCode | string | 非必须 | - | 角色编码筛选 | - |
| grade | string | 非必须 | - | 年级筛选 | - |
| beginTime | string | 非必须 | - | 创建时间起，格式 yyyy-MM-dd HH:mm:ss | - |
| endTime | string | 非必须 | - | 创建时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| userId | integer | 必须 | - | 用户 ID | format: int64 |
| username | string | 必须 | - | 登录账号 | - |
| realName | string | 必须 | - | 姓名 | - |
| studentNo | string | 非必须 | - | 学号 | - |
| grade | string | 非必须 | - | 年级 | - |
| major | string | 非必须 | - | 专业 | - |
| phone | string | 非必须 | - | 手机号 | - |
| email | string | 非必须 | - | 邮箱 | - |
| avatarUrl | string | 非必须 | - | 头像地址 | - |
| status | integer | 必须 | - | 0 禁用，1 启用 | format: int32 |
| roleCodes | array | 必须 | - | 角色编码列表 | - |
| roleNames | array | 非必须 | - | 角色名称列表，便于前端直接展示 | - |
| lastLoginTime | string | 非必须 | - | 最近登录时间 | - |
| createTime | string | 必须 | - | 创建时间 | - |

#### 7.3.2 用户详情（1-09）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-09 |
| 接口名称 | 用户详情 |
| Path | `/admin/user/{id}` |
| Method | `GET` |
| 接口描述 | 查询指定用户详情，含角色 ID 列表，用于编辑回显 |
| 权限码 | `system:user:query` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 用户 ID | 12 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| userId | integer | 必须 | - | 用户 ID | format: int64 |
| username | string | 必须 | - | 登录账号 | - |
| realName | string | 必须 | - | 姓名 | - |
| studentNo | string | 非必须 | - | 学号 | - |
| grade | string | 非必须 | - | 年级 | - |
| major | string | 非必须 | - | 专业 | - |
| phone | string | 非必须 | - | 手机号 | - |
| email | string | 非必须 | - | 邮箱 | - |
| avatarUrl | string | 非必须 | - | 头像地址 | - |
| status | integer | 必须 | - | 0 禁用，1 启用 | format: int32 |
| roleCodes | array | 必须 | - | 角色编码列表 | - |
| roleNames | array | 非必须 | - | 角色名称列表，便于前端直接展示 | - |
| roleIds | array | 必须 | - | 角色 ID 列表，用于编辑表单的角色回显 | - |
| lastLoginTime | string | 非必须 | - | 最近登录时间 | - |
| createTime | string | 必须 | - | 创建时间 | - |
| remark | string | 非必须 | - | 备注 | - |

**错误码**：10012 用户不存在。

#### 7.3.3 新增用户（1-10）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-10 |
| 接口名称 | 新增用户 |
| Path | `/admin/user` |
| Method | `POST` |
| 接口描述 | 由负责人创建成员账号，初始密码由后台配置（默认 123456），首次登录建议修改 |
| 权限码 | `system:user:add` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| username | string | 必须 | - | 登录账号，4~32 位，全局唯一 | - |
| realName | string | 必须 | - | 姓名 | - |
| studentNo | string | 非必须 | - | 学号，唯一 | - |
| grade | string | 非必须 | - | 年级 | - |
| major | string | 非必须 | - | 专业 | - |
| phone | string | 非必须 | - | 手机号 | - |
| email | string | 非必须 | - | 邮箱 | - |
| roleIds | array | 必须 | - | 角色 ID 列表，至少一个 | - |
| status | integer | 非必须 | 1 | 0 禁用，1 启用 | format: int32 |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| userId | integer | 必须 | - | 新增用户 ID | format: int64 |

**错误码**：10013 登录账号已存在；10014 学号已存在；10015 角色不存在。

#### 7.3.4 修改用户（1-11）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-11 |
| 接口名称 | 修改用户 |
| Path | `/admin/user` |
| Method | `PUT` |
| 接口描述 | 修改用户基础信息与角色，不允许修改登录账号 |
| 权限码 | `system:user:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| userId | integer | 必须 | - | 用户 ID | format: int64 |
| realName | string | 必须 | - | 姓名 | - |
| studentNo | string | 非必须 | - | 学号，唯一 | - |
| grade | string | 非必须 | - | 年级 | - |
| major | string | 非必须 | - | 专业 | - |
| phone | string | 非必须 | - | 手机号 | - |
| email | string | 非必须 | - | 邮箱 | - |
| roleIds | array | 必须 | - | 角色 ID 列表，至少一个 | - |
| status | integer | 非必须 | 1 | 0 禁用，1 启用 | format: int32 |
| remark | string | 非必须 | - | 备注 | - |

> `username` 为登录账号，创建后不可修改，本接口不接收该字段。

**返回数据**：`data` 为 `null`。

**错误码**：10012 用户不存在；10014 学号已存在；10015 角色不存在。

#### 7.3.5 启用 / 禁用用户（1-12）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-12 |
| 接口名称 | 启用 / 禁用用户 |
| Path | `/admin/user/{id}/status` |
| Method | `PUT` |
| 接口描述 | 变更用户启用状态，禁用后该用户令牌立即失效且无法登录 |
| 权限码 | `system:user:edit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 用户 ID | 12 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 必须 | - | 0 禁用，1 启用 | format: int32 |

**返回数据**：`data` 为 `null`。

**错误码**：10012 用户不存在；10016 不允许禁用超级管理员。

#### 7.3.6 重置密码（1-13）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-13 |
| 接口名称 | 重置密码 |
| Path | `/admin/user/{id}/password/reset` |
| Method | `PUT` |
| 接口描述 | 管理员重置指定用户密码，重置后用户需重新登录 |
| 权限码 | `system:user:reset` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 用户 ID | 12 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| newPassword | string | 非必须 | 系统默认密码 | 不传则重置为系统默认密码（123456） | - |

**返回数据**：`data` 为 `null`。

**错误码**：10012 用户不存在。

#### 7.3.7 删除用户（1-14）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-14 |
| 接口名称 | 删除用户 |
| Path | `/admin/user/{id}` |
| Method | `DELETE` |
| 接口描述 | 逻辑删除用户；存在未归还资产或待审核申请时禁止删除 |
| 权限码 | `system:user:remove` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 用户 ID | 12 |

**返回数据**：`data` 为 `null`。

**错误码**：10012 用户不存在；10017 该用户存在未归还资产；10018 该用户存在待处理的申请；10019 不允许删除超级管理员。

> **删除后账号名会被释放**：服务端的删除动作是 `UPDATE sys_user SET deleted = id WHERE id = ? AND deleted = 0`——把 `deleted` 从 0 改为本行 `id`。`uk_username` / `uk_student_no` 只约束 `deleted = 0` 的行，因此账号名与学号随即可以被重新注册，不会出现「同名账号永远注册不上」的问题；同时该行仍通过原 `id` 保留全部历史业务数据。写法与理由见《数据库设计》1.5 节。

### 7.4 角色与权限（1-15 ~ 1-21）

#### 7.4.1 角色下拉列表（1-15）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-15 |
| 接口名称 | 角色下拉列表 |
| Path | `/admin/role/list` |
| Method | `GET` |
| 接口描述 | 查询全部启用角色，用于用户表单的角色选择；仅返回必要字段 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**：无。

**返回数据**

`data` 为数组，元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| roleId | integer | 必须 | - | 角色 ID | format: int64 |
| roleName | string | 必须 | - | 角色名称 | - |
| roleCode | string | 必须 | - | 角色编码 | - |

> **为什么权限码是「登录即可」**：本接口是各管理表单共用的下拉数据源，只返回角色 ID / 名称 / 编码三个非敏感字段，不暴露任何权限内容。若收紧为 `system:role:list`，会出现「能新增用户、却选不到角色」的权限死锁。角色完整信息（含权限数量、用户数量）由 `1-16` 提供，受 `system:role:list` 保护。

#### 7.4.2 角色分页查询（1-16）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-16 |
| 接口名称 | 角色分页查询 |
| Path | `/admin/role/page` |
| Method | `GET` |
| 接口描述 | 分页查询角色，含每个角色已分配的权限码数量 |
| 权限码 | `system:role:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 非必须 | - | 0 停用，1 启用 | format: int32 |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| roleId | integer | 必须 | - | 角色 ID | format: int64 |
| roleName | string | 必须 | - | 角色名称 | - |
| roleCode | string | 必须 | - | 角色编码 | - |
| dataScope | integer | 必须 | - | 1 全部数据，2 本人数据 | format: int32 |
| permissionCount | integer | 非必须 | - | 已分配权限码数量 | format: int32 |
| userCount | integer | 非必须 | - | 关联用户数量 | format: int32 |
| status | integer | 必须 | - | 0 停用，1 启用 | format: int32 |
| remark | string | 非必须 | - | 备注 | - |
| createTime | string | 必须 | - | 创建时间 | - |

#### 7.4.3 新增角色（1-17）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-17 |
| 接口名称 | 新增角色 |
| Path | `/admin/role` |
| Method | `POST` |
| 接口描述 | 新增角色，可同时指定权限码集合 |
| 权限码 | `system:role:add` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| roleName | string | 必须 | - | 角色名称，最长 32 字 | - |
| roleCode | string | 必须 | - | 角色编码，大写字母加下划线，全局唯一 | - |
| dataScope | integer | 非必须 | 2 | 1 全部数据，2 本人数据 | format: int32 |
| permCodes | array | 非必须 | [] | 权限码列表 | - |
| sortNo | integer | 非必须 | 0 | 排序号，升序 | format: int32 |
| status | integer | 非必须 | 1 | 0 停用，1 启用 | format: int32 |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| roleId | integer | 必须 | - | 新增角色 ID | format: int64 |

**错误码**：10020 角色名称已存在；10021 角色编码已存在；10022 权限码不存在。

#### 7.4.4 修改角色（1-18）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-18 |
| 接口名称 | 修改角色 |
| Path | `/admin/role` |
| Method | `PUT` |
| 接口描述 | 修改角色基础信息；不在此接口调整权限码（见 7.4.6） |
| 权限码 | `system:role:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| roleId | integer | 必须 | - | 角色 ID | format: int64 |
| roleName | string | 必须 | - | 角色名称，最长 32 字 | - |
| roleCode | string | 必须 | - | 角色编码，大写字母加下划线，全局唯一 | - |
| dataScope | integer | 非必须 | 2 | 1 全部数据，2 本人数据 | format: int32 |
| sortNo | integer | 非必须 | 0 | 排序号，升序 | format: int32 |
| status | integer | 非必须 | 1 | 0 停用，1 启用 | format: int32 |
| remark | string | 非必须 | - | 备注 | - |

> 本接口不接收 `permCodes` 字段；权限码的调整由接口 `1-20` 负责。

**返回数据**：`data` 为 `null`。

**错误码**：10015 角色不存在；10020 角色名称已存在；10024 内置角色不允许修改编码。

#### 7.4.5 删除角色（1-19）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-19 |
| 接口名称 | 删除角色 |
| Path | `/admin/role/{id}` |
| Method | `DELETE` |
| 接口描述 | 逻辑删除角色，已被用户引用时禁止删除 |
| 权限码 | `system:role:remove` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 角色 ID | 3 |

**返回数据**：`data` 为 `null`。

**错误码**：10015 角色不存在；10025 该角色下存在用户，无法删除；10026 内置角色不允许删除。

#### 7.4.6 角色授权（1-20）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-20 |
| 接口名称 | 角色授权 |
| Path | `/admin/role/{id}/permission` |
| Method | `PUT` |
| 接口描述 | 全量覆盖式分配角色权限码；权限变更后相关用户需重新登录或前端刷新权限 |
| 权限码 | `system:role:auth` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 角色 ID | 3 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| permCodes | array | 必须 | - | 权限码列表，传空数组表示清空权限 | - |

**返回数据**：`data` 为 `null`。

**错误码**：10015 角色不存在；10022 权限码不存在；10027 不允许修改超级管理员的权限。

#### 7.4.7 权限码树（1-21）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-21 |
| 接口名称 | 权限码树 |
| Path | `/admin/permission/tree` |
| Method | `GET` |
| 接口描述 | 返回后端登记的权限码树（按模块分组），供授权界面渲染勾选框；v1 为只读数据 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**：无。

**返回数据**

`data` 为树形数组，元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| code | string | 非必须 | - | 权限码，叶子节点才返回 | - |
| label | string | 必须 | - | 展示名称 | - |
| module | string | 必须 | - | 所属模块标识，如 `system`、`budget` | - |
| children | array | 非必须 | [] | 子节点 | - |

> **为什么权限码是「登录即可」**：本接口只返回权限码字典（模块分组 + 展示名称），供授权界面渲染勾选框，不含任何用户数据与业务数据；真正敏感的「授权动作」由 `1-20` 把关（需 `system:role:auth`）。若连字典一并收紧，会造成「进得了授权页、却拉不到勾选框数据」的死锁。

**响应示例**

```json
{
  "code": 200,
  "data": [
    {
      "label": "用户管理",
      "module": "system",
      "children": [
        { "code": "system:user:list", "label": "用户查询", "module": "system" },
        { "code": "system:user:add", "label": "用户新增", "module": "system" }
      ]
    }
  ],
  "msg": "success"
}
```

### 7.5 注册与邀请码（1-22 ~ 1-29）

> 本组接口是第 6 章「成员接入方案」的落地实现，业务规则见 6.1 ~ 6.3 节。

#### 7.5.1 注册方式配置查询（1-22）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-22 |
| 接口名称 | 注册方式配置查询 |
| Path | `/common/register/config` |
| Method | `GET` |
| 接口描述 | 返回当前开放的注册通道与提示文案，供注册页决定展示「邀请码注册」还是「申请入驻」表单 |
| 权限码 | 无（公开接口） |
| 是否需令牌 | 否 |

**请求参数**：无。

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| inviteEnabled | integer | 必须 | - | 1 开启邀请码注册，0 关闭 | format: int32 |
| applyEnabled | integer | 必须 | - | 1 开启注册申请，0 关闭 | format: int32 |
| captchaEnabled | integer | 必须 | - | 1 需要图形验证码 | format: int32 |
| tips | string | 非必须 | - | 注册页提示文案，由后台配置 | - |

> **开关放在后端配置文件，不在数据库**：`inviteEnabled` / `applyEnabled` / `captchaEnabled` 与 `tips` 均来自后端 `application.yml`，**没有对应的配置表，也没有管理端修改接口**——改开关需要改配置并重启（或走配置中心热更新）。原因见《数据库设计》5.4 节：这类配置由部署者维护、不随业务变化，放配置文件可避免「配置表被误改成前台可读」。若后期确实需要在线开关，再补 `sys_config` 表并新增一对读写接口，本接口的返回结构不变。

#### 7.5.2 邀请码注册（1-23）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-23 |
| 接口名称 | 邀请码注册 |
| Path | `/user/register` |
| Method | `POST` |
| 接口描述 | 成员凭邀请码自助注册，注册成功即启用并返回登录账号 |
| 权限码 | 无（公开接口） |
| 是否需令牌 | 否 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| inviteCode | string | 必须 | - | 管理员下发的邀请码 | - |
| username | string | 必须 | - | 登录账号，4~32 位，全局唯一 | - |
| password | string | 必须 | - | 登录密码，6~32 位 | - |
| realName | string | 必须 | - | 真实姓名 | - |
| studentNo | string | 必须 | - | 学号，全局唯一 | - |
| major | string | 非必须 | - | 专业 | - |
| phone | string | 非必须 | - | 手机号 | - |
| captchaKey | string | 非必须 | - | 验证码标识，开启验证码时必填 | - |
| captchaCode | string | 非必须 | - | 验证码内容 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| userId | integer | 必须 | - | 新账号 ID | format: int64 |
| username | string | 必须 | - | 登录账号 | - |

**错误码**：10004 验证码错误或已过期；10013 登录账号已存在；10014 学号已存在；10028 邀请码无效或已失效；10029 邀请码使用次数已达上限。

#### 7.5.3 提交注册申请（1-24）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-24 |
| 接口名称 | 提交注册申请 |
| Path | `/common/register/apply` |
| Method | `POST` |
| 接口描述 | 无邀请码时提交入驻申请，由管理员审核；同一学号重复提交将覆盖旧申请 |
| 权限码 | 无（公开接口） |
| 是否需令牌 | 否 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| realName | string | 必须 | - | 真实姓名 | - |
| studentNo | string | 必须 | - | 学号 | - |
| major | string | 非必须 | - | 专业 | - |
| phone | string | 非必须 | - | 手机号 | - |
| reason | string | 非必须 | - | 申请理由，最长 200 字 | - |
| captchaKey | string | 必须 | - | 验证码标识 | - |
| captchaCode | string | 必须 | - | 验证码内容 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| applyId | integer | 必须 | - | 申请记录 ID | format: int64 |

**错误码**：10030 提交过于频繁（同一 IP 每小时限 3 次）；10034 该学号已存在账号，无法重复申请。

#### 7.5.4 注册申请分页（1-25）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-25 |
| 接口名称 | 注册申请分页 |
| Path | `/admin/register/apply/page` |
| Method | `GET` |
| 接口描述 | 分页查询注册申请，支持按状态与时间筛选 |
| 权限码 | `system:register:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 非必须 | - | 0 待审核，1 已通过，2 已驳回 | format: int32 |
| beginTime | string | 非必须 | - | 申请时间起 | - |
| endTime | string | 非必须 | - | 申请时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| applyId | integer | 必须 | - | 申请 ID | format: int64 |
| realName | string | 必须 | - | 姓名 | - |
| studentNo | string | 必须 | - | 学号 | - |
| major | string | 非必须 | - | 专业 | - |
| phone | string | 非必须 | - | 手机号 | - |
| reason | string | 非必须 | - | 申请理由 | - |
| status | integer | 必须 | - | 0 待审核，1 已通过，2 已驳回 | format: int32 |
| ip | string | 非必须 | - | 提交 IP，便于识别恶意申请 | - |
| applyTime | string | 必须 | - | 提交时间 | - |
| auditRemark | string | 非必须 | - | 审核意见 | - |
| createUserId | integer | 非必须 | - | 通过后生成的用户 ID | format: int64 |

#### 7.5.5 注册申请审核（1-26）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-26 |
| 接口名称 | 注册申请审核 |
| Path | `/admin/register/apply/{id}/audit` |
| Method | `PUT` |
| 接口描述 | 审核注册申请；通过时自动创建账号、分配角色并返回初始密码 |
| 权限码 | `system:register:audit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 申请 ID | 5 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| auditStatus | integer | 必须 | - | 1 通过，2 驳回 | format: int32 |
| roleIds | array | 非必须 | 成员角色 | 通过时生效，分配的角色 ID 列表 | - |
| username | string | 非必须 | 学号 | 通过时可自定义登录账号 | - |
| auditRemark | string | 非必须 | - | 审核意见，驳回时建议填写 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| userId | integer | 非必须 | - | 通过时生成的用户 ID | format: int64 |
| username | string | 非必须 | - | 通过时生成的登录账号 | - |
| initialPassword | string | 非必须 | - | 初始密码，仅审核通过时返回一次 | - |

**错误码**：10013 登录账号已存在；10031 注册申请不存在；10032 该注册申请已处理。

> **初始密码只出现这一次**：`initialPassword` 是随机生成后**明文返回的唯一时机**——数据库里只存 BCrypt 哈希、不存明文，服务端也无法再算回来。前端必须把 `username` 与 `initialPassword` 一起展示给管理员（建议支持一键复制），弹窗关掉后就只能走「重置密码」（接口 `1-13`）重新收一份。

#### 7.5.6 邀请码分页（1-27）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-27 |
| 接口名称 | 邀请码分页 |
| Path | `/admin/invite-code/page` |
| Method | `GET` |
| 接口描述 | 分页查询邀请码及其使用情况 |
| 权限码 | `system:invite:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 非必须 | - | 0 作废，1 有效 | format: int32 |
| keyword | string | 非必须 | - | 模糊匹配邀请码 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| id | integer | 必须 | - | 邀请码 ID | format: int64 |
| code | string | 必须 | - | 邀请码，8 位大写字母数字 | - |
| maxUseCount | integer | 必须 | - | 可用次数 | format: int32 |
| usedCount | integer | 必须 | - | 已使用次数 | format: int32 |
| expireTime | string | 非必须 | - | 过期时间，为空表示长期有效 | - |
| status | integer | 必须 | - | 0 作废，1 有效 | format: int32 |
| createTime | string | 必须 | - | 生成时间 | - |

#### 7.5.7 生成邀请码（1-28）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-28 |
| 接口名称 | 生成邀请码 |
| Path | `/admin/invite-code` |
| Method | `POST` |
| 接口描述 | 批量生成邀请码，生成后由负责人在群内分发 |
| 权限码 | `system:invite:add` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| count | integer | 非必须 | 1 | 生成数量，范围 1~50 | format: int32 |
| maxUseCount | integer | 非必须 | 1 | 每个邀请码可用次数 | format: int32 |
| expireTime | string | 非必须 | - | 过期时间，不传表示长期有效 | - |
| remark | string | 非必须 | - | 备注，如「2026 秋季纳新」 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| codes | array | 必须 | - | 本次生成的邀请码列表 | - |

**响应示例**

```json
{
  "code": 200,
  "data": {
    "codes": ["A3F9K2M7", "B8D1N5P4", "C6E0Q3R9"]
  },
  "msg": "success"
}
```

#### 7.5.8 作废邀请码（1-29）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 1-29 |
| 接口名称 | 作废邀请码 |
| Path | `/admin/invite-code/{id}` |
| Method | `DELETE` |
| 接口描述 | 作废邀请码，作废后不可再用于注册 |
| 权限码 | `system:invite:remove` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 邀请码 ID | 8 |

**返回数据**：`data` 为 `null`。

**错误码**：10033 邀请码不存在。

---

## 第 8 章 模块二：代码仓库关联（repo）

### 8.1 模块概述

- **职责**：成员绑定 Gitee / GitHub 托管账号，采集仓库与提交记录，输出进度趋势与个人贡献度图表。
- **数据模型**：见 5.2 节。
- **接口清单**：见 4.3 节（`2-01` ~ `2-17`，共 17 个接口）。
- **已确定口径**：
  1. 关联方式采用**个人访问令牌（AccessToken）**，v1 不做 OAuth 授权，后续版本可兼容；
  2. 提交统计范围包含**所有分支**，不做分支过滤；本期只采集当前绑定账户**本人名下**的仓库，组织仓库与他人仓库暂不纳入（后续可通过平台 API 扩展）；
  3. 核心指标为**提交数量**，活跃度热力图按提交数分级着色（提交越多颜色越深）；
  4. 代码行数与活跃天数一并采集入库，贡献度算法权重待团队会议确定；
  5. **采集全部由后台自动完成**：成员只需绑定一次令牌，绑定后触发首次采集，此后每日定时增量采集；成员端**不提供手动同步**，避免反复触发导致第三方平台限流，也保证全中心统计口径一致；
  6. **合并提交（merge）默认不计入统计**（`is_merge = 1` 的记录仍入库，只是聚合时过滤），提交明细查询可用 `includeMerge = 1` 查看全部。

### 8.2 平台与账户绑定（2-01 ~ 2-06）

#### 8.2.1 支持的平台列表（2-01）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-01 |
| 接口名称 | 支持的平台列表 |
| Path | `/user/repo/platform/list` |
| Method | `GET` |
| 接口描述 | 返回平台支持的代码托管平台与令牌申请入口，用于绑定表单的平台下拉与帮助提示 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**：无。

**返回数据**

`data` 为数组，元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| platform | string | 必须 | - | 平台标识：gitee / github | - |
| platformName | string | 必须 | - | 平台展示名称 | - |
| tokenUrl | string | 非必须 | - | 令牌申请入口地址，前端引导用户前往 | - |
| tokenTip | string | 非必须 | - | 令牌权限要求提示文案 | - |

**响应示例**

```json
{
  "code": 200,
  "data": [
    {
      "platform": "github",
      "platformName": "GitHub",
      "tokenUrl": "https://github.com/settings/tokens",
      "tokenTip": "需勾选 repo 与 read:user 权限"
    },
    {
      "platform": "gitee",
      "platformName": "Gitee",
      "tokenUrl": "https://gitee.com/personal_access_tokens",
      "tokenTip": "需勾选 projects 与 pull_requests 权限"
    }
  ],
  "msg": "success"
}
```

#### 8.2.2 绑定代码托管账户（2-02）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-02 |
| 接口名称 | 绑定代码托管账户 |
| Path | `/user/repo/account` |
| Method | `POST` |
| 接口描述 | 成员提交个人访问令牌绑定托管账户；后端校验令牌有效性后加密落库，并拉取一次仓库列表 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| platform | string | 必须 | - | 平台标识：gitee / github | - |
| accountName | string | 必须 | - | 托管平台账号名 | - |
| accessToken | string | 必须 | - | 个人访问令牌，最长 256 个字符，需具备读取仓库与提交的权限 | 仅允许可打印 ASCII；后端 AES 加密后存储，接口不返回明文 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| accountId | integer | 必须 | - | 新增托管账户 ID | format: int64 |
| projectCount | integer | 必须 | - | 本次拉取到的仓库数量 | format: int32 |

**错误码**：20002 该账户已绑定，请勿重复绑定；20003 访问令牌无效或已过期；20007 第三方平台接口调用失败。

> **绑定即自动采集，成员无需手动触发**：绑定成功后服务端**异步**拉取一次仓库列表并写入 `repo_project`，随后由每日定时任务增量采集提交记录。绑定接口返回时数据可能仍在采集中，前端宜提示「数据正在自动采集，稍后刷新」。本次拉取失败不影响绑定结果，失败原因写入同步日志，可在 `2-06` 查看状态。

#### 8.2.3 我的绑定账户列表（2-03）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-03 |
| 接口名称 | 我的绑定账户列表 |
| Path | `/user/repo/account/list` |
| Method | `GET` |
| 接口描述 | 查询当前登录用户绑定的全部托管账户，用于绑定管理页展示 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**：无（仅返回当前用户自己的账户）。

**返回数据**

`data` 为数组，元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| accountId | integer | 必须 | - | 托管账户 ID | format: int64 |
| platform | string | 必须 | - | 平台标识：gitee / github | - |
| platformName | string | 必须 | - | 平台展示名称 | - |
| accountName | string | 必须 | - | 托管平台账号名 | - |
| avatarUrl | string | 非必须 | - | 平台头像地址（平台外链，不转存 OSS） | - |
| status | integer | 必须 | - | 0 失效，1 有效 | format: int32 |
| projectCount | integer | 必须 | 0 | 已采集的仓库数量 | format: int32 |
| lastSyncTime | string | 非必须 | - | 最近同步时间 | - |
| bindTime | string | 必须 | - | 绑定时间 | - |

> **令牌不回传**：`accessToken` 属敏感字段，任何接口都不返回明文或密文，前端只需展示「已绑定」状态。

#### 8.2.4 更新绑定账户令牌（2-04）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-04 |
| 接口名称 | 更新绑定账户令牌 |
| Path | `/user/repo/account` |
| Method | `PUT` |
| 接口描述 | 托管平台令牌过期或权限变更时，用新令牌覆盖旧令牌 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| accountId | integer | 必须 | - | 托管账户 ID | format: int64 |
| accessToken | string | 必须 | - | 新的个人访问令牌，最长 256 个字符 | 仅允许可打印 ASCII；后端 AES 加密后覆盖存储 |

**返回数据**：`data` 为 `null`。

**错误码**：20001 托管账户不存在；20003 访问令牌无效或已过期；20005 该账户不属于当前用户。

> 只允许更新本人绑定的账户，服务层校验 `repo_account.user_id` 是否为当前用户。
>
> **令牌更新后自动重新采集**：校验新令牌有效即写入，并把该账户的 `sync_status` 置为「1 采集中」，由后台任务立即重采一次——成员换令牌后无需再触发同步，可在 `2-06` 查看进度。

#### 8.2.5 解绑账户（2-05）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-05 |
| 接口名称 | 解绑账户 |
| Path | `/user/repo/account/{id}` |
| Method | `DELETE` |
| 接口描述 | 解绑托管账户；解绑后不再采集该账户下的仓库与提交 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 托管账户 ID | 3 |

**返回数据**：`data` 为 `null`。

**错误码**：20001 托管账户不存在；20005 该账户不属于当前用户。

> 解绑会连带逻辑删除该账户下的 `repo_project` 与 `repo_commit` 记录，历史统计数据将不再包含这部分提交。为避免误操作，前端需二次确认。

#### 8.2.6 我的同步状态（2-06）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-06 |
| 接口名称 | 我的同步状态 |
| Path | `/user/repo/sync/status` |
| Method | `GET` |
| 接口描述 | 查询当前用户各绑定账户的自动采集状态、最近一次采集结果与下次自动采集时间 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**：无（仅返回当前用户自己的账户）。

**返回数据**

`data` 为数组（无绑定账户时返回空数组），元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| accountId | integer | 必须 | - | 托管账户 ID | format: int64 |
| platform | string | 必须 | - | 平台标识：gitee / github | - |
| accountName | string | 必须 | - | 托管平台账号名 | - |
| syncStatus | integer | 必须 | 0 | 采集状态：0 未采集，1 采集中，2 采集正常，3 采集失败 | format: int32 |
| lastSyncTime | string | 非必须 | - | 最近一次自动采集完成时间 | - |
| lastSyncResult | integer | 非必须 | - | 最近一次采集结果：0 失败 1 成功 | format: int32 |
| newCommitCount | integer | 必须 | 0 | 最近一次自动采集新增的提交数 | format: int32 |
| errorMsg | string | 非必须 | - | 采集失败原因；采集正常时为空 | - |
| nextSyncTime | string | 非必须 | - | 下一次自动采集时间（由定时任务配置计算，见 5.2 节） | - |

> **成员端为什么没有「立即同步」**：提交数据全部由后台自动采集——绑定账户后触发首次采集，此后**每日定时增量采集**（失败自动重试）。成员端只做读取，不提供触发按钮：一是避免成员反复触发导致第三方平台限流、甚至把令牌锁死；二是保证全中心统计口径一致（否则同一时间点各人数据新旧不一）。因此本接口替代了原「手动同步」，只回答「采到哪了、下次什么时候采」。
>
> **采集失败怎么办**：若 `syncStatus = 3` 且 `errorMsg` 提示令牌无效或过期，请用接口 `2-04` 更新令牌——更新成功后服务端会立即重新采集一次，无需手动操作；若提示第三方平台调用失败，多为限流或网络波动，等待下次自动采集即可（管理员也可在后台用 `2-17` 手动补采）。

> **403 不等于令牌失效**：GitHub / Gitee 在权限不足或触发限流时也可能返回 403。服务端只在平台明确返回未授权（401）或响应明确表明令牌无效时才把账户置为失效；其余 403 按可重试失败处理，保留账户并等待下次采集，避免误停用。

> **统计范围**：本期只采集当前绑定账户**本人名下**的仓库，组织仓库与他人仓库暂不纳入。
>
> **数据时效提示**：前端可在统计页面展示「数据由系统自动更新，最近更新于 `lastSyncTime`」，避免成员误以为实时。

### 8.3 仓库管理（2-07 ~ 2-08）

#### 8.3.1 我的仓库分页（2-07）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-07 |
| 接口名称 | 我的仓库分页 |
| Path | `/user/repo/project/page` |
| Method | `GET` |
| 接口描述 | 分页查询当前用户已采集的仓库，支持按平台、是否纳入统计筛选 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| keyword | string | 非必须 | - | 模糊匹配 repoName / ownerName | - |
| platform | string | 非必须 | - | 平台标识：gitee / github | - |
| tracked | integer | 非必须 | - | 0 不统计，1 统计 | format: int32 |
| accountId | integer | 非必须 | - | 限定某个托管账户 | format: int64 |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| projectId | integer | 必须 | - | 仓库 ID | format: int64 |
| repoName | string | 必须 | - | 仓库名称 | - |
| repoUrl | string | 必须 | - | 仓库地址 | - |
| ownerName | string | 非必须 | - | 仓库所有者 | - |
| language | string | 非必须 | - | 主要语言 | - |
| defaultBranch | string | 非必须 | - | 默认分支 | - |
| starCount | integer | 必须 | 0 | Star 数 | format: int32 |
| forkCount | integer | 必须 | 0 | Fork 数 | format: int32 |
| lastPushTime | string | 非必须 | - | 最近推送时间 | - |
| tracked | integer | 必须 | - | 是否纳入统计：0 不统计 1 统计 | format: int32 |
| syncStatus | integer | 必须 | - | 采集状态：0 未采集 1 采集中 2 采集正常 3 采集失败 | format: int32 |
| commitCount | integer | 非必须 | 0 | 该仓库已采集的提交总数，用于列表展示 | format: int32 |

> `commitCount` 为聚合值，按 `repo_project.id` 对 `repo_commit` 计数得到；列表查询时统一批量聚合，避免逐行查询。

#### 8.3.2 开启 / 关闭仓库统计（2-08）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-08 |
| 接口名称 | 开启 / 关闭仓库统计 |
| Path | `/user/repo/project/{id}/track` |
| Method | `PUT` |
| 接口描述 | 切换某个仓库是否纳入提交统计；关闭后该仓库的提交不再计入热力图与排行榜 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 仓库 ID | 12 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| tracked | integer | 必须 | - | 0 不统计，1 统计 | format: int32 |

**返回数据**：`data` 为 `null`。

**错误码**：20004 仓库不存在；20005 该仓库不在当前账户下。

> 关闭统计**不会删除已采集的提交记录**，只是统计查询时过滤掉。重新开启后历史提交立即恢复计入，无需重新同步。

### 8.4 提交记录与统计（2-09 ~ 2-12）

#### 8.4.1 提交记录分页（2-09）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-09 |
| 接口名称 | 提交记录分页 |
| Path | `/user/repo/commit/page` |
| Method | `GET` |
| 接口描述 | 分页查询当前用户已采集的提交明细，支持按仓库、分支、时间与提交信息筛选 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| projectId | integer | 非必须 | - | 限定某个仓库 | format: int64 |
| branch | string | 非必须 | - | 限定某个分支 | - |
| keyword | string | 非必须 | - | 模糊匹配提交信息 message | - |
| includeMerge | integer | 非必须 | 0 | 是否包含合并提交：0 不包含 1 包含 | format: int32 |
| beginTime | string | 非必须 | - | 提交时间起，格式 yyyy-MM-dd HH:mm:ss | - |
| endTime | string | 非必须 | - | 提交时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| commitId | integer | 必须 | - | 提交记录 ID | format: int64 |
| commitSha | string | 必须 | - | 提交 SHA（完整 40 位） | - |
| shortSha | string | 必须 | - | 提交 SHA 前 8 位，便于列表展示 | - |
| projectId | integer | 必须 | - | 所属仓库 ID | format: int64 |
| projectName | string | 必须 | - | 仓库名称 | - |
| branch | string | 非必须 | - | 所属分支 | - |
| authorName | string | 非必须 | - | 提交作者名 | - |
| message | string | 非必须 | - | 提交信息 | - |
| commitTime | string | 必须 | - | 提交时间 | - |
| addLines | integer | 必须 | 0 | 新增行数 | format: int32 |
| delLines | integer | 必须 | 0 | 删除行数 | format: int32 |
| isMerge | integer | 必须 | 0 | 是否合并提交：0 否 1 是 | format: int32 |

> `includeMerge` 默认 0，即热力图与趋势统计**默认排除合并提交**，避免「一次合并刷出一堆提交」导致数据虚高。

#### 8.4.2 提交-日期趋势图（2-10）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-10 |
| 接口名称 | 提交-日期趋势图 |
| Path | `/user/repo/commit/chart` |
| Method | `GET` |
| 接口描述 | 按时间粒度返回当前用户的提交趋势，供前端绘制折线图 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| projectId | integer | 非必须 | - | 限定某个仓库；不传表示全部仓库 | format: int64 |
| beginDate | string | 非必须 | 近 30 天 | 开始日期，格式 yyyy-MM-dd | - |
| endDate | string | 非必须 | 今天 | 结束日期 | - |
| granularity | integer | 非必须 | 1 | 时间粒度：1 按日 2 按周 3 按月 | format: int32 |

**返回数据**

`data` 为数组，元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| date | string | 必须 | - | 时间点标签；按日 yyyy-MM-dd，按周 yyyy-第w周，按月 yyyy-MM | - |
| commitCount | integer | 必须 | 0 | 该时间点的提交数 | format: int32 |
| addLines | integer | 必须 | 0 | 新增行数 | format: int32 |
| delLines | integer | 必须 | 0 | 删除行数 | format: int32 |

> 区间内**没有提交的日期同样返回**（`commitCount = 0`），保证前端折线图不断点。

#### 8.4.3 贡献热力图（2-11）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-11 |
| 接口名称 | 贡献热力图 |
| Path | `/user/repo/commit/heatmap` |
| Method | `GET` |
| 接口描述 | 按「日期 → 当日提交数量」聚合，返回 GitHub 风格热力图数据 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| year | string | 非必须 | 当前年份 | 统计年份，格式 yyyy | - |
| userId | integer | 非必须 | 当前用户 | 查看指定成员的热力图（仅统计口径相同的前台场景使用） | format: int64 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| year | string | 必须 | - | 统计年份 | - |
| totalCommits | integer | 必须 | 0 | 全年提交总数 | format: int32 |
| activeDays | integer | 必须 | 0 | 有提交的天数（活跃天数） | format: int32 |
| maxDaily | integer | 必须 | 0 | 单日最大提交数，用于前端计算色阶基准 | format: int32 |
| cells | array | 必须 | - | 每日数据点数组，结构见下表 | - |

`cells` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| date | string | 必须 | - | 日期，格式 yyyy-MM-dd | - |
| count | integer | 必须 | 0 | 当日提交数 | format: int32 |
| level | integer | 必须 | 0 | 色阶等级 0~4，0 表示无提交；前端按等级着色（等级越高颜色越深） | format: int32 |

**色阶划分规则**

| level | 条件 | 前端建议颜色 |
| --- | --- | --- |
| 0 | `count = 0` | 空白色块 |
| 1 | `0 < count ≤ maxDaily × 25%` | 最浅 |
| 2 | `maxDaily × 25% < count ≤ maxDaily × 50%` | 浅 |
| 3 | `maxDaily × 50% < count ≤ maxDaily × 75%` | 中 |
| 4 | `count > maxDaily × 75%` | 最深 |

> **色阶由后端算好下发**（`level` 字段），前端不再自行判断阈值——这样多端（PC / 移动端）着色规则一致，改规则只需改后端。

#### 8.4.4 个人贡献概览（2-12）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-12 |
| 接口名称 | 个人贡献概览 |
| Path | `/user/repo/stat/overview` |
| Method | `GET` |
| 接口描述 | 返回当前用户在指定时间范围内的贡献指标汇总与排名 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| beginTime | string | 非必须 | 本年度 1 月 1 日 | 统计起始时间 | - |
| endTime | string | 非必须 | 当前时间 | 统计截止时间 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| commitCount | integer | 必须 | 0 | 提交总数 | format: int32 |
| activeDays | integer | 必须 | 0 | 活跃天数 | format: int32 |
| addLines | integer | 必须 | 0 | 新增代码行数 | format: int32 |
| delLines | integer | 必须 | 0 | 删除代码行数 | format: int32 |
| projectCount | integer | 必须 | 0 | 有提交的仓库数 | format: int32 |
| branchCount | integer | 必须 | 0 | 有提交的分支数 | format: int32 |
| rank | integer | 非必须 | - | 在中心内的提交数排名 | format: int32 |
| contributionScore | number | 非必须 | - | 贡献度得分 | 计算权重见下方说明 |
| lastSyncTime | string | 非必须 | - | 最近一次自动采集完成时间，用于展示「数据更新于…」 | - |

> **贡献度算法（待会议确定）**：当前默认公式为
> `提交数 × 1 + 活跃天数 × 2 + 代码行数 × 0.01`，各项权重**可配置**（配置项 `repo.contribution.weight.*`）。
> 该公式为 v1 占位实现，`contributionScore` 字段结构不变，权重调整不影响接口契约。

### 8.5 后台管理与数据补采（2-13 ~ 2-17）

#### 8.5.1 账户与仓库分页（2-13）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-13 |
| 接口名称 | 账户与仓库分页（后台） |
| Path | `/admin/repo/account/page` |
| Method | `GET` |
| 接口描述 | 管理端查看全中心成员的账户绑定与提交采集情况 |
| 权限码 | `repo:account:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| keyword | string | 非必须 | - | 模糊匹配 accountName / realName | - |
| platform | string | 非必须 | - | 平台标识：gitee / github | - |
| status | integer | 非必须 | - | 0 失效，1 有效 | format: int32 |
| grade | string | 非必须 | - | 年级筛选 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| accountId | integer | 必须 | - | 托管账户 ID | format: int64 |
| userId | integer | 必须 | - | 成员用户 ID | format: int64 |
| realName | string | 必须 | - | 成员姓名 | - |
| grade | string | 非必须 | - | 年级 | - |
| platform | string | 必须 | - | 平台标识：gitee / github | - |
| accountName | string | 必须 | - | 托管平台账号名 | - |
| status | integer | 必须 | - | 0 失效，1 有效 | format: int32 |
| projectCount | integer | 必须 | 0 | 已采集仓库数 | format: int32 |
| commitCount | integer | 必须 | 0 | 已采集提交数 | format: int32 |
| lastSyncTime | string | 非必须 | - | 最近同步时间 | - |
| bindTime | string | 必须 | - | 绑定时间 | - |

#### 8.5.2 成员贡献排行（2-14）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-14 |
| 接口名称 | 成员贡献排行（后台） |
| Path | `/admin/repo/stat/rank` |
| Method | `GET` |
| 接口描述 | 按指定指标返回全中心成员的贡献排行，用于贡献度看板 |
| 权限码 | `repo:stat:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| beginTime | string | 非必须 | 本年度 1 月 1 日 | 统计起始时间 | - |
| endTime | string | 非必须 | 当前时间 | 统计截止时间 | - |
| sortField | integer | 非必须 | 1 | 排序依据：1 提交数 2 活跃天数 3 代码行数 4 贡献度得分 | format: int32 |
| limit | integer | 非必须 | 20 | 返回条数上限，范围 1~100 | format: int32 |

**返回数据**

`data` 为数组，元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| rank | integer | 必须 | - | 名次，从 1 开始 | format: int32 |
| userId | integer | 必须 | - | 成员用户 ID | format: int64 |
| realName | string | 必须 | - | 成员姓名 | - |
| grade | string | 非必须 | - | 年级 | - |
| avatarUrl | string | 非必须 | - | 头像地址 | - |
| commitCount | integer | 必须 | 0 | 提交总数 | format: int32 |
| activeDays | integer | 必须 | 0 | 活跃天数 | format: int32 |
| addLines | integer | 必须 | 0 | 新增代码行数 | format: int32 |
| delLines | integer | 必须 | 0 | 删除代码行数 | format: int32 |
| contributionScore | number | 非必须 | - | 贡献度得分 | - |

> **只统计有效账户**：`repo_account.status = 0`（令牌失效）与 `tracked = 0` 的仓库不计入排行。

#### 8.5.3 中心提交趋势（2-15）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-15 |
| 接口名称 | 中心提交趋势（后台） |
| Path | `/admin/repo/stat/trend` |
| Method | `GET` |
| 接口描述 | 按时间粒度返回全中心的提交趋势与参与人数 |
| 权限码 | `repo:stat:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| beginDate | string | 非必须 | 近 90 天 | 开始日期，格式 yyyy-MM-dd | - |
| endDate | string | 非必须 | 今天 | 结束日期 | - |
| granularity | integer | 非必须 | 2 | 时间粒度：1 按日 2 按周 3 按月 | format: int32 |

**返回数据**

`data` 为数组，元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| date | string | 必须 | - | 时间点标签 | - |
| commitCount | integer | 必须 | 0 | 该时间点的提交总数 | format: int32 |
| memberCount | integer | 必须 | 0 | 该时间点有提交的成员数 | format: int32 |
| addLines | integer | 必须 | 0 | 新增行数 | format: int32 |
| delLines | integer | 必须 | 0 | 删除行数 | format: int32 |

#### 8.5.4 同步日志分页（2-16）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-16 |
| 接口名称 | 同步日志分页（后台） |
| Path | `/admin/repo/sync/log/page` |
| Method | `GET` |
| 接口描述 | 查询自动采集与管理员手动补采的执行记录，用于排查采集失败原因 |
| 权限码 | `repo:sync:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| userId | integer | 非必须 | - | 限定某个成员 | format: int64 |
| syncType | integer | 非必须 | - | 1 定时自动 2 管理员手动补采 | format: int32 |
| syncResult | integer | 非必须 | - | 0 失败 1 成功 | format: int32 |
| beginTime | string | 非必须 | - | 执行时间起 | - |
| endTime | string | 非必须 | - | 执行时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| logId | integer | 必须 | - | 日志 ID | format: int64 |
| userId | integer | 必须 | - | 成员用户 ID | format: int64 |
| realName | string | 非必须 | - | 成员姓名 | - |
| platform | string | 非必须 | - | 平台标识 | - |
| accountName | string | 非必须 | - | 托管平台账号名 | - |
| syncType | integer | 必须 | - | 1 定时自动 2 管理员手动补采 | format: int32 |
| syncResult | integer | 必须 | - | 0 失败 1 成功 | format: int32 |
| commitCount | integer | 必须 | 0 | 本次同步到的提交数 | format: int32 |
| costMs | integer | 必须 | 0 | 耗时（毫秒） | format: int64 |
| errorMsg | string | 非必须 | - | 错误信息，失败时排查依据 | - |
| createTime | string | 必须 | - | 执行时间 | - |

**错误码**：20001 托管账户不存在；20008 未选择要同步的账户。

> **排查指引**：若 `errorMsg` 提示令牌无效，引导成员在 `2-04` 更新令牌；若提示接口调用失败，多为第三方平台限流或网络问题，可等待下次定时同步。

#### 8.5.5 手动补采（后台，2-17）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 2-17 |
| 接口名称 | 手动补采（后台） |
| Path | `/admin/repo/sync` |
| Method | `POST` |
| 接口描述 | 管理员手工触发一次补采（一个、多个或全部账户）；日常采集由定时任务自动完成 |
| 权限码 | `repo:sync:exec` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| accountIds | array | 非必须 | [] | 待补采的账户 ID 列表；传空数组表示全部有效账户 | - |
| fullSync | integer | 非必须 | 0 | 采集范围：0 增量补采（从 `repo_account.last_sync_time` 起拉）1 全量补采（忽略上次时间，重拉全部历史提交） | format: int32 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| totalAccounts | integer | 必须 | - | 本次处理的账户总数 | format: int32 |
| successCount | integer | 必须 | - | 成功的账户数 | format: int32 |
| failCount | integer | 必须 | - | 失败的账户数 | format: int32 |
| commitCount | integer | 必须 | - | 全部账户合计新增提交数 | format: int32 |
| costMs | integer | 必须 | - | 总耗时（毫秒） | format: int64 |

**错误码**：20006 同步任务执行中，请稍后再试；20008 未选择要同步的账户。

> **串行执行 + 限流**：服务端按账户串行处理并加入间隔，避免短时间内大量请求触发第三方平台风控。账户数较多时总耗时会明显增长，前端应提示「已提交，请稍后查看同步日志」。
>
> **这是补采，不是日常入口**：日常采集由定时任务自动完成（每日凌晨一次），成员端也没有同步按钮。本接口只用于三种情况：① 成员批量更换令牌后补数据；② 新成员集中接入时批量初始化；③ 排查数据缺失问题时即时重采。
>
> **不要频繁调用**：全量补采会遍历所选账户下所有仓库的所有分支，短时间重复触发容易触发第三方平台风控。需要等待时可先看 `2-16` 同步日志。
>
> **`fullSync` 只在本次请求内生效、不落库**：它只是一个「采集范围」开关，不会写进 `repo_sync_log`——该表的 `sync_type` 只区分「1 定时自动 / 2 管理员手动补采」，与增量 / 全量无关。之所以不需要记录：无论哪种范围，提交都按 `repo_commit` 的 `(project_id, commit_sha)` 唯一键去重写入，重采不会让热力图数字虚高，唯一代价是多花时间。
>
> **什么时候必须传 `fullSync = 1`**：① 成员令牌失效一段时间后重新授权——增量起点（`last_sync_time`）之后、令牌失效期间的提交会漏采；② 排查「某段时间的提交缺失」。日常采集与普通补采都用默认的 `0`。

---

## 第 9 章 模块三：部门预算管理（budget）

### 9.1 模块概述

- **职责**：预算周期与科目维护、成员经费申请、管理员审核、支出登记、执行率统计与公示。
- **数据模型**：见 5.3 节。
- **接口清单**：见 4.4 节（`3-01` ~ `3-25`，共 25 个接口）。
- **已确定口径**：
  1. 预算由**管理员统一管理**，采购与报销统一经管理员之手；
  2. 成员在预算周期**执行期间可随时提交申请**，取消集中「征集」环节；
  3. 资金流动全程可追踪，形成「申请 → 审核 → 拨款 → 支出 → 公示」完整链路；
  4. 接口 `3-16`（发起预算征集）为 v1.1 预留，v1 不实现。

### 9.2 前台：周期公示（3-01 ~ 3-04）

#### 9.2.1 公示预算周期分页（3-01）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-01 |
| 接口名称 | 公示预算周期分页 |
| Path | `/user/budget/period/page` |
| Method | `GET` |
| 接口描述 | 成员端查询已公示的预算周期及执行进度 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| year | string | 非必须 | - | 年度筛选，格式 yyyy | - |
| status | integer | 非必须 | - | 状态筛选：1 执行中 2 已结束 | format: int32 |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；仅返回 `isPublic = 1` 的周期，按 `startDate` 倒序。`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| periodId | integer | 必须 | - | 预算周期 ID | format: int64 |
| title | string | 必须 | - | 周期标题 | - |
| year | string | 必须 | - | 所属年度 | - |
| totalAmount | number | 必须 | 0.00 | 预算总额度 | - |
| usedAmount | number | 必须 | 0.00 | 已使用额度 | - |
| remainAmount | number | 必须 | 0.00 | 剩余额度 | - |
| executionRate | number | 必须 | 0.00 | 执行率（百分比数值，如 62.50 表示 62.50%） | - |
| startDate | string | 非必须 | - | 周期开始日期 | - |
| endDate | string | 非必须 | - | 周期结束日期 | - |
| status | integer | 必须 | - | 0 草稿，1 执行中，2 已结束 | format: int32 |
| publicTime | string | 非必须 | - | 公示时间 | - |

> **草稿不公示**：`status = 0`（草稿）的周期无论 `isPublic` 取值如何，都不在前台返回。

#### 9.2.2 预算周期详情（3-02）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-02 |
| 接口名称 | 预算周期详情 |
| Path | `/user/budget/period/{id}` |
| Method | `GET` |
| 接口描述 | 查询周期详情与执行概况，用于前台预算详情页头部 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 预算周期 ID | 2 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| periodId | integer | 必须 | - | 预算周期 ID | format: int64 |
| title | string | 必须 | - | 周期标题 | - |
| year | string | 必须 | - | 所属年度 | - |
| totalAmount | number | 必须 | 0.00 | 预算总额度 | - |
| usedAmount | number | 必须 | 0.00 | 已使用额度 | - |
| remainAmount | number | 必须 | 0.00 | 剩余额度 | - |
| executionRate | number | 必须 | 0.00 | 执行率（百分比数值） | - |
| startDate | string | 非必须 | - | 周期开始日期 | - |
| endDate | string | 非必须 | - | 周期结束日期 | - |
| description | string | 非必须 | - | 周期说明 | - |
| status | integer | 必须 | - | 0 草稿，1 执行中，2 已结束 | format: int32 |
| itemCount | integer | 必须 | 0 | 科目数量 | format: int32 |
| applyCount | integer | 必须 | 0 | 该周期下的申请总数 | format: int32 |
| expenseCount | integer | 必须 | 0 | 该周期下的支出笔数 | format: int32 |
| publicTime | string | 非必须 | - | 公示时间 | - |

**错误码**：30001 预算周期不存在。

#### 9.2.3 预算科目明细（3-03）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-03 |
| 接口名称 | 预算科目明细 |
| Path | `/user/budget/period/{id}/item/list` |
| Method | `GET` |
| 接口描述 | 查询指定周期下的全部科目及各自额度使用情况 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 预算周期 ID | 2 |

**返回数据**

`data` 为数组，按 `sortNo` 升序；元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| itemId | integer | 必须 | - | 科目 ID | format: int64 |
| name | string | 必须 | - | 科目名称 | - |
| category | integer | 必须 | - | 1 设备采购，2 耗材，3 活动，4 差旅，5 其他 | format: int32 |
| budgetAmount | number | 必须 | 0.00 | 科目预算额度 | - |
| usedAmount | number | 必须 | 0.00 | 已使用额度 | - |
| executionRate | number | 必须 | 0.00 | 执行率（百分比数值） | - |
| remark | string | 非必须 | - | 备注 | - |

**错误码**：30001 预算周期不存在。

#### 9.2.4 支出明细公示（3-04）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-04 |
| 接口名称 | 支出明细公示 |
| Path | `/user/budget/expense/page` |
| Method | `GET` |
| 接口描述 | 分页查询周期的支出明细，供成员监督资金去向 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| periodId | integer | 非必须 | 最新公示周期 | 预算周期 ID | format: int64 |
| itemId | integer | 非必须 | - | 科目筛选 | format: int64 |
| keyword | string | 非必须 | - | 模糊匹配支出标题 | - |
| beginDate | string | 非必须 | - | 支出日期起，格式 yyyy-MM-dd | - |
| endDate | string | 非必须 | - | 支出日期止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| expenseId | integer | 必须 | - | 支出记录 ID | format: int64 |
| periodId | integer | 必须 | - | 所属周期 ID | format: int64 |
| periodTitle | string | 必须 | - | 周期标题 | - |
| itemId | integer | 必须 | - | 所属科目 ID | format: int64 |
| itemName | string | 必须 | - | 科目名称 | - |
| title | string | 必须 | - | 支出标题 | - |
| amount | number | 必须 | 0.00 | 支出金额 | - |
| expenseDate | string | 必须 | - | 支出日期 | - |
| handlerName | string | 非必须 | - | 经办人姓名 | - |
| remark | string | 非必须 | - | 备注 | - |

> **凭证不在此接口返回**：发票 / 收据可能含个人或商户敏感信息，公示接口只披露金额、科目、日期与经办人，凭证文件 ID 不对成员端开放。

### 9.3 前台：预算申请（3-05 ~ 3-09）

#### 9.3.1 提交预算申请（3-05）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-05 |
| 接口名称 | 提交预算申请 |
| Path | `/user/budget/apply` |
| Method | `POST` |
| 接口描述 | 成员提交经费申请；预算周期**执行期间可随时提交**，无需等待集中征集 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| periodId | integer | 必须 | - | 预算周期 ID | format: int64 |
| itemId | integer | 必须 | - | 预算科目 ID | format: int64 |
| title | string | 必须 | - | 申请标题，最长 100 字 | - |
| amount | number | 必须 | - | 申请金额，最多两位小数 | 必须大于 0 |
| reason | string | 非必须 | - | 申请事由，最长 500 字 | - |
| expectDate | string | 非必须 | - | 期望使用日期，格式 yyyy-MM-dd | - |
| attachFileId | integer | 非必须 | - | 附件文件 ID，可先调用接口 8-01 上传后填入 | format: int64 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| applyId | integer | 必须 | - | 申请记录 ID | format: int64 |

**错误码**：30001 预算周期不存在；30002 该预算周期未处于执行中，无法提交申请；30003 预算科目不存在；30004 该科目不属于所选预算周期；30005 申请金额必须大于 0；30006 申请金额超出科目剩余额度。

> **额度校验只看科目剩余额度**，不占用额度：申请仅代表「想花这笔钱」，真正扣减额度发生在管理员登记支出时（接口 `3-22`）。因此同一科目允许存在多笔待审核申请，审核时由管理员控制实际批准金额。

#### 9.3.2 我的预算申请分页（3-06）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-06 |
| 接口名称 | 我的预算申请分页 |
| Path | `/user/budget/apply/my/page` |
| Method | `GET` |
| 接口描述 | 分页查询当前登录用户提交的经费申请 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 非必须 | - | 0 待审核，1 已通过，2 已驳回，3 已撤销 | format: int32 |
| periodId | integer | 非必须 | - | 限定某个预算周期 | format: int64 |
| beginTime | string | 非必须 | - | 提交时间起 | - |
| endTime | string | 非必须 | - | 提交时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；仅返回 `applyUserId = 当前用户` 的记录，按提交时间倒序。`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| applyId | integer | 必须 | - | 申请记录 ID | format: int64 |
| periodId | integer | 必须 | - | 所属周期 ID | format: int64 |
| periodTitle | string | 必须 | - | 周期标题 | - |
| itemId | integer | 必须 | - | 科目 ID | format: int64 |
| itemName | string | 必须 | - | 科目名称 | - |
| title | string | 必须 | - | 申请标题 | - |
| amount | number | 必须 | 0.00 | 申请金额 | - |
| expectDate | string | 非必须 | - | 期望使用日期 | - |
| attachFileId | integer | 非必须 | - | 附件文件 ID | format: int64 |
| status | integer | 必须 | - | 0 待审核，1 已通过，2 已驳回，3 已撤销 | format: int32 |
| auditUserName | string | 非必须 | - | 审核人姓名 | - |
| auditTime | string | 非必须 | - | 审核时间 | - |
| auditRemark | string | 非必须 | - | 审核意见 | - |
| createTime | string | 必须 | - | 提交时间 | - |

#### 9.3.3 预算申请详情（3-07）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-07 |
| 接口名称 | 预算申请详情 |
| Path | `/user/budget/apply/{id}` |
| Method | `GET` |
| 接口描述 | 查询指定申请的完整信息，含申请事由与关联的支出记录 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 申请记录 ID | 18 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| applyId | integer | 必须 | - | 申请记录 ID | format: int64 |
| periodId | integer | 必须 | - | 所属周期 ID | format: int64 |
| periodTitle | string | 必须 | - | 周期标题 | - |
| itemId | integer | 必须 | - | 科目 ID | format: int64 |
| itemName | string | 必须 | - | 科目名称 | - |
| applyUserId | integer | 必须 | - | 申请人 ID | format: int64 |
| applyUserName | string | 必须 | - | 申请人姓名 | - |
| title | string | 必须 | - | 申请标题 | - |
| amount | number | 必须 | 0.00 | 申请金额 | - |
| reason | string | 非必须 | - | 申请事由 | - |
| expectDate | string | 非必须 | - | 期望使用日期 | - |
| attachFileId | integer | 非必须 | - | 附件文件 ID | format: int64 |
| status | integer | 必须 | - | 0 待审核，1 已通过，2 已驳回，3 已撤销 | format: int32 |
| auditUserName | string | 非必须 | - | 审核人姓名 | - |
| auditTime | string | 非必须 | - | 审核时间 | - |
| auditRemark | string | 非必须 | - | 审核意见 | - |
| expenseList | array | 非必须 | [] | 关联的支出记录，元素含 `expenseId`、`title`、`amount`、`expenseDate` | - |
| createTime | string | 必须 | - | 提交时间 | - |

**错误码**：30007 预算申请不存在；30008 无权访问该预算申请。

> **越权保护**：成员只能查看本人提交的申请。管理端查看他人申请使用接口 `3-20` / `3-21`，不共用本接口。

#### 9.3.4 撤销预算申请（3-08）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-08 |
| 接口名称 | 撤销预算申请 |
| Path | `/user/budget/apply/{id}/cancel` |
| Method | `PUT` |
| 接口描述 | 成员撤销尚未审核的申请 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 申请记录 ID | 18 |

**返回数据**：`data` 为 `null`。

**错误码**：30007 预算申请不存在；30008 无权访问该预算申请；30009 当前状态不允许撤销。

> **只有「待审核」可撤销**：已通过（可能已产生支出）、已驳回、已撤销的申请都不能再次撤销。前端应据 `status` 控制按钮可用性，服务端同样校验，避免绕过。

#### 9.3.5 我的经费汇总（3-09）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-09 |
| 接口名称 | 我的经费汇总 |
| Path | `/user/budget/stat/my` |
| Method | `GET` |
| 接口描述 | 统计当前登录用户在某年度的申请与获批情况 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| year | string | 非必须 | 当前年份 | 统计年度，格式 yyyy | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| applyTotal | integer | 必须 | 0 | 申请总数 | format: int32 |
| applyAmount | number | 必须 | 0.00 | 申请总金额 | - |
| approvedCount | integer | 必须 | 0 | 已通过数量 | format: int32 |
| approvedAmount | number | 必须 | 0.00 | 已通过总金额 | - |
| rejectedCount | integer | 必须 | 0 | 已驳回数量 | format: int32 |
| pendingCount | integer | 必须 | 0 | 待审核数量 | format: int32 |
| cancelledCount | integer | 必须 | 0 | 已撤销数量 | format: int32 |
| expenseAmount | number | 必须 | 0.00 | 已产生的实际支出总额 | - |

### 9.4 后台：周期管理（3-10 ~ 3-16）

#### 9.4.1 预算周期分页（后台，3-10）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-10 |
| 接口名称 | 预算周期分页（后台） |
| Path | `/admin/budget/period/page` |
| Method | `GET` |
| 接口描述 | 管理端分页查询全部预算周期（含草稿） |
| 权限码 | `budget:period:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| keyword | string | 非必须 | - | 模糊匹配周期标题 | - |
| year | string | 非必须 | - | 年度筛选，格式 yyyy | - |
| status | integer | 非必须 | - | 0 草稿，1 执行中，2 已结束 | format: int32 |
| isPublic | integer | 非必须 | - | 0 不公示，1 公示中 | format: int32 |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| periodId | integer | 必须 | - | 预算周期 ID | format: int64 |
| title | string | 必须 | - | 周期标题 | - |
| year | string | 必须 | - | 所属年度 | - |
| totalAmount | number | 必须 | 0.00 | 预算总额度 | - |
| usedAmount | number | 必须 | 0.00 | 已使用额度 | - |
| remainAmount | number | 必须 | 0.00 | 剩余额度 | - |
| executionRate | number | 必须 | 0.00 | 执行率（百分比数值） | - |
| status | integer | 必须 | - | 0 草稿，1 执行中，2 已结束 | format: int32 |
| isPublic | integer | 必须 | - | 0 不公示，1 公示中 | format: int32 |
| startDate | string | 非必须 | - | 周期开始日期 | - |
| endDate | string | 非必须 | - | 周期结束日期 | - |
| itemCount | integer | 必须 | 0 | 科目数量 | format: int32 |
| applyCount | integer | 必须 | 0 | 申请数量 | format: int32 |
| createTime | string | 必须 | - | 创建时间 | - |

#### 9.4.2 新增预算周期（起草，3-11）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-11 |
| 接口名称 | 新增预算周期（起草） |
| Path | `/admin/budget/period` |
| Method | `POST` |
| 接口描述 | 管理员创建预算周期，初始状态为草稿；确认无误后由接口 `3-14` 流转为执行中 |
| 权限码 | `budget:period:add` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| title | string | 必须 | - | 周期标题，最长 100 字 | - |
| year | string | 必须 | - | 所属年度，格式 yyyy | - |
| totalAmount | number | 必须 | - | 预算总额度，必须大于 0 | - |
| startDate | string | 非必须 | - | 周期开始日期，格式 yyyy-MM-dd | - |
| endDate | string | 非必须 | - | 周期结束日期，需晚于开始日期 | - |
| description | string | 非必须 | - | 周期说明，最长 500 字 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| periodId | integer | 必须 | - | 新增周期 ID | format: int64 |

#### 9.4.3 修改预算周期（3-12）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-12 |
| 接口名称 | 修改预算周期 |
| Path | `/admin/budget/period` |
| Method | `PUT` |
| 接口描述 | 修改周期信息与总额度；总额度不得低于已使用额度 |
| 权限码 | `budget:period:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| periodId | integer | 必须 | - | 预算周期 ID | format: int64 |
| title | string | 必须 | - | 周期标题，最长 100 字 | - |
| year | string | 必须 | - | 所属年度 | - |
| totalAmount | number | 必须 | - | 预算总额度 | 不得低于 `usedAmount` |
| startDate | string | 非必须 | - | 周期开始日期 | - |
| endDate | string | 非必须 | - | 周期结束日期 | - |
| description | string | 非必须 | - | 周期说明 | - |

**返回数据**：`data` 为 `null`。

**错误码**：30001 预算周期不存在；30016 周期总额度不得低于已使用额度。

#### 9.4.4 删除预算周期（3-13）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-13 |
| 接口名称 | 删除预算周期 |
| Path | `/admin/budget/period/{id}` |
| Method | `DELETE` |
| 接口描述 | 逻辑删除预算周期；存在科目或申请时禁止删除 |
| 权限码 | `budget:period:remove` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 预算周期 ID | 2 |

**返回数据**：`data` 为 `null`。

**错误码**：30001 预算周期不存在；30014 该周期下存在科目或申请，无法删除。

> **只允许删除空周期**：只要周期下存在任何科目或申请记录就拒绝删除，避免出现「有支出但查不到周期」的孤儿数据。确实需要作废时，改用 `3-14` 把状态流转为「已结束」。

#### 9.4.5 周期状态流转（3-14）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-14 |
| 接口名称 | 周期状态流转 |
| Path | `/admin/budget/period/{id}/status` |
| Method | `PUT` |
| 接口描述 | 变更周期状态：草稿 → 执行中 → 已结束 |
| 权限码 | `budget:period:audit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 预算周期 ID | 2 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 必须 | - | 目标状态：1 执行中，2 已结束 | format: int32 |

**返回数据**：`data` 为 `null`。

**错误码**：30001 预算周期不存在；30018 预算周期状态流转不合法；30019 存在待审核的申请，无法结束周期。

**状态流转规则**

| 当前状态 | 允许流转到 | 前置校验 |
| --- | --- | --- |
| 0 草稿 | 1 执行中 | 至少存在一个科目 |
| 1 执行中 | 2 已结束 | 无待审核申请，且所有已通过申请均有对应支出或明确未执行 |
| 2 已结束 | 不允许 | 终态，不可回退 |

> **进入「执行中」后成员才能提交申请**（接口 `3-05`）。状态单向流转，不允许回退，避免已发生的支出失去归属周期。

#### 9.4.6 公示 / 取消公示（3-15）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-15 |
| 接口名称 | 公示 / 取消公示 |
| Path | `/admin/budget/period/{id}/public` |
| Method | `PUT` |
| 接口描述 | 控制周期是否对成员端公示；公示后成员可查看周期、科目与支出明细 |
| 权限码 | `budget:period:public` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 预算周期 ID | 2 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| isPublic | integer | 必须 | - | 0 取消公示，1 公示 | format: int32 |

**返回数据**：`data` 为 `null`。

**错误码**：30001 预算周期不存在。

> 公示时服务端自动写入 `publicTime = 当前时间`；取消公示不清空 `publicTime`，便于追溯首次公示时间。

#### 9.4.7 发起预算征集（v1.1 预留）（3-16）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-16 |
| 接口名称 | 发起预算征集（v1.1 预留） |
| Path | `/admin/budget/period/{id}/collect` |
| Method | `POST` |
| 接口描述 | **v1.0 不实现，接口位预留**。原设计为集中征集成员需求，现已改为「成员在执行期间随时提交申请」，此环节取消 |
| 权限码 | `budget:collect:exec` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 预算周期 ID | 2 |

**返回数据**：`data` 为 `null`。

**错误码**：30020 该功能在 v1.0 未开放。

> **保留原因**：若将来恢复集中征集机制（例如每学期初统一收集一次需求），可直接启用本接口而无需改动前端路由与权限码。v1.0 调用将返回业务码 `30020`。

### 9.5 后台：科目管理（3-17 ~ 3-19）

#### 9.5.1 新增预算科目（3-17）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-17 |
| 接口名称 | 新增预算科目 |
| Path | `/admin/budget/item` |
| Method | `POST` |
| 接口描述 | 在指定周期下新增预算科目与额度 |
| 权限码 | `budget:item:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| periodId | integer | 必须 | - | 所属预算周期 ID | format: int64 |
| name | string | 必须 | - | 科目名称，最长 64 字 | - |
| category | integer | 非必须 | 5 | 1 设备采购，2 耗材，3 活动，4 差旅，5 其他 | format: int32 |
| budgetAmount | number | 必须 | - | 科目预算额度，必须大于 0 | - |
| sortNo | integer | 非必须 | 0 | 排序号，升序 | format: int32 |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| itemId | integer | 必须 | - | 新增科目 ID | format: int64 |

**错误码**：30001 预算周期不存在。

> **科目额度之和不必等于周期总额度**：周期总额度是控制上限，科目额度是分配方案。若科目额度之和超过周期总额度，服务端不拦截（可能有意预留机动额度），但会在看板（`3-25`）中以告警色提示。

#### 9.5.2 修改预算科目（3-18）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-18 |
| 接口名称 | 修改预算科目 |
| Path | `/admin/budget/item` |
| Method | `PUT` |
| 接口描述 | 修改科目信息与额度；额度不得低于已使用额度 |
| 权限码 | `budget:item:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| itemId | integer | 必须 | - | 科目 ID | format: int64 |
| name | string | 必须 | - | 科目名称，最长 64 字 | - |
| category | integer | 非必须 | 5 | 科目分类 | format: int32 |
| budgetAmount | number | 必须 | - | 科目预算额度 | 不得低于 `usedAmount` |
| sortNo | integer | 非必须 | 0 | 排序号 | format: int32 |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**：`data` 为 `null`。

**错误码**：30003 预算科目不存在；30017 科目额度不得低于已使用额度。

#### 9.5.3 删除预算科目（3-19）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-19 |
| 接口名称 | 删除预算科目 |
| Path | `/admin/budget/item/{id}` |
| Method | `DELETE` |
| 接口描述 | 逻辑删除预算科目；已被申请或支出引用时禁止删除 |
| 权限码 | `budget:item:edit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 科目 ID | 7 |

**返回数据**：`data` 为 `null`。

**错误码**：30003 预算科目不存在；30013 该科目已被申请或支出引用，无法删除。

### 9.6 后台：申请审核与支出（3-20 ~ 3-24）

#### 9.6.1 申请审核分页（3-20）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-20 |
| 接口名称 | 申请审核分页（后台） |
| Path | `/admin/budget/apply/page` |
| Method | `GET` |
| 接口描述 | 管理端分页查询全部经费申请，用于审核排队与进度跟踪 |
| 权限码 | `budget:apply:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 非必须 | - | 0 待审核，1 已通过，2 已驳回，3 已撤销 | format: int32 |
| periodId | integer | 非必须 | - | 限定预算周期 | format: int64 |
| itemId | integer | 非必须 | - | 限定科目 | format: int64 |
| applyUserId | integer | 非必须 | - | 限定申请人 | format: int64 |
| keyword | string | 非必须 | - | 模糊匹配申请标题 / 申请人姓名 | - |
| beginTime | string | 非必须 | - | 提交时间起 | - |
| endTime | string | 非必须 | - | 提交时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| applyId | integer | 必须 | - | 申请记录 ID | format: int64 |
| periodId | integer | 必须 | - | 所属周期 ID | format: int64 |
| periodTitle | string | 必须 | - | 周期标题 | - |
| itemId | integer | 必须 | - | 科目 ID | format: int64 |
| itemName | string | 必须 | - | 科目名称 | - |
| applyUserId | integer | 必须 | - | 申请人 ID | format: int64 |
| applyUserName | string | 必须 | - | 申请人姓名 | - |
| grade | string | 非必须 | - | 申请人年级 | - |
| title | string | 必须 | - | 申请标题 | - |
| amount | number | 必须 | 0.00 | 申请金额 | - |
| reason | string | 非必须 | - | 申请事由 | - |
| expectDate | string | 非必须 | - | 期望使用日期 | - |
| attachFileId | integer | 非必须 | - | 附件文件 ID | format: int64 |
| status | integer | 必须 | - | 0 待审核，1 已通过，2 已驳回，3 已撤销 | format: int32 |
| auditUserId | integer | 非必须 | - | 审核人 ID | format: int64 |
| auditUserName | string | 非必须 | - | 审核人姓名 | - |
| auditTime | string | 非必须 | - | 审核时间 | - |
| auditRemark | string | 非必须 | - | 审核意见 | - |
| createTime | string | 必须 | - | 提交时间 | - |

#### 9.6.2 申请审核（通过 / 驳回，3-21）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-21 |
| 接口名称 | 申请审核（通过 / 驳回） |
| Path | `/admin/budget/apply/{id}/audit` |
| Method | `PUT` |
| 接口描述 | 管理员审核经费申请；通过时可调整批准金额 |
| 权限码 | `budget:apply:audit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 申请记录 ID | 18 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| auditStatus | integer | 必须 | - | 1 通过，2 驳回 | format: int32 |
| approvedAmount | number | 非必须 | 申请金额 | 批准金额，通过时可少于申请金额（部分批准） | 不得超过科目剩余额度 |
| auditRemark | string | 非必须 | - | 审核意见，驳回时建议填写 | - |

**返回数据**：`data` 为 `null`。

**错误码**：30007 预算申请不存在；30010 该申请已审核，无法重复审核；30006 申请金额超出科目剩余额度。

> **审核只改状态，不动额度**：通过审核不扣减科目额度，额度在登记支出（`3-22`）时扣减。这样「批了钱但实际没花完」不会造成额度虚耗。
>
> **部分批准**：`approvedAmount` 允许小于申请金额，用于「申请 500 批 300」的场景。若请求中未传，默认按申请金额全额批准。

#### 9.6.3 登记支出（3-22）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-22 |
| 接口名称 | 登记支出 |
| Path | `/admin/budget/expense` |
| Method | `POST` |
| 接口描述 | 管理员登记实际支出并扣减周期与科目额度，形成「申请 → 审核 → 拨款 → 支出」链路的最后一环 |
| 权限码 | `budget:expense:add` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| periodId | integer | 必须 | - | 所属预算周期 ID | format: int64 |
| itemId | integer | 必须 | - | 所属预算科目 ID | format: int64 |
| applyId | integer | 非必须 | - | 关联的预算申请 ID；不传表示无申请的直接支出 | format: int64 |
| title | string | 必须 | - | 支出标题，最长 100 字 | - |
| amount | number | 必须 | - | 支出金额，必须大于 0 | 不得超过周期与科目剩余额度 |
| expenseDate | string | 必须 | - | 支出日期，格式 yyyy-MM-dd | - |
| voucherFileId | integer | 非必须 | - | 凭证文件 ID（发票 / 收据），先调接口 8-01 上传 | format: int64 |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| expenseId | integer | 必须 | - | 新增支出记录 ID | format: int64 |
| periodUsedAmount | number | 必须 | - | 扣减后周期已用额度 | - |
| itemUsedAmount | number | 必须 | - | 扣减后科目已用额度 | - |

**错误码**：30001 预算周期不存在；30003 预算科目不存在；30004 该科目不属于所选预算周期；30007 预算申请不存在；30011 支出金额超出周期剩余额度；30015 该预算周期已结束，无法登记支出。

> **同一事务内完成三件事**：写入 `budget_expense` 记录 → 累加 `budget_period.used_amount` → 累加 `budget_item.used_amount`。任一步失败整体回滚，避免出现「支出登记了但额度没扣」的不一致。
>
> **凭证建议必填**：字段为「非必须」是为了兼容小额支出暂未取得发票的情况，但为满足「任何一笔资金流动可追溯」的目标，**建议管理员一律上传凭证**。

#### 9.6.4 支出分页（后台，3-23）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-23 |
| 接口名称 | 支出分页（后台） |
| Path | `/admin/budget/expense/page` |
| Method | `GET` |
| 接口描述 | 管理端分页查询支出记录，含凭证文件 ID |
| 权限码 | `budget:expense:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| periodId | integer | 非必须 | - | 限定预算周期 | format: int64 |
| itemId | integer | 非必须 | - | 限定科目 | format: int64 |
| handlerId | integer | 非必须 | - | 限定经办人 | format: int64 |
| keyword | string | 非必须 | - | 模糊匹配支出标题 | - |
| beginDate | string | 非必须 | - | 支出日期起 | - |
| endDate | string | 非必须 | - | 支出日期止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| expenseId | integer | 必须 | - | 支出记录 ID | format: int64 |
| periodId | integer | 必须 | - | 所属周期 ID | format: int64 |
| periodTitle | string | 必须 | - | 周期标题 | - |
| itemId | integer | 必须 | - | 科目 ID | format: int64 |
| itemName | string | 必须 | - | 科目名称 | - |
| title | string | 必须 | - | 支出标题 | - |
| amount | number | 必须 | 0.00 | 支出金额 | - |
| expenseDate | string | 必须 | - | 支出日期 | - |
| voucherFileId | integer | 非必须 | - | 凭证文件 ID | format: int64 |
| handlerId | integer | 必须 | - | 经办人 ID | format: int64 |
| handlerName | string | 非必须 | - | 经办人姓名 | - |
| applyId | integer | 非必须 | - | 关联的申请 ID | format: int64 |
| remark | string | 非必须 | - | 备注 | - |
| createTime | string | 必须 | - | 登记时间 | - |

#### 9.6.5 删除支出记录（3-24）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-24 |
| 接口名称 | 删除支出记录 |
| Path | `/admin/budget/expense/{id}` |
| Method | `DELETE` |
| 接口描述 | 逻辑删除支出记录，并回退已占用的周期与科目额度 |
| 权限码 | `budget:expense:remove` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 支出记录 ID | 31 |

**返回数据**：`data` 为 `null`。

**错误码**：30012 支出记录不存在。

> **额度回退在同一事务内完成**：逻辑删除支出记录 → 扣减 `budget_period.used_amount` → 扣减 `budget_item.used_amount`。
>
> **注意关联资产**：若该笔支出已被资产入账引用（`asset_info.budget_expense_id`），删除支出会导致资产失去经费来源。服务端会提示但**不阻断**（管理场景下需要允许纠错），前端应给出明显警告。

### 9.7 后台：执行看板（3-25）

#### 9.7.1 预算执行看板（3-25）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 3-25 |
| 接口名称 | 预算执行看板 |
| Path | `/admin/budget/stat/overview` |
| Method | `GET` |
| 接口描述 | 一次性返回预算执行的整体指标、科目维度明细与月度趋势，供管理端看板渲染 |
| 权限码 | `budget:stat:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| periodId | integer | 非必须 | 最新执行中周期 | 预算周期 ID | format: int64 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| periodId | integer | 必须 | - | 预算周期 ID | format: int64 |
| periodTitle | string | 必须 | - | 周期标题 | - |
| totalAmount | number | 必须 | 0.00 | 预算总额度 | - |
| usedAmount | number | 必须 | 0.00 | 已使用额度 | - |
| remainAmount | number | 必须 | 0.00 | 剩余额度 | - |
| executionRate | number | 必须 | 0.00 | 执行率（百分比数值） | - |
| itemStats | array | 必须 | [] | 科目维度统计，结构见下表 | - |
| applyStats | object | 必须 | - | 申请统计，含 `pendingCount`、`approvedCount`、`rejectedCount`、`approvedAmount` | - |
| monthTrend | array | 必须 | [] | 月度支出趋势，元素含 `month`（yyyy-MM）、`amount` | - |
| overBudgetItems | array | 非必须 | [] | 科目额度之和超出周期总额度的科目名列表，用于告警提示 | - |

`itemStats` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| itemId | integer | 必须 | - | 科目 ID | format: int64 |
| itemName | string | 必须 | - | 科目名称 | - |
| category | integer | 必须 | - | 科目分类 | format: int32 |
| budgetAmount | number | 必须 | 0.00 | 科目预算额度 | - |
| usedAmount | number | 必须 | 0.00 | 已使用额度 | - |
| executionRate | number | 必须 | 0.00 | 执行率（百分比数值） | - |

**错误码**：30001 预算周期不存在。

> **告警提示**：前端应把 `executionRate > 90` 的科目以醒目色标注；`overBudgetItems` 非空时在看板顶部给出「科目额度之和已超出周期总额度」的提示。

---

## 第 10 章 模块四：部门财产管理（asset）

### 10.1 模块概述

- **职责**：资产分类与台账、领用借用申请与审核、变动流水登记、盘点任务与差异统计。
- **数据模型**：见 5.4 节。
- **接口清单**：见 4.5 节（`4-01` ~ `4-27`，共 27 个接口）。
- **已确定口径**：
  1. **一物一码**（单件单记录），借用需审核，日常登记类操作由负责人直接生效；
  2. 所有变动（领用 / 借用 / 归还 / 维修 / 报废 / 调拨 / 盘点）统一写入变动流水表；
  3. 通过 `budgetExpenseId` 与模块三的支出记录关联，实现「采购 → 入账」闭环。

### 10.2 前台：资产查询与借用（4-01 ~ 4-07）

#### 10.2.1 资产分页（前台）（4-01）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-01 |
| 接口名称 | 资产分页（前台） |
| Path | `/user/asset/page` |
| Method | `GET` |
| 接口描述 | 成员端分页查询资产台账，一物一码、单件单记录 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| categoryId | integer | 非必须 | - | 资产分类筛选（含子分类） | format: int64 |
| status | integer | 非必须 | - | 1 在用，2 闲置，3 维修中，4 已借出，5 已报废 | format: int32 |
| keyword | string | 非必须 | - | 模糊匹配 assetNo / name / brand / model | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| assetId | integer | 必须 | - | 资产 ID | format: int64 |
| assetNo | string | 必须 | - | 资产编号（一物一码） | - |
| name | string | 必须 | - | 资产名称 | - |
| categoryId | integer | 非必须 | - | 分类 ID | format: int64 |
| categoryName | string | 非必须 | - | 分类名称 | - |
| brand | string | 非必须 | - | 品牌 | - |
| model | string | 非必须 | - | 型号 | - |
| status | integer | 必须 | - | 1 在用，2 闲置，3 维修中，4 已借出，5 已报废 | format: int32 |
| keeperName | string | 非必须 | - | 保管人姓名 | - |
| location | string | 非必须 | - | 存放位置 | - |
| imageUrls | array | 非必须 | [] | 资产照片地址数组（OSS） | - |

#### 10.2.2 资产详情（前台）（4-02）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-02 |
| 接口名称 | 资产详情（前台） |
| Path | `/user/asset/{id}` |
| Method | `GET` |
| 接口描述 | 查询资产完整信息与最近变动流水，用于详情页 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 资产 ID | 25 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| assetId | integer | 必须 | - | 资产 ID | format: int64 |
| assetNo | string | 必须 | - | 资产编号 | - |
| name | string | 必须 | - | 资产名称 | - |
| categoryId | integer | 非必须 | - | 分类 ID | format: int64 |
| categoryName | string | 非必须 | - | 分类名称 | - |
| brand | string | 非必须 | - | 品牌 | - |
| model | string | 非必须 | - | 型号 | - |
| price | number | 必须 | 0.00 | 购置价格 | - |
| purchaseDate | string | 非必须 | - | 购置日期 | - |
| supplier | string | 非必须 | - | 供应商 | - |
| sourceType | integer | 必须 | - | 1 采购，2 捐赠，3 调拨 | format: int32 |
| status | integer | 必须 | - | 1 在用，2 闲置，3 维修中，4 已借出，5 已报废 | format: int32 |
| keeperId | integer | 非必须 | - | 保管人 ID | format: int64 |
| keeperName | string | 非必须 | - | 保管人姓名 | - |
| location | string | 非必须 | - | 存放位置 | - |
| imageUrls | array | 非必须 | [] | 资产照片地址数组 | - |
| warrantyEndDate | string | 非必须 | - | 保修截止日期 | - |
| remark | string | 非必须 | - | 备注 | - |
| recentRecords | array | 非必须 | [] | 最近 10 条变动流水，元素含 `recordType`、`fromUserName`、`toUserName`、`recordTime`、`remark` | - |

**错误码**：40001 资产不存在。

#### 10.2.3 资产分类树（4-03）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-03 |
| 接口名称 | 资产分类树 |
| Path | `/user/asset/category/tree` |
| Method | `GET` |
| 接口描述 | 返回资产分类树，用于资产列表的分类筛选与表单选择 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**：无。

**返回数据**

`data` 为树形数组，元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| categoryId | integer | 必须 | - | 分类 ID | format: int64 |
| name | string | 必须 | - | 分类名称 | - |
| parentId | integer | 必须 | 0 | 父分类 ID，0 表示一级分类 | format: int64 |
| assetCount | integer | 必须 | 0 | 该分类下的资产数量（含子分类） | format: int32 |
| children | array | 非必须 | [] | 子分类 | - |

#### 10.2.4 资产总览统计（4-04）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-04 |
| 接口名称 | 资产总览统计 |
| Path | `/user/asset/stat/overview` |
| Method | `GET` |
| 接口描述 | 返回资产的数量与金额总览，用于成员端资产页头部卡片 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**：无。

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| total | integer | 必须 | 0 | 资产总数 | format: int32 |
| totalValue | number | 必须 | 0.00 | 资产总价值 | - |
| inUse | integer | 必须 | 0 | 在用数量 | format: int32 |
| idle | integer | 必须 | 0 | 闲置数量 | format: int32 |
| repair | integer | 必须 | 0 | 维修中数量 | format: int32 |
| borrowed | integer | 必须 | 0 | 已借出数量 | format: int32 |
| scrapped | integer | 必须 | 0 | 已报废数量 | format: int32 |
| categoryStats | array | 非必须 | [] | 分类维度统计，元素含 `categoryName`、`count` | - |

#### 10.2.5 借用 / 领用申请（4-05）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-05 |
| 接口名称 | 借用 / 领用申请 |
| Path | `/user/asset/borrow` |
| Method | `POST` |
| 接口描述 | 成员提交资产借用或领用申请；借用需审核并归还，领用审核通过后资产状态置为已借出，不产生归还流程 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| assetId | integer | 必须 | - | 资产 ID | format: int64 |
| borrowType | integer | 必须 | - | 1 借用（需归还），2 领用（不归还） | format: int32 |
| purpose | string | 非必须 | - | 用途说明，最长 255 字 | - |
| expectReturnTime | string | 非必须 | - | 预计归还时间，格式 yyyy-MM-dd HH:mm:ss；借用时建议填写 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| borrowId | integer | 必须 | - | 借用记录 ID | format: int64 |

**错误码**：40001 资产不存在；40007 该资产当前已被借出；40015 资产已报废，无法借用；40019 该资产已有待审核的申请。

> **状态前置校验**：只有 `status` 为「在用」或「闲置」的资产可申请借用；「维修中」「已报废」不可申请，「已借出」需等待归还。**同一资产同时只允许存在一条待审核申请**，重复提交返回 `40019`。**提交申请不改变资产状态**，资产状态在审核通过时才置为「已借出」。

#### 10.2.6 我的借用记录（4-06）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-06 |
| 接口名称 | 我的借用记录 |
| Path | `/user/asset/borrow/my/page` |
| Method | `GET` |
| 接口描述 | 分页查询当前登录用户的借用 / 领用记录 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 非必须 | - | 0 待审核，1 已通过，2 已驳回，3 已归还，4 已逾期 | format: int32 |
| borrowType | integer | 非必须 | - | 1 借用，2 领用 | format: int32 |
| beginTime | string | 非必须 | - | 申请时间起 | - |
| endTime | string | 非必须 | - | 申请时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；仅返回 `userId = 当前用户` 的记录。`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| borrowId | integer | 必须 | - | 借用记录 ID | format: int64 |
| assetId | integer | 必须 | - | 资产 ID | format: int64 |
| assetNo | string | 必须 | - | 资产编号 | - |
| assetName | string | 必须 | - | 资产名称 | - |
| borrowType | integer | 必须 | - | 1 借用，2 领用 | format: int32 |
| purpose | string | 非必须 | - | 用途说明 | - |
| borrowTime | string | 必须 | - | 借用时间 | - |
| expectReturnTime | string | 非必须 | - | 预计归还时间 | - |
| actualReturnTime | string | 非必须 | - | 实际归还时间 | - |
| status | integer | 必须 | - | 0 待审核，1 已通过，2 已驳回，3 已归还，4 已逾期 | format: int32 |
| auditRemark | string | 非必须 | - | 审核意见 | - |

#### 10.2.7 归还登记（本人，4-07）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-07 |
| 接口名称 | 归还登记（本人） |
| Path | `/user/asset/borrow/{id}/return` |
| Method | `PUT` |
| 接口描述 | 借用人在成员端登记归还，归还后资产状态恢复为「在用」 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 借用记录 ID | 48 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| remark | string | 非必须 | - | 归还备注，如「外壳有轻微磨损」 | - |

**返回数据**：`data` 为 `null`。

**错误码**：40008 借用记录不存在；40009 只能归还本人借用的资产；40010 该借用记录状态不允许归还；40011 领用记录无需归还。

> **归还是一次三写的事务**：更新 `asset_borrow`（`actual_return_time`、`status = 3 已归还`）→ 更新 `asset_info.status` 为「在用」→ 写入 `asset_record` 一条「归还」流水。三者必须同事务，否则会出现「记录已还但资产还显示已借出」的不一致。

### 10.3 后台：资产台账（4-08 ~ 4-14）

#### 10.3.1 资产分页（后台，4-08）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-08 |
| 接口名称 | 资产分页（后台） |
| Path | `/admin/asset/page` |
| Method | `GET` |
| 接口描述 | 管理端分页查询资产台账，支持更多筛选维度 |
| 权限码 | `asset:info:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| categoryId | integer | 非必须 | - | 分类筛选（含子分类） | format: int64 |
| status | integer | 非必须 | - | 资产状态 | format: int32 |
| sourceType | integer | 非必须 | - | 1 采购，2 捐赠，3 调拨 | format: int32 |
| keeperId | integer | 非必须 | - | 保管人筛选 | format: int64 |
| keyword | string | 非必须 | - | 模糊匹配 assetNo / name / brand / model | - |
| beginDate | string | 非必须 | - | 购置日期起 | - |
| endDate | string | 非必须 | - | 购置日期止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| assetId | integer | 必须 | - | 资产 ID | format: int64 |
| assetNo | string | 必须 | - | 资产编号 | - |
| name | string | 必须 | - | 资产名称 | - |
| categoryId | integer | 非必须 | - | 分类 ID | format: int64 |
| categoryName | string | 非必须 | - | 分类名称 | - |
| brand | string | 非必须 | - | 品牌 | - |
| model | string | 非必须 | - | 型号 | - |
| price | number | 必须 | 0.00 | 购置价格 | - |
| purchaseDate | string | 非必须 | - | 购置日期 | - |
| sourceType | integer | 必须 | - | 1 采购，2 捐赠，3 调拨 | format: int32 |
| status | integer | 必须 | - | 资产状态 | format: int32 |
| keeperId | integer | 非必须 | - | 保管人 ID | format: int64 |
| keeperName | string | 非必须 | - | 保管人姓名 | - |
| location | string | 非必须 | - | 存放位置 | - |
| budgetExpenseId | integer | 非必须 | - | 关联的预算支出 ID（采购入账来源） | format: int64 |
| createTime | string | 必须 | - | 创建时间 | - |

#### 10.3.2 资产详情（后台，4-09）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-09 |
| 接口名称 | 资产详情（后台） |
| Path | `/admin/asset/{id}` |
| Method | `GET` |
| 接口描述 | 查询资产完整信息、变动流水与借用历史，用于编辑回显与追溯 |
| 权限码 | `asset:info:query` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 资产 ID | 25 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| assetId | integer | 必须 | - | 资产 ID | format: int64 |
| assetNo | string | 必须 | - | 资产编号 | - |
| name | string | 必须 | - | 资产名称 | - |
| categoryId | integer | 非必须 | - | 分类 ID | format: int64 |
| categoryName | string | 非必须 | - | 分类名称 | - |
| brand | string | 非必须 | - | 品牌 | - |
| model | string | 非必须 | - | 型号 | - |
| price | number | 必须 | 0.00 | 购置价格 | - |
| purchaseDate | string | 非必须 | - | 购置日期 | - |
| supplier | string | 非必须 | - | 供应商 | - |
| sourceType | integer | 必须 | - | 1 采购，2 捐赠，3 调拨 | format: int32 |
| budgetExpenseId | integer | 非必须 | - | 关联的预算支出 ID（采购入账来源） | format: int64 |
| status | integer | 必须 | - | 1 在用，2 闲置，3 维修中，4 已借出，5 已报废 | format: int32 |
| keeperId | integer | 非必须 | - | 保管人 ID | format: int64 |
| keeperName | string | 非必须 | - | 保管人姓名 | - |
| location | string | 非必须 | - | 存放位置 | - |
| imageUrls | array | 非必须 | [] | 资产照片地址数组（OSS） | - |
| warrantyEndDate | string | 非必须 | - | 保修截止日期 | - |
| remark | string | 非必须 | - | 备注 | - |
| records | array | 非必须 | [] | 全部变动流水，元素含 `recordId`、`recordType`、`fromUserName`、`toUserName`、`fromLocation`、`toLocation`、`recordTime`、`operatorName`、`remark` | - |
| borrowHistory | array | 非必须 | [] | 历史借用记录，元素含 `borrowId`、`userName`、`borrowTime`、`actualReturnTime`、`status` | - |
| createTime | string | 必须 | - | 创建时间 | - |

> 相比成员端的 4-02（接口 `4-02`），本接口额外返回 `budgetExpenseId`（采购入账来源）、`records`（全部变动流水，不限最近 10 条）与 `borrowHistory`（历史借用记录）。

**错误码**：40001 资产不存在。

#### 10.3.3 新增资产（4-10）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-10 |
| 接口名称 | 新增资产 |
| Path | `/admin/asset` |
| Method | `POST` |
| 接口描述 | 台账入账。若资产来自预算采购，须填写 `budgetExpenseId` 形成「采购 → 入账」闭环 |
| 权限码 | `asset:info:add` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| assetNo | string | 必须 | - | 资产编号，全局唯一 | 建议按 `类别缩写-年份-序号` 规则编号 |
| name | string | 必须 | - | 资产名称，最长 100 字 | - |
| categoryId | integer | 非必须 | - | 资产分类 ID | format: int64 |
| brand | string | 非必须 | - | 品牌 | - |
| model | string | 非必须 | - | 型号 | - |
| price | number | 非必须 | 0.00 | 购置价格 | - |
| purchaseDate | string | 非必须 | - | 购置日期，格式 yyyy-MM-dd | - |
| supplier | string | 非必须 | - | 供应商 | - |
| sourceType | integer | 非必须 | 1 | 1 采购，2 捐赠，3 调拨 | format: int32 |
| budgetExpenseId | integer | 非必须 | - | 关联的预算支出 ID；来源为采购时建议填写 | format: int64 |
| keeperId | integer | 非必须 | - | 保管人 ID | format: int64 |
| location | string | 非必须 | - | 存放位置 | - |
| imageUrls | array | 非必须 | [] | 资产照片地址数组，先调接口 8-01 上传后填入 | - |
| warrantyEndDate | string | 非必须 | - | 保修截止日期 | - |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| assetId | integer | 必须 | - | 新增资产 ID | format: int64 |

**错误码**：40002 资产编号已存在；40003 资产分类不存在。

#### 10.3.4 修改资产（4-11）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-11 |
| 接口名称 | 修改资产 |
| Path | `/admin/asset` |
| Method | `PUT` |
| 接口描述 | 修改资产基础信息；状态变更请用接口 `4-13`，保管人与位置分配请用接口 `4-14` |
| 权限码 | `asset:info:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| assetId | integer | 必须 | - | 资产 ID | format: int64 |
| assetNo | string | 非必须 | - | 资产编号，全局唯一；不传表示不变更 | - |
| name | string | 非必须 | - | 资产名称，最长 100 字；不传表示不变更 | - |
| categoryId | integer | 非必须 | - | 资产分类 ID | format: int64 |
| brand | string | 非必须 | - | 品牌 | - |
| model | string | 非必须 | - | 型号 | - |
| price | number | 非必须 | 0.00 | 购置价格 | - |
| purchaseDate | string | 非必须 | - | 购置日期 | - |
| supplier | string | 非必须 | - | 供应商 | - |
| sourceType | integer | 非必须 | 1 | 1 采购，2 捐赠，3 调拨 | format: int32 |
| budgetExpenseId | integer | 非必须 | - | 关联的预算支出 ID | format: int64 |
| warrantyEndDate | string | 非必须 | - | 保修截止日期 | - |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**：`data` 为 `null`。

**错误码**：40001 资产不存在；40002 资产编号已存在；40003 资产分类不存在。

> **部分更新**：只更新请求中出现的字段，未传字段保持原值；`assetNo` / `name` 如传入则不能为空字符串。本接口**不接收** `status`、`keeperId`、`location` 字段，状态变更与人员位置调整有独立的接口（`4-13`、`4-14`）。

#### 10.3.5 删除资产（4-12）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-12 |
| 接口名称 | 删除资产 |
| Path | `/admin/asset/{id}` |
| Method | `DELETE` |
| 接口描述 | 逻辑删除资产；存在未归还借用记录时禁止删除 |
| 权限码 | `asset:info:remove` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 资产 ID | 25 |

**返回数据**：`data` 为 `null`。

**错误码**：40001 资产不存在；40007 该资产当前已被借出；40020 该资产存在未完成的借用/领用记录。

> **报废请用状态变更**：资产损坏或淘汰应调用 `4-13` 把状态置为「已报废」并登记流水，而非删除。删除只用于「录入错误」的场景。

#### 10.3.6 资产状态变更（4-13）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-13 |
| 接口名称 | 资产状态变更 |
| Path | `/admin/asset/{id}/status` |
| Method | `PUT` |
| 接口描述 | 变更资产状态并自动写入一条变动流水 |
| 权限码 | `asset:info:edit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 资产 ID | 25 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 必须 | - | 目标状态：2 闲置，3 维修中，5 已报废 | format: int32 |
| remark | string | 非必须 | - | 变更原因，如「风扇异响送修」 | - |

**返回数据**：`data` 为 `null`。

**错误码**：40001 资产不存在；40006 资产状态不允许该操作。

**状态变更规则**

| 目标状态 | 允许的来源状态 | 自动写入的流水类型 |
| --- | --- | --- |
| 2 闲置 | 1 在用、3 维修中 | 6 调拨 |
| 3 维修中 | 1 在用、2 闲置 | 4 维修 |
| 5 已报废 | 1 在用、2 闲置、3 维修中 | 5 报废 |
| 1 在用 | 3 维修中、2 闲置（维修完成或重新启用） | 4 维修 |

> **「已借出」不能手动设置**：状态 4 由借用审核通过自动置位，归还时自动复位，不接受手工变更，避免与 `asset_borrow` 记录脱节。

#### 10.3.7 分配保管人与存放位置（4-14）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-14 |
| 接口名称 | 分配保管人与存放位置 |
| Path | `/admin/asset/{id}/assign` |
| Method | `PUT` |
| 接口描述 | 指定资产的保管人与存放位置，并写入一条「调拨」流水 |
| 权限码 | `asset:info:assign` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 资产 ID | 25 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| keeperId | integer | 非必须 | - | 保管人 ID；不传表示不变更 | format: int64 |
| location | string | 非必须 | - | 存放位置描述，最长 128 字 | - |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**：`data` 为 `null`。

**错误码**：40001 资产不存在。

> **自动留痕**：服务端会比对变更前后的保管人与位置，只要任一发生变化就写入一条 `record_type = 6 调拨` 的流水，记录 `from_user_id` / `to_user_id` 与 `from_location` / `to_location`。这样资产的每一次转手都有据可查，无需管理员手动登记。

> **状态说明**：资产处于「已借出」状态时仍允许调整保管人与位置（例如领用后转交或换工位），本接口只改保管人与位置，不改变 `asset_info.status`。

### 10.4 后台：变动流水（4-15 ~ 4-16）

#### 10.4.1 变动记录登记（4-15）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-15 |
| 接口名称 | 变动记录登记 |
| Path | `/admin/asset/record` |
| Method | `POST` |
| 接口描述 | 手动登记一条资产变动流水，用于系统未自动覆盖的场景（如外部调拨、遗留资产补录） |
| 权限码 | `asset:record:add` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| assetId | integer | 必须 | - | 资产 ID | format: int64 |
| recordType | integer | 必须 | - | 1 领用，2 借用，3 归还，4 维修，5 报废，6 调拨，7 盘点 | format: int32 |
| fromUserId | integer | 非必须 | - | 变动前使用人 ID | format: int64 |
| toUserId | integer | 非必须 | - | 变动后使用人 ID | format: int64 |
| fromLocation | string | 非必须 | - | 变动前存放位置 | - |
| toLocation | string | 非必须 | - | 变动后存放位置 | - |
| recordTime | string | 非必须 | 当前时间 | 变动时间，格式 yyyy-MM-dd HH:mm:ss | - |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| recordId | integer | 必须 | - | 流水记录 ID | format: int64 |

**错误码**：40001 资产不存在。

> **优先使用自动流水**：归还（`4-07` / `4-22`）、借用审核（`4-21`）、状态变更（`4-13`）、保管人分配（`4-14`）都会自动写入流水。本接口仅用于补录历史数据或登记特殊变动（如资产外借给其他部门）。

#### 10.4.2 变动流水分页（4-16）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-16 |
| 接口名称 | 变动流水分页 |
| Path | `/admin/asset/record/page` |
| Method | `GET` |
| 接口描述 | 分页查询资产变动流水，用于资产去向追溯 |
| 权限码 | `asset:record:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| assetId | integer | 非必须 | - | 限定某个资产 | format: int64 |
| recordType | integer | 非必须 | - | 变动类型筛选 | format: int32 |
| operatorId | integer | 非必须 | - | 限定操作人 | format: int64 |
| keyword | string | 非必须 | - | 模糊匹配资产编号 / 资产名称 | - |
| beginTime | string | 非必须 | - | 变动时间起 | - |
| endTime | string | 非必须 | - | 变动时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| recordId | integer | 必须 | - | 流水记录 ID | format: int64 |
| assetId | integer | 必须 | - | 资产 ID | format: int64 |
| assetNo | string | 必须 | - | 资产编号 | - |
| assetName | string | 必须 | - | 资产名称 | - |
| recordType | integer | 必须 | - | 1 领用，2 借用，3 归还，4 维修，5 报废，6 调拨，7 盘点 | format: int32 |
| fromUserId | integer | 非必须 | - | 变动前使用人 ID | format: int64 |
| fromUserName | string | 非必须 | - | 变动前使用人姓名 | - |
| toUserId | integer | 非必须 | - | 变动后使用人 ID | format: int64 |
| toUserName | string | 非必须 | - | 变动后使用人姓名 | - |
| fromLocation | string | 非必须 | - | 变动前存放位置 | - |
| toLocation | string | 非必须 | - | 变动后存放位置 | - |
| recordTime | string | 必须 | - | 变动时间 | - |
| operatorId | integer | 必须 | - | 操作人 ID | format: int64 |
| operatorName | string | 非必须 | - | 操作人姓名 | - |
| remark | string | 非必须 | - | 备注 | - |

### 10.5 后台：分类管理（4-17 ~ 4-19）

#### 10.5.1 新增资产分类（4-17）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-17 |
| 接口名称 | 新增资产分类 |
| Path | `/admin/asset/category` |
| Method | `POST` |
| 接口描述 | 新增资产分类，支持二级分类（`parentId` 指向父分类） |
| 权限码 | `asset:category:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| name | string | 必须 | - | 分类名称，最长 64 字 | - |
| parentId | integer | 非必须 | 0 | 父分类 ID，0 表示一级分类 | format: int64 |
| code | string | 非必须 | - | 分类编码，全局唯一 | - |
| sortNo | integer | 非必须 | 0 | 排序号，升序 | format: int32 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| categoryId | integer | 必须 | - | 新增分类 ID | format: int64 |

**错误码**：40003 资产分类不存在（`parentId` 指向的父分类不存在）。

#### 10.5.2 修改资产分类（4-18）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-18 |
| 接口名称 | 修改资产分类 |
| Path | `/admin/asset/category` |
| Method | `PUT` |
| 接口描述 | 修改分类名称、父分类与排序 |
| 权限码 | `asset:category:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| categoryId | integer | 必须 | - | 分类 ID | format: int64 |
| name | string | 必须 | - | 分类名称，最长 64 字 | - |
| parentId | integer | 非必须 | 0 | 父分类 ID，0 表示一级分类 | format: int64 |
| code | string | 非必须 | - | 分类编码 | - |
| sortNo | integer | 非必须 | 0 | 排序号 | format: int32 |

**返回数据**：`data` 为 `null`。

**错误码**：40003 资产分类不存在。

> **不允许把分类挂到自己或自己的子分类下**（会造成环），服务端会校验并拒绝，返回 `40003`。

#### 10.5.3 删除资产分类（4-19）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-19 |
| 接口名称 | 删除资产分类 |
| Path | `/admin/asset/category/{id}` |
| Method | `DELETE` |
| 接口描述 | 逻辑删除分类；存在子分类或已被资产引用时禁止删除 |
| 权限码 | `asset:category:edit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 分类 ID | 4 |

**返回数据**：`data` 为 `null`。

**错误码**：40003 资产分类不存在；40004 该分类下存在资产，无法删除；40005 该分类存在子分类，无法删除。

### 10.6 后台：借用审核与归还（4-20 ~ 4-22）

#### 10.6.1 借用审核分页（4-20）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-20 |
| 接口名称 | 借用审核分页 |
| Path | `/admin/asset/borrow/page` |
| Method | `GET` |
| 接口描述 | 管理端分页查询借用 / 领用申请，用于审核排队与在借跟踪 |
| 权限码 | `asset:borrow:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 非必须 | - | 0 待审核，1 已通过，2 已驳回，3 已归还，4 已逾期 | format: int32 |
| borrowType | integer | 非必须 | - | 1 借用，2 领用 | format: int32 |
| userId | integer | 非必须 | - | 限定借用人 | format: int64 |
| assetId | integer | 非必须 | - | 限定资产 | format: int64 |
| keyword | string | 非必须 | - | 模糊匹配资产编号 / 资产名称 / 借用人姓名 | - |
| beginTime | string | 非必须 | - | 申请时间起 | - |
| endTime | string | 非必须 | - | 申请时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| borrowId | integer | 必须 | - | 借用记录 ID | format: int64 |
| assetId | integer | 必须 | - | 资产 ID | format: int64 |
| assetNo | string | 必须 | - | 资产编号 | - |
| assetName | string | 必须 | - | 资产名称 | - |
| borrowType | integer | 必须 | - | 1 借用，2 领用 | format: int32 |
| userId | integer | 必须 | - | 借用人 ID | format: int64 |
| userName | string | 必须 | - | 借用人姓名 | - |
| grade | string | 非必须 | - | 年级 | - |
| purpose | string | 非必须 | - | 用途说明 | - |
| borrowTime | string | 必须 | - | 借用时间 | - |
| expectReturnTime | string | 非必须 | - | 预计归还时间 | - |
| actualReturnTime | string | 非必须 | - | 实际归还时间 | - |
| status | integer | 必须 | - | 0 待审核，1 已通过，2 已驳回，3 已归还，4 已逾期 | format: int32 |
| auditUserId | integer | 非必须 | - | 审核人 ID | format: int64 |
| auditUserName | string | 非必须 | - | 审核人姓名 | - |
| auditTime | string | 非必须 | - | 审核时间 | - |
| auditRemark | string | 非必须 | - | 审核意见 | - |

> **逾期状态由定时任务维护**：每日扫描 `expect_return_time < 当前时间` 且仍未归还的记录，自动置为「4 已逾期」。

#### 10.6.2 借用审核（4-21）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-21 |
| 接口名称 | 借用审核 |
| Path | `/admin/asset/borrow/{id}/audit` |
| Method | `PUT` |
| 接口描述 | 审核借用 / 领用申请；通过时把资产状态置为「已借出」 |
| 权限码 | `asset:borrow:audit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 借用记录 ID | 48 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| auditStatus | integer | 必须 | - | 1 通过，2 驳回 | format: int32 |
| auditRemark | string | 非必须 | - | 审核意见，驳回时建议填写 | - |

**返回数据**：`data` 为 `null`。

**错误码**：40008 借用记录不存在；40006 资产状态不允许该操作；40017 该借用记录状态不允许审核。

> **通过审核时同事务做三件事**：更新 `asset_borrow.status = 1` → 把 `asset_info.status` 置为「4 已借出」→ 写入一条流水：`borrow_type = 1 借用` 时 `record_type = 2 借用`，`borrow_type = 2 领用` 时 `record_type = 1 领用`。领用不进入归还流程，资产状态保持「已借出」以表示已发放。
>
> **并发保护**：若资产已被另一次审核置为「已借出」，本次审核返回 `40006`，避免同一资产被重复借出。

#### 10.6.3 归还登记（后台，4-22）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-22 |
| 接口名称 | 归还登记（后台） |
| Path | `/admin/asset/borrow/{id}/return` |
| Method | `PUT` |
| 接口描述 | 管理员代为登记归还，适用于成员未在端上操作或资产由管理员收回的场景 |
| 权限码 | `asset:borrow:edit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 借用记录 ID | 48 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| remark | string | 非必须 | - | 归还备注 | - |

**返回数据**：`data` 为 `null`。

**错误码**：40008 借用记录不存在；40010 该借用记录状态不允许归还；40011 领用记录无需归还。

### 10.7 后台：盘点（4-23 ~ 4-26）

#### 10.7.1 发起盘点（4-23）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-23 |
| 接口名称 | 发起盘点 |
| Path | `/admin/asset/inventory` |
| Method | `POST` |
| 接口描述 | 创建盘点任务，系统自动为待盘点资产生成明细行 |
| 权限码 | `asset:inventory:add` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| title | string | 必须 | - | 盘点任务标题，最长 100 字 | - |
| categoryId | integer | 非必须 | - | 限定盘点某分类下的资产；不传表示全部资产 | format: int64 |
| excludeScrapped | integer | 非必须 | 1 | 是否排除已报废资产：0 不排除 1 排除 | format: int32 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| inventoryId | integer | 必须 | - | 盘点任务 ID | format: int64 |
| totalCount | integer | 必须 | - | 生成的待盘点明细数量 | format: int32 |

**错误码**：40003 资产分类不存在。

> **本期不允许并存多个进行中的盘点任务**：若已有 `status = 0 进行中` 的任务，服务端返回 `40013` 提示先结束上一轮，避免同一资产被两个盘点任务重复覆盖。

#### 10.7.2 盘点任务分页（4-24）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-24 |
| 接口名称 | 盘点任务分页 |
| Path | `/admin/asset/inventory/page` |
| Method | `GET` |
| 接口描述 | 分页查询盘点任务及完成进度 |
| 权限码 | `asset:inventory:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 非必须 | - | 0 进行中，1 已完成 | format: int32 |
| keyword | string | 非必须 | - | 模糊匹配任务标题 | - |
| beginDate | string | 非必须 | - | 开始日期起 | - |
| endDate | string | 非必须 | - | 开始日期止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| inventoryId | integer | 必须 | - | 盘点任务 ID | format: int64 |
| title | string | 必须 | - | 任务标题 | - |
| beginTime | string | 必须 | - | 开始时间 | - |
| endTime | string | 非必须 | - | 结束时间 | - |
| status | integer | 必须 | - | 0 进行中，1 已完成 | format: int32 |
| totalCount | integer | 必须 | 0 | 应盘点总数 | format: int32 |
| checkedCount | integer | 必须 | 0 | 已盘点数量（`checkStatus ≠ 0` 的明细数） | format: int32 |
| normalCount | integer | 必须 | 0 | 正常数量 | format: int32 |
| abnormalCount | integer | 必须 | 0 | 异常数量（盘亏 + 位置不符） | format: int32 |
| progressRate | number | 必须 | 0.00 | 盘点进度（百分比数值） | - |

#### 10.7.3 盘点明细登记（4-25）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-25 |
| 接口名称 | 盘点明细登记 |
| Path | `/admin/asset/inventory/item/{id}` |
| Method | `PUT` |
| 接口描述 | 逐项登记盘点结果，可单条登记或批量提交 |
| 权限码 | `asset:inventory:exec` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 盘点明细 ID | 312 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| checkStatus | integer | 必须 | - | 盘点结果：1 正常，2 盘亏，3 位置不符 | format: int32 |
| remark | string | 非必须 | - | 差异说明，结果非「正常」时建议填写 | - |

**返回数据**：`data` 为 `null`。

**错误码**：40014 盘点明细不存在；40013 该盘点任务已结束。

#### 10.7.4 结束盘点（生成差异，4-26）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-26 |
| 接口名称 | 结束盘点 |
| Path | `/admin/asset/inventory/{id}/finish` |
| Method | `PUT` |
| 接口描述 | 结束盘点任务，汇总统计并生成差异结果 |
| 权限码 | `asset:inventory:exec` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 盘点任务 ID | 5 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| updateAssetStatus | integer | 非必须 | 0 | 「盘亏」明细是否同步把资产状态改为「已报废」：0 否 1 是 | format: int32 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| inventoryId | integer | 必须 | - | 盘点任务 ID | format: int64 |
| totalCount | integer | 必须 | - | 应盘点总数 | format: int32 |
| normalCount | integer | 必须 | - | 正常数量 | format: int32 |
| lossCount | integer | 必须 | - | 盘亏数量 | format: int32 |
| mismatchCount | integer | 必须 | - | 位置不符数量 | format: int32 |
| uncheckedCount | integer | 必须 | - | 未盘点数量 | format: int32 |
| abnormalList | array | 非必须 | [] | 异常明细列表，元素含 `assetId`、`assetNo`、`assetName`、`checkStatus`、`remark` | - |

**错误码**：40012 盘点任务不存在；40013 该盘点任务已结束。

> **结束时的汇总与留痕**：回填 `asset_inventory` 的 `total_count` / `normal_count` / `abnormal_count`，并把 `status` 置为「已完成」、写入 `end_time`；同时为每条异常明细写入一条 `record_type = 7 盘点` 的资产流水。
>
> **未盘点的处理**：`uncheckedCount > 0` 时仍允许结束，但返回值会明确给出数量，前端应弹出确认提示，避免「漏盘」被无声略过。

### 10.8 后台：台账导出（4-27）

#### 10.8.1 资产台账导出（4-27）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 4-27 |
| 接口名称 | 资产台账导出 |
| Path | `/admin/asset/export` |
| Method | `GET` |
| 接口描述 | 按筛选条件导出资产台账为 Excel 文件，用于线下归档与上报 |
| 权限码 | `asset:info:export` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| categoryId | integer | 非必须 | - | 分类筛选（含子分类） | format: int64 |
| status | integer | 非必须 | - | 资产状态筛选 | format: int32 |
| sourceType | integer | 非必须 | - | 1 采购，2 捐赠，3 调拨 | format: int32 |
| keyword | string | 非必须 | - | 模糊匹配 assetNo / name / brand / model | - |
| beginDate | string | 非必须 | - | 购置日期起 | - |
| endDate | string | 非必须 | - | 购置日期止 | - |

**返回数据**

本接口返回**文件流**（`Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`），不套用统一响应结构。响应头中带：

| 响应头 | 说明 |
| --- | --- |
| Content-Disposition | `attachment; filename="资产台账_20260923.xlsx"`（文件名已 URL 编码） |

导出列：资产编号、资产名称、分类、品牌、型号、购置价格、购置日期、供应商、来源、状态、保管人、存放位置、关联预算支出、备注。

**错误码**：40016 导出数据量超出上限。

> **导出上限保护**：单次导出最多 **10000 行**（可用配置项 `asset.export.max-rows` 调整）。超出时返回 `40016`，提示用户增加筛选条件后分批导出。这是为了避免大结果集一次性加载导致后端内存溢出。
>
> **前端处理**：导出接口返回文件流而非 JSON，前端需用 `responseType: 'blob'` 发起请求，并从 `Content-Disposition` 解析文件名。

---

## 第 11 章 模块五：部门工位管理（seat）

### 11.1 模块概述

- **职责**：区域与平面图底图维护、工位坐标与状态管理、工位申请与分配、管理端平面图拖拽调整、成员间座位调换。
- **数据模型**：见 5.5 节。
- **接口清单**：见 4.6 节（`5-01` ~ `5-26`，共 26 个接口）。
- **已确定口径**：
  1. 负责人可**直接分配**工位（接口 5-15）；
  2. 成员间调换座位需「对方确认 + 负责人审核」两步，任一步拒绝即终止并保留申请记录；
  3. 平面图按「上传底图 + 工位相对坐标（百分比）」实现，前端按比例渲染；
  4. 管理端平面图可直接**拖拽调整**：拖到空工位即移动、拖到已占用工位即互换，即时生效、无需审核（接口 `5-25`、`5-26`）；
  5. 平面图分「布局」与「座位」两种模式：布局改坐标（`5-13`），座位改使用人（`5-26`），互不干扰。

### 11.2 前台：平面图与工位查询（5-01 ~ 5-03）

#### 11.2.1 平面图数据（5-01）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-01 |
| 接口名称 | 平面图数据 |
| Path | `/user/seat/plan` |
| Method | `GET` |
| 接口描述 | 一次返回区域、底图尺寸与全部工位坐标及状态，供前端渲染可交互平面图 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| areaId | integer | 非必须 | - | 指定区域 ID；不传表示返回全部启用的区域 | format: int64 |

**返回数据**

`data` 为数组，元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| areaId | integer | 必须 | - | 区域 ID | format: int64 |
| name | string | 必须 | - | 区域名称 | - |
| planImageUrl | string | 非必须 | - | 平面图底图地址（OSS） | - |
| width | integer | 必须 | 0 | 底图宽度（像素），坐标换算基准 | format: int32 |
| height | integer | 必须 | 0 | 底图高度（像素），坐标换算基准 | format: int32 |
| seats | array | 必须 | [] | 该区域下的工位数组，结构见下表 | - |

`seats` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| seatId | integer | 必须 | - | 工位 ID | format: int64 |
| seatNo | string | 必须 | - | 工位编号，如 A-01 | - |
| seatType | integer | 必须 | - | 1 工位，2 会议室，3 储物柜，4 其他 | format: int32 |
| xRatio | number | 必须 | 0 | X 坐标占底图宽度的百分比（0~100） | - |
| yRatio | number | 必须 | 0 | Y 坐标占底图高度的百分比（0~100） | - |
| widthRatio | number | 必须 | 0 | 宽度占底图宽度的百分比 | - |
| heightRatio | number | 必须 | 0 | 高度占底图高度的百分比 | - |
| rotation | number | 非必须 | 0 | 旋转角度（度） | - |
| status | integer | 必须 | - | 1 空闲，2 已占用，3 预留，4 维修中 | format: int32 |
| occupantName | string | 非必须 | - | 使用人姓名；空闲时为 null | - |
| isMine | integer | 必须 | 0 | 是否当前用户使用：0 否 1 是 | format: int32 |

> **坐标一律为百分比**：前端渲染时按 `left = xRatio% × 容器宽` 定位，因此底图更换分辨率、浏览器缩放都不需要后端改数据。
>
> **隐私保护**：只返回使用人姓名，不返回学号、手机号等联系方式。
>
> **管理端另有全量视图**：管理端渲染可拖拽平面图请用接口 `5-25`，该接口额外返回使用人 ID、年级、专业、分配时间与工位备注。

#### 11.2.2 工位列表（5-02）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-02 |
| 接口名称 | 工位列表 |
| Path | `/user/seat/list` |
| Method | `GET` |
| 接口描述 | 以列表形式返回工位信息，供不支持平面图的场景（如移动端）或搜索使用 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| areaId | integer | 非必须 | - | 区域筛选 | format: int64 |
| seatType | integer | 非必须 | - | 工位类型筛选 | format: int32 |
| status | integer | 非必须 | - | 状态筛选 | format: int32 |
| keyword | string | 非必须 | - | 模糊匹配 seatNo | - |

**返回数据**

`data` 为数组，元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| seatId | integer | 必须 | - | 工位 ID | format: int64 |
| areaId | integer | 必须 | - | 所属区域 ID | format: int64 |
| areaName | string | 必须 | - | 区域名称 | - |
| seatNo | string | 必须 | - | 工位编号 | - |
| seatType | integer | 必须 | - | 1 工位，2 会议室，3 储物柜，4 其他 | format: int32 |
| status | integer | 必须 | - | 1 空闲，2 已占用，3 预留，4 维修中 | format: int32 |
| occupantName | string | 非必须 | - | 使用人姓名 | - |
| assignTime | string | 非必须 | - | 分配时间 | - |

#### 11.2.3 我的工位（5-03）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-03 |
| 接口名称 | 我的工位 |
| Path | `/user/seat/my` |
| Method | `GET` |
| 接口描述 | 查询当前登录用户的工位；未分配时返回 null |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**：无。

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| seatId | integer | 非必须 | null | 工位 ID；未分配时为 null | format: int64 |
| seatNo | string | 非必须 | - | 工位编号 | - |
| seatType | integer | 非必须 | - | 工位类型 | format: int32 |
| areaId | integer | 非必须 | - | 区域 ID | format: int64 |
| areaName | string | 非必须 | - | 区域名称 | - |
| assignTime | string | 非必须 | - | 分配时间 | - |
| xRatio | number | 非必须 | - | X 坐标百分比，便于前端在平面图上高亮 | - |
| yRatio | number | 非必须 | - | Y 坐标百分比 | - |

> 整个 `data` 可能为 `null`（该成员尚未分配工位），前端需做空值处理，不要直接取字段。

### 11.3 前台：工位申请（5-04 ~ 5-06）

#### 11.3.1 提交工位申请（5-04）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-04 |
| 接口名称 | 提交工位申请 |
| Path | `/user/seat/apply` |
| Method | `POST` |
| 接口描述 | 成员申请工位；可指定具体工位，也可只填意向区域 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| areaId | integer | 必须 | - | 意向区域 ID | format: int64 |
| seatId | integer | 非必须 | - | 意向工位 ID；不传表示仅申请意向区域，由负责人分配 | format: int64 |
| reason | string | 非必须 | - | 申请理由，最长 500 字 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| applyId | integer | 必须 | - | 工位申请 ID | format: int64 |

**错误码**：50001 工位区域不存在；50003 工位不存在；50005 该工位已被占用。

> **同一人只允许一份「待审核」申请**：已有待审核申请时重复提交会被拒绝（`50008`），避免审核队列出现同一个人的多条申请；需要改意向时先撤销（`5-06`）再重新提交。
>
> **已有工位也可再申请**：本接口不限制申请人是否已有工位。若已有工位又申请到新工位，审核通过时原工位自动释放，无需先手动退出。

#### 11.3.2 我的工位申请分页（5-05）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-05 |
| 接口名称 | 我的工位申请分页 |
| Path | `/user/seat/apply/my/page` |
| Method | `GET` |
| 接口描述 | 分页查询当前登录用户提交的工位申请 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 非必须 | - | 0 待审核，1 已通过，2 已驳回，3 已撤销 | format: int32 |
| beginTime | string | 非必须 | - | 申请时间起 | - |
| endTime | string | 非必须 | - | 申请时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；仅返回 `userId = 当前用户` 的记录。`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| applyId | integer | 必须 | - | 工位申请 ID | format: int64 |
| areaId | integer | 必须 | - | 意向区域 ID | format: int64 |
| areaName | string | 必须 | - | 区域名称 | - |
| seatId | integer | 非必须 | - | 意向工位 ID；为空表示仅意向区域 | format: int64 |
| seatNo | string | 非必须 | - | 意向工位编号 | - |
| reason | string | 非必须 | - | 申请理由 | - |
| status | integer | 必须 | - | 0 待审核，1 已通过，2 已驳回，3 已撤销 | format: int32 |
| auditUserName | string | 非必须 | - | 审核人姓名 | - |
| auditTime | string | 非必须 | - | 审核时间 | - |
| auditRemark | string | 非必须 | - | 审核意见 | - |
| createTime | string | 必须 | - | 申请时间 | - |

#### 11.3.3 撤销工位申请（5-06）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-06 |
| 接口名称 | 撤销工位申请 |
| Path | `/user/seat/apply/{id}/cancel` |
| Method | `PUT` |
| 接口描述 | 撤销尚未审核的工位申请 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 工位申请 ID | 22 |

**返回数据**：`data` 为 `null`。

**错误码**：50006 工位申请不存在；50007 无权访问该工位申请；50008 当前状态不允许该操作。

### 11.4 后台：区域管理（5-07 ~ 5-10）

#### 11.4.1 区域列表（后台，5-07）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-07 |
| 接口名称 | 区域列表（后台） |
| Path | `/admin/seat/area/list` |
| Method | `GET` |
| 接口描述 | 查询全部区域（含未启用），用于工位表单的区域选择与布局编辑器区域切换 |
| 权限码 | `seat:area:list` |
| 是否需令牌 | 是 |

**请求参数**：无。

**返回数据**

`data` 为数组，按 `sortNo` 升序；元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| areaId | integer | 必须 | - | 区域 ID | format: int64 |
| name | string | 必须 | - | 区域名称 | - |
| planImageUrl | string | 非必须 | - | 平面图底图地址（OSS） | - |
| width | integer | 必须 | 0 | 底图宽度（像素） | format: int32 |
| height | integer | 必须 | 0 | 底图高度（像素） | format: int32 |
| description | string | 非必须 | - | 区域说明 | - |
| seatCount | integer | 必须 | 0 | 该区域下的工位数量 | format: int32 |
| sortNo | integer | 必须 | 0 | 排序号 | format: int32 |

#### 11.4.2 新增区域（上传平面图，5-08）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-08 |
| 接口名称 | 新增区域（上传平面图） |
| Path | `/admin/seat/area` |
| Method | `POST` |
| 接口描述 | 新增区域并关联平面图底图；`width` / `height` 是工位坐标换算的基准，必须填写 |
| 权限码 | `seat:area:add` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| name | string | 必须 | - | 区域名称，最长 64 字 | - |
| planImageUrl | string | 非必须 | - | 平面图底图地址，先调接口 8-01 上传（`bizType = seat`）后填入 | - |
| width | integer | 非必须 | 0 | 底图宽度（像素） | format: int32 |
| height | integer | 非必须 | 0 | 底图高度（像素） | format: int32 |
| description | string | 非必须 | - | 区域说明 | - |
| sortNo | integer | 非必须 | 0 | 排序号，升序 | format: int32 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| areaId | integer | 必须 | - | 新增区域 ID | format: int64 |

> **`width` / `height` 必须与底图实际像素一致**：前端在校验时会用该尺寸把百分比坐标换算成像素定位；若尺寸填错，平面图上的工位会整体错位。建议前端读取图片后自动填充这两个值供管理员确认。

#### 11.4.3 修改区域（5-09）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-09 |
| 接口名称 | 修改区域 |
| Path | `/admin/seat/area` |
| Method | `PUT` |
| 接口描述 | 修改区域信息或更换底图；更换底图且尺寸变化时需同步复核工位坐标 |
| 权限码 | `seat:area:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| areaId | integer | 必须 | - | 区域 ID | format: int64 |
| name | string | 必须 | - | 区域名称，最长 64 字 | - |
| planImageUrl | string | 非必须 | - | 平面图底图地址 | - |
| width | integer | 非必须 | 0 | 底图宽度（像素） | format: int32 |
| height | integer | 非必须 | 0 | 底图高度（像素） | format: int32 |
| description | string | 非必须 | - | 区域说明 | - |
| sortNo | integer | 非必须 | 0 | 排序号 | format: int32 |

**返回数据**：`data` 为 `null`。

**错误码**：50001 工位区域不存在；50014 平面图底图尺寸未设置。

> **换底图的正确姿势**：先修改底图与尺寸，再用接口 `5-13` 批量重排工位坐标。若底图尺寸变化而工位坐标仍是按旧尺寸标注的，平面图会错位——因为坐标存的是百分比而非像素，**尺寸变化会直接改变实际位置**。

#### 11.4.4 删除区域（5-10）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-10 |
| 接口名称 | 删除区域 |
| Path | `/admin/seat/area/{id}` |
| Method | `DELETE` |
| 接口描述 | 逻辑删除区域；区域下存在工位时禁止删除 |
| 权限码 | `seat:area:remove` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 区域 ID | 1 |

**返回数据**：`data` 为 `null`。

**错误码**：50001 工位区域不存在；50002 该区域下存在工位，无法删除。

### 11.5 后台：工位管理（5-11 ~ 5-17）

#### 11.5.1 新增工位（5-11）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-11 |
| 接口名称 | 新增工位 |
| Path | `/admin/seat` |
| Method | `POST` |
| 接口描述 | 在指定区域内新增工位并标注坐标 |
| 权限码 | `seat:seat:add` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| areaId | integer | 必须 | - | 所属区域 ID | format: int64 |
| seatNo | string | 必须 | - | 工位编号，同一区域内唯一 | - |
| seatType | integer | 非必须 | 1 | 1 工位，2 会议室，3 储物柜，4 其他 | format: int32 |
| xRatio | number | 非必须 | 0 | X 坐标占底图宽度的百分比（0~100） | - |
| yRatio | number | 非必须 | 0 | Y 坐标占底图高度的百分比（0~100） | - |
| widthRatio | number | 非必须 | 0 | 宽度占底图宽度的百分比 | - |
| heightRatio | number | 非必须 | 0 | 高度占底图高度的百分比 | - |
| rotation | number | 非必须 | 0 | 旋转角度（度） | - |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| seatId | integer | 必须 | - | 新增工位 ID | format: int64 |

**错误码**：50001 工位区域不存在；50004 工位编号在该区域内已存在。

#### 11.5.2 修改工位（5-12）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-12 |
| 接口名称 | 修改工位 |
| Path | `/admin/seat` |
| Method | `PUT` |
| 接口描述 | 修改工位编号、类型、坐标与备注；不改变使用人 |
| 权限码 | `seat:seat:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| seatId | integer | 必须 | - | 工位 ID | format: int64 |
| areaId | integer | 必须 | - | 所属区域 ID | format: int64 |
| seatNo | string | 必须 | - | 工位编号，同一区域内唯一 | - |
| seatType | integer | 非必须 | 1 | 1 工位，2 会议室，3 储物柜，4 其他 | format: int32 |
| xRatio | number | 非必须 | 0 | X 坐标百分比 | - |
| yRatio | number | 非必须 | 0 | Y 坐标百分比 | - |
| widthRatio | number | 非必须 | 0 | 宽度百分比 | - |
| heightRatio | number | 非必须 | 0 | 高度百分比 | - |
| rotation | number | 非必须 | 0 | 旋转角度（度） | - |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**：`data` 为 `null`。

**错误码**：50003 工位不存在；50004 工位编号在该区域内已存在。

#### 11.5.3 批量保存工位布局坐标（5-13）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-13 |
| 接口名称 | 批量保存工位布局坐标 |
| Path | `/admin/seat/batch` |
| Method | `PUT` |
| 接口描述 | 一次性保存同一区域内多个工位的坐标，用于平面图布局编辑器的「保存」动作；只改坐标，不涉及使用人 |
| 权限码 | `seat:seat:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| areaId | integer | 必须 | - | 区域 ID | format: int64 |
| seats | array | 必须 | - | 工位坐标数组，单次最多 200 项 | 元素结构见下表 |

`seats` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| seatId | integer | 必须 | - | 工位 ID | format: int64 |
| xRatio | number | 必须 | - | X 坐标百分比 | - |
| yRatio | number | 必须 | - | Y 坐标百分比 | - |
| widthRatio | number | 非必须 | - | 宽度百分比 | - |
| heightRatio | number | 非必须 | - | 高度百分比 | - |
| rotation | number | 非必须 | - | 旋转角度（度） | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| updatedCount | integer | 必须 | - | 实际更新的工位数 | format: int32 |

**错误码**：50001 工位区域不存在；50003 工位不存在。

> **全量覆盖式保存**：请求中的坐标数组应包含该区域下**所有**需要保存的工位（编辑器整体提交），服务端按 `seatId` 逐条更新。单次上限 200 项，超过时前端需分批提交。
>
> **只改坐标、不动人**：本接口不修改 `user_id` 与 `status`，因此「把某人拖到另一个座位」不走这里，而走 `5-26`。两者分开是为了让「调整布局」与「调整人员」互不干扰：布局模式可以随便拖框，不会误把人的座位换掉。

#### 11.5.4 删除工位（5-14）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-14 |
| 接口名称 | 删除工位 |
| Path | `/admin/seat/{id}` |
| Method | `DELETE` |
| 接口描述 | 逻辑删除工位；工位仍被占用时禁止删除 |
| 权限码 | `seat:seat:remove` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 工位 ID | 45 |

**返回数据**：`data` 为 `null`。

**错误码**：50003 工位不存在；50005 该工位已被占用。

> **先释放再删除**：删除前需先调用 `5-16` 释放工位使用人，否则返回 `50005`。

#### 11.5.5 分配工位（5-15）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-15 |
| 接口名称 | 分配工位 |
| Path | `/admin/seat/{id}/assign` |
| Method | `PUT` |
| 接口描述 | 负责人直接把工位分配给指定成员；若该成员已有其他工位则自动释放原工位 |
| 权限码 | `seat:seat:assign` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 工位 ID | 45 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| userId | integer | 必须 | - | 被分配成员的用户 ID | format: int64 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| seatId | integer | 必须 | - | 工位 ID | format: int64 |
| releasedSeatId | integer | 非必须 | null | 被自动释放的原工位 ID；无原工位时为 null | format: int64 |

**错误码**：50003 工位不存在；50005 该工位已被占用。

> **乐观锁保护**：工位表带 `version` 字段。两人同时把不同成员分配到同一工位时，后提交者会因版本号不匹配而失败并返回 `50005`，避免「一个工位两个使用人」的脏数据。
>
> **自动腾退**：若目标成员当前已占用其他工位，服务端在同一事务内把原工位置为空闲，并在返回中给出 `releasedSeatId`，前端可据此提示「已自动释放 A-03」。

#### 11.5.6 释放工位（5-16）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-16 |
| 接口名称 | 释放工位 |
| Path | `/admin/seat/{id}/release` |
| Method | `PUT` |
| 接口描述 | 解除工位与使用人的绑定，状态置为「空闲」 |
| 权限码 | `seat:seat:assign` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 工位 ID | 45 |

**返回数据**：`data` 为 `null`。

**错误码**：50003 工位不存在；50008 当前状态不允许该操作。

> **「预留」与「维修中」不能被释放**：只有状态为「已占用」的工位可以释放。预留位与维修位需先通过 `5-17` 变更状态。

#### 11.5.7 工位状态变更（5-17）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-17 |
| 接口名称 | 工位状态变更 |
| Path | `/admin/seat/{id}/status` |
| Method | `PUT` |
| 接口描述 | 变更工位状态，用于预留、维修等场景 |
| 权限码 | `seat:seat:edit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 工位 ID | 45 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 必须 | - | 目标状态：1 空闲，3 预留，4 维修中 | format: int32 |
| remark | string | 非必须 | - | 变更原因 | - |

**返回数据**：`data` 为 `null`。

**错误码**：50003 工位不存在；50008 当前状态不允许该操作。

> **「已占用」不能手动设置**：状态 2 只能由分配（`5-15`）或调换审核通过（`5-24`）自动置位。若要手工指定使用人，请用 `5-15`。

### 11.6 后台：工位申请审核（5-18 ~ 5-19）

#### 11.6.1 工位申请分页（5-18）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-18 |
| 接口名称 | 工位申请分页（后台） |
| Path | `/admin/seat/apply/page` |
| Method | `GET` |
| 接口描述 | 管理端分页查询工位申请，用于审核排队 |
| 权限码 | `seat:apply:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 非必须 | - | 0 待审核，1 已通过，2 已驳回，3 已撤销 | format: int32 |
| areaId | integer | 非必须 | - | 限定意向区域 | format: int64 |
| userId | integer | 非必须 | - | 限定申请人 | format: int64 |
| keyword | string | 非必须 | - | 模糊匹配申请人姓名 / 工位编号 | - |
| beginTime | string | 非必须 | - | 申请时间起 | - |
| endTime | string | 非必须 | - | 申请时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| applyId | integer | 必须 | - | 工位申请 ID | format: int64 |
| userId | integer | 必须 | - | 申请人 ID | format: int64 |
| userName | string | 必须 | - | 申请人姓名 | - |
| grade | string | 非必须 | - | 年级 | - |
| currentSeatId | integer | 非必须 | null | 当前工位 ID，便于判断是否需要腾退 | format: int64 |
| currentSeatNo | string | 非必须 | - | 当前工位编号 | - |
| areaId | integer | 必须 | - | 意向区域 ID | format: int64 |
| areaName | string | 必须 | - | 意向区域名称 | - |
| seatId | integer | 非必须 | - | 意向工位 ID | format: int64 |
| seatNo | string | 非必须 | - | 意向工位编号 | - |
| seatStatus | integer | 非必须 | - | 意向工位当前状态，供审核时判断是否仍可分配 | format: int32 |
| reason | string | 非必须 | - | 申请理由 | - |
| status | integer | 必须 | - | 0 待审核，1 已通过，2 已驳回，3 已撤销 | format: int32 |
| auditUserName | string | 非必须 | - | 审核人姓名 | - |
| auditTime | string | 非必须 | - | 审核时间 | - |
| auditRemark | string | 非必须 | - | 审核意见 | - |
| createTime | string | 必须 | - | 申请时间 | - |

#### 11.6.2 工位申请审核（5-19）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-19 |
| 接口名称 | 工位申请审核 |
| Path | `/admin/seat/apply/{id}/audit` |
| Method | `PUT` |
| 接口描述 | 审核工位申请；通过时可直接指定实际分配的工位 |
| 权限码 | `seat:apply:audit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 工位申请 ID | 22 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| auditStatus | integer | 必须 | - | 1 通过，2 驳回 | format: int32 |
| assignSeatId | integer | 非必须 | 意向工位 | 通过时实际分配的工位 ID；申请仅填了意向区域时可在此指定 | format: int64 |
| auditRemark | string | 非必须 | - | 审核意见，驳回时建议填写 | - |

**返回数据**：`data` 为 `null`。

**错误码**：50006 工位申请不存在；50003 工位不存在；50005 该工位已被占用；50008 当前状态不允许该操作。

> **通过审核时同事务做三件事**：更新申请状态 → 分配工位（若申请人已有其他工位则先释放）→ 更新工位状态为「已占用」。
>
> **意向工位可能已被占用**：申请提交后到审核前，意向工位可能已被分配给别人。此时审核接口返回 `50005`，管理员应改传 `assignSeatId` 指定另一个空闲工位，或驳回申请。

### 11.7 前台：座位调换（5-20 ~ 5-22）

#### 11.7.1 成员发起座位调换（5-20）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-20 |
| 接口名称 | 成员发起座位调换 |
| Path | `/user/seat/swap` |
| Method | `POST` |
| 接口描述 | 成员向另一位成员发起座位对调申请，需「对方确认 + 负责人审核」两步完成 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| targetUserId | integer | 必须 | - | 对调方成员的用户 ID | format: int64 |
| reason | string | 非必须 | - | 调换理由，最长 500 字 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| swapId | integer | 必须 | - | 调换申请 ID | format: int64 |
| applySeatId | integer | 必须 | - | 申请方当前工位 ID | format: int64 |
| targetSeatId | integer | 必须 | - | 对调方当前工位 ID | format: int64 |

**错误码**：50010 不能与自己调换工位；50011 对调方工位不存在或已释放。

> **双方都必须有工位**：申请方与对调方当前都得占用工位，否则无法交换。服务端会校验 `seat_info` 中两人各自的 `user_id` 记录，缺失任一方则返回 `50011`。
>
> **工位在提交时快照**：`applySeatId` / `targetSeatId` 在创建申请时即写入，后续若一方工位被管理员调整，审核时会校验目标工位是否仍属于该人，不一致则拒绝（`50011`）。

#### 11.7.2 我的调换申请分页（5-21）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-21 |
| 接口名称 | 我的调换申请分页 |
| Path | `/user/seat/swap/my/page` |
| Method | `GET` |
| 接口描述 | 分页查询当前用户**发起或收到**的座位调换申请 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| role | integer | 非必须 | 0 | 查询视角：0 全部（我发起 + 我收到） 1 我发起的 2 我收到的 | format: int32 |
| status | integer | 非必须 | - | 0 待对方确认，1 对方已同意待审核，2 已通过，3 已拒绝，4 已取消 | format: int32 |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| swapId | integer | 必须 | - | 调换申请 ID | format: int64 |
| applyUserId | integer | 必须 | - | 申请方用户 ID | format: int64 |
| applyUserName | string | 必须 | - | 申请方姓名 | - |
| applySeatId | integer | 必须 | - | 申请方工位 ID | format: int64 |
| applySeatNo | string | 必须 | - | 申请方工位编号 | - |
| targetUserId | integer | 必须 | - | 对调方用户 ID | format: int64 |
| targetUserName | string | 必须 | - | 对调方姓名 | - |
| targetSeatId | integer | 必须 | - | 对调方工位 ID | format: int64 |
| targetSeatNo | string | 必须 | - | 对调方工位编号 | - |
| reason | string | 非必须 | - | 调换理由 | - |
| status | integer | 必须 | - | 0 待对方确认，1 对方已同意待审核，2 已通过，3 已拒绝，4 已取消 | format: int32 |
| isTarget | integer | 必须 | 0 | 当前用户是否为对调方：0 否 1 是（决定是否展示「同意 / 拒绝」按钮） | format: int32 |
| handleTime | string | 非必须 | - | 对方处理时间 | - |
| handleRemark | string | 非必须 | - | 对方处理意见 | - |
| auditUserName | string | 非必须 | - | 审核人姓名 | - |
| auditTime | string | 非必须 | - | 审核时间 | - |
| auditRemark | string | 非必须 | - | 审核意见 | - |
| createTime | string | 必须 | - | 发起时间 | - |

#### 11.7.3 调换对方确认 / 拒绝（5-22）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-22 |
| 接口名称 | 调换对方确认 / 拒绝 |
| Path | `/user/seat/swap/{id}/handle` |
| Method | `PUT` |
| 接口描述 | 对调方成员处理收到的调换申请；同意后进入负责人审核环节 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 调换申请 ID | 9 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| handleStatus | integer | 必须 | - | 1 同意，2 拒绝 | format: int32 |
| handleRemark | string | 非必须 | - | 处理意见 | - |

**返回数据**：`data` 为 `null`。

**错误码**：50009 座位调换申请不存在；50012 仅对调方可处理该申请；50013 调换申请前置状态不满足。

**状态流转**

| 当前状态 | 本接口可流转到 | 说明 |
| --- | --- | --- |
| 0 待对方确认 | 1 对方已同意待审核 | `handleStatus = 1` 同意 |
| 0 待对方确认 | 3 已拒绝 | `handleStatus = 2` 拒绝（终态） |
| 其他状态 | 不允许 | 返回 `50013` |

> **不能自己同意自己**：接口校验当前用户是否为该申请的 `targetUserId`，否则返回 `50012`。这是防越权的关键校验。
>
> **同意不等于换座成功**：状态 1 只是「对方同意让我换」，真正的工位交换发生在负责人审核通过（`5-24`）时。

### 11.8 后台：调换审核（5-23 ~ 5-24）

#### 11.8.1 调换申请分页（后台，5-23）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-23 |
| 接口名称 | 调换申请分页（后台） |
| Path | `/admin/seat/swap/page` |
| Method | `GET` |
| 接口描述 | 管理端分页查询座位调换申请，用于审核与历史追溯 |
| 权限码 | `seat:swap:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 非必须 | - | 0 待对方确认，1 对方已同意待审核，2 已通过，3 已拒绝，4 已取消 | format: int32 |
| userId | integer | 非必须 | - | 限定参与调换的成员（申请方或对调方） | format: int64 |
| keyword | string | 非必须 | - | 模糊匹配双方姓名 / 工位编号 | - |
| beginTime | string | 非必须 | - | 发起时间起 | - |
| endTime | string | 非必须 | - | 发起时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| swapId | integer | 必须 | - | 调换申请 ID | format: int64 |
| applyUserId | integer | 必须 | - | 申请方用户 ID | format: int64 |
| applyUserName | string | 必须 | - | 申请方姓名 | - |
| applyUserGrade | string | 非必须 | - | 申请方年级 | - |
| applySeatId | integer | 必须 | - | 申请方工位 ID | format: int64 |
| applySeatNo | string | 必须 | - | 申请方工位编号 | - |
| targetUserId | integer | 必须 | - | 对调方用户 ID | format: int64 |
| targetUserName | string | 必须 | - | 对调方姓名 | - |
| targetUserGrade | string | 非必须 | - | 对调方年级 | - |
| targetSeatId | integer | 必须 | - | 对调方工位 ID | format: int64 |
| targetSeatNo | string | 必须 | - | 对调方工位编号 | - |
| reason | string | 非必须 | - | 调换理由 | - |
| status | integer | 必须 | - | 0 待对方确认，1 对方已同意待审核，2 已通过，3 已拒绝，4 已取消 | format: int32 |
| handleTime | string | 非必须 | - | 对方处理时间 | - |
| handleRemark | string | 非必须 | - | 对方处理意见 | - |
| auditUserId | integer | 非必须 | - | 审核人 ID | format: int64 |
| auditUserName | string | 非必须 | - | 审核人姓名 | - |
| auditTime | string | 非必须 | - | 审核时间 | - |
| auditRemark | string | 非必须 | - | 审核意见 | - |
| createTime | string | 必须 | - | 发起时间 | - |

> 相比成员端的 5-21（接口 `5-21`），本接口额外返回双方年级，便于管理员判断调换是否合理；同时不返回 `isTarget`（管理端没有「我是对调方」的视角）。

#### 11.8.2 调换申请审核（负责人，5-24）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-24 |
| 接口名称 | 调换申请审核（负责人） |
| Path | `/admin/seat/swap/{id}/audit` |
| Method | `PUT` |
| 接口描述 | 负责人审核已获对方同意的调换申请；通过时在同一事务内交换双方工位 |
| 权限码 | `seat:swap:audit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 调换申请 ID | 9 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| auditStatus | integer | 必须 | - | 1 通过，2 驳回（与全平台审核接口一致，见 3.2 节） | format: int32 |
| auditRemark | string | 非必须 | - | 审核意见 | - |

**返回数据**：`data` 为 `null`。

**错误码**：50009 座位调换申请不存在；50013 调换申请前置状态不满足；50011 对调方工位不存在或已释放。

**状态流转与交换逻辑**

| 当前状态 | 本接口可流转到 | 事务内动作 |
| --- | --- | --- |
| 1 对方已同意待审核 | 2 已通过 | 交换两条 `seat_info` 记录的 `user_id` 与 `assign_time` |
| 1 对方已同意待审核 | 3 已拒绝 | 仅更新状态，工位不动（终态） |
| 其他状态 | 不允许 | 返回 `50013` |

> **入参与记录状态是两套枚举，不要混用**：请求参数 `auditStatus` 沿用全平台审核接口的 `1 通过 / 2 驳回`（见 3.2 节）；下表「本接口可流转到」列的 `2 已通过 / 3 已拒绝` 是调换记录 `seat_swap.status` 自身的取值。服务端映射规则：`auditStatus = 1` → 记录 `status = 2`；`auditStatus = 2` → 记录 `status = 3`。
>
> **前置状态必须是「对方已同意」**：直接从「待对方确认」跳到「已通过」属于跳步，服务端拒绝，保证「对方确认 + 负责人审核」两步缺一不可。
>
> **交换前复核工位归属**：审核通过前服务端再查一次两个工位当前的使用人是否仍是这两位成员；若期间被管理员用 `5-15` 调整过，则返回 `50011` 拒绝，避免把工位换错人。
>
> **设计意图**：私下换座会导致平面图与实际不符，因此调换必须经过「对方确认 + 负责人审核」，且每一步都有记录可追溯。

### 11.9 后台：平面图拖拽（5-25 ~ 5-26）

> 本组接口服务于管理端「可拖拽平面图」：平面图上直接显示每个工位的使用人，管理员拖动即可调整——**拖到空工位 = 平移（移动）**，**拖到已占用工位 = 互换**，操作即时生效、无需审核。它与成员端座位调换申请（5-20 ~ 5-24）的区别只在「谁发起」：管理员直接发起即时生效，成员发起需「对方确认 + 负责人审核」。两者最终都落到同一张 `seat_info` 表，效果一致。

#### 11.9.1 平面图数据（后台，5-25）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-25 |
| 接口名称 | 平面图数据（后台） |
| Path | `/admin/seat/plan` |
| Method | `GET` |
| 接口描述 | 一次返回区域、底图尺寸与全部工位坐标及使用人信息，供管理端渲染可拖拽平面图 |
| 权限码 | `seat:seat:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| areaId | integer | 非必须 | - | 指定区域 ID；不传表示返回全部区域 | format: int64 |
| withDisabled | integer | 非必须 | 1 | 是否包含停用区域：0 不包含 1 包含 | format: int32 |

**返回数据**

`data` 为数组，按 `sortNo` 升序；元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| areaId | integer | 必须 | - | 区域 ID | format: int64 |
| name | string | 必须 | - | 区域名称 | - |
| planImageUrl | string | 非必须 | - | 平面图底图地址（OSS） | - |
| width | integer | 必须 | 0 | 底图宽度（像素），坐标换算基准 | format: int32 |
| height | integer | 必须 | 0 | 底图高度（像素），坐标换算基准 | format: int32 |
| sortNo | integer | 必须 | 0 | 排序号 | format: int32 |
| seatStat | object | 必须 | - | 工位统计，含 `total`、`free`、`used`、`reserved`、`repairing` | - |
| seats | array | 必须 | [] | 该区域下的工位数组，结构见下表 | - |

`seats` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| seatId | integer | 必须 | - | 工位 ID | format: int64 |
| seatNo | string | 必须 | - | 工位编号 | - |
| seatType | integer | 必须 | - | 1 工位，2 会议室，3 储物柜，4 其他 | format: int32 |
| xRatio | number | 必须 | 0 | X 坐标占底图宽度的百分比（0~100） | - |
| yRatio | number | 必须 | 0 | Y 坐标占底图高度的百分比（0~100） | - |
| widthRatio | number | 必须 | 0 | 宽度占底图宽度的百分比 | - |
| heightRatio | number | 必须 | 0 | 高度占底图高度的百分比 | - |
| rotation | number | 非必须 | 0 | 旋转角度（度） | - |
| status | integer | 必须 | - | 1 空闲，2 已占用，3 预留，4 维修中 | format: int32 |
| occupantUserId | integer | 非必须 | null | 使用人用户 ID；空闲时为 null | format: int64 |
| occupantName | string | 非必须 | - | 使用人姓名 | - |
| occupantGrade | string | 非必须 | - | 使用人年级，便于管理员核对 | - |
| occupantMajor | string | 非必须 | - | 使用人专业 | - |
| assignTime | string | 非必须 | - | 分配时间 | - |
| remark | string | 非必须 | - | 工位备注 | - |

> **与成员端 5-01 的差异**：管理端额外返回 `occupantUserId`、年级、专业、分配时间与备注，便于拖拽时确认「拖的是谁」；成员端只返回使用人姓名（隐私保护，见 5-01 说明）。
>
> **拖拽必须一次拿全量**：本接口不分页，一次返回整张平面图。工位数量一般在百位以内，单次返回可接受；若某个区域工位超过 300 个，建议前端只渲染当前视口内的工位。

#### 11.9.2 平面图拖拽调整（移动 / 互换，5-26）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 5-26 |
| 接口名称 | 平面图拖拽调整（移动 / 互换） |
| Path | `/admin/seat/plan/drag` |
| Method | `POST` |
| 接口描述 | 管理员在平面图上拖动使用人：拖到空工位即移动，拖到已占用工位即互换；即时生效 |
| 权限码 | `seat:seat:assign` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| fromSeatId | integer | 必须 | - | 被拖动的源工位 ID（当前有人使用） | format: int64 |
| toSeatId | integer | 必须 | - | 拖动落点工位 ID：空工位则移动，已占用则互换 | format: int64 |
| remark | string | 非必须 | - | 调整说明，如「按方向重新排座」，最长 255 字 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| action | string | 必须 | - | 本次动作：`move` 移动，`swap` 互换 | - |
| fromSeat | object | 必须 | - | 源工位最新状态，含 `seatId`、`seatNo`、`status`、`occupantUserId`、`occupantName` | - |
| toSeat | object | 必须 | - | 目标工位最新状态，字段同 `fromSeat` | - |

**错误码**：50003 工位不存在；50005 该工位已被占用；50008 当前状态不允许该操作；50015 源工位当前无人，无法拖动；50016 源工位与目标工位不能相同；50017 目标工位为预留或维修中，不可占用。

> **拖动即分配，不走审核**：本接口对应「负责人可直接分配工位」（见 11.1 节口径 1），与成员端调换申请（`5-20` ~ `5-24`）的区别只在发起方与是否需要对方确认。
>
> **互换是原子的**：两人互换时，服务端在**同一事务**内交换两条 `seat_info` 记录的 `user_id` 与 `assign_time`，`status` 保持 `2 已占用`——不会出现「两人都空着」或「两人占同一工位」的中间态。
>
> **落点是空工位时是「平移」**：源工位置为 `1 空闲` 并清空 `user_id`，目标工位置为 `2 已占用` 并写入使用人与 `assign_time`。
>
> **预留位与维修位不可拖入**：`status = 3`（预留）或 `4`（维修中）的工位返回 `50017`，需先用 `5-17` 改回空闲。会议室、储物柜（`seatType = 2` / `3`）可以拖入，前端宜二次确认。
>
> **并发保护**：与 `5-15` 一致，依赖 `seat_info.version` 乐观锁；两人同时拖动相同工位时，后提交者返回 `50005`，前端刷新平面图后重试。
>
> **留痕**：每次拖拽写入 `sys_oper_log`（操作人、动作、源 / 目标工位、说明），可在接口 `8-12` 操作日志分页中追溯，用于回答「这个座位什么时候被换的、谁换的」。
>
> **两件事不要混**：本接口只改「谁坐哪」，**不动坐标**；整体挪动工位框、旋转工位属于布局绘制，走 `5-13` 批量保存坐标。建议前端用「座位模式 / 布局模式」两个开关区分，避免误操作。

---

## 第 12 章 模块六：学长学姐展板（honor）

### 12.1 模块概述

- **职责**：已毕业学长学姐的信息与荣誉记录维护，成员自助提交、管理员审核后上板，前台动态展示。
- **数据模型**：见 5.6 节。
- **接口清单**：见 4.7 节（`6-01` ~ `6-19`，共 19 个接口）。
- **已确定口径**：
  1. 展板信息由**成员自行填写**，管理员审核通过后上板；
  2. 状态包含：待审核 / 已通过 / 已驳回 / 已下板；
  3. 审核前可撤回（接口 `6-16`）；被驳回或审核通过后需要修改时，在**原记录**上修改并重新送审（接口 `6-19`），不新建记录；
  4. 前台公示接口（6-01 ~ 6-04）仅返回「审核通过且已上板」的数据。

### 12.2 前台公示（6-01 ~ 6-04）

> **可见性说明**：本组接口位于 `/user/**` 下，按 1.4 节约定需要登录后才能访问——展板定位为「部门内部展示 + 中心大屏登录后轮播」。若后续需要在未登录状态对外展示（如招新宣传页），可将本组接口整体迁至 `/common/honor/**`，接口结构不变，见 16.4 节 Q7。

#### 12.2.1 展板学长分页（6-01）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-01 |
| 接口名称 | 展板学长分页 |
| Path | `/user/honor/alumni/page` |
| Method | `GET` |
| 接口描述 | 成员端查询展板上的学长学姐信息，用于展板列表页 |
| 权限码 | 登录即可 |
| 是否需令牌 | 可选 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| graduationYear | string | 非必须 | - | 毕业年份筛选，如 2024 | - |
| major | string | 非必须 | - | 专业筛选 | - |
| keyword | string | 非必须 | - | 模糊匹配 name / company / position | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；**仅返回 `status = 1 已通过` 且 `isShow = 1 上板` 的记录**，按 `sortNo` 升序、`graduationYear` 倒序。`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| alumniId | integer | 必须 | - | 学长学姐信息 ID | format: int64 |
| name | string | 必须 | - | 姓名 | - |
| gender | integer | 必须 | 0 | 0 未知，1 男，2 女 | format: int32 |
| graduationYear | string | 非必须 | - | 毕业年份 | - |
| major | string | 非必须 | - | 专业 | - |
| className | string | 非必须 | - | 班级 | - |
| company | string | 非必须 | - | 就职单位 | - |
| position | string | 非必须 | - | 职位 | - |
| avatarUrl | string | 非必须 | - | 头像地址（OSS） | - |
| brief | string | 非必须 | - | 个人简介（列表返回摘要，最长 100 字） | - |
| tags | array | 非必须 | [] | 标签数组（由 `tags` 字段按逗号拆分） | - |
| honorCount | integer | 必须 | 0 | 荣誉记录数量 | format: int32 |
| sortNo | integer | 必须 | 0 | 展示排序号 | format: int32 |

> **双条件过滤**：前台只展示「审核通过」**且**「已上板」的数据。管理员即便审核通过，也要显式上板（`6-09`）才会出现在展板，便于控制展示节奏。

#### 12.2.2 学长详情（荣誉时间轴，6-02）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-02 |
| 接口名称 | 学长详情（荣誉时间轴） |
| Path | `/user/honor/alumni/{id}` |
| Method | `GET` |
| 接口描述 | 查询学长学姐详情与荣誉记录列表，供详情页渲染荣誉时间轴 |
| 权限码 | 登录即可 |
| 是否需令牌 | 可选 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 学长学姐信息 ID | 15 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| alumniId | integer | 必须 | - | 学长学姐信息 ID | format: int64 |
| name | string | 必须 | - | 姓名 | - |
| gender | integer | 必须 | 0 | 0 未知，1 男，2 女 | format: int32 |
| graduationYear | string | 非必须 | - | 毕业年份 | - |
| major | string | 非必须 | - | 专业 | - |
| className | string | 非必须 | - | 班级 | - |
| company | string | 非必须 | - | 就职单位 | - |
| position | string | 非必须 | - | 职位 | - |
| avatarUrl | string | 非必须 | - | 头像地址 | - |
| brief | string | 非必须 | - | 个人简介（完整内容） | - |
| tags | array | 非必须 | [] | 标签数组 | - |
| records | array | 必须 | [] | 荣誉记录数组，按 `awardDate` 倒序，结构见下表 | - |

`records` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| recordId | integer | 必须 | - | 荣誉记录 ID | format: int64 |
| title | string | 必须 | - | 荣誉名称 | - |
| level | integer | 必须 | - | 1 国家级，2 省级，3 校级，4 企业及其他 | format: int32 |
| awardDate | string | 非必须 | - | 获奖日期 | - |
| description | string | 非必须 | - | 荣誉说明 | - |
| certificateUrl | string | 非必须 | - | 证书图片 / PDF 地址（OSS） | - |

**错误码**：60001 学长学姐信息不存在。

> **未上板的数据不可访问**：详情接口同样校验「已通过 + 已上板」，否则返回 `60001`（不暴露「存在但未上板」的信息，避免通过遍历 ID 探测未公开数据）。

#### 12.2.3 精选轮播（6-03）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-03 |
| 接口名称 | 精选轮播 |
| Path | `/user/honor/alumni/highlight` |
| Method | `GET` |
| 接口描述 | 返回用于首页轮播的精选学长学姐，取排序号靠前的前若干条 |
| 权限码 | 登录即可 |
| 是否需令牌 | 可选 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| limit | integer | 非必须 | 6 | 返回条数上限，范围 1~20 | format: int32 |

**返回数据**

`data` 为数组，元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| alumniId | integer | 必须 | - | 学长学姐信息 ID | format: int64 |
| name | string | 必须 | - | 姓名 | - |
| graduationYear | string | 非必须 | - | 毕业年份 | - |
| major | string | 非必须 | - | 专业 | - |
| company | string | 非必须 | - | 就职单位 | - |
| position | string | 非必须 | - | 职位 | - |
| avatarUrl | string | 非必须 | - | 头像地址 | - |
| brief | string | 非必须 | - | 个人简介摘要 | - |

> 排序规则：`sortNo` 升序取前 `limit` 条；管理员可通过接口 `6-10` 调整排序把想重点展示的人提到前面。

#### 12.2.4 展板统计（6-04）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-04 |
| 接口名称 | 展板统计 |
| Path | `/user/honor/stat` |
| Method | `GET` |
| 接口描述 | 返回展板的人数、荣誉数与年份分布，用于展板页头部统计区 |
| 权限码 | 登录即可 |
| 是否需令牌 | 可选 |

**请求参数**：无。

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| alumniCount | integer | 必须 | 0 | 展板人数（已通过且已上板） | format: int32 |
| honorCount | integer | 必须 | 0 | 荣誉记录总数 | format: int32 |
| yearStats | array | 必须 | [] | 毕业年份分布，元素含 `year`、`count` | - |
| levelStats | array | 必须 | [] | 荣誉级别分布，元素含 `level`、`count` | - |
| companyCount | integer | 必须 | 0 | 涉及的用人单位数量 | format: int32 |

### 12.3 后台：学长管理（6-05 ~ 6-13）

#### 12.3.1 学长分页（后台，6-05）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-05 |
| 接口名称 | 学长分页（后台） |
| Path | `/admin/honor/alumni/page` |
| Method | `GET` |
| 接口描述 | 管理端分页查询全部学长学姐信息（含待审核、已下板），用于管理列表 |
| 权限码 | `honor:alumni:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 非必须 | - | 0 待审核，1 已通过，2 已驳回 | format: int32 |
| isShow | integer | 非必须 | - | 0 下板，1 上板 | format: int32 |
| graduationYear | string | 非必须 | - | 毕业年份筛选 | - |
| keyword | string | 非必须 | - | 模糊匹配 name / company / position | - |
| submitUserId | integer | 非必须 | - | 限定填写人 | format: int64 |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| alumniId | integer | 必须 | - | 学长学姐信息 ID | format: int64 |
| name | string | 必须 | - | 姓名 | - |
| gender | integer | 必须 | 0 | 0 未知，1 男，2 女 | format: int32 |
| graduationYear | string | 非必须 | - | 毕业年份 | - |
| major | string | 非必须 | - | 专业 | - |
| company | string | 非必须 | - | 就职单位 | - |
| position | string | 非必须 | - | 职位 | - |
| avatarUrl | string | 非必须 | - | 头像地址 | - |
| tags | string | 非必须 | - | 标签，逗号分隔原串 | - |
| honorCount | integer | 必须 | 0 | 荣誉记录数量 | format: int32 |
| submitUserId | integer | 非必须 | - | 填写人 ID | format: int64 |
| submitUserName | string | 非必须 | - | 填写人姓名 | - |
| status | integer | 必须 | - | 0 待审核，1 已通过，2 已驳回 | format: int32 |
| isShow | integer | 必须 | - | 0 下板，1 上板 | format: int32 |
| sortNo | integer | 必须 | 0 | 展示排序号 | format: int32 |
| auditUserName | string | 非必须 | - | 审核人姓名 | - |
| auditTime | string | 非必须 | - | 审核时间 | - |
| auditRemark | string | 非必须 | - | 审核意见 | - |
| createTime | string | 必须 | - | 创建时间 | - |

#### 12.3.2 新增学长（6-06）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-06 |
| 接口名称 | 新增学长 |
| Path | `/admin/honor/alumni` |
| Method | `POST` |
| 接口描述 | 管理员直接录入学长学姐信息，录入后状态为「已通过」，可立即上板 |
| 权限码 | `honor:alumni:add` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| name | string | 必须 | - | 姓名，最长 32 字 | - |
| gender | integer | 非必须 | 0 | 0 未知，1 男，2 女 | format: int32 |
| graduationYear | string | 非必须 | - | 毕业年份，如 2024 | - |
| major | string | 非必须 | - | 专业 | - |
| className | string | 非必须 | - | 班级 | - |
| company | string | 非必须 | - | 就职单位 | - |
| position | string | 非必须 | - | 职位 | - |
| avatarUrl | string | 非必须 | - | 头像地址，先调接口 8-01 上传（`bizType = honor`）后填入 | - |
| brief | string | 非必须 | - | 个人简介（富文本或长文本） | - |
| tags | string | 非必须 | - | 标签，逗号分隔，如 `保研,ACM 金牌` | - |
| isShow | integer | 非必须 | 0 | 0 下板，1 上板 | format: int32 |
| sortNo | integer | 非必须 | 0 | 展示排序号，升序 | format: int32 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| alumniId | integer | 必须 | - | 新增信息 ID | format: int64 |

> **管理员录入免审核**：本接口写入的记录 `status` 直接为 1（已通过），`submitUserId` 记为当前管理员。成员自助提交（`6-14`）才需要走审核流程。

#### 12.3.3 修改学长（6-07）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-07 |
| 接口名称 | 修改学长 |
| Path | `/admin/honor/alumni` |
| Method | `PUT` |
| 接口描述 | 修改学长学姐信息，支持调整展示排序与上板状态 |
| 权限码 | `honor:alumni:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| alumniId | integer | 必须 | - | 学长学姐信息 ID | format: int64 |
| name | string | 必须 | - | 姓名，最长 32 字 | - |
| gender | integer | 非必须 | 0 | 0 未知，1 男，2 女 | format: int32 |
| graduationYear | string | 非必须 | - | 毕业年份，如 2024 | - |
| major | string | 非必须 | - | 专业 | - |
| className | string | 非必须 | - | 班级 | - |
| company | string | 非必须 | - | 就职单位 | - |
| position | string | 非必须 | - | 职位 | - |
| avatarUrl | string | 非必须 | - | 头像地址 | - |
| brief | string | 非必须 | - | 个人简介 | - |
| tags | string | 非必须 | - | 标签，逗号分隔，如 `保研,ACM 金牌` | - |
| isShow | integer | 非必须 | 0 | 0 下板，1 上板 | format: int32 |
| sortNo | integer | 非必须 | 0 | 展示排序号，升序 | format: int32 |

**返回数据**：`data` 为 `null`。

**错误码**：60001 学长学姐信息不存在。

> **本接口不改审核状态与上板开关**：审核状态变更见 `6-18`，上板 / 下板见 `6-09`。这样职责清晰，也避免误操作把待审核数据直接改成已通过。

#### 12.3.4 删除学长（6-08）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-08 |
| 接口名称 | 删除学长 |
| Path | `/admin/honor/alumni/{id}` |
| Method | `DELETE` |
| 接口描述 | 逻辑删除学长学姐信息，同时逻辑删除其下全部荣誉记录 |
| 权限码 | `honor:alumni:remove` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 学长学姐信息 ID | 15 |

**返回数据**：`data` 为 `null`。

**错误码**：60001 学长学姐信息不存在。

> **连带删除荣誉记录**：项目未使用物理外键与级联删除，由服务端在同一事务内把该 `alumniId` 下的 `honor_record` 一并逻辑删除，避免留下无主荣誉记录。

#### 12.3.5 上板 / 下板（6-09）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-09 |
| 接口名称 | 上板 / 下板 |
| Path | `/admin/honor/alumni/{id}/status` |
| Method | `PUT` |
| 接口描述 | 控制某条信息是否在前台展板展示 |
| 权限码 | `honor:alumni:edit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 学长学姐信息 ID | 15 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| isShow | integer | 必须 | - | 0 下板，1 上板 | format: int32 |

**返回数据**：`data` 为 `null`。

**错误码**：60001 学长学姐信息不存在；60006 当前状态不允许上板。

> **只有已通过审核的才能上板**：若 `status` 仍为「待审核」或「已驳回」，上板请求返回 `60006`。下板不受此限制，可随时执行。

#### 12.3.6 调整展示排序（6-10）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-10 |
| 接口名称 | 调整展示排序 |
| Path | `/admin/honor/alumni/{id}/sort` |
| Method | `PUT` |
| 接口描述 | 调整某条信息的展示排序号，数值越小越靠前 |
| 权限码 | `honor:alumni:edit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 学长学姐信息 ID | 15 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| sortNo | integer | 必须 | - | 排序号，升序排列 | format: int32 |

**返回数据**：`data` 为 `null`。

**错误码**：60001 学长学姐信息不存在。

> 只影响单条记录的排序号。若需要整体重排（如拖拽排序），前端可对多条的 `sortNo` 依次调用本接口，或使用下方约定：按 10 递增预留间隙（10、20、30…），插入时取中间值即可避免频繁重排。

#### 12.3.7 新增荣誉记录（6-11）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-11 |
| 接口名称 | 新增荣誉记录 |
| Path | `/admin/honor/record` |
| Method | `POST` |
| 接口描述 | 为指定学长学姐新增一条荣誉记录 |
| 权限码 | `honor:record:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| alumniId | integer | 必须 | - | 所属学长学姐 ID | format: int64 |
| title | string | 必须 | - | 荣誉名称，最长 200 字 | - |
| level | integer | 非必须 | 4 | 1 国家级，2 省级，3 校级，4 企业及其他 | format: int32 |
| awardDate | string | 非必须 | - | 获奖日期，格式 yyyy-MM-dd | - |
| description | string | 非必须 | - | 荣誉说明，最长 500 字 | - |
| certificateUrl | string | 非必须 | - | 证书图片 / PDF 地址，先调接口 8-01 上传后填入 | - |
| sortNo | integer | 非必须 | 0 | 排序号，升序 | format: int32 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| recordId | integer | 必须 | - | 新增荣誉记录 ID | format: int64 |

**错误码**：60001 学长学姐信息不存在。

#### 12.3.8 修改荣誉记录（6-12）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-12 |
| 接口名称 | 修改荣誉记录 |
| Path | `/admin/honor/record` |
| Method | `PUT` |
| 接口描述 | 修改荣誉记录的名称、级别、获奖日期、说明与证书 |
| 权限码 | `honor:record:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| recordId | integer | 必须 | - | 荣誉记录 ID | format: int64 |
| alumniId | integer | 必须 | - | 所属学长学姐 ID | format: int64 |
| title | string | 必须 | - | 荣誉名称，最长 200 字 | - |
| level | integer | 非必须 | 4 | 1 国家级，2 省级，3 校级，4 企业及其他 | format: int32 |
| awardDate | string | 非必须 | - | 获奖日期，格式 yyyy-MM-dd | - |
| description | string | 非必须 | - | 荣誉说明，最长 500 字 | - |
| certificateUrl | string | 非必须 | - | 证书图片 / PDF 地址 | - |
| sortNo | integer | 非必须 | 0 | 排序号，升序 | format: int32 |

**返回数据**：`data` 为 `null`。

**错误码**：60005 荣誉记录不存在。

#### 12.3.9 删除荣誉记录（6-13）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-13 |
| 接口名称 | 删除荣誉记录 |
| Path | `/admin/honor/record/{id}` |
| Method | `DELETE` |
| 接口描述 | 逻辑删除单条荣誉记录 |
| 权限码 | `honor:record:edit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 荣誉记录 ID | 88 |

**返回数据**：`data` 为 `null`。

**错误码**：60005 荣誉记录不存在。

### 12.4 前台：提交、撤回与修改（6-14 ~ 6-16、6-19）

#### 12.4.1 成员提交学长信息（6-14）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-14 |
| 接口名称 | 成员提交学长信息 |
| Path | `/user/honor/alumni` |
| Method | `POST` |
| 接口描述 | 成员自助提交学长学姐信息，提交后进入待审核状态 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| name | string | 必须 | - | 姓名，最长 32 字 | - |
| gender | integer | 非必须 | 0 | 0 未知，1 男，2 女 | format: int32 |
| graduationYear | string | 非必须 | - | 毕业年份，如 2024 | - |
| major | string | 非必须 | - | 专业 | - |
| className | string | 非必须 | - | 班级 | - |
| company | string | 非必须 | - | 就职单位 | - |
| position | string | 非必须 | - | 职位 | - |
| avatarUrl | string | 非必须 | - | 头像地址，先调接口 8-01 上传（`bizType = honor`）后填入 | - |
| brief | string | 非必须 | - | 个人简介 | - |
| tags | string | 非必须 | - | 标签，逗号分隔 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| alumniId | integer | 必须 | - | 提交记录 ID | format: int64 |
| status | integer | 必须 | - | 审核状态，固定返回 0 待审核 | format: int32 |

> **提交后不能直接可见**：成员提交的记录 `status = 0 待审核`、`isShow = 0 下板`，需管理员审核通过（`6-18`）并上板（`6-09`）后才出现在展板。
>
> **`submitUserId` 由服务端写入**：记录填写人以便追溯，不接受前端传入，防止伪造他人提交。

#### 12.4.2 我的提交记录分页（6-15）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-15 |
| 接口名称 | 我的提交记录分页 |
| Path | `/user/honor/alumni/my/page` |
| Method | `GET` |
| 接口描述 | 分页查询当前登录用户提交的学长学姐信息及其审核状态 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 非必须 | - | 0 待审核，1 已通过，2 已驳回 | format: int32 |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；仅返回 `submitUserId = 当前用户` 的记录。`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| alumniId | integer | 必须 | - | 提交记录 ID | format: int64 |
| name | string | 必须 | - | 姓名 | - |
| graduationYear | string | 非必须 | - | 毕业年份 | - |
| major | string | 非必须 | - | 专业 | - |
| company | string | 非必须 | - | 就职单位 | - |
| position | string | 非必须 | - | 职位 | - |
| avatarUrl | string | 非必须 | - | 头像地址 | - |
| status | integer | 必须 | - | 0 待审核，1 已通过，2 已驳回 | format: int32 |
| isShow | integer | 必须 | - | 0 下板，1 上板 | format: int32 |
| auditUserName | string | 非必须 | - | 审核人姓名 | - |
| auditTime | string | 非必须 | - | 审核时间 | - |
| auditRemark | string | 非必须 | - | 审核意见，驳回原因在此查看 | - |
| canWithdraw | integer | 必须 | 0 | 是否可撤回：0 否 1 是（仅待审核时为 1） | format: int32 |
| createTime | string | 必须 | - | 提交时间 | - |

#### 12.4.3 撤回提交（审核前，6-16）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-16 |
| 接口名称 | 撤回提交 |
| Path | `/user/honor/alumni/{id}/withdraw` |
| Method | `PUT` |
| 接口描述 | 成员撤回尚未审核的提交，撤回后记录失效 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 提交记录 ID | 21 |

**返回数据**：`data` 为 `null`。

**错误码**：60001 学长学姐信息不存在；60003 无权撤回该提交；60004 已审核通过的信息不可撤回。

> **只有「待审核」可撤回**：已通过（可能已上板）或已驳回的记录都不能撤回，避免展板数据被提交人单方面移除导致信息不一致。已通过后如需修改，请走接口 `6-19`（在原记录上修改，修改后自动下板并重新送审），避免产生重复记录。


#### 12.4.4 重新提交我的信息（6-19）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-19 |
| 接口名称 | 重新提交我的信息 |
| Path | `/user/honor/alumni/{id}` |
| Method | `PUT` |
| 接口描述 | 提交人修改自己提交的学长学姐信息（被驳回后修正、或已通过后补充资料）并重新送审 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 提交记录 ID | 21 |

Body 参数（字段与接口 `6-14` 相同，未传的字段保持原值）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| name | string | 非必须 | - | 姓名，最长 32 字 | - |
| gender | integer | 非必须 | - | 0 未知，1 男，2 女 | format: int32 |
| graduationYear | string | 非必须 | - | 毕业年份，如 2024 | - |
| major | string | 非必须 | - | 专业 | - |
| className | string | 非必须 | - | 班级 | - |
| company | string | 非必须 | - | 就职单位 | - |
| position | string | 非必须 | - | 职位 | - |
| avatarUrl | string | 非必须 | - | 头像地址，先调接口 8-01 上传（`bizType = honor`）后填入 | - |
| brief | string | 非必须 | - | 个人简介 | - |
| tags | string | 非必须 | - | 标签，逗号分隔 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| alumniId | integer | 必须 | - | 提交记录 ID | format: int64 |
| status | integer | 必须 | - | 审核状态，修改后固定重置为 0 待审核 | format: int32 |
| isShow | integer | 必须 | 0 | 展示状态，修改后固定重置为 0 下板 | format: int32 |

**错误码**：60001 学长学姐信息不存在；60003 无权撤回该提交（含「非本人提交」）；60008 当前状态不允许修改。

**状态流转**

| 修改前状态 | 是否允许修改 | 修改后状态 |
| --- | --- | --- |
| 0 待审核 | 允许（等同于 `6-16` 撤回后重填） | 0 待审核 |
| 2 已驳回 | 允许 | 0 待审核，重新排队 |
| 1 已通过 | 允许 | 0 待审核，且 `isShow` 重置为 0（自动下板） |
| 其他状态 | 不允许 | 返回 `60008` |

> **为什么需要本接口**：被驳回的信息若只能「重新提交一条新记录」，展板与审核队列里会出现同一个人的多条重复记录。本接口让成员在**原记录**上修正，审核通过后展板仍是同一条数据；实现对应 16.4 节 Q2 的默认方案。
>
> **已通过的记录再改会自动下板**：按 Q2 口径，审核通过后再次修改需重新走审核，因此修改即把 `isShow` 置 0；管理员如需恢复展示，可用 `6-09` 上板或 `6-10` 调整排序。
>
> **只改文本字段**：本接口不改 `submitUserId`、`auditUserId`、`auditTime` 等审计字段；审核人与审核时间在下次审核（`6-18`）时覆盖写入。

### 12.5 后台：审核（6-17 ~ 6-18）

#### 12.5.1 待审核信息分页（6-17）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-17 |
| 接口名称 | 待审核信息分页（后台） |
| Path | `/admin/honor/alumni/audit/page` |
| Method | `GET` |
| 接口描述 | 分页查询成员提交的待审核信息，用于审核排队 |
| 权限码 | `honor:alumni:audit` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 非必须 | 0 | 0 待审核（默认），1 已通过，2 已驳回 | format: int32 |
| submitUserId | integer | 非必须 | - | 限定提交人 | format: int64 |
| beginTime | string | 非必须 | - | 提交时间起 | - |
| endTime | string | 非必须 | - | 提交时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| alumniId | integer | 必须 | - | 提交记录 ID | format: int64 |
| name | string | 必须 | - | 姓名 | - |
| gender | integer | 必须 | 0 | 0 未知，1 男，2 女 | format: int32 |
| graduationYear | string | 非必须 | - | 毕业年份 | - |
| major | string | 非必须 | - | 专业 | - |
| className | string | 非必须 | - | 班级 | - |
| company | string | 非必须 | - | 就职单位 | - |
| position | string | 非必须 | - | 职位 | - |
| avatarUrl | string | 非必须 | - | 头像地址 | - |
| brief | string | 非必须 | - | 个人简介 | - |
| tags | string | 非必须 | - | 标签，逗号分隔原串 | - |
| submitUserId | integer | 必须 | - | 提交人 ID | format: int64 |
| submitUserName | string | 非必须 | - | 提交人姓名 | - |
| submitUserGrade | string | 非必须 | - | 提交人年级，便于判断信息可信度 | - |
| status | integer | 必须 | - | 0 待审核，1 已通过，2 已驳回 | format: int32 |
| isShow | integer | 必须 | - | 0 下板，1 上板 | format: int32 |
| auditUserName | string | 非必须 | - | 审核人姓名 | - |
| auditTime | string | 非必须 | - | 审核时间 | - |
| auditRemark | string | 非必须 | - | 审核意见 | - |
| createTime | string | 必须 | - | 提交时间 | - |

#### 12.5.2 审核通过 / 驳回（6-18）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 6-18 |
| 接口名称 | 审核通过 / 驳回 |
| Path | `/admin/honor/alumni/{id}/audit` |
| Method | `PUT` |
| 接口描述 | 审核成员提交的学长学姐信息；通过后可选择直接上板 |
| 权限码 | `honor:alumni:audit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 提交记录 ID | 21 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| auditStatus | integer | 必须 | - | 1 通过，2 驳回 | format: int32 |
| isShow | integer | 非必须 | 0 | 通过时是否同时上板：0 不上板 1 上板 | format: int32 |
| auditRemark | string | 非必须 | - | 审核意见，驳回时建议填写 | - |

**返回数据**：`data` 为 `null`。

**错误码**：60001 学长学姐信息不存在；60002 该信息已审核，无法重复审核。

**状态流转**

| 当前状态 | 本接口可流转到 | 说明 |
| --- | --- | --- |
| 0 待审核 | 1 已通过 | 可同时把 `isShow` 置 1 直接上板 |
| 0 待审核 | 2 已驳回 | 终态；成员可修改后重新提交新记录 |
| 其他状态 | 不允许 | 返回 `60002` |

> **审核通过不等于上板**：`isShow` 默认 0。管理员若把 `isShow` 传 1，则一次操作完成「通过 + 上板」。
>
> **不做「已驳回后重新打开」**：驳回是终态，成员需重新提交一条记录。这样审核历史清晰，也避免同一记录反复审核导致 `auditTime` / `auditRemark` 被覆盖丢失追溯价值。

---

## 第 13 章 模块七：值日排班管理（duty）

### 13.1 模块概述

- **职责**：排班计划维护、自动排班生成、值日安排公示、值日完成确认、成员换班、提醒配置。
- **数据模型**：见 5.7 节。
- **接口清单**：见 4.8 节（`7-01` ~ `7-21`，共 21 个接口）。
- **已确定口径**：
  1. v1 以**分组轮转**为主（`planType = 2`），按周轮转与手工指定作为备选；
  2. 发布后成员端公示，成员可发起换班并需对方同意；
  3. 提醒渠道先做**站内消息**，邮件 / 微信推送列入 v1.1。

### 13.2 前台：值日查看与确认（7-01 ~ 7-04）

#### 13.2.1 值日安排分页（公示，7-01）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-01 |
| 接口名称 | 值日安排分页（公示） |
| Path | `/user/duty/schedule/page` |
| Method | `GET` |
| 接口描述 | 成员端查询已发布计划下的值日安排 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| planId | integer | 非必须 | 最新已发布计划 | 排班计划 ID | format: int64 |
| userId | integer | 非必须 | - | 限定某个成员 | format: int64 |
| groupName | string | 非必须 | - | 分组名称筛选 | - |
| status | integer | 非必须 | - | 0 未完成，1 已完成，2 请假，3 已换班 | format: int32 |
| beginDate | string | 非必须 | - | 值日日期起，格式 yyyy-MM-dd | - |
| endDate | string | 非必须 | - | 值日日期止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；**仅返回 `plan.status = 1 已发布` 的计划下的安排**，按 `dutyDate` 升序。`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| scheduleId | integer | 必须 | - | 值日安排 ID | format: int64 |
| planId | integer | 必须 | - | 所属计划 ID | format: int64 |
| planTitle | string | 必须 | - | 计划标题 | - |
| dutyDate | string | 必须 | - | 值日日期 | - |
| userId | integer | 必须 | - | 值日人 ID | format: int64 |
| userName | string | 必须 | - | 值日人姓名 | - |
| groupName | string | 非必须 | - | 所属分组名称 | - |
| taskContent | string | 非必须 | - | 值日内容 | - |
| status | integer | 必须 | - | 0 未完成，1 已完成，2 请假，3 已换班 | format: int32 |
| confirmTime | string | 非必须 | - | 完成确认时间 | - |
| isMine | integer | 必须 | 0 | 是否当前用户的值日：0 否 1 是（决定是否展示「确认完成」按钮） | format: int32 |

#### 13.2.2 我的值日（含待办，7-02）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-02 |
| 接口名称 | 我的值日（含待办） |
| Path | `/user/duty/schedule/my` |
| Method | `GET` |
| 接口描述 | 返回当前用户的下次值日与待办统计，用于首页待办卡片 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**：无。

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| nextDuty | object | 非必须 | null | 下次值日，结构见下表；无未来值日时为 null | - |
| todayDuty | object | 非必须 | null | 今日值日；今日无值日时为 null | - |
| pendingCount | integer | 必须 | 0 | 待完成（未确认）的值日次数 | format: int32 |
| completedCount | integer | 必须 | 0 | 已完成的次数（当前计划内） | format: int32 |
| swappedCount | integer | 必须 | 0 | 已换班的次数（当前计划内） | format: int32 |
| pendingSwapCount | integer | 必须 | 0 | 待我处理的换班申请数 | format: int32 |

`nextDuty` / `todayDuty` 结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| scheduleId | integer | 必须 | - | 值日安排 ID | format: int64 |
| dutyDate | string | 必须 | - | 值日日期 | - |
| groupName | string | 非必须 | - | 所属分组名称 | - |
| taskContent | string | 非必须 | - | 值日内容 | - |
| status | integer | 必须 | - | 0 未完成，1 已完成，2 请假，3 已换班 | format: int32 |

> `pendingSwapCount` 用于在首页提示「有 N 条换班申请待你处理」，避免对方发起后无人响应。

#### 13.2.3 值日日历（月视图，7-03）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-03 |
| 接口名称 | 值日日历（月视图） |
| Path | `/user/duty/schedule/calendar` |
| Method | `GET` |
| 接口描述 | 按月返回值日排布，供前端渲染日历视图 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| month | string | 非必须 | 当前月份 | 查询月份，格式 yyyy-MM | - |
| planId | integer | 非必须 | 最新已发布计划 | 排班计划 ID | format: int64 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| month | string | 必须 | - | 查询月份，格式 yyyy-MM | - |
| planId | integer | 非必须 | - | 排班计划 ID | format: int64 |
| planTitle | string | 非必须 | - | 计划标题 | - |
| days | array | 必须 | [] | 日期数组，结构见下表 | - |

`days` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| date | string | 必须 | - | 日期，格式 yyyy-MM-dd | - |
| weekday | integer | 必须 | - | 星期几：1 周一 ~ 7 周日 | format: int32 |
| isMine | integer | 必须 | 0 | 当日是否由当前用户值日 | format: int32 |
| duties | array | 必须 | [] | 当日值日安排，元素含 `scheduleId`、`userId`、`userName`、`groupName`、`taskContent`、`status` | - |

> **无值日的日期同样返回**（`duties = []`），保证前端日历网格完整、不断行。

#### 13.2.4 值日完成确认（7-04）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-04 |
| 接口名称 | 值日完成确认 |
| Path | `/user/duty/schedule/{id}/confirm` |
| Method | `PUT` |
| 接口描述 | 值日人确认自己已完成当日值日 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 值日安排 ID | 176 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**：`data` 为 `null`。

**错误码**：70006 值日安排不存在；70007 只能确认本人的值日；70008 该值日已完成确认。

> **不能代他人确认**：接口校验 `duty_schedule.user_id` 是否为当前用户。管理员如需代确认，使用接口 `7-18` 变更状态。
>
> **确认后状态置为「已完成」**并写入 `confirm_time`。已进入「已换班」或「请假」状态的条目不允许确认。

### 13.3 前台：换班（7-05 ~ 7-07）

#### 13.3.1 发起换班申请（7-05）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-05 |
| 接口名称 | 发起换班申请 |
| Path | `/user/duty/swap` |
| Method | `POST` |
| 接口描述 | 成员向另一位成员发起换班申请，需对方同意即生效 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| scheduleId | integer | 必须 | - | 申请方自己的值日安排 ID | format: int64 |
| targetUserId | integer | 必须 | - | 换班对象（希望接替的人）的用户 ID | format: int64 |
| targetScheduleId | integer | 非必须 | - | 对调的目标值日安排 ID；不传表示「请对方单向代我值日」 | format: int64 |
| reason | string | 非必须 | - | 换班理由，最长 500 字 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| swapId | integer | 必须 | - | 换班申请 ID | format: int64 |

**错误码**：70006 值日安排不存在；70010 不能与自己换班；70011 目标值日不存在或不属于对方。

> **两种换班语义**：
> 1. **单向代值**（不传 `targetScheduleId`）：请对方替自己完成这次值日，对方不需要有值日；
> 2. **双向对调**（传 `targetScheduleId`）：两人互换两天的值日，需校验该条目确实属于对方。
>
> 请求方的值日条目必须仍是「未完成」状态，否则无法发起。

#### 13.3.2 我的换班申请分页（7-06）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-06 |
| 接口名称 | 我的换班申请分页 |
| Path | `/user/duty/swap/my/page` |
| Method | `GET` |
| 接口描述 | 分页查询当前用户**发起或收到**的换班申请 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| role | integer | 非必须 | 0 | 查询视角：0 全部（我发起 + 我收到） 1 我发起的 2 我收到的 | format: int32 |
| status | integer | 非必须 | - | 0 待确认，1 已同意，2 已拒绝，3 已取消 | format: int32 |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| swapId | integer | 必须 | - | 换班申请 ID | format: int64 |
| scheduleId | integer | 必须 | - | 申请方的值日安排 ID | format: int64 |
| applyUserId | integer | 必须 | - | 申请人 ID | format: int64 |
| applyUserName | string | 必须 | - | 申请人姓名 | - |
| applyDutyDate | string | 必须 | - | 申请方的值日日期 | - |
| targetUserId | integer | 必须 | - | 换班对象 ID | format: int64 |
| targetUserName | string | 必须 | - | 换班对象姓名 | - |
| targetScheduleId | integer | 非必须 | - | 对调的目标值日安排 ID；为空表示单向代值 | format: int64 |
| targetDutyDate | string | 非必须 | - | 对调方的值日日期 | - |
| reason | string | 非必须 | - | 换班理由 | - |
| status | integer | 必须 | - | 0 待确认，1 已同意，2 已拒绝，3 已取消 | format: int32 |
| isTarget | integer | 必须 | 0 | 当前用户是否为换班对象：0 否 1 是（决定是否展示「同意 / 拒绝」按钮） | format: int32 |
| handleTime | string | 非必须 | - | 处理时间 | - |
| handleRemark | string | 非必须 | - | 处理意见 | - |
| createTime | string | 必须 | - | 发起时间 | - |

#### 13.3.3 换班处理（同意 / 拒绝，7-07）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-07 |
| 接口名称 | 换班处理（同意 / 拒绝） |
| Path | `/user/duty/swap/{id}/handle` |
| Method | `PUT` |
| 接口描述 | 换班对象处理收到的申请；同意后换班立即生效 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 换班申请 ID | 34 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| handleStatus | integer | 必须 | - | 1 同意，2 拒绝 | format: int32 |
| handleRemark | string | 非必须 | - | 处理意见 | - |

**返回数据**：`data` 为 `null`。

**错误码**：70009 换班申请不存在；70012 仅换班对象可处理该申请。

> **换班无需管理员审核**：与座位调换（模块五）不同，值日换班只要对方同意即可生效——值日属日常事务，当事人协商即可，不必占用管理员精力。
>
> **同意后同事务执行**：把原条目 `status` 置为「3 已换班」；若为双向对调，则交换两条 `duty_schedule` 的 `user_id`；若为单向代值，则为对方新建一条同日期的值日条目（`group_name` 沿用原分组）。
>
> **只能自己处理**：接口校验当前用户是否为该申请的 `targetUserId`，否则返回 `70012`。

### 13.4 后台：排班计划（7-08 ~ 7-13）

#### 13.4.1 排班计划分页（后台，7-08）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-08 |
| 接口名称 | 排班计划分页（后台） |
| Path | `/admin/duty/plan/page` |
| Method | `GET` |
| 接口描述 | 管理端分页查询排班计划（含草稿与已结束） |
| 权限码 | `duty:plan:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| keyword | string | 非必须 | - | 模糊匹配计划标题 | - |
| planType | integer | 非必须 | - | 1 按周轮转，2 分组轮转，3 手工指定 | format: int32 |
| status | integer | 非必须 | - | 0 草稿，1 已发布，2 已结束 | format: int32 |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| planId | integer | 必须 | - | 排班计划 ID | format: int64 |
| title | string | 必须 | - | 计划标题 | - |
| planType | integer | 必须 | - | 1 按周轮转，2 分组轮转，3 手工指定 | format: int32 |
| startDate | string | 必须 | - | 计划开始日期 | - |
| endDate | string | 必须 | - | 计划结束日期 | - |
| memberCount | integer | 必须 | 0 | 参与轮转的成员数 | format: int32 |
| groupCount | integer | 必须 | 0 | 分组数（`planType = 2` 时有值） | format: int32 |
| scheduleCount | integer | 必须 | 0 | 已生成的值日条目数 | format: int32 |
| completedCount | integer | 必须 | 0 | 已完成的条目数 | format: int32 |
| status | integer | 必须 | - | 0 草稿，1 已发布，2 已结束 | format: int32 |
| publishTime | string | 非必须 | - | 发布时间 | - |
| remark | string | 非必须 | - | 备注 | - |
| createTime | string | 必须 | - | 创建时间 | - |

#### 13.4.2 新增排班计划（7-09）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-09 |
| 接口名称 | 新增排班计划 |
| Path | `/admin/duty/plan` |
| Method | `POST` |
| 接口描述 | 创建排班计划，指定轮转方式、周期与参与成员 |
| 权限码 | `duty:plan:add` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| title | string | 必须 | - | 计划标题，最长 100 字 | - |
| planType | integer | 非必须 | 2 | 1 按周轮转，2 分组轮转，3 手工指定 | format: int32 |
| startDate | string | 必须 | - | 计划开始日期，格式 yyyy-MM-dd | - |
| endDate | string | 必须 | - | 计划结束日期，需晚于开始日期 | - |
| memberIds | array | 必须 | - | 参与轮转的成员 ID 数组，至少一人 | - |
| groupConfig | array | 非必须 | [] | 分组配置，`planType = 2` 时必填；元素含 `name`、`memberIds` | - |
| taskContent | string | 非必须 | - | 默认值日内容，如「扫地 + 倒垃圾 + 关窗断电」 | - |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| planId | integer | 必须 | - | 新增计划 ID | format: int64 |

**错误码**：70003 计划起止日期不合法；70004 参与轮转的成员为空，无法生成排班。

> **保存为 JSON 字段**：`memberIds` 与 `groupConfig` 在 `duty_plan` 表中以 JSON 类型存储，读写需配 `JacksonTypeHandler`（见《数据库设计》1.6 节）。
>
> **v1 默认分组轮转**：`planType` 默认 2。按周轮转（1）与手工指定（3）作为备选方式保留。

#### 13.4.3 修改排班计划（7-10）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-10 |
| 接口名称 | 修改排班计划 |
| Path | `/admin/duty/plan` |
| Method | `PUT` |
| 接口描述 | 修改排班计划的轮转方式、周期、参与成员与值日内容 |
| 权限码 | `duty:plan:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| planId | integer | 必须 | - | 排班计划 ID | format: int64 |
| title | string | 必须 | - | 计划标题，最长 100 字 | - |
| planType | integer | 非必须 | 2 | 1 按周轮转，2 分组轮转，3 手工指定 | format: int32 |
| startDate | string | 必须 | - | 计划开始日期，格式 yyyy-MM-dd | - |
| endDate | string | 必须 | - | 计划结束日期，需晚于开始日期 | - |
| memberIds | array | 必须 | - | 参与轮转的成员 ID 数组，至少一人 | - |
| groupConfig | array | 非必须 | [] | 分组配置，`planType = 2` 时必填；元素含 `name`、`memberIds` | - |
| taskContent | string | 非必须 | - | 默认值日内容 | - |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**：`data` 为 `null`。

**错误码**：70001 排班计划不存在；70003 计划起止日期不合法。

> **修改成员或分组后需重新生成**：计划信息变更不会自动重排，管理员需再次调用接口 `7-12` 重新生成值日条目。已发布计划建议先取消发布（`7-13`）再调整，避免成员端看到频繁变动的排班。

#### 13.4.4 删除排班计划（7-11）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-11 |
| 接口名称 | 删除排班计划 |
| Path | `/admin/duty/plan/{id}` |
| Method | `DELETE` |
| 接口描述 | 逻辑删除计划，并连带逻辑删除其下的值日条目与换班申请 |
| 权限码 | `duty:plan:remove` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 排班计划 ID | 4 |

**返回数据**：`data` 为 `null`。

**错误码**：70001 排班计划不存在。

> **已发布计划不建议删除**：若计划已发布且成员已确认过部分值日，删除会让历史记录消失。建议改用 `7-13` 取消发布或 `7-10` 把结束日期提前，保留历史。

#### 13.4.5 自动生成排班（7-12）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-12 |
| 接口名称 | 自动生成排班 |
| Path | `/admin/duty/plan/{id}/generate` |
| Method | `POST` |
| 接口描述 | 按计划配置自动生成周期内每日的值日安排 |
| 权限码 | `duty:plan:generate` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 排班计划 ID | 4 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| overwrite | integer | 非必须 | 0 | 是否覆盖已存在的条目：0 不覆盖（仅补空缺） 1 覆盖重建 | format: int32 |
| skipWeekend | integer | 非必须 | 1 | 是否跳过周末：0 不跳过 1 跳过周六周日 | format: int32 |
| perDayCount | integer | 非必须 | 1 | 每天值日人数 | format: int32 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| planId | integer | 必须 | - | 排班计划 ID | format: int64 |
| generatedCount | integer | 必须 | - | 本次生成的值日条目数 | format: int32 |
| skippedCount | integer | 必须 | - | 因已存在而跳过的条目数（`overwrite = 0` 时） | format: int32 |
| dateRange | string | 必须 | - | 生成的日期范围，如 `2026-09-23 ~ 2026-12-31` | - |

**错误码**：70001 排班计划不存在；70004 参与轮转的成员为空，无法生成排班。

**排班算法（v1）**

| 方式 | 规则 |
| --- | --- |
| 1 按周轮转 | 以自然周为单位，每周换一位成员；周内每天由该成员值日 |
| 2 分组轮转（默认） | 按 `groupConfig` 的分组顺序逐日轮转，一组一天；组内 `perDayCount` 人 |
| 3 手工指定 | 不自动生成，由管理员逐条登记（接口 `7-14`） |

> **幂等保护**：`overwrite = 0` 时只补空缺日期，不会重复创建（依赖 `(plan_id, duty_date, user_id)` 唯一索引）。
>
> **`overwrite = 1` 会重建全部条目**，包括已完成确认的记录——请仅在计划尚未发布时使用。
>
> **成员变动后的重新生成**：成员名单变化后重新调用本接口（`overwrite = 1`），但建议先取消发布；已完成的历史条目会丢失确认状态，必要时可先导出留档。

#### 13.4.6 发布 / 取消发布（7-13）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-13 |
| 接口名称 | 发布 / 取消发布 |
| Path | `/admin/duty/plan/{id}/publish` |
| Method | `PUT` |
| 接口描述 | 控制计划是否对成员端公示；发布后成员才能看到排班 |
| 权限码 | `duty:plan:publish` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 排班计划 ID | 4 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| publish | integer | 必须 | - | 0 取消发布，1 发布 | format: int32 |

**返回数据**：`data` 为 `null`。

**错误码**：70001 排班计划不存在；70002 该计划已发布，不可重复发布；70005 该计划尚无值日安排，无法发布。

**状态流转**

| 操作 | 前置校验 | 结果 |
| --- | --- | --- |
| 发布 | 至少存在一条值日条目 | `status = 1 已发布`，写入 `publish_time` |
| 取消发布 | 无 | `status = 0 草稿`，`publish_time` 保留 |

> **没排班不允许发布**：计划下没有任何值日条目时发布会被拒绝（`70005`），避免成员端看到空计划。
>
> **取消发布保留 `publish_time`**：便于追溯首次发布时间，也便于判断该计划是否曾对外可见。

### 13.5 后台：值日条目（7-14 ~ 7-18）

#### 13.5.1 新增值日条目（7-14）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-14 |
| 接口名称 | 新增值日条目 |
| Path | `/admin/duty/schedule` |
| Method | `POST` |
| 接口描述 | 手工登记一条值日安排，用于补排、临时调整或 `planType = 3 手工指定` 的场景 |
| 权限码 | `duty:schedule:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| planId | integer | 必须 | - | 所属排班计划 ID | format: int64 |
| dutyDate | string | 必须 | - | 值日日期，格式 yyyy-MM-dd | - |
| userId | integer | 必须 | - | 值日人 ID | format: int64 |
| groupName | string | 非必须 | - | 分组名称 | - |
| taskContent | string | 非必须 | - | 值日内容；不传则取计划的默认值日内容 | - |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| scheduleId | integer | 必须 | - | 新增值日条目 ID | format: int64 |

**错误码**：70001 排班计划不存在；70014 该成员当日已有值日安排。

> **同一人同一天不可重复排班**：`(planId, dutyDate, userId)` 建了组合唯一索引，重复提交会被拒绝。若同一天需要多人值日，分别用不同 `userId` 各建一条。

#### 13.5.2 修改值日条目（7-15）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-15 |
| 接口名称 | 修改值日条目 |
| Path | `/admin/duty/schedule` |
| Method | `PUT` |
| 接口描述 | 修改值日人、日期、内容或备注 |
| 权限码 | `duty:schedule:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| scheduleId | integer | 必须 | - | 值日条目 ID | format: int64 |
| dutyDate | string | 必须 | - | 值日日期，格式 yyyy-MM-dd | - |
| userId | integer | 必须 | - | 值日人 ID | format: int64 |
| groupName | string | 非必须 | - | 分组名称 | - |
| taskContent | string | 非必须 | - | 值日内容 | - |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**：`data` 为 `null`。

**错误码**：70006 值日安排不存在。

> **本接口不改完成状态**：状态变更见 `7-18`。这样「调整排班」与「变更完成情况」两件事互不干扰。

#### 13.5.3 删除值日条目（7-16）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-16 |
| 接口名称 | 删除值日条目 |
| Path | `/admin/duty/schedule/{id}` |
| Method | `DELETE` |
| 接口描述 | 逻辑删除一条值日安排 |
| 权限码 | `duty:schedule:edit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 值日条目 ID | 176 |

**返回数据**：`data` 为 `null`。

**错误码**：70006 值日安排不存在。

#### 13.5.4 值日安排分页（后台，7-17）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-17 |
| 接口名称 | 值日安排分页（后台） |
| Path | `/admin/duty/schedule/page` |
| Method | `GET` |
| 接口描述 | 管理端分页查询值日安排，支持更多筛选维度与全局视图 |
| 权限码 | `duty:schedule:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| planId | integer | 非必须 | - | 限定排班计划 | format: int64 |
| userId | integer | 非必须 | - | 限定值日人 | format: int64 |
| groupName | string | 非必须 | - | 分组名称筛选 | - |
| status | integer | 非必须 | - | 0 未完成，1 已完成，2 请假，3 已换班 | format: int32 |
| keyword | string | 非必须 | - | 模糊匹配值日人姓名 | - |
| beginDate | string | 非必须 | - | 值日日期起 | - |
| endDate | string | 非必须 | - | 值日日期止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| scheduleId | integer | 必须 | - | 值日安排 ID | format: int64 |
| planId | integer | 必须 | - | 所属计划 ID | format: int64 |
| planTitle | string | 必须 | - | 计划标题 | - |
| dutyDate | string | 必须 | - | 值日日期 | - |
| userId | integer | 必须 | - | 值日人 ID | format: int64 |
| userName | string | 必须 | - | 值日人姓名 | - |
| grade | string | 非必须 | - | 年级 | - |
| groupName | string | 非必须 | - | 分组名称 | - |
| taskContent | string | 非必须 | - | 值日内容 | - |
| status | integer | 必须 | - | 0 未完成，1 已完成，2 请假，3 已换班 | format: int32 |
| confirmTime | string | 非必须 | - | 完成确认时间 | - |
| remark | string | 非必须 | - | 备注 | - |
| swapCount | integer | 必须 | 0 | 该条目的换班次数 | format: int32 |

#### 13.5.5 值日状态变更（7-18）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-18 |
| 接口名称 | 值日状态变更 |
| Path | `/admin/duty/schedule/{id}/status` |
| Method | `PUT` |
| 接口描述 | 管理员变更值日完成状态，用于代确认、登记请假、纠正误操作 |
| 权限码 | `duty:schedule:edit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 值日条目 ID | 176 |

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 必须 | - | 目标状态：0 未完成，1 已完成，2 请假 | format: int32 |
| remark | string | 非必须 | - | 变更原因，如「已请假，另行补值」 | - |

**返回数据**：`data` 为 `null`。

**错误码**：70006 值日安排不存在；70008 该值日已完成确认。

**状态变更规则**

| 目标状态 | 允许的来源状态 | 说明 |
| --- | --- | --- |
| 1 已完成 | 0 未完成、2 请假 | 管理员代确认，写入 `confirm_time` |
| 2 请假 | 0 未完成 | 登记请假 |
| 0 未完成 | 1 已完成、2 请假 | 纠正误操作，清空 `confirm_time` |
| 3 已换班 | 不允许手动设置 | 只由换班生效（`7-07`）自动置位 |

> **「已换班」不接受手工设置**：该状态由换班流程自动写入，手工设置会与 `duty_swap` 记录脱节。

### 13.6 后台：统计与提醒（7-19 ~ 7-21）

#### 13.6.1 值日统计看板（7-19）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-19 |
| 接口名称 | 值日统计看板 |
| Path | `/admin/duty/stat/overview` |
| Method | `GET` |
| 接口描述 | 返回值日执行情况统计，用于管理端查看谁在认真值日 |
| 权限码 | `duty:stat:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| planId | integer | 非必须 | 最新已发布计划 | 排班计划 ID | format: int64 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| planId | integer | 必须 | - | 排班计划 ID | format: int64 |
| planTitle | string | 必须 | - | 计划标题 | - |
| totalCount | integer | 必须 | 0 | 值日条目总数 | format: int32 |
| completedCount | integer | 必须 | 0 | 已完成数 | format: int32 |
| unfinishedCount | integer | 必须 | 0 | 未完成数（含已过期的） | format: int32 |
| leaveCount | integer | 必须 | 0 | 请假数 | format: int32 |
| swappedCount | integer | 必须 | 0 | 已换班数 | format: int32 |
| completionRate | number | 必须 | 0.00 | 完成率（百分比数值） | - |
| overdueCount | integer | 必须 | 0 | 已过期仍未完成的数量（`dutyDate < 今天` 且 `status = 0`） | format: int32 |
| memberStats | array | 必须 | [] | 成员维度统计，结构见下表 | - |
| groupStats | array | 非必须 | [] | 分组维度统计，元素含 `groupName`、`totalCount`、`completedCount`、`completionRate` | - |

`memberStats` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| userId | integer | 必须 | - | 成员用户 ID | format: int64 |
| userName | string | 必须 | - | 成员姓名 | - |
| groupName | string | 非必须 | - | 所属分组 | - |
| totalCount | integer | 必须 | 0 | 应值日次数 | format: int32 |
| completedCount | integer | 必须 | 0 | 已完成次数 | format: int32 |
| overdueCount | integer | 必须 | 0 | 逾期未完成次数 | format: int32 |
| completionRate | number | 必须 | 0.00 | 完成率（百分比数值） | - |

> `overdueCount` 是管理员最该关注的指标——它反映「排了班但没人做」的情况。前端建议把完成率低于 60% 或逾期次数大于 0 的成员标红。

#### 13.6.2 提醒配置查询（7-20）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-20 |
| 接口名称 | 提醒配置查询 |
| Path | `/admin/duty/reminder` |
| Method | `GET` |
| 接口描述 | 查询值日提醒配置；支持全局配置与按计划配置 |
| 权限码 | `duty:reminder:config` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| planId | integer | 非必须 | - | 查询指定计划的配置；不传则查询全局配置 | format: int64 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| configId | integer | 非必须 | null | 配置 ID；尚未创建时为 null | format: int64 |
| planId | integer | 非必须 | null | 所属排班计划 ID；null 表示全局配置 | format: int64 |
| aheadMinutes | integer | 必须 | 30 | 提前提醒分钟数 | format: int32 |
| channel | integer | 必须 | 1 | 提醒渠道：1 站内消息 | format: int32 |
| enabled | integer | 必须 | 1 | 是否启用：0 停用 1 启用 | format: int32 |
| isGlobal | integer | 必须 | 0 | 是否来自全局配置：0 否 1 是 | format: int32 |

> **配置优先级**：查询 `planId` 对应的专属配置；若不存在，则回退返回全局配置并把 `isGlobal` 置 1。这样前端只调一次就能拿到生效值。

#### 13.6.3 提醒配置保存（7-21）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 7-21 |
| 接口名称 | 提醒配置保存 |
| Path | `/admin/duty/reminder` |
| Method | `PUT` |
| 接口描述 | 保存值日提醒配置；存在则更新，不存在则创建 |
| 权限码 | `duty:reminder:config` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| planId | integer | 非必须 | null | 所属排班计划 ID；不传或传 null 表示保存全局配置 | format: int64 |
| aheadMinutes | integer | 必须 | - | 提前提醒分钟数，范围 5~1440 | format: int32 |
| channel | integer | 非必须 | 1 | 提醒渠道：1 站内消息 | format: int32 |
| enabled | integer | 非必须 | 1 | 是否启用：0 停用 1 启用 | format: int32 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| configId | integer | 必须 | - | 配置 ID | format: int64 |

**错误码**：70001 排班计划不存在。

> **v1 提醒渠道仅站内消息**：`channel` 字段已按枚举设计，邮件与微信推送列入 v1.1。届时只需扩展 `channel` 取值，接口结构不变。定时任务生成的提醒以站内消息落库，成员用接口 `8-20` ~ `8-22`（见 14.8 节）查看与标记已读。
>
> **提醒的去重**：定时任务按「计划 + 日期 + 接收人」判断是否已生成过提醒（依赖 `sys_message` 的 `biz_type` + `biz_id`），避免重复推送。详见《数据库设计》3.8.6 节。

---

## 第 14 章 模块八：通用支撑（common）

### 14.1 模块概述

- **职责**：文件上传与访问、数据字典、图形验证码、健康检查、公告、首页与后台看板、操作日志、视频资源登记、站内消息，为其余 7 个模块提供通用能力。
- **依赖关系**：文件接口（8-01）被预算凭证、资产图片、学长学姐头像、平面图底图、荣誉证书等复用，**建议最先实现**。
- **数据模型**：见 5.8 节；视频接入方式见 5.9 节。
- **文件存储约定**：阿里云 OSS（对象存储），对象键为 `upload/{bizType}/{yyyy}/{MM}/{uuid}.{ext}`，Bucket 公共读；上传链路为**后端中转**（前端 → 平台后端 → OSS）。`sys_file` 保存元信息（含 `object_key`、`bucket`），业务表只保存 `fileId` 与 `url`。完整约定见 2.9 节。
- **接口清单**：见 4.9 节（`8-01` ~ `8-22`，共 22 个接口）。

> **编号说明**：本模块接口编号为连续的 `8-01` ~ `8-22`，一经确定不随章节调整而变化（编号规则见 1.2 节）；V1.0 的编号调整记录见 4.9 节。

### 14.2 文件管理（8-01 ~ 8-03）

#### 14.2.1 单文件上传（8-01）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-01 |
| 接口名称 | 单文件上传 |
| Path | `/user/file/upload` |
| Method | `POST` |
| 接口描述 | 上传单个文件，返回文件信息；业务表只保存返回的 fileId 与 url |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Headers：

| 参数名称 | 参数值 | 是否必须 | 说明 |
| --- | --- | --- | --- |
| Content-Type | multipart/form-data | 必须 | 不可使用 application/json |

Body 参数（form-data）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| file | file | 必须 | - | 待上传文件 | 图片 ≤ 5MB，文档 ≤ 20MB |
| bizType | string | 非必须 | common | 业务归属：asset / honor / budget / seat / notice / video / avatar | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| fileId | integer | 必须 | - | 文件主键 | format: int64 |
| name | string | 必须 | - | 原始文件名 | - |
| url | string | 必须 | - | 访问地址（OSS 完整 URL，Bucket 公共读） | - |
| size | integer | 必须 | - | 文件大小（字节） | format: int64 |
| contentType | string | 非必须 | - | MIME 类型 | - |
| bizType | string | 非必须 | - | 业务归属 | - |

**错误码**：80001 上传文件为空；80002 文件类型不支持；80003 文件大小超出限制。

> **`url` 是 OSS 完整地址**：形如 `https://{bucket}.{endpoint}/upload/{bizType}/{yyyy}/{MM}/{uuid}.{ext}`，Bucket 公共读，前端可直接用于 `<img>` / 下载；示例中的 `iecmp-files.oss-cn-hangzhou.aliyuncs.com` 为占位，实际以部署配置为准。对象键 `object_key` 与存储桶 `bucket` 只落在 `sys_file`，不返回给前端。

**响应示例**

```json
{
  "code": 200,
  "data": {
    "fileId": 101,
    "name": "plan.png",
    "url": "https://iecmp-files.oss-cn-hangzhou.aliyuncs.com/upload/seat/2026/09/9f2c1a7b.png",
    "size": 204800,
    "contentType": "image/png",
    "bizType": "seat"
  },
  "msg": "success"
}
```

#### 14.2.2 多文件上传（8-02）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-02 |
| 接口名称 | 多文件上传 |
| Path | `/user/file/upload/batch` |
| Method | `POST` |
| 接口描述 | 一次上传多个文件，用于资产图片、荣誉证书等场景 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Headers：

| 参数名称 | 参数值 | 是否必须 | 说明 |
| --- | --- | --- | --- |
| Content-Type | multipart/form-data | 必须 | 不可使用 application/json |

Body 参数（form-data）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| files | file[] | 必须 | - | 待上传文件列表，单次最多 9 个 | - |
| bizType | string | 非必须 | common | 业务归属 | - |

**返回数据**

`data` 为文件信息对象数组，每个元素的结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| fileId | integer | 必须 | - | 文件主键 | format: int64 |
| name | string | 必须 | - | 原始文件名 | - |
| url | string | 必须 | - | 访问地址（OSS 完整 URL） | - |
| size | integer | 必须 | - | 文件大小（字节） | format: int64 |
| contentType | string | 非必须 | - | MIME 类型 | - |
| bizType | string | 非必须 | - | 业务归属 | - |

数组元素顺序与上传时的 `files` 顺序一致，前端可按序对应。

**错误码**：80001 上传文件为空；80002 文件类型不支持；80003 文件大小超出限制；单次超过 9 个文件返回通用错误码 400。

#### 14.2.3 文件删除（8-03）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-03 |
| 接口名称 | 文件删除 |
| Path | `/user/file/{id}` |
| Method | `DELETE` |
| 接口描述 | 删除文件记录与物理文件，仅允许删除本人上传且未被业务引用的文件 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 文件 ID | 101 |

**返回数据**：`data` 为 `null`。

**错误码**：80004 文件不存在；80005 文件已被业务引用，无法删除；80006 只能删除本人上传的文件。

### 14.3 基础能力（8-04、8-13、8-14）

#### 14.3.1 数据字典查询（8-04）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-04 |
| 接口名称 | 数据字典查询 |
| Path | `/common/dict/{type}` |
| Method | `GET` |
| 接口描述 | 按字典类型返回枚举列表，前端下拉统一走此接口，避免在各页面硬编码枚举 |
| 权限码 | 无（公开接口） |
| 是否需令牌 | 否 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| type | string | 必须 | 字典类型：assetStatus / assetCategory / budgetCategory / budgetApplyStatus / seatStatus / seatType / dutyStatus / honorLevel / videoBizType | assetStatus |

**返回数据**

`data` 为数组，元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| label | string | 必须 | - | 展示文案，如「在用」 | - |
| value | integer | 必须 | - | 枚举值，如 1 | format: int32 |

> **说明**：字典结果在 Redis 中缓存 1 小时，枚举变更时由后端主动清理缓存。

#### 14.3.2 图形验证码（8-13）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-13 |
| 接口名称 | 图形验证码 |
| Path | `/common/captcha` |
| Method | `GET` |
| 接口描述 | 获取图形验证码，用于登录与注册申请，防机器提交 |
| 权限码 | 无（公开接口） |
| 是否需令牌 | 否 |

**请求参数**：无。

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| captchaKey | string | 必须 | - | 验证码标识，提交表单时回传 | - |
| captchaImage | string | 必须 | - | 验证码图片，Base64 字符串（含 `data:image/png;base64` 前缀） | - |

> **说明**：验证码有效期 5 分钟且一次性使用；同一 IP 每分钟最多获取 10 次。

#### 14.3.3 健康检查（8-14）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-14 |
| 接口名称 | 健康检查 |
| Path | `/common/health` |
| Method | `GET` |
| 接口描述 | 供 FRP、运维脚本与前端探活使用，返回服务与依赖状态 |
| 权限码 | 无（公开接口） |
| 是否需令牌 | 否 |

**请求参数**：无。

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | string | 必须 | - | UP / DOWN | - |
| version | string | 非必须 | - | 服务版本号 | - |
| time | string | 必须 | - | 服务器当前时间 | - |
| mysql | string | 非必须 | - | 数据库连通状态 | - |
| redis | string | 非必须 | - | 缓存连通状态 | - |

### 14.4 公告（8-05 ~ 8-09）

#### 14.4.1 公告分页（前台，8-05）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-05 |
| 接口名称 | 公告分页（前台） |
| Path | `/user/notice/page` |
| Method | `GET` |
| 接口描述 | 成员端查询已发布公告，置顶优先，用于首页与公告列表页 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| noticeType | integer | 非必须 | - | 1 中心公告，2 制度，3 活动，4 其他 | format: int32 |
| keyword | string | 非必须 | - | 模糊匹配标题 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；仅返回 `status = 1` 且 `publishTime <= 当前时间` 的记录，排序为 `is_top desc, publish_time desc`。`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| noticeId | integer | 必须 | - | 公告 ID | format: int64 |
| title | string | 必须 | - | 公告标题 | - |
| content | string | 必须 | - | 公告内容（富文本 HTML） | - |
| noticeType | integer | 必须 | - | 公告类型 | format: int32 |
| isTop | integer | 必须 | - | 0 普通，1 置顶 | format: int32 |
| publishTime | string | 必须 | - | 发布时间 | - |

> **说明**：公告内容量小，列表直接返回富文本 `content`，不再单独提供详情接口。

#### 14.4.2 公告分页（后台，8-06）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-06 |
| 接口名称 | 公告分页（后台） |
| Path | `/admin/notice/page` |
| Method | `GET` |
| 接口描述 | 后台查询全部公告（含草稿、已下架），用于管理列表 |
| 权限码 | `common:notice:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| status | integer | 非必须 | - | 0 草稿，1 已发布，2 已下架 | format: int32 |
| noticeType | integer | 非必须 | - | 公告类型 | format: int32 |
| beginTime | string | 非必须 | - | 创建时间起 | - |
| endTime | string | 非必须 | - | 创建时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| noticeId | integer | 必须 | - | 公告 ID | format: int64 |
| title | string | 必须 | - | 公告标题 | - |
| content | string | 必须 | - | 公告内容（富文本 HTML） | - |
| noticeType | integer | 必须 | - | 公告类型 | format: int32 |
| isTop | integer | 必须 | - | 0 普通，1 置顶 | format: int32 |
| publishTime | string | 必须 | - | 发布时间 | - |
| status | integer | 必须 | - | 0 草稿，1 已发布，2 已下架 | format: int32 |
| createBy | string | 非必须 | - | 创建人姓名 | - |
| createTime | string | 必须 | - | 创建时间 | - |

#### 14.4.3 公告新增（8-07）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-07 |
| 接口名称 | 公告新增 |
| Path | `/admin/notice` |
| Method | `POST` |
| 接口描述 | 新增公告，可立即发布或定时发布 |
| 权限码 | `common:notice:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| title | string | 必须 | - | 标题，最长 100 字 | - |
| content | string | 必须 | - | 内容，富文本 HTML | - |
| noticeType | integer | 非必须 | 1 | 1 中心公告，2 制度，3 活动，4 其他 | format: int32 |
| isTop | integer | 非必须 | 0 | 0 普通，1 置顶 | format: int32 |
| status | integer | 非必须 | 1 | 0 草稿，1 立即发布 | format: int32 |
| publishTime | string | 非必须 | 当前时间 | 定时发布时间，需晚于当前时间 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| noticeId | integer | 必须 | - | 新增公告 ID | format: int64 |

**错误码**：80009 定时发布时间必须晚于当前时间。

#### 14.4.4 公告修改（8-08）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-08 |
| 接口名称 | 公告修改 |
| Path | `/admin/notice` |
| Method | `PUT` |
| 接口描述 | 修改公告信息，字段同新增，另可下架（`status = 2`） |
| 权限码 | `common:notice:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| noticeId | integer | 必须 | - | 公告 ID | format: int64 |
| title | string | 必须 | - | 标题，最长 100 字 | - |
| content | string | 必须 | - | 内容，富文本 HTML | - |
| noticeType | integer | 非必须 | 1 | 1 中心公告，2 制度，3 活动，4 其他 | format: int32 |
| isTop | integer | 非必须 | 0 | 0 普通，1 置顶 | format: int32 |
| status | integer | 非必须 | - | 0 草稿，1 已发布，2 已下架（传 2 即下架） | format: int32 |
| publishTime | string | 非必须 | - | 发布时间，需晚于当前时间 | - |

**返回数据**：`data` 为 `null`。

**错误码**：80007 公告不存在。

#### 14.4.5 公告删除（8-09）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-09 |
| 接口名称 | 公告删除 |
| Path | `/admin/notice/{id}` |
| Method | `DELETE` |
| 接口描述 | 逻辑删除公告 |
| 权限码 | `common:notice:edit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 公告 ID | 6 |

**返回数据**：`data` 为 `null`。

**错误码**：80007 公告不存在。

### 14.5 数据看板（8-10、8-11）

#### 14.5.1 前台首页看板（8-10）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-10 |
| 接口名称 | 前台首页看板 |
| Path | `/user/home/overview` |
| Method | `GET` |
| 接口描述 | 成员端首页聚合数据，一次请求返回首页需要的各模块摘要，避免前端多次调用 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**：无。

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| mySeat | object | 非必须 | null | 我的工位，含 `seatId`、`seatNo`、`areaName`；未分配时返回 null | - |
| nextDuty | object | 非必须 | null | 我的下次值日，含 `dutyDate`、`taskContent`、`groupName` | - |
| borrowingCount | integer | 必须 | 0 | 我当前借用中（未归还）的资产数量 | format: int32 |
| budgetApplyPending | integer | 必须 | 0 | 我待审核的预算申请数 | format: int32 |
| budgetApprovedAmount | number | 必须 | 0.00 | 我本年度已通过的申请总金额 | - |
| commitCountThisMonth | integer | 必须 | 0 | 我本月提交次数 | format: int32 |
| unreadMsgCount | integer | 必须 | 0 | 我的未读站内消息数（见 8-21） | format: int32 |
| topNotices | array | 非必须 | [] | 置顶公告列表，元素含 `noticeId`、`title`、`publishTime`，最多 5 条 | - |

#### 14.5.2 后台统计看板（8-11）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-11 |
| 接口名称 | 后台统计看板 |
| Path | `/admin/stat/dashboard` |
| Method | `GET` |
| 接口描述 | 管理端首页看板：核心统计 + 待办聚合，用于管理员快速掌握中心状态 |
| 权限码 | `common:stat:list` |
| 是否需令牌 | 是 |

**请求参数**：无。

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| userCount | integer | 必须 | - | 启用状态的成员数 | format: int32 |
| assetStat | object | 必须 | - | 资产统计，含 `total`、`inUse`、`idle`、`repair`、`scrapped` | - |
| budgetStat | object | 必须 | - | 当前执行周期统计，含 `periodTitle`、`totalAmount`、`usedAmount`、`executionRate` | - |
| todayDuty | array | 非必须 | [] | 今日值日人员，元素含 `userId`、`realName`、`groupName`、`status` | - |
| repoCommitCountThisMonth | integer | 必须 | 0 | 全中心本月提交次数 | format: int32 |
| pendingTodo | object | 必须 | - | 待办数量聚合，含 `budgetApply`、`assetBorrow`、`seatApply`、`seatSwap`、`honorAudit`、`registerApply` | - |

### 14.6 操作日志（8-12）

#### 14.6.1 操作日志分页（8-12）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-12 |
| 接口名称 | 操作日志分页 |
| Path | `/admin/log/page` |
| Method | `GET` |
| 接口描述 | 查询后台写操作日志，用于审计与问题回溯 |
| 权限码 | `system:log:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| module | string | 非必须 | - | 模块标识，如 system / budget / asset | - |
| operation | string | 非必须 | - | 操作类型，如 add / edit / remove / audit | - |
| operatorName | string | 非必须 | - | 操作人姓名，模糊匹配 | - |
| status | integer | 非必须 | - | 0 失败，1 成功 | format: int32 |
| beginTime | string | 非必须 | - | 操作时间起 | - |
| endTime | string | 非必须 | - | 操作时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| logId | integer | 必须 | - | 日志 ID | format: int64 |
| module | string | 必须 | - | 所属模块 | - |
| operation | string | 必须 | - | 操作类型 | - |
| requestUri | string | 必须 | - | 请求地址 | - |
| requestParam | string | 非必须 | - | 请求参数摘要，超长截断（≤ 2000 字符），敏感字段脱敏 | - |
| responseMsg | string | 非必须 | - | 返回信息摘要 | - |
| operatorId | integer | 非必须 | - | 操作人 ID | format: int64 |
| operatorName | string | 非必须 | - | 操作人姓名 | - |
| ip | string | 非必须 | - | 来源 IP | - |
| costMs | integer | 非必须 | - | 耗时（毫秒） | format: int64 |
| status | integer | 必须 | - | 0 失败，1 成功 | format: int32 |
| createTime | string | 必须 | - | 操作时间 | - |

> **说明**：日志保留策略建议保留 12 个月，超期由定时任务归档或清理；日志表数据量增长较快时可按月分表。

### 14.7 视频资源（8-15 ~ 8-19）

> 视频文件由 Jellyfin 托管并生成播放链接（接入流程见 5.9 节），平台侧只维护「链接 + 展示配置」，**不承担上传、转码与流量转发**。

#### 14.7.1 前台展示视频列表（8-15）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-15 |
| 接口名称 | 前台展示视频列表 |
| Path | `/user/video/list` |
| Method | `GET` |
| 接口描述 | 按业务归属拉取上架视频，用于首页与学长学姐展板的视频展示区 |
| 权限码 | 登录即可 |
| 是否需令牌 | 可选 |

**请求参数**

Query 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| bizType | integer | 非必须 | 2 | 1 首页展示，2 学长学姐展板，3 其他 | format: int32 |
| limit | integer | 非必须 | 20 | 返回条数上限，范围 1~50 | format: int32 |

**返回数据**

`data` 为数组，仅返回 `status = 1` 的视频，按 `sortNo` 升序；元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| videoId | integer | 必须 | - | 视频记录 ID | format: int64 |
| title | string | 必须 | - | 视频标题 | - |
| coverUrl | string | 非必须 | - | 封面图地址 | - |
| playUrl | string | 必须 | - | 播放地址（Jellyfin 生成，直链或 HLS 播放列表） | - |
| duration | integer | 非必须 | - | 时长（秒） | format: int32 |
| bizType | integer | 必须 | - | 业务归属 | format: int32 |

#### 14.7.2 视频分页（后台，8-16）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-16 |
| 接口名称 | 视频分页（后台） |
| Path | `/admin/video/page` |
| Method | `GET` |
| 接口描述 | 后台分页查询视频记录，含下架数据，便于维护播放链接 |
| 权限码 | `common:video:list` |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| bizType | integer | 非必须 | - | 业务归属 | format: int32 |
| status | integer | 非必须 | - | 0 下架，1 上架 | format: int32 |
| keyword | string | 非必须 | - | 模糊匹配标题 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| videoId | integer | 必须 | - | 视频记录 ID | format: int64 |
| title | string | 必须 | - | 视频标题 | - |
| coverUrl | string | 非必须 | - | 封面图地址 | - |
| playUrl | string | 必须 | - | 播放地址（Jellyfin 生成，直链或 HLS 播放列表） | - |
| duration | integer | 非必须 | - | 时长（秒） | format: int32 |
| bizType | integer | 必须 | - | 业务归属：1 首页展示，2 学长学姐展板，3 其他 | format: int32 |
| jellyfinItemId | string | 非必须 | - | Jellyfin 媒体项 ID，用于链接失效时重新拼接 | - |
| status | integer | 必须 | - | 0 下架，1 上架 | format: int32 |
| remark | string | 非必须 | - | 备注 | - |
| createTime | string | 必须 | - | 创建时间 | - |

#### 14.7.3 新增视频（8-17）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-17 |
| 接口名称 | 新增视频 |
| Path | `/admin/video` |
| Method | `POST` |
| 接口描述 | 登记一条视频：管理员在 Jellyfin 取得播放链接后填入平台 |
| 权限码 | `common:video:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| title | string | 必须 | - | 视频标题，最长 100 字 | - |
| playUrl | string | 必须 | - | 播放地址，必须以 `http://` 或 `https://` 开头 | - |
| bizType | integer | 必须 | 2 | 1 首页展示，2 学长学姐展板，3 其他 | format: int32 |
| coverUrl | string | 非必须 | - | 封面图地址，可先调用接口 8-01 上传后填入 | - |
| jellyfinItemId | string | 非必须 | - | Jellyfin 媒体项 ID，用于链接失效时重新拼接 | - |
| duration | integer | 非必须 | - | 时长（秒） | format: int32 |
| sortNo | integer | 非必须 | 0 | 展示排序，升序 | format: int32 |
| status | integer | 非必须 | 1 | 0 下架，1 上架 | format: int32 |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| videoId | integer | 必须 | - | 新增视频 ID | format: int64 |

**错误码**：80011 播放地址格式不正确（必须以 `http://` 或 `https://` 开头）。

#### 14.7.4 视频修改（8-18）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-18 |
| 接口名称 | 视频修改 |
| Path | `/admin/video` |
| Method | `PUT` |
| 接口描述 | 修改视频信息，常用于更换失效的播放链接、上下架与调整排序 |
| 权限码 | `common:video:edit` |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| videoId | integer | 必须 | - | 视频记录 ID | format: int64 |
| title | string | 必须 | - | 视频标题，最长 100 字 | - |
| playUrl | string | 必须 | - | 播放地址，必须以 `http://` 或 `https://` 开头 | - |
| bizType | integer | 必须 | 2 | 1 首页展示，2 学长学姐展板，3 其他 | format: int32 |
| coverUrl | string | 非必须 | - | 封面图地址，可先调用接口 8-01 上传后填入 | - |
| jellyfinItemId | string | 非必须 | - | Jellyfin 媒体项 ID，用于链接失效时重新拼接 | - |
| duration | integer | 非必须 | - | 时长（秒） | format: int32 |
| sortNo | integer | 非必须 | 0 | 展示排序，升序 | format: int32 |
| status | integer | 非必须 | 1 | 0 下架，1 上架 | format: int32 |
| remark | string | 非必须 | - | 备注 | - |

**返回数据**：`data` 为 `null`。

**错误码**：80010 视频记录不存在；80011 播放地址格式不正确。

#### 14.7.5 视频删除（8-19）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-19 |
| 接口名称 | 视频删除 |
| Path | `/admin/video/{id}` |
| Method | `DELETE` |
| 接口描述 | 删除视频记录；仅删除平台登记信息，不删除 Jellyfin 中的媒体文件 |
| 权限码 | `common:video:edit` |
| 是否需令牌 | 是 |

**请求参数**

路径参数：

| 参数名称 | 类型 | 是否必须 | 备注 | 示例 |
| --- | --- | --- | --- | --- |
| id | integer | 必须 | 视频记录 ID | 3 |

**返回数据**：`data` 为 `null`。

**错误码**：80010 视频记录不存在。

### 14.8 站内消息（8-20 ~ 8-22）

> **为什么需要本组接口**：值日提醒（模块七）与各类审核结果都通过 `sys_message` 落库推送，而 v1 的提醒渠道**只有站内消息**。若没有读取消息与标记已读的接口，「消息已生成但成员看不到」，站内消息渠道就是空的。消息由服务端定时任务与业务事件写入，成员端不提供发送消息的接口。
>
> **消息来源（写入方）**：① 值日提醒 —— 模块七定时任务按 `duty_reminder_config` 生成，`msgType = 1`；② 审核结果 —— 各模块审核类接口（`1-26` 注册申请、`3-21` 预算申请、`4-21` 资产借用、`5-19` 工位申请、`5-24` 座位调换、`6-18` 学长信息）在通过 / 驳回后向申请人写入一条，`msgType = 2`；③ 系统通知 —— 管理员或系统事件写入，`msgType = 3`。排班发布、公告发布等是否需要额外推送，见 16.4 节 Q8。

#### 14.8.1 我的消息分页（8-20）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-20 |
| 接口名称 | 我的消息分页 |
| Path | `/user/message/page` |
| Method | `GET` |
| 接口描述 | 分页查询当前登录用户的站内消息，用于消息中心列表页 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Query 参数（含 2.4 节通用分页参数）：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| isRead | integer | 非必须 | - | 阅读状态筛选：0 未读 1 已读 | format: int32 |
| msgType | integer | 非必须 | - | 消息类型：1 值日提醒 2 审核结果 3 系统通知 | format: int32 |
| beginTime | string | 非必须 | - | 消息时间起 | - |
| endTime | string | 非必须 | - | 消息时间止 | - |

**返回数据**

`data` 为分页对象，含 `total`、`pageNum`、`pageSize`、`pages`、`list` 五个字段（字段含义见 3.1 节）；仅返回 `receiver_id = 当前用户` 的消息，按 `create_time` 倒序。`list` 元素结构：

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| messageId | integer | 必须 | - | 消息 ID | format: int64 |
| title | string | 必须 | - | 消息标题 | - |
| content | string | 非必须 | - | 消息内容 | - |
| msgType | integer | 必须 | - | 消息类型：1 值日提醒 2 审核结果 3 系统通知 | format: int32 |
| bizType | string | 非必须 | - | 关联业务类型，如 `duty`、`budget`、`asset`、`honor` | - |
| bizId | integer | 非必须 | - | 关联业务主键，供前端跳转详情 | format: int64 |
| isRead | integer | 必须 | 0 | 阅读状态：0 未读 1 已读 | format: int32 |
| createTime | string | 必须 | - | 消息时间 | - |

> **越权保护**：只返回本人消息，服务层固定拼接 `receiver_id = 当前用户 ID`，不接受前端传入接收人参数。
>
> **跳转约定**：`bizType` + `bizId` 组合供前端跳转（如 `duty` → 我的值日、`budget` → 预算申请详情），映射关系由前端维护，后端只保证两个字段有值。

#### 14.8.2 未读消息数（8-21）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-21 |
| 接口名称 | 未读消息数 |
| Path | `/user/message/unread/count` |
| Method | `GET` |
| 接口描述 | 返回当前用户未读消息总数与分类数量，用于导航栏红点与消息中心分组 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**：无。

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| total | integer | 必须 | 0 | 未读消息总数 | format: int32 |
| dutyCount | integer | 必须 | 0 | 未读「值日提醒」数量 | format: int32 |
| auditCount | integer | 必须 | 0 | 未读「审核结果」数量 | format: int32 |
| systemCount | integer | 必须 | 0 | 未读「系统通知」数量 | format: int32 |

> **高频接口走缓存**：前端可能 30~60 秒轮询一次，后端用 Redis 计数缓存实现，标记已读时同步递减，避免每次都扫 `sys_message` 表。

#### 14.8.3 标记消息已读（8-22）

**基本信息**

| 项目 | 内容 |
| --- | --- |
| 接口编号 | 8-22 |
| 接口名称 | 标记消息已读 |
| Path | `/user/message/read` |
| Method | `PUT` |
| 接口描述 | 把指定消息标记为已读，也支持一键把本人全部（或某类型）消息标记已读 |
| 权限码 | 登录即可 |
| 是否需令牌 | 是 |

**请求参数**

Body 参数：

| 参数名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| messageIds | array | 非必须 | [] | 待标记的消息 ID 数组，单次最多 100 条 | `all = 1` 时忽略本字段 |
| all | integer | 非必须 | 0 | 是否全部已读：0 否 1 是（只作用于本人消息） | format: int32 |
| msgType | integer | 非必须 | - | 仅配合 `all = 1` 使用：只把该类型的消息标记已读 | format: int32 |

**返回数据**

| 名称 | 类型 | 是否必须 | 默认值 | 备注 | 其他信息 |
| --- | --- | --- | --- | --- | --- |
| updatedCount | integer | 必须 | 0 | 实际由未读变为已读的消息数 | format: int32 |

**错误码**：80012 消息不存在；80013 无权操作该消息。

> **幂等**：已读消息重复提交不报错，`updatedCount` 只统计本次真正发生状态变化的条数。
>
> **越权保护**：`messageIds` 中只要有一条不属于当前用户，整批操作整体拒绝（`80013`），避免用试探方式修改他人消息状态。
>
> **`messageIds` 与 `all` 二选一**：两者都传时以 `all = 1` 为准；都不传时返回 `80012`。

---

## 第 15 章 接口明细完成情况

本章记录各模块接口明细的完成状态。截至 V1.6，**8 个模块共 186 个接口的明细已全部编写完成**。

| 模块 | 所在章节 | 明细状态 | 接口数量 | 负责人 | 建议实现周次 |
| --- | --- | --- | --- | --- | --- |
| 模块一 用户与权限 | 第 7 章 | **已完成** | 29 | 待填写 | 第 1 周 |
| 模块二 代码仓库关联 | 第 8 章 | **已完成** | 17 | 待填写 | 第 4 周 |
| 模块三 部门预算 | 第 9 章 | **已完成** | 25 | 待填写 | 第 2 周 |
| 模块四 部门财产 | 第 10 章 | **已完成** | 27 | 待填写 | 第 2 周 |
| 模块五 部门工位 | 第 11 章 | **已完成** | 26 | 待填写 | 第 3 周 |
| 模块六 学长学姐展板 | 第 12 章 | **已完成** | 19 | 待填写 | 第 4 周 |
| 模块七 值日排班 | 第 13 章 | **已完成** | 21 | 待填写 | 第 3 周 |
| 模块八 通用支撑 | 第 14 章 | **已完成** | 22 | 待填写 | 第 1 周 |
| **合计** | - | **186 / 186 已完成** | **186** | - | - |

> **分工说明**：团队会议已确定模块分工，表中「负责人」列留空待填写；「建议实现周次」沿用原工期建议，可结合分工调整。

**建议实现顺序**

模块一（账号与权限）+ 模块八基础（文件 / 字典 / 验证码，被其他模块依赖）→ 模块三 → 模块四 → 模块七 → 模块五 → 模块六 → 模块二（需提前做托管平台令牌验证）。

**后续维护约定**

1. 接口明细发生增删时，同步更新本表的「明细状态」与「接口数量」；
2. 任何字段、路径、错误码、枚举的变更，须先更新本文档并登记修订记录，再改动代码；
3. 本文档修改后，需重新执行 `tools/gen-api-doc.py` 生成 HTML 浏览版，避免两份文档不一致。

---

## 第 16 章 附录

### 16.1 错误码总表

**模块一 · 用户与权限（10001 ~ 19999）**

| 错误码 | 含义 |
| --- | --- |
| 10001 | 账号不存在 |
| 10002 | 账号被禁用 |
| 10003 | 用户名或密码错误 |
| 10004 | 验证码错误或已过期 |
| 10005 | 账号已锁定，请稍后再试 |
| 10006 | 该账号无前台登录权限 |
| 10007 | 刷新令牌无效或已过期 |
| 10008 | 手机号已被占用 |
| 10009 | 邮箱已被占用 |
| 10010 | 原密码错误 |
| 10011 | 两次输入的新密码不一致 |
| 10012 | 用户不存在 |
| 10013 | 登录账号已存在 |
| 10014 | 学号已存在 |
| 10015 | 角色不存在 |
| 10016 | 不允许禁用超级管理员 |
| 10017 | 该用户存在未归还资产 |
| 10018 | 该用户存在待处理的申请 |
| 10019 | 不允许删除超级管理员 |
| 10020 | 角色名称已存在 |
| 10021 | 角色编码已存在 |
| 10022 | 权限码不存在 |
| 10023 | **（已废弃）** 原「角色不存在」，与 10015 语义重复；V1.0 起统一使用 10015，本编号保留不占用 |
| 10024 | 内置角色不允许修改编码 |
| 10025 | 该角色下存在用户，无法删除 |
| 10026 | 内置角色不允许删除 |
| 10027 | 不允许修改超级管理员的权限 |
| 10028 | 邀请码无效或已失效 |
| 10029 | 邀请码使用次数已达上限 |
| 10030 | 提交过于频繁，请稍后再试 |
| 10031 | 注册申请不存在 |
| 10032 | 该注册申请已处理 |
| 10033 | 邀请码不存在 |
| 10034 | 该学号已存在账号，无法重复申请 |

**模块八 · 文件与通用支撑（80001 ~ 89999）**

| 错误码 | 含义 |
| --- | --- |
| 80001 | 上传文件为空 |
| 80002 | 文件类型不支持 |
| 80003 | 文件大小超出限制 |
| 80004 | 文件不存在 |
| 80005 | 文件已被业务引用，无法删除 |
| 80006 | 只能删除本人上传的文件 |
| 80007 | 公告不存在 |
| 80008 | **（保留，未使用）** 空占位，供后续扩展 |
| 80009 | 定时发布时间必须晚于当前时间 |
| 80010 | 视频记录不存在 |
| 80011 | 播放地址格式不正确 |
| 80012 | 消息不存在 |
| 80013 | 无权操作该消息 |

**模块二 · 代码仓库关联（20001 ~ 29999）**

| 错误码 | 含义 |
| --- | --- |
| 20001 | 托管账户不存在 |
| 20002 | 该账户已绑定，请勿重复绑定 |
| 20003 | 访问令牌无效或已过期 |
| 20004 | 仓库不存在 |
| 20005 | 该账户或仓库不属于当前用户 |
| 20006 | 同步任务执行中，请稍后再试 |
| 20007 | 第三方平台接口调用失败 |
| 20008 | 未选择要同步的账户 |

**模块三 · 部门预算（30001 ~ 39999）**

| 错误码 | 含义 |
| --- | --- |
| 30001 | 预算周期不存在 |
| 30002 | 该预算周期未处于执行中，无法提交申请 |
| 30003 | 预算科目不存在 |
| 30004 | 该科目不属于所选预算周期 |
| 30005 | 申请金额必须大于 0 |
| 30006 | 申请金额超出科目剩余额度 |
| 30007 | 预算申请不存在 |
| 30008 | 无权访问该预算申请 |
| 30009 | 当前状态不允许撤销 |
| 30010 | 该申请已审核，无法重复审核 |
| 30011 | 支出金额超出周期剩余额度 |
| 30012 | 支出记录不存在 |
| 30013 | 该科目已被申请或支出引用，无法删除 |
| 30014 | 该周期下存在科目或申请，无法删除 |
| 30015 | 该预算周期已结束，无法登记支出 |
| 30016 | 周期总额度不得低于已使用额度 |
| 30017 | 科目额度不得低于已使用额度 |
| 30018 | 预算周期状态流转不合法 |
| 30019 | 存在待审核的申请，无法结束周期 |
| 30020 | 该功能在 v1.0 未开放 |

**模块四 · 部门财产（40001 ~ 49999）**

| 错误码 | 含义 |
| --- | --- |
| 40001 | 资产不存在 |
| 40002 | 资产编号已存在 |
| 40003 | 资产分类不存在 |
| 40004 | 该分类下存在资产，无法删除 |
| 40005 | 该分类存在子分类，无法删除 |
| 40006 | 资产状态不允许该操作 |
| 40007 | 该资产当前已被借出 |
| 40008 | 借用记录不存在 |
| 40009 | 只能归还本人借用的资产 |
| 40010 | 该借用记录状态不允许归还 |
| 40011 | 领用记录无需归还 |
| 40012 | 盘点任务不存在 |
| 40013 | 该盘点任务已结束 |
| 40014 | 盘点明细不存在 |
| 40015 | 资产已报废，无法借用 |
| 40016 | 导出数据量超出上限 |
| 40017 | 该借用记录状态不允许审核 |
| 40018 | 分类编码已存在 |
| 40019 | 该资产已有待审核的申请 |
| 40020 | 该资产存在未完成的借用/领用记录 |

**模块五 · 部门工位（50001 ~ 59999）**

| 错误码 | 含义 |
| --- | --- |
| 50001 | 工位区域不存在 |
| 50002 | 该区域下存在工位，无法删除 |
| 50003 | 工位不存在 |
| 50004 | 工位编号在该区域内已存在 |
| 50005 | 该工位已被占用 |
| 50006 | 工位申请不存在 |
| 50007 | 无权访问该工位申请 |
| 50008 | 当前状态不允许该操作 |
| 50009 | 座位调换申请不存在 |
| 50010 | 不能与自己调换工位 |
| 50011 | 对调方工位不存在或已释放 |
| 50012 | 仅对调方可处理该申请 |
| 50013 | 调换申请前置状态不满足 |
| 50014 | 平面图底图尺寸未设置 |
| 50015 | 源工位当前无人，无法拖动 |
| 50016 | 源工位与目标工位不能相同 |
| 50017 | 目标工位为预留或维修中，不可占用 |

**模块六 · 学长学姐展板（60001 ~ 69999）**

| 错误码 | 含义 |
| --- | --- |
| 60001 | 学长学姐信息不存在 |
| 60002 | 该信息已审核，无法重复审核 |
| 60003 | 无权撤回该提交 |
| 60004 | 已审核通过的信息不可撤回 |
| 60005 | 荣誉记录不存在 |
| 60006 | 当前状态不允许上板 |
| 60007 | **（保留，未使用）** 删除学长学姐信息时其荣誉记录一并逻辑删除（见 `6-08`），故不触发该错误；后续若改为禁止连带删除，可直接启用本编号 |
| 60008 | 当前状态不允许修改 |

**模块七 · 值日排班（70001 ~ 79999）**

| 错误码 | 含义 |
| --- | --- |
| 70001 | 排班计划不存在 |
| 70002 | 该计划已发布，不可重复发布 |
| 70003 | 计划起止日期不合法 |
| 70004 | 参与轮转的成员为空，无法生成排班 |
| 70005 | 该计划尚无值日安排，无法发布 |
| 70006 | 值日安排不存在 |
| 70007 | 只能确认本人的值日 |
| 70008 | 该值日已完成确认 |
| 70009 | 换班申请不存在 |
| 70010 | 不能与自己换班 |
| 70011 | 目标值日不存在或不属于对方 |
| 70012 | 仅换班对象可处理该申请 |
| 70013 | **（保留，未使用）** 提醒配置查询在无专属配置时回退全局配置（见 `7-20`），不返回该错误；本编号保留备用 |
| 70014 | 该成员当日已有值日安排 |

### 16.2 各模块自有枚举汇总

| 模块 | 字段 | 取值 |
| --- | --- | --- |
| 模块一 用户 | `sys_user.status` | 0 禁用，1 启用 |
| 模块一 用户 | `sys_role.data_scope` | 1 全部数据，2 本人数据 |
| 模块一 用户 | `sys_invite_code.status` | 0 作废，1 有效 |
| 模块一 用户 | `sys_register_apply.status` | 0 待审核，1 已通过，2 已驳回 |
| 模块二 仓库 | `repo_project.tracked` | 0 不统计，1 统计 |
| 模块二 仓库 | `repo_account.sync_status` | 0 未采集，1 采集中，2 采集正常，3 采集失败 |
| 模块二 仓库 | `repo_project.sync_status` | 0 未采集，1 采集中，2 采集正常，3 采集失败 |
| 模块二 仓库 | `repo_sync_log.sync_type` | 1 定时自动，2 管理员手动补采 |
| 模块二 仓库 | `repo_sync_log.sync_result` | 0 失败，1 成功 |
| 模块三 预算 | `budget_period.status` | 0 草稿，1 执行中，2 已结束 |
| 模块三 预算 | `budget_item.category` | 1 设备采购，2 耗材，3 活动，4 差旅，5 其他 |
| 模块三 预算 | `budget_apply.status` | 0 待审核，1 已通过，2 已驳回，3 已撤销 |
| 模块四 财产 | `asset_info.source_type` | 1 采购，2 捐赠，3 调拨 |
| 模块四 财产 | `asset_info.status` | 1 在用，2 闲置，3 维修中，4 已借出，5 已报废 |
| 模块四 财产 | `asset_borrow.borrow_type` | 1 借用，2 领用 |
| 模块四 财产 | `asset_borrow.status` | 0 待审核，1 已通过，2 已驳回，3 已归还，4 已逾期 |
| 模块四 财产 | `asset_record.record_type` | 1 领用，2 借用，3 归还，4 维修，5 报废，6 调拨，7 盘点 |
| 模块四 财产 | `asset_inventory.status` | 0 进行中，1 已完成 |
| 模块四 财产 | `asset_inventory_item.check_status` | 0 未盘点，1 正常，2 盘亏，3 位置不符 |
| 模块五 工位 | `seat_info.seat_type` | 1 工位，2 会议室，3 储物柜，4 其他 |
| 模块五 工位 | `seat_info.status` | 1 空闲，2 已占用，3 预留，4 维修中 |
| 模块五 工位 | `seat_apply.status` | 0 待审核，1 已通过，2 已驳回，3 已撤销 |
| 模块五 工位 | `seat_swap.status` | 0 待对方确认，1 对方已同意待审核，2 已通过，3 已拒绝，4 已取消 |
| 模块六 荣誉 | `honor_alumni.status` | 0 待审核，1 已通过，2 已驳回 |
| 模块六 荣誉 | `honor_alumni.is_show` | 0 下板，1 上板 |
| 模块六 荣誉 | `honor_record.level` | 1 国家级，2 省级，3 校级，4 企业及其他 |
| 模块七 值日 | `duty_plan.plan_type` | 1 按周轮转，2 分组轮转，3 手工指定 |
| 模块七 值日 | `duty_plan.status` | 0 草稿，1 已发布，2 已结束 |
| 模块七 值日 | `duty_schedule.status` | 0 未完成，1 已完成，2 请假，3 已换班 |
| 模块七 值日 | `duty_swap.status` | 0 待确认，1 已同意，2 已拒绝，3 已取消 |
| 模块七 值日 | `duty_reminder_config.channel` | 1 站内消息 |
| 模块八 通用 | `sys_notice.status` | 0 草稿，1 已发布，2 已下架 |
| 模块八 通用 | `sys_notice.notice_type` | 1 中心公告，2 制度，3 活动，4 其他 |
| 模块八 通用 | `sys_oper_log.status` | 0 失败，1 成功 |
| 模块八 通用 | `sys_video.biz_type` | 1 首页展示，2 学长学姐展板，3 其他 |
| 模块八 通用 | `sys_video.status` | 0 下架，1 上架 |
| 模块八 通用 | `sys_message.msg_type` | 1 值日提醒，2 审核结果，3 系统通知 |
| 模块八 通用 | `sys_message.is_read` | 0 未读，1 已读 |

### 16.3 权限码总表

| 模块 | 权限码 |
| --- | --- |
| 用户与权限 | `system:user:list`、`system:user:query`、`system:user:add`、`system:user:edit`、`system:user:reset`、`system:user:remove`、`system:role:list`、`system:role:add`、`system:role:edit`、`system:role:remove`、`system:role:auth`、`system:register:list`、`system:register:audit`、`system:invite:list`、`system:invite:add`、`system:invite:remove`、`system:log:list` |
| 代码仓库关联 | `repo:account:list`、`repo:stat:list`、`repo:sync:list`、`repo:sync:exec` |
| 部门预算 | `budget:period:list`、`budget:period:add`、`budget:period:edit`、`budget:period:remove`、`budget:period:audit`、`budget:period:public`、`budget:collect:exec`、`budget:item:edit`、`budget:apply:list`、`budget:apply:audit`、`budget:expense:add`、`budget:expense:list`、`budget:expense:remove`、`budget:stat:list` |
| 部门财产 | `asset:info:list`、`asset:info:query`、`asset:info:add`、`asset:info:edit`、`asset:info:remove`、`asset:info:assign`、`asset:info:export`、`asset:category:edit`、`asset:record:add`、`asset:record:list`、`asset:borrow:list`、`asset:borrow:audit`、`asset:borrow:edit`、`asset:inventory:add`、`asset:inventory:list`、`asset:inventory:exec` |
| 部门工位 | `seat:area:list`、`seat:area:add`、`seat:area:edit`、`seat:area:remove`、`seat:seat:list`、`seat:seat:add`、`seat:seat:edit`、`seat:seat:remove`、`seat:seat:assign`、`seat:apply:list`、`seat:apply:audit`、`seat:swap:list`、`seat:swap:audit` |
| 学长学姐展板 | `honor:alumni:list`、`honor:alumni:add`、`honor:alumni:edit`、`honor:alumni:remove`、`honor:alumni:audit`、`honor:record:edit` |
| 值日排班 | `duty:plan:list`、`duty:plan:add`、`duty:plan:edit`、`duty:plan:remove`、`duty:plan:generate`、`duty:plan:publish`、`duty:schedule:list`、`duty:schedule:edit`、`duty:stat:list`、`duty:reminder:config` |
| 通用支撑 | `common:notice:list`、`common:notice:edit`、`common:stat:list`、`common:video:list`、`common:video:edit` |

### 16.4 需求确认与会议记录

> 本节为接口设计过程中的确认记录，作为设计依据留档，不属于接口规范正文。

**一、已确认事项（第一轮评审，2026-09-21）**

| 编号 | 模块 | 确认结论 | 落文档位置 |
| --- | --- | --- | --- |
| A1 | 全局 | 响应码 `200` 表示成功，业务错误使用分段错误码 | 2.3、2.8 |
| A2 | 全局 | 分页请求 `pageNum` / `pageSize`，返回 `total` / `pageNum` / `pageSize` / `pages` / `list` | 2.4 |
| A3 | 全局 | 主键使用数据库自增 `bigint`，后续可平滑扩展为雪花 ID | 2.5、5.1 |
| A4 | 全局 | 认证采用 JWT 双令牌 + Redis 黑名单 | 1.4 |
| A5 | 全局 | Jellyfin 用于**展示视频**：由 Jellyfin 生成播放 URL，URL 存入平台数据库，前端读取 URL 播放 | 5.8、4.9、接口 8-15 ~ 8-19 |
| A6 | 全局 | 团队分工与工期已由会议确定，第 15 章分工表仅作参考占位 | 第 15 章 |
| A7 | 模块一 | 账号体系：一套账号表 + 角色区分前后台；账号离场用「停用」而非删除 | 5.1、6.3 |
| A8 | 模块二 | 关联托管账号后拉取提交信息；以**提交数量**为核心指标，展示 GitHub 风格活跃度热力图；代码行数、活跃天数一并采集，统计口径待开会确定 | 5.2、4.3 |
| A9 | 模块三 | 预算由管理员统一管理：成员只提交申请（周期执行期间可随时提交），采购与报销统一经管理员之手，资金流动全程可追踪 | 5.3、4.4 |
| A10 | 模块四 | 借用需审核；日常登记类操作由负责人直接生效 | 5.4、4.5 |
| A11 | 模块五 | 负责人可直接分配工位；成员之间可发起座位调换申请；平面图先按底图 + 坐标实现 | 5.5、4.6 |
| A12 | 模块六 | 学长学姐展板信息由成员自行填写，管理员审核通过后上板 | 5.6、4.7 |
| A13 | 模块七 | 值日排班按**分组轮转**；提醒先做站内消息 | 5.7、4.8 |
| A14 | 模块二 | Gitee / GitHub 关联方式确定为**个人访问令牌（AccessToken）**；平台登录认证沿用 JWT 双令牌刷新 | 1.4、5.2 |
| A15 | 模块二 | 提交统计范围为**所有分支**的提交，不做分支过滤 | 5.2、4.3 |
| A16 | 模块三 | 成员在预算周期执行期间可**随时**提交经费申请，取消集中「征集」环节 | 5.3、4.4 |
| A17 | 模块八 | 视频归入公共模块；视频业务名称统一为「学长学姐展板」 | 5.8、4.9 |
| A18 | 模块八 | 视频文件由 Jellyfin 存储并生成播放链接，平台仅把链接录入数据库，前端读取链接播放 | 5.9 |
| A19 | 模块八 | 文件（图片、证书、凭证等）存储介质确定为**阿里云 OSS**；上传链路为**后端中转**（前端 → 平台后端 → OSS），Bucket 为**公共读**；前端不接触 AccessKey | 2.9、5.8 |

**二、待确认事项（第二轮）**

| 编号 | 模块 | 待确认内容 | 本文档当前采用的默认方案 |
| --- | --- | --- | --- |
| Q1 | 模块二 | 贡献度算法权重、是否统计代码行数与活跃天数 | 提交数 × 1 + 活跃天数 × 2 + 代码行 × 0.01，权重可配置，待开会确定 |
| Q2 | 模块六 | 成员填写后是否允许撤回 / 再次编辑 | 审核前可撤回，审核通过后再次修改需重新走审核 |
| Q3 | 模块七 | 分组轮转的分组如何维护 | 在排班计划内维护分组配置（JSON），成员变动时重新生成排班 |
| Q4 | 模块八 | 文件存储位置 | **已确定（V1.1）**：阿里云 OSS + 后端中转上传 + Bucket 公共读，见 2.9 节与 A19 条 |
| Q5 | 模块八 | 视频播放链接的来源：管理员手工录入还是对接 Jellyfin API 自动同步 | v1 由管理员在 Jellyfin 侧取得播放链接后录入平台；v1.1 可对接 Jellyfin API 自动同步 |
| Q6 | 全局 | 测试环境与生产环境的正式域名 | 见 1.3 节，暂以占位符标注「待补充」 |

**三、已确认事项（第三轮评审，2026-09-24）**

| 编号 | 模块 | 确认结论 | 落文档位置 |
| --- | --- | --- | --- |
| A20 | 模块二 | 提交统计**全部由后台自动完成**（绑定账户后首次采集 + 每日定时增量采集，失败自动重试），**取消用户端手动同步**；用户端只提供只读的同步状态查询 | 8.1、`2-06`、5.2 |
| A21 | 模块五 | 管理端平面图**直接显示每个人所在工位**，拖动即可调整：拖到空工位即移动、拖到已占用工位即互换，即时生效、无需审核 | 11.1、11.9、`5-25`、`5-26` |
| A22 | 模块八 | 站内消息必须有成员端读取与标记已读接口，否则「提醒已生成但成员看不到」，补齐 `8-20` ~ `8-22` | 14.8 |
| A23 | 模块六 | 成员提交的信息在驳回后 / 已通过后需要再修改时，在**原记录**上修改并重新送审，不再新建记录 | 12.4、`6-19` |

**四、待确认事项（第三轮补充）**

| 编号 | 模块 | 待确认内容 | 本文档当前采用的默认方案 |
| --- | --- | --- | --- |
| Q7 | 模块六 | 展板是否需要**未登录匿名展示**（招新宣传页、大厅大屏） | v1 按 1.4 节约定需要登录（接口仍在 `/user/honor/**`）；若需要匿名展示，把该组接口整体迁至 `/common/honor/**`，接口结构与字段不变 |
| Q8 | 模块八、全局 | 除值日提醒与审核结果外，还有哪些事件需要推送站内消息（如排班发布、公告发布、资产逾期） | v1 先做「值日提醒 + 审核结果」两类；其余事件需要时按 `msgType = 3` 扩展，接口不变，见 14.8 节 |
| Q9 | 模块五 | 工位变动是否需要独立的流水表（当前拖拽 / 分配 / 调换只写入 `sys_oper_log`） | v1 不建表，统一通过 `8-12` 操作日志追溯；若后续需要「某个工位的完整变动史」，再补 `seat_record` 表 |
| Q10 | 模块七 | 成员本人是否需要**自助请假**入口（当前「请假」只能由管理员用 `7-18` 登记） | v1 不提供自助入口：成员先向分组负责人报备，由负责人在 `7-18` 登记 `status = 2 请假`；若需要自助，续排为 `7-22`（`PUT /user/duty/schedule/{id}/leave`），状态机与 `7-18` 一致 |
| Q11 | 模块四 | 借用 / 领用申请在审核前是否可以由本人**撤销**（当前只能等管理员驳回；模块三、五、六均已有撤销 / 撤回接口，仅模块四缺失，属口径不对称） | v1 不提供，与 A10「借用需审核」保持一致；若需要，续排为 `4-28`（`PUT /user/asset/borrow/{id}/cancel`），并给 16.2 的 `asset_borrow.status` 增加 `5 已撤销` |
| Q12 | 模块六、模块八 | 「学长学姐展板」这一名称同时用于模块六（信息与荣誉记录）与模块八 `8-15` ~ `8-19`（视频），是否要区分命名 | v1 保持：模块六负责展板**文字信息**，模块八 `8-15` ~ `8-19` 负责展板**视频区**（`sys_video.biz_type = 2 学长学姐展板`），前端在同一展板页内分区块展示，详见 5.9 节 |

### 16.5 与规划文档的差异说明

| 规划文档描述 | 本文档处理 |
| --- | --- |
| 功能需求 8「前端简洁美观」 | 属于前端规范，不在接口文档范围 |
| 技术栈中的 Jellyfin | 作为展示视频的媒体服务，平台仅存储播放 URL（见 5.9 节） |
| 「后期可加值日提醒」 | v1 提供站内消息提醒与提醒配置接口，邮件 / 微信推送列入 v1.1 |

### 16.6 后续版本规划（v1.1 及以后）

| 事项 | 说明 |
| --- | --- |
| 菜单与权限点维护 | 将 2.7 节的权限码升级为可维护的菜单 / 按钮表 |
| Excel 批量导入 | 成员名单、资产台账、学长信息批量导入 |
| 消息通知 | v1 已完成站内消息中心（接口 `8-20` ~ `8-22`）；v1.1 扩展邮件 / 微信推送，仅需扩展 `sys_message.msg_type` 与提醒渠道 `channel` 取值 |
| 提交统计汇总表 | 引入 `repo_commit_daily`，支撑大时间跨度热力图查询 |
| 视频自动同步 | 对接 Jellyfin API 自动同步播放链接与元数据（v1 为管理员在 Jellyfin 取得链接后手工录入） |
| 数据导出 | 预算执行报表、值日统计报表导出为 Excel |
| 操作日志审计 | 增加日志详情查看与登录日志独立查询 |
| 托管平台 OAuth | 模块二在 AccessToken 基础上兼容 Gitee / GitHub OAuth 授权 |

---

> 本文档为 IECMP 创业中心管理平台接口设计的唯一权威来源。任何字段、路径、错误码、枚举的变更，须先更新本文档并在修订记录中登记，再改动代码。
>
> 文档版本：V1.6　|　最后更新：2026-09-24　|　编写：后端组

