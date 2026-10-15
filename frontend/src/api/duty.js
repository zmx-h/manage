// 模块七 · 值日排班 接口封装（骨架）
//
// 本文件只列出该模块的接口清单与调用写法模板，**尚未实现任何方法**。
// 统一返回：code === 200 时 resolve 出 data 字段（见 src/api/request.js）。
//
// 写法模板（示例，取消注释并按接口文档补齐参数即可）：
//
// import request from '@/api/request'
//
// /** 1-08 用户分页查询 · GET /admin/user/page */
// export function pageUser(query) {
//   return request.get('/admin/user/page', { params: query })
// }
//
// /** 1-10 新增用户 · POST /admin/user */
// export function addUser(data) {
//   return request.post('/admin/user', data)
// }
//
// 接口清单：
//   7-01 GET    /user/duty/schedule/page                  值日安排分页（公示）
//   7-02 GET    /user/duty/schedule/my                    我的值日（含待办）
//   7-03 GET    /user/duty/schedule/calendar              值日日历（月视图）
//   7-04 PUT    /user/duty/schedule/{id}/confirm          值日完成确认
//   7-05 POST   /user/duty/swap                           发起换班申请
//   7-06 GET    /user/duty/swap/my/page                   我的换班申请分页
//   7-07 PUT    /user/duty/swap/{id}/handle               换班处理（同意 / 拒绝）
//   7-08 GET    /admin/duty/plan/page                     排班计划分页（后台）
//   7-09 POST   /admin/duty/plan                          新增排班计划
//   7-10 PUT    /admin/duty/plan                          修改排班计划
//   7-11 DELETE /admin/duty/plan/{id}                     删除排班计划
//   7-12 POST   /admin/duty/plan/{id}/generate            自动生成排班
//   7-13 PUT    /admin/duty/plan/{id}/publish             发布 / 取消发布
//   7-14 POST   /admin/duty/schedule                      新增值日条目
//   7-15 PUT    /admin/duty/schedule                      修改值日条目
//   7-16 DELETE /admin/duty/schedule/{id}                 删除值日条目
//   7-17 GET    /admin/duty/schedule/page                 值日安排分页（后台）
//   7-18 PUT    /admin/duty/schedule/{id}/status          值日状态变更
//   7-19 GET    /admin/duty/stat/overview                 值日统计看板
//   7-20 GET    /admin/duty/reminder                      提醒配置查询
//   7-21 PUT    /admin/duty/reminder                      提醒配置保存

export default {}
