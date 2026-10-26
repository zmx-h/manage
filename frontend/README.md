# IECMP 前端

基于 **Vue 3 + Vite + Element Plus + Pinia + Vue Router + Axios**，沿用项目现有工程，不引入第三方付费模板。

## 不接后端，完整测试前端（推荐当前使用）

```powershell
npm run dev:mock
```

启动后打开 `http://127.0.0.1:5173/__mock/`，点击“一键体验管理端”或“一键体验成员端”。如需手动测试登录：

| 角色 | 账号 | 初始密码 |
| --- | --- | --- |
| 超级管理员 | `admin` | `demo1234` |
| 成员 | `member` | `demo1234` |
| 第二位成员（换班 / 换座确认） | `member2` | `demo1234` |
| 部长（权限隔离） | `minister` | `demo1234` |
| 中心负责人 | `leader` | `demo1234` |

邀请码 `DEMO2026`，验证码固定 `2468`。测试数据均为虚构，不要输入真实密码、令牌、附件或个人资料。

### 最小改动方案

模拟 API 仅通过 **Vite 开发服务中间件**拦截 `/api`，保持原有 Vue 页面、Axios 请求、路由守卫、权限校验和业务组件不变。不需要安装新依赖，也不需要后端进程、数据库、Redis 或 OSS。

- `npm run dev:mock`：启动离线模拟；仅绑定 `127.0.0.1`，端口固定 5173。
- `npm run dev`：仍为原有真实后端代理模式；两个命令二选一，停止旧服务后再启动。
- 生产 `npm run build` 不加载 mock 插件，模拟账号、数据、控制台和媒体不会进入构建产物。
- 所有模拟业务修改保存在 `frontend/.mock-data/database.json`（已忽略 Git），刷新页面和重启服务后保留。
- 控制台“一键重置”恢复初始账号和测试数据、清空会话。切回真实后端前请退出模拟账号或清除本地登录令牌。
- `/preview/**` 仍是原先的只读空数据预览；需要完整交互请用模拟登录后的正常路由。

### 可以测试

真实页面中的登录、注册、权限隔离、分页筛选、详情、新增 / 编辑 / 删除、预算申请与部分批准、支出回退、资产借还与盘点、工位移动 / 互换与布局坐标保存、展板审核、排班发布 / 换班、公告、消息已读、文件上传下载、本地视频播放、贡献热力图和趋势图。

控制台可设置 `0–10000 ms` 请求延迟，并给某个接口注入**下一次**空数据 / 401 / 403 / 500 响应。401 会保留会话以便观察现有前端的刷新与重试流程。未知接口返回明确错误，**绝不回退到真实后端**。所有请求仍遵循 `{code,data,msg}`，失败更新会回滚模拟数据，不以“假成功”掩盖操作错误。

模拟导出为 UTF-8 CSV（文件名 `assets-mock.csv`，可用 Excel 打开），用于验证下载交互，不伪装成真实 XLSX。上传返回本地 URL；内置视频、头像和底图均来自本机。模拟数据与规则用于前端验证，不替代真实后端权限、并发、安全与第三方服务验收；文档保留的禁用接口 `3-16` 仍按约定返回 `30020`。

同一浏览器的标签页共享登录态；测试两位成员协作请使用不同浏览器或无痕窗口。

### 模拟模式验证

```powershell
npm run test:mock     # 9 项 Node 单元 / HTTP 测试
npm run test:mock:ui  # 7 项浏览器集成测试，使用真正的本地模拟响应
```

浏览器测试会自动重置**本地模拟**数据，不触及任何后端。请先保存需要保留的演示状态。

代码隔离在 `mock/`：`plugin.js`（仅开发服务）、`server.js`（HTTP 与控制台）、`engine.js`（可变测试数据与流程）、`seed.js` / `dictionaries.json`（虚构数据与文档字典）、`media/`（本地播放器与验证码测试资源）。此目录不是生产后端，不应对公网开放。

## 运行

```powershell
cd frontend
npm ci
npm run dev
```

默认地址：`http://localhost:5173`。

- 成员登录：`/login`
- 管理端登录：`/admin/login`
- 入驻 / 邀请码注册：`/register`
- 管理端只读界面预览：`/preview/admin/dashboard`
- 成员端只读界面预览：`/preview/user/home`

预览入口不设置令牌、不授予权限、不发业务请求、不包含模拟业务数据。真实页面仍需登录并通过服务端身份 / 权限校验。

## 界面与功能

- 管理端 26 个、成员端 17 个业务路由，8 个模块共享统一导航、搜索、表格、分页、表单和详情抽屉。
- 浅色 / 深色 / 跟随系统；默认跟随系统，系统主题变化时即时响应。
- 简体中文 / English；界面与 Element Plus 组件语言同步，偏好保存在本地。
- 适量毛玻璃效果用于导航、顶部栏与装饰卡片；桌面 / 移动端自适应。
- 登录注册、个人资料与密码、列表筛选、创建编辑、审核、撤回 / 删除确认、文件上传、资产导出、站内消息。
- 工位平面图区分 **座位模式**（移动 / 互换，`5-26`）与 **布局模式**（坐标保存，`5-13`）。
- 仓库提交趋势图、贡献热力图、权限树、值日月历；仅渲染接口返回数据。
- JWT 自动携带、单次共享刷新请求、刷新失败清除会话；业务 `code` 与 HTTP 状态分开处理。
- 富文本当前以纯文本安全展示，不执行接口返回的 HTML；媒体 URL 仅允许 HTTP(S)。

## 接口契约

`docs/IECMP-接口开发文档-v1.6.md` 是唯一接口契约来源。`src/config/contracts.json` 包含全部 **186** 个接口的路径、方法、权限、入参和返回字段。

```powershell
npm run generate:contracts
```

`src/api/client.js` 的 `invoke('3-21', values)` 只发送该接口定义的字段，分别构造路径参数、Query、JSON 或 multipart；不会把响应记录的附加字段原样提交。`5-13` 的 `seats` 子字段不会被错误平铺到根对象。

模块请求封装：`src/api/{auth,repo,budget,asset,seat,honor,duty,common}.js`。页面、操作按钮与权限配置：`src/config/pages.json`。复杂表单中的分组配置等嵌套字段暂以 JSON 编辑，角色等可用元数据则使用选择器。

**注意：186 个请求契约并不代表 186 个后端业务已实现。** 当前仓库控制器仍返回 `Result.todo(...)` / `UnsupportedOperationException`。真实登录、数据查询和保存依赖后端完成业务逻辑，并配置 MySQL、Redis、OSS 等服务。前端明确显示“后端接口尚未实现”，不会用假成功或模拟数据掩盖。

部分接口契约缺少独立编辑详情或后台科目读取入口，前端仅使用已有字段与入口；例如排班修改时，列表未返回的 `memberIds` / `groupConfig` 需要重新填写，不会凭空合成或覆盖为假数据。业务流程需要待真实接口完成后继续逐模块联调。

## 环境配置

开发配置在 `.env.development`：

```dotenv
VITE_API_BASE_URL=/api
VITE_API_TARGET=http://localhost:8080
```

Vite 将 `/api` 代理到后端，生产部署须在 Nginx 配置相同前缀并为 Vue Router history 路由提供 `try_files $uri $uri/ /index.html` 回退。不可在前端配置数据库密码、JWT 密钥、OSS AccessKey。

## 验证

```powershell
npm run lint
npm run build
npm test
```

Playwright 测试默认使用 Windows 本机 Chrome。其他机器设置 `BROWSER_PATH` 指向可用 Chromium 浏览器，或调整 `playwright.config.js`。

18 项验收覆盖：全部 43 个预览 / 认证路由、186 个契约与后端声明路径、主题随系统及偏好持久化、中英文、移动端溢出、登录、创建表单、审核枚举、令牌刷新、权限隔离、未实现 / 网络错误、工位占用 / 坐标两类拖拽、消息未读数、multipart 上传、富文本注入防护、真实数据趋势图和月历、必填路径参数。

浏览器集成测试通过拦截响应进行验证，**测试数据只存在 `tests/`，不会进入正式页面或生产接口**。这不代替真实后端端到端验收。

## 主要目录

```text
src/
  api/                     统一请求和按文档字段调用
  components/common/       可复用视觉 / 表单 / 上传 / 图表组件
  composables/             资源状态与工作台路由
  config/                  API 契约、页面配置、字段标签
  layouts/                 管理端与成员端共享布局
  locales/                 中英文界面文案
  views/workspace/         看板、资源页面、工位、个人中心
scripts/generate-contracts.py  从文档生成请求契约
scripts/generate-api-modules.py  生成模块请求封装与字段辅助标签
```

As of 2026-10-20.
