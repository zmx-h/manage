// 模块六 · 学长学姐展板 接口封装（骨架）
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
//   6-01 GET    /user/honor/alumni/page                   展板学长分页
//   6-02 GET    /user/honor/alumni/{id}                   学长详情（荣誉时间轴）
//   6-03 GET    /user/honor/alumni/highlight              精选轮播
//   6-04 GET    /user/honor/stat                          展板统计
//   6-05 GET    /admin/honor/alumni/page                  学长分页（后台）
//   6-06 POST   /admin/honor/alumni                       新增学长
//   6-07 PUT    /admin/honor/alumni                       修改学长
//   6-08 DELETE /admin/honor/alumni/{id}                  删除学长
//   6-09 PUT    /admin/honor/alumni/{id}/status           上板 / 下板
//   6-10 PUT    /admin/honor/alumni/{id}/sort             调整展示排序
//   6-11 POST   /admin/honor/record                       新增荣誉记录
//   6-12 PUT    /admin/honor/record                       修改荣誉记录
//   6-13 DELETE /admin/honor/record/{id}                  删除荣誉记录
//   6-14 POST   /user/honor/alumni                        成员提交学长信息
//   6-15 GET    /user/honor/alumni/my/page                我的提交记录分页
//   6-16 PUT    /user/honor/alumni/{id}/withdraw          撤回提交
//   6-17 GET    /admin/honor/alumni/audit/page            待审核信息分页（后台）
//   6-18 PUT    /admin/honor/alumni/{id}/audit            审核通过 / 驳回
//   6-19 PUT    /user/honor/alumni/{id}                   重新提交我的信息

export default {}
