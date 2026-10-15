# IECMP 前端（Vue 3 + Vite）

骨架工程：**目录结构、路由、状态管理、请求封装已就绪，页面与接口调用待实现**。

## 启动

```bash
npm install
npm run dev          # http://localhost:5173
```

开发期 `/api` 由 Vite proxy 转发到 `VITE_API_TARGET`（默认 `http://localhost:8080`）。

## 目录

| 目录 | 作用 |
| --- | --- |
| `src/api/` | 按模块拆分的接口调用文件（骨架为空，见文件内注释的写法模板） |
| `src/api/request.js` | Axios 实例：注入令牌、统一处理 code / 401 / 403 |
| `src/router/` | 路由表，分「管理端」「用户端」两份，页面组件全部是空壳 |
| `src/store/` | Pinia：`user`（登录态与权限）、`app`（界面状态） |
| `src/layouts/` | 三套布局：管理端、用户端、空白页 |
| `src/views/` | 页面空壳，**尚未实现任何界面** |
| `src/directives/permission.js` | `v-perm` 按钮级权限指令 |
| `src/utils/` | 令牌读写、字典缓存、时间与金额格式化 |

## 约定

1. 后端 HTTP 状态码恒为 200，业务结果看响应体 `code`（200 成功，10001~89999 为模块错误码）。
2. 请求头令牌格式：`Authorization: Bearer {accessToken}`。
3. 收到 401 由 `request.js` 统一清空令牌并跳转登录页；403 统一提示「权限不足」。
4. 页面文案与状态含义以 `docs/IECMP-接口开发文档-v1.6.md` 为准；枚举文案可调用 `GET /api/common/dict/{type}` 获取。
