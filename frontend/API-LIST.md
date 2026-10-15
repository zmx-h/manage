# 前端接口调用清单（骨架）

> 由 `docs/IECMP-接口开发文档-v1.6.md` 生成，共 186 个接口。
> 前端按模块在 `src/api/*.js` 中实现调用，本表用于对照进度。

## 模块一 · 用户与权限（`src/api/auth.js`）

| 编号 | 方法 | 路径 | 接口名称 | 权限码 |
| --- | --- | --- | --- | --- |
| 1-01 | POST | `/admin/login` | 后台登录 | - |
| 1-02 | POST | `/user/login` | 前台登录 | - |
| 1-03 | POST | `/user/logout` | 退出登录 | - |
| 1-04 | POST | `/user/token/refresh` | 刷新令牌 | - |
| 1-05 | GET | `/user/info` | 当前用户信息 | - |
| 1-06 | PUT | `/user/info` | 修改个人资料 | - |
| 1-07 | PUT | `/user/password` | 修改密码 | - |
| 1-08 | GET | `/admin/user/page` | 用户分页查询 | system:user:list |
| 1-09 | GET | `/admin/user/{id}` | 用户详情 | system:user:query |
| 1-10 | POST | `/admin/user` | 新增用户 | system:user:add |
| 1-11 | PUT | `/admin/user` | 修改用户 | system:user:edit |
| 1-12 | PUT | `/admin/user/{id}/status` | 启用 / 禁用用户 | system:user:edit |
| 1-13 | PUT | `/admin/user/{id}/password/reset` | 重置密码 | system:user:reset |
| 1-14 | DELETE | `/admin/user/{id}` | 删除用户 | system:user:remove |
| 1-15 | GET | `/admin/role/list` | 角色下拉列表 | - |
| 1-16 | GET | `/admin/role/page` | 角色分页查询 | system:role:list |
| 1-17 | POST | `/admin/role` | 新增角色 | system:role:add |
| 1-18 | PUT | `/admin/role` | 修改角色 | system:role:edit |
| 1-19 | DELETE | `/admin/role/{id}` | 删除角色 | system:role:remove |
| 1-20 | PUT | `/admin/role/{id}/permission` | 角色授权 | system:role:auth |
| 1-21 | GET | `/admin/permission/tree` | 权限码树 | - |
| 1-22 | GET | `/common/register/config` | 注册方式配置查询 | - |
| 1-23 | POST | `/user/register` | 邀请码注册 | - |
| 1-24 | POST | `/common/register/apply` | 提交注册申请 | - |
| 1-25 | GET | `/admin/register/apply/page` | 注册申请分页 | system:register:list |
| 1-26 | PUT | `/admin/register/apply/{id}/audit` | 注册申请审核 | system:register:audit |
| 1-27 | GET | `/admin/invite-code/page` | 邀请码分页 | system:invite:list |
| 1-28 | POST | `/admin/invite-code` | 生成邀请码 | system:invite:add |
| 1-29 | DELETE | `/admin/invite-code/{id}` | 作废邀请码 | system:invite:remove |

## 模块二 · 代码仓库关联（`src/api/repo.js`）

| 编号 | 方法 | 路径 | 接口名称 | 权限码 |
| --- | --- | --- | --- | --- |
| 2-01 | GET | `/user/repo/platform/list` | 支持的平台列表 | - |
| 2-02 | POST | `/user/repo/account` | 绑定代码托管账户 | - |
| 2-03 | GET | `/user/repo/account/list` | 我的绑定账户列表 | - |
| 2-04 | PUT | `/user/repo/account` | 更新绑定账户令牌 | - |
| 2-05 | DELETE | `/user/repo/account/{id}` | 解绑账户 | - |
| 2-06 | GET | `/user/repo/sync/status` | 我的同步状态 | - |
| 2-07 | GET | `/user/repo/project/page` | 我的仓库分页 | - |
| 2-08 | PUT | `/user/repo/project/{id}/track` | 开启 / 关闭仓库统计 | - |
| 2-09 | GET | `/user/repo/commit/page` | 提交记录分页 | - |
| 2-10 | GET | `/user/repo/commit/chart` | 提交-日期趋势图 | - |
| 2-11 | GET | `/user/repo/commit/heatmap` | 贡献热力图 | - |
| 2-12 | GET | `/user/repo/stat/overview` | 个人贡献概览 | - |
| 2-13 | GET | `/admin/repo/account/page` | 账户与仓库分页（后台） | repo:account:list |
| 2-14 | GET | `/admin/repo/stat/rank` | 成员贡献排行（后台） | repo:stat:list |
| 2-15 | GET | `/admin/repo/stat/trend` | 中心提交趋势（后台） | repo:stat:list |
| 2-16 | GET | `/admin/repo/sync/log/page` | 同步日志分页（后台） | repo:sync:list |
| 2-17 | POST | `/admin/repo/sync` | 手动补采（后台） | repo:sync:exec |

## 模块三 · 部门预算（`src/api/budget.js`）

| 编号 | 方法 | 路径 | 接口名称 | 权限码 |
| --- | --- | --- | --- | --- |
| 3-01 | GET | `/user/budget/period/page` | 公示预算周期分页 | - |
| 3-02 | GET | `/user/budget/period/{id}` | 预算周期详情 | - |
| 3-03 | GET | `/user/budget/period/{id}/item/list` | 预算科目明细 | - |
| 3-04 | GET | `/user/budget/expense/page` | 支出明细公示 | - |
| 3-05 | POST | `/user/budget/apply` | 提交预算申请 | - |
| 3-06 | GET | `/user/budget/apply/my/page` | 我的预算申请分页 | - |
| 3-07 | GET | `/user/budget/apply/{id}` | 预算申请详情 | - |
| 3-08 | PUT | `/user/budget/apply/{id}/cancel` | 撤销预算申请 | - |
| 3-09 | GET | `/user/budget/stat/my` | 我的经费汇总 | - |
| 3-10 | GET | `/admin/budget/period/page` | 预算周期分页（后台） | budget:period:list |
| 3-11 | POST | `/admin/budget/period` | 新增预算周期（起草） | budget:period:add |
| 3-12 | PUT | `/admin/budget/period` | 修改预算周期 | budget:period:edit |
| 3-13 | DELETE | `/admin/budget/period/{id}` | 删除预算周期 | budget:period:remove |
| 3-14 | PUT | `/admin/budget/period/{id}/status` | 周期状态流转 | budget:period:audit |
| 3-15 | PUT | `/admin/budget/period/{id}/public` | 公示 / 取消公示 | budget:period:public |
| 3-16 | POST | `/admin/budget/period/{id}/collect` | 发起预算征集（v1.1 预留） | budget:collect:exec |
| 3-17 | POST | `/admin/budget/item` | 新增预算科目 | budget:item:edit |
| 3-18 | PUT | `/admin/budget/item` | 修改预算科目 | budget:item:edit |
| 3-19 | DELETE | `/admin/budget/item/{id}` | 删除预算科目 | budget:item:edit |
| 3-20 | GET | `/admin/budget/apply/page` | 申请审核分页（后台） | budget:apply:list |
| 3-21 | PUT | `/admin/budget/apply/{id}/audit` | 申请审核（通过 / 驳回） | budget:apply:audit |
| 3-22 | POST | `/admin/budget/expense` | 登记支出 | budget:expense:add |
| 3-23 | GET | `/admin/budget/expense/page` | 支出分页（后台） | budget:expense:list |
| 3-24 | DELETE | `/admin/budget/expense/{id}` | 删除支出记录 | budget:expense:remove |
| 3-25 | GET | `/admin/budget/stat/overview` | 预算执行看板 | budget:stat:list |

## 模块四 · 部门财产（`src/api/asset.js`）

| 编号 | 方法 | 路径 | 接口名称 | 权限码 |
| --- | --- | --- | --- | --- |
| 4-01 | GET | `/user/asset/page` | 资产分页（前台） | - |
| 4-02 | GET | `/user/asset/{id}` | 资产详情（前台） | - |
| 4-03 | GET | `/user/asset/category/tree` | 资产分类树 | - |
| 4-04 | GET | `/user/asset/stat/overview` | 资产总览统计 | - |
| 4-05 | POST | `/user/asset/borrow` | 借用 / 领用申请 | - |
| 4-06 | GET | `/user/asset/borrow/my/page` | 我的借用记录 | - |
| 4-07 | PUT | `/user/asset/borrow/{id}/return` | 归还登记（本人） | - |
| 4-08 | GET | `/admin/asset/page` | 资产分页（后台） | asset:info:list |
| 4-09 | GET | `/admin/asset/{id}` | 资产详情（后台） | asset:info:query |
| 4-10 | POST | `/admin/asset` | 新增资产 | asset:info:add |
| 4-11 | PUT | `/admin/asset` | 修改资产 | asset:info:edit |
| 4-12 | DELETE | `/admin/asset/{id}` | 删除资产 | asset:info:remove |
| 4-13 | PUT | `/admin/asset/{id}/status` | 资产状态变更 | asset:info:edit |
| 4-14 | PUT | `/admin/asset/{id}/assign` | 分配保管人与存放位置 | asset:info:assign |
| 4-15 | POST | `/admin/asset/record` | 变动记录登记 | asset:record:add |
| 4-16 | GET | `/admin/asset/record/page` | 变动流水分页 | asset:record:list |
| 4-17 | POST | `/admin/asset/category` | 新增资产分类 | asset:category:edit |
| 4-18 | PUT | `/admin/asset/category` | 修改资产分类 | asset:category:edit |
| 4-19 | DELETE | `/admin/asset/category/{id}` | 删除资产分类 | asset:category:edit |
| 4-20 | GET | `/admin/asset/borrow/page` | 借用审核分页 | asset:borrow:list |
| 4-21 | PUT | `/admin/asset/borrow/{id}/audit` | 借用审核 | asset:borrow:audit |
| 4-22 | PUT | `/admin/asset/borrow/{id}/return` | 归还登记（后台） | asset:borrow:edit |
| 4-23 | POST | `/admin/asset/inventory` | 发起盘点 | asset:inventory:add |
| 4-24 | GET | `/admin/asset/inventory/page` | 盘点任务分页 | asset:inventory:list |
| 4-25 | PUT | `/admin/asset/inventory/item/{id}` | 盘点明细登记 | asset:inventory:exec |
| 4-26 | PUT | `/admin/asset/inventory/{id}/finish` | 结束盘点 | asset:inventory:exec |
| 4-27 | GET | `/admin/asset/export` | 资产台账导出 | asset:info:export |

## 模块五 · 部门工位（`src/api/seat.js`）

| 编号 | 方法 | 路径 | 接口名称 | 权限码 |
| --- | --- | --- | --- | --- |
| 5-01 | GET | `/user/seat/plan` | 平面图数据 | - |
| 5-02 | GET | `/user/seat/list` | 工位列表 | - |
| 5-03 | GET | `/user/seat/my` | 我的工位 | - |
| 5-04 | POST | `/user/seat/apply` | 提交工位申请 | - |
| 5-05 | GET | `/user/seat/apply/my/page` | 我的工位申请分页 | - |
| 5-06 | PUT | `/user/seat/apply/{id}/cancel` | 撤销工位申请 | - |
| 5-07 | GET | `/admin/seat/area/list` | 区域列表（后台） | seat:area:list |
| 5-08 | POST | `/admin/seat/area` | 新增区域（上传平面图） | seat:area:add |
| 5-09 | PUT | `/admin/seat/area` | 修改区域 | seat:area:edit |
| 5-10 | DELETE | `/admin/seat/area/{id}` | 删除区域 | seat:area:remove |
| 5-11 | POST | `/admin/seat` | 新增工位 | seat:seat:add |
| 5-12 | PUT | `/admin/seat` | 修改工位 | seat:seat:edit |
| 5-13 | PUT | `/admin/seat/batch` | 批量保存工位布局坐标 | seat:seat:edit |
| 5-14 | DELETE | `/admin/seat/{id}` | 删除工位 | seat:seat:remove |
| 5-15 | PUT | `/admin/seat/{id}/assign` | 分配工位 | seat:seat:assign |
| 5-16 | PUT | `/admin/seat/{id}/release` | 释放工位 | seat:seat:assign |
| 5-17 | PUT | `/admin/seat/{id}/status` | 工位状态变更 | seat:seat:edit |
| 5-18 | GET | `/admin/seat/apply/page` | 工位申请分页（后台） | seat:apply:list |
| 5-19 | PUT | `/admin/seat/apply/{id}/audit` | 工位申请审核 | seat:apply:audit |
| 5-20 | POST | `/user/seat/swap` | 成员发起座位调换 | - |
| 5-21 | GET | `/user/seat/swap/my/page` | 我的调换申请分页 | - |
| 5-22 | PUT | `/user/seat/swap/{id}/handle` | 调换对方确认 / 拒绝 | - |
| 5-23 | GET | `/admin/seat/swap/page` | 调换申请分页（后台） | seat:swap:list |
| 5-24 | PUT | `/admin/seat/swap/{id}/audit` | 调换申请审核（负责人） | seat:swap:audit |
| 5-25 | GET | `/admin/seat/plan` | 平面图数据（后台） | seat:seat:list |
| 5-26 | POST | `/admin/seat/plan/drag` | 平面图拖拽调整（移动 / 互换） | seat:seat:assign |

## 模块六 · 学长学姐展板（`src/api/honor.js`）

| 编号 | 方法 | 路径 | 接口名称 | 权限码 |
| --- | --- | --- | --- | --- |
| 6-01 | GET | `/user/honor/alumni/page` | 展板学长分页 | - |
| 6-02 | GET | `/user/honor/alumni/{id}` | 学长详情（荣誉时间轴） | - |
| 6-03 | GET | `/user/honor/alumni/highlight` | 精选轮播 | - |
| 6-04 | GET | `/user/honor/stat` | 展板统计 | - |
| 6-05 | GET | `/admin/honor/alumni/page` | 学长分页（后台） | honor:alumni:list |
| 6-06 | POST | `/admin/honor/alumni` | 新增学长 | honor:alumni:add |
| 6-07 | PUT | `/admin/honor/alumni` | 修改学长 | honor:alumni:edit |
| 6-08 | DELETE | `/admin/honor/alumni/{id}` | 删除学长 | honor:alumni:remove |
| 6-09 | PUT | `/admin/honor/alumni/{id}/status` | 上板 / 下板 | honor:alumni:edit |
| 6-10 | PUT | `/admin/honor/alumni/{id}/sort` | 调整展示排序 | honor:alumni:edit |
| 6-11 | POST | `/admin/honor/record` | 新增荣誉记录 | honor:record:edit |
| 6-12 | PUT | `/admin/honor/record` | 修改荣誉记录 | honor:record:edit |
| 6-13 | DELETE | `/admin/honor/record/{id}` | 删除荣誉记录 | honor:record:edit |
| 6-14 | POST | `/user/honor/alumni` | 成员提交学长信息 | - |
| 6-15 | GET | `/user/honor/alumni/my/page` | 我的提交记录分页 | - |
| 6-16 | PUT | `/user/honor/alumni/{id}/withdraw` | 撤回提交 | - |
| 6-17 | GET | `/admin/honor/alumni/audit/page` | 待审核信息分页（后台） | honor:alumni:audit |
| 6-18 | PUT | `/admin/honor/alumni/{id}/audit` | 审核通过 / 驳回 | honor:alumni:audit |
| 6-19 | PUT | `/user/honor/alumni/{id}` | 重新提交我的信息 | - |

## 模块七 · 值日排班（`src/api/duty.js`）

| 编号 | 方法 | 路径 | 接口名称 | 权限码 |
| --- | --- | --- | --- | --- |
| 7-01 | GET | `/user/duty/schedule/page` | 值日安排分页（公示） | - |
| 7-02 | GET | `/user/duty/schedule/my` | 我的值日（含待办） | - |
| 7-03 | GET | `/user/duty/schedule/calendar` | 值日日历（月视图） | - |
| 7-04 | PUT | `/user/duty/schedule/{id}/confirm` | 值日完成确认 | - |
| 7-05 | POST | `/user/duty/swap` | 发起换班申请 | - |
| 7-06 | GET | `/user/duty/swap/my/page` | 我的换班申请分页 | - |
| 7-07 | PUT | `/user/duty/swap/{id}/handle` | 换班处理（同意 / 拒绝） | - |
| 7-08 | GET | `/admin/duty/plan/page` | 排班计划分页（后台） | duty:plan:list |
| 7-09 | POST | `/admin/duty/plan` | 新增排班计划 | duty:plan:add |
| 7-10 | PUT | `/admin/duty/plan` | 修改排班计划 | duty:plan:edit |
| 7-11 | DELETE | `/admin/duty/plan/{id}` | 删除排班计划 | duty:plan:remove |
| 7-12 | POST | `/admin/duty/plan/{id}/generate` | 自动生成排班 | duty:plan:generate |
| 7-13 | PUT | `/admin/duty/plan/{id}/publish` | 发布 / 取消发布 | duty:plan:publish |
| 7-14 | POST | `/admin/duty/schedule` | 新增值日条目 | duty:schedule:edit |
| 7-15 | PUT | `/admin/duty/schedule` | 修改值日条目 | duty:schedule:edit |
| 7-16 | DELETE | `/admin/duty/schedule/{id}` | 删除值日条目 | duty:schedule:edit |
| 7-17 | GET | `/admin/duty/schedule/page` | 值日安排分页（后台） | duty:schedule:list |
| 7-18 | PUT | `/admin/duty/schedule/{id}/status` | 值日状态变更 | duty:schedule:edit |
| 7-19 | GET | `/admin/duty/stat/overview` | 值日统计看板 | duty:stat:list |
| 7-20 | GET | `/admin/duty/reminder` | 提醒配置查询 | duty:reminder:config |
| 7-21 | PUT | `/admin/duty/reminder` | 提醒配置保存 | duty:reminder:config |

## 模块八 · 通用支撑（`src/api/common.js`）

| 编号 | 方法 | 路径 | 接口名称 | 权限码 |
| --- | --- | --- | --- | --- |
| 8-01 | POST | `/user/file/upload` | 单文件上传 | - |
| 8-02 | POST | `/user/file/upload/batch` | 多文件上传 | - |
| 8-03 | DELETE | `/user/file/{id}` | 文件删除 | - |
| 8-04 | GET | `/common/dict/{type}` | 数据字典查询 | - |
| 8-05 | GET | `/user/notice/page` | 公告分页（前台） | - |
| 8-06 | GET | `/admin/notice/page` | 公告分页（后台） | common:notice:list |
| 8-07 | POST | `/admin/notice` | 公告新增 | common:notice:edit |
| 8-08 | PUT | `/admin/notice` | 公告修改 | common:notice:edit |
| 8-09 | DELETE | `/admin/notice/{id}` | 公告删除 | common:notice:edit |
| 8-10 | GET | `/user/home/overview` | 前台首页看板 | - |
| 8-11 | GET | `/admin/stat/dashboard` | 后台统计看板 | common:stat:list |
| 8-12 | GET | `/admin/log/page` | 操作日志分页 | system:log:list |
| 8-13 | GET | `/common/captcha` | 图形验证码 | - |
| 8-14 | GET | `/common/health` | 健康检查 | - |
| 8-15 | GET | `/user/video/list` | 前台展示视频列表 | - |
| 8-16 | GET | `/admin/video/page` | 视频分页（后台） | common:video:list |
| 8-17 | POST | `/admin/video` | 新增视频 | common:video:edit |
| 8-18 | PUT | `/admin/video` | 视频修改 | common:video:edit |
| 8-19 | DELETE | `/admin/video/{id}` | 视频删除 | common:video:edit |
| 8-20 | GET | `/user/message/page` | 我的消息分页 | - |
| 8-21 | GET | `/user/message/unread/count` | 未读消息数 | - |
| 8-22 | PUT | `/user/message/read` | 标记消息已读 | - |

